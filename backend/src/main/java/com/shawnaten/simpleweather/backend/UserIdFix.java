package com.shawnaten.simpleweather.backend;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

public class UserIdFix {
    public static String getUserId(User user) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity userEntity = new com.google.appengine.api.datastore.Entity("User");
        userEntity.setProperty("user", user);
        Key userKey = datastore.put(userEntity);
        userEntity = datastore.get(userKey);
        datastore.delete(userKey);
        user = (User) userEntity.getProperty("user");
        return user.getUserId();
    }
}
