const mongoose = require("mongoose");

const LocationSchema = mongoose.Schema(
    {
        user_id: { type : String, required : true },
        latitude: Number,
        longitude: Number,
        time: Number,
    }
);

module.exports = mongoose.model("Location", LocationSchema);
