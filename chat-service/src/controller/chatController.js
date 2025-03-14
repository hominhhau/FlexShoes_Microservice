const { raw } = require("body-parser");
const db = require("../models/index");
const { Sequelize, Op } = require("sequelize");

const sendMess = async (req, res) => {
  try {
    let data = await db.Chat.create({
      clientId: req.body.clientId,
      adminId: 1,
      senderId: req.body.senderId,
      message: req.body.message,
    });

    let mess = await db.Chat.findAll({
      where: {
        clientId: req.body.clientId,
        adminId: 1,
      },
    });

    return res.status(200).json({
      EM: "success", //error message
      EC: 0,
      DT: mess, // data
    });
  } catch (error) {
    console.log("error", error);

    return res.status(500).json({
      EM: "error from sever sendMess", //error message
      EC: -1, //error code
      DT: "", // data
    });
  }
};

const showMess = async (req, res) => {
  try {
    let data = await db.Chat.findAll({
      where: {
        clientId: req.body.senderId.senderID,
        adminId: 1,
      },
    });

    return res.status(200).json({
      EM: "success", //error message
      EC: 0,
      DT: data, // data
    });
  } catch (error) {
    console.log("error", error);

    return res.status(500).json({
      EM: "error from sever showMess", //error message
      EC: -1, //error code
      DT: "", // data
    });
  }
};

const getAllSender = async (req, res) => {
  try {
    let data = await db.Chat.findAll({
      // clientId không trùng nhau
      where: {
        createdAt: {
          [Op.eq]: Sequelize.literal(
            "(SELECT MAX(createdAt) FROM Chat AS C WHERE C.clientId = Chat.clientId)"
          ),
        },
      },
    });

    return res.status(200).json({
      EM: "success", //error message
      EC: 0,
      DT: data, // data
    });
  } catch (error) {
    console.log("error", error);

    return res.status(500).json({
      EM: "error from sever getAllSender", //error message
      EC: -1, //error code
      DT: "", // data
    });
  }
};

module.exports = {
  sendMess,
  showMess,
  getAllSender,
};
