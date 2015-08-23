package com.shawnaten.simpleweather.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.shawnaten.simpleweather.backend.model.GCMRecord;
import com.shawnaten.simpleweather.backend.model.Image;
import com.shawnaten.simpleweather.backend.model.Prefs;
import com.shawnaten.simpleweather.backend.model.SavedPlace;

public class OfyService {

    static {
        ObjectifyService.register(Image.class);
        ObjectifyService.register(SavedPlace.class);
        ObjectifyService.register(GCMRecord.class);
        ObjectifyService.register(Prefs.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
