const express = require('express');
const sizeController = require('../controllers/sizeController');
const router = express.Router();

router.get('/getAllSizes', sizeController.getAllSizes);
router.get('/getSizeById/:id', sizeController.getSizeById);
router.get('/getSizeByName/:name', sizeController.getSizeByName);
router.post('/createSize', sizeController.createSize);
router.delete('/deleteSizeById/:id', sizeController.deleteSize);

module.exports = router;