const express = require('express');
const productController = require('../controllers/productsController');
const router = express.Router();
const { uploadMultiple } = require('../middleware/upload');


router.get('/getAllProducts', productController.getAllProducts);
router.get('/getAllProducts/:id', productController.getProductById);
router.post('/createProduct', uploadMultiple, productController.createProduct);
router.post('/update', uploadMultiple, productController.update);
// router.post('/filterProducts', productController.getFilteredProducts);


module.exports = router;