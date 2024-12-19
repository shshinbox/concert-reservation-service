package me.songha.concert.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaAdminClientConfig {

    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(adminClientProps());
    }

    private Map<String, Object> adminClientProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.37:9092");
        return props;
    }

}
