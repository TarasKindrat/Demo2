FROM java:openjdk-8-alpine
COPY ./target/carts.jar /opt/carts.jar
WORKDIR /opt
#ADD /home/taras/carts/carts.jar ./carts.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/urandom","-jar","./carts.jar", "--port=80"]
