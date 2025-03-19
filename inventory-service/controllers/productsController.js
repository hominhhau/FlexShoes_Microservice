const Product = require("../models/Product");

//All products
const getAllProducts = async (req, res) => {
    try{
        const products = await Product.find();
        console.log("Test console SanPham:",products);
        res.status(200).json(products);
    }catch(error){
        console.log("Khong get duoc SP");
        res.status(500).json({message: "Error when get all products"});
    }
};

module.exports = {
    getAllProducts
};