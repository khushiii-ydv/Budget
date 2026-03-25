const express = require('express');
const cors = require('cors');
require('dotenv').config();

const authRoutes = require('./routes/authRoutes');
const dashboardRoutes = require('./routes/dashboardRoutes');
const transactionRoutes = require('./routes/transactionRoutes');
const budgetRoutes = require('./routes/budgetRoutes');
const morgan = require('morgan');
const logger = require('./middleware/logger');
const { initDb } = require('./config/db');

const app = express();
const PORT = process.env.PORT || 5001;

// Initialize Database
initDb();

app.use(cors());
app.use(express.json());

// Request logger middleware
app.use(morgan('combined', { stream: { write: message => logger.info(message.trim()) } }));

app.use((req, res, next) => {
    logger.info(`[REQ] ${req.method} ${req.url}`);
    next();
});

// Routes
app.use('/api/auth', authRoutes);
app.use('/api/dashboard', dashboardRoutes);
app.use('/api/transactions', transactionRoutes);
app.use('/api/budgets', budgetRoutes);
app.use('/api/insights', require('./routes/insightsRoutes'));

app.get('/', (req, res) => {
    res.send('BudgetBee API is running...');
});

const server = app.listen(PORT, '0.0.0.0', () => {
    logger.info(`Server is running on port ${PORT}`);
});

server.on('error', (err) => {
    logger.error('SERVER ERROR EVENT:', err);
});

server.on('close', () => {
    console.log('SERVER CLOSED');
});

// Handle unhandled promise rejections
process.on('unhandledRejection', (err, promise) => {
    logger.error(`Unhandled Rejection: ${err.message}`, err);
});

process.on('uncaughtException', (err) => {
    logger.error(`Uncaught Exception: ${err.message}`, err);
});

process.on('exit', (code) => {
    logger.info(`Process about to exit with code: ${code}`);
});

// Keep process alive
setInterval(() => {
    // This keeps the event loop active
}, 10000);
