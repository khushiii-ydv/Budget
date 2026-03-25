const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const { query, seedUserData } = require('../config/db');
const logger = require('../middleware/logger');

// In-memory OTP store (for simplicity in this phase)
const otps = {};

/**
 * @route   POST api/auth/login
 * @desc    Check if user exists and send OTP
 * @access  Public
 */
exports.login = async (req, res) => {
    const { mobileNumber, password } = req.body;

    if (!mobileNumber || !password) {
        return res.status(400).json({ success: false, message: 'Please provide mobile number and password' });
    }

    try {
        const userResult = await query('SELECT * FROM users WHERE mobile_number = $1', [mobileNumber]);
        
        if (userResult.rows.length === 0) {
            logger.warn(`[AUTH] Login attempt for non-existent user: ${mobileNumber}`);
            return res.status(401).json({ success: false, message: 'Invalid mobile number or password' });
        }

        const user = userResult.rows[0];
        const isMatch = await bcrypt.compare(password, user.password);
        
        if (!isMatch) {
            // Special case for seed user if they forgot password but want to use 'hello'
            if (mobileNumber === '9307136478' && password === 'hello') {
                logger.info(`[AUTH] Seed user logging in with fallback password`);
            } else {
                logger.warn(`[AUTH] Password mismatch for ${mobileNumber}`);
                return res.status(401).json({ success: false, message: 'Invalid mobile number or password' });
            }
        }

        // Mocking OTP send
        const otp = '1234';
        otps[mobileNumber] = { otp, userId: user.id, userName: user.name };
        logger.info(`[AUTH] Login OTP for ${mobileNumber}: ${otp}`);

        return res.status(200).json({
            success: true,
            message: 'OTP sent to your mobile number'
        });
    } catch (error) {
        logger.error(`[AUTH] Login Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};

/**
 * @route   POST api/auth/register
 * @desc    Register a new user and send OTP
 * @access  Public
 */
exports.register = async (req, res) => {
    const { name, mobileNumber, password } = req.body;

    if (!name || !mobileNumber || !password) {
        return res.status(400).json({ success: false, message: 'Please fill all fields' });
    }

    try {
        // Check if user already exists
        const userExists = await query('SELECT * FROM users WHERE mobile_number = $1', [mobileNumber]);
        if (userExists.rows.length > 0) {
            logger.warn(`[AUTH] Register failed: User ${mobileNumber} already exists`);
            return res.status(400).json({ success: false, message: 'User already registered' });
        }

        const otp = '1234';
        otps[mobileNumber] = { otp, name, password }; // Store temp registration data
        logger.info(`[AUTH] Register OTP for ${mobileNumber}: ${otp} (Data stored for verification)`);

        return res.status(200).json({
            success: true,
            message: 'OTP sent successfully'
        });
    } catch (error) {
        logger.error(`[AUTH] Register Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};

/**
 * @route   POST api/auth/verify-otp
 * @desc    Verify OTP and complete registration/login
 * @access  Public
 */
exports.verifyOtp = async (req, res) => {
    const { mobileNumber, otp } = req.body;

    if (!mobileNumber || !otp) {
        return res.status(400).json({ success: false, message: 'Please provide mobile number and OTP' });
    }

    try {
        const otpData = otps[mobileNumber];

        if (otpData && otpData.otp === otp) {
            let userId;
            let userName;

            if (otpData.userId) {
                // Scenario: Login verification
                userId = otpData.userId;
                userName = otpData.userName;
            } else {
                // Scenario: Registration verification
                const salt = await bcrypt.genSalt(10);
                const hashedPassword = await bcrypt.hash(otpData.password, salt);

                const newUser = await query(
                    'INSERT INTO users (name, mobile_number, password) VALUES ($1, $2, $3) RETURNING id',
                    [otpData.name, mobileNumber, hashedPassword]
                );
                userId = newUser.rows[0].id;
                userName = otpData.name;
            }

            // Seed data ONLY for the specific account requested by user
            if (mobileNumber === '9307136478') {
                await seedUserData(userId);
            }

            delete otps[mobileNumber];

            // Generate JWT
            const token = jwt.sign(
                { user: { id: userId, name: userName, mobileNumber: mobileNumber } },
                process.env.JWT_SECRET || 'secret',
                { expiresIn: '7d' }
            );

            return res.status(200).json({
                success: true,
                message: 'Success',
                token,
                user: { id: userId, name: userName, mobileNumber: mobileNumber }
            });
        } else {
            logger.warn(`[AUTH] OTP Verification failed for ${mobileNumber}. Expected: ${otpData?.otp}, Received: ${otp}`);
            return res.status(400).json({ success: false, message: 'Invalid or expired OTP' });
        }
    } catch (error) {
        logger.error(`[AUTH] Verify OTP Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};

/**
 * @route   GET api/auth/profile
 * @desc    Get current user profile
 * @access  Private
 */
exports.getProfile = async (req, res) => {
    const userId = req.user.id;
    try {
        const userResult = await query('SELECT id, name, mobile_number, monthly_income, currency, created_at FROM users WHERE id = $1', [userId]);
        if (userResult.rows.length === 0) {
            return res.status(404).json({ success: false, message: 'User not found' });
        }
        res.json({ success: true, user: userResult.rows[0] });
    } catch (error) {
        logger.error(`[AUTH] Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};

/**
 * @route   POST api/auth/profile/update
 * @desc    Update user profile settings
 * @access  Private
 */
exports.updateProfile = async (req, res) => {
    const userId = req.user.id;
    const { monthly_income, currency } = req.body;
    try {
        await query(
            'UPDATE users SET monthly_income = $1, currency = $2 WHERE id = $3',
            [monthly_income, currency, userId]
        );
        res.json({ success: true, message: 'Profile updated successfully' });
    } catch (error) {
        logger.error(`[AUTH] Error: ${error.message}`, error);
        res.status(500).send('Server Error');
    }
};
