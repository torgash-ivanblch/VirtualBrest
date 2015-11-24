package im.point.torgash.virtualbrest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


public class VBWebViewFragment extends Fragment {
    SharedPreferences prefs;
    MenuItem menuItem;
    OnFragmentInteractionListener mListener;
    final String TAG = "VIRTBREST";
    String link = "";
    private ArrayList<MyRssItem> mParam1;
    private int mParam2;
    int currentTextSize = 0;
    View rootView;
    View abprogress;
    String rssLink = "http://virtualbrest.by/rss/news.php";
    ArrayList<Map<String, String>> dataArray;
    ArrayList<Map<String, String>> data;
    ActionBar aBar;
    WebView wvVB;
    AsyncTask<String, Void, String> loadFilterTask;
    static boolean progressSet = false;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String pageIdentity;

    public static VBWebViewFragment newInstance(ArrayList<MyRssItem> list, int position) {
        VBWebViewFragment fragment = new VBWebViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, list);
        args.putInt(ARG_PARAM2, position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "WVFragment onCreateView invoked");
        setHasOptionsMenu(true);
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        if (null == rootView) {
            abprogress = inflater.inflate(R.layout.progress_wheel, null);
            rootView = inflater.inflate(R.layout.fragment_vbweb_view, null);


            aBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            aBar.show();
            aBar.setDisplayHomeAsUpEnabled(true);

            wvVB = (WebView) rootView.findViewById(R.id.wvVB);
            WebSettings webSettings = wvVB.getSettings();
            webSettings.setDefaultFontSize(prefs.getInt("textSize" + pageIdentity, 12));
            webSettings.setMinimumFontSize(prefs.getInt("textSize" + pageIdentity, 12));
            webSettings.setBlockNetworkImage(prefs.getBoolean("dontLoadImages", false));
        }


        // создаем адаптер


        // определяем список и присваиваем ему адаптер
//                sAdapter.setViewBinder(new MyViewBinder());


        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "WVFragment Options menu created");
        // If the drawer is open, show the global app actions in the action bar.
        // See also
        // showGlobalContextActionBar, which controls the top-left area of the
        // action bar.
        menu.clear();
        inflater.inflate(R.menu.menu_wv, menu);
        menuItem = menu.getItem(1);

        WebViewClient wvclient = new WebViewClient() {
            boolean loadingFinished = true;
            boolean redirect = false;

            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                if (!loadingFinished) {
                    redirect = true;
                }
                loadingFinished = false;
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                Log.d("DOWNLOAD", "Redirecting to " + url);


                view.loadUrl(url);
                return false; // then it is not handled by default action
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loadingFinished = false;
                if (!progressSet) {

                    MenuItemCompat.setActionView(menuItem, abprogress);
                    progressSet = true;
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    if (null != getActivity()) {

                        MenuItemCompat.setActionView(menuItem, null);
                        progressSet = false;
                    }
                    //HIDE LOADING IT HAS FINISHED
                } else {
                    redirect = false;
                }
//                view.loadUrl("javascript:(function() { " +
//                        "document.querySelectorAll('p[id=\"text\"]')[1].style.display=\"none\"; " +
//                        "})");
                // do your stuff here

            }
        };
        wvVB.setWebViewClient(wvclient);
        boolean singleColumn = prefs.getBoolean("singleColumn", false);
        if (singleColumn) {
            menu.getItem(2).setChecked(true);
            wvVB.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        } else {
            menu.getItem(2).setChecked(false);
            wvVB.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        }
        wvVB.getSettings().setJavaScriptEnabled(true);
        if (!link.equals("")) {
            MenuItemCompat.setActionView(menuItem, abprogress);
            loadOrReloadData(link);
            Log.d(TAG, "WVFragment onStart invoked");
            Log.d(TAG, "menuItem = " + menuItem + "; abprogress = " + abprogress);

        }


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//		if (mDrawerToggle.onOptionsItemSelected(item)) {
//			return true;
//		}
        if (item.getItemId() == R.id.action_wv_size) {
            //TODO fontsize change
            WebSettings webSettings = wvVB.getSettings();
            SharedPreferences.Editor editor;
            switch (currentTextSize) {
                case 0:
                    currentTextSize = 1;
                    webSettings.setDefaultFontSize(16);
                    webSettings.setMinimumFontSize(16);
                    editor = prefs.edit();
                    editor.putInt("textSize" + pageIdentity, 16);
                    editor.apply();
                    break;
                case 1:
                    currentTextSize = 2;
                    webSettings.setDefaultFontSize(24);
                    webSettings.setMinimumFontSize(24);
                    editor = prefs.edit();
                    editor.putInt("textSize" + pageIdentity, 24);
                    editor.apply();
                    break;
                case 2:
                    currentTextSize = 0;
                    webSettings.setDefaultFontSize(12);
                    webSettings.setMinimumFontSize(12);
                    editor = prefs.edit();
                    editor.putInt("textSize" + pageIdentity, 12);
                    editor.apply();
                    break;
            }
            /*

I finally found it:-

WebSettings webSettings = webView.getSettings();

either setTextSize or

webSettings.setTextSize(WebSettings.TextSize.SMALLEST);

This one works too:-

webSettings.setDefaultFontSize(10);

*/
        }

        if (item.getItemId() == R.id.action_wv_refresh) {
            Toast.makeText(getActivity(), "Refreshing page...", Toast.LENGTH_SHORT)
                    .show();

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            abprogress = inflater.inflate(R.layout.progress_wheel, null);
            MenuItemCompat.setActionView(item, abprogress);
            progressSet = true;

            loadOrReloadData(link);

            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            getFragmentManager().popBackStackImmediate();
            return true;
        }
        SharedPreferences.Editor editor;
        if (item.getItemId() == R.id.setting_webview_singlecolumn) {
            if (item.isChecked()) {
                item.setChecked(false);
                wvVB.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

                editor = prefs.edit();
                editor.putBoolean("singleColumn", false);
                editor.apply();
            } else {
                item.setChecked(true);
                wvVB.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                editor = prefs.edit();
                editor.putBoolean("singleColumn", true);
                editor.apply();
            }
        }
        if (item.getItemId() == R.id.webview_tobrowser) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(intent);
        }
        if (item.getItemId() == R.id.webview_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, link);
            intent.setType("text/plain");
            startActivity(intent);
        }
        if (item.getItemId() == R.id.webview_copylink) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("web link", link);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), "Ссылка скопирована в буфер обмена", Toast.LENGTH_LONG).show();
        }


        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        mListener.showDrawerToggle(false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(false);
        if (getArguments() != null) {
            mParam1 = (ArrayList<MyRssItem>) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
            link = mParam1.get(mParam2).mNewsLink;
            pageIdentity = mParam1.get(mParam2).mFeedIdentify;
        }
        setRetainInstance(true);
    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }


    public void setData(String data) {
        link = data;
    }

    public void loadOrReloadData(String link) {
        loadFilterTask = new AsyncTask<String, Void, String>() {


            @Override
            protected String doInBackground(String... params) {
                String result = "";
                Document doc = null;
                try

                {
                    doc = Jsoup.connect(params[0]).get();
                } catch (
                        IOException e
                        )

                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "";
                }
                doc.select("#text").get(0).remove();
                doc.getElementById("pdaspis").remove();
                doc.getElementById("header").remove();
                doc.getElementById("yandex_ad").remove();

                doc.getElementsByClass("pdah2").remove();
                doc.select("p:contains(К списку всех новостей)").remove();
                result = doc.html();


                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                wvVB.loadDataWithBaseURL("http://virtualbrest.by", s, "text/html", "utf-8", null);
                wvVB.getSettings().setJavaScriptEnabled(true);
//                wvVB.loadData(s, "text/html; charset=UTF-8", null);
                super.onPostExecute(s);
            }

        };
        loadFilterTask.execute(link);
    }
}