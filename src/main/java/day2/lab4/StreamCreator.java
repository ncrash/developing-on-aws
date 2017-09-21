package day2.lab4;// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.ResourceInUseException;
import java.util.concurrent.TimeUnit;

// The StreamCreator class creates a Kinesis stream
public class StreamCreator {

  // Before running the code, check that the ~/.aws/credentials file contains your credentials

  public static final String STREAM_NAME = "KinesisLabStream";
  public static final Region REGION = Utils.getRegion();
  public static final int WAIT_TIME_MINUTES = 10;
  public static final int POLLING_INTERVAL_SECONDS = 20;

  // Kinesis Client Object
  private static AmazonKinesisClient kinesis;

  public static void main(String[] args) throws Exception {
    init();

    // Creates a stream with 10 shards
    createKinesisStream(STREAM_NAME, 10);
  }

  private static void init() throws Exception {

    // The ProfileCredentialsProvider will return your default credential profile by reading from the ~/.aws/credentials file
    AWSCredentials credentials = null;
    try {
      credentials = new ProfileCredentialsProvider().getCredentials();
    } catch (Exception e) {
      throw new AmazonClientException(
          "AmazonClientException represents an error that occurred inside the client on the local host,"
              + "either while trying to send the request to AWS or interpret the response."
              + " "
              + "For example, if no network connection is available, the client won't be able to connect to AWS to execute a request and will throw an AmazonClientException.",
          e);
    }

    kinesis = createKinesisClient(credentials);
  }

  private static void createKinesisStream(String streamName, int shardCount) throws Exception {
    try {
      createStream(streamName, shardCount);
    } catch (ResourceInUseException re) {
      System.out.printf("Stream %s already exists %n", streamName);
    }

    waitForStreamToBecomeActive(streamName);
  }

  private static void waitForStreamToBecomeActive(String streamName) throws Exception {
    System.out.printf("Waiting for %s to become ACTIVE... %n", streamName);

    String streamStatus = null;
    boolean isActive = false;
    long startTime = System.currentTimeMillis();
    long endTime = startTime + TimeUnit.MINUTES.toMillis(WAIT_TIME_MINUTES);

    while (System.currentTimeMillis() < endTime) {
      Thread.sleep(TimeUnit.SECONDS.toMillis(POLLING_INTERVAL_SECONDS));
      streamStatus = getStreamStatus(streamName);
      System.out.printf("Current stream status: %s%n ", streamStatus);
      if ("ACTIVE".equals(streamStatus)) {
        isActive = true;
        break;
      }
    }

    if (!isActive) {
      throw new Exception(String.format("Stream %s never became active", streamName));
    }
  }

  /**
   * Create an Amazon Kinesis client and set the region
   *
   * @param credentials   AWS credentials
   * @return              Kinesis client object
   */
  private static AmazonKinesisClient createKinesisClient(AWSCredentials credentials) {
    // STUDENT TODO 1: Replace the solution with your own code
    return Solution.createKinesisClient(credentials, REGION);
  }

  /**
   * Create a Kinesis stream request
   *
   * @param streamName    Name of the Kinesis stream
   * @param shardCount    Number of shards in Kinesis stream
   */
  private static void createStream(String streamName, int shardCount) {
    // STUDENT TODO 2: Replace the solution with your own code
    Solution.createStream(kinesis, streamName, shardCount);
  }

  /**
   * Check if Kinesis stream status is active
   *
   * @param streamName    Name of the Kinesis stream
   * @return              Stream status
   */
  private static String getStreamStatus(String streamName) {
    // STUDENT TODO 3: Replace the solution with your own code
    return Solution.getStreamStatus(kinesis, streamName);
  }
}
