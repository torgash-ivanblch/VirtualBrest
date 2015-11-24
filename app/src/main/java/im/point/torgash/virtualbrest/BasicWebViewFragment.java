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
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Map;


public class BasicWebViewFragment extends Fragment {
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
    ArrayList<Map<String, String>> dataArray;
    ArrayList<Map<String, String>> data;
    ActionBar aBar;
    ObservableWebView wvVB;
    AsyncTask<String, Void, String> loadFilterTask;
    static boolean progressSet = false;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    String pageIdentity;
    boolean isNotYetLoaded = true;

    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;
    private WebChromeClient  mWebChromeClient;

    public static BasicWebViewFragment newInstance(ArrayList<MyRssItem> list, int position) {
        BasicWebViewFragment fragment = new BasicWebViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, list);
        args.putInt(ARG_PARAM2, position);
        fragment.setArguments(args);
        return fragment;
    }
    public static BasicWebViewFragment newInstance(String link) {
        BasicWebViewFragment fragment = new BasicWebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM3, link);

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
            customViewContainer = (FrameLayout) rootView.findViewById(R.id.customViewContainer);
            final FloatingActionButton button = (FloatingActionButton)rootView.findViewById(R.id.scrollButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wvVB.scrollTo(0,0);
                }
            });
            button.setVisibility(View.GONE);
            aBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            aBar.show();
            aBar.setDisplayHomeAsUpEnabled(true);

            wvVB = (ObservableWebView) rootView.findViewById(R.id.wvVB);
            wvVB.setWebChromeClient(mWebChromeClient = new WebChromeClient() {
                Bitmap mDefaultVideoPoster;
                View mVideoProgressView;

                @Override
                public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                    onShowCustomView(view, callback);    //To change body of overridden methods use File | Settings | File Templates.
                }

                @Override
                public void onShowCustomView(View view,CustomViewCallback callback) {

                    // if a view already exists then immediately terminate the new one
                    if (mCustomView != null) {
                        callback.onCustomViewHidden();
                        return;
                    }
                    mCustomView = view;
                    wvVB.setVisibility(View.GONE);
                    customViewContainer.setVisibility(View.VISIBLE);
                    customViewContainer.addView(view);
                    customViewCallback = callback;
                }

                @Override
                public View getVideoLoadingProgressView() {

                    if (mVideoProgressView == null) {
                        LayoutInflater inflater = LayoutInflater.from(getActivity());
                        mVideoProgressView = inflater.inflate(R.layout.video_progress, null);
                    }
                    return mVideoProgressView;
                }

                @Override
                public void onHideCustomView() {
                    super.onHideCustomView();    //To change body of overridden methods use File | Settings | File Templates.
                    if (mCustomView == null)
                        return;

                    wvVB.setVisibility(View.VISIBLE);
                    customViewContainer.setVisibility(View.GONE);

                    // Hide the custom view.
                    mCustomView.setVisibility(View.GONE);

                    // Remove the custom view from its container.
                    customViewContainer.removeView(mCustomView);
                    customViewCallback.onCustomViewHidden();

                    mCustomView = null;
                }
                @Override
                public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg)
                {

                    WebView.HitTestResult result = view.getHitTestResult();
                    String data = result.getExtra();
                    loadOrReloadData(data);
//                    Context context = view.getContext();
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
//                    context.startActivity(browserIntent);
                    return false;
                }
            });

            wvVB.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            WebSettings webSettings = wvVB.getSettings();
            webSettings.setDefaultFontSize(prefs.getInt("textSize" + pageIdentity, 12));
            webSettings.setMinimumFontSize(prefs.getInt("textSize"+ pageIdentity, 12));
            webSettings.setBlockNetworkImage(prefs.getBoolean("dontLoadImages", false));
            webSettings.setAppCacheEnabled(true);

            webSettings.setSaveFormData(true);
            wvVB.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {
                @Override
                public void onScroll(int l, int t) {
                    if(t>=200 && !link.contains("vb.by")){
                        button.setVisibility(View.VISIBLE);
                    } else button.setVisibility(View.GONE);
                }
            });
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
        if(isNotYetLoaded){
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
            wvVB.getSettings().setSupportMultipleWindows(true);
            boolean singleColumn = prefs.getBoolean("singleColumn", false);
            if(singleColumn){
                menu.getItem(2).setChecked(true);
                wvVB.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            }else {
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
            isNotYetLoaded = false;
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
                    editor.putInt("textSize"+ pageIdentity, 24);
                    editor.apply();
                    break;
                case 2:
                    currentTextSize = 0;
                    webSettings.setDefaultFontSize(12);
                    webSettings.setMinimumFontSize(12);
                    editor = prefs.edit();
                    editor.putInt("textSize"+ pageIdentity, 12);
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
            getActivity().setTitle("Новости Бреста");
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
            }
            else {
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
            String linkToShare = wvVB.getUrl();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, linkToShare.contains("virtualbrest") ? linkToShare.replace("android.php?pdaid=", "news") + ".php" : linkToShare);
            intent.setType("text/plain");
            startActivity(intent);
        }
        if (item.getItemId() == R.id.webview_copylink) {
            String linkToShare = wvVB.getUrl();
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("web link", linkToShare.contains("virtualbrest") ? linkToShare.replace("android.php?pdaid=", "news") + ".php" : linkToShare);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), "Ссылка скопирована в буфер обмена", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(false);
        if (getArguments() != null && getArguments().size() == 2) {
            mParam1 = (ArrayList<MyRssItem>) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
            link = mParam1.get(mParam2).mNewsLink;
            pageIdentity = mParam1.get(mParam2).mFeedIdentify;
        } else if(getArguments() != null && getArguments().size() == 1) {
            link = getArguments().getString(ARG_PARAM3);
            pageIdentity = link;
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

    public void loadOrReloadData(final String link) {

        wvVB.loadUrl(link);
        wvVB.getSettings().setJavaScriptEnabled(true);

    }
    public boolean inCustomView() {
        return (mCustomView != null);
    }

    public void hideCustomView() {

        mWebChromeClient.onHideCustomView();
    }

    @Override
    public void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        wvVB.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        wvVB.onResume();
        mListener.showDrawerToggle(false);
    }

    @Override
    public void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        if (inCustomView()) {
            hideCustomView();
        }
    }

}