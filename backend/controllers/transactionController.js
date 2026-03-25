const { query } = require('../config/db');
const logger = require('../middleware/logger');

/**
 * @route   GET api/transactions
 * @desc    Get all transactions for user
 * @access  Private
 */
exports.getAllTransactions = async (req, res) => {
    const userId = req.user.id;

    try {
        const result = await query(
            'SELECT * FROM transactions WHERE user_id = $1 ORDER BY date DESC, created_at DESC',
            [userId]
        );
        
        return res.status(200).json({
            success: true,
            transactions: result.rows.map(t => ({
                ...t,
                amount: parseFloat(t.amount),
                date: t.date.toISOString().split('T')[0]
            }))
        });
    } catch (error) {
        logger.error(`[TRANSACTION] Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};

/**
 * @route   POST api/transactions
 * @desc    Add a new transaction
 * @access  Private
 */
exports.addTransaction = async (req, res) => {
    const { title, amount, category, type, date, paymentMethod } = req.body;
    const userId = req.user.id;

    if (!title || !amount || !category || !type || !date) {
        return res.status(400).json({ success: false, message: 'Please provide all fields' });
    }

    try {
        const result = await query(
            'INSERT INTO transactions (user_id, title, amount, category, type, date, payment_method) VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING *',
            [userId, title, parseFloat(amount), category, type, date, paymentMethod || 'Cash']
        );

        const newTransaction = result.rows[0];
        newTransaction.amount = parseFloat(newTransaction.amount);
        newTransaction.date = newTransaction.date.toISOString().split('T')[0];

        return res.status(201).json({
            success: true,
            message: 'Transaction added successfully',
            transaction: newTransaction
        });
    } catch (error) {
        logger.error(`[TRANSACTION] Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};

/**
 * @route   DELETE api/transactions/:id
 * @desc    Delete a transaction
 * @access  Private
 */
exports.deleteTransaction = async (req, res) => {
    const { id } = req.params;
    const userId = req.user.id;

    try {
        const result = await query(
            'DELETE FROM transactions WHERE id = $1 AND user_id = $2',
            [id, userId]
        );

        if (result.rowCount === 0) {
            return res.status(404).json({ success: false, message: 'Transaction not found or unauthorized' });
        }

        return res.status(200).json({
            success: true,
            message: 'Transaction deleted successfully'
        });
    } catch (error) {
        logger.error(`[TRANSACTION] Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};
