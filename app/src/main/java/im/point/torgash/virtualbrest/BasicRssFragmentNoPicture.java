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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEnclosure;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import im.point.torgash.mediarss.MediaEntryModule;
import im.point.torgash.mediarss.types.UrlReference;


public class BasicRssFragmentNoPicture extends Fragment implements FragmentManager.OnBackStackChangedListener, OnNewsClicked {


    private OnFragmentInteractionListener mListener;
    //    VBWebViewFragment basicwvfrag;
    BasicWebViewFragment basicwvfrag;
    ImageLoader imageLoader;
    BasicRssAdapterNoPicture sAdapter;
    private ArrayAdapter<String> adapter = null;
    final String TAG = "VIRTBREST";
    SwipeRefreshLayout refreshLayout;
    ListView lvRSS;
    View rootView;
    String rssLink = "";
    String rssDBName = "";
    ArrayList<Map<String, String>> dataArray;
    ArrayList<Map<String, String>> data;
    ActionBar aBar;
    ArrayList<String> pageLinks;
    ArrayList<String> pictureLinks;
    FragmentTransaction fTrans;
    ArrayList<MyRssItem> itemList;
    //    FixedRecyclerView recyclerView;
    protected BasicRssFragmentNoPicture staticFragment = this;
    public static LinearLayoutManager llm;
    final BasicRssFragmentNoPicture frag = this;
    String mParam1;
    String mTitle;

    public static BasicRssFragmentNoPicture newInstance(String link) {
        BasicRssFragmentNoPicture fragment = new BasicRssFragmentNoPicture();
        Bundle args = new Bundle();
        args.putString("rssLink", link);
        fragment.setArguments(args);
        return fragment;
    }
    public static BasicRssFragmentNoPicture newInstance(int position) {
        BasicRssFragmentNoPicture fragment = new BasicRssFragmentNoPicture();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(false);
        mTitle = getActivity().getTitle().toString();
        if (getArguments() != null) {
            String[] feedLinks = getResources().getStringArray(R.array.feed_links);
            String[] feedDBNames = getResources().getStringArray(R.array.feed_db_names);
            rssLink = feedLinks[getArguments().getInt("position")];
            rssDBName = feedDBNames[getArguments().getInt("position")];
        }
        setRetainInstance(true);
    }
    View progressWheel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
                    .diskCacheFileCount(100)
                    .defaultDisplayImageOptions(defaultOptions)
                    .diskCacheSize(50 * 1024 * 1024)

                    .build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);
            rootView = inflater.inflate(R.layout.fragment_basic_rss, null);
            lvRSS = (ListView) rootView.findViewById(R.id.lvRSS);
            refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshlayout);
            refreshLayout.setColorScheme(
                    R.color.swipe_color_1, R.color.swipe_color_2,
                    R.color.swipe_color_3, R.color.swipe_color_4);
            progressWheel = rootView.findViewById(R.id.empty_view);
            aBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            setHasOptionsMenu(true);
            aBar.setDisplayHomeAsUpEnabled(true);

            lvRSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MyRssItem item = itemList.get(position);
                    MySQLiteSingleton.markNewsAsReadByNewsLinkHash(getActivity(), rssDBName, item.mNewsLink);
                    goToArticle(position);
                }
            });
            final FloatingActionButton button = (FloatingActionButton)rootView.findViewById(R.id.scrollButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lvRSS.smoothScrollToPosition(0);
                }
            });
            button.setVisibility(View.GONE);
            lvRSS.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem == 0) {
                        button.setVisibility(View.GONE);
                        refreshLayout.setEnabled(true);
                    }
                    else
                    {
                        refreshLayout.setEnabled(false);
                        button.setVisibility(View.VISIBLE);
                    }
                }
            });

            if (null == data) {
                data = new ArrayList<>();

            }

            itemList = new ArrayList<>();
            // создаем адаптер
            llm = new LinearLayoutManager(getActivity());

//            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//                int mLastFirstVisibleItem = 0;
//
//                @Override
//                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                }
//
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    final int currentFirstVisibleItem = llm.findFirstVisibleItemPosition();
//
//                    if (currentFirstVisibleItem > this.mLastFirstVisibleItem) {
//                        ((MainActivity)getActivity()).getSupportActionBar().hide();
//                    } else if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
//                        ((MainActivity)getActivity()).getSupportActionBar().show();
//                    }
//
//                    this.mLastFirstVisibleItem = currentFirstVisibleItem;
//                }
//            });
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    try {
                        populateRssList();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }


                }


            });
            if (data.size() == 0) {

                refreshLayout.setRefreshing(true);

                try {
                    populateRssList();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

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

    public void populateRssList() throws MalformedURLException {
        rssDownload();
    }

    ArrayList<Map<String, String>> result = null;

    public void rssDownload() throws MalformedURLException {

        refreshLayout.setRefreshing(true);
        URL feedUrl;
        feedUrl = new URL(rssLink);
        SyndFeedInput input = new SyndFeedInput();
        RssAsyncFetch rssFetch = new RssAsyncFetch();
        rssFetch.execute(input);
    }

    @Override
    public void onBackStackChanged() {

    }

    @Override
    public void goToArticle(int position) {
        basicwvfrag = BasicWebViewFragment.newInstance(itemList, position);
//        passDataToVBWVFrag(itemList.get(position).mNewsLink);
        fTrans = getFragmentManager().beginTransaction();
        fTrans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
//        fTrans.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fTrans.replace(R.id.container, basicwvfrag);

        fTrans.addToBackStack("BasicWV");


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
//                progressD = new ProgressDialog(getActivity());
//                progressD.setIndeterminate(true);
//
//
//                progressD.setCancelable(false);
//
//                progressD.setMessage("Загрузка новостей...");
//                progressD.show();
            }

        }

        @Override
        protected void onPostExecute(SyndFeed s) {
//            if (itemList.size() == 0) progressD.dismiss();
            Log.d(TAG, "Finished loading, processing list...");
            refreshLayout.setRefreshing(false);
            progressWheel.setVisibility(View.GONE);
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
                    List<SyndEnclosure> sE = (List<SyndEnclosure>) ent.getEnclosures();
                    String imageURL = "";
                    if (sE.size() != 0) {
                        imageURL = sE.get(0).getUrl();
                    } else {
                        MediaEntryModule m = (MediaEntryModule) ent.getModule(MediaEntryModule.URI);
                        Log.d(TAG, "SyndEnclosure null, getting mediaentry... ");
                        if (null != m) {
                            Log.d(TAG, "MediaEntryModule string is " + m.toString());
                            UrlReference ref = (UrlReference) m.getMediaContents()[0].getReference();
                            imageURL = ref.getUrl().toString();

                        }
                    }
                    Log.d(TAG, "image url is " + imageURL);
                    MyRssItem tempItem = new MyRssItem(ent.getTitle(), ent.getDescription().getValue().length() > 120 ? ent.getDescription().getValue().substring(0, 120) + "..." : ent.getDescription().getValue(), link, imageURL, rssLink);
                    itemList.add(tempItem);
                    MySQLiteSingleton.makeNewDBRecord(getActivity(), rssDBName, tempItem, System.currentTimeMillis());
                }
            }
            if(null != getActivity()) {
                sAdapter = new BasicRssAdapterNoPicture(getActivity(), itemList);

                // определяем список и присваиваем ему адаптер
//                sAdapter.setViewBinder(new MyViewBinder());

                lvRSS.setAdapter(sAdapter);
            }

        }

        /**
         * <p>Applications should preferably override {@link #onCancelled(Object)}.
         * This method is invoked by the default implementation of
         * {@link #onCancelled(Object)}.</p>
         * <p/>
         * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
         * {@link #doInBackground(Object[])} has finished.</p>
         *
         * @see #onCancelled(Object)
         * @see #cancel(boolean)
         * @see #isCancelled()
         */
        @Override
        protected void onCancelled() {
//            progressD.dismiss();
            super.onCancelled();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    populateRssList();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


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

            try {
                populateRssList();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
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


}
