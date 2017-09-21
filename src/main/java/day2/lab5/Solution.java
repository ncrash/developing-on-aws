package day2.lab5;// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class Solution {

  public static AmazonSNSClient createSNSClient(
      AmazonSNSClient snsClient, AWSCredentials credentials, Region REGION) {
    snsClient = new AmazonSNSClient(credentials);
    snsClient.setRegion(REGION);
    return snsClient;
  }

  public static void publishEmailMessage(
      AmazonSNSClient snsClient, String arn, String msg, String subject) {
    snsClient.publish(arn, msg, subject);
  }

  public static String convertOrderToJSON(ObjectMapper mapper, Order order) {
    String jsonOrder = null;
    try {
      jsonOrder = mapper.writeValueAsString(order);
    } catch (Exception x) {
      // Do nothing
    }
    return jsonOrder;
  }

  public static void publishOrder(AmazonSNSClient snsClient, String arn, String jsonOrder) {
    snsClient.publish(arn, jsonOrder);
  }

  public static AmazonSQSClient createSQSClient(
      AmazonSQSClient sqsClient, AWSCredentials credentials, Region REGION) {
    sqsClient = new AmazonSQSClient(credentials);
    sqsClient.setRegion(REGION);
    return sqsClient;
  }

  public static String getURL(AmazonSQSClient sqsClient, String queue) {
    String queueUrl = null;
    GetQueueUrlResult queueUrlResult = sqsClient.getQueueUrl(queue);
    queueUrl = queueUrlResult.getQueueUrl();
    return queueUrl;
  }

  public static ReceiveMessageRequest createRequest(String queueUrl) {
    ReceiveMessageRequest request = new ReceiveMessageRequest(queueUrl);
    request.setWaitTimeSeconds(20);
    request.setMaxNumberOfMessages(10);
    return request;
  }

  public static ReceiveMessageResult getMessageResult(
      AmazonSQSClient sqsClient, ReceiveMessageRequest request) {
    ReceiveMessageResult receiveMessageResult = sqsClient.receiveMessage(request);
    return receiveMessageResult;
  }

  public static List<Message> getMessages(ReceiveMessageResult receiveMessageResult) {
    List<Message> messages = null;
    messages = receiveMessageResult.getMessages();
    return messages;
  }

  public static void deleteMessage(AmazonSQSClient sqsClient, String queueUrl, Message message) {
    String messageReceiptHandle = message.getReceiptHandle();
    sqsClient.deleteMessage(queueUrl, messageReceiptHandle);
  }
}
