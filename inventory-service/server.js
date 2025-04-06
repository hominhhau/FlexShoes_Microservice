const app = require('./app');
// const swaggerJsDoc = require("swagger-jsdoc");
// const swaggerUi = require("swagger-ui-express");
const PORT = process.env.PORT || 5000;


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
  


app.listen(PORT,'0.0.0.0', () => {
    console.log(`Server/service running on port ${PORT}`);
    //console.log("Swagger UI: http://localhost:5000/api-docs");
});