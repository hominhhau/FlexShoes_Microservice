const http = require("http");
const express = require("express");
const cors = require("cors");
const configCORS = require("./config/cors.js");
const configViewEngine = require("./config/viewEngine.js");
const chatController = require("./controller/chatController.js");

const app = express();
// config viewEngine
configViewEngine(app);

// Middleware để phân tích cú pháp JSON
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// CORS middleware
configCORS(app);

// chat admin socket.id
app.get("/chat", (req, res) => {
  res.render("chatAdmin.ejs");
});


app.post("/send", chatController.sendMess);
app.post("/show", chatController.showMess);
app.get("/getAllSender", chatController.getAllSender);

// Bắt đầu lắng nghe trên một cổng
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
