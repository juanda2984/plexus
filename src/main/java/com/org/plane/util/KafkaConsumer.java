package com.org.plane.util;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Profile("!test")
public class KafkaConsumer {

	@KafkaListener(topics = "ship-topic", groupId = "ship-group")
    public void listen(String message) {
        System.out.println("Received Message: " + message);
    }
}
