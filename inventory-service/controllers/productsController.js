const Product = require("../models/Product");


module.exports = {
    getAllProducts: async (req, res) => {
        try{
            const products = await Product.find();
            console.log("Test console SanPham:",products);
            res.status(200).json(products);
        }catch(error){
            console.log("Khong get duoc SP");
            res.status(500).json({message: "Error when get all products"});
        }
    },
    getProductById: async (req, res) => {
        try{
            console.log("ID nhận được:",req.params.id);
            const product = await Product.findById(req.params.id);
            console.log("Product tìm được: ",product);
            if(!product){
                return res.status(404).json({message: "Product not found"});
            }
            res.status(200).json(product);
        }catch(error){
            console.log("Khong get duoc SP");
            res.status(500).json({message: "Error when get product by id"});
        }
    },
};