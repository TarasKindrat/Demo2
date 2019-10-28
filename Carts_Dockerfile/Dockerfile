FROM java:openjdk-8-alpine

WORKDIR /opt
#COPY /home/taras/carts/carts.jar ./carts.jar
#EXPOSE 8081
ARG MONGO-DB_IP=""
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/urandom", "-Ddb:carts-db=${MONGO-DB_IP}", "-jar", "./carts.jar", "--port=80"]
CMD [, ]

