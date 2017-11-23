package com.learning.aws.ml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.machinelearning.AmazonMachineLearningClient;
import com.amazonaws.services.machinelearning.model.CreateDataSourceFromS3Request;
import com.amazonaws.services.machinelearning.model.CreateEvaluationRequest;
import com.amazonaws.services.machinelearning.model.CreateMLModelRequest;
import com.amazonaws.services.machinelearning.model.MLModelType;
import com.amazonaws.services.machinelearning.model.S3DataSpec;
import com.learning.aws.util.Util;

public class BuildModel {

    private AmazonMachineLearningClient client;
    private String friendlyEntityName;
    private String trainDataSourceId;
    private String testDataSourceId;
    private String mlModelId;
    private String evaluationId;
    private int trainPercent=70;
    private String trainingDataUrl;
    private String schemaFilename;
    
    public BuildModel(AWSCredentials credentials, String friendlyName, String trainingDataUrl, String schemaFilename) {
        this.client = new AmazonMachineLearningClient(credentials);
        this.friendlyEntityName = friendlyName;
        this.trainingDataUrl = trainingDataUrl;
        this.schemaFilename = schemaFilename;
        client.describeEvaluations();
    }

    public void build() throws IOException {
        createDataSources();
        createModel();
        createEvaluation();
    }

    public void createDataSources() throws IOException {
        trainDataSourceId = Identifiers.newDataSourceId();
        // trainDataSourceId = "ds-" + UUID.randomUUID().toString();  // simpler, a bit more ugly
        createDataSource(trainDataSourceId, friendlyEntityName + " - training data", 0, trainPercent);
        
        testDataSourceId = Identifiers.newDataSourceId();
        // testDataSourceId = "ds-" + UUID.randomUUID().toString();  // simpler, a bit more ugly
        createDataSource(testDataSourceId, friendlyEntityName + " - testing data", trainPercent, 100);
    }

    public void createDataSource(String entityId, String entityName, int percentBegin, int percentEnd) throws IOException {
        String dataSchema = Util.loadFile(schemaFilename);
        String dataRearrangementString = "{\"splitting\":{\"percentBegin\":"+percentBegin+",\"percentEnd\":"+percentEnd+",\"strategy\":\"random\"}}";
        CreateDataSourceFromS3Request request = new CreateDataSourceFromS3Request()
            .withDataSourceId(entityId)
            .withDataSourceName(entityName)
            .withComputeStatistics(true);
        S3DataSpec dataSpec = new S3DataSpec()
            .withDataLocationS3(trainingDataUrl)
            .withDataRearrangement(dataRearrangementString)
            .withDataSchema(dataSchema);
        request.setDataSpec(dataSpec);
        client.createDataSourceFromS3(request);
        System.out.printf("Created DataSource %s with id %s\n",  entityName, entityId);
    }
    
    /**
     * Creates an ML Model object, which begins the training process. 
     * The quality of the model that the training algorithm produces depends 
     * primarily on the data, but also on the hyper-parameters specified in 
     * the parameters map, and the feature-processing recipe.
     * @throws IOException 
     */
    public void createModel() throws IOException {
        mlModelId = Identifiers.newMLModelId();
        // mlModelId = "ml-" + UUID.randomUUID().toString();  // simpler, a bit more ugly
        
        Map<String, String> parameters = new HashMap<String,String>();
        parameters.put("sgd.maxPasses", "100");
        parameters.put("sgd.maxMLModelSizeInBytes", "104857600");  // 100 MiB
        parameters.put("sgd.l2RegularizationAmount", "1e-4");
        
        CreateMLModelRequest request = new CreateMLModelRequest()
            .withMLModelId(mlModelId)
            .withMLModelName(friendlyEntityName + " model")
            .withMLModelType(MLModelType.BINARY)
            .withParameters(parameters)
            .withTrainingDataSourceId(trainDataSourceId);
        client.createMLModel(request);
        System.out.printf("Created ML Model with id %s\n", mlModelId);
    }

    /**
     * Creates an Evaluation, which measures the quality of the ML Model
     * by seeing how many predictions it gets correct, when run on a 
     * held-out sample (30%) of the original data.
     */
    public void createEvaluation() {
        evaluationId = Identifiers.newEvaluationId();
        // evaluationId = "ev-" + UUID.randomUUID().toString();  // simpler, a bit more ugly
        CreateEvaluationRequest request = new CreateEvaluationRequest()
            .withEvaluationDataSourceId(testDataSourceId)
            .withEvaluationName(friendlyEntityName + " evaluation")
            .withMLModelId(mlModelId);
        client.createEvaluation(request);
        System.out.printf("Created Evaluation with id %s\n", evaluationId);
    }
}
