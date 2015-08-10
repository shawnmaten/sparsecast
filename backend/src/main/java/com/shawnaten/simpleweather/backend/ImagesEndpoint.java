package com.shawnaten.simpleweather.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "imagesApi",
        version = "v1",
        resource = "images",
        namespace = @ApiNamespace(
                ownerDomain = "backend.simpleweather.shawnaten.com",
                ownerName = "backend.simpleweather.shawnaten.com",
                packagePath = ""
        ),
        clientIds = {
                Constants.WEB_LOCAL_ID,
                Constants.WEB_APP_ENGINE_ID,
                Constants.ANDROID_DEBUG_ID,
                Constants.ANDROID_RELEASE_ID
        },
        audiences = {Constants.WEB_LOCAL_ID, Constants.WEB_APP_ENGINE_ID}
)
public class ImagesEndpoint {
    public static final String CATEGORY = "category";
    public static final String POST_URL = "postUrl";
    public static final String IMAGE_URL = "imageUrl";

    public static final String CLEAR_DAY = "clearDay";
    public static final String CLEAR_NIGHT = "clearNight";
    public static final String RAIN = "rain";
    public static final String SNOW = "snow";
    public static final String SLEET = "sleet";
    public static final String WIND = "wind";
    public static final String FOG = "fog";
    public static final String CLOUDY = "cloudy";
    public static final String PARTLY_CLOUDY_DAY = "partlyCloudyDay";
    public static final String PARTLY_CLOUDY_NIGHT  = "partlyCloudyNight";
    public static final String HAIL = "hail";
    public static final String THUNDERSTORM = "thunderstorm";
    public static final String TORNADO = "tornado";
    public static final String DEFAULT = "default";

    private static final Logger logger = Logger.getLogger(ImagesEndpoint.class.getName());

    @ApiMethod(name = "getImage")
    public Image getImages(User user, @Named("category") String category)
            throws OAuthRequestException {
        if (user != null) {
            Random random = new Random();
            List<Image> images = OfyService.ofy().load().type(Image.class)
                    .filter("category", category).list();
            if (images.size() > 0) {
                return images.get(random.nextInt(images.size()));
            } else {
                Image defaultImage = new Image();
                defaultImage.setCategory("default");
                defaultImage.setShortcode("q-GYnWOuZO");
                return defaultImage;
            }
        } else
            throw new OAuthRequestException("unauthorized request");
    }

    @ApiMethod(name = "insertImage")
    public Image insertImages(User user, Image image) throws OAuthRequestException {
        if (user != null) {
            OfyService.ofy().save().entity(image).now();
            return image;
        } else
            throw new OAuthRequestException("unauthorized request");
    }
}