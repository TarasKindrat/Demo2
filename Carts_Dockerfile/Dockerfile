FROM java:openjdk-8-alpine

WORKDIR /opt
COPY /home/taras/carts/carts.jar ./carts.jar
EXPOSE 8081
ENTRYPOINT ["/usr/bin/java","-Ddb:carts-db=mongo-db", "-jar"]
CMD ["./carts.jar", "--port=8081"]

