const express = require('express');
const cors = require('cors');
const connectDB = require('./configs/db');
require('dotenv').config();

// Import routes
const productRoutes = require('./routes/productRoutes');
const brandTypeRoutes = require('./routes/brandTypeRoutes');
const numberOfProductsRoutes = require('./routes/numberOfProductsRoutes');
const imageRoutes = require('./routes/imageRoutes');
const colorRoutes = require('./routes/colorRoutes');
const sizeRoutes = require('./routes/sizeRoutes');
const productTypes = require('./routes/productTypeRoutes');
const listingProductRoutes = require('./routes/listingProductRoutes');

const app = express();

// ✅ CORS config to allow credentials from localhost:3000
app.use(cors({
    origin: 'http://localhost:3000',
    credentials: true,
}));

// Body parser middleware
app.use(express.json());

// Route middlewares
app.use('/inventory', productRoutes);
app.use('/inventory', brandTypeRoutes);
app.use('/inventory', colorRoutes);
app.use('/inventory', sizeRoutes);
app.use('/inventory', imageRoutes);
app.use('/inventory', numberOfProductsRoutes);
app.use('/inventory', productTypes);
app.use('/inventory', listingProductRoutes);

// Connect DB
connectDB();

// Test endpoint
app.get("/", (req, res) => {
    res.send("Inventory Service is running!");
});

// Start server
const PORT = process.env.PORT || 8085;
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});
