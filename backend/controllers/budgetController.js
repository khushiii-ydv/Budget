const { query, seedUserData } = require('../config/db');
const logger = require('../middleware/logger');

/**
 * @route   GET api/budgets
 * @desc    Get budget vs actual spending
 * @access  Private
 */
exports.getBudgets = async (req, res) => {
    const userId = req.user.id;

    try {
        // Ensure data is seeded only for authorized user
        if (req.user.mobileNumber === '9307136478') {
            await seedUserData(userId);
        }

        // Fetch budget limits
        const budgetResult = await query('SELECT * FROM budgets WHERE user_id = $1', [userId]);
        
        // Fetch spending per category from transactions
        const spendingResult = await query(
            "SELECT category, SUM(amount) as total FROM transactions WHERE user_id = $1 AND type = 'Expense' GROUP BY category",
            [userId]
        );

        const spendingMap = {};
        spendingResult.rows.forEach(row => {
            spendingMap[row.category] = parseFloat(row.total);
        });

        const budgets = budgetResult.rows.map(b => {
            const spent = spendingMap[b.category] || 0;
            const limit = parseFloat(b.amount_limit);
            return {
                id: b.id,
                category: b.category,
                limit: limit,
                spent: spent,
                percentage: limit > 0 ? (spent / limit) * 100 : 0
            };
        });

        const totals = budgets.reduce((acc, b) => {
            acc.totalLimit += b.limit;
            acc.totalSpent += b.spent;
            return acc;
        }, { totalLimit: 0, totalSpent: 0 });

        const { totalLimit, totalSpent } = totals;

        const userResult = await query('SELECT name FROM users WHERE id = $1', [userId]);
        const userName = userResult.rows[0]?.name || 'User';

        return res.status(200).json({
            success: true,
            userName: userName,
            summary: {
                totalLimit,
                totalSpent,
                percentage: totalLimit > 0 ? (totalSpent / totalLimit) * 100 : 0,
                remaining: totalLimit - totalSpent,
                daysRemaining: 12, // Mocked for now to match image
                dailyAvg: totalSpent / 30 // Rough estimate
            },
            categories: budgets
        });
    } catch (error) {
        logger.error(`[BUDGET] Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};

/**
 * @route   GET api/goals
 * @desc    Get savings goals
 * @access  Private
 */
exports.getGoals = async (req, res) => {
    const userId = req.user.id;
    try {
        if (req.user.mobileNumber === '9307136478') {
            await seedUserData(userId);
        }
        const result = await query('SELECT * FROM goals WHERE user_id = $1', [userId]);
        const goals = result.rows.map(g => ({
            id: g.id,
            name: g.name,
            target: parseFloat(g.target_amount),
            current: parseFloat(g.current_amount),
            deadline: g.deadline,
            icon: g.icon_name,
            percentage: (parseFloat(g.current_amount) / parseFloat(g.target_amount)) * 100
        }));

        return res.status(200).json({
            success: true,
            goals: goals
        });
    } catch (error) {
        logger.error(`[BUDGET] Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};

/**
 * @route   POST api/budgets/update
 * @desc    Update budget limit for a category
 * @access  Private
 */
exports.updateBudget = async (req, res) => {
    const { category, limit } = req.body;
    const userId = req.user.id;

    if (!category || limit === undefined) {
        return res.status(400).json({ success: false, message: 'Category and limit are required' });
    }

    try {
        await query(
            'INSERT INTO budgets (user_id, category, amount_limit) VALUES ($1, $2, $3) ON CONFLICT (user_id, category) DO UPDATE SET amount_limit = $3',
            [userId, category, parseFloat(limit)]
        );

        return res.status(200).json({ success: true, message: 'Budget updated successfully' });
    } catch (error) {
        logger.error(`[BUDGET] Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};

/**
 * @route   POST api/budgets/goals/:id/add-fund
 * @desc    Add funds to a savings goal
 * @access  Private
 */
exports.addFundToGoal = async (req, res) => {
    const { amount } = req.body;
    const goalId = req.params.id;
    const userId = req.user.id;

    if (!amount || isNaN(amount)) {
        return res.status(400).json({ success: false, message: 'Valid amount is required' });
    }

    try {
        const result = await query(
            'UPDATE goals SET current_amount = current_amount + $1 WHERE id = $2 AND user_id = $3 RETURNING *',
            [parseFloat(amount), goalId, userId]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({ success: false, message: 'Goal not found' });
        }

        return res.status(200).json({ success: true, goal: result.rows[0] });
    } catch (error) {
        logger.error(`[BUDGET] Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};
