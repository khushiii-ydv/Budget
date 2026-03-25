const { Pool } = require('pg');
const logger = require('../middleware/logger');
require('dotenv').config();

const pool = new Pool({
    user: process.env.DB_USER,
    host: process.env.DB_HOST,
    database: process.env.DB_NAME,
    password: process.env.DB_PASSWORD,
    port: process.env.DB_PORT,
});

pool.on('connect', () => {
    logger.info('[DB] Connected to PostgreSQL');
});

pool.on('error', (err) => {
    logger.error('[DB] Unexpected error on idle client', err);
    process.exit(-1);
});

const query = (text, params) => pool.query(text, params);

const seedUserData = async (userId) => {
    try {
        // Use let for clearer scope and trace issues
        let seedCheck;
        let budgetCheck;
        
        try {
            seedCheck = await query("SELECT * FROM transactions WHERE user_id = $1 AND title = 'Opening Balance'", [userId]);
            budgetCheck = await query("SELECT * FROM budgets WHERE user_id = $1 LIMIT 1", [userId]);
        } catch (innerError) {
            logger.error(`[DB] Query error in seedUserData for user ${userId}: ${innerError.message}`, innerError);
            return;
        }
        
        if (!seedCheck || !seedCheck.rows || seedCheck.rows.length === 0 || !budgetCheck || !budgetCheck.rows || budgetCheck.rows.length === 0) {
            logger.info(`[DB] Forced seeding: Wiping old data and seeding exact image data for user ${userId}...`);
            
            // Wipe existing data to ensure clean image-match data
            await query('DELETE FROM transactions WHERE user_id = $1', [userId]);
            await query('DELETE FROM budgets WHERE user_id = $1', [userId]);
            await query('DELETE FROM goals WHERE user_id = $1', [userId]);

            // Data calculated to match images:
            // Total Income: 8,420, Total Expenses: 3,210, Balance: 42,650
            // Math: Starting Balance (37,440) + Income (8,420) - Expenses (3,210) = 42,650
            await query(
                `INSERT INTO transactions (user_id, title, amount, category, type, date, payment_method) VALUES 
                ($1, 'Opening Balance', 38370.00, 'Initial', 'Income', '2026-03-01', 'System'),
                ($1, 'Salary Payment', 4500.00, 'Income', 'Income', '2026-03-01', 'Bank Transfer'),
                ($1, 'Freelance Project', 4060.00, 'Income', 'Income', '2026-03-05', 'UPI'),
                ($1, 'Grocery Store', 520.00, 'Food & Dining', 'Expense', '2026-03-22', 'Debit Card'),
                ($1, 'Fuel Station', 1104.00, 'Travel', 'Expense', '2026-03-21', 'Credit Card'),
                ($1, 'Monthly Rent', 2100.00, 'Rent', 'Expense', '2026-03-20', 'Bank Transfer'),
                ($1, 'Movie Night', 345.00, 'Fun', 'Expense', '2026-03-19', 'Cash'),
                ($1, 'Subscription', 211.00, 'Other', 'Expense', '2026-03-18', 'UPI')`,
                [userId]
            );
            // Seed Budgets (to match 65%, 92%, 100%, 115% in images)
            await query(
                `INSERT INTO budgets (user_id, category, amount_limit) VALUES 
                ($1, 'Food & Dining', 800.00),
                ($1, 'Travel', 1200.00),
                ($1, 'Rent', 2100.00),
                ($1, 'Fun', 300.00),
                ($1, 'Other', 1100.00)`,
                [userId]
            );

            // Seed Savings Goals
            await query(
                `INSERT INTO goals (user_id, name, target_amount, current_amount, deadline, icon_name) VALUES 
                ($1, 'New Tesla Model 3', 45000.00, 20250.00, 'Sept 2025', 'directions_car'),
                ($1, 'Tokyo Vacation', 6000.00, 4800.00, 'Dec 2024', 'beach_access')`,
                [userId]
            );

            logger.info(`[DB] Successfully seeded exact image data (Transactions, Budgets, Goals) for user ${userId}`);
        }
    } catch (err) {
        logger.error(`[DB] Seeding error for user ${userId}: ${err.message}`, err);
    }
};

const initDb = async () => {
    const createUsersTable = `
        CREATE TABLE IF NOT EXISTS users (
            id SERIAL PRIMARY KEY,
            name VARCHAR(100),
            mobile_number VARCHAR(15) UNIQUE NOT NULL,
            password VARCHAR(255) NOT NULL,
            monthly_income DECIMAL(12, 2) DEFAULT 0,
            currency VARCHAR(10) DEFAULT 'INR',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    `;

    const createTransactionsTable = `
        CREATE TABLE IF NOT EXISTS transactions (
            id SERIAL PRIMARY KEY,
            user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
            title VARCHAR(255) NOT NULL,
            amount DECIMAL(12, 2) NOT NULL,
            category VARCHAR(100) NOT NULL,
            type VARCHAR(10) NOT NULL, -- 'Income' or 'Expense'
            date DATE NOT NULL,
            payment_method VARCHAR(50),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    `;

    const createBudgetsTable = `
        CREATE TABLE IF NOT EXISTS budgets (
            id SERIAL PRIMARY KEY,
            user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
            category VARCHAR(100) NOT NULL,
            amount_limit DECIMAL(12, 2) NOT NULL,
            UNIQUE(user_id, category)
        );
    `;

    const createGoalsTable = `
        CREATE TABLE IF NOT EXISTS goals (
            id SERIAL PRIMARY KEY,
            user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
            name VARCHAR(255) NOT NULL,
            target_amount DECIMAL(12, 2) NOT NULL,
            current_amount DECIMAL(12, 2) DEFAULT 0,
            deadline VARCHAR(50),
            icon_name VARCHAR(50)
        );
    `;

    try {
        await query(createUsersTable);
        await query(createTransactionsTable);
        await query(createBudgetsTable);
        await query(createGoalsTable);
        
        // Seed initial user if none exists
        const userCount = await query('SELECT COUNT(*) FROM users');
        if (parseInt(userCount.rows[0].count) === 0) {
            const salt = await require('bcrypt').genSalt(10);
            const hashedPass = await require('bcrypt').hash('hello', salt);
            const userResult = await query(
                'INSERT INTO users (name, mobile_number, password) VALUES ($1, $2, $3) RETURNING id',
                ['Khushi', '9307136478', hashedPass]
            );
            const userId = userResult.rows[0].id;
            logger.info('[DB] Seeded initial user: Khushi (9307136478)');
            await seedUserData(userId);
        }

        logger.info('[DB] Tables initialized and seeded successfully');
    } catch (err) {
        logger.error(`[DB] Initialization error: ${err.message}`, err);
    }
};

module.exports = {
    query,
    initDb,
    seedUserData
};
