package com.ansh.notification;

import com.ansh.event.AnimalEvent;
import com.ansh.notification.handler.AnimalEventHandlerRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AnimalInfoConsumer {
  @Value("${animalTopicId}")
  private String animalTopicId;

  @Autowired
  private AnimalEventHandlerRegistry handlerRegistry;

  @KafkaListener(topics = "${animalTopicId}", groupId = "animalGroupId")
  public void listen(ConsumerRecord<String, String> message) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    AnimalEvent animalEvent = objectMapper.readValue(message.value(), AnimalEvent.class);
    handlerRegistry.handleEvent(animalEvent);
  }

  protected void setAnimalTopicId(String animalTopicId) {
    this.animalTopicId = animalTopicId;
  }
}
