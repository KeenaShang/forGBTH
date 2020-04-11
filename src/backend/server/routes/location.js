const express 	= require("express");
const router 	= express.Router();
const Location 	= require('../models/Location');
const check_admin = require('../cache').check_admin;
const check_user   = require('../cache').check_user;

// Query tracked locations
router.get("/", (req, res) => {
    if (!check_admin(req.body.user)) {
        res.status(401).send('Unauthorized Error');
        return;
    }

	// specify queries
	queries = {};
	if (req.query.user) {
		queries['user_id'] = req.query.user;
	}
	if (req.query.from || req.query.to) {
		queries['time'] = {};
		if (req.query.from) {
			if (!isNumber(req.query.from)) {
				return res.status(400).send({ error: "Invalid query: {from} is not a number" });
			}
			queries['time']['$gte'] = parseInt(req.query.from);
		}
		if (req.query.to) {
			if (!isNumber(req.query.to)) {
				return res.status(400).send({ error: "Invalid query: {to} is not a number" });
			}			
			queries['time']['$lte'] = parseInt(req.query.to);
		}
	}

	Location.find(queries, (err, locations) => {
		if (err) return handleError(err);
		res.json(locations);
	});
});


// Save tracked locations
router.post("/", (req, res) => {
	if (!req.body.user_id) {
		return res.status(422).send({ error: "Invalid format: user_id is empty" });
	}
    if (!check_user(req.body.user)) {
        res.status(401).send('Unauthorized Error');
        return;
    }	

	// check JSON format and create LocationSchema
	const newLocations = [];
	const locations = req.body.locations;
	for (const loc_key in locations) {
		const keys = Object.keys(locations[loc_key]);
		if (keys.length != 3) {
			return res.status(422).send({ error: "Invalid JSON format" });
		}
		const lat = locations[loc_key][keys[0]];
		const long = locations[loc_key][keys[1]];
		const t = locations[loc_key][keys[2]];

		if (isNaN(lat) || isNaN(long) || isNaN(t)) {
			return res.status(422).send({ error: "Invalid JSON format" });
		}

		newLocations.push(new Location({
	        user_id: req.body.user_id,
	        latitude: lat,
	        longitude: long,
	        time: t,
		}));
	}

	newLocations.forEach(loc => {
	    loc.save((err) => {
	        if (err) return handleError(err);
	    });
	})

	res.send('Locations are saved successfully');
});

// Delete tracked locations
router.delete("/", (req, res) => {
    if (!check_admin(req.body.user)) {
        res.status(401).send('Unauthorized Error');
        return;
    }

	// specify queries
	queries = {};
	if (req.query.user) {
		queries['user_id'] = req.query.user;
	}
	if (req.query.from || req.query.to) {
		queries['time'] = {};
		if (req.query.from) {
			if (!isNumber(req.query.from)) {
				return res.status(400).send({ error: "Invalid query: {from} is not a number" });
			}
			queries['time']['$gte'] = parseInt(req.query.from);
		}
		if (req.query.to) {
			if (!isNumber(req.query.to)) {
				return res.status(400).send({ error: "Invalid query: {to} is not a number" });
			}			
			queries['time']['$lte'] = parseInt(req.query.to);
		}
	}

	Location.deleteMany(queries, (err, result) => {
		if (err) {
			res.send(err);
		} else {
			res.send(result);
		}
	});
});


function isNumber(value) {
	return /^-?\d+\.?\d*$/.test(value);
};

module.exports = router;
