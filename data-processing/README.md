# Tycoon - Data processing
Data preprocessing will be done using Apache Spark. The goal of preprocessing
Includes all massive data processing using Spark batch
## Spark on Kubernetes

See [official website](https://spark.apache.org/docs/latest/running-on-kubernetes.html)

- Docker registry in Load Balancer

## Preparing environment (arm64)
- create build context for arm64
- build the image with selected context

## Reading zipped files



./bin/spark-submit \
    --master k8s://https://<k8s-apiserver-host>:<k8s-apiserver-port> \
    --deploy-mode cluster \
    --name spark-pi \
    --class org.apache.spark.examples.SparkPi \
    --conf spark.executor.instances=2 \
    --conf spark.kubernetes.container.image=<spark-image> \
    local:///path/to/examples.jar

https://normanlimxk.com/2022/05/04/cloud-agnostic-big-data-processing-with-kubernetes-spark-and-minio/
# Add Amazon S3 jars to spark installation
wget https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/3.1.2/hadoop-aws-3.1.2.jar
wget https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk/1.11.534/aws-java-sdk-1.11.534.jar
wget https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-core/1.11.534/aws-java-sdk-core-1.11.534.jar
wget https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-dynamodb/1.11.534/aws-java-sdk-dynamodb-1.11.534.jar
wget https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-kms/1.11.534/aws-java-sdk-kms-1.11.534.jar
wget https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-s3/1.11.534/aws-java-sdk-s3-1.11.534.jar
wget https://repo1.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.3/httpclient-4.5.3.jar
wget https://repo1.maven.org/maven2/joda-time/joda-time/2.9.9/joda-time-2.9.9.jar

# Copiar los jars al directorio jars/ en $SPARK_HOME
# Estos ficheros se añadirán a la imagen que vamos a crear

docker pull --platform linux/arm64 apache/spark:latest
docker tag c544b2629c53 docker.adrianramosrp.com/spark:latest
docker push docker.adrianramosrp.com/spark:latest

kubectl create serviceaccount spark

 kubectl create clusterrolebinding spark-role --clusterrole=edit --serviceaccount=default:spark --namespace=default

./bin/spark-submit \
    --master k8s://https://192.168.1.171:6443 \
    --deploy-mode cluster \
    --name spark-pi \
    --class org.apache.spark.examples.SparkPi \
    --conf spark.executor.instances=5 \
    --conf spark.kubernetes.container.image=docker.adrianramosrp.com/spark:latest \
    --conf spark.kubernetes.container.image.pullSecrets=regcred \
    --conf spark.kubernetes.authenticate.driver.serviceAccountName=spark \ 
    --conf spark.hadoop.fs.s3a.access.key=minio \
    --conf spark.hadoop.fs.s3a.secret.key=minio1234 \
    --conf spark.hadoop.fs.s3a.impl=org.apache.hadoop.fs.s3a.S3AFileSystem \
    --conf spark.hadoop.fs.s3a.endpoint=minio-service.default.svc.cluster.local:9000 \
    --conf spark.hadoop.fs.s3a.connection.ssl.enabled=false \
    --conf spark.hadoop.fs.s3a.path.style.access=true \
    --conf spark.hadoop.mapreduce.fileoutputcommitter.algorithm.version=2 \
    --conf spark.hadoop.mapreduce.fileoutputcommitter.cleanup-failures.ignored=true \
    --conf spark.kubernetes.authenticate.caCertFile=/var/run/secrets/kubernetes.io/serviceaccount/ca.crt  \
    --conf spark.kubernetes.authenticate.oauthTokenFile=/var/run/secrets/kubernetes.io/serviceaccount/token  \
    --jars s3a://spark/spark-examples_2.12-3.3.0.jar


    local:///opt/spark/examples/jars/spark-examples_2.12-3.3.0.jar

./bin/spark-shell \
    --master k8s://https://192.168.1.171:6443 \
    --deploy-mode client \
    --name spark-shell \
    --conf spark.executor.instances=1 \
    --conf spark.kubernetes.container.image=docker.adrianramosrp.com/spark:latest \
    --conf spark.kubernetes.container.image.pullSecrets=regcred \
    --conf spark.kubernetes.container.image.pullPolicy=Always \
    --conf spark.kubernetes.authenticate.driver.serviceAccountName=spark

spark.sparkContext.hadoopConfiguration.set("fs.s3a.access.key", "minio")
spark.sparkContext.hadoopConfiguration.set("fs.s3a.secret.key", "minio1234")
spark.sparkContext.hadoopConfiguration.set("fs.s3a.endpoint", "minio-service.default.svc.cluster.local:9000")
spark.sparkContext.hadoopConfiguration.set("fs.s3a.endpoint", "http://192.168.1.214:9000")
spark.sparkContext.hadoopConfiguration.set("fs.s3a.aws.credentials.provider", "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")
val df = spark.read.csv("s3a://spark/addresses.csv")


./bin/spark-submit \
    --master k8s://https://192.168.1.171:6443 \
    --deploy-mode cluster \
    --name spark-test-s3 \
    --class org.apache.spark.examples.SparkPi \
    --packages org.apache.hadoop:hadoop-aws:3.2.2 \
    --conf spark.executor.instances=5 \
    --conf spark.kubernetes.container.image=docker.adrianramosrp.com/spark:latest \
    --conf spark.kubernetes.container.image.pullSecrets=regcred \
    --conf spark.kubernetes.authenticate.driver.serviceAccountName=spark \ 
    --conf spark.kubernetes.file.upload.path=s3a://spark/spark
    --conf spark.hadoop.fs.s3a.access.key=minio \
    --conf spark.hadoop.fs.s3a.secret.key=minio1234 \
    --conf spark.hadoop.fs.s3a.endpoint=192.168.1.214:9000 \
    --conf spark.hadoop.fs.s3a.credentials.provider=org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider \
    --conf spark.hadoop.fs.s3a.impl=org.apache.hadoop.fs.s3a.S3AFileSystem
    --conf spark.hadoop.fs.s3a.fast.upload=true
    --conf spark.hadoop.fs.s3a.connection.ssl.enabled=false \
    --conf spark.hadoop.mapreduce.fileoutputcommitter.algorithm.version=2 \
    --conf spark.hadoop.mapreduce.fileoutputcommitter.cleanup-failures.ignored=true \
    s3a://spark/spark-examples_2.12-3.3.0.jar

./bin/docker-image-tool.sh -r docker.adrianramosrp.com -t latest -f kubernetes/dockerfiles/spark/Dockerfile build
./bin/docker-image-tool.sh -r docker.adrianramosrp.com -t latest -f kubernetes/dockerfiles/spark/Dockerfile push

#ADD https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/3.3.2/hadoop-aws-3.3.2.jar /opt/spark/jars
#ADD https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-bundle/1.11.1026/aws-java-sdk-bundle-1.11.1026.jar /opt/spark/jars
#ADD https://repo1.maven.org/maven2/org/wildfly/openssl/wildfly-openssl/1.0.7.Final/wildfly-openssl-1.0.7.Final.jar /opt/spark/jars

