package fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.card.splitter_pro.Notifications;
import com.card.splitter_pro.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link settings_view#newInstance} factory method to
 * create an instance of this fragment.
 */
public class settings_view extends Fragment implements CompoundButton.OnCheckedChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Switch darkSwicth,alertSwitch;

    private SharedPreferences.Editor configEditor;
    private SharedPreferences config;

    public settings_view() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment settings_view.
     */
    // TODO: Rename and change types and number of parameters
    public static settings_view newInstance(String param1, String param2) {
        settings_view fragment = new settings_view();
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

        configEditor = getContext().getSharedPreferences("config", Context.MODE_PRIVATE).edit();
        config = getContext().getSharedPreferences("config", Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout._settings_view, container, false);

        darkSwicth = view.findViewById(R.id.darkswitch);
        alertSwitch = view.findViewById(R.id.alertswitch);

        darkSwicth.setOnCheckedChangeListener(this);
        alertSwitch.setOnCheckedChangeListener(this);

        if (config.getBoolean("night_mode", false))
        {
            darkSwicth.setChecked(true);
        }

        if (config.getBoolean("alert_me", false))
        {
            alertSwitch.setChecked(true);
        }


        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId())
        {
            case R.id.alertswitch : setAlert(isChecked);
            break;

            case R.id.darkswitch : setDarkMode(isChecked);
            break;
        }
    }


    public void setDarkMode(boolean ischecked)
    {
        Log.e("setDarkMode","setDarkMode = "+ischecked);

        if (ischecked)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            configEditor.putBoolean("night_mode",true);
            configEditor.commit();

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            configEditor.putBoolean("night_mode",false);
            configEditor.commit();
        }
    }


    public void setAlert(boolean ischecked)
    {
        Log.e("setAlert","setAlert = "+ischecked);

        if (ischecked)
        {
            configEditor.putBoolean("alert_me",true);
            configEditor.commit();

        }else {
            configEditor.putBoolean("alert_me",false);
            configEditor.commit();
        }

    }



}