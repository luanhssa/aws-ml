package com.learning.aws.util;

import java.io.File;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

public class AWSUtil {

	static public AWSCredentials getCredentials() {
		String path = System.getProperty("user.home") + File.separator + "aws" + File.separator + "accessKeys.csv";
				
		return getCredentials(path);
	}
	
	static public AWSCredentials getCredentials(String path) {
		
		String[] cred = CSVUtil.read(false, path, ",").get(0);
		System.out.println(cred[0]);
		System.out.println(cred[1]);
		
		AWSCredentials credentials = new BasicAWSCredentials(cred[0], cred[1]);
		
		return credentials;
	}
}
