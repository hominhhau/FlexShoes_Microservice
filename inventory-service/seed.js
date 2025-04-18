// path=seed.js
const mongoose = require('mongoose');
const BrandType = require('./models/BrandType');
const ProductType = require('./models/ProductType'); // ✅ Thêm dòng này vì bạn sử dụng ProductType
const Color = require('./models/Color');
const Size = require('./models/Size');
const Product = require('./models/Product');
const Image = require('./models/Image');
const NumberOfProducts = require('./models/NumberOfProducts');

const seedData = async () => {
  try {
    // ✅ Kết nối đến MongoDB
    await mongoose.connect('mongodb://localhost:27017/inventoryDB');

    // ✅ Xóa dữ liệu cũ
    await Promise.all([
      BrandType.deleteMany({}),
      ProductType.deleteMany({}),
      Color.deleteMany({}),
      Size.deleteMany({}),
      Product.deleteMany({}),
      Image.deleteMany({}),
      NumberOfProducts.deleteMany({})
    ]);

    // ✅ Seed BrandType
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

    // ✅ Seed ProductType
    // ✅ Seed ProductType (CATEGORY)
    const productTypes = await ProductType.insertMany([
      { productTypeName: 'Casual shoes', description: 'Category for casual and everyday wear shoes' },
      { productTypeName: 'Runners', description: 'Category for running shoes and trainers' },
      { productTypeName: 'Hiking', description: 'Category for hiking and trekking shoes' },
      { productTypeName: 'Sneaker', description: 'Fashion sneakers for casual and streetwear' },
      { productTypeName: 'Basketball', description: 'Basketball-specific performance shoes' },
      { productTypeName: 'Golf', description: 'Golf shoes with stability and grip for swings' },
      { productTypeName: 'Outdoor', description: 'Shoes designed for outdoor adventures and durability' }
    ]);


    // ✅ Seed Color
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

    // ✅ Seed Size
    const sizes = await Size.insertMany([
      { nameSize: '38' }, { nameSize: '39' }, { nameSize: '40' },
      { nameSize: '41' }, { nameSize: '42' }, { nameSize: '43' }, { nameSize: '44' }, { nameSize: '45' }, { nameSize: '46' }, { nameSize: '47' }

    ]);

    // // ✅ Seed Image
    // const images = await Image.insertMany([
    //   { imageName: 'nike-air-max-trang-xanh-1.png', URL: 'https://picsum.photos/seed/picsum1/200/300' },
    //   { imageName: 'nike-air-max-trang-xanh-2.png', URL: 'https://picsum.photos/seed/picsum2/200/300' },
    //   { imageName: 'adidas-ultraboost01-trang-1.png', URL: 'https://picsum.photos/seed/picsum3/200/300' },
    //   { imageName: 'adidas-ultraboost01-trang-2.png', URL: 'https://picsum.photos/seed/picsum4/200/300' }
    // ]);

    // ✅ Seed Quantity (NumberOfProducts)
    // const quantities = await NumberOfProducts.insertMany([
    //   { quantity: 50, size: sizes[0]._id, color: colors[0]._id },
    //   { quantity: 50, size: sizes[1]._id, color: colors[1]._id },
    //   { quantity: 50, size: sizes[2]._id, color: colors[2]._id },
    //   { quantity: 50, size: sizes[3]._id, color: colors[3]._id },


    // ]);

    // // ✅ Seed Product
    // const products = await Product.insertMany([
    //   {
    //     productName: 'Nike Air Max',
    //     description: 'Comfortable and stylish sneakers',
    //     originalPrice: 120.00,
    //     status: true,
    //     discount: 5.00,
    //     totalQuantity: 50,
    //     gender: true,
    //     tax: 10.0,
    //     sellingPrice: 115.00,
    //     proType: productTypes[0]._id,
    //     braType: brands[0]._id,
    //     image: { imageID: images[0]._id },
    //     inventory: [
    //       { numberOfProduct: quantities[0]._id },
    //       { numberOfProduct: quantities[1]._id }
    //     ]
    //   },
    //   {
    //     productName: 'Adidas Ultraboost',
    //     description: 'High-performance running shoes',
    //     originalPrice: 150.00,
    //     status: true,
    //     discount: 15.00,
    //     totalQuantity: 50,
    //     gender: true,
    //     tax: 10.0,
    //     sellingPrice: 135.00,
    //     proType: productTypes[1]._id,
    //     braType: brands[1]._id,
    //     image: { imageID: images[1]._id },
    //     inventory: [
    //       { numberOfProduct: quantities[2]._id },
    //       { numberOfProduct: quantities[3]._id }
    //     ]
    //   }
    // ]);

    console.log('✅ Dữ liệu đã được seed thành công!');
  } catch (error) {
    console.error('❌ Lỗi khi seed dữ liệu:', error);
  } finally {
    mongoose.connection.close();
  }
};

seedData();