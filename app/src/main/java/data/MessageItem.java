package data;

/**
 * Created by penpen on 27.11.15.
 */
public class MessageItem {
    private String message;
    private String date;
    private Boolean isnew;
    private Boolean isnews;
    private String profile_pic;
    private String profile_name;
    private String image;

    public MessageItem(String message, String date, Boolean isnew, Boolean isnews, String image, String profile_name, String profile_pic) {
        super();
        this.message = message;
        this.date = date;
        this.isnew = isnew;
        this.isnews = isnews;
        this.image = image;
        this.profile_name = profile_name;
        this.profile_pic = profile_pic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsnew() {
        return isnew;
    }

    public void setIsnew(Boolean isnew) {
        this.isnew = isnew;
    }

    public Boolean getIsnews() {
        return isnews;
    }

    public void setIsnews(Boolean isnews) {
        this.isnews = isnews;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
