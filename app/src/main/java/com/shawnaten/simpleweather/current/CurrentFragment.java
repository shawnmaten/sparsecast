package com.shawnaten.simpleweather.current;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawnaten.networking.Forecast;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.CustomAlertDialog;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.FragmentListener;

import static junit.framework.Assert.assertNotNull;

public class CurrentFragment extends Fragment implements FragmentListener {
    private static Forecast.Response forecast;
    private static Boolean newData = false;
	
	public CurrentFragment() {
		
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment mFrag, sFrag, dFrag, gFrag;

        if (savedInstanceState == null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();

            mFrag = new MainStatsFragment();
            sFrag = new SummariesFragment();
            dFrag = new DetailsFragment();
            gFrag = new GraphicsFragment();

            ft.add(R.id.fragment_1, mFrag, "mainStats");
            ft.add(R.id.fragment_2, sFrag, "summaries");
            ft.add(R.id.fragment_2, dFrag, "details");
            ft.add(R.id.fragment_2, gFrag, "graphics");

            ft.commit();
        }

    }

	@Override
	public void onResume () {
		super.onResume();

        Fragment mFrag, sFrag, dFrag, gFrag;
        FragmentListener mListen, sListen, dListen, gListen;
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        mFrag = fm.findFragmentByTag("mainStats");
        sFrag = fm.findFragmentByTag("summaries");
        dFrag = fm.findFragmentByTag("details");
        gFrag = fm.findFragmentByTag("graphics");

        assertNotNull(mFrag);
        assertNotNull(sFrag);
        assertNotNull(dFrag);
        assertNotNull(gFrag);

        mListen = (FragmentListener) mFrag;
        sListen = (FragmentListener) sFrag;
        dListen = (FragmentListener) dFrag;
        gListen = (FragmentListener) gFrag;

        ft.remove(dFrag);
        ft.remove(gFrag);
        ft.commit();
        fm.executePendingTransactions();

        ft = fm.beginTransaction();
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                ft.add(R.id.fragment_2, dFrag, "details");
                ft.detach(dFrag);
                ft.add(R.id.fragment_2, gFrag, "graphics");
                ft.detach(gFrag);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                ft.add(R.id.fragment_3, dFrag, "details");
                ft.add(R.id.fragment_3, gFrag, "graphics");
                ft.detach(gFrag);
                break;
        }
        ft.attach(sFrag).commit();

        if (newData) {
            LinearLayout alertView = (LinearLayout) getView().findViewById(R.id.alert_view).findViewById(R.id.alert_view_content);

            mListen.onNewData(forecast);
            sListen.onNewData(forecast);
            dListen.onNewData(forecast);
            gListen.onNewData(forecast);

            createAlertViews(alertView);
            newData = false;
        }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_current, container, false);

        if (forecast != null)
            createAlertViews((LinearLayout) view.findViewById(R.id.alert_view).findViewById(R.id.alert_view_content));
        return view;
    }

	@Override
	public void onNewData(Forecast.Response data) {
        forecast = data;
        newData = true;
        if (isVisible())
            onResume();
	}

    @Override
    public void onButtonClick(View view) {
        int id = view.getId();
        Fragment toDetach = null, toAttach;
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                toDetach = fm.findFragmentById(R.id.fragment_2);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                toDetach = fm.findFragmentById(R.id.fragment_3);
                break;
        }

        switch (view.getId()) {
            case R.id.summaries_button:
                toAttach = getChildFragmentManager().findFragmentByTag("summaries");
                ft.detach(toDetach).attach(toAttach).commit();
                break;
            case R.id.details_button:
                toAttach = getChildFragmentManager().findFragmentByTag("details");
                ft.detach(toDetach).attach(toAttach).commit();
                break;
            case R.id.graphics_button:
                toAttach = getChildFragmentManager().findFragmentByTag("graphics");
                ft.detach(toDetach).attach(toAttach).commit();
                break;
            case R.id.time:
                break;
            default:
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                CustomAlertDialog temp = new CustomAlertDialog();
                Bundle args = new Bundle();
                args.putString("title", forecast.getAlerts()[id-1].getTitle());
                args.putString("message", forecast.getAlerts()[id - 1].getDescription());
                args.putInt("code", 1);
                temp.setArguments(args);
                temp.show(getFragmentManager(), "current.alert");
        }
    }

    private void createAlertViews(LinearLayout layout) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layout.removeAllViews();

        TextView textView = (TextView) inflater.inflate(R.layout.alert_view_item, (ViewGroup) getView(), false);
        textView.setId(R.id.time);
        textView.setText(String.format("%s %s", getString(R.string.conditions), ForecastTools.timeForm.format(forecast.getCurrently().getTime())));
        layout.addView(textView);

        int i = 1;
        if (forecast.getAlerts() != null) {
            for (Forecast.Alert alert : forecast.getAlerts()) {
                textView = (TextView) inflater.inflate(R.layout.alert_view_item, (ViewGroup) getView(), false);
                textView.setId(i++);
                textView.setText(alert.getTitle());
                layout.addView(textView);
            }
        } else
            textView.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
    }

}
