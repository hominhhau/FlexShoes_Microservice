const express = require('express');
const productTypeController = require('../controllers/productTypeController');
const router = express.Router();

router.get('/getAllProductTypes', productTypeController.getAllProductTypes);
router.get('/getProductTypeById/:id', productTypeController.getProductTypeById);
router.get('/getProductTypeByName/:name', productTypeController.getProductTypeByName);
router.post('/createProductType', productTypeController.createProductType);
router.delete('/deleteProductType/:id', productTypeController.deleteProductType);

module.exports = router;