const { get } = require('mongoose');
const Color = require('../models/Color');

module.exports = {
    //Get all colors
    getAllColors: async (req, res) => {
        try{
            const colors = await Color.find();
            console.log("Test console Color:",colors);
            res.status(200).json(colors);
        }catch(error){
            console.log("Khong get duoc Color");
            res.status(500).json({message: "Error when get all colors"});
        }
    },
    getColorById: async (req, res) => {
        try {
            const color = await Color.findById(req.params.id);
            if (!color) {
                return res.status(404).json({ message: 'Color not found' });
            }
            res.status(200).json(color);
        } catch (error) {
            console.error('Error fetching color:', error);
            res.status(500).json({ message: 'Error fetching color' });
        }
    },
    getColorByName: async (req, res) => {
        try {
            const color = await Color.findOne({ colorName: req.params.name });
            console.log("Color name:", req.params.name);
            if (!color) {
                return res.status(404).json({ message: 'Color not found' });
            }
            res.status(200).json(color);
        } catch (error) {
            console.error('Error fetching color:', error);
            res.status(500).json({ message: 'Error fetching color' });
        }
    },
    createColor: async (req, res) => {
        try {
            const newColor = new Color(req.body);
            await newColor.save();
            console.log("New color created:", newColor);
            res.status(201).json(newColor);
        } catch (error) {
            console.error('Error creating color:', error);
            res.status(500).json({ message: 'Error creating color' });
        }
    },
    deleteColor: async (req, res) => {
        try {
            const color = await Color.findByIdAndDelete(req.params.id);
            console.log("Delete color:", color);
            if (!color) {
                return res.status(404).json({ message: 'Color not found' });
            }
            res.status(200).json({ message: 'Color deleted successfully' });
        } catch (error) {
            console.error('Error deleting color:', error);
            res.status(500).json({ message: 'Error deleting color' });
        }
    },
};