const { query, seedUserData } = require('../config/db');
const logger = require('../middleware/logger');

/**
 * @route   GET api/dashboard/summary
 * @desc    Get dashboard summary statistics
 * @access  Private
 */
exports.getSummary = async (req, res) => {
    const userId = req.user.id;

    try {
        // Ensure user has seed data if they are the authorized number
        if (req.user.mobileNumber === '9307136478') {
            await seedUserData(userId);
        }

        // Get user info
        const userResult = await query('SELECT name FROM users WHERE id = $1', [userId]);
        const userName = userResult.rows.length > 0 ? userResult.rows[0].name : 'User';

        // Get total income
        const incomeResult = await query(
            "SELECT SUM(amount) as total FROM transactions WHERE user_id = $1 AND type = 'Income'",
            [userId]
        );
        const totalIncome = parseFloat(incomeResult.rows[0].total || 0);

        // Get total expenses
        const expenseResult = await query(
            "SELECT SUM(amount) as total FROM transactions WHERE user_id = $1 AND type = 'Expense'",
            [userId]
        );
        const totalExpenses = parseFloat(expenseResult.rows[0].total || 0);

        const totalBalance = totalIncome - totalExpenses;

        // Get recent transactions
        const transactionsResult = await query(
            "SELECT * FROM transactions WHERE user_id = $1 ORDER BY date DESC, created_at DESC LIMIT 5",
            [userId]
        );

        const summary = {
            totalBalance,
            totalIncome,
            totalExpenses,
            savings: totalBalance,
            recentTransactions: transactionsResult.rows.map(t => ({
                id: t.id,
                title: t.title,
                amount: parseFloat(t.amount),
                category: t.category,
                type: t.type,
                date: t.date.toISOString().split('T')[0],
                paymentMethod: t.payment_method
            }))
        };

        return res.status(200).json({
            success: true,
            summary: summary,
            userName: userName
        });
    } catch (error) {
        logger.error(`[DASHBOARD] Summary Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};
