package im.point.torgash.virtualbrest.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import im.point.torgash.virtualbrest.MainActivity;
import im.point.torgash.virtualbrest.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link im.point.torgash.virtualbrest.util.AboutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link im.point.torgash.virtualbrest.util.AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Preferences.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static AboutFragment newInstance() {
        return new AboutFragment();


    }

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();


    }
    View rootView;
    LayoutInflater mInflater;
    ViewGroup mContainer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mInflater = inflater;
        mContainer = container;
        rootView = inflater.inflate(R.layout.section_about, container, false);
        TextView tvTextWithLinks = (TextView) rootView.findViewById(R.id.tvTextWithLinks);
        TextView tvTextWithEmail = (TextView) rootView.findViewById(R.id.tvTextWithEmail);
//        Linkify.addLinks(tvTextWithLinks, Linkify.WEB_URLS);
//        Linkify.addLinks(tvTextWithEmail, Linkify.EMAIL_ADDRESSES);
//        tvTextWithLinks.setText(Html.fromHtml("Ver. 0.9.1 beta\n" +
//                "\n" +
//                "Автор: <a href=http://xbrest.by>xbrest.by</a>\n" +
//                "Замечания, вопросы, предложения: <a href=mailto:info@xbrest.by>info@xbrest.by</a>"));
//
//        tvTextWithLinks.setMovementMethod(LinkMovementMethod.getInstance());
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("О программе");

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.isVisible()) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View newView = inflater.inflate(R.layout.section_about, null);
            // This just inflates the view but doesn't add it to any thing.
            // You need to add it to the root view of the fragment
            ViewGroup rootView = (ViewGroup) getView();
            // Remove all the existing views from the root view.
            // This is also a good place to recycle any resources you won't need anymore
            rootView.removeAllViews();
            rootView.addView(newView);

        }
    }


}
