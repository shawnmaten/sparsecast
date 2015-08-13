package com.shawnaten.simpleweather.backend;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.shawnaten.simpleweather.lib.model.Places;

import java.util.List;

import javax.inject.Inject;

import static com.shawnaten.simpleweather.backend.OfyService.ofy;

public class UpdatePlacesTask implements DeferredTask {

    @Inject
    Places.DetailsService service;

    @Override
    public void run() {
        Dagger.getLocationComp().injectUpdatePlacesTask(this);

        List<SavedPlace> places = ofy().load().type(SavedPlace.class).list();

        for (SavedPlace place : places) {
            if (place.getName() == null) {
                Places.DetailsResponse info = service.getDetails(
                       Constants.PUBLIC_GOOGLE_API_KEY,
                        place.getPlaceId(),
                        "en"
                );

                place.setName(info.getResult().getName());
                place.setLat(info.getResult().getGeometry().getLocation().getLat());
                place.setLng(info.getResult().getGeometry().getLocation().getLng());

                ofy().save().entity(place);
            }
        }
    }

}
