const app = require('./app');
// const swaggerJsDoc = require("swagger-jsdoc");
// const swaggerUi = require("swagger-ui-express");
const PORT = process.env.PORT || 8085;


// const swaggerOptions = {
//     swaggerDefinition: {
//       openapi: "3.0.0",
//       info: {
//         title: "Inventory Service API",
//         description: "API quản lý sản phẩm, thương hiệu, kích thước, màu sắc...",
//         version: "1.0.0",
//         contact: {
//           name: "Nguyễn Thị Quỳnh Giang",
//         },
//       },
//       servers: [
//         {
//           url: "http://localhost:5000",
//         },
//       ],
//     },
//     apis: ["./routes/*.js"], 
//   };
  
//   const swaggerDocs = swaggerJsDoc(swaggerOptions);
//   app.use("/api-docs", swaggerUi.serve, swaggerUi.setup(swaggerDocs));
  

// const { Eureka } = require('eureka-js-client');
// const os = require('os');

// function registerWithEureka() {
//     const hostName = "localhost"; // hoặc dùng "localhost" nếu chạy local
//     const ipAddr = '127.0.0.1'; // hoặc IP máy thật nếu chạy thật
  
//     const client = new Eureka({
//       instance: {
//         app: 'inventory-service',
//         hostName,
//         ipAddr,
//         port: {
//           '$': PORT,
//           '@enabled': true
//         },
//         vipAddress: 'inventory-service',
//         dataCenterInfo: {
//           '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
//           name: 'MyOwn'
//         }
//       },
//       eureka: {
//         host: 'localhost', // địa chỉ của Eureka server
//         port: 8761,
//         servicePath: '/eureka/apps/',
//         maxRetries: 3,
//         requestRetryDelay: 5000
//       }
//     });
  
//     client.start(error => {
//       if (error) {
//         console.error('❌ Lỗi khi đăng ký Eureka:', error);
//       } else {
//         console.log('🎉 Node service đã đăng ký với Eureka!');
//       }
//     });
//   }



app.listen(PORT,'0.0.0.0', () => {
    console.log(`Server/service running on port ${PORT}`);
    //registerWithEureka();
    //console.log("Swagger UI: http://localhost:5000/api-docs");
});