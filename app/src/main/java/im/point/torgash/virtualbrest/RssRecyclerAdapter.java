package im.point.torgash.virtualbrest;

/**
 * Created by torgash on 04.02.15.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class RssRecyclerAdapter extends FixedRecyclerView.Adapter<RssRecyclerAdapter.VBFeedListRowHolder> {
    SharedPreferences prefs;
    ArrayList<Drawable> localImageCacheList; //for better performance in RecyclerView;
    private static final String TAG = "VIRTBREST";
    private ArrayList<MyRssItem> itemsData;
    private Context contex;
    DisplayImageOptions options;
    Fragment currentFragment;
    LinearLayoutManager llm;
    RecyclerView recyclerView;
    boolean scrollStopped = true;
    View responsibleView;
    Semaphore sem;
    Fragment frag;

    public RssRecyclerAdapter(Context context, ArrayList<MyRssItem> itemsData, LinearLayoutManager llm, RecyclerView rv, View respView, VirtualBrestRssFragment fragment) {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.rectangle)
                .showImageForEmptyUri(R.drawable.rectangle)
                .showImageOnFail(R.drawable.rectangle)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)

                .build();
        prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        frag = fragment;
        sem = new Semaphore(2);
        responsibleView = respView;
        this.itemsData = itemsData;
        contex = context;
        this.llm = llm;
        recyclerView = rv;
//        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                switch (newState) {
//                    case RecyclerView.SCROLL_STATE_IDLE:
//                        scrollStopped = true;
//                        Log.d(TAG, "scroll state idle");
//                        break;
//                    case RecyclerView.SCROLL_STATE_DRAGGING:
//                        scrollStopped = true;
//                        Log.d(TAG, "scroll state dragging");
//                        break;
//                    case RecyclerView.SCROLL_STATE_SETTLING:
//                        scrollStopped = false;
//                        Log.d(TAG, "scroll state dragging");
//                        break;
//                }
//            }
//
//
//        });
        localImageCacheList = new ArrayList<>(itemsData.size());
    }

    int n = 0;

    // Create new views (invoked by the layout manager)
    @Override
    public VBFeedListRowHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {

        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rss_list_item, parent, false);

        // create ViewHolder

        VBFeedListRowHolder viewHolder = new VBFeedListRowHolder(itemLayoutView);
        Log.d(TAG, "ViewHolder #" + (n++) + " created");
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final VBFeedListRowHolder viewHolder, final int position) {
        Handler h;
        final int p = position;
        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        viewHolder.tvTitle.setText(itemsData.get(position).mTitle);
        Log.d(TAG, "Position: " + p + "; First visible: " + llm.findFirstVisibleItemPosition() + "Last visible: " + llm.findLastVisibleItemPosition());
        if (!localImageCacheList.isEmpty() && null != localImageCacheList.get(p)) {
            viewHolder.ivPicture.setImageDrawable(localImageCacheList.get(p));
            Log.d(TAG, "Using ArrayList cache in position " + p);
        } else {


            Log.d(TAG, "Picture #" + p + " is null, loading...");
            if(!prefs.getBoolean("dontLoadImages", false)) {
                try {
                    final Handler finalH = new Handler() {
                        public void handleMessage(android.os.Message msg) {
                            // обновляем TextView

                            String finalImageLink = (String) msg.obj;
                            ImageLoader.getInstance().displayImage(finalImageLink, viewHolder.ivPicture);
                        }


                    };
                    ImageFromHtmlRetriever t = new ImageFromHtmlRetriever(sem, p, itemsData, finalH);


                } catch (IllegalArgumentException e) {

                    ImageLoader.getInstance().displayImage("@drawable/rectangle", viewHolder.ivPicture);
                    localImageCacheList.set(p, viewHolder.ivPicture.getDrawable());
                }
            }
        }


    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    /**
     * Called when a view created by this adapter has been recycled.
     * <p/>
     * <p>A view is recycled when decides that it no longer
     * needs to be attached to its parent {@link android.support.v7.widget.RecyclerView}. This can be because it has
     * fallen out of visibility or a set of cached views represented by views still
     * attached to the parent RecyclerView. If an item view has large or expensive data
     * bound to it such as large bitmaps, this may be a good place to release those
     * resources.</p>
     *
     * @param holder The ViewHolder for the view being recycled
     */
    @Override
    public void onViewRecycled(VBFeedListRowHolder holder) {
        holder.ivPicture.setImageDrawable(null);
        super.onViewRecycled(holder);
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

    /**
     * Created by torgash on 05.02.15.
     */ // inner class to hold a reference to each item of RecyclerView
    public class VBFeedListRowHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "VIRTBREST";
        public TextView tvTitle;
        public ImageView ivPicture;
        View layoutView;

        public VBFeedListRowHolder(View itemLayoutView) {
            super(itemLayoutView);
            layoutView = itemLayoutView;
            tvTitle = (TextView) itemLayoutView.findViewById(R.id.tvFeedTitle);
            ivPicture = (ImageView) itemLayoutView.findViewById(R.id.ivNewsLogo);

            layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Click performed");
                    OnNewsClicked iface = (OnNewsClicked) frag;
                    iface.goToArticle(getPosition());
                }
            });
        }


    }
}