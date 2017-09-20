package day1.lab2;

import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;

import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * lab2 S3 일반 업로드와 멀티파트 업로드 성능차이 실습문제
 *
 * cli에서는 멀티파트로 수행해도 구조적인 문제로 속도향상을 얻을 수 없음
 * sdk에서는 병열처리를 하기 때문에 100메가 이상에서 속도향상의 이점을 얻을 수 있음
 *
 * aws-dev Created by daekwon.kang@gmail.com on 20/09/2017
 * Blog : http://ncrash.github.io/
 * Github : http://github.com/ncrash
 */
public class Practice {
  public static final String OUTPUT_BUCKET_NAME = "dev-on-aws-output.daekwon.kang";

  // Region in which student's buckets will be created
  public static final Region BUCKET_REGION = Utils.getRegion();

  // The Amazon S3 client allows you to manage buckets and objects programmatically
  public static AmazonS3Client s3ClientForStudentBuckets;

  public static void main(String[] args) {
    // Create AmazonS3Client
    // The AmazonS3Client will automatically retrieve the credential profiles file at the default location (~/.aws/credentials)
    s3ClientForStudentBuckets = createS3Client(BUCKET_REGION);

    StopWatch stopWatch = new StopWatch();
    stopWatch.reset();
    stopWatch.start();

    String fileKey = "big-file-multipart-off";
    File uploadFile = new File("/Users/ncrash/Downloads/test1234");

    putObjectBasic(OUTPUT_BUCKET_NAME, fileKey, uploadFile);

    stopWatch.stop();
    System.out.println("basic duration time : " + stopWatch.toString());

    stopWatch.reset();
    stopWatch.start();

    putObjectMultipart(OUTPUT_BUCKET_NAME, fileKey, uploadFile);

    stopWatch.stop();

    System.out.println("multipart duration time : " + stopWatch.toString());
  }

  /**
   * Upload a file to a S3 bucket
   *
   * @param bucketName        Name of the S3 bucket
   * @param fileKey           Key (path) to the file
   * @param transformedFile   Contents of the file
   */
  private static void putObjectBasic(String bucketName, String fileKey, File transformedFile) {
    s3ClientForStudentBuckets.putObject(bucketName, fileKey, transformedFile);
  }

  /**
   * Multipart Upload a file to a S3 bucket
   *
   * @param bucketName        Name of the S3 bucket
   * @param fileKey           Key (path) to the file
   * @param file   Contents of the file
   */
  private static void putObjectMultipart(String bucketName, String fileKey, File file) {
// Create a list of UploadPartResponse objects. You get one of these for
// each part upload.
    List<PartETag> partETags = new ArrayList<PartETag>();

// Step 1: Initialize.
    InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
            bucketName, fileKey);
    InitiateMultipartUploadResult initResponse =
            s3ClientForStudentBuckets.initiateMultipartUpload(initRequest);

    long contentLength = file.length();
    long partSize = 5 * 1024 * 1024; // Set part size to 5 MB.

    try {
      // Step 2: Upload parts.
      long filePosition = 0;
      for (int i = 1; filePosition < contentLength; i++) {
        // Last part can be less than 5 MB. Adjust part size.
        partSize = Math.min(partSize, (contentLength - filePosition));

        // Create request to upload a part.
        UploadPartRequest uploadRequest = new UploadPartRequest()
                .withBucketName(bucketName).withKey(fileKey)
                .withUploadId(initResponse.getUploadId()).withPartNumber(i)
                .withFileOffset(filePosition)
                .withFile(file)
                .withPartSize(partSize);

        // Upload part and add response to our list.
        partETags.add(s3ClientForStudentBuckets.uploadPart(uploadRequest).getPartETag());

        filePosition += partSize;
      }

      // Step 3: Complete.
      CompleteMultipartUploadRequest compRequest = new
              CompleteMultipartUploadRequest(bucketName,
              fileKey,
              initResponse.getUploadId(),
              partETags);

      s3ClientForStudentBuckets.completeMultipartUpload(compRequest);
    } catch (Exception e) {
      s3ClientForStudentBuckets.abortMultipartUpload(new AbortMultipartUploadRequest(
              bucketName, fileKey, initResponse.getUploadId()));
    }
  }

  /**
   * Return a S3 Client
   *
   * @param bucketRegion    Region containing the buckets
   * @return                The S3 Client
   */
  private static AmazonS3Client createS3Client(Region bucketRegion) {
    return Solution.createS3Client(bucketRegion);
  }
}
