const express = require('express');
const router = express.Router();
const hikeController = require('../controllers/hikeController');
const { validateHikeCreation, validateUUID } = require('../middleware/validation');

router.get('/search', hikeController.searchHikes);
router.get('/', hikeController.getAllHikes);
router.post('/', validateHikeCreation, hikeController.createHike);
router.get('/:id', validateUUID('id'), hikeController.getHikeById);
router.put('/:id', validateUUID('id'), hikeController.updateHike);
router.delete('/:id', validateUUID('id'), hikeController.deleteHike);

module.exports = router;
