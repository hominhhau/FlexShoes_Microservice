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
});
module.exports = mongoose.model("NumberOfProducts", NumberOfProductsSchema);