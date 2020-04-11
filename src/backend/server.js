const express       = require("express");
const path          = require("path");
const mongoose      = require("mongoose");
const bodyParser    = require("body-parser");
const compression   = require('compression')
const passport      = require("passport");
const LocalStrategy = require("passport-local");
const app           = express();
const server        = require('http').createServer(app);
const io            = require('socket.io')(server);
const port          = process.env.PORT || 3000;
const db_uri        = process.env.MONGO_CLUSTER_URI || process.env.MONGO_DOCKER_URI
                       || "mongodb://localhost:27017/devDB";


/* Application Setup */ 
app.use(compression())
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(passport.initialize());
app.use(passport.session());

/* Database Setup */
// Connect to DB
mongoose.connect(db_uri, {
   useNewUrlParser: true,
   useUnifiedTopology: true,
   useCreateIndex: true
 }, (err) => {
    if (err) {
        console.log("Error connecting to the mongo database. \
                     Make sure you are running mongo in another terminal.");
        throw err;
    }
    console.log("DB connected successfully.");
});


// Add default data to DB
require('./server/data/default_data').add_default_data();

// Import Database schema
const User = require('./server/models/User');
const Location = require('./server/models/Location');

// Import Routes
const userRoute = require('./server/routes/user');
const locationRoute = require('./server/routes/location');
const authRoute = require('./server/routes/auth');

//passport.use(new LocalStrategy(User.authenticate()));
passport.use(User.createStrategy());
passport.serializeUser(User.serializeUser());
passport.deserializeUser(User.deserializeUser());


/* Application Routes */
// API Routes
app.use('/auth', authRoute)
app.use('/api/user', userRoute);
app.use('/api/locations', locationRoute);

// If none of the above api matches, send error message
app.use('/api/', (req, res) => {
    res.status(404).send('Invalid api');
});



app.get('/', (req, res) => {
    //res.send("Welcome");
    res.send('GBTH tracking server.');
});

// If none of the above routes matches, send 404 message
app.use('/', (req, res) => {
    res.status(404).send('Page not found');
});


/* Start server */ 
server.listen(port, () => console.log(`GBTH server running at port ${port}.`));


/* socket for updating current status and location */
const nameCache     = require('./server/cache').nameCache;
const statusCache   = require('./server/cache').statusCache;

io.on('connection', (socket) => {
    console.log('User Conncetion');
    socket.emit('all status', get_all_status());


    socket.on('status', (status) => {
        if (status.user_id) {
            // save to cache
            statusCache.set(status.user_id, status);
            if (status.name) { // name field is optional
                nameCache.set(status.user_id, status.name);
            } else {
                name =  nameCache.get(status.user_id);
                status['name'] = name ? name : status.user_id;
            }

            // broadcast status
            io.emit('status', status);
        }
    });
});


function get_all_status() {
    keys = statusCache.keys();
    status = [];

    // If user name is not registered, put user_id in name field
    for (let i = 0; i < keys.length; i++) {
        item = statusCache.get(keys[i]);
        name = nameCache.get(keys[i]);
        item['name'] = name ? name : keys[i];

        status.push(item);
    }

    return status;
}
