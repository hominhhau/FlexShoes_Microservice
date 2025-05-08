const { get } = require('mongoose');
const BrandType = require('../models/BrandType');


module.exports = {
    getAllBrandTypes: async (req, res) => {
        try {
            const brandTypes = await BrandType.find();

            res.status(200).json(brandTypes);
        } catch (error) {
            console.log("Khong get duoc BrandType");
            res.status(500).json({ message: "Error when get all brand types" });
        }
    },
    getBrandTypeById: async (req, res) => {
        try {
            console.log("ID nhận được:", req.params.id);
            const brandType = await BrandType.findById(req.params.id);

            if (!brandType) {
                return res.status(404).json({ message: "Brand type not found" });
            }
            res.status(200).json(brandType);
        } catch (error) {
            console.log("Khong get duoc BrandType by id");
            res.status(500).json({ message: "Error when get brand type by id" });
        }
    },
    getBrandTypeByName: async (req, res) => {
        try {
            console.log("Name nhận được:", req.params.name);
            const brandType = await BrandType.findOne({ brandTypeName: req.params.name });
            console.log("BrandType tìm được: ", brandType);
            if (!brandType) {
                return res.status(404).json({ message: "Brand type not found" });
            }
            res.status(200).json(brandType);
        } catch (error) {
            console.log("Khong get duoc BrandType by name");
            res.status(500).json({ message: "Error when get brand type by name" });
        }
    },
    createBrandType: async (req, res) => {
        try {
            const newBrandType = new BrandType(req.body);
            await newBrandType.save();
            console.log("BrandType tao thanh cong: ", newBrandType);
            res.status(201).json(newBrandType);
        } catch (error) {
            console.log("Khong tao duoc BrandType");
            res.status(500).json({ message: "Error when create brand type" });
        }
    },
    deleteBrandType: async (req, res) => {
        try {
            const brandType = await BrandType.findByIdAndDelete(req.params.id);
            console.log("ID nhận được:", req.params.id);
            if (!brandType) {
                return res.status(404).json({ message: "Brand type not found" });
            }
            console.log("BrandType xoa thanh cong: ", brandType);
            res.status(200).json({ message: "Brand type deleted successfully" });
        } catch (error) {
            console.log("Khong xoa duoc BrandType");
            res.status(500).json({ message: "Error when delete brand type" });
        }
    }
};
