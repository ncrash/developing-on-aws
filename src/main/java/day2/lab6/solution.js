// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

module.exports = {
  parmsForGet: function(srcBucket, srcKey) {
    console.log(
      'RUNNING SOLUTION CODE:' +
      'parmsForGet! ' +
      'Follow the steps in the lab guide to replace this method with your own implementation.');
    var params = {
      Bucket: srcBucket,
      Key: srcKey
    };
    return params;
  },
  parmsForPut: function(dstBucket, dstKey, data) {
    console.log(
      'RUNNING SOLUTION CODE:' +
      'parmsForPut! ' +
      'Follow the steps in the lab guide to replace this method with your own implementation.');
    var params = {
      Bucket: dstBucket,
      Key: dstKey,
      Body: data
    };
    return params;
  }
}
