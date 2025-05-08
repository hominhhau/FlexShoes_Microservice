const mongoose = require("mongoose");

const ColorSchema = new mongoose.Schema({
    colorName: {
        type: String,
        required: true,
    },
    hex:{
        type: String,
        required: true,
    }
});
module.exports = mongoose.model("Color", ColorSchema);