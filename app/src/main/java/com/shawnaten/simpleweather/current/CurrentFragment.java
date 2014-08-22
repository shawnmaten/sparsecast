package com.shawnaten.simpleweather.current;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;
import com.shawnaten.tools.FragmentListener;

public class CurrentFragment extends Fragment implements FragmentListener, View.OnClickListener {
    private int frag2Selected = R.id.summary;

    private static final SparseArray<String> childFragNames = new SparseArray<>();
    static {
        childFragNames.put(R.id.stats, "stats");
        childFragNames.put(R.id.summary, "summary");
        childFragNames.put(R.id.details, "details");
        childFragNames.put(R.id.graphics, "graphics");
    }

	public CurrentFragment() {
		
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()

            .add(R.id.fragment_1, new StatsFragment(), childFragNames.valueAt(0))
            .add(R.id.fragment_2, new SummaryFragment(), childFragNames.valueAt(1))
            .add(R.id.fragment_2, new DetailsFragment(), childFragNames.valueAt(2))
            .add(R.id.fragment_3, new GraphicsFragment(), childFragNames.valueAt(3))

            .commit();
        } else  {
            frag2Selected = savedInstanceState.getInt("frag2Selected");
        }

    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_current_main, container, false);
        view.findViewById(R.id.summary).setOnClickListener(this);
        view.findViewById(R.id.details).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.powered_by_forecast)).setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        switchFragments();
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("frag2Selected", frag2Selected);
    }

    @Override
    public void onClick(View v) {
        frag2Selected = v.getId();
        switchFragments();
    }

    private void switchFragments() {
        FragmentManager fm = getChildFragmentManager();
        Fragment toDetach, toAttach;
        int toDetachId = 0, toAttachId = 0;

        switch (frag2Selected) {
            case R.id.summary:
                toDetachId = R.id.details;
                toAttachId = R.id.summary;
                break;
            case R.id.details:
                toDetachId = R.id.summary;
                toAttachId = R.id.details;
                break;
        }

        toDetach = fm.findFragmentByTag(childFragNames.get(toDetachId));
        toAttach = fm.findFragmentByTag(childFragNames.get(toAttachId));

        fm.beginTransaction().detach(toDetach).attach(toAttach).commit();
        //noinspection ConstantConditions
        ((TextView) getView().findViewById(toAttachId)).setTextColor(getResources().getColor(R.color.accent_87));
        ((TextView) getView().findViewById(toDetachId)).setTextColor(getResources().getColor(R.color.text_primary));

    }

    @Override
    public void onNewData() {
        for (int i = 0; i < childFragNames.size(); i++) {
            FragmentListener listener = (FragmentListener) getChildFragmentManager().findFragmentByTag(childFragNames.valueAt(i));
            if (listener != null) {
                listener.onNewData();
            }
        }
    }

}
