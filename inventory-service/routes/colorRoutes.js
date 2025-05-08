const express = require('express');
const colorController = require('../controllers/colorController');
const router = express.Router();

router.get('/getAllColors', colorController.getAllColors);
router.get('/getColorById/:id', colorController.getColorById);
router.get('/getColorByName/:name', colorController.getColorByName);
router.post('/createColor', colorController.createColor);
router.delete('/deleteColorById/:id', colorController.deleteColor);

module.exports = router;