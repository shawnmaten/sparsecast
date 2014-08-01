package com.shawnaten.main.current;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawnaten.main.R;
import com.shawnaten.networking.Forecast;
import com.shawnaten.tools.CustomAlertDialog;
import com.shawnaten.tools.ForecastTools;
import com.shawnaten.tools.FragmentListener;

import java.util.ArrayList;

public class CurrentFragment extends Fragment implements FragmentListener, View.OnClickListener {
    private static Forecast.Response forecast;
    private static Boolean newData = false;
    private static ArrayList<String> fragNames;
	
	public CurrentFragment() {
		
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            Fragment mFrag, sFrag, dFrag, gFrag;

            fragNames = new ArrayList<>();

            mFrag = new MainStatsFragment();
            ft.add(R.id.fragment_1, mFrag, "mainStats");

            sFrag = new SummariesFragment();
            ft.add(R.id.fragment_2, sFrag, "summaries");
            fragNames.add("summaries");

            dFrag = new DetailsFragment();
            ft.add(R.id.fragment_2, dFrag, "details");
            fragNames.add("details");
            ft.detach(dFrag);

            gFrag = new GraphicsFragment();
            ft.add(R.id.fragment_2, gFrag, "graphics");
            fragNames.add("graphics");
            ft.detach(gFrag);

            ft.commit();
        }

    }

	@Override
	public void onResume () {
		super.onResume();

        if (newData) {
            LinearLayout alertView = (LinearLayout) getView().findViewById(R.id.alert_view).findViewById(R.id.alert_view_content);
            FragmentListener mListen, sListen, dListen, gListen;
            Fragment mFrag, sFrag, dFrag, gFrag;

            mFrag = getChildFragmentManager().findFragmentByTag("mainStats");
            mListen = (FragmentListener) mFrag;
            mListen.onNewData(forecast);

            sFrag = getChildFragmentManager().findFragmentByTag("summaries");
            sListen = (FragmentListener) sFrag;
            sListen.onNewData(forecast);

            dFrag = getChildFragmentManager().findFragmentByTag("details");
            dListen = (FragmentListener) dFrag;
            dListen.onNewData(forecast);

            gFrag = getChildFragmentManager().findFragmentByTag("graphics");
            gListen = (FragmentListener) gFrag;
            gListen.onNewData(forecast);

            createAlertViews(alertView);
            setAlertText(alertView);
            newData = false;
        }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_current, container, false);

        ImageButton prev, next;
        prev = (ImageButton) view.findViewById(R.id.prev);
        next = (ImageButton) view.findViewById(R.id.next);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);

        if (forecast != null)
            createAlertViews((LinearLayout) view.findViewById(R.id.alert_view).findViewById(R.id.alert_view_content));
        return view;
    }

	@Override
	public void onNewData(Object data) {
        if (Forecast.Response.class.isInstance(data)) {
            forecast = (Forecast.Response) data;
            newData = true;
            if (isVisible())
                onResume();
        } else if (View.class.isInstance(data)) {
            int id = ((View) data).getId();

            if (id != R.id.time) {
                ((View) data).performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                CustomAlertDialog temp = new CustomAlertDialog();
                Bundle args = new Bundle();
                args.putString("title", forecast.getAlerts()[id-1].getTitle());
                args.putString("message", forecast.getAlerts()[id - 1].getDescription());
                args.putInt("code", 1);
                temp.setArguments(args);
                temp.show(getFragmentManager(), "current.alert");
            }
        }

	}

    private void createAlertViews(LinearLayout layout) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layout.removeAllViews();

        TextView textView = (TextView) inflater.inflate(R.layout.alert_view_item, null);
        textView.setId(R.id.time);
        layout.addView(textView);

        int i = 1;
        if (forecast.getAlerts() != null) {
            for (Forecast.Alert alert : forecast.getAlerts()) {
                textView = (TextView) inflater.inflate(R.layout.alert_view_item, null);
                textView.setId(i++);
                layout.addView(textView);
            }
        } else
            textView.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
    }

    private void setAlertText(LinearLayout layout) {
        TextView textView = (TextView) layout.findViewById(R.id.time);
        textView.setText(String.format("%s %s", getString(R.string.conditions), ForecastTools.timeForm.format(forecast.getCurrently().getTime())));

        int i = 1;
        if (forecast.getAlerts() != null) {
            for (Forecast.Alert alert : forecast.getAlerts()) {
                textView = (TextView) layout.findViewById(i++);
                textView.setText(alert.getTitle());
            }
        }
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getChildFragmentManager();
        String last = fragNames.get(fragNames.size() - 1), first = fragNames.get(0), second = fragNames.get(1);

        switch (v.getId()) {
            case R.id.prev:
                getChildFragmentManager().beginTransaction()
                        .detach(fm.findFragmentByTag(first))
                        .attach(fm.findFragmentByTag(last))
                        .commit();
                fragNames.remove(last);
                fragNames.add(0, last);
                break;
            case R.id.next:
                getChildFragmentManager().beginTransaction()
                        .detach(fm.findFragmentByTag(first))
                        .attach(fm.findFragmentByTag(second))
                        .commit();
                fragNames.remove(first);
                fragNames.add(first);
                break;
        }
    }
}
