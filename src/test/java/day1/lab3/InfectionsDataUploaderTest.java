package day1.lab3;// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InfectionsDataUploaderTest {

  @Test
  public void test() throws Exception {
    InfectionsDataUploader.main(new String[0]);
    assertEquals(1000, InfectionsDataUploader.numItemsAdded, 0);
  }
}
