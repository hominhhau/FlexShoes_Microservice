# Sử dụng Node.js phiên bản nhẹ
FROM node:18-alpine

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Sao chép file package.json và package-lock.json để tối ưu caching
COPY package*.json ./

# Cài đặt dependencies
RUN npm install --omit=dev

# Sao chép toàn bộ mã nguồn vào container
COPY . .

# Expose port mà service sử dụng
EXPOSE 5000

# Khởi động service
CMD ["node", "server.js"]
