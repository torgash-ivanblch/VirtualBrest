package im.point.torgash.virtualbrest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class VirtualBrestRssFragment extends Fragment implements FragmentManager.OnBackStackChangedListener, OnNewsClicked {


    private OnFragmentInteractionListener mListener;
    //    VBWebViewFragment basicwvfrag;
    VBWebViewFragment vbwvfrag;
    ImageLoader imageLoader;
    RssRecyclerAdapter sAdapter;
    private ArrayAdapter<String> adapter = null;
    final String TAG = "VIRTBREST";
    SwipeRefreshLayout refreshLayout;
    ListView lvRSS;
    View rootView;
    String rssLink = "http://virtualbrest.by/rss/news.php";
    ArrayList<Map<String, String>> dataArray;
    ArrayList<Map<String, String>> data;
    ActionBar aBar;
    ArrayList<String> pageLinks;
    FragmentTransaction fTrans;
    ArrayList<MyRssItem> itemList;
    FixedRecyclerView recyclerView;
    protected VirtualBrestRssFragment staticFragment = this;
    public static LinearLayoutManager llm;
    final VirtualBrestRssFragment frag = this;
    String mTitle;
    @Override


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTitle = getActivity().getTitle().toString();
        if (rootView == null) {

            rootView = inflater.inflate(R.layout.fragment_rss, null);
            recyclerView = (FixedRecyclerView) rootView.findViewById(R.id.recyclerView);
            refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshlayout);
            refreshLayout.setColorScheme(
                    R.color.swipe_color_1, R.color.swipe_color_2,
                    R.color.swipe_color_3, R.color.swipe_color_4);
            aBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            setHasOptionsMenu(true);
            aBar.setDisplayHomeAsUpEnabled(true);


//        lvRSS.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem == 0)
//                    refreshLayout.setEnabled(true);
//                else
//                    refreshLayout.setEnabled(false);
//            }
//        });

            if (null == data) {
                data = new ArrayList<>();

            }

            itemList = new ArrayList<>();
            // создаем адаптер
            llm = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(llm);
            recyclerView.setWillNotCacheDrawing(true);
            recyclerView.setItemViewCacheSize(90);
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                int mLastFirstVisibleItem = 0;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    final int currentFirstVisibleItem = llm.findFirstVisibleItemPosition();

                    if (currentFirstVisibleItem > this.mLastFirstVisibleItem) {
                        ((MainActivity) getActivity()).getSupportActionBar().hide();
                    } else if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
                        ((MainActivity) getActivity()).getSupportActionBar().show();
                    }

                    this.mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            });
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    populateRssList();


                }


            });
            if (data.size() == 0) {

                refreshLayout.setRefreshing(true);

                populateRssList();

            }
        } else {
            // Do not inflate the layout again.
            // The returned View of onCreateView will be added into the fragment.
            // However it is not allowed to be added twice even if the parent is same.
            // So we must remove _rootView from the existing parent view group
            // (it will be added back).
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        return rootView;
    }

    public void populateRssList() {
        dataArray = rssDownload();
    }

    ArrayList<Map<String, String>> result = null;

    public ArrayList<Map<String, String>> rssDownload() {

        refreshLayout.setRefreshing(true);
        URL feedUrl;
        try {
            Log.d("DEBUG", "Entered:" + rssLink);
            feedUrl = new URL(rssLink);

            SyndFeedInput input = new SyndFeedInput();
            RssAsyncFetch rssFetch = new RssAsyncFetch();
            rssFetch.execute(input);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onBackStackChanged() {

    }

    @Override
    public void goToArticle(int position) {
        vbwvfrag = VBWebViewFragment.newInstance(itemList, position);
//        passDataToVBWVFrag(itemList.get(position).mNewsLink);
        fTrans = getFragmentManager().beginTransaction();
        fTrans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
//        fTrans.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fTrans.replace(R.id.container, vbwvfrag);

        fTrans.addToBackStack("VBWV");


        fTrans.commit();
    }

    public class RssAsyncFetch extends AsyncTask<SyndFeedInput, String, SyndFeed> {
        ProgressDialog progressD;

        @Override
        protected SyndFeed doInBackground(SyndFeedInput... input) {
            SyndFeed feed = null;
            try {
                feed = input[0].build(new XmlReader(new URL(rssLink)));
            } catch (IOException e) {
                e.printStackTrace();

                return null;
            } catch (FeedException e) {
                e.printStackTrace();

                return null;
            }
            return feed;
        }

        @Override
        protected void onPreExecute() {
            if (itemList.size() == 0) {
                progressD = new ProgressDialog(getActivity());
                progressD.setIndeterminate(true);


                progressD.setCancelable(false);

                progressD.setMessage("Загрузка новостей...");
                progressD.show();
            }

        }

        @Override
        protected void onPostExecute(SyndFeed s) {
            if (itemList.size() == 0) progressD.dismiss();
            Log.d(TAG, "Finished loading, processing list...");
            refreshLayout.setRefreshing(false);
            List<SyndEntry> entries = null;
            if (null != s) {
                entries = s.getEntries();
            } else {
                Toast.makeText(getActivity(), "Проверьте соединение с Интернетом", Toast.LENGTH_LONG).show();
            }
            if (getActivity() != null && null != entries) {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.feeds_retrieved) + entries.size(),
                        Toast.LENGTH_SHORT).show();

                itemList.clear();
                Iterator<SyndEntry> iterator = entries.listIterator();
                pageLinks = new ArrayList<>();
                while (iterator.hasNext()) {
                    SyndEntry ent = iterator.next();
                    String link = ent.getLink();
                    pageLinks.add(link);
                    MyRssItem tempItem = new MyRssItem(ent.getTitle(), ent.getDescription().getValue().length() > 120 ? ent.getDescription().getValue().substring(0, 120) + "..." : ent.getDescription().getValue(), link, rssLink);
                    itemList.add(tempItem);

                }
            }
            if(null != getActivity()) {
                sAdapter = new RssRecyclerAdapter(getActivity(), itemList, llm, recyclerView, rootView, frag);

                // определяем список и присваиваем ему адаптер
//                sAdapter.setViewBinder(new MyViewBinder());

                recyclerView.setAdapter(sAdapter);
            }

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateRssList();


            }


        });

    }

    @Override

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar.
        // See also
        // showGlobalContextActionBar, which controls the top-left area of the
        // action bar.
        menu.clear();
        inflater.inflate(R.menu.menu_rss, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//		if (mDrawerToggle.onOptionsItemSelected(item)) {
//			return true;
//		}

        if (item.getItemId() == R.id.action_rss_refresh) {
            Toast.makeText(getActivity(), "Refresh...", Toast.LENGTH_SHORT)
                    .show();

            populateRssList();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            getActivity().setTitle("Новости Бреста");
            getFragmentManager().popBackStackImmediate();
            return true;
        }
        return super.onOptionsItemSelected(item);


    }

    public void onResume() {
        super.onResume();
        getActivity().setTitle(mTitle);
        mListener.showDrawerToggle(false);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    public void setData() {
//        myTextView.setText(data);
    }

    //Here you send some data over MainActivity to the other Fragment
    private void passDataToVBWVFrag(String data) {
        ((MainActivity) getActivity()).sendDataToVBWVFrag(data);

    }

    public Fragment getCurrentFragment() {
        return staticFragment;
    }

    public static LinearLayoutManager getLlm() {
        return llm;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
