/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package no.nordicsemi.android.scriba.hrs;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.achartengine.GraphicalView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.scriba.R;
import no.nordicsemi.android.scriba.profile.BleManager;
import no.nordicsemi.android.scriba.profile.BleProfileActivity;

/**
 * HRSActivity is the main Heart rate activity. It implements HRSManagerCallbacks to receive callbacks from HRSManager class. The activity supports portrait and landscape orientations. The activity
 * uses external library AChartEngine to show real time graph of HR values.
 */
// TODO The HRSActivity should be rewritten to use the service approach, like other do.
public class HRSActivity extends BleProfileActivity implements HRSManagerCallbacks {
	@SuppressWarnings("unused")
	private final String TAG = "HRSActivity";

	private final static String GRAPH_STATUS = "graph_status";
	private final static String GRAPH_COUNTER = "graph_counter";
	private final static String HR_VALUE = "hr_value";

	private final static int MAX_HR_VALUE = 65535;
	private final static int MIN_POSITIVE_VALUE = 0;
	private final static int REFRESH_INTERVAL = 40; // 1/25th of a second interval

	private Handler mHandler = new Handler();

	private boolean isGraphInProgress = false;

	private GraphicalView mGraphView;
	private LineGraphView mLineGraph;
	private TextView mHRSValue, mHRSPosition;

	public static float mHrmValue = 0;
	private int mCounter = 0;

	public static List<Float> myValues = new ArrayList<Float>();

	public static Activity fa;


	@Override
	protected void onCreateView(final Bundle savedInstanceState) {

		setContentView(R.layout.activity_feature_hrs);

		//set the size of the activity dialog
		/*WindowManager.LayoutParams params = getWindow().getAttributes();
		params.x = -20;
		params.height = 300;
		params.width = 480;
		params.y = -10;
		this.getWindow().setAttributes(params);*/

		setFinishOnTouchOutside(false);

		setGUI();

		fa = this;
	}

	private void setGUI() {
		mLineGraph = LineGraphView.getLineGraphView();
		mHRSValue = (TextView) findViewById(R.id.text_hrs_value);
		mHRSPosition = (TextView) findViewById(R.id.text_hrs_position);
		showGraph();
	}

	private void showGraph() {
		mGraphView = mLineGraph.getView(this);
		ViewGroup layout = (ViewGroup) findViewById(R.id.graph_hrs);
		layout.addView(mGraphView);
		//help with threading
		mGraphView.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onRestoreInstanceState(@NonNull final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {
			isGraphInProgress = savedInstanceState.getBoolean(GRAPH_STATUS);
			mCounter = savedInstanceState.getInt(GRAPH_COUNTER);
			mHrmValue = savedInstanceState.getFloat(HR_VALUE);
			if (isGraphInProgress)
				startShowGraph();
		}
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean(GRAPH_STATUS, isGraphInProgress);
		outState.putInt(GRAPH_COUNTER, mCounter);
		outState.putFloat(HR_VALUE, mHrmValue);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		stopShowGraph();
	}

	@Override
	protected int getLoggerProfileTitle() {
		return R.string.hrs_feature_title;
	}

	@Override
	protected int getAboutTextId() {
		return R.string.hrs_about_text;
	}

	@Override
	protected int getDefaultDeviceName() {
		return R.string.hrs_default_name;
	}

	@Override
	protected UUID getFilterUUID() {
		return HRSManager.HR_SERVICE_UUID;
	}

	private void updateGraph(final int hrmValue) {
		mCounter++;
		mLineGraph.addValue(new Point(mCounter, hrmValue));
		//myValues.add((float) hrmValue);
		//Log.i("System.out", "Val: "+ hrmValue);
		//Log.i("System.out", "Size: "+ myValues.size());
		//Log.i("System.out", "Counter: "+ mCounter);
		//Log.i("System.out", "List: "+ String.valueOf(myValues));
		mGraphView.repaint();
	}

	private Runnable mRepeatTask = new Runnable() {
		@Override
		public void run() {
			if (mHrmValue > 0)
				updateGraph((int) mHrmValue);
			if (isGraphInProgress)
				mHandler.postDelayed(mRepeatTask, REFRESH_INTERVAL);
				myValues.add(mHrmValue);
		}
	};

	void startShowGraph() {
		isGraphInProgress = true;
		mRepeatTask.run();
	}

	void stopShowGraph() {
		isGraphInProgress = false;
		mHandler.removeCallbacks(mRepeatTask);
	}

	@Override
	protected BleManager<HRSManagerCallbacks> initializeManager() {
		final HRSManager manager = HRSManager.getInstance(getApplicationContext());
		manager.setGattCallbacks(this);
		return manager;
	}

	private void setHRSValueOnView(final int value) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (value >= MIN_POSITIVE_VALUE && value <= MAX_HR_VALUE) {
					mHRSValue.setText(" - ");
				} else {
					mHRSValue.setText(R.string.not_available_value);
				}
			}
		});
	}

	private void setHRSPositionOnView(final String position) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (position != null) {
					mHRSPosition.setText(position);
				} else {
					mHRSPosition.setText(R.string.not_available);
				}
			}
		});
	}

	@Override
	public void onServicesDiscovered(final boolean optionalServicesFound) {
		// this may notify user or show some views
	}

	@Override
	public void onDeviceReady() {
		startShowGraph();
	}

	@Override
	public void onHRSensorPositionFound(final String position) {
		setHRSPositionOnView(position);
	}

	@Override
	public void onHRValueReceived(int value) {
		mHrmValue = value;
		setHRSValueOnView((int) mHrmValue);
	}

	@Override
	public void onDeviceDisconnected() {
		super.onDeviceDisconnected();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mHRSValue.setText(R.string.not_available_value);
				mHRSPosition.setText(R.string.not_available);
				stopShowGraph();
			}
		});
	}

	@Override
	protected void setDefaultUI() {
		mHRSValue.setText(R.string.not_available_value);
		mHRSPosition.setText(R.string.not_available);
	}

	public void closeConnectScriba(View view) {
		this.moveTaskToBack(true);
	}
}
