const mongoose = require("mongoose");
const passportLocalMongoose = require("passport-local-mongoose");

const UserSchema = mongoose.Schema(
    {
        user_id: { type : String, required : true, dropDups: true },
        name: String,
        is_admin: { type: Boolean, default: false },
    }
);

UserSchema.plugin(passportLocalMongoose, {
	usernameField: 'user_id',
	usernameUnique: true
});

module.exports = mongoose.model("User", UserSchema);