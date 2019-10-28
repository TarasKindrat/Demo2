FROM java:openjdk-8-alpine

WORKDIR /opt
COPY ./home/taras/carts/carts.jar ./carts.jar

ENTRYPOINT ["java","-Ddb:carts-db=mongo-db","-jar","./carts.jar", "--port=80"]
