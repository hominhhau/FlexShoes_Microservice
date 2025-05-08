const express = require('express');
const numberOfProductsController = require('../controllers/numberOfProductsController');
const router = express.Router();

router.get('/getAllNumberOfProducts', numberOfProductsController.getAllNumberOfProducts);
router.get('/getNumberOfProductsById/:id', numberOfProductsController.getNumberOfProductsById);
router.post('/createQuantity', numberOfProductsController.createQuantity);
router.patch('/attachInventoryToProduct', numberOfProductsController.attachInventoryToProduct);
module.exports = router;