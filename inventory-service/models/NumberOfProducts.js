const mongoose = require("mongoose");

const NumberOfProductsSchema = new mongoose.Schema({
    quantity: {
        type: Number,
        required: true
    },
    size: {
        // _id: {
        //     type: mongoose.Schema.Types.ObjectId, ref: 'Size'
        // },
        // sizeName: {
        //     type: String,
        // }
        type: mongoose.Schema.Types.ObjectId, ref: 'Size'
    },
    color:{
        // _id: {
        //     type: mongoose.Schema.Types.ObjectId, ref: 'Color'
        // },
        // colorName: {
        //     type: String,
        // }
        type: mongoose.Schema.Types.ObjectId, ref: 'Color'
    },
});
module.exports = mongoose.model("NumberOfProducts", NumberOfProductsSchema);