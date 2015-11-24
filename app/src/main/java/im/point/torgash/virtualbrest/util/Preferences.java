package im.point.torgash.virtualbrest.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import im.point.torgash.virtualbrest.MainActivity;
import im.point.torgash.virtualbrest.MyUpdateService;
import im.point.torgash.virtualbrest.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Preferences.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Preferences#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Preferences extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "VIRTBREST";

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
    public static Preferences newInstance(String param1, String param2) {
        Preferences fragment = new Preferences();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static Preferences newInstance() {
        Preferences fragment = new Preferences();

        return fragment;
    }

    public Preferences() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_preferences, container, false);
        CheckBox cbDontLoadImages = (CheckBox) rootView.findViewById(R.id.cbDontLoadImages);
        final SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        final SharedPreferences prefsDefault = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        boolean dontLoadImages = prefs.getBoolean("dontLoadImages", false);
        cbDontLoadImages.setChecked(dontLoadImages);
        cbDontLoadImages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("dontLoadImages", isChecked);
                editor.apply();
            }
        });
        final TextView tvFrequency = (TextView) rootView.findViewById(R.id.tvFrequency);
        final long frequency = prefsDefault.getLong("serviceUpdateFrequency", 0L);
        final long[] frequencyVariants = {900000L, 3600000L, 7200000L, 1440000L, 0L};
        final String[] frequencyMenuVariants = {"Каждые 15 минут", "Каждый час", "Каждые 2 часа", "Каждые 4 часа", "Никогда"};
        for (int i = 0; i < frequencyVariants.length; i++) {
            if (frequency == frequencyVariants[i]) {
                tvFrequency.setText(frequencyMenuVariants[i]);
            }
        }
        RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.update_freq_picker);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title("Расписание обновлений")
                        .items(frequencyMenuVariants)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                SharedPreferences.Editor editor = prefsDefault.edit();
                                editor.putLong("serviceUpdateFrequency", frequencyVariants[which]);
                                editor.commit();
                                tvFrequency.setText(frequencyMenuVariants[which]);
                                getActivity().stopService(new Intent(getActivity(), MyUpdateService.class));
                                Log.d(TAG, "Service stopped after settings change");
                                getActivity().startService(new Intent(getActivity(), MyUpdateService.class));
                                Log.d(TAG, "Service restarted");
                            }
                        })
                        .show();
            }
        });
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
        getActivity().setTitle("Настройки");

    }
}
