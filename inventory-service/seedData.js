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
    // Xóa dữ liệu cũ
    await Promise.all([
      Product.deleteMany({}),
      Size.deleteMany({}),
      Color.deleteMany({}),
      NumberOfProducts.deleteMany({}),
      ProductType.deleteMany({}),
      BrandType.deleteMany({}),
      Image.deleteMany({}),
    ]);

    console.log("Seeding data...");

    // Size
    const sizes = await Size.insertMany([
      { nameSize: "S" },
      { nameSize: "M" },
      { nameSize: "L" },
      { nameSize: "XL" },
      { nameSize: "XXL" },
      { nameSize: "36" },
      { nameSize: "37" },
      { nameSize: "38" },
      { nameSize: "39" },
      { nameSize: "40" },
      { nameSize: "41" },
      { nameSize: "42" },
      { nameSize: "43" },
      { nameSize: "44" },
      { nameSize: "45" },
    ]);

    // Color
    const colors = await Color.insertMany([
      { colorName: "Red", hex: "#FF0000" },
      { colorName: "Blue", hex: "#0000FF" },
      { colorName: "Green", hex: "#00FF00" },
      { colorName: "Black", hex: "#000000" },
      { colorName: "White", hex: "#FFFFFF" },
      { colorName: "Gray", hex: "#808080" },
      { colorName: "Yellow", hex: "#FFFF00" },
      { colorName: "Pink", hex: "#FFC0CB" },
      { colorName: "Brown", hex: "#A52A2A" },
      { colorName: "Purple", hex: "#800080" },
    ]);

    // Product Type
    const productType = await ProductType.insertMany([
      {
        producTypeName: "Men Shoes",
        description: "Category for all men's shoes",
      },
      {
        producTypeName: "Women Shoes",
        description: "Category for all women's shoes",
      },
      { producTypeName: "Kids Shoes", description: "Category for kids' shoes" },
      {
        producTypeName: "Sports Shoes",
        description: "Shoes for sports and sneakers",
      },
      { producTypeName: "Casual Shoes", description: "Everyday wear shoes" },
    ]);

    // Brand Type
    const brandType = await BrandType.insertMany([
      { brandTypeName: "Nike", description: "Innovative athletic footwear" },
      { brandTypeName: "Adidas", description: "Performance & lifestyle shoes" },
      { brandTypeName: "Puma", description: "Casual and athletic shoes" },
      { brandTypeName: "NewBalance", description: "Comfortable running shoes" },
      { brandTypeName: "Reebok", description: "Training and fitness footwear" },
      { brandTypeName: "Converse", description: "Chuck Taylor sneakers" },
      { brandTypeName: "Vans", description: "Skateboarding and youth culture" },
      { brandTypeName: "UnderArmour", description: "Performance sportswear" },
      { brandTypeName: "ASICS", description: "Japanese quality running shoes" },
      { brandTypeName: "Fila", description: "Retro-style athletic shoes" },
    ]);

    // Image
    const images = await Image.insertMany([
      { imageName: "Image 1", URL: "https://picsum.photos/200?1" },
      { imageName: "Image 2", URL: "https://picsum.photos/200?2" },
      { imageName: "Image 3", URL: "https://picsum.photos/200?3" },
      { imageName: "Image 4", URL: "https://picsum.photos/200?4" },
      { imageName: "Image 5", URL: "https://picsum.photos/200?5" },
      { imageName: "Image 6", URL: "https://picsum.photos/200?6" },
    ]);

    // Tạo Inventory
    const inventory = [];
    for (let i = 0; i < 10; i++) {
      inventory.push({
        quantity: Math.floor(Math.random() * 50 + 1),
        size: sizes[Math.floor(Math.random() * sizes.length)]._id,
        color: colors[Math.floor(Math.random() * colors.length)]._id,
      });
    }

    const numberOfProducts = await NumberOfProducts.insertMany(inventory);

    // Product List
    const productList = [
      "Nike Air Max",
      "Nike Air Force 1 Shadow",
      "Adidas Ultraboost",
      "Puma RS-X",
      "Converse All Star Trekwave",
      "New Balance 550",
      "Vans Old Skool Stackform",
      "ASICS Gel-Game 9",
      "Under Armour Infinite Pro",
      "Reebok DMX Series 2K",
      "Fila UNISEX Scanline Mule",
      "Fila UNISEX Como Mule",
    ];

    const productDescriptions = {
      "Nike Air Max": "Comfortable and stylish sneakers",
      "Nike Air Force 1 Shadow": "Iconic everyday shoes",
      "Adidas Ultraboost": "High-performance running shoes",
      "Puma RS-X": "Retro-style sneakers",
      "Converse All Star Trekwave": "Classic high-top shoes",
      "New Balance 550": "Comfortable daily shoes",
      "Vans Old Skool Stackform": "Popular skate shoes",
      "ASICS Gel-Game 9": "Supportive running shoes",
      "Under Armour Infinite Pro": "Lightweight gym shoes",
      "Reebok DMX Series 2K": "Training and fitness shoes",
      "Fila UNISEX Scanline Mule": "Retro-style sneakers",
      "Fila UNISEX Como Mule": "Everyday sporty sneakers",
    };

    // Create Products
    for (let i = 0; i < productList.length; i++) {
      // Lấy số lượng ảnh ngẫu nhiên từ 1 đến 3
      const numberOfImages = Math.floor(Math.random() * 3) + 1;

      // Shuffle mảng images (để random ảnh)
      const shuffledImages = images.sort(() => 0.5 - Math.random());

      // Lấy ngẫu nhiên 1-3 ảnh đầu tiên sau khi shuffle
      const selectedImages = shuffledImages.slice(0, numberOfImages);
      const genders = ["male", "female", "unisex"];

      await Product.create({
        productName: productList[i],
        originalPrice: Math.floor(Math.random() * 2000000) + 2000000,
        description: productDescriptions[productList[i]],
        totalQuantity: numberOfProducts[i % numberOfProducts.length].quantity,
        status: true,
        discount: Math.floor(Math.random() * 20),
        gender: genders[Math.floor(Math.random() * genders.length)],
        tax: 5,
        sellingPrice: Math.floor(Math.random() * 1800000) + 1000000,
        proType: productType[i % productType.length]._id,
        braType: brandType[i % brandType.length]._id,
        //image: [{ imageID: images[i % images.length]._id }],
        // Lấy tối đa 3 ảnh
        image: selectedImages.map((img) => ({
          imageID: img._id,
        })),
        inventory: numberOfProducts.map((inv) => ({
          numberOfProduct: inv._id, // chỉ cần _id
        })),
      });
    }

    console.log("✅ Seeding completed!");
  } catch (err) {
    console.error("❌ Error seeding data:", err);
  } finally {
    mongoose.disconnect();
  }
}

seedData();
