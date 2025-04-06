const NumberOfProducts = require('../models/NumberOfProducts');

module.exports = {
    getAllNumberOfProducts: async (req, res) => {
        try{
            const numberOfProducts = await NumberOfProducts.find()
            .populate('size', 'nameSize')
            .populate('color', 'colorName');

            res.status(200).json(numberOfProducts);
        }catch(error){
            console.log("Khong get duoc Number of Products");
            res.status(500).json({message: "Error when get all number of products"});
        }
    },
    getNumberOfProductsById: async (req, res) => {
        try{
            const numberOfProducts = await NumberOfProducts.findById(req.params.id)
            .populate('size', 'nameSize')
            .populate('color', 'colorName');

            if(!numberOfProducts){
                return res.status(404).json({message: "Number of products not found"});
            }
            res.status(200).json(numberOfProducts);
        }catch(error){
            console.log("Khong get duoc Number of Products by ID");
            res.status(500).json({message: "Error when get number of products by id"});
        }
    },

};