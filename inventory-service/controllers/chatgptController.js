require('dotenv').config();
const express = require('express');
const { OpenAI } = require('openai');

const app = express();
app.use(express.json());

const openai = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });
module.exports = {
    chatGPTResponse: async (req, res) => {
        const { shortDescription } = req.body;

        if (!shortDescription) {
            return res.status(400).json({ error: 'Missing shortDescription' });
        }

        try {
            const chatResponse = await openai.chat.completions.create({
                model: 'gpt-3.5-turbo',
                messages: [
                    {
                        role: 'system',
                        content: 'Bạn là một chuyên gia marketing chuyên viết mô tả sản phẩm giày hấp dẫn, dễ hiểu và có chất riêng.',
                    },
                    {
                        role: 'user',
                        content: `Viết lại mô tả hấp dẫn cho sản phẩm giày sau: "${shortDescription}"`,
                    },
                ],
                temperature: 0.8,
                max_tokens: 300,
            });

            const improvedDescription = chatResponse.choices[0].message.content;
            res.json({ improvedDescription });
        } catch (error) {
            console.error('Error generating description:', error);
            res.status(500).json({ error: 'Something went wrong' });
        }
    }
};



