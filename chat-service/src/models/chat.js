"use strict";
const { Model } = require("sequelize");
module.exports = (sequelize, DataTypes) => {
  class Chat extends Model {
    /**
     * Helper method for defining associations.
     * This method is not a part of Sequelize lifecycle.
     * The `models/index` file will call this method automatically.
     */
    static associate(models) {
      
    }
  }
  Chat.init(
    {
      clientId: DataTypes.INTEGER,
      adminId: DataTypes.INTEGER,
      senderId: DataTypes.INTEGER,
      message: DataTypes.STRING,
      status: DataTypes.INTEGER,
      type: DataTypes.STRING,
      productId: DataTypes.STRING,
    },
    {
      sequelize,
      modelName: "Chat",
    }
  );
  return Chat;
};
