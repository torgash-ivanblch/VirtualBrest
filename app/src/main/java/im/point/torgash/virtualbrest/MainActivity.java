package im.point.torgash.virtualbrest;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.reflect.Method;

import im.point.torgash.virtualbrest.util.AboutFragment;
import im.point.torgash.virtualbrest.util.Preferences;


public class MainActivity extends ActionBarActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks, OnFragmentInteractionListener {
    private static final String TAG = "VIRTBREST";
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    VirtualBrestRssFragment vbrssfrag;
    public static VBWebViewFragment vbwvfrag;


//    Toolbar toolbar;
//
//    PagerSlidingTabStrip tabs;
//
//    ViewPager pager;

    private Drawable oldBackground = null;
    //    private int currentColor;
    private SystemBarTintManager mTintManager;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
//    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
//    ViewPager mViewPager;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //let's start the service for a chance
        Intent serviceIntent = new Intent(this, MyUpdateService.class);
        // стартуем сервис
        startService(serviceIntent);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .diskCacheFileCount(100)
                .defaultDisplayImageOptions(defaultOptions)
                .diskCacheSize(35 * 1024 * 1024)

                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        setContentView(R.layout.activity_main);

        final ActionBar aBar = getSupportActionBar();
        aBar.setHomeButtonEnabled(true);
        aBar.setTitle("Новости Бреста");
        mTitle = getTitle();
        drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//        ButterKnife.inject(this);
//        vbwvfrag = new VBWebViewFragment();

        // create our manager instance after the content view is set
        mTintManager = new SystemBarTintManager(this);
// enable status bar tint

        mTintManager.setStatusBarTintEnabled(true);
        int commonColor = getResources().getColor(R.color.darkblue);
        Drawable colorDrawable = new ColorDrawable(commonColor);
        Drawable bottomDrawable = new ColorDrawable(getResources().getColor(android.R.color.transparent));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
        if (oldBackground == null) {
            getSupportActionBar().setBackgroundDrawable(ld);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});
            getSupportActionBar().setBackgroundDrawable(td);
            td.startTransition(200);
        }
        mTintManager.setTintColor(commonColor);


        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) findViewById(R.id.pager);
//        mViewPager.setAdapter(mSectionsPagerAdapter);
//        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
//        tabs.setViewPager(mViewPager);
//        changeColor(getResources().getColor(android.R.color.holo_green_dark));
    }

    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (drawer != null) {
            drawer.closeDrawer(Gravity.LEFT);
        }
        final int _position = position;
        Thread t = new Thread(new Runnable() {

            public void run() {
                if(_position == 0){
                    //TODO right here logo does nothing. Later it will lead to site
                }else
                if (_position == 3) {
                    AboutFragment frag = AboutFragment.newInstance();
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.container,
                                    frag).addToBackStack("about").commit();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onSectionAttached(4);
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        }
                    });
                }else
                if (_position == 2) {
                    Preferences fragment = Preferences.newInstance();
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.container,
                                    fragment).addToBackStack("settings").commit();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           onSectionAttached(3);
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        }
                    });
                } else {
                    PlaceholderFragment temp = new PlaceholderFragment();
                    Fragment newInstanceFragment = temp.newInstance(_position + 1);
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.container,
                                    newInstanceFragment).commit();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onSectionAttached(_position + 1);
                        }
                    });
                }
            }
        });
        t.start();


    }

    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    Log.e(TAG, "onMenuOpened", e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return super.onMenuOpened(featureId, menu);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 2:
                mTitle = "Новости Бреста";
                restoreActionBar();
                break;
            case 3:
                mTitle = getString(R.string.section_settings);
                restoreActionBar();
                break;
            case 4:
                mTitle = getString(R.string.section_about);
                restoreActionBar();
                break;
            default:
                mTitle = "Новости Бреста";
                restoreActionBar();
                break;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Preferences fragment = Preferences.newInstance();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container,
                            fragment).addToBackStack("about").commit();


                    onSectionAttached(3);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            return true;
     }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Change the title associated with this activity.  If this is a
     * top-level activity, the title for its window will change.  If it
     * is an embedded activity, the parent can do whatever it wants
     * with it.
     *
     * @param title
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTitle = title;
        restoreActionBar();
    }

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void showDrawerToggle(boolean showDrawerToggle) {
        mDrawerToggle = NavigationDrawerFragment.mDrawerToggle;
        mDrawerToggle.setDrawerIndicatorEnabled(showDrawerToggle);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.close();

        }else{

            super.onBackPressed();
        }
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */


    public void sendDataToVBWVFrag(String data) {
        vbwvfrag.setData(data);
        Log.d(TAG, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onNewIntent(Intent intent){
        if (intent.hasExtra("link")) {
            setIntent(intent);
            Log.d(TAG, "It's alright, we've got a new intent from notification, going to onResume for fragment change");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent.hasExtra("link")) {
            getSupportActionBar().setTitle("Важные новости");
            String targetLink = intent.getStringExtra("link");
            BasicWebViewFragment fragment = BasicWebViewFragment.newInstance(targetLink);
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container,
                            fragment).commit();
            Log.d(TAG, "Got the intent on resume, going to fragment");

        }
    }


}
