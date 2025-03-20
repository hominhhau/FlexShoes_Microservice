# Giai đoạn build
FROM maven:3.8-openjdk-17-slim AS build
WORKDIR /app

# Copy file cấu hình Maven trước để tối ưu cache
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Tải dependencies để tối ưu tốc độ build
RUN ./mvnw dependency:go-offline

# Copy mã nguồn vào container
COPY src ./src

# Build ứng dụng (bỏ qua test để giảm thời gian)
RUN mvn clean package -DskipTests

# Giai đoạn chạy ứng dụng
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy file JAR từ giai đoạn build sang
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080
EXPOSE 8081

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
