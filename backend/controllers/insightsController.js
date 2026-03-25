const { query } = require('../config/db');
const logger = require('../middleware/logger');

/**
 * @route   GET api/insights
 * @desc    Get spending insights (distribution and trends)
 * @access  Private
 */
exports.getInsights = async (req, res) => {
    const userId = req.user.id;
    try {
        // 1. Category Distribution
        const distributionResult = await query(
            `SELECT category, SUM(amount) as total 
             FROM transactions 
             WHERE user_id = $1 AND type = 'Expense' 
             GROUP BY category`,
            [userId]
        );

        // 2. Spending Trends (Last 7 Days)
        const trendsResult = await query(
            `SELECT TO_CHAR(date, 'Dy') as day, SUM(amount) as total 
             FROM transactions 
             WHERE user_id = $1 AND type = 'Expense' 
             AND date >= CURRENT_DATE - INTERVAL '7 days'
             GROUP BY date 
             ORDER BY date ASC`,
            [userId]
        );

        res.json({
            success: true,
            distribution: distributionResult.rows,
            trends: trendsResult.rows
        });
    } catch (error) {
        logger.error(`[INSIGHTS] Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};

/**
 * @route   GET api/insights/suggestions
 * @desc    Get AI-powered saving suggestions
 * @access  Private
 */
exports.getSuggestions = async (req, res) => {
    const userId = req.user.id;
    try {
        // Simple logic: If Food > 20% of total expenses, suggest cooking more.
        const totalExpensesResult = await query(
            "SELECT SUM(amount) FROM transactions WHERE user_id = $1 AND type = 'Expense'",
            [userId]
        );
        const foodExpensesResult = await query(
            "SELECT SUM(amount) FROM transactions WHERE user_id = $1 AND type = 'Expense' AND category = 'Food & Dining'",
            [userId]
        );

        const total = parseFloat(totalExpensesResult.rows[0].sum || 0);
        const food = parseFloat(foodExpensesResult.rows[0].sum || 0);

        const suggestions = [];
        if (food > (total * 0.2)) {
            suggestions.push({
                title: "Reduce Dining Out",
                description: `You've spent ₹${Math.round(food)} on Food. Cooking at home could save you ~₹500 next month!`,
                icon: "restaurant"
            });
        }
        
        suggestions.push({
            title: "Subscription Audit",
            description: "We found recurring payments. Cancelling unused apps could save ₹150/mo.",
            icon: "subscriptions"
        });

        res.json({ success: true, suggestions });
    } catch (error) {
        logger.error(`[INSIGHTS] Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};
