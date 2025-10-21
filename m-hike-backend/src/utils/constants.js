const DIFFICULTY_LEVELS = {
  EASY: 'easy',
  MODERATE: 'moderate',
  HARD: 'hard',
  EXPERT: 'expert'
};

const OBSERVATION_CATEGORIES = {
  WILDLIFE: 'wildlife',
  WEATHER: 'weather',
  TRAIL_CONDITIONS: 'trail_conditions',
  SCENERY: 'scenery',
  OTHER: 'other'
};

const ERROR_MESSAGES = {
  UNAUTHORIZED: 'Unauthorized access',
  NOT_FOUND: 'Resource not found',
  VALIDATION_ERROR: 'Validation error',
  SERVER_ERROR: 'Internal server error',
  DATABASE_ERROR: 'Database error',
  INVALID_CREDENTIALS: 'Invalid credentials',
  USER_EXISTS: 'User already exists',
  INVALID_TOKEN: 'Invalid or expired token'
};

const SUCCESS_MESSAGES = {
  CREATED: 'Resource created successfully',
  UPDATED: 'Resource updated successfully',
  DELETED: 'Resource deleted successfully',
  LOGIN_SUCCESS: 'Login successful',
  REGISTER_SUCCESS: 'Registration successful'
};

const PAGINATION_DEFAULTS = {
  PAGE: 1,
  LIMIT: 10,
  MAX_LIMIT: 100
};

const FILE_UPLOAD = {
  MAX_SIZE: 5 * 1024 * 1024,
  ALLOWED_TYPES: ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'],
  ALLOWED_EXTENSIONS: ['.jpg', '.jpeg', '.png', '.gif', '.webp']
};

module.exports = {
  DIFFICULTY_LEVELS,
  OBSERVATION_CATEGORIES,
  ERROR_MESSAGES,
  SUCCESS_MESSAGES,
  PAGINATION_DEFAULTS,
  FILE_UPLOAD
};
