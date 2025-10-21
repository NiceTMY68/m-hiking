const { sequelize } = require('../config/database');
const Hike = require('./Hike');
const Observation = require('./Observation');

Hike.hasMany(Observation, {
  foreignKey: 'hike_id',
  as: 'observations',
  onDelete: 'CASCADE'
});

Observation.belongsTo(Hike, {
  foreignKey: 'hike_id',
  as: 'hike'
});


const syncDatabase = async (force = false) => {
  try {
    await sequelize.sync({ force, alter: !force });
    console.log('Database synchronized successfully!');
  } catch (error) {
    console.error('Error synchronizing database:', error);
    throw error;
  }
};

module.exports = {
  sequelize,
  Hike,
  Observation,
  syncDatabase
};
