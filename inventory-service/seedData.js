require("dotenv").config();
const mongoose = require("mongoose");
const connectDB = require("./configs/db");

const Size = require("./models/Size");
const Color = require("./models/Color");
const NumberOfProducts = require("./models/NumberOfProducts");
const Product = require("./models/Product");
const ProductType = require("./models/ProductType");
const BrandType = require("./models/BrandType");
const Image = require("./models/Image");

connectDB();

async function seedData() {
  try {
    await Product.deleteMany({});
    await Size.deleteMany({});
    await Color.deleteMany({});
    await NumberOfProducts.deleteMany({});
    await ProductType.deleteMany({});
    await BrandType.deleteMany({});
    await Image.deleteMany({});

    console.log("Seeding data...");
    const sizes = await Size.insertMany([
      { nameSize: "39" },
      { nameSize: "40" },
      { nameSize: "41" },
    ]);
    const colors = await Color.insertMany([
      { colorName: "Đỏ" },
      { colorName: "Xanh" },
      { colorName: "Đen" },
    ]);

    // Tạo dữ liệu NumberOfProducts
    const numberOfProducts = await NumberOfProducts.insertMany([
      {
        quantity: 10,
        size: { _id: sizes[0]._id, sizeName: sizes[0].nameSize },
        color: { _id: colors[0]._id, colorName: colors[0].colorName },
      },
      {
        quantity: 5,
        size: { _id: sizes[1]._id, sizeName: sizes[1].nameSize },
        color: { _id: colors[1]._id, colorName: colors[1].colorName },
      },
    ]);

    const productType = await ProductType.create({
      producTypeName: "Giày chạy bộ",
      description: "Dành cho chạy bộ, nhẹ, bám đường tốt",
    });
    const brandType = await BrandType.create({
      brandTypeName: "Nike",
      description: "Thương hiệu thể thao hàng đầu",
    });
    const image = await Image.create({
      imageName: "Nike Air Max",
      URL: "https://picsum.photos/200",
    });
    await Product.create([
      {
        productName: "Nike Air Max 2024",
        originalPrice: 3000000,
        description: "Giày thể thao Nike cao cấp",
        status: true,
        discount: 10,
        gender: true,
        tax: 5,
        sellingPrice: 2700000,
        proType: productType._id,
        braType: brandType._id,
        image: [{ imageID: image._id }],
        inventory: numberOfProducts.map((nop) => ({
          quantity: nop.quantity,
          size: { _id: nop.size._id, sizeName: nop.size.sizeName },
          color: { _id: nop.color._id, colorName: nop.color.colorName },
        })),
      },
      {
        productName: "Nike Air Air Jordan 2023",
        originalPrice: 4000000,
        description: "Giày thể thao Nike cao cấpp",
        status: true,
        discount: 20,
        gender: true,
        tax: 3,
        sellingPrice: 2200000,
        proType: productType._id,
        braType: brandType._id,
        image: [{ imageID: image._id }],
        inventory: numberOfProducts.map((nop) => ({
          quantity: nop.quantity,
          size: { _id: nop.size._id, sizeName: nop.size.sizeName },
          color: { _id: nop.color._id, colorName: nop.color.colorName },
        })),
      },
    ]);

    console.log("Seeding completed!");
  } catch (error) {
    console.error("Error seeding data:", error);
  } finally {
    mongoose.disconnect();
  }
}
seedData();
