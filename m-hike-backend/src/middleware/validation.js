const validateUserRegistration = (req, res, next) => {
  const { username, email, password } = req.body;
  const errors = [];

  if (!username || username.trim().length < 3) {
    errors.push('Username must be at least 3 characters long');
  }

  if (!email || !isValidEmail(email)) {
    errors.push('Valid email is required');
  }

  if (!password || password.length < 6) {
    errors.push('Password must be at least 6 characters long');
  }

  if (errors.length > 0) {
    return res.status(400).json({
      success: false,
      message: 'Validation failed',
      errors
    });
  }

  next();
};

const validateUserLogin = (req, res, next) => {
  const { email, password } = req.body;
  const errors = [];

  if (!email) {
    errors.push('Email is required');
  }

  if (!password) {
    errors.push('Password is required');
  }

  if (errors.length > 0) {
    return res.status(400).json({
      success: false,
      message: 'Validation failed',
      errors
    });
  }

  next();
};

const validateHikeCreation = (req, res, next) => {
  const { name, location, date, difficulty } = req.body;
  const errors = [];

  if (!name || name.trim().length === 0) {
    errors.push('Hike name is required');
  }

  if (!location || location.trim().length === 0) {
    errors.push('Location is required');
  }

  if (!date || !isValidDate(date)) {
    errors.push('Valid date is required');
  }

  if (difficulty && !['easy', 'moderate', 'hard', 'expert'].includes(difficulty)) {
    errors.push('Difficulty must be one of: easy, moderate, hard, expert');
  }

  if (req.body.length && (isNaN(req.body.length) || req.body.length < 0)) {
    errors.push('Length must be a positive number');
  }

  if (errors.length > 0) {
    return res.status(400).json({
      success: false,
      message: 'Validation failed',
      errors
    });
  }

  next();
};

const validateObservationCreation = (req, res, next) => {
  const { observation } = req.body;
  const errors = [];

  if (!observation || observation.trim().length === 0) {
    errors.push('Observation text is required');
  }

  if (observation && observation.length > 500) {
    errors.push('Observation text must be 500 characters or less');
  }

  if (req.body.category && !['wildlife', 'weather', 'trail_conditions', 'scenery', 'other'].includes(req.body.category)) {
    errors.push('Category must be one of: wildlife, weather, trail_conditions, scenery, other');
  }

  if (errors.length > 0) {
    return res.status(400).json({
      success: false,
      message: 'Validation failed',
      errors
    });
  }

  next();
};

const validateUUID = (paramName) => {
  return (req, res, next) => {
    const uuid = req.params[paramName];
    const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

    if (!uuid || !uuidRegex.test(uuid)) {
      return res.status(400).json({
        success: false,
        message: `Invalid ${paramName} format`
      });
    }

    next();
  };
};

const isValidEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

const isValidDate = (date) => {
  const parsedDate = new Date(date);
  return parsedDate instanceof Date && !isNaN(parsedDate);
};

module.exports = {
  validateUserRegistration,
  validateUserLogin,
  validateHikeCreation,
  validateObservationCreation,
  validateUUID
};
