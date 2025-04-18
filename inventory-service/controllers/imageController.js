const Image = require('../models/Image');
const { uploadFile } = require('../utils/file.service');


module.exports = {
    //Get all images
    getAllImages: async (req, res) => {
        try {
            const images = await Image.find();
            console.log("Test console Image:", images);
            res.status(200).json(images);
        } catch (error) {
            console.log("Khong get duoc Image");
            res.status(500).json({ message: "Error when get all images" });
        }
    },
    createImage: async (req, res) => {
        try {

            const { file } = req.files || {}; // Sử dụng req.files để lấy file từ form-data
            console.log("File nhận được:", file);
            if (!file) {
                return res.status(400).json({ message: "No file uploaded" });
            }
            const imageUrl = await uploadFile(file);
            console.log("Image URL:", imageUrl);
            const newImage = new Image({ URL: imageUrl, imageName: req.body.imageName });
            await newImage.save();
            res.status(201).json(newImage);
        } catch (error) {
            console.error("Error creating image:", error);
            res.status(500).json({ message: "Error creating image" });
        }
    },

};