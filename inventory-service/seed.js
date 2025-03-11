console.log("Chạy seed.js");
const mongoose = require("mongoose");
const connectDB = require("./configs/db");
const BrandType = require("./models/BrandType");
const Color = require("./models/Color");
const Image = require("./models/Image");
const Size = require("./models/Size");
const NumberOfProducts = require("./models/NumberOfProducts");
const ProductType = require("./models/ProductType");
const Product = require("./models/Product");
require("dotenv").config();

const brandTypes = [
  { brandTypeName: "Thể thao", description: "Dòng giày chuyên dụng cho thể thao." },
  { brandTypeName: "Sneaker", description: "Dòng giày thời trang phổ biến." },
  { brandTypeName: "Boot", description: "Dòng giày bền bỉ và cá tính." },
  { brandTypeName: "Sandal", description: "Dòng giày nhẹ nhàng, thoáng mát." },
];
const colors = [
  { colorName: "Đen" },
  { colorName: "Trắng" },
  { colorName: "Xám" },
  { colorName: "Xanh" },
  { colorName: "Đỏ" },
  { colorName: "Vàng" },
  { colorName: "Cam" },
  { colorName: "Hồng" },
  { colorName: "Tím" },
  { colorName: "Nâu" },
];
const images = [
  {imageName: "Nike Air Max", URL: "https://picsum.photos/200"},
  {imageName: "Adidas UltraBoost", URL: "https://picsum.photos/200"},
  {imageName: "Puma RS-X", URL: "https://picsum.photos/200"},
  {imageName: "Vans Old Skool", URL: "https://picsum.photos/200"},
  {imageName: "Converse Chuck Taylor", URL: "https://picsum.photos/200"}
];
const sizeData = [
  { nameSize: "36" },
  { nameSize: "37" },
  { nameSize: "38" },
  { nameSize: "39" },
  { nameSize: "40" },
  { nameSize: "41" },
  { nameSize: "42" },
  { nameSize: "43" },
  { nameSize: "44" }
];
const productTypesData = [
  { producTypeName: "Sneakers", description: "Comfortable sports shoes" },
  { producTypeName: "Formal", description: "Elegant office wear shoes" },
  { producTypeName: "Boots", description: "Sturdy and stylish boots" }
];

const seedData = async () => {
  try {
    await connectDB(); 
    console.log("Kết nối MongoDB thành công!");

    // Xóa dữ liệu cũ trước khi thêm mới (tuỳ chọn)
    //await BrandType.deleteMany({});
    //await Color.deleteMany({});
    //await Image.deleteMany({});
    //await Size.deleteMany({});
    //await NumberOfProducts.deleteMany({})
    //await ProductType.deleteMany({});
    await Product.deleteMany({});
    console.log("Đã xóa dữ liệu cũ.");

    // Thêm dữ liệu mới
    //await BrandType.insertMany(brandTypes);
    //await Color.insertMany(colors);
    //await Image.insertMany(images);
    //await Size.insertMany(sizeData);


    //ID cua size va color tu db 
  //   const size = await Size.findOne({nameSize: "36"});
  //   const color = await Color.findOne({colorName: "Đen"});
  //   if(!size || !color){
  //     console.log("Khong tim thay size hoac color");
  //     mongoose.connection.close();
  //     return;
  //   }


  
  //   const numberOfProductsData = [
  //     {
  //         quantity: 100,
  //         size: {
  //             _id: size._id,
  //             sizeName: size.nameSize
  //         },
  //         color: {
  //             _id: color._id,
  //             colorName: color.colorName
  //         }
  //     },
  //     {
  //         quantity: 50,
  //         size: {
  //             _id: size._id,
  //             sizeName: size.nameSize
  //         },
  //         color: {
  //             _id: color._id,
  //             colorName: color.colorName
  //         }
  //     }
  // ];
  // await NumberOfProducts.insertMany(numberOfProductsData);

    //await ProductType.insertMany(productTypesData);

    const proType = await ProductType.findOne({producTypeName: "Sneakers"});
    const braType = await BrandType
    .findOne({brandTypeName: "Sneaker"});
    const size = await Size.findOne({nameSize: "36"});
    const color = await Color.findOne({colorName: "Đen"});

    if(!proType || !braType || !size || !color){
      console.log("Khong tim thay product type hoac brand type hoac size hoac color");
      mongoose.connection.close();
      return;
    }

    const image = await Image.create({imageName: "Nike Air Max", URL: "https://picsum.photos/200"});

    const inventoryItem = await NumberOfProducts.create({
      quantity: 40,
      size: {
        _id: size._id,
        sizeName: size.nameSize
      },
      color: {
        _id: color._id,
        colorName: color.colorName
      }
    });

    const productData = {
      productName: "Nike Air Max",
      originalPrice: 1200000,
      description: "Comfortable sports shoes from Nike",
      status: true,
      discount: 10,
      totalQuantity: 50,
      gender: true, // true = Nam, false = Nữ
      tax: 5,
      sellingPrice: 1100000,
      proType: proType._id,
      braType: braType._id,
      image: [{ _id: image._id }], // Chỉ lưu ObjectId của hình ảnh
      inventory: [{
          // quantity: inventoryItem._id,
          quantity: inventoryItem._id,
          size: inventoryItem.size,
          color: inventoryItem.color
      }]
  };

    await Product.create(productData);
    console.log("Dữ liệu đã được thêm thành công!");

    mongoose.connection.close();
    console.log("Đóng kết nối MongoDB.");
  } catch (error) {
    console.error("Lỗi khi seed dữ liệu:", error);
    mongoose.connection.close();
  }
};

seedData();