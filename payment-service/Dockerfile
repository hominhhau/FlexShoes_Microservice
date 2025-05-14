# Giai đoạn build
FROM maven:3.8-openjdk-17-slim AS build
WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
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



RUN apt-get update && apt-get install -y tzdata && \
    ln -snf /usr/share/zoneinfo/Asia/Ho_Chi_Minh /etc/localtime && \
    echo "Asia/Ho_Chi_Minh" > /etc/timezone && \
    apt-get clean


# Copy file JAR từ giai đoạn build sang
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8081
EXPOSE 8081

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
