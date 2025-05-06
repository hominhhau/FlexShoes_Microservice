//search: sequelize
"use strict";
module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.createTable("Chat", {
      id: {
        allowNull: false,
        autoIncrement: true,
        primaryKey: true,
        type: Sequelize.INTEGER,
      },
      clientId: {
        type: Sequelize.INTEGER,
      },
      senderId: {
        type: Sequelize.INTEGER,
      },
      adminId: {
        type: Sequelize.INTEGER,
      },
      message : {
        type: Sequelize.STRING,
      },
      status: {
        type: Sequelize.INTEGER, // 0 - chưa xem, 1 - đã xem
      },
      type: {
        type: Sequelize.STRING, // text, image
        defaultValue: "text",
      },

      createdAt: {
        allowNull: false,
        type: Sequelize.DATE,
      },
      updatedAt: {
        allowNull: false,
        type: Sequelize.DATE,
      },
    });
  },
  down: async (queryInterface, Sequelize) => {
    await queryInterface.dropTable("Chat");
  },
};

// search : sequelize run specific migration
// npx sequelize-cli db:migrate --to chat.js
// npx sequelize-cli db:migrate --to migrate_addColumnUser.js
