package im.point.torgash.virtualbrest;

import java.io.Serializable;

/**
 * Created by torgash on 03.02.15.
 */
public class MyRssItem implements Serializable{
    public String mTitle;
    public String mDescription;
    public String mPictureLink;
    public String mNewsLink;
    public String mFeedIdentify;

    public MyRssItem(String title, String description, String newsLink, String feedIndentify) {
        mTitle = title;
        mDescription = description;
        mNewsLink = newsLink;
        mFeedIdentify = feedIndentify;
    }

    public MyRssItem(String title, String description, String newsLink, String pictureLink, String feedIdentify) {
        mTitle = title;
        mDescription = description;
        mNewsLink = newsLink;
        mPictureLink = pictureLink;
        mFeedIdentify = feedIdentify;
    }
}
