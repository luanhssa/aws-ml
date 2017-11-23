package com.learning.aws.ml;

import java.io.IOException;

public class ML {

	public static void main(String[] args) throws IOException {
		System.out.println("Hello World");

        String trainingDataUrl = "s3://aml-sample-data/banking.csv";
        String schemaFilename = "banking.csv.schema";
        String recipeFilename = "recipe.json";
        String friendlyEntityName = "Java Marketing Sample";
		
//		BuildModel builder = new BuildModel(friendlyEntityName, trainingDataUrl, schemaFilename, recipeFilename);
//        builder.build();
	}
}
