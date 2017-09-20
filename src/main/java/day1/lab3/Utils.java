package day1.lab3;// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

public class Utils {

  public static final String LAB_S3_BUCKET_NAME = "us-west-2-aws-staging";
  public static final String LAB_S3_BUCKET_REGION = "us-west-2";
  public static final String INFECTIONS_DATA_FILE_KEY =
      "awsu-ilt/AWS-100-DEV/v2.2/binaries/input/lab-3-dynamoDB/InfectionsData.csv";
  public static final String PATIENT_REPORT_PREFIX =
      "awsu-ilt/AWS-100-DEV/v2.2/binaries/input/lab-3-dynamoDB/PatientRecord";

  public static Region getRegion() {
    Region region = Regions.getCurrentRegion();

    // For local testing only
    if (region == null) {
      region = Region.getRegion(Regions.AP_NORTHEAST_1);
    }

    System.out.println("getRegion returns " + region.getName());
    return region;
  }
}
