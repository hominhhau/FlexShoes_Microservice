const express = require('express');
const brandTypeController = require('../controllers/brandTypeController');
const router = express.Router();

router.get('/getAllBrandTypes', brandTypeController.getAllBrandTypes);
router.get('/getBrandTypes/:id', brandTypeController.getBrandTypeById);
router.get('/getBrandTypesByName/:name', brandTypeController.getBrandTypeByName);
router.post('/createBrandType', brandTypeController.createBrandType);
router.delete('/deleteBrandTypeById/:id', brandTypeController.deleteBrandType);

module.exports = router;