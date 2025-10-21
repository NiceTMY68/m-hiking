const { Observation, Hike } = require('../models');

// Get all observations for a hike
const getObservationsByHike = async (req, res) => {
  try {
    const { hikeId } = req.params;

    const hike = await Hike.findOne({ where: { id: hikeId } });

    if (!hike) {
      return res.status(404).json({
        success: false,
        message: 'Hike not found'
      });
    }

    const observations = await Observation.findAll({
      where: { hike_id: hikeId },
      order: [['time', 'ASC']]
    });

    res.status(200).json({
      success: true,
      data: observations
    });
  } catch (error) {
    console.error('Get observations error:', error);
    res.status(500).json({
      success: false,
      message: 'Error fetching observations',
      error: error.message
    });
  }
};

// Create new observation
const createObservation = async (req, res) => {
  try {
    const { hikeId } = req.params;
    const { observation, time, comments, image_url, category } = req.body;

    const hike = await Hike.findOne({ where: { id: hikeId } });

    if (!hike) {
      return res.status(404).json({
        success: false,
        message: 'Hike not found'
      });
    }

    const newObservation = await Observation.create({
      hike_id: hikeId,
      observation,
      time: time || new Date(),
      comments,
      image_url,
      category: category || 'other',
      user_id: req.userId
    });

    res.status(201).json({
      success: true,
      message: 'Observation created successfully',
      data: newObservation
    });
  } catch (error) {
    console.error('Create observation error:', error);
    res.status(500).json({
      success: false,
      message: 'Error creating observation',
      error: error.message
    });
  }
};

// Get single observation
const getObservationById = async (req, res) => {
  try {
    const { id } = req.params;

    const observation = await Observation.findOne({
      where: { id },
      include: [
        {
          model: Hike,
          as: 'hike',
          attributes: ['id', 'name', 'location', 'date']
        }
      ]
    });

    if (!observation) {
      return res.status(404).json({
        success: false,
        message: 'Observation not found'
      });
    }

    res.status(200).json({
      success: true,
      data: observation
    });
  } catch (error) {
    console.error('Get observation error:', error);
    res.status(500).json({
      success: false,
      message: 'Error fetching observation',
      error: error.message
    });
  }
};

// Update observation
const updateObservation = async (req, res) => {
  try {
    const { id } = req.params;
    const { observation, time, comments, image_url, category } = req.body;

    const observationRecord = await Observation.findOne({ where: { id } });

    if (!observationRecord) {
      return res.status(404).json({
        success: false,
        message: 'Observation not found'
      });
    }

    if (observation !== undefined) observationRecord.observation = observation;
    if (time !== undefined) observationRecord.time = time;
    if (comments !== undefined) observationRecord.comments = comments;
    if (image_url !== undefined) observationRecord.image_url = image_url;
    if (category !== undefined) observationRecord.category = category;

    await observationRecord.save();

    res.status(200).json({
      success: true,
      message: 'Observation updated successfully',
      data: observationRecord
    });
  } catch (error) {
    console.error('Update observation error:', error);
    res.status(500).json({
      success: false,
      message: 'Error updating observation',
      error: error.message
    });
  }
};

// Delete observation
const deleteObservation = async (req, res) => {
  try {
    const { id } = req.params;

    const observation = await Observation.findOne({ where: { id } });

    if (!observation) {
      return res.status(404).json({
        success: false,
        message: 'Observation not found'
      });
    }

    await observation.destroy();

    res.status(200).json({
      success: true,
      message: 'Observation deleted successfully'
    });
  } catch (error) {
    console.error('Delete observation error:', error);
    res.status(500).json({
      success: false,
      message: 'Error deleting observation',
      error: error.message
    });
  }
};

module.exports = {
  getObservationsByHike,
  createObservation,
  getObservationById,
  updateObservation,
  deleteObservation
};
