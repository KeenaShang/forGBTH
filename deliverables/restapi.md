#### Note: All API calls must include `"user": {"id": , "token": }` field.  
#### Some API calls require admin privillage.

### GET tracked locations queried by (user_id, period of time); this is for creating csv file [admin privillage]
 ``` GET /api/locations?user={user_id}&from={time}&to={time} ```
 ```json
 [
    {"user_id": "???", "latitude": 43.651070, "longitude": -79.347015, "time": 1479249799770},
 ...]
 ```  
 
### DELETE tracked locations queried by (user_id, period of time) [admin privillage]
 ``` DELETE /api/locations?user={user_id}&from={time}&to={time} ```


### POST tracked locations
``` POST /api/locations ```
 ```json
{"user_id": "???", "locations": [[(lat), (long), (time)], ...]}
```


### GET all users [admin privillage]
``` GET /api/user/{user_id}/name ```
 ```json
[
    {"user_id": "user1", "name": "Derek", "is_admin": true},
 ...]
```

### GET user name
``` GET /api/user/{user_id}/name ```
 ```json
{"name": "Derek"}
```

### PUT user name
``` PUT /api/user/{user_id}/name ```
 ```json
{"name": "Derek"}
```

### PUT user is_admin [admin privillage]
``` PUT /api/user/{user_id}/is_admin ```
 ```json
{"is_admin": false}
```

### PUT password [admin privillage]
``` PUT /api/user/{user_id}/password ```
 ```json
{"password": "new pw"}
```

### DELETE user [admin privillage]
``` DELETE /api/user/{user_id} ```


### POST register [admin privillage]
``` POST /auth/register ```
 ```json
{"user_id": "user1", "password": "pw123", "is_admin": false}
```


### POST login
``` POST /auth/login ```
 ```json
{"user_id": "user1", "password": "pw123"}
```


### GET logout
``` GET /auth/logout ```

