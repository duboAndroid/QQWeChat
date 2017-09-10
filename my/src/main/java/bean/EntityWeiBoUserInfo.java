package bean;

public class EntityWeiBoUserInfo {
    private int id;
    private String screen_name;//用户昵称
    private String name;//友好显示名称
    private String province;
    private String city;
    private String location;
    private String description;
    private String url;
    private String profile_image_url;
    private String domain;
    private String gender;
    private int followers_count;
    private int friends_count;
    private int statuses_count;
    private int favourites_count;
    private String created_at;
    private boolean following;
    private boolean allow_all_act_msg;
    private boolean geo_enabled;
    private boolean verified;
    private Status status;
    private boolean allow_all_comment;
    private String avatar_large;//用户头像地址（大图），180×180像素
    private String verified_reason;
    private boolean follow_me;
    private int online_status;
    private int bi_followers_count;

    public int getId() {
        return id;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public String getDomain() {
        return domain;
    }

    public String getGender() {
        return gender;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public int getFriends_count() {
        return friends_count;
    }

    public int getStatuses_count() {
        return statuses_count;
    }

    public int getFavourites_count() {
        return favourites_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public boolean isFollowing() {
        return following;
    }

    public boolean isAllow_all_act_msg() {
        return allow_all_act_msg;
    }

    public boolean isGeo_enabled() {
        return geo_enabled;
    }

    public boolean isVerified() {
        return verified;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isAllow_all_comment() {
        return allow_all_comment;
    }

    public String getAvatar_large() {
        return avatar_large;
    }

    public String getVerified_reason() {
        return verified_reason;
    }

    public boolean isFollow_me() {
        return follow_me;
    }

    public int getOnline_status() {
        return online_status;
    }

    public int getBi_followers_count() {
        return bi_followers_count;
    }

    public class Status {
        private String created_at;
        private int id;
        private String text;
        private String source;
        private boolean favorited;
        private boolean truncated;
        private String in_reply_to_status_id;
        private String in_reply_to_user_id;
        private String in_reply_to_screen_name;
        private String geo;
        private String mid;
        private int reposts_count;
        private int comments_count;

        public String getCreated_at() {
            return created_at;
        }

        public int getId() {
            return id;
        }

        public String getText() {
            return text;
        }

        public String getSource() {
            return source;
        }

        public boolean isFavorited() {
            return favorited;
        }

        public boolean isTruncated() {
            return truncated;
        }

        public String getIn_reply_to_status_id() {
            return in_reply_to_status_id;
        }

        public String getIn_reply_to_user_id() {
            return in_reply_to_user_id;
        }

        public String getIn_reply_to_screen_name() {
            return in_reply_to_screen_name;
        }

        public String getGeo() {
            return geo;
        }

        public String getMid() {
            return mid;
        }

        public int getReposts_count() {
            return reposts_count;
        }

        public int getComments_count() {
            return comments_count;
        }
    }
}