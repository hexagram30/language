VERSION=$(shell grep defproject project.clj |cut -f 3 -d' '|tr -d \")
RS_DOCKER_TAG=hexagram30/redisearch

docker-image:
	docker build -t $(RS_DOCKER_TAG) .
