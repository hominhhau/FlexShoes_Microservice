const mongoose = require("mongoose");
const Product = require("../models/Product");
const Image = require("../models/Image");
const NumberOfProducts = require("../models/NumberOfProducts");

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
      console.log("ID nhận được:", req.params.id);
      const product = await Product.findById(req.params.id);
      console.log("Product tìm được: ", product);
      if (!product) {
        return res.status(404).json({ message: "Product not found" });
      }
      res.status(200).json(product);
    } catch (error) {
      console.log("Khong get duoc SP");
      res.status(500).json({ message: "Error when get product by id" });
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
  createProduct: async (req, res) => {
    try {
      console.log("📥 Dữ liệu nhận được từ client:", req.body);
  
      const {
        productName,
        description,
        originalPrice,
        status,
        sellingPrice,
        vat,
        gender,
        images,
        brand,
        productCategory,
      } = req.body;
  
      if (
        productName === undefined ||
        originalPrice === undefined ||
        sellingPrice === undefined ||
        gender === undefined ||
        !productCategory?.categoryId ||
        !brand?.brandId
      ) {
        return res.status(400).json({ message: "Thiếu thông tin bắt buộc để tạo sản phẩm." });
      }
  
      // //Lấy danh sách inventory
      // const allInventories = await NumberOfProducts.find();
      // if (allInventories.length === 0) {
      //   return res.status(400).json({ message: "Không có dữ liệu inventory (NumberOfProducts) nào." });
      // }
  
      //Lấy hình ảnh ngẫu nhiên từ DB
      const allImages = await Image.find();
      if (!allImages.length) {
        return res.status(400).json({ message: "Không có hình ảnh nào trong database." });
      }
  
      const numberOfImages = Math.floor(Math.random() * 3) + 1;
      const selectedImages = [];
      const usedIndexes = new Set();
  
      while (selectedImages.length < numberOfImages) {
        const index = Math.floor(Math.random() * allImages.length);
        if (!usedIndexes.has(index)) {
          usedIndexes.add(index);
          selectedImages.push({ imageID: allImages[index]._id });
        }
      }
  
      // ✅ Gán inventory
      // const inventory = allInventories.map(inv => ({
      //   numberOfProduct: inv._id
      // }));
  
      // ✅ Tính tổng số lượng tồn
      //const totalQuantity = allInventories.reduce((sum, item) => sum + (item.quantity || 0), 0);
  
      // ✅ Tạo sản phẩm mới (bỏ các field bạn không truyền từ client)
      const newProduct = new Product({
        productName,
        description,
        originalPrice,
        status,
        sellingPrice,
        vat,
        gender,
        image: selectedImages,
        //inventory,
        // totalQuantity,
        brand,
        productCategory
      });
  
      await newProduct.save();

       // Sau khi tạo xong, lấy danh sách inventory liên quan tới sản phẩm này
    const relatedInventories = await NumberOfProducts.find({ product: newProduct._id });

    const inventory = relatedInventories.map(inv => ({
      numberOfProduct: inv._id
    }));

    const totalQuantity = relatedInventories.reduce((sum, item) => sum + (item.quantity || 0), 0);

    // Gán vào sản phẩm rồi lưu lại
    newProduct.inventory = inventory;
    newProduct.totalQuantity = totalQuantity;
    await newProduct.save();
  
      res.status(201).json({
        message: "Tạo sản phẩm thành công",
        product: newProduct
      });
  
    } catch (error) {
      console.error("Lỗi khi tạo sản phẩm:", error.message);
      console.error("stack:", error.stack);
      res.status(500).json({
        message: "Lỗi khi tạo sản phẩm",
        error: error.message
      });
    }
  },
  
};
