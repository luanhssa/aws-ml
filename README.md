# AWS - Machine Learning

This project uploads data to AWS S3 and then uses this data to create a AWS Machine Learning Model
===

### Requirements:
- Java 8+
- AWS credentials
    - $USERHOME/aws/accessKeys.csv
    - or just provide the path

---

### Project tree:
    ├── src
    |    └── com
    |        └── learning
    |            └── aws
    |                ├── [MainApp.java](../blob/master/src/com/learning/aws/MainApp.java)
    |                ├── ml
    |                |    ├── [BuildModel.java](../blob/master/src/com/learning/aws/ml/BuildModel.java)
    |                |    ├── [Identifiers.java](../blob/master/src/com/learning/aws/ml/Identifiers.java)
    |                |    ├── [ML.java](../blob/master/src/com/learning/aws/ml/ML.java)
    |                |    └── [UseModel.java](../blob/master/src/com/learning/aws/ml/UseModel.java)
    |                ├── s3
    |                |    └── [S3.java](../blob/master/src/com/learning/aws/s3/S3.java)
    |                └── util
    |                    ├── [AWSUtil.java](../blob/master/src/com/learning/aws/util/AWSUtil.java)
    |                    ├── [CSVUtil.java](../blob/master/src/com/learning/aws/util/CSVUtil.java)
    |                    └── [Util.java](../blob/master/src/com/learning/aws/util/Util.java)
    ├── data
    |    ├── [data.csv](../blob/master/data/data.csv)
    |    └── [data-schema.json(../blob/master/data/data-schema.json)
    └── [pom.xml](../blob/master/pom.xml)

 