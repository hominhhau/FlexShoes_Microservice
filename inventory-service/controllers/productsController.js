const mongoose = require("mongoose");
const Product = require("../models/Product");

module.exports = {
  getAllProducts: async (req, res) => {
    try {
      const products = await Product.find();
      console.log("Test console SanPham:", products);
      res.status(200).json(products);
    } catch (error) {
      console.log("Khong get duoc SP");
      res.status(500).json({ message: "Error when get all products" });
    }
  },
  getProductById: async (req, res) => {
    try {
      const productId = req.params.id;
      console.log("ID nhận được:", productId);
  
      const product = await Product.findById(productId)
        .populate({
          path: 'image.imageID',
          select: 'URL',
        })
        .populate({
          path: 'inventory',
          model: 'NumberOfProducts',
          populate: [
            {
              path: 'numberOfProduct', // Populate the numberOfProduct document itself
              populate: [ // Then populate fields within numberOfProduct
                {
                  path: 'size',
                  model: 'Size',
                  select: 'nameSize',
                },
                {
                  path: 'color',
                  model: 'Color',
                  select: 'colorName hex', // Lấy cả hex code cho màu sắc nếu cần
                },
              ],
            },
          ],
        })
        .populate('proType', 'producTypeName description') // Lấy thêm description nếu cần
        .populate('braType', 'brandTypeName description'); // Lấy thêm description nếu cần
  
      console.log("Product tìm được: ", product);
  
      if (!product) {
        return res.status(404).json({ message: "Không tìm thấy sản phẩm" });
      }
  
      res.status(200).json(product);
  
    } catch (error) {
      console.error("Lỗi khi lấy sản phẩm theo ID:", error);
      res.status(500).json({ message: "Lỗi khi lấy sản phẩm theo ID", error: error.message });
    }
  },
  getFilteredProducts: async (req, res) => {
    try {
      const { productTypeId, brandTypeId, sizeId, colorId } = req.body;
      // Chuyển tham số ID thành mongoose.ObjectId nếu chúng là chuỗi
      const productTypeObjectId = new mongoose.Types.ObjectId(productTypeId);
      const brandTypeObjectId = new mongoose.Types.ObjectId(brandTypeId);
      const sizeObjectId = new mongoose.Types.ObjectId(sizeId);
      const colorObjectId = new mongoose.Types.ObjectId(colorId);

      console.log("productTypeId:", productTypeId);
      console.log("brandTypeId:", brandTypeId);
      console.log("sizeId:", sizeId);
      console.log("colorId:", colorId);

      const result = await Product.aggregate([
        {
          $match: {
            sellingPrice: { $gte: 1000000, $lte: 3000000 },
            gender: true,
            proType: productTypeObjectId,
            braType: brandTypeObjectId,
          },
        },
        {
          $unwind: "$inventory",
        },
        {
          $lookup: {
            from: "NumberOfProducts",
            localField: "inventory.numberOfProduct",
            foreignField: "_id",
            as: "numberOfProductDetails",
          },
        },
        {
          $unwind: "$numberOfProductDetails",
        },
        {
          $lookup: {
            from: "sizes",
            localField: "numberOfProductDetails.size",
            foreignField: "_id",
            as: "sizeDetail",
          },
        },
        {
          $lookup: {
            from: "colors",
            localField: "numberOfProductDetails.color",
            foreignField: "_id",
            as: "colorDetail",
          },
        },
        {
          $unwind: "$sizeDetail",
        },
        {
          $unwind: "$colorDetail",
        },
        {
          $match: {
            "sizeDetail._id": sizeObjectId,
            "colorDetail._id": colorObjectId,
          },
        },
        {
          $group: {
            _id: "$_id",
            productName: { $first: "$productName" },
            sellingPrice: { $first: "$sellingPrice" },
            gender: { $first: "$gender" },
            braType: { $first: "$braType" },
            proType: { $first: "$proType" },
            description: { $first: "$description" },
            inventory: { $push: "$inventory" },
            image: { $first: "$image" },
          },
        },
      ]);

      // Kiểm tra nếu không có kết quả
      if (result.length === 0) {
        return res
          .status(404)
          .json({ message: "Không tìm thấy sản phẩm phù hợp" });
      }

      return res.status(200).json({
        message: "Lọc sản phẩm thành công",
        products: result,
      });
    } catch (error) {
      // Xử lý lỗi
      console.error("Lỗi khi lọc sản phẩm:", error);
      return res.status(500).json({ message: "Lỗi khi lọc sản phẩm" });
    }
  },
};
