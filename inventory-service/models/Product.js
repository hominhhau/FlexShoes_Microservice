const mongoose = require("mongoose");

const ProductSchema = new mongoose.Schema({
    productName:{
        type: String,
        required: true
    },
    originalPrice:{//gia goc
        type: Number,
        required: true
    },
    description:{
        type: String
    },
    status:{
        type: String,
        default: true
    },
    discount:{
        type: Number,
        default: 0
    },
    totalQuantity:{
        type: Number,
        default: 0
    },
    gender:{
        type: String,
        required: true
    },
    tax:{
        type: Number,
        default: 0
    },
    sellingPrice:{//gia ban
        type: Number,
        required: true
    },
    proType:{
        type: mongoose.Schema.Types.ObjectId, ref: 'ProductType'
    },
    braType:{
        type: mongoose.Schema.Types.ObjectId, ref: 'BrandType'
    },
    image: [
        {
            _id: false,
            imageID: {
                type: mongoose.Schema.Types.ObjectId, ref: 'Image'
            }
        }
    ],
    inventory:[
        {
            _id: false,// tùy
            numberOfProduct:{
                type: mongoose.Schema.Types.ObjectId, 
                ref: 'NumberOfProducts'   
            },       
        }
    ]

});
ProductSchema.pre('save', async function (next) {
    await this.populate('inventory.numberOfProduct');
    this.totalQuantity = this.inventory.reduce((sum, item) => {
        return sum + (item.numberOfProduct?.quantity || 0);
    }, 0);
    next();
});


module.exports = mongoose.model("Product", ProductSchema);