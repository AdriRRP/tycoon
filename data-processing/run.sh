./bin/spark-submit \
    --master k8s://https://192.168.1.171:6443 \
    --deploy-mode cluster \
    --name spark-test-s3 \
    --class org.apache.spark.examples.SparkPi \
    --conf spark.executor.instances=2 \
    --conf spark.kubernetes.container.image=docker.adrianramosrp.com/spark:latest \
    --conf spark.kubernetes.container.image.pullPolicy=Always \
    --conf spark.kubernetes.container.image.pullSecrets=regcred \
    --conf spark.kubernetes.authenticate.driver.serviceAccountName=spark \
    --conf spark.hadoop.fs.s3a.access.key=minio \
    --conf spark.hadoop.fs.s3a.secret.key=minio1234 \
    --conf spark.hadoop.fs.s3a.endpoint=http://192.168.1.214:9000 \
    --conf spark.hadoop.fs.s3a.credentials.provider=org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider \
    --conf spark.hadoop.fs.s3a.impl=org.apache.hadoop.fs.s3a.S3AFileSystem \
    --conf spark.hadoop.fs.s3a.fast.upload=true \
    --conf spark.hadoop.fs.s3a.connection.ssl.enabled=true \
    s3a://spark/spark-examples_2.12-3.3.0.jar
#    local:///opt/spark/examples/jars/spark-examples_2.12-3.3.0.jar
    #--conf spark.kubernetes.file.upload.path=s3a://spark/spark \

#    --conf spark.hadoop.mapreduce.fileoutputcommitter.algorithm.version=2 \
#    --conf spark.hadoop.mapreduce.fileoutputcommitter.cleanup-failures.ignored=true \
    #--packages org.apache.hadoop:hadoop-aws:3.2.2 \
#    s3a://spark/spark-examples_2.12-3.3.0.jar
