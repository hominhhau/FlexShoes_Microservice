const express = require('express');
const cors = require('cors');
const connectDB = require('./configs/db');
require('dotenv').config();
const productRoutes = require('./routes/productRoutes');

const app = express();
app.use(cors());
app.use(express.json());

app.use('/products', productRoutes);
connectDB();


module.exports = app;
