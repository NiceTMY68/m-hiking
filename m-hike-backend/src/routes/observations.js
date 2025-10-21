const express = require('express');
const router = express.Router();
const observationController = require('../controllers/observationController');
const { validateObservationCreation, validateUUID } = require('../middleware/validation');

router.get('/hikes/:hikeId/observations', 
  validateUUID('hikeId'),
  observationController.getObservationsByHike
);

router.post('/hikes/:hikeId/observations', 
  validateUUID('hikeId'),
  validateObservationCreation,
  observationController.createObservation
);

router.get('/observations/:id', 
  validateUUID('id'),
  observationController.getObservationById
);

router.put('/observations/:id', 
  validateUUID('id'),
  observationController.updateObservation
);

router.delete('/observations/:id', 
  validateUUID('id'),
  observationController.deleteObservation
);

module.exports = router;
