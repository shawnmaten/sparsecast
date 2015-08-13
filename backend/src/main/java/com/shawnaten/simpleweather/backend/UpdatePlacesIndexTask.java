package com.shawnaten.simpleweather.backend;

import com.google.appengine.api.taskqueue.DeferredTask;

import java.util.List;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

public class UpdatePlacesIndexTask implements DeferredTask {

    @Override
    public void run() {

        List<SavedPlace> places = ofy().load().type(SavedPlace.class).list();

        for (SavedPlace place : places) {

            ofy().delete().entity(place).now();
            SavedPlace newPlace = new SavedPlace();
            newPlace.setUserId(place.getUserId());
            newPlace.setPlaceId(place.getPlaceId());
            newPlace.setLat(place.getLat());
            newPlace.setLng(place.getLng());
            newPlace.setName(place.getName());
            ofy().save().entity(newPlace).now();

        }
    }

}
