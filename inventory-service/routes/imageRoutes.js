const express = require('express');
const imageController = require('../controllers/imageController');
const router = express.Router();

router.get('/getAllImages', imageController.getAllImages);
router.post('/createImage', imageController.createImage);

module.exports = router;