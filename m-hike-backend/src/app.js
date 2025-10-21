const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const path = require('path');
require('dotenv').config();

const app = express();

app.use(helmet());
app.use(cors());
app.use(morgan('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.use('/uploads', express.static(path.join(__dirname, '../uploads')));

const hikeRoutes = require('./routes/hikes');
const observationRoutes = require('./routes/observations');
const uploadRoutes = require('./routes/upload');

app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'OK',
    message: 'M-Hiking API is running',
    timestamp: new Date().toISOString()
  });
});

app.get('/', (req, res) => {
  res.json({
    message: 'Welcome to M-Hiking API',
    version: '1.0.0',
    endpoints: {
      hikes: '/api/hikes',
      observations: '/api/observations',
      upload: '/api/upload',
      health: '/health'
    }
  });
});

app.use('/api/hikes', hikeRoutes);
app.use('/api', observationRoutes);
app.use('/api/upload', uploadRoutes);

app.use((req, res) => {
  res.status(404).json({
    success: false,
    error: 'Route not found',
    path: req.path
  });
});

app.use((err, req, res, next) => {
  console.error('Error:', err.stack);
  res.status(err.status || 500).json({
    success: false,
    error: err.message || 'Internal Server Error',
    ...(process.env.NODE_ENV === 'development' && { stack: err.stack })
  });
});

module.exports = app;
