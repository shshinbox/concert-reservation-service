package me.songha.concert.shared.messaging;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class KafkaLagService {
    protected final AdminClient adminClient;

    public long getLag(String groupId, String topic, int partition, long userOffset) throws ExecutionException, InterruptedException {
        return getLagForPartition(groupId, topic, partition, userOffset);
    }

    private long getLagForPartition(String groupId, String topic, int partition, long userOffset) throws ExecutionException, InterruptedException {
        TopicPartition topicPartition = new TopicPartition(topic, partition);

        ListConsumerGroupOffsetsResult offsetsResult = adminClient.listConsumerGroupOffsets(groupId);
        Map<TopicPartition, Long> offsets = offsetsResult.partitionsToOffsetAndMetadata()
                .get()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(topicPartition))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().offset()));
        long currentOffset = offsets.getOrDefault(topicPartition, -1L);

        return Math.max(0, userOffset - currentOffset);
    }
}