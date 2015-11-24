package im.point.torgash.virtualbrest;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by torgash on 03.02.15.
 */
public class BasicRssAdapterNoPicture extends ArrayAdapter<MyRssItem> {
    private static final String TAG = "VIRTBREST";
    Context contex;
    Semaphore s;
    public BasicRssAdapterNoPicture(Context context, ArrayList<MyRssItem> news) {
        super(context, 0, news);
        contex = context;
        s = new Semaphore(2);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MyRssItem myRssItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.basic_rss_list_item, parent, false);
        }
        // Lookup view for data population
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvFeedTitle);
//        TextView tvDescription = (Tex.error(R.drawable.user_placeholder_error)tView) convertView.findViewById(R.id.tvDescription);

        tvTitle.setText(myRssItem.mTitle.contains("- важно!") ? myRssItem.mTitle.substring(0,myRssItem.mTitle.length() - 9) : myRssItem.mTitle);
//        tvDescription.setText(myRssItem.mDescription);




        // Return the completed view to render on screen
        return convertView;
    }
    public class ImageFromHtmlRetriever implements Runnable {
        Semaphore s;
        int position;
        String htmlLink;
        Handler h;

        public ImageFromHtmlRetriever(Semaphore _s, int p, ArrayList<MyRssItem> list, Handler _h) {
            s = _s;
            position = p;
            htmlLink = list.get(position).mNewsLink;
            h = _h;
            new Thread(this).start();
        }

        public void run() {

            Document doc = null;

            try

            {
                s.acquire();
                doc = Jsoup.connect(htmlLink).get();
            } catch (IOException | InterruptedException e)

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
                    Message msg = h.obtainMessage();
                    msg.obj = imageLink;
                    h.sendMessage(msg);

                }


            }
            s.release();
        }
    }
}