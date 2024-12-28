FROM openjdk:17
EXPOSE 8080
ADD target/purchase-coupon-api-docker.jar purchase-coupon-api-docker.jar
ENTRYPOINT ["java","-jar","/purchase-coupon-api-docker.jar"]