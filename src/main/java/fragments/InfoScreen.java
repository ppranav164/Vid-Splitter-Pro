package fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.card.splitter_pro.BuildConfig;
import com.card.splitter_pro.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoScreen extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView text_version;

    public InfoScreen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoScreen newInstance(String param1, String param2) {
        InfoScreen fragment = new InfoScreen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info_screen, container, false);
        text_version = view.findViewById(R.id.text_version);
        String VERSION_NAME = BuildConfig.VERSION_NAME;
        text_version.setText(VERSION_NAME);

        showInfo();

        return  view;
    }



    public void showInfo()
    {
        String VERSION = "V"+BuildConfig.VERSION_NAME;
        text_version.setText(VERSION);
    }

}