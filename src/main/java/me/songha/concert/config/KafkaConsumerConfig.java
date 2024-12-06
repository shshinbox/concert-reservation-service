package me.songha.concert.config;

import lombok.extern.slf4j.Slf4j;
import me.songha.concert.concert.ConcertNotFoundException;
import me.songha.concert.reservation.pending.ReservationPendingProducerRequest;
import me.songha.concert.shared.exception.ReservationIllegalArgumentException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
public class KafkaConsumerConfig {
    private static final String DLQ_TOPIC = "dlq-topic";

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.37:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "reservation-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "me.songha.concert.*");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> genericKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(genericConsumerFactory());
        factory.setBatchListener(true);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Object> genericConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(Object.class)
        );
    }

    @Bean
    public ConsumerFactory<String, ReservationPendingProducerRequest> reservationConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(ReservationPendingProducerRequest.class)
        );
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            kafkaTemplate.send(DLQ_TOPIC, record.key().toString(), record.value());
            log.error("[Error] Moved to DLQ: {}, exception message: {}", record.value(), exception.getMessage());
        }, new FixedBackOff(3000L, 3));

        errorHandler.addNotRetryableExceptions(ReservationIllegalArgumentException.class, ConcertNotFoundException.class);
        errorHandler.setAckAfterHandle(true);
        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReservationPendingProducerRequest> reservationKafkaListenerContainerFactory(
            ConsumerFactory<String, ReservationPendingProducerRequest> consumerFactory, KafkaTemplate<String, Object> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, ReservationPendingProducerRequest> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.setCommonErrorHandler(errorHandler(kafkaTemplate));
        factory.setBatchListener(true);
        return factory;
    }

}
