const { get } = require('mongoose');
const ProductType = require('../models/ProductType');

module.exports = {
    getAllProductTypes: async (req, res) => {
        try {
            const productTypes = await ProductType.find();
            console.log("Test console ProductType:", productTypes);
            res.status(200).json(productTypes);
        } catch (error) {
            console.log("Khong get duoc ProductType");
            res.status(500).json({ message: "Error when get all product types" });
        }
    },
    getProductTypeById: async (req, res) => {
        try {
            const productType = await ProductType.findById(req.params.id);
            if (!productType) {
                return res.status(404).json({ message: "Product type not found" });
            }
            res.status(200).json(productType);
        } catch (error) {
            console.log("Khong get duoc ProductType by id");
            res.status(500).json({ message: "Error when get product type by id" });
        }
    },
    getProductTypeByName: async (req, res) => {
        try{
            const productType = await ProductType.findOne({productTypeName: req.params.name});
            if(!productType){
                return res.status(404).json({message: "Product type not found"});
            }
            res.status(200).json(productType);
        }catch(error){
            console.log("Khong get duoc ProductType by name");
            res.status(500).json({message: "Error when get product type by name"});
        }
    },
    createProductType: async (req, res) => {
        try {
            const newProductType = new ProductType(req.body);
            await newProductType.save();
            res.status(201).json(newProductType);
        } catch (error) {
            console.log("Khong tao duoc ProductType");
            res.status(500).json({ message: "Error when create product type" });
        }
    },
    deleteProductType: async (req, res) => {
        try {
            const productType = await ProductType.findByIdAndDelete(req.params.id);
            if (!productType) {
                return res.status(404).json({ message: "Product type not found" });
            }
            res.status(200).json({ message: "Product type deleted successfully" });
        } catch (error) {
            console.log("Khong xoa duoc ProductType");
            res.status(500).json({ message: "Error when delete product type" });
        }
    },
};