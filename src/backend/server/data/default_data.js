const mongoose		= require("mongoose");
const User 		= require('../models/User');
const Location 		= require('../models/Location');
const nameCache 	= require('../cache').nameCache;
const statusCache 	= require('../cache').statusCache;

_user_ids = ["id1", "id2", "id3"]

_users = [
	// {user_id: "id1", name: "P1"},
	// {user_id: "id2", name: "P2"},
	// {user_id: "id3", name: "T1"},
];

_locations = [
	{user_id: "id1", latitude: 43.651070, longitude: -79.347015, time: 1583766000000},
	{user_id: "id1", latitude: 43.651070, longitude: -80.347015, time: 1583769600000},

	{user_id: "id2", latitude: 43.651070, longitude: -81.347015, time: 1583758800000},
	{user_id: "id2", latitude: 41.651070, longitude: -80.347015, time: 1583762400000},
	{user_id: "id2", latitude: 42.651070, longitude: -80.347015, time: 1583766000000},

	{user_id: "id3", latitude: 43.551164, longitude: -80.347015, time: 1583758800000},
	{user_id: "id3", latitude: 41.551164, longitude: -80.347015, time: 1583762400000},
	{user_id: "id3", latitude: 42.551164, longitude: -80.347015, time: 1583766000000},
];

_status = [
	// {user_id: "id1", latitude: 43.659583, longitude: -79.39722, status: 1, time: 1583769600000}, 
	// {user_id: "id2", latitude: 43.6687,   longitude: -79.3853,  status: 2, time: 1583769600000}, 
	// {user_id: "id3", latitude: 43.6626,   longitude: -79.3933,  status: 3, time: 1583769600000}, 
]


async function add_default_data() {
	// delete default data to avoid duplicity
	// User.deleteMany({user_id: {$in: _user_ids}}, (err) => {
	User.deleteMany({}, (err) => {	
		if (err) throw err;

		_users.forEach(user => {
			User.create(user, (err) => { if (err) throw err; });
			nameCache.set(user.user_id, user.name);
		})
	});

	//Location.deleteMany({user_id: {$in: _user_ids}}, (err) => {
	Location.deleteMany({}, (err) => {
		if (err) throw err;

		_locations.forEach(location => {
			Location.create(location, (err) => { if (err) throw err; });
		})
	});

	// add status to cache
	for (let i = 0; i < _status.length; i++) {
		statusCache.set(_status[i]['user_id'], _status[i]);
	}

	// add default admin
	const admin_id = "admin";
	const admin_pw = "admin";
	User.register(new User({ user_id: admin_id, name: admin_id, is_admin: true }), admin_pw, (err) => {
		if (err) throw err;
	});
}

module.exports.add_default_data = add_default_data;