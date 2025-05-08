const Product = require('../models/Product');
const BrandType = require('../models/BrandType');
const Size = require('../models/Size');
const Color = require('../models/Color');
const NumberOfProducts = require('../models/NumberOfProducts');
const ProductType = require('../models/ProductType');

module.exports = {
    filterProductsByCriteria: async (req, res) => {
        const { colors, sizes, brands, category, genders, minPrice, maxPrice } = req.query;
        console.log('Query Params:', req.query);

        let query = { status: 'Available' }; // Adjusted to match schema default

        try {
            // Filter by brand
            if (brands) {
                const brandNames = brands.split(',');
                const brandTypes = await BrandType.find({ brandTypeName: { $in: brandNames } });
                const brandTypeIds = brandTypes.map(bt => bt._id);
                if (brandTypeIds.length) {
                    query.braType = { $in: brandTypeIds };
                } else {
                    return res.status(200).json([]);
                }
            }

            // Filter by category
            if (category) {
                const categoryNames = category.split(',');
                const productTypes = await ProductType.find({ productTypeName: { $in: categoryNames } });
                const productTypeIds = productTypes.map(pt => pt._id);
                console.log('Product Type IDs:', productTypeIds);
                if (productTypeIds.length) {
                    query.proType = { $in: productTypeIds };
                } else {
                    return res.status(200).json([]);
                }
            }

            // Filter by gender
            if (genders) {
                const genderList = genders.split(',').map(g => g.trim().toUpperCase());
                const validGenders = ['MEN', 'WOMEN', 'UNISEX'];
                const filteredGenders = genderList.filter(g => validGenders.includes(g));

                if (filteredGenders.length > 0) {
                    query.gender = { $in: filteredGenders };
                } else {
                    return res.status(200).json([]); // No valid genders provided
                }
            }

            // Filter by price
            if (minPrice || maxPrice) {
                query.sellingPrice = {};
                if (minPrice) query.sellingPrice.$gte = parseFloat(minPrice);
                if (maxPrice) query.sellingPrice.$lte = parseFloat(maxPrice);
            }

            // Filter by size
            let sizeConditions = [];
            if (sizes) {
                const sizeList = sizes.split(',');
                const sizeDocs = await Size.find({ nameSize: { $in: sizeList } });
                sizeConditions = sizeDocs.map(s => s._id);
            }

            // Filter by color
            let colorConditions = [];
            if (colors) {
                const colorList = colors.split(',');
                const colorDocs = await Color.find({ colorName: { $in: colorList } });
                colorConditions = colorDocs.map(c => c._id);
            }

            // Query NumberOfProducts that match size/color
            if (sizeConditions.length > 0 || colorConditions.length > 0) {
                const inventoryQuery = {};
                if (sizeConditions.length) inventoryQuery.size = { $in: sizeConditions };
                if (colorConditions.length) inventoryQuery.color = { $in: colorConditions };

                // Check for available stock in NumberOfProducts
                const matchedNumbers = await NumberOfProducts.find(inventoryQuery).distinct('_id');
                if (matchedNumbers.length > 0) {
                    const matchedProducts = await Product.find({
                        'inventory.numberOfProduct': { $in: matchedNumbers }
                    }).distinct('_id'); // Ensure unique product IDs

                    if (matchedProducts.length > 0) {
                        query._id = { $in: matchedProducts };
                    } else {
                        return res.status(200).json([]); // No products found
                    }
                } else {
                    return res.status(200).json([]); // No stock available
                }
            }

            console.log('Final Query:', query);

            // Fetch products and ensure no duplicates by using distinct
            const products = await Product.find(query)
                .distinct('_id') // Ensure unique products
                .then(async (productIds) => {
                    // Fetch full product details for the unique IDs
                    return await Product.find({ _id: { $in: productIds } })
                        .populate('proType')
                        .populate('braType')
                        .populate({
                            path: 'inventory.numberOfProduct',
                            populate: [
                                { path: 'size' },
                                { path: 'color' }
                            ]
                        })
                        .populate('image.imageID');
                });

            return res.status(200).json(products);
        } catch (error) {
            console.error('Error in filterProductsByCriteria:', error);
            res.status(500).json({ message: error.message });
        }
    }
};