const mongoose = require("mongoose");

const NumberOfProductsSchema = new mongoose.Schema({
    quantity: {
        type: Number,
        required: true
    },
    size: {
        _id: {
            type: mongoose.Schema.Types.ObjectId, ref: 'Size'
        },
        sizeName: {
            type: String,
        }
    },
    color:{
        _id: {
            type: mongoose.Schema.Types.ObjectId, ref: 'Color'
        },
        colorName: {
            type: String,
        }
    },
});
module.exports = mongoose.model("NumberOfProducts", NumberOfProductsSchema);