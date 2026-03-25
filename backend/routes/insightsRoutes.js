const express = require('express');
const router = express.Router();
const insightsController = require('../controllers/insightsController');
const auth = require('../middleware/auth');

router.get('/', auth, insightsController.getInsights);
router.get('/suggestions', auth, insightsController.getSuggestions);

module.exports = router;
