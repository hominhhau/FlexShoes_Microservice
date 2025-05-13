const mongoose = require("mongoose");
const Product = require("../models/Product");
const NumberOfProducts = require("../models/NumberOfProducts");
const Image = require("../models/Image");
const { uploadFile } = require("../utils/file.service");
const Color = require("../models/Color");
const Size = require("../models/Size");

module.exports = {
  getAllProducts: async (req, res) => {
    try {
      const products = await Product.find().populate("image.imageID");

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
        .populate("proType", "productTypeName description") // Lấy thêm description nếu cần
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
  update: async (req, res) => {
    try {
      const {
        productId,
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
        oldImageIds,
      } = req.body;

      if (!productId) {
        return res.status(400).json({ message: "Missing productId" });
      }

      const product = await Product.findById(productId);
      if (!product) {
        return res.status(404).json({ message: "Product not found" });
      }

      // --- Handle images ---
      let updatedImageList = [];

      // Giữ lại ảnh cũ không bị xoá
      const oldImageIdArray = JSON.parse(oldImageIds || "[]");
      updatedImageList = product.image.filter((img) =>
        oldImageIdArray.includes(img.imageID.toString())
      );

      // Xóa ảnh không nằm trong oldImageIds
      const imagesToRemove = product.image.filter(
        (img) => !oldImageIdArray.includes(img.imageID.toString())
      );
      const removedImageIds = imagesToRemove.map((img) => img.imageID);
      await Image.deleteMany({ _id: { $in: removedImageIds } });

      // Upload ảnh mới
      if (req.files && req.files.length > 0) {
        const newImages = await Promise.all(
          req.files.map(async (file) => {
            const url = await uploadFile(file);
            const imageDoc = new Image({
              imageName: file.originalname,
              URL: url,
            });
            await imageDoc.save();
            return { imageID: imageDoc._id };
          })
        );
        updatedImageList = [...updatedImageList, ...newImages];
      }

      // --- Handle inventory ---
      const parsedInventory = JSON.parse(inventory);
      let totalQuantity = 0;
      const updatedInventoryList = await Promise.all(
        parsedInventory.map(async (item) => {
          const inv = item.numberOfProduct;
          totalQuantity += parseInt(inv.quantity);

          if (inv._id) {
            // Update existing inventory item
            await NumberOfProducts.findByIdAndUpdate(inv._id, {
              quantity: inv.quantity,
              size: inv.size._id || inv.size,
              color: inv.color._id || inv.color,
            });
            return { numberOfProduct: inv._id };
          } else {
            // Create new inventory item
            const newInv = new NumberOfProducts({
              quantity: inv.quantity,
              size: inv.size._id || inv.size,
              color: inv.color._id || inv.color,
            });
            await newInv.save();
            return { numberOfProduct: newInv._id };
          }
        })
      );

      // --- Update product ---
      const discountPrice =
        parseFloat(originalPrice) -
        (parseFloat(originalPrice) * parseFloat(discount)) / 100;
      const finalSellingPrice = discountPrice + parseFloat(vat || 0);

      product.productName = productName;
      product.description = description;
      product.originalPrice = parseFloat(originalPrice);
      product.sellingPrice = finalSellingPrice;
      product.status = status;
      product.gender = gender;
      product.discount = parseFloat(discount);
      product.tax = parseFloat(vat);
      product.braType = brandId;
      product.proType = categoryId;
      product.image = updatedImageList;
      product.inventory = updatedInventoryList;
      product.totalQuantity = totalQuantity;

      console.log("Updated product:", product);

      await product.save();

      res
        .status(200)
        .json({ message: "Product updated successfully", product });
    } catch (error) {
      console.error("Error updating product:", error);
      res
        .status(500)
        .json({ message: "Error updating product", error: error.message });
    }
  },

// purchase: async (req, res) => {
//   const { items } = req.body;
//   console.log('Dữ liệu nhận được từ frontend:', req.body);

//   try {
//     //Duyet qua tung sp
//     for (let item of items) {
//       const { productId, colorName, sizeName, quantity } = item;

//       const product = await Product.findById(productId).populate("inventory.numberOfProduct");
//       if (!product)
//         return res.status(404).json({ message: "Product not found" });

//       //find ObjectId của color và size
//       const color = await Color.findOne({ colorName: colorName });
//       const size = await Size.findOne({ nameSize: sizeName });

//       if (!color || !size) {
//         return res.status(400).json({ message: `Không tìm thấy màu hoặc size: ${colorName}, ${sizeName}` });
//       }

//       const colorId = color._id;
//       const sizeId = size._id;

//       //find item trong inventory khop mau va size
//       const inventoryItem = product.inventory.find(i =>
//         i.numberOfProduct.color.toString() === colorId.toString() &&
//         i.numberOfProduct.size.toString() === sizeId.toString()
//       );

//       if (!inventoryItem) {
//         return res.status(400).json({
//           message: `Không tìm thấy sản phẩm với size ${sizeName} và màu ${colorName}`,
//         });
//       }

//       if (inventoryItem.numberOfProduct.quantity < quantity) {
//         return res.status(400).json({
//           message: `Không đủ hàng cho size ${sizeName}, màu ${colorName}`,
//         });
//       }

//       //tru sl ton kho
//       inventoryItem.numberOfProduct.quantity -= quantity;
//       await inventoryItem.numberOfProduct.save();
//     }

//     //update sl ton kho
//     for (let item of items) {
//       const product = await Product.findById(item.productId).populate("inventory.numberOfProduct");
//       product.totalQuantity = product.inventory.reduce((sum, i) => {
//         return sum + (i.numberOfProduct?.quantity || 0);
//       }, 0);
//       await product.save();
//     }


//     res.json({ message: "Thành công", updated: true });
//   } catch (err) {
//     console.error("Chi tiết lỗi:", err);
//     res.status(500).json({ message: "Có lỗi xảy ra khi xử lý đơn hàng" });
//   }
// },

purchase: async (req, res) => {
  const { items } = req.body;
  console.log('Dữ liệu nhận được từ frontend:', req.body);

  if (!items || !Array.isArray(items) || items.length === 0) {
    return res.status(400).json({ message: "Danh sách sản phẩm không hợp lệ" });
  }

  const session = await mongoose.startSession();
  session.startTransaction();
  try {
    const updatedProducts = new Set();
    for (let item of items) {
      const { productId, colorName, sizeName, quantity } = item;
      if (!productId || !colorName || !sizeName || !quantity) {
        throw new Error("Dữ liệu sản phẩm không đầy đủ");
      }

      const product = await Product.findById(productId)
        .populate("inventory.numberOfProduct")
        .session(session);
      if (!product) {
        throw new Error("Product not found");
      }

      const color = await Color.findOne({ colorName: { $regex: `^${colorName}$`, $options: 'i' } }).session(session);
      const size = await Size.findOne({ nameSize: { $regex: `^${sizeName}$`, $options: 'i' } }).session(session);
      if (!color || !size) {
        throw new Error(`Không tìm thấy màu hoặc size: ${colorName}, ${sizeName}`);
      }

      const inventoryItem = product.inventory.find(i =>
        i.numberOfProduct.color.toString() === color._id.toString() &&
        i.numberOfProduct.size.toString() === size._id.toString()
      );
      if (!inventoryItem) {
        throw new Error(`Không tìm thấy sản phẩm với size ${sizeName} và màu ${colorName}`);
      }

      if (inventoryItem.numberOfProduct.quantity < quantity) {
        throw new Error(`Không đủ hàng cho size ${sizeName}, màu ${colorName}`);
      }

      inventoryItem.numberOfProduct.quantity -= quantity;
      await inventoryItem.numberOfProduct.save({ session });

      if (!updatedProducts.has(productId)) {
        product.totalQuantity = product.inventory.reduce((sum, i) => {
          return sum + (i.numberOfProduct?.quantity || 0);
        }, 0);
        await product.save({ session });
        updatedProducts.add(productId);
      }
    }

    await session.commitTransaction();
    res.json({ message: "Thành công", updated: true });
  } catch (err) {
    await session.abortTransaction();
    console.error("Chi tiết lỗi:", err);
    res.status(500).json({ message: "Có lỗi xảy ra khi xử lý đơn hàng", error: err.message });
  } finally {
    session.endSession();
  }
}

};

