const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/database');

const Hike = sequelize.define('Hike', {
  id: {
    type: DataTypes.UUID,
    defaultValue: DataTypes.UUIDV4,
    primaryKey: true
  },
  name: {
    type: DataTypes.STRING(200),
    allowNull: false,
    validate: {
      notEmpty: true,
      len: [1, 200]
    }
  },
  location: {
    type: DataTypes.STRING(255),
    allowNull: false,
    validate: {
      notEmpty: true
    }
  },
  date: {
    type: DataTypes.DATE,
    allowNull: false,
    validate: {
      isDate: true
    }
  },
  parking_available: {
    type: DataTypes.BOOLEAN,
    defaultValue: false
  },
  length: {
    type: DataTypes.FLOAT,
    allowNull: true,
    comment: 'Length in kilometers',
    validate: {
      min: 0
    }
  },
  difficulty: {
    type: DataTypes.ENUM('easy', 'moderate', 'hard', 'expert'),
    allowNull: false,
    defaultValue: 'moderate'
  },
  description: {
    type: DataTypes.TEXT,
    allowNull: true
  },
}, {
  tableName: 'hikes',
  timestamps: true,
  createdAt: 'created_at',
  updatedAt: 'updated_at',
  indexes: [
    { fields: ['date'] },
    { fields: ['difficulty'] }
  ]
});

module.exports = Hike;
