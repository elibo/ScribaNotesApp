package com.materialnotes.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.materialnotes.R;
import roboguice.fragment.RoboDialogFragment;

public class InfoDialog extends RoboDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_info, container, false);
    }

}