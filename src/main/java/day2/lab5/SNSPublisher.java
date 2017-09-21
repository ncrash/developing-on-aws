package day2.lab5;// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// The SNSPublisher class publishes messages to SNS topics
public class SNSPublisher {

  private static final String TOPIC_ARN_EMAIL = "arn:aws:sns:ap-northeast-1:909859851799:EmailSNSTopic";

  private static final String TOPIC_ARN_SMS = "arn:aws:sns:ap-northeast-1:909859851799:SmsSNSTopic";

  private static final String TOPIC_ARN_ORDER = "arn:aws:sns:ap-northeast-1:909859851799:OrderSNSTopic";

  private static final Region REGION = Utils.getRegion();
  private static final String EMAIL_SUBJECT = "Status of pharmaceuticals order.";
  private static final String EMAIL_MESSAGE =
      "Your pharmaceutical supplies will be shipped 5 business days from the date of order.";
  private static final String ORDER_DETAILS = "Ibuprofen, Acetaminophen";

  public static final int NUM_MESSAGES = 10;

  private static AmazonSNSClient snsClient = null;

  public static void main(String[] args) throws Exception {
    SNSPublisher snsPublisher = new SNSPublisher();
    snsPublisher.init();
    snsPublisher.publishMessages();
  }

  private void init() throws Exception {
    AWSCredentials credentials = null;
    try {
      credentials = new ProfileCredentialsProvider().getCredentials();
    } catch (Exception e) {
      throw new AmazonClientException(
          "Cannot load the credentials from the credential profiles file. "
              + "Please make sure that your credentials file is at the correct "
              + "location (~/.aws/credentials), and is in valid format.",
          e);
    }

    createSNSClient(credentials);
  }

  private void publishMessages() throws Exception {
    publishEmailMessage();
    publishOrderMessages();
  }

  private void publishOrderMessages() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    Order order = null;
    String jsonOrder = null; // Order in JSON format.
    for (int i = 1; i < (NUM_MESSAGES + 1); i++) {
      order = new Order(i, "2015/10/" + i, ORDER_DETAILS);
      System.out.println("Publishing order to SNS topic: " + order);
      jsonOrder = convertOrderToJSON(mapper, order);
      publishOrder(jsonOrder);
    }
  }

  /**
   * Create an instance of the AmazonSNSClient class
   *
   * @param credentials   AWS Credentials
   */
  private static void createSNSClient(AWSCredentials credentials) {
    snsClient = Solution.createSNSClient(snsClient, credentials, REGION);
  }

  /** Publish a message to the SNS topic for email messages */
  private static void publishEmailMessage() {
    Solution.publishEmailMessage(snsClient, TOPIC_ARN_SMS, EMAIL_MESSAGE, EMAIL_SUBJECT);
  }

  /**
   * Convert the order to JSON format
   *
   * @param mapper    Object mapper
   * @param order     The order
   * @return          The order in JSON format
   */
  private static String convertOrderToJSON(ObjectMapper mapper, Order order) {
    return Solution.convertOrderToJSON(mapper, order);
  }

  /**
   * Publish the JSON-formatted order to the SNS topic for orders.
   *
   * @param jsonOrder   The order in JSON format
   */
  private static void publishOrder(String jsonOrder) {
    Solution.publishOrder(snsClient, TOPIC_ARN_ORDER, jsonOrder);
  }
}
