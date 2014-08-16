package com.shawnaten.simpleweather.current;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.FragmentListener;

public class CurrentFragment extends Fragment implements FragmentListener, View.OnClickListener {
    private int frag2Selected = R.id.summary;

    private SparseArray<String> childFragments = new SparseArray<>();

	public CurrentFragment() {
		
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment stFrag, smFrag, dFrag, gFrag;
        String names[] = getResources().getStringArray(R.array.tab_current_child_fragments);
        childFragments.put(R.id.stats, names[0]);
        childFragments.put(R.id.summary, names[1]);
        childFragments.put(R.id.details, names[2]);
        childFragments.put(R.id.graphics, names[3]);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();

            stFrag = new StatsFragment();
            smFrag = new SummaryFragment();
            dFrag = new DetailsFragment();
            gFrag = new GraphicsFragment();

            ft.add(R.id.fragment_1, stFrag, names[0]);
            ft.add(R.id.fragment_2, smFrag, names[1]);
            ft.add(R.id.fragment_2, dFrag, names[2]);
            ft.add(R.id.fragment_3, gFrag, names[3]);

            ft.commit();
        }

    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_current_main, container, false);
        view.findViewById(R.id.summary).setOnClickListener(this);
        view.findViewById(R.id.details).setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            frag2Selected = savedInstanceState.getInt("frag2Selected");
        }

        switchFragments();

    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("frag2Selected", frag2Selected);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        /*
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
        */
        return true;
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

    @Override
    public void onClick(View v) {
        frag2Selected = v.getId();
        switchFragments();
    }

    private void switchFragments() {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        int toDetach = 0, toAttach = 0;

        switch (frag2Selected) {
            case R.id.summary:
                toDetach = R.id.details;
                toAttach = R.id.summary;
                break;
            case R.id.details:
                toDetach = R.id.summary;
                toAttach = R.id.details;
                break;
        }

        ft.detach(fm.findFragmentByTag(childFragments.get(toDetach)));
        ft.attach(fm.findFragmentByTag(childFragments.get(toAttach)));
        //noinspection ConstantConditions
        ((TextView) getView().findViewById(toAttach)).setTextColor(getResources().getColor(R.color.accent_87));
        ((TextView) getView().findViewById(toDetach)).setTextColor(getResources().getColor(R.color.text_primary));

        ft.commit();
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
