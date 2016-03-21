package jkpawl.septimasoftware.com.cloudynote;

/**
 * Created by J.Pawluczuk on 3/21/16.
 */
public class Note {

    private String mTitle;
    private String mMessage;
    private String mDate;
    private Integer mColor;
    private Integer mId;

    public Integer getColor() {
        return mColor;
    }

    public void setColor(Integer color) {
        this.mColor = color;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String msg) {
        this.mMessage = msg;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }
}
