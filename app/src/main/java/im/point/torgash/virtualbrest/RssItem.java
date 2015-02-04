package im.point.torgash.virtualbrest;

import android.graphics.drawable.Drawable;

/**
 * Created by torgash on 03.02.15.
 */
public class RssItem {
    public String mTitle;
    public String mDescription;

    public String mPictureLink;
    public RssItem(String title, String description, String pictureLink){
        mTitle = title;
        mDescription = description;
        mPictureLink = pictureLink;
    }
}
