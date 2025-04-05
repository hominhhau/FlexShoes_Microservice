const express = require('express');
const cors = require('cors');
const connectDB = require('./configs/db');
require('dotenv').config();

const productRoutes = require('./routes/productRoutes');
const brandTypeRoutes = require('./routes/brandTypeRoutes');
const numberOfProductsRoutes = require('./routes/numberOfProductsRoutes');
const imageRoutes = require('./routes/imageRoutes');
const colorRoutes = require('./routes/colorRoutes');
const sizeRoutes = require('./routes/sizeRoutes');
const productTypes = require('./routes/productTypeRoutes');


const app = express();
app.use(cors());
app.use(express.json());


app.use('/', productRoutes);
app.use('/', brandTypeRoutes);
app.use('/', colorRoutes);
app.use('/', sizeRoutes);
app.use('/', imageRoutes);
app.use('/', numberOfProductsRoutes);
app.use('/', productTypes);

connectDB();

app.get("/", (req, res) => {
    res.send("Inventory Service is running !");
});


module.exports = app;
