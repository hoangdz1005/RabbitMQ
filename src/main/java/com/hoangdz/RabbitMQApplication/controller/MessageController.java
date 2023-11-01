package com.hoangdz.RabbitMQApplication.controller;

import com.hoangdz.RabbitMQApplication.model.User;
import com.hoangdz.RabbitMQApplication.publisher.RabbitMQJsonProducer;
import com.hoangdz.RabbitMQApplication.publisher.RabbitMQProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MessageController {
    private final RabbitMQProducer rabbitMQProducer;
    private final RabbitMQJsonProducer rabbitMQJsonProducer;

    public MessageController(RabbitMQProducer rabbitMQProducer, RabbitMQJsonProducer rabbitMQJsonProducer) {
        this.rabbitMQProducer = rabbitMQProducer;
        this.rabbitMQJsonProducer = rabbitMQJsonProducer;
    }
    @GetMapping("/publish")
    public ResponseEntity<String> sendMessage(@RequestParam("message") String message){
        rabbitMQProducer.sendMessage(message);
        return ResponseEntity.ok("Message sent to RabbitMQ.");
    }


    @PostMapping("/publish")
    public ResponseEntity<String> sendJsonMessage(@RequestBody User user){
        rabbitMQJsonProducer.sendJsonMessage(user);
        return ResponseEntity.ok("Json message sent to RabbitMQ.");
    }
}
