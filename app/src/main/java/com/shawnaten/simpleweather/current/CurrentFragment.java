package com.shawnaten.simpleweather.current;

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

import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.FragmentListener;

public class CurrentFragment extends Fragment implements FragmentListener {
    private boolean frag2ToDetach = false;
    private int frag2Selected = R.id.summaries;

	public CurrentFragment() {
		
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Fragment stFrag, smFrag, iFrag, gFrag;

        if (savedInstanceState == null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();

            stFrag = new StatsFragment();
            smFrag = new SummariesFragment();
            iFrag = new InfoFragment();
            gFrag = new GraphicsFragment();

            ft.add(R.id.fragment_1, stFrag, "stats");
            ft.add(R.id.fragment_2, smFrag, "summaries");
            ft.add(R.id.fragment_2, iFrag, "info");
            ft.add(R.id.fragment_3, gFrag, "graphics");
            frag2ToDetach = true;

            ft.commit();
        }

    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_current_main, container, false);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tab_current, menu);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (frag2ToDetach || savedInstanceState != null) {
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            if (savedInstanceState != null) {
                frag2Selected = savedInstanceState.getInt("frag2Selected");
            }

            switch (frag2Selected) {
                case R.id.summaries:
                    ft.detach(fm.findFragmentByTag("info")).commit();
                    break;
                case R.id.info:
                    ft.detach(fm.findFragmentByTag("summaries")).commit();
                    break;
            }

            frag2ToDetach = false;
        }

    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("frag2Selected", frag2Selected);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_switch:
                FragmentManager fm = getChildFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                switch (frag2Selected) {
                    case R.id.summaries:
                        ft.detach(fm.findFragmentByTag("summaries")).attach(fm.findFragmentByTag("info")).commit();
                        frag2Selected = R.id.info;
                        break;
                    case R.id.info:
                        ft.detach(fm.findFragmentByTag("info")).attach(fm.findFragmentByTag("summaries")).commit();
                        frag2Selected = R.id.summaries;
                        break;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onButtonClick(int id) {
        /*
        switch (id) {
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
        */
    }

    /*
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
    */

}
