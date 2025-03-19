const mongoose = require("mongoose");

const ProductTypeSchema = new mongoose.Schema({
  producTypeName: { 
    type: String, 
    required: true 
},
  description: { 
    type: String 
}
});

module.exports = mongoose.model("ProductType", ProductTypeSchema);
