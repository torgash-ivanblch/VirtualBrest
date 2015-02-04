package im.point.torgash.virtualbrest;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by torgash on 03.02.15.
 */
public class RssAdapter extends ArrayAdapter<RssItem> {
    private static final String TAG = "VIRTBREST";
    Context contex;

    public RssAdapter(Context context, ArrayList<RssItem> users) {
        super(context, 0, users);
        contex = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RssItem rssItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rss_list_item, parent, false);
        }
        // Lookup view for data population
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvFeedTitle);
//        TextView tvDescription = (Tex.error(R.drawable.user_placeholder_error)tView) convertView.findViewById(R.id.tvDescription);
        final ImageView ivPicture = (ImageView) convertView.findViewById(R.id.ivNewsLogo);
        // Populate the data into the template view using the data object
        tvTitle.setText(rssItem.mTitle);
//        tvDescription.setText(rssItem.mDescription);
        new AsyncTask<String, String, String>() {


            @Override
            protected String doInBackground(String... params) {


                Document doc = null;
                try

                {
                    doc = Jsoup.connect(params[0]).get();
                } catch (IOException e)

                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
                org.jsoup.nodes.Element ele = null;
                String imageLink = "";
                if (null != doc) {
                    ele = doc.select("#text").get(1);
                    if (!ele.select("img[src]").isEmpty()) {

                        imageLink = ele.select("img[src]").get(0).absUrl("src");

                    }
                    return imageLink;
                }

                return "";

            }


            @Override
            protected void onPreExecute() {


            }

            @Override
            protected void onPostExecute(String s) {
                Picasso.with(contex).setIndicatorsEnabled(true);
                try {
                    Picasso.with(contex).load(s).placeholder(R.drawable.rectangle).error(R.drawable.rectangle).into(ivPicture);
                }catch(IllegalArgumentException e) {
                    Picasso.with(contex).load(R.drawable.rectangle).into(ivPicture);
                }
//            sAdapter.notifyDataSetChanged();

            }

            @Override
            protected void onProgressUpdate(String... values) {

            }

        }

                .

                        execute(rssItem.mPictureLink);


        // Return the completed view to render on screen
        return convertView;
    }
}