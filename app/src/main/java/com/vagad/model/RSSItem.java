package com.vagad.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * This class handle RSS Item <item> node in rss xml
 */
public class RSSItem implements Parcelable {

    // All <item> node name
    String _title;
    String _link;
    String _description;
    String _pubdate;
    String _guid;
    String _image;

    public String get_news_type() {
        return _news_type;
    }

    public void set_news_type(String _news_type) {
        this._news_type = _news_type;
    }

    String _news_type;

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    boolean isFav;

    public int getId() {
        return id;
    }

    private int id;

    public String getImage() {
        return _image;
    }

    public void setImage(String _image) {
        this._image = _image;
    }


    // constructor
    public RSSItem() {

    }

    // constructor with parameters
    public RSSItem(String title, String link, String description, String pubdate, String guid, String image, String news_type) {
        this._title = title;
        this._link = link;
        this._description = description;
        this._pubdate = pubdate;
        this._guid = guid;
        this._image = image;
        this._news_type = news_type;
    }

    /**
     * All SET methods
     */
    public void setTitle(String title) {
        this._title = title;
    }

    public void setLink(String link) {
        this._link = link;
    }

    public void setDescription(String description) {
        this._description = description;
    }

    public void setPubdate(String pubDate) {
        this._pubdate = pubDate;
    }


    public void setGuid(String guid) {
        this._guid = guid;
    }

    /**
     * All GET methods
     */
    public String getTitle() {
        return this._title;
    }

    public String getLink() {
        return this._link;
    }

    public String getDescription() {
        return this._description;
    }

    public String getPubdate() {
        return this._pubdate;
    }

    public String getGuid() {
        return this._guid;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected RSSItem(Parcel in) {
        _title = in.readString();
        _link = in.readString();
        _description = in.readString();
        _pubdate = in.readString();
        _guid = in.readString();
        _image = in.readString();
        _news_type = in.readString();
        isFav = in.readByte() != 0x00;
        id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_title);
        dest.writeString(_link);
        dest.writeString(_description);
        dest.writeString(_pubdate);
        dest.writeString(_guid);
        dest.writeString(_image);
        dest.writeString(_news_type);
        dest.writeByte((byte) (isFav ? 0x01 : 0x00));
        dest.writeInt(id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RSSItem> CREATOR = new Parcelable.Creator<RSSItem>() {
        @Override
        public RSSItem createFromParcel(Parcel in) {
            return new RSSItem(in);
        }

        @Override
        public RSSItem[] newArray(int size) {
            return new RSSItem[size];
        }
    };
}
