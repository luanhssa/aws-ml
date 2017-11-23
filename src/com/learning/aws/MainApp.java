package com.learning.aws;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.model.Bucket;
import com.learning.aws.ml.BuildModel;
import com.learning.aws.s3.S3;
import com.learning.aws.util.AWSUtil;

public class MainApp {

	private AWSCredentials credentials;
	
	private String s3Bucket = "";
	
	public MainApp(String credentialsPath) {
		this.credentials = AWSUtil.getCredentials(credentialsPath);
	}
	
	public MainApp() {
		this.credentials = AWSUtil.getCredentials();
	}
	
	public void run() {
		s3();
		
		ml();
	}

	/**
	 * 1. Creates a s3 bucket
	 * 2. Uploads the data file
	 */
	private void s3() {
		S3 s3 = new S3(this.credentials);
		String bucketName = "aws-ml-";

		boolean bucketExists = false;
		List<Bucket> buckets = s3.listBuckets();
		
		for(Bucket bucket : buckets) {
			if(bucket.getName().startsWith(bucketName)) {
				System.out.println("Bucket '" + bucketName + "' already exists!");
				bucketExists = true;
				bucketName = bucket.getName();
			}
		}
		if(!bucketExists) {
			bucketName = s3.createBucket(bucketName);
			buckets = s3.listBuckets();
		}
		this.s3Bucket = bucketName;
		
		String dataFile = "data"+ File.separator +"data.csv";
		String dataName = "data.csv";
				
		s3.upload(this.s3Bucket, dataName, new File(dataFile));
		
//		s3.listObjects(bucketName, dataName);
	}

	/**
	 * 1. Gets the data from S3
	 * 2. Builds a Machine Learning model
	 */
	private void ml() {
		String trainingDataUrl = "s3://"+ this.s3Bucket +"/data.csv";
        String schemaFilename = "data/data-schema.json";
        String friendlyEntityName = "Java Small Nodules";
        
        BuildModel builder = new BuildModel(AWSUtil.getCredentials(), friendlyEntityName, trainingDataUrl, schemaFilename);
        try {
			builder.build();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MainApp app = new MainApp();
		app.run();
	}
}
