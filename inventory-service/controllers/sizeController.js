const { get } = require('mongoose');
const Size = require('../models/Size');

module.exports = {
    getAllSizes: async (req, res) => {
        try {
            const sizes = await Size.find();
            res.status(200).json(sizes);
        } catch (error) {
            res.status(500).json({ message: 'Error fetching sizes', error });
        }
    },
    getSizeById: async (req, res) => {
        try {
            const size = await Size.findById(req.params.id);
            if (!size) {
                return res.status(404).json({ message: 'Size not found' });
            }
            res.status(200).json(size);
        } catch (error) {
            res.status(500).json({ message: 'Error fetching size', error });
        }
    },
    getSizeByName: async (req, res) => {
        try {
            const size = await Size.findOne({ nameSize: req.params.name });
            if (!size) {
                return res.status(404).json({ message: 'Size not found' });
            }
            res.status(200).json(size);
        } catch (error) {
            res.status(500).json({ message: 'Error fetching size', error });
        }
    },
    createSize: async (req, res) => {
        try {
            const size = new Size(req.body);
            await size.save();
            res.status(201).json(size);
        } catch (error) {
            res.status(500).json({ message: 'Error creating size', error });
        }
    },
    deleteSize: async (req, res) => {
        try {
            const size = await Size.findByIdAndDelete(req.params.id);
            if (!size) {
                return res.status(404).json({ message: 'Size not found' });
            }
            res.status(200).json({ message: 'Size deleted successfully' });
        } catch (error) {
            res.status(500).json({ message: 'Error deleting size', error });
        }
    }

};