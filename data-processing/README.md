# Tycoon - Data processing
Data preprocessing will be done using Apache Spark. The goal of preprocessing
Includes all massive data processing using Spark batch
## Spark on Kubernetes

See [official website](https://spark.apache.org/docs/latest/running-on-kubernetes.html)

- Docker registry in Load Balancer

## Preparing environment (arm64)
- create build context for arm64
- build the image with selected context

## Dealing zipped files
Because the target data to be processed in tycoon are logs, it is common for zip compression to be used when ingesting large volumes of small text files.
To remedy this shortcoming, an attempt was made to use Minio's native ability to [read the contents of zip files directly from the object store](https://blog.min.io/small-file-archives/), but the following issues arose:
- Apache spark's implementation of the S3 file system does not allow adding custom http headers.
- Even if the source class is extended to pass the `x-minio-extract` header to `true`, Minio's native capability does not allow reading `.zip` files recursively (most target data for tycoon has this Format).

As an alternative solution, the possibility of adding support for `.zip` files to apache spark was studied.
In the first instance, we tried to read the `.zip` files directly as Spark binary files, and decompress their contents in memory.
The [original object](log-parser/src/main/scala/org/tycoon/utils/ZipUtils.scala) has been kept in case in the future the reading could be optimized enough not to cause a `java heap space` exception.

Another option considered was to extend hadoop's [CompressionCodec](https://hadoop.apache.org/docs/r2.8.0/api/org/apache/hadoop/io/compress/CompressionCodec.html) interface to support reading `.zip` files.
Hadoop doesn't natively support this kind of compression because ZIP is a container format, whereas, for example, GZip is just a stream-like format (used to store a file).

Since none of the previous options is viable, since it requires a lot of memory to keep the original file and the recursively decompressed ones, we have decided to do a local decompression with subsequent upload to Minio through a bash script.

### Decompressing and uploading ZIP files
The bash script `extract-and-push.sh` allows you to uncompress a directory containing recursively compressed ZIP files in a temporary directory, and then upload all the text files (.txt format) contained in it to the S3 file system offered by Minio.
```
./extract-and-push.sh -h
```

```
Usage: extract-and-push.sh [-h] [-v] -b mybucket -a mc_alias [-t /tmp/extractions] -i /tmp/input_folder

Given a directory path, recursively searches through the .zip files for txt files and uploads them to the selected s3 bucket.

Available options:

-h, --help        Print this help and exit
-v, --verbose     Print script debug info
-b, --bucket      [Mandatory] Target S3 bucket
-a, --mc-alias    [Mandatory] Target mc (minio client) alias with S3 credentials configured
-t, --temp-path   Temporary path where .zip files are extracted (default: /tmp/extract-and-push_1660663933799710587)
-i, --input-path  [Mandatory] Input directory path where target files are located
-m, --minio-bin   Minio client binary path (default: /usr/bin/mc)
-p, --bucket-path Path into the bucket where files are placed (default: root bucket path)
```

Example usage command:
```
./extract-and-push.sh -b tycoon -a k8s-minio -t /media/adrirrp/Data/manos_poker/tmp -i /media/adrirrp/Data/manos_poker/test -m /opt/minio/mc -p landing
```




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

https://blog.min.io/small-file-archives/

Explicar la aventura del zip
- Mejor opción: cabecera http en cliente minio
  - Spark no lo soporta
  - No permite zip anidados
- Opción codec zip para hadoop
  - Peta la JVM por hava heap
