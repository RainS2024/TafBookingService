
FROM amazoncorretto:17
WORKDIR /app
COPY build/libs/Booking-service-v1.jar app.jar
EXPOSE 8082
CMD ["java", "-jar", "app.jar"]
