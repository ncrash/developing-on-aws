// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

var config = require('./config.js');

module.exports = function(grunt) {

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    prompt: {
      createBuckets: {
        options: {
          questions: [
            {
              config: 'inputBucket',
              type: 'input',
              message: 'Please enter a name for your input bucket:',
              validate: function(value) {
                if(value.length > 2 &&
                   value.length < 64 &&
                   value.indexOf(" ") == -1 &&
                   value.indexOf("_") == -1) {
                  return true;
                } else {
                  return 'Invalid bucket name, please enter a bucket name in lower case, without spaces or underscores.';
                }
              }
            },
            {
              config: 'outputBucket',
              type: 'input',
              message: 'Please enter a name for your output bucket:',
              validate: function(value) {
                if(value.length > 2 &&
                   value.length < 64 &&
                   value.indexOf(" ") == -1 &&
                   value.indexOf("_") == -1) {
                  return true;
                } else {
                  return 'Invalid bucket name, please enter a bucket name in lower case, without spaces or underscores.';
                }
              }
            }
          ],
          then: function(answers, done) {
            var AWS = require('aws-sdk');
            var s3bucketinp = new AWS.S3({region: config.region, params: {Bucket: answers.inputBucket }});
            s3bucketinp.createBucket(function(err, data) {
              if (err) {
                if (err.code == 'BucketAlreadyExists')
                  console.log('Error: The bucket name ' + answers.inputBucket + ' is taken!');
                else if (err.code == 'InvalidBucketName')
                  console.log('Error: ' + answers.inputBucket + ' is an invalid bucket name!');
                else if (err.code == 'UnknownEndpoint')
                  console.log('Error: ' + config.region + ' is an invalid region!');
                else
                  console.log('Error: ' + err.message);
              }
              else
                console.log(answers.inputBucket + ' created successfully ')
            })

            var s3bucketout = new AWS.S3({region: config.region, params: {Bucket: answers.outputBucket }});
            s3bucketout.createBucket(function(err, data) {
              if (err) {
                if (err.code == 'BucketAlreadyExists')
                  console.log('Error: The bucket name ' + answers.outputBucket + ' is taken!');
                else if (err.code == 'InvalidBucketName')
                  console.log('Error: ' + answers.outputBucket + ' is an invalid bucket name!');
                else if (err.code == 'UnknownEndpoint')
                  console.log('Error: ' + config.region + ' is an invalid region!');
                else
                  console.log('Error: ' + err.message);
              }
              else {
                console.log(answers.outputBucket + ' created successfully ')
              }
              done();
            })
            return true;
          }
        }
      }
    },
    lambda_invoke: {
      default: {
      }
    },
    lambda_deploy: {
      default: {
        arn: config.lambdaARN
      }
    },
    lambda_package: {
      default: {

      }
    }
  });

  // Load plugins
  grunt.loadNpmTasks('grunt-aws-lambda');
  grunt.loadNpmTasks('grunt-prompt');

  // Register tasks
  grunt.registerTask('createBuckets','prompt:createBuckets');
  grunt.registerTask('test', function() {
    var AWS = require('aws-sdk');

    var s3 = new AWS.S3({region: config.region});

    var event = require('./event.json');
    var outputKey = event.Records[0].s3.object.key + ".json";
    var outputBucket = config.outputBucket;

    var JSON_COMMENT = "\"comment\": \"DataTransformer JSON\",";
    var done = this.async();

    grunt.task.requires('lambda_invoke');

    s3.getObject({Bucket: outputBucket, Key: outputKey}, function(err, data) {
      if (err) {
        grunt.log.error(err);
        done();
      } else {
        var output = data.Body.toString();
        if (output.search(JSON_COMMENT) >= 0) {
          grunt.log.error('Unable to download, transform, or upload due to an error:');
          done();
        } else {
          grunt.log.writeln('Successfully transformed and uploaded:');
          done();
        }
      }
    });
  });
};
