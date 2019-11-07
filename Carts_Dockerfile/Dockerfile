FROM java:openjdk-8-alpine

WORKDIR /opt
ADD /home/taras/carts/carts.jar ./carts.jar
#EXPOSE 8081
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/urandom","-jar","./carts.jar", "--port=80"]
#CMD ["java", "-Djava.security.egd=file:/dev/urandom", "-Ddb:carts-db=MONGO-DB_IP", "-jar", "./carts.jar", "--port=80"]


