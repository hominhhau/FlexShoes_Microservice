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

const getLastMessage = async (req, res) => {
  try {
    const senderIds = req.query.senderIds
      ?.split(",")
      .map((id) => parseInt(id))
      .filter(Boolean);
    if (!senderIds || senderIds.length === 0) {
      return res.status(400).json({ message: "senderIds is required" });
    }
    
    const latestMessages = await Promise.all(
      senderIds.map(async (clientId) => {
        const latestMessage = await db.Chat.findOne({
          where: { clientId },
          order: [["createdAt", "DESC"]],
        });

        return latestMessage;
      })
    );

    return res.status(200).json({
      EM: "success", //error message
      EC: 0,
      DT: latestMessages.filter(Boolean), // data
    });
  } catch (error) {
    console.log("error", error);

    return res.status(500).json({
      EM: "error from sever getLastMessage", //error message
      EC: -1, //error code
      DT: "", // data
    });
  }
};

module.exports = {
  sendMess,
  showMess,
  getAllSender,
  getLastMessage,
};
