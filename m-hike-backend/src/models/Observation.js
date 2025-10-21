const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/database');

const Observation = sequelize.define('Observation', {
  id: {
    type: DataTypes.UUID,
    defaultValue: DataTypes.UUIDV4,
    primaryKey: true
  },
  hike_id: {
    type: DataTypes.UUID,
    allowNull: false,
    references: {
      model: 'hikes',
      key: 'id'
    },
    onDelete: 'CASCADE'
  },
  observation: {
    type: DataTypes.STRING(500),
    allowNull: false,
    validate: {
      notEmpty: true,
      len: [1, 500]
    }
  },
  time: {
    type: DataTypes.DATE,
    allowNull: false,
    defaultValue: DataTypes.NOW
  },
  comments: {
    type: DataTypes.TEXT,
    allowNull: true
  },
  image_url: {
    type: DataTypes.STRING(500),
    allowNull: true,
    validate: {
      isUrl: true
    }
  },
  category: {
    type: DataTypes.ENUM('wildlife', 'weather', 'trail_conditions', 'scenery', 'other'),
    allowNull: false,
    defaultValue: 'other'
  }
}, {
  tableName: 'observations',
  timestamps: true,
  createdAt: 'created_at',
  updatedAt: 'updated_at',
  indexes: [
    { fields: ['hike_id'] },
    { fields: ['category'] }
  ]
});

module.exports = Observation;
