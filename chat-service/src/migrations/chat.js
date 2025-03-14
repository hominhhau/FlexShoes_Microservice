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
// npx sequelize-cli db:migrate --to 20231105121046-create-user.js
// npx sequelize-cli db:migrate --to migrate_addColumnUser.js
