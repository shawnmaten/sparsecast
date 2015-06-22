package com.shawnaten.simpleweather.fragments.current;

import com.shawnaten.simpleweather.ui.Tab;

import javax.inject.Inject;

public class CurrentFragment extends Tab {
    @Inject
	public CurrentFragment() {
		
	}

    /*
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_current_main, container, false);
        ((TextView) view.findViewById(R.placeId.powered_by_forecast))
                .setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }
    */
}
