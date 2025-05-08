const mongoose = require("mongoose");

const ProductTypeSchema = new mongoose.Schema({
  productTypeName: {
    type: String,
    required: true
  },
  description: {
    type: String
  }
});

module.exports = mongoose.model("ProductType", ProductTypeSchema);
