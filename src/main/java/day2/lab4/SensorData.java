package day2.lab4;// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

import java.nio.ByteBuffer;

// The SensorData class is used to pass around sensor id and data
public class SensorData {
  public String sensorId;
  public ByteBuffer data;

  public SensorData(String id, ByteBuffer sData) {
    sensorId = id;
    data = sData;
  }
}
