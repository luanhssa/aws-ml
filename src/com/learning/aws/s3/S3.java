package com.learning.aws.s3;

import java.io.File;
import java.util.List;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class S3 {

	private AmazonS3 s3;
	
	public S3(AWSCredentials credentials) {
		s3 = new AmazonS3Client(credentials);
        Region saEast1 = Region.getRegion(Regions.SA_EAST_1);
        s3.setRegion(saEast1);
//        s3.setEndpoint("https://s3.sa-east-1.amazonaws.com");
	}
	
	/**
	 * Returns the name generated to the created bucket
	 * @param name bucket prefix name
	 * @return bucketName full bucket name
	 */
	public String createBucket(String name) {
		String bucketName = name + "-" + UUID.randomUUID();
		
		System.out.println("Creating bucket " + bucketName + "\n");
        s3.createBucket(bucketName);

        return bucketName;
	}
	
	public List<Bucket> listBuckets() {
		List<Bucket> bucketList = s3.listBuckets();
		System.out.println("Listing buckets");
        for (Bucket bucket : bucketList) {
            System.out.println(" - " + bucket.getName());
        }
        System.out.println();
        
        return bucketList;
	}
	
	public void upload(String bucketName, String objectKey, File file) {
		System.out.println("Uploading a new object to S3 from a file\n");
        Region saEast1 = Region.getRegion(Regions.SA_EAST_1);
        s3.setRegion(saEast1);
//        s3.setEndpoint("https://us-east-1.s3.amazonaws.com");

		try {
			s3.putObject(new PutObjectRequest(bucketName, objectKey, file));
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        
	}
	
	public S3Object download(String bucketName, String objectKey) {
		System.out.println("Downloading an object");
        S3Object object = s3.getObject(new GetObjectRequest(bucketName, objectKey));
//        System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
//        displayTextInputStream(object.getObjectContent());
        
        return object;
	}
	
	public ObjectListing listObjects(String bucketName, String prefix) {
		System.out.println("Listing objects");
		ObjectListing objectListing;
		if(prefix != null) { 
			objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(bucketName)
                .withPrefix(prefix));
		} else {
			objectListing = s3.listObjects(new ListObjectsRequest()
	                .withBucketName(bucketName));
		}
//        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
//            System.out.println(" - " + objectSummary.getKey() + "  " +
//                    "(size = " + objectSummary.getSize() + ")");
//        }
//        System.out.println();
		
		return objectListing;
	}
	
	public void deleteObject(String bucketName, String objectKey) {
        System.out.println("Deleting an object\n");
        s3.deleteObject(bucketName, objectKey);
	}
	
	public void deleteBucket(String bucketName) {
		System.out.println("Deleting bucket " + bucketName + "\n");
        s3.deleteBucket(bucketName);
	}
}
