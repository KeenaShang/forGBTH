const express    = require("express");
const router     = express.Router();
const passport   = require("passport");
const User       = require('../models/User');
const tokenCache = require('../cache').tokenCache;
const check_admin   = require('../cache').check_admin;


// Register with user_id and password
router.post('/register', function(req, res, next) {
	const admin = req.body.user;
	const user_id = req.body.user_id;
	const password = req.body.password;
	const is_admin = req.body.is_admin;

	if (!check_admin(admin)) {
		res.status(401).send('Unauthorized Error');
		return;
	}

	User.register(new User({ user_id: user_id, name: user_id, is_admin: is_admin }), password, (err) => {
		if (err) {
			res.status(400).json({ err: err });
		} else {
			passport.authenticate('local')(req, res, () => {
				res.send('Registered successfully.');
			});
		}
	});
});

// Login with user_id and password
router.post('/login', passport.authenticate('local', {failWithError: true}), 
	(req, res, next) => {
		const user_id = req.user.user_id
		User.findOne({user_id: user_id}, (err, user) => {
			if(err) {
				return next(err);
			}
			else {
				if (tokenCache.get(user_id)) {
					// res.status(409).send('Already logged in.');
					const data = {is_admin: user.is_admin, token: tokenCache.get(user_id).token}
					tokenCache.set(user_id, data);
					res.json(data);
				} else {
					const data = {is_admin: user.is_admin, token: Math.random()}
					tokenCache.set(user_id, data);
					res.json(data);
				}

			}
		});	
	},
	(err, req, res, next) => {
		res.json({ err: err });
	}
);

// Logout
router.get('/logout', function(req, res) {
	const user_id = req.body.user.id;
	const token = req.body.user.token;

	if (user_id == null || token == null) {
		res.status(400).send('Specify user_id and token')
	} else if (tokenCache.get(user_id) == null) {
		res.end()
	} else if (tokenCache.get(user_id).token == token) {
		tokenCache.del(req.body.user_id);
		req.logout();
		res.send('Logout successfully');
	} else {
		res.status(401).send('Unauthorized Error')
	}
});


module.exports = router;
