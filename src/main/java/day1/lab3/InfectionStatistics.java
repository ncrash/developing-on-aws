package day1.lab3;// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;

import java.util.Iterator;

// The InfectionStatistics class queries the infections table and reports the total number of infections in a city.
public class InfectionStatistics {

  // City for which number of infections must be queried
  public static final String inputCity = "Reno";

  public static int itemCount = 0;

  private static final String INFECTIONS_TABLE_NAME = InfectionsTableCreator.INFECTIONS_TABLE_NAME;
  private static final String CITY_DATE_INDEX_NAME = InfectionsTableCreator.CITY_DATE_INDEX_NAME;
  private static final Region REGION = Utils.getRegion();

  private static DynamoDB dynamoDB = null;
  private static AmazonDynamoDBClient dynamoDBClient = null;

  public static void main(String[] args) throws Exception {

    try {
      // Instantiate DynamoDB client and object
      dynamoDBClient = new AmazonDynamoDBClient();
      dynamoDBClient.setRegion(REGION);
      dynamoDB = new DynamoDB(dynamoDBClient);

      // Query items for a city
      ItemCollection<QueryOutcome> items =
          queryCityRelatedItems(dynamoDB, INFECTIONS_TABLE_NAME, CITY_DATE_INDEX_NAME, inputCity);

      System.out.println("-------------------------------------------------------------");

      // Retrieve a reference to an iterator from the ItemCollection object returned by the query.
      Iterator<Item> itemIterator = items.iterator();
      Item item = null;
      // Iterate over items
      while (itemIterator.hasNext()) {
        // Count the number of items
        itemCount++;
        item = itemIterator.next();
        System.out.printf(
            "%s - %s - %s %n",
            item.getString("PatientId"), item.getString("City"), item.getString("Date"));
      }
      System.out.println("-------------------------------------------------------------");
      System.out.printf("Summary: Number of infections in %s city is %d %n", inputCity, itemCount);

    } catch (AmazonServiceException ase) {
      System.out.println("Error Message:    " + ase.getMessage());
      System.out.println("HTTP Status Code: " + ase.getStatusCode());
      System.out.println("AWS Error Code:   " + ase.getErrorCode());
      System.out.println("Error Type:       " + ase.getErrorType());
      System.out.println("Request ID:       " + ase.getRequestId());
    } catch (AmazonClientException ace) {
      System.out.println("Error Message: " + ace.getMessage());
    }
  }

  /**
   * Query the infections table to retrieve the items that relate to the given city
   *
   * @param dynamoDB              Instance of DynamoDB class
   * @param infectionsTableName   Table name
   * @param cityDateGsiName       Global secondary index
   * @param inputCity             City
   * @return                      Query results
   */
  public static ItemCollection<QueryOutcome> queryCityRelatedItems(
      DynamoDB dynamoDB, String infectionsTableName, String cityDateGsiName, String inputCity) {
    return Solution.queryCityRelatedItems(
        dynamoDB, infectionsTableName, cityDateGsiName, inputCity);
  }
}
