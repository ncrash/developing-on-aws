package day1.lab3;// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InfectionStatisticsTest {

  @Test
  public void test() throws Exception {
    InfectionStatistics.main(new String[] {"Reno"});
    assertEquals(178, InfectionStatistics.itemCount, 0);
  }
}
