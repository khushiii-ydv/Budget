const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');

router.post('/login', authController.login);
router.post('/register', authController.register);
router.post('/verify-otp', authController.verifyOtp);
router.get('/profile', require('../middleware/auth'), authController.getProfile);
router.post('/profile/update', require('../middleware/auth'), authController.updateProfile);

module.exports = router;
