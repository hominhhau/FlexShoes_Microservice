const mongoose = require("mongoose");

const NumberOfProductsSchema = new mongoose.Schema({
    quantity: {
        type: Number,
        required: true
    },
    size: {
        type: mongoose.Schema.Types.ObjectId, ref: 'Size'
    },
    color:{
        type: mongoose.Schema.Types.ObjectId, ref: 'Color'
    },
    product: {
        type: mongoose.Schema.Types.ObjectId, ref: 'Product'
    },
});
module.exports = mongoose.model("NumberOfProducts", NumberOfProductsSchema);