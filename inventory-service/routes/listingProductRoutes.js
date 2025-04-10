const express = require('express');
const listingProductController = require('../controllers/listingProductController');
const router = express.Router();

router.get('/filterProductsByCriteria', listingProductController.filterProductsByCriteria);

module.exports = router;