init:
	#docker run --rm --privileged docker/binfmt:a7996909642ee92942dcd6cff44b9b95f08dad64
	docker run --rm --privileged docker/binfmt:latest
	docker buildx create \
		--name multiarch-builder \
		--driver-opt network=host
	docker buildx use multiarch-builder
	docker buildx inspect --bootstrap
build:
	docker buildx build --platform linux/arm,linux/arm64,linux/amd64 -t docker.adrianramosrp.com/jcrypt-price-collector:latest .
push:
	docker buildx build \
		--platform linux/arm,linux/arm64,linux/amd64 \
		--progress=plain \
		--network=host \
		-t docker.adrianramosrp.com/jcrypt-price-collector:latest . --push
	#docker push docker.adrianramosrp.com/jcrypt-price-collector:latest
clean:
	docker buildx use default
	docker buildx rm multiarch-builder
all: clean init build push clean 
init-arm64:
	docker run --rm --privileged docker/binfmt:a7996909642ee92942dcd6cff44b9b95f08dad64
	docker buildx create \
		--name arm64-builder \
		--driver-opt network=host
	docker buildx use arm64-builder
	docker buildx inspect --bootstrap
build-arm64:
	docker buildx build --platform linux/arm64 -t docker.adrianramosrp.com/jcrypt-price-collector:latest .
push-arm64:
	docker buildx build \
		--platform linux/arm,linux/arm64,linux/amd64 \
		--progress=plain \
		--network=host \
		-t docker.adrianramosrp.com/jcrypt-price-collector:latest . --push
	#docker push docker.adrianramosrp.com/jcrypt-price-collector:latest
clean-arm64:
	docker buildx use default
	docker buildx rm arm64-builder
all-arm64: clean init build push clean 
