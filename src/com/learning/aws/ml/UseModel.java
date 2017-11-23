package com.learning.aws.ml;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import com.amazonaws.services.machinelearning.AmazonMachineLearningClient;
import com.amazonaws.services.machinelearning.model.CreateBatchPredictionRequest;
import com.amazonaws.services.machinelearning.model.CreateDataSourceFromS3Request;
import com.amazonaws.services.machinelearning.model.GetMLModelRequest;
import com.amazonaws.services.machinelearning.model.GetMLModelResult;
import com.amazonaws.services.machinelearning.model.S3DataSpec;
import com.amazonaws.services.machinelearning.model.UpdateMLModelRequest;
import com.learning.aws.util.Util;

/**
 * This class demonstrates using a model built by 
 * {@link com.learning.aws.ml.BuildModel}
 * to make batch predictions.
 */
public class UseModel {
    private static final String UNSCORED_DATA_S3_URL = "s3://aws-ml-cb285a72-4f15-45bd-b92e-d6b360c8c203/banking.csv";
    private static final String UNSCORED_DATA_LOCAL_SCHEMA = "banking.csv.schema";

    public static void main(String[] args) throws IOException {
        UseModel useModel = new UseModel(args);
        useModel.waitForModel();
        useModel.setThreshold();
        useModel.createBatchPrediction();
    }
    
    private String mlModelId;
    private float threshold;
    private String s3OutputUrl;
    private AmazonMachineLearningClient client;
    private Random random;
    private String schemaFilename;
    
    /**
     * @param args command-line arguments:
     *   mlModelid
     *   score threshhold
     *   s3:// url where output should go
     */
    public UseModel(String[] args) {
        mlModelId = args[0];
        threshold = Float.valueOf(args[1]);
        s3OutputUrl = args[2];
        this.client = new AmazonMachineLearningClient();
        random = new Random();
    }


    /**
     * waits for the model to reach a terminal state.
     */
    private void waitForModel() {
        long delay = 2000;
        while(true) {
            GetMLModelRequest request = new GetMLModelRequest()
                .withMLModelId(mlModelId);
            GetMLModelResult model = client.getMLModel(request);
            System.out.printf("Model %s is %s at %s\n", mlModelId, model.getStatus(), (new Date()).toString());
            switch(model.getStatus()) {
            case "COMPLETED":
            case "FAILED":
            case "INVALID":
                // These are terminal states
                return;
            }
            
            // exponential backoff with Jitter
            delay *= 1.1 + random.nextFloat();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
        
    }
    
    
    /**
     * Sets the score threshold on the ML Model.
     * This configures the ML Model by picking what raw prediction score
     * is the cut-off between a positive & a negative prediction.
     */
    private void setThreshold() {
        UpdateMLModelRequest request = new UpdateMLModelRequest()
            .withMLModelId(mlModelId)
            .withScoreThreshold(threshold);
        client.updateMLModel(request);
    }
    

    private void createBatchPrediction() throws IOException {
        // First create a datasource for the input data for the batch prediction
        String dataSourceId = Identifiers.newDataSourceId();
        // dataSourceId = "ds-" + UUID.randomUUID().toString();  // simpler, a bit more ugly
        S3DataSpec dataSpec = new S3DataSpec()
            .withDataSchema(Util.loadFile(UNSCORED_DATA_LOCAL_SCHEMA))
            .withDataLocationS3(UNSCORED_DATA_S3_URL);
        CreateDataSourceFromS3Request dsRequest = new CreateDataSourceFromS3Request()
            .withDataSourceId(dataSourceId)
            .withDataSourceName("DataSource for batch prediction")
            .withDataSpec(dataSpec)
            .withComputeStatistics(false);
        client.createDataSourceFromS3(dsRequest);
        System.out.printf("Created DataSource for batch prediction with id %s\n", dataSourceId);
        
        String batchPredictionId = Identifiers.newBatchPredictionId();
        // batchPredictionId = "bp-" + UUID.randomUUID().toString();  // simpler, a bit more ugly
        CreateBatchPredictionRequest bpRequest = new CreateBatchPredictionRequest()
            .withBatchPredictionId(batchPredictionId)
            .withBatchPredictionName("Java sample Batch Prediction")
            .withMLModelId(mlModelId)
            .withOutputUri(this.s3OutputUrl)
            .withBatchPredictionDataSourceId(dataSourceId);
        client.createBatchPrediction(bpRequest);
        System.out.printf("Created BatchPrediction with id %s\n", batchPredictionId);
    }
}
