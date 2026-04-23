package com.cristaline.cristal.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("prod")
public class KafkaTopicConfig {

    @Bean
    NewTopic gameEventsTopic(@Value("${app.kafka.topics.game-events}") String topicName) {
        return TopicBuilder.name(topicName).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic importRequestsTopic(@Value("${app.kafka.topics.import-requests}") String topicName) {
        return TopicBuilder.name(topicName).partitions(1).replicas(1).build();
    }
}
