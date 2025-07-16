const mongoose = require('mongoose');

const BrandTypeSchema = new mongoose.Schema({
    brandTypeName:{
        type: String,
        required: true,
    },
    description:{
        type: String,
    }
    
});

module.exports = mongoose.model('BrandType', BrandTypeSchema);