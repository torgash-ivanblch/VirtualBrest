package im.point.torgash.virtualbrest;

/**
 * Created by torgash on 04.02.15.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RssRecyclerAdapter extends RecyclerView.Adapter<RssRecyclerAdapter.ViewHolder> {
    private ArrayList<RssItem> itemsData;
    private Context contex;
    public RssRecyclerAdapter(Context context, ArrayList<RssItem> itemsData) {
        this.itemsData = itemsData;
        contex = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RssRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rss_list_item, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        viewHolder.tvTitle.setText(itemsData.get(position).mTitle);
        Picasso.with(contex).setIndicatorsEnabled(true);
        if(viewHolder.ivPicture.getDrawable().equals(null)) {
            try {
                Picasso.with(contex).load(itemsData.get(position).mPictureLink).placeholder(R.drawable.rectangle).error(R.drawable.rectangle).into(viewHolder.ivPicture);
            } catch (IllegalArgumentException e) {
                Picasso.with(contex).load(R.drawable.rectangle).into(viewHolder.ivPicture);
            }
        }


    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvTitle;
        public ImageView ivPicture;
        View layoutView;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            layoutView = itemLayoutView;
            tvTitle = (TextView) itemLayoutView.findViewById(R.id.tvFeedTitle);
            ivPicture = (ImageView) itemLayoutView.findViewById(R.id.ivNewsLogo);
        }

        @Override
        public void onClick(View v) {
            if(v == layoutView){

            }
        }
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}