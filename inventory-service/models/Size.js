const mongoose = require("mongoose");

const SizeSchema = new mongoose.Schema({
  nameSize: { 
    type: String, 
    required: true }
});

module.exports = mongoose.model("Size", SizeSchema);
