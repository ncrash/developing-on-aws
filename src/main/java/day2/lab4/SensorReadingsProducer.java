package day2.lab4;// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

import com.amazonaws.regions.Region;
import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;
import com.amazonaws.services.kinesis.producer.UserRecordResult;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Random;

// The SensorReadingsProducer class generates data for sensor readings and publishes records to a Kinesis stream
public class SensorReadingsProducer {

  // Before running the code, check that the ~/.aws/credentials file contains your credentials

  public static final String STREAM_NAME = StreamCreator.STREAM_NAME;
  public static final Region REGION = Utils.getRegion();
  public static final int TOTAL_NUM_EVENTS = 200;
  private final Random RANDOM = new Random();

  private static SensorReadingsProducer sensorReadingsProducer = null;

  private int successCounter = 0;

  public static void main(String[] args) {
    sensorReadingsProducer = getSensorReadingsProducer();
    sensorReadingsProducer.addDataToStream();
  }

  private void addDataToStream() {
    SensorData sensorData = null;

    KinesisProducerConfiguration config = createKinesisProducerConfig();
    KinesisProducer kinesisProducer = createKinesisProducer(config);

    FutureCallback<UserRecordResult> myCallback =
        new FutureCallback<UserRecordResult>() {
          @Override
          public void onFailure(Throwable t) {
            System.out.println("Failed to add record to stream.");
            t.printStackTrace();
          };

          @Override
          public void onSuccess(UserRecordResult result) {
            successCounter++;
            if (successCounter == TOTAL_NUM_EVENTS) {
              System.out.println("Number of records added records to stream. :  " + successCounter);
            }
          };
        };

    for (int readingCounter = 0; readingCounter < TOTAL_NUM_EVENTS; readingCounter++) {
      sensorData = generateData(readingCounter);
      ListenableFuture<UserRecordResult> future =
          addUserRecordToStream(kinesisProducer, sensorData);
      Futures.addCallback(future, myCallback);
    }
  }

  // Generates sensor data. DO NOT MODIFY THIS METHOD
  private SensorData generateData(int readingCounter) {
    String sensorId = getSensorId(readingCounter);
    int temperature = getRandomTemperature(readingCounter);

    String data = sensorId + ":" + temperature;
    ByteBuffer dataBytes = null;

    dataBytes = ByteBuffer.wrap(data.getBytes(Charset.forName("UTF-8")));

    return new SensorData(sensorId, dataBytes);
  }

  private String getSensorId(int readingCounter) {
    String sensorId = null;
    String[] sensorIds = {"A12345", "Z09876"};

    if (readingCounter % 2 == 0) {
      sensorId = sensorIds[0];
    } else {
      sensorId = sensorIds[1];
    }

    return sensorId;
  }

  private int getRandomTemperature(int readingCounter) {
    int temperature = 0;
    int randomNumber = RANDOM.nextInt(10);

    if (readingCounter > 100 && readingCounter < 150 && readingCounter % 2 == 0) {
      temperature = randomNumber + 50;
    } else {
      temperature = randomNumber + 30;
    }
    return temperature;
  }

  public static SensorReadingsProducer getSensorReadingsProducer() {
    if (sensorReadingsProducer == null) {
      sensorReadingsProducer = new SensorReadingsProducer();
    }
    return sensorReadingsProducer;
  }

  public int getSuccessCounter() {
    return successCounter;
  }

  /**
   * Create an instance of KinesisProducerConfiguration and set the region
   *
   * @return        Kinesis configuration
   */
  private static KinesisProducerConfiguration createKinesisProducerConfig() {
    // STUDENT TODO 4: Replace the solution with your own code
    return Solution.createKinesisProducerConfig(REGION);
  }

  /**
   * Create a KinesisProducer
   *
   * @param config      Kinesis configuration
   * @return            Kinesis producer
   */
  private static KinesisProducer createKinesisProducer(KinesisProducerConfiguration config) {
    // STUDENT TODO 5: Replace the solution with your own code
    return Solution.createKinesisProducer(config);
  }

  /**
   * Create a future callback
   *
   * @param kinesisProducer   Kinesis producer
   * @param sensorData        Sensor data
   * @return                  Kinesis config
   */
  private static ListenableFuture<UserRecordResult> addUserRecordToStream(
      KinesisProducer kinesisProducer, SensorData sensorData) {
    // STUDENT TODO 6: Replace the solution with your own code
    return Solution.addUserRecordToStream(STREAM_NAME, kinesisProducer, sensorData);
  }
}
