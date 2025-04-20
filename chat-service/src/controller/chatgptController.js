require("dotenv").config();
const express = require("express");
const { OpenAI } = require("openai");

// const app = express();
// app.use(express.json());

const openai = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });
module.exports = {
  chatGPTResponse: async (req, res) => {
    const { message } = req.body;

    if (!message) {
      return res.status(400).json({ error: "Missing message" });
    }

    try {
      const response = await openai.chat.completions.create({
        model: "gpt-3.5-turbo",
        messages: [
          {
            role: "system",
            content:
              "Bạn là một chuyên gia marketing chuyên viết mô tả sản phẩm giày hấp dẫn, dễ hiểu và có chất riêng.",
          },
          { role: "user", content: message },
        ],
      });

      const reply = response.choices[0].message.content;
      res.json({ reply });
    } catch (error) {
      console.error("Error generating description:", error);
      res.status(500).json({ error: "Something went wrong" });
    }
  },
};
