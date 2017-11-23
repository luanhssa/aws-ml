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
    |                ├── MainApp.java
    |                ├── ml
    |                |    ├── BuildModel.java
    |                |    ├── Identifiers.java
    |                |    ├── ML.java
    |                |    └── UseModel.java
    |                ├── s3
    |                |    └── S3.java
    |                └── util
    |                    ├── AWSUtil.java
    |                    ├── CSVUtil.java
    |                    └── Util.java
    ├── data
    |    ├── data.csv
    |    └── data-schema.json
    └── pom.xml

 