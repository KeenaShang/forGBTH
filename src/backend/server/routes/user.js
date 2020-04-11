const express   = require("express");
const router    = express.Router();
const User  = require('../models/User');
const nameCache = require('../cache').nameCache;
const tokenCache = require('../cache').tokenCache;
const check_admin   = require('../cache').check_admin;
const check_user   = require('../cache').check_user;


// Get all users
router.get("/", (req, res) => {
    if (!check_admin(req.body.user)) {
        res.status(401).send('Unauthorized Error');
        return;
    }

    User.find({}, (err, users) => {
        if (err) return handleError(err);
        res.json(users);
    });
});

// Get user name
router.get("/:id/name", (req, res) => {
    if (!check_user(req.body.user)) {
        res.status(401).send('Unauthorized Error');
        return;
    }

    User.findOne({ user_id: req.params.id }, (err, user) => {
        if (err) return handleError(err);
        if (user) {
            res.json({name: user.name})
        } else {
            res.status(404).send('User is not registered');
        }
    });
});

// Update user name
router.put("/:id/name", (req, res) => {
    if (!check_user(req.body.user)) {
        res.status(401).send('Unauthorized Error');
        return;
    }

    User.findOne({ user_id: req.params.id }, (err, user) => {
        if (err) return handleError(err);
        if (user) {
            // update user name
            user.name = req.body.name;
            user.save((err, savedUser) => {
                if (err) return handleError(err);

                nameCache.set(req.params.id, req.body.name);
                res.send('Updated user name: ' + savedUser.name);
            });
        } else {
            res.status(404).send('User is not registered');
        }
    });
});

// Update is_admin of user
router.put("/:id/is_admin", (req, res) => {
    if (!check_admin(req.body.user)) {
        res.status(401).send('Unauthorized Error');
        return;
    }

    User.findOne({ user_id: req.params.id }, (err, user) => {
        if (err) return handleError(err);
        if (user) {
            // update user name
            user.is_admin = req.body.is_admin;
            user.save((err, savedUser) => {
                if (err) return handleError(err);

                if (tokenCache.get(req.params.id)) {
                    const data = {is_admin: req.body.is_admin, 
                                  token: tokenCache.get(req.params.id).token};
                    tokenCache.set(user_id, data);
                }

                res.send('Updated user is_admin: ' + savedUser.is_admin);
            });
        } else {
            res.status(404).send('User is not registered');
        }
    });
});

// Update user password
router.put("/:id/password", (req, res) => {
    if (!check_admin(req.body.user)) {
        res.status(401).send('Unauthorized Error');
        return;
    }

    User.findOne({ user_id: req.params.id }, (err, user) => {
        if (err) return handleError(err);
        if (user) {
            user.setPassword(req.body.password, (err) => {  
                user.save((err, savedUser) => {
                    if (err) return handleError(err);
                    res.send('Updated user password');
                });
            });
        } else {
            res.status(404).send('User is not registered');
        }
    });
});

// Delete user
router.delete("/:id", (req, res) => {
    if (!check_admin(req.body.user)) {
        res.status(401).send('Unauthorized Error');
        return;
    } else if  (req.params.id == 'admin') {
        res.status(400).send('Cannot remove this user.');
        return;
    }

    User.deleteOne({ user_id: req.params.id }, function(err, result) {
        if (err) return handleError(err);

        tokenCache.del(req.params.id);
        res.send(result);
    });
});


module.exports = router;
