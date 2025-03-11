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
        type: Boolean,
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
        type: Boolean,
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
            imageID: {
                type: mongoose.Schema.Types.ObjectId, ref: 'Image'
            }
        }
    ],
    inventory:[
        {
            quantity:{
              type: Number, ref: 'NumberOfProducts'
            },
            // numberOfProduct:{
            //     type: mongoose.Schema.Types.ObjectId, ref: 'NumberOfProducts'   

            // },
            size:{
               _id: {
                     type: mongoose.Schema.Types.ObjectId, ref: 'Size'
               },
               sizeName: {
                     type: String,
               }
            },
            color:{
                _id: {
                    type: mongoose.Schema.Types.ObjectId, ref: 'Color'
                },
                colorName: {
                    type: String,
                }
            }
        }
    ]

});
ProductSchema.pre('save', function (next) {
    this.totalQuantity = this.inventory.reduce((sum, item) => sum + (item.quantity || 0), 0);
    next();
});

module.exports = mongoose.model("Product", ProductSchema);