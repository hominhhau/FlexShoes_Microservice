const express = require('express');
const productController = require('../controllers/productsController');
const router = express.Router();

router.get('/getAllProducts', productController.getAllProducts);
router.get('/getAllProducts/:id', productController.getProductById);
router.post('/filterProducts', productController.getFilteredProducts);


module.exports = router;