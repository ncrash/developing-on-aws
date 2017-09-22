// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

var SolutionCode = function() {
};

SolutionCode.prototype.paramsForStorage = function(inserttext) {
  var solutionWarning =
      "RUNNING SOLUTION CODE:" +
      "paramsForStorage! " +
      "Follow the steps in the lab guide to replace this method with your own implementation.";
  addResult(results, solutionWarning);

  var putParams = {
    TableName: 'CognitoLab',
    Item: {
      CognitoIdentity: {
          "S": AWS.config.credentials.identityId
      },
      Text: {
        "S" : inserttext
      }
    }
  };
  return putParams;
}

SolutionCode.prototype.paramsForRetrieval = function() {
  var solutionWarning =
      "RUNNING SOLUTION CODE:" +
      "paramsForRetrieval! " +
      "Follow the steps in the lab guide to replace this method with your own implementation.";
  addResult(results, solutionWarning);

  var getParams = {
    TableName: 'CognitoLab',
    Key: {
      CognitoIdentity: { "S": AWS.config.credentials.identityId }
    }
  };
  return getParams;
}

SolutionCode.prototype.processResults = function(err, data) {
  var solutionWarning =
      "RUNNING SOLUTION CODE:" +
      "processResults! " +
      "Follow the steps in the lab guide to replace this method with your own implementation.";
  addResult(results, solutionWarning);

  if (err) {
    addResult(results, "<b>Error:</b> " + err, "error");
    return;
  }
  if (!data.Item) {
    addResult(results, "No Items", "error");
  }
  var record = "<b>Hash</b> " + data.Item.CognitoIdentity.S +
    "<br><b>Text</b> " + data.Item.Text.S;
  addResult(results, record);
}

SolutionCode.prototype.filterRows = function() {
  var solutionWarning =
      "\nRUNNING SOLUTION CODE:" +
      "filterRows! " +
      "Follow the steps in the lab guide to replace this method with your own implementation.";
  addResult(results, solutionWarning);

  var scanParams = {
    TableName: 'CognitoLab',
  };
  dynamoDB.scan(scanParams, function(err, data) {
    if (err) {
      addResult(results, "<b>Error:</b> " + err, "error");
      return;
    }
  });

  var deleteParams = {
    TableName: 'CognitoLab',
    Key: {
      CognitoIdentity: { "S": "<another-identity>" }
    }
  };
  dynamoDB.deleteItem(deleteParams, function(err, data) {
    if (err) {
      addResult(results, "<b>Error:</b> " + err, "error");
      return;
    }
  });
}
