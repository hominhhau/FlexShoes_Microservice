# Sử dụng Node.js phiên bản 18
FROM node:20-alpine

# Thiết lập thư mục làm việc trong container
WORKDIR /ChatDB

# Sao chép file package.json và package-lock.json vào container
COPY package*.json ./

# Cài đặt các dependencies
RUN npm install
RUN npm install -g @babel/cli @babel/core

# Sao chép toàn bộ mã nguồn vào container
COPY . .

# build thư mục build
RUN npm run build-src

# Cấu hình biến môi trường để kết nối PostgreSQL
ENV DB_SSL=false 
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=1234
ENV DB_DATABASE_NAME=ChatDB
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV DB_DIALECT=postgres

# Expose cổng mà ứng dụng Node.js chạy
EXPOSE 8080

# Chạy ứng dụng Node.js
CMD ["npm", "run", "build"]

# docker build --tag node-docker .
# docker run -p 8080:8080 -d node-docker