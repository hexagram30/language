FROM redislabs/redisearch:latest

EXPOSE 6379
ENTRYPOINT ["redis-server", "--appendonly=yes", "--appendfsync=no", \
            "--save=300 1 60 10"]
