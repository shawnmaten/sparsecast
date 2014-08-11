package com.shawnaten.simpleweather.current;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.shawnaten.networking.Forecast;
import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.CustomAlertDialog;
import com.shawnaten.tools.FragmentListener;

public class CurrentFragment extends Fragment implements FragmentListener, PopupMenu.OnMenuItemClickListener {
    private Forecast.Response forecast;
    private Boolean newData = false;
    private MenuItem colorInfo;
	
	public CurrentFragment() {
		
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Fragment mFrag, sFrag, dFrag, gFrag;

        if (savedInstanceState == null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();

            mFrag = new MainStatsFragment();
            sFrag = new SummariesFragment();
            dFrag = new StatsFragment();
            gFrag = new GraphicsFragment();

            ft.add(R.id.fragment_1, mFrag, "mainStats");
            ft.add(R.id.fragment_2, sFrag, "summaries");
            ft.add(R.id.fragment_2, dFrag, "stats");
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
        dFrag = fm.findFragmentByTag("stats");
        gFrag = fm.findFragmentByTag("graphics");

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
                ft.add(R.id.fragment_2, dFrag, "stats");
                ft.detach(dFrag);
                ft.add(R.id.fragment_2, gFrag, "graphics");
                ft.detach(gFrag);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                ft.add(R.id.fragment_3, dFrag, "stats");
                ft.add(R.id.fragment_3, gFrag, "graphics");
                ft.detach(gFrag);
                break;
        }
        ft.attach(sFrag).commit();

        if (forecast != null) {
            //LinearLayout alertView = (LinearLayout) getView().findViewById(R.id.alert_view).findViewById(R.id.alert_view_content);

            mListen.onReceiveData(forecast);
            sListen.onReceiveData(forecast);
            dListen.onReceiveData(forecast);
            gListen.onReceiveData(forecast);

            //createAlertViews(alertView);
        }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_current, container, false);

        if (forecast != null) {
            //createAlertViews((LinearLayout) view.findViewById(R.id.alert_view).findViewById(R.id.alert_view_content));
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tab_current, menu);
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu) {
        colorInfo = menu.findItem(R.id.color_info);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popup:
                PopupMenu popup = new PopupMenu(getActivity(), getActivity().findViewById(R.id.popup));
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.tab_current_popup, popup.getMenu());
                switch (getResources().getConfiguration().orientation) {
                    case Configuration.ORIENTATION_LANDSCAPE:
                        popup.getMenu().removeItem(R.id.summaries);
                        break;
                }
                popup.setOnMenuItemClickListener(this);
                popup.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public void onReceiveData(Forecast.Response data) {
        forecast = data;
        if (isVisible())
            onResume();
	}

    @Override
    public void onButtonClick(int id) {
        /*
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
        */

        switch (id) {
            /*
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
                */
            case R.id.time:
                break;
            default:
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

        int i = 1;
        if (forecast.getAlerts() != null) {
            for (Forecast.Alert alert : forecast.getAlerts()) {
                TextView textView = (TextView) inflater.inflate(R.layout.alert_view_item, (ViewGroup) getView(), false);
                textView.setId(i++);
                textView.setText(alert.getTitle());
                layout.addView(textView);
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
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

        switch (item.getItemId()) {
            case R.id.summaries:
                toAttach = getChildFragmentManager().findFragmentByTag("summaries");
                ft.detach(toDetach).attach(toAttach).commit();
                colorInfo.setVisible(false);
                break;
            case R.id.stats:
                toAttach = getChildFragmentManager().findFragmentByTag("stats");
                ft.detach(toDetach).attach(toAttach).commit();
                colorInfo.setVisible(false);
                break;
            case R.id.graphics:
                toAttach = getChildFragmentManager().findFragmentByTag("graphics");
                ft.detach(toDetach).attach(toAttach).commit();
                colorInfo.setVisible(true);
                break;
        }
        return true;
    }
}
