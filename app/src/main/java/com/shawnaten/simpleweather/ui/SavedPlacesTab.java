package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.Response;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.SavedPlace;
import com.shawnaten.simpleweather.tools.LocationSettings;

import java.util.ArrayList;
import java.util.List;

public class SavedPlacesTab extends Tab {
    private SearchAdapter adapter;

    private List<SavedPlace> savedPlaces;

    private ArrayList<String> attributions = new ArrayList<>();

    public static SavedPlacesTab newInstance(String title, int layout) {
        Bundle args = new Bundle();
        SavedPlacesTab tab = new SavedPlacesTab();
        args.putString(TabAdapter.TAB_TITLE, title);
        args.putInt(TAB_LAYOUT, layout);
        tab.setArguments(args);
        tab.analyticsTrackName = "SavedPlacesTab";
        return tab;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new SearchAdapter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        RecyclerView recyclerView;

        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onNewData(Object data) {
        super.onNewData(data);

        if (data instanceof Response) {

            savedPlaces = ((Response) data).getData();

            adapter.notifyItemRangeInserted(0, savedPlaces.size());

        }
    }

    private class NormalViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView;
        public int id;

        public NormalViewHolder(View listItemView) {
            super(listItemView);
            this.nameView = (TextView) listItemView.findViewById(R.id.name);
            listItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LocationSettings.setPlace(savedPlaces.get(id), null);
                    getActivity().setResult(MainActivity.PLACE_SELECTED_CODE);
                    getActivity().finish();

                }
            });
        }
    }

    private class AttributionsViewHolder extends RecyclerView.ViewHolder {
        public TextView thirdPartyAttributions;

        public AttributionsViewHolder(View attributionsView) {
            super(attributionsView);

            thirdPartyAttributions = (TextView) attributionsView.findViewById(R.id.third_party);
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter {
        private static final int NORMAL_TYPE = 0;
        private static final int ATTRIBUTIONS_TYPE = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            switch (viewType) {
                case ATTRIBUTIONS_TYPE:
                    View attributionsView = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.attributions_google_third, viewGroup, false);
                    return new AttributionsViewHolder(attributionsView);
                default:
                    View listItemView = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.saved_place_item, viewGroup, false);
                    return new NormalViewHolder(listItemView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            switch (getItemViewType(i)) {
                case ATTRIBUTIONS_TYPE:
                    AttributionsViewHolder attributionsHolder = (AttributionsViewHolder) viewHolder;
                    attributionsHolder.thirdPartyAttributions.setText(null);
                    for (String attribution : attributions) {
                        attributionsHolder.thirdPartyAttributions
                                .append(Html.fromHtml(attribution) + "\n");
                    }
                    break;
                default:
                    final NormalViewHolder holder = (NormalViewHolder) viewHolder;
                    holder.id = i;
                    holder.nameView.setText(savedPlaces.get(i).getName());
            }
        }

        @Override
        public int getItemCount() {
            return savedPlaces != null && savedPlaces.size() > 0 ? savedPlaces.size() + 1: 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return ATTRIBUTIONS_TYPE;
            }
            else
                return NORMAL_TYPE;
        }
    }
}
