const express = require('express');
const router = express.Router();
const uploadController = require('../controllers/uploadController');
const { upload, handleUploadError } = require('../middleware/upload');

router.post('/image', 
  upload.single('image'),
  handleUploadError,
  uploadController.uploadImage
);

router.post('/images', 
  upload.array('images', 10),
  handleUploadError,
  uploadController.uploadMultipleImages
);

module.exports = router;
