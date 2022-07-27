# Tycoon - Data processing

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
    --conf spark.executor.instances=5 \
    --conf spark.kubernetes.container.image=<spark-image> \
    local:///path/to/examples.jar

docker pull --platform linux/arm64 apache/spark:latest
docker tag c544b2629c53 docker.adrianramosrp.com/spark:latest
docker push docker.adrianramosrp.com/spark:latest


./bin/spark-submit \
    --master k8s://https://192.168.1.171:6443 \
    --deploy-mode cluster \
    --name spark-pi \
    --class org.apache.spark.examples.SparkPi \
    --conf spark.executor.instances=5 \
    --conf spark.kubernetes.container.image=apache/spark:latest \
    local:///opt/spark/examples/jars/spark-examples_2.12-3.3.0.jar

