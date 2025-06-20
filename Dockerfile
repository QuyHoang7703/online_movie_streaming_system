#BUILD STAGE
#Tạo môi trường build có maven 3.8.7 và có jdk 18, gắn tên stage là build
FROM maven:3.8.7-openjdk-18 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
# Copy thư mục src vào build/src của image
COPY src ./src
# Dọn dẹp build cũ và build project, tạo file '.jar' trong thư mục target, bỏ qua test
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:17

WORKDIR /app
COPY --from=build /build/target/OnlineMovieStreamingSystem-*.jar /app/app.jar

# Extract the JAR version
EXPOSE 8088

ENV ACTIVE_PROFILE=dev

CMD java -jar app.jar --spring.profiles.active=${ACTIVE_PROFILE}