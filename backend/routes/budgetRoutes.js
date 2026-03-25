const express = require('express');
const router = express.Router();
const budgetController = require('../controllers/budgetController');
const auth = require('../middleware/auth');

router.get('/summary', auth, budgetController.getBudgets);
router.get('/goals', auth, budgetController.getGoals);
router.post('/update', auth, budgetController.updateBudget);
router.post('/goals/:id/add-fund', auth, budgetController.addFundToGoal);

module.exports = router;
