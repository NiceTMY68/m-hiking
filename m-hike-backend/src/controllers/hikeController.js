const { Hike, Observation } = require('../models');
const { Op } = require('sequelize');

// Create new hike
const createHike = async (req, res) => {
  try {
    const { name, location, device_id, date, parking_available, length, difficulty, description, is_public } = req.body;

    const hike = await Hike.create({
      name,
      location,
      device_id,
      date,
      parking_available: parking_available || false,
      length,
      difficulty: difficulty || 'moderate',
      description,
      is_public: !!is_public
    });

    res.status(201).json({
      success: true,
      message: 'Hike created successfully',
      data: hike
    });
  } catch (error) {
    console.error('Create hike error:', error);
    res.status(500).json({
      success: false,
      message: 'Error creating hike',
      error: error.message
    });
  }
};

// Get all hikes with pagination and filtering
const getAllHikes = async (req, res) => {
  try {
    const {
      page = 1,
      limit = 10,
      difficulty,
      location,
      startDate,
      endDate,
      sortBy = 'date',
      order = 'DESC'
    } = req.query;

    const offset = (page - 1) * limit;
    const where = {};
    const deviceId = req.headers['x-device-id'];
    const onlyPublic = String(req.query.onlyPublic || '').toLowerCase() === 'true';
    if (deviceId && !onlyPublic) {
      where.device_id = deviceId;
    }
    if (onlyPublic) {
      where.is_public = true;
    }

    if (difficulty) {
      where.difficulty = difficulty;
    }

    if (location) {
      where.location = {
        [Op.iLike]: `%${location}%`
      };
    }

    if (startDate || endDate) {
      where.date = {};
      if (startDate) where.date[Op.gte] = new Date(startDate);
      if (endDate) where.date[Op.lte] = new Date(endDate);
    }

    const { count, rows: hikes } = await Hike.findAndCountAll({
      where,
      limit: parseInt(limit),
      offset: parseInt(offset),
      order: [[sortBy, order.toUpperCase()]],
      include: [
        {
          model: Observation,
          as: 'observations',
          attributes: ['id', 'observation', 'time', 'category']
        }
      ]
    });

    res.status(200).json({
      success: true,
      data: {
        hikes,
        pagination: {
          total: count,
          page: parseInt(page),
          limit: parseInt(limit),
          totalPages: Math.ceil(count / limit)
        }
      }
    });
  } catch (error) {
    console.error('Get hikes error:', error);
    res.status(500).json({
      success: false,
      message: 'Error fetching hikes',
      error: error.message
    });
  }
};

// Get single hike by ID
const getHikeById = async (req, res) => {
  try {
    const { id } = req.params;

    const hike = await Hike.findOne({
      where: {
        id,
        
      },
      include: [
        {
          model: Observation,
          as: 'observations',
          order: [['time', 'ASC']]
        }
      ]
    });

    if (!hike) {
      return res.status(404).json({
        success: false,
        message: 'Hike not found'
      });
    }

    res.status(200).json({
      success: true,
      data: hike
    });
  } catch (error) {
    console.error('Get hike error:', error);
    res.status(500).json({
      success: false,
      message: 'Error fetching hike',
      error: error.message
    });
  }
};

// Update hike
const updateHike = async (req, res) => {
  try {
    const { id } = req.params;
    const { name, location, device_id, date, parking_available, length, difficulty, description, is_public } = req.body;

    const hike = await Hike.findOne({
      where: {
        id
      }
    });

    if (!hike) {
      return res.status(404).json({
        success: false,
        message: 'Hike not found'
      });
    }

    if (name !== undefined) hike.name = name;
    if (location !== undefined) hike.location = location;
    if (device_id !== undefined) hike.device_id = device_id;
    if (date !== undefined) hike.date = date;
    if (parking_available !== undefined) hike.parking_available = parking_available;
    if (length !== undefined) hike.length = length;
    if (difficulty !== undefined) hike.difficulty = difficulty;
    if (description !== undefined) hike.description = description;
    if (is_public !== undefined) hike.is_public = !!is_public;

    await hike.save();

    res.status(200).json({
      success: true,
      message: 'Hike updated successfully',
      data: hike
    });
  } catch (error) {
    console.error('Update hike error:', error);
    res.status(500).json({
      success: false,
      message: 'Error updating hike',
      error: error.message
    });
  }
};

// Delete hike
const deleteHike = async (req, res) => {
  try {
    const { id } = req.params;

    const hike = await Hike.findOne({
      where: {
        id
      }
    });

    if (!hike) {
      return res.status(404).json({
        success: false,
        message: 'Hike not found'
      });
    }

    await hike.destroy();

    res.status(200).json({
      success: true,
      message: 'Hike deleted successfully'
    });
  } catch (error) {
    console.error('Delete hike error:', error);
    res.status(500).json({
      success: false,
      message: 'Error deleting hike',
      error: error.message
    });
  }
};

// Search hikes
const searchHikes = async (req, res) => {
  try {
    const { q, difficulty, minLength, maxLength } = req.query;

    const where = { user_id: req.userId };

    if (q) {
      where[Op.or] = [
        { name: { [Op.iLike]: `%${q}%` } },
        { location: { [Op.iLike]: `%${q}%` } },
        { description: { [Op.iLike]: `%${q}%` } }
      ];
    }

    if (difficulty) {
      where.difficulty = difficulty;
    }

    if (minLength || maxLength) {
      where.length = {};
      if (minLength) where.length[Op.gte] = parseFloat(minLength);
      if (maxLength) where.length[Op.lte] = parseFloat(maxLength);
    }

    const hikes = await Hike.findAll({ where, order: [['date', 'DESC']], limit: 50 });

    res.status(200).json({
      success: true,
      data: {
        results: hikes,
        count: hikes.length
      }
    });
  } catch (error) {
    console.error('Search hikes error:', error);
    res.status(500).json({
      success: false,
      message: 'Error searching hikes',
      error: error.message
    });
  }
};

module.exports = {
  createHike,
  getAllHikes,
  getHikeById,
  updateHike,
  deleteHike,
  searchHikes
};
