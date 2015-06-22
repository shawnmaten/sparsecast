package com.shawnaten.tools;

import com.google.gson.annotations.SerializedName;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public class Instagram {
    public static class Envelope {
        protected Meta meta;
        protected Pagination pagination;

        public Meta getMeta() {
            return meta;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }

    public static class Meta {
        @SerializedName("error_type") protected String errorType;
        protected int code;
        @SerializedName("error_message") protected String errorMessage;

        public String getErrorType() {
            return errorType;
        }

        public int getCode() {
            return code;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static class Pagination {
        @SerializedName("next_url") protected String nextUrl;
        @SerializedName("next_max_id") protected long nextMaxId;

        public String getNextUrl() {
            return nextUrl;
        }

        public long getNextMaxId() {
            return nextMaxId;
        }
    }

    public static class MediaResponse extends Envelope {
        protected MediaData data[];

        public MediaData[] getData() {
            return data;
        }
    }

    public static class UserResponse extends Envelope {
        protected UserData data;

        public UserData getData() {
            return data;
        }
    }

    public static class SingleMediaResponse extends Envelope {
        protected MediaData data;

        public MediaData getData() {
            return data;
        }
    }

    public static class MediaData {
        protected String type;
        protected String tags[];
        protected Likes likes;
        protected String link;
        protected Images images;
        protected UserData user;

        public String getType() {
            return type;
        }

        public String[] getTags() {
            return tags;
        }

        public Likes getLikes() {
            return likes;
        }

        public String getLink() {
            return link;
        }

        public Images getImages() {
            return images;
        }

        public static class Likes {
            protected long count;

            public long getCount() {
                return count;
            }
        }

        public UserData getUser() {
            return user;
        }

        public static class Images {
            @SerializedName("low_resolution") protected Image lowResolution;
            protected Image thumbnail;
            @SerializedName("standard_resolution") protected Image standardResolution;

            public Image getLowResolution() {
                return lowResolution;
            }

            public Image getThumbnail() {
                return thumbnail;
            }

            public Image getStandardResolution() {
                return standardResolution;
            }

            public static class Image {
                private String url;
                private long width;
                private long height;

                public String getUrl() {
                    return url;
                }

                public long getWidth() {
                    return width;
                }

                public long getHeight() {
                    return height;
                }
            }
        }
    }

    public static class UserData {
        protected long id;
        protected String username;
        @SerializedName("full_name") protected String fullName;
        @SerializedName("profile_picture") protected String profilePicture;
        protected String bio;
        protected String website;
        protected Counts counts;

        public long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getFullName() {
            return fullName;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public String getBio() {
            return bio;
        }

        public String getWebsite() {
            return website;
        }

        public Counts getCounts() {
            return counts;
        }

        public static class Counts {
            protected long media;
            protected long follows;
            @SerializedName("followed_by") protected long followedBy;

            public long getMedia() {
                return media;
            }

            public long getFollows() {
                return follows;
            }

            public long getFollowedBy() {
                return followedBy;
            }
        }
    }

    public interface Service {
        @GET("/tags/{tag}/media/recent")
        Instagram.MediaResponse getTagged(
                @Path("tag") String tag,
                @Query("count") long count,
                @Query("min_tag_id") long minTagId,
                @Query("max_tag_id") long maxTagId,
                @Query("client_id") String key
        );

        @GET("/users/{user-placeId}/")
        Observable<Instagram.UserResponse> getUser(
                @Path("user-placeId") long id,
                @Query("client_id") String key
        );

        @GET("/media/popular")
        Observable<Instagram.MediaResponse> getPopular(
                @Query("min_tag_id") long minTagId,
                @Query("max_tag_id") long maxTagId,
                @Query("client_id") String key
        );

        @GET("/media/search")
        Observable<Instagram.MediaResponse> getNearby(
                @Query("lat") Double lat,
                @Query("lng") Double lng,
                @Query("min_timestamp") long minTimestamp,
                @Query("max_timestamp") long maxTimestamp,
                @Query("distance") long distance,
                @Query("count") long count,
                @Query("client_id") String key
        );

        @GET("/media/shortcode/{shortcode}")
        Instagram.SingleMediaResponse getMedia(
                @Query("client_id") String key,
                @Path("shortcode") String shortcode

        );
    }
}
