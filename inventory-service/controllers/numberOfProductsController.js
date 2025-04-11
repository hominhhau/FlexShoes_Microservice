const NumberOfProducts = require("../models/NumberOfProducts");
const Product = require("../models/Product");
module.exports = {
  getAllNumberOfProducts: async (req, res) => {
    try {
      const numberOfProducts = await NumberOfProducts.find()
        .populate("size", "nameSize")
        .populate("color", "colorName");

      res.status(200).json(numberOfProducts);
    } catch (error) {
      console.log("Khong get duoc Number of Products");
      res
        .status(500)
        .json({ message: "Error when get all number of products" });
    }
  },
  getNumberOfProductsById: async (req, res) => {
    try {
      const numberOfProducts = await NumberOfProducts.findById(req.params.id)
        .populate("size", "nameSize")
        .populate("color", "colorName");

      if (!numberOfProducts) {
        return res
          .status(404)
          .json({ message: "Number of products not found" });
      }
      res.status(200).json(numberOfProducts);
    } catch (error) {
      console.log("Khong get duoc Number of Products by ID");
      res
        .status(500)
        .json({ message: "Error when get number of products by id" });
    }
  },

  createQuantity: async (req, res) => {
    try {
      const { product, color, size, quantity } = req.body;
      console.log("product:", product);
      console.log("color:", color);
      console.log("size:", size);
      console.log("quantity:", quantity);
      if (!product || !color || !size || quantity === undefined) {
        return res
          .status(400)
          .json({ message: "Thiếu product, color, size hoặc quantity" });
      }

      const newNumberOfProduct = new NumberOfProducts({
        product,
        color,
        size,
        quantity,
      });

      await newNumberOfProduct.save();

      res.status(201).json({
        message: "Tạo số lượng sản phẩm thành công",
        numberOfProduct: newNumberOfProduct,
      });
    } catch (error) {
      console.error("Lỗi khi tạo số lượng sản phẩm:", error);
      res.status(500).json({ message: "Lỗi khi tạo số lượng sản phẩm" });
    }
  },

  //gan inventory vao san pham
  attachInventoryToProduct: async (req, res) => {
    try {
      const { _id, inventoryIds } = req.body;
  
      const product = await Product.findById(_id);
      if (!product) {
        return res.status(404).json({ message: "Không tìm thấy sản phẩm" });
      }
  
      // Gán inventory
      product.inventory = inventoryIds.map(id => ({ numberOfProduct: id }));
      await product.save();
  
      // Populate lại inventory nếu muốn trả về đầy đủ
      const updatedProduct = await Product.findById(_id).populate("inventory.numberOfProduct");
  
      res.status(200).json({
        message: "Gắn inventory thành công",
        product: updatedProduct,
      });
    } catch (error) {
      console.error("Lỗi attachInventoryToProduct:", error);
      res.status(500).json({ message: "Lỗi", error: error.message });
    }
  }
  
};
