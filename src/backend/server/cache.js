const NodeCache     = require("node-cache");
const statusCache   = new NodeCache( { stdTTL: 0, checkperiod: 0 } );
const nameCache     = new NodeCache( { stdTTL: 0, checkperiod: 0 } );
const tokenCache    = new NodeCache( { stdTTL: 12*60*60, checkperiod: 6*60*60 } );


module.exports.statusCache = statusCache;
module.exports.nameCache = nameCache;
module.exports.tokenCache = tokenCache;
module.exports.check_admin = function(admin) {
	// return admin && (tokenCache.get(admin.id) != null && tokenCache.get(admin.id).is_admin
	//  && tokenCache.get(admin.id).token == admin.token);
	return true;
};
module.exports.check_user = function(user) {
	// return user && (tokenCache.get(user.id) != null
	//  && tokenCache.get(user.id).token == user.token);
	return true;
};