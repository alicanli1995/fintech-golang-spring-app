package com.paylinkfusion.gateway.config;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Slf4j
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.admin.properties.bootstrap.servers}")
    private String kafkaAdminURL;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAdminURL);
        return new KafkaAdmin(config);
    }

    @Bean
    public void listAndCreateTopics() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAdminURL);

        try (AdminClient adminClient = AdminClient.create(config)) {
            ListTopicsResult topicsResult = adminClient.listTopics();
            Set<String> topicNames = topicsResult.names().get();

            List<String> desiredTopic = List.of("payment-request", "user-register", "payment-success");
            desiredTopic
                    .stream()
                    .filter(myTopic -> !topicNames.contains(myTopic))
                    .map(myTopic -> new NewTopic(myTopic, 1, (short) 1))
                    .forEach(newTopic -> createTopics(adminClient, newTopic));

        } catch (Exception e) {
            log.error("Error while creating topic: " + e.getMessage());
        }
    }

    private static void createTopics(AdminClient adminClient, NewTopic newTopic) {
        try {
            adminClient.createTopics(List.of(newTopic)).all().get();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error while creating topic: " + e.getMessage());
        }
    }
}
