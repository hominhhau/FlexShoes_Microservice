const Image = require('../models/Image');

module.exports = {
    //Get all images
    getAllImages: async (req, res) => {
        try{
            const images = await Image.find();
            console.log("Test console Image:",images);
            res.status(200).json(images);
        }catch(error){
            console.log("Khong get duoc Image");
            res.status(500).json({message: "Error when get all images"});
        }
    },
    createImage: async (req, res) => {
        try {
            const newImage = new Image(req.body);
            const savedImage = await newImage.save();
            res.status(201).json(savedImage);
        } catch (error) {
            console.error("Error creating image:", error);
            res.status(500).json({ message: "Error creating image" });
        }
    },

};