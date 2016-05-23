package com.materialnotes.widget;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.materialnotes.R;

import roboguice.fragment.RoboDialogFragment;
import roboguice.inject.InjectView;


public class InfoDialog extends RoboDialogFragment {

    private static final String TAG = InfoDialog.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_info, container, false);
    }


 /*   @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            Log.e(TAG, "Couldn't find version name", ex);
        }
    }*/
}