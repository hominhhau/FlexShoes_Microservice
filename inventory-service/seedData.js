// path=seed.js
const mongoose = require('mongoose');
const BrandType = require('./models/BrandType');
const Color = require('./models/Color');
const Size = require('./models/Size');
const Product = require('./models/Product');
const Image = require('./models/Image');
const NumberOfProducts = require('./models/NumberOfProducts');

const seedData = async () => {
  try {
    // Kết nối đến MongoDB
    await mongoose.connect('mongodb://localhost:27017/inventoryDB');

    // Xóa dữ liệu cũ
    await BrandType.deleteMany({});
    await Color.deleteMany({});
    await Size.deleteMany({});
    await Product.deleteMany({});
    await Image.deleteMany({});
    await NumberOfProducts.deleteMany({});

    // Dữ liệu cho BrandType
    const brands = await BrandType.insertMany([
      { brandTypeName: 'Nike', description: 'Leading global brand known for innovative athletic footwear' },
      { brandTypeName: 'Adidas', description: 'Popular sportswear brand with a focus on performance and lifestyle shoes' },
      { brandTypeName: 'Puma', description: 'International brand with a variety of casual and athletic shoes' },
      { brandTypeName: 'NewBalance', description: 'Brand specializing in running and casual footwear with superior comfort' },
      { brandTypeName: 'Reebok', description: 'Iconic sports brand known for its fitness and training footwear' },
      { brandTypeName: 'Converse', description: 'Classic American brand famous for its Chuck Taylor All-Star sneakers' },
      { brandTypeName: 'Vans', description: 'Skateboarding brand with a strong presence in youth culture' },
      { brandTypeName: 'UnderArmour', description: 'Athletic brand focused on performance and innovation in sportswear' },
      { brandTypeName: 'ASICS', description: 'Japanese brand recognized for high-quality running and athletic shoes' },
      { brandTypeName: 'Fila', description: 'Global sportswear brand with a blend of casual and athletic styles' }
    ]);

    // Dữ liệu cho Color
    const colors = await Color.insertMany([
      { colorName: 'Red', hex: '#FF0000' },
      { colorName: 'Blue', hex: '#0000FF' },
      { colorName: 'Green', hex: '#008000' },
      { colorName: 'Black', hex: '#000000' },
      { colorName: 'White', hex: '#FFFFFF' },
      { colorName: 'Gray', hex: '#808080' },
      { colorName: 'Yellow', hex: '#FFFF00' },
      { colorName: 'Pink', hex: '#FFC0CB' },
      { colorName: 'Brown', hex: '#A52A2A' },
      { colorName: 'Purple', hex: '#800080' }
    ]);

    // Dữ liệu cho Size
    const sizes = await Size.insertMany([
      { nameSize: 'S' },
      { nameSize: 'M' },
      { nameSize: 'L' },
      { nameSize: 'XL' },
      { nameSize: 'XXL' },
      { nameSize: '36' },
      { nameSize: '37' },
      { nameSize: '38' },
      { nameSize: '39' },
      { nameSize: '40' },
      { nameSize: '41' },
      { nameSize: '42' },
      { nameSize: '43' },
      { nameSize: '44' },
      { nameSize: '45' }
    ]);

    // Dữ liệu cho Image
    const images = await Image.insertMany([
      { imageName: 'nike-air-max-trang-xanh-1.png', URL: 'https://picsum.photos/seed/picsum/200/300' },
      { imageName: 'nike-air-max-trang-xanh-2.png', URL: 'https://picsum.photos/seed/picsum/200/300' },
      { imageName: 'adidas-ultraboost01-trang-1.png', URL: 'https://picsum.photos/seed/picsum/200/300' },
      { imageName: 'adidas-ultraboost01-trang-2.png', URL: 'https://picsum.photos/seed/picsum/200/300' }
    ]);

    // Dữ liệu cho NumberOfProducts
    const quantities = await NumberOfProducts.insertMany([
      { quantity: 50, size: sizes[0]._id, color: colors[1]._id }, // S, Blue
      { quantity: 50, size: sizes[1]._id, color: colors[0]._id }  // M, Red
      // Thêm các số lượng khác tương tự
    ]);

    // Dữ liệu cho Product
    const products = await Product.insertMany([
      {
        productName: 'Nike Air Max',
        description: 'Comfortable and stylish sneakers',
        originalPrice: 120.00,
        status: true,
        discount: 5.00,
        totalQuantity: 50,
        gender: true,
        tax: 10.0,
        sellingPrice: 115.00,
        proType: brands[0]._id, // ID của Nike
        image: {
          imageID: images[0]._id // Chỉ lưu ObjectId của hình ảnh
        },
        inventory: [
          { numberOfProduct: quantities[0]._id }, // S, Blue
          { numberOfProduct: quantities[1]._id }  // M, Red
        ]
      },
      {
        productName: 'Adidas Ultraboost',
        description: 'High-performance running shoes',
        originalPrice: 150.00,
        status: true,
        discount: 15.00,
        totalQuantity: 50,
        gender: true,
        tax: 10.0,
        sellingPrice: 135.00,
        proType: brands[1]._id, // ID của Adidas
        image: {
          imageID: images[1]._id // Chỉ lưu ObjectId của hình ảnh
        },
        inventory: [
          { numberOfProduct: quantities[0]._id }, // S, Blue
          { numberOfProduct: quantities[1]._id }  // M, Red
        ]
      }
      // Thêm các sản phẩm khác tương tự
    ]);

    console.log('Dữ liệu đã được nhập thành công!');
  } catch (error) {
    console.error('Lỗi khi nhập dữ liệu:', error);
  } finally {
    mongoose.connection.close();
  }
};

seedData();
