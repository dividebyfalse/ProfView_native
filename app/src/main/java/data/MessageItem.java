package data;

/**
 * Created by penpen on 27.11.15.
 */
public class MessageItem {
    private String message;
    private String date;
    private Boolean isnew;

    public MessageItem() {

    }

    public MessageItem(String message, String date, Boolean isnew) {
        super();
        this.message = message;
        this.date = date;
        this.isnew = isnew;
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
}
