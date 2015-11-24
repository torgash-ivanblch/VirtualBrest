package im.point.torgash.virtualbrest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    SharedPreferences prefs;
    ActionBar aBar;
    private static final String ARG_SECTION_NUMBER = "section_number";
    VirtualBrestRssFragment vbrssFragment;
    FragmentTransaction fTrans;
    private OnFragmentInteractionListener mListener;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;

    }


    public PlaceholderFragment() {
    }

    ListView lvMainList;
    View rootView;
    String[] feedNames;
    String[] feedLinks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        int sect = getArguments().getInt(ARG_SECTION_NUMBER);
        vbrssFragment = new VirtualBrestRssFragment();
        aBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
//        aBar.setTitle("Новости Бреста");
        switch (sect) {
            case 1:
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_main, container,
                        false);
                lvMainList = (ListView) rootView.findViewById(R.id.lvMainList);
                feedNames = getResources().getStringArray(R.array.feed_names);
                feedLinks = getResources().getStringArray(R.array.feed_links);
                TypedArray imgs = getResources().obtainTypedArray(R.array.news_icons);
                ArrayList<Map<String, Object>> data = null;
                if (!prefs.contains("feedsArray")) {
                    data = new ArrayList<>(
                            feedNames.length);
                    Map<String, Object> m;
                    Map<String, Drawable> mD;
                    for (int i = 0; i < imgs.length(); i++) {
                        m = new HashMap<>();
                        int tempDrawable = imgs.getResourceId(i, -1);
                        String feedName = feedNames[i];
                        m.put("feedName", feedName);
                        m.put("feedIcon", tempDrawable);
                        data.add(m);
                    }

                }


                String[] from = {"feedName", "feedIcon"};
                // массив ID View-компонентов, в которые будут вставлять данные
                int[] to = {R.id.tvFeedName, R.id.ivNewsLogo};

                // создаем адаптер
                SimpleAdapter sAdapter = new SimpleAdapter(getActivity(), data, R.layout.main_list_item,
                        from, to);

                // определяем список и присваиваем ему адаптер
//                sAdapter.setViewBinder(new MyViewBinder());
                lvMainList.setAdapter(sAdapter);
                lvMainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        aBar.setTitle(feedNames[position]);
                        switch (position) {
                            case 0:
                                getActivity().setTitle(feedNames[position]);
                                switchToBasicRssFragment(position);
                                break;
                            case 1:
                                getActivity().setTitle(feedNames[position]);
                                switchToBasicRssFragment(position);
                                break;
                            case 2:
                                getActivity().setTitle(feedNames[position]);
                                switchToBasicRssFragmentNoPicture(position);

                                break;
                            case 3:
                                getActivity().setTitle(feedNames[position]);
                                switchToBasicRssFragment(position);
                                break;
                            case 7:
                                getActivity().setTitle(feedNames[position]);
                                switchToBasicRssFragment(position);
                                break;
                            case 8:
                                getActivity().setTitle(feedNames[position]);
                                switchToBasicRssFragment(position);
                                break;
                            case 4:
                                getActivity().setTitle(feedNames[position]);
                                switchToBasicWebView(feedLinks[position]);
                                break;
                            case 5:
                                getActivity().setTitle(feedNames[position]);
                                switchToBasicWebView(feedLinks[position]);
                                break;
                            case 6:
                                getActivity().setTitle(feedNames[position]);
                                switchToBasicWebView(feedLinks[position]);
                                break;
                            case 9:
                                break;
                            case 10:
                                break;
                        }
                    }
                });
                break;
            case 3:

                break;
            default:
                rootView = inflater.inflate(R.layout.fragment_main, container,
                        false);

                break;
        }

        return rootView;
    }

    private void switchToBasicWebView(String feedLink) {
        BasicWebViewFragment basicwvfrag = BasicWebViewFragment.newInstance(feedLink);
//        passDataToVBWVFrag(itemList.get(position).mNewsLink);
        fTrans = getFragmentManager().beginTransaction();
        fTrans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
//        fTrans.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fTrans.replace(R.id.container, basicwvfrag);

        fTrans.addToBackStack("BasicWV");


        fTrans.commit();
    }

    private void switchToVBRssFragment() {
        fTrans = getFragmentManager().beginTransaction();
//        fTrans.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fTrans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fTrans.replace(R.id.container, vbrssFragment, "VBRSS");
        fTrans.addToBackStack("VBRSS");


        fTrans.commit();
    }

    public void onResume() {
        super.onResume();
        aBar.show();
        getActivity().setTitle("Новости Бреста");
        mListener.showDrawerToggle(true);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    private void switchToBasicRssFragment(int position) {
        BasicRssFragment basicrssfragment = BasicRssFragment.newInstance(position);
        fTrans = getFragmentManager().beginTransaction();
//        fTrans.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fTrans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fTrans.replace(R.id.container, basicrssfragment, "BasicRSS");
        fTrans.addToBackStack("BasicRSS");

        fTrans.commit();
    }
    private void switchToBasicRssFragmentNoPicture(int position) {
        BasicRssFragmentNoPicture basicrssfragment = BasicRssFragmentNoPicture.newInstance(position);
        fTrans = getFragmentManager().beginTransaction();
//        fTrans.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fTrans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fTrans.replace(R.id.container, basicrssfragment, "BasicRSS");
        fTrans.addToBackStack("BasicRSS");


        fTrans.commit();
    }

}
