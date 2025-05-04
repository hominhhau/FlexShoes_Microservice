const mongoose = require("mongoose");
const Product = require("../models/Product");
const NumberOfProducts = require("../models/NumberOfProducts");
const Image = require("../models/Image");
const { uploadFile } = require("../utils/file.service");

module.exports = {
  getAllProducts: async (req, res) => {
    try {
      const products = await Product.find()
        .populate({
          path: "image.imageID",
          model: "Image",
          select: "URL",
        })
        .populate({
          path: "inventory",
          model: "NumberOfProducts",
          populate: [
            {
              path: "numberOfProduct", // Populate the numberOfProduct document itself
              populate: [
                // Then populate fields within numberOfProduct
                {
                  path: "size",
                  model: "Size",
                  select: "nameSize",
                },
                {
                  path: "color",
                  model: "Color",
                  select: "colorName hex", // Lấy cả hex code cho màu sắc nếu cần
                },
              ],
            },
          ],
        });

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
          path: "image.imageID",
          select: "URL",
        })
        .populate({
          path: "inventory",
          model: "NumberOfProducts",
          populate: [
            {
              path: "numberOfProduct", // Populate the numberOfProduct document itself
              populate: [
                // Then populate fields within numberOfProduct
                {
                  path: "size",
                  model: "Size",
                  select: "nameSize",
                },
                {
                  path: "color",
                  model: "Color",
                  select: "colorName hex", // Lấy cả hex code cho màu sắc nếu cần
                },
              ],
            },
          ],
        })
        .populate("proType", "producTypeName description") // Lấy thêm description nếu cần
        .populate("braType", "brandTypeName description"); // Lấy thêm description nếu cần

      console.log("Product tìm được: ", product);

      if (!product) {
        return res.status(404).json({ message: "Không tìm thấy sản phẩm" });
      }

      res.status(200).json(product);
    } catch (error) {
      console.error("Lỗi khi lấy sản phẩm theo ID:", error);
      res
        .status(500)
        .json({
          message: "Lỗi khi lấy sản phẩm theo ID",
          error: error.message,
        });
    }
  },

  createProduct: async (req, res) => {
    try {
      if (!req.files || req.files.length === 0) {
        return res.status(400).json({ message: "No images uploaded" });
      }

      const {
        productName,
        description,
        originalPrice,
        discount,
        vat,
        status,
        gender,
        brandId,
        categoryId,
        inventory,
      } = req.body;

      // Upload ảnh lên S3 và tạo document Image
      const imageUploadPromises = req.files.map(async (file) => {
        console.log("Uploading file:", file.originalname);
        const url = await uploadFile(file); // Upload lên S3
        const imageDoc = new Image({
          imageName: file.originalname, // Dùng originalname làm imageName
          URL: url, // Gán URL từ S3
        });
        await imageDoc.save();
        return { imageID: imageDoc._id }; // Trả về object chứa imageID
      });
      const imageDocs = await Promise.all(imageUploadPromises);
      console.log("Created Image documents:", imageDocs);

      // Parse inventory từ chuỗi JSON
      let parsedInventory;
      try {
        parsedInventory = JSON.parse(inventory);
      } catch (error) {
        return res
          .status(400)
          .json({ message: "Invalid inventory format", error: error.message });
      }

      // Tạo document NumberOfProducts
      let totalQuantity = 0; // Initialize totalQuantity

      const inventoryPromises = parsedInventory.map(async (item) => {
        totalQuantity += parseInt(item.quantity, 10); // Accumulate total quantity
        const numberOfProduct = new NumberOfProducts({
          quantity: item.quantity,
          size: item.size,
          color: item.color,
        });
        await numberOfProduct.save();
        return { numberOfProduct: numberOfProduct._id }; // Return object containing numberOfProduct
      });
      const inventoryDocs = await Promise.all(inventoryPromises);
      console.log("Created NumberOfProducts documents:", inventoryDocs);
      // giá gốc -(giá gốc*giảm giá/100) + giá trị VAT trên giá gốc
      const discountPercentage =
        parseFloat(originalPrice) -
        (parseFloat(originalPrice) * parseFloat(discount)) / 100 +
        parseFloat(vat || 0);

      // Tạo Product với image và inventory đã có
      const newProduct = new Product({
        productName,
        description,
        originalPrice: parseFloat(originalPrice),
        sellingPrice: discountPercentage,
        status: status, // Chuyển đổi để khớp schema
        gender,
        discount: parseFloat(discount),
        braType: brandId,
        proType: categoryId,
        image: imageDocs, // Gán mảng imageID
        inventory: inventoryDocs, // Gán mảng numberOfProduct
        totalQuantity: totalQuantity, // Gán tổng số lượng sản phẩm
        tax: parseFloat(vat || 0), // Gán giá trị VAT từ req.body
      });

      console.log("newProduct:", newProduct);

      // Lưu Product vào database
      const savedProduct = await newProduct.save();

      res.status(201).json({
        message: "Product created successfully",
        product: savedProduct,
      });
    } catch (error) {
      console.error("Error creating product:", error);
      res
        .status(500)
        .json({ message: "Error creating product", error: error.message });
    }
  },
};
