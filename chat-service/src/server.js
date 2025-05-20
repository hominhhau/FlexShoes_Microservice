const http = require("http");
const express = require("express");
const cors = require("cors");
const configCORS = require("./config/cors.js");
const configViewEngine = require("./config/viewEngine.js");
const chatController = require("./controller/chatController.js");
const ChatGPTController = require('../src/controller/chatgptController.js')

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


app.post("/chat/send", chatController.sendMess);
app.post("/chat/show", chatController.showMess);
app.get("/chat/getAllSender", chatController.getAllSender);
app.get("/chat/getLastMessage", chatController.getLastMessage);
app.post("/chat/updateMessageStatus", chatController.updateMessageStatus);

// chatbot AI chatGPT
app.post('/chat/chatgpt', ChatGPTController.chatGPTResponse);

const { Eureka } = require('eureka-js-client');

function registerWithEureka(port) {
  const hostName = "chat-service";
  const ipAddr = '127.0.0.1';

  const client = new Eureka({
    instance: {
      app: 'chat-service',
      hostName,
      ipAddr,
      port: {
        '$': port,
        '@enabled': true
      },
      vipAddress: 'chat-service',
      dataCenterInfo: {
        '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
        name: 'MyOwn'
      },
    },
    eureka: {
      host: 'eureka-server',
      port: 8761,
      servicePath: '/eureka/apps/',
      maxRetries: 3,
      requestRetryDelay: 5000
    }
  });

  client.start(error => {
    if (error) {
      console.error('❌ Lỗi khi đăng ký Eureka:', error);
    } else {
      console.log('🎉 Đã đăng ký service với Eureka!');
    }
  });
}

// Bắt đầu lắng nghe trên một cổng
const PORT = process.env.PORT || 8089;
app.listen(PORT, () => {
  registerWithEureka(PORT); // Đăng ký với Eureka tại đây
  console.log(`Server is running on port ${PORT}`);
});
