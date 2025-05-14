# Sử dụng Node.js phiên bản 18
FROM node:20-alpine

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Sao chép file package.json và package-lock.json vào container
COPY package*.json ./

# Cài đặt các dependencies
RUN npm install
RUN npm install -g @babel/cli @babel/core

# Sao chép toàn bộ mã nguồn vào container
COPY . .

# build thư mục build
# RUN npm run build-src

# Thiết lập biến môi trường mặc định (có thể bị ghi đè từ docker-compose)
ENV DB_SSL=false 
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=1234
# sửa lại tên database ở đây
ENV DB_DATABASE_NAME=ChatDB
# ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV DB_DIALECT=postgres

# Expose cổng mà ứng dụng Node.js chạy
EXPOSE 8089

# Chạy ứng dụng Node.js
CMD ["sh", "-c", "npx sequelize-cli db:migrate && npm start"]

# docker build --tag node-docker .
# docker run -p 8089:8089 -d node-docker