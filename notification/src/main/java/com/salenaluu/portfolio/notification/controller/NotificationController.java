package com.salenaluu.portfolio.notification.controller;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
public class NotificationController {
    @Value("${cloud.aws.sns.topic.interviewStatus.arn}")
    private String topicArn;

    private final AmazonSNSClient snsClient;

    // ===== SENDER AREA =====
    // Here we can subscribe new email addresses.
    @GetMapping("/subscribe")
    public Mono<String> addSubscription(@RequestParam String email){
        SubscribeRequest request =
                new SubscribeRequest(topicArn,"email",email);
        snsClient.subscribe(request);
        return Mono.just("check your emails !");
    }
    // Here we can send a message to all subscribers.
    @GetMapping("/publish")
    public Mono<String> publishMessageToTopic(@RequestParam String message){
        PublishRequest publishRequest =
                new PublishRequest(topicArn,message, "****A new Blogpost was published****");
        snsClient.publish(publishRequest);
        return Mono.just("message successfully published");
    }
    // ===== RECEIVER AREA =====
    // With the SQS-Listener we can receive our current Messages
    @SqsListener(value = "Receiver", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void receiveSuccess(String message){
        log.info("Message received {}",message);
    }
}
