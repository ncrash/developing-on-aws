// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

var async = require('async');
var AWS = require('aws-sdk');
var util = require('util');
var config = require('./config.js');
var solution = require('./solution.js');

// Before running the code, update ~/.aws/credentials file with your credentials.

var s3 = new AWS.S3({region: config.region});

exports.handler = function(event, context) {
  var srcBucket = event.Records[0].s3.bucket.name;
  var srcKey    = decodeURIComponent(event.Records[0].s3.object.key.replace(/\+/g, ' '));
  var dstBucket = config.outputBucket;
  var dstKey    = srcKey + ".json";

  // Sanity check: validate that source and destination are different buckets.
  if (srcBucket == dstBucket) {
    console.error('Destination bucket must not match source bucket.');
    return;
  }

  // This function will convert a CSV string into a JSON string.
  function transformText(str) {
    var lines = str.split("\n")
    headers = lines.shift().split(",")
    var json = []
    for (var x = 0; x < lines.length; x++) {
      if (lines[x].length > 0) {
        items = lines[x].split(",")
        var obj = {}
        for (var i in items) {
          obj.comment = "DataTransformer JSON"
          obj[headers[i]] = items[i]
        }
        json.push(obj)
      }
    }
    return JSON.stringify(json);
  }

  async.waterfall([
    function download(next) {
      // STUDENT TODO 2: Replace the solution with your own code
      // Replace the call to solution.parmsForGet and provide your own 'params' object.
      // 'params' must include the Bucket (srcBucket) and Key (srcKey) parameters.

      var params = solution.parmsForGet(srcBucket, srcKey);

      s3.getObject(params, next);
    },
    function transform(response, next) {
      originalString = response.Body.toString();
      var transformedString = transformText(originalString);
      var transformedObject = new Buffer(transformedString);
      next(null, transformedObject);
    },
    function upload(data, next) {
      // STUDENT TODO 3: Replace the solution with your own code
      // Replace the call to solution.parmsForPut and provide your own 'params' object.
      // 'params' must include the Bucket (dstBucket), Key (dstKey) and Body (data) parameters.

      var params = solution.parmsForPut(dstBucket, dstKey, data);

      s3.putObject(params, next);
    }],
    function(err) {
      if (err) {
        console.error('Unable to download, transform, or upload due to an error: ' + err);
      } else {
        console.log('Successfully transformed and uploaded: ');
      }
      context.done(err, 'Transformation complete');
    }
  );
};
