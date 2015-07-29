package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.SavedPlace;
import com.shawnaten.tools.LocationSettings;

import java.util.ArrayList;
import java.util.List;

public class SavedPlacesTab extends Tab {
    private SearchAdapter adapter;
    private GoogleApiClient googleApiClient;
    private final ArrayMap<SavedPlace, String> savedPlaces = new ArrayMap<>();
    private ArrayList<String> attributions = new ArrayList<>();

    public static SavedPlacesTab newInstance(String title, int layout) {
        Bundle args = new Bundle();
        SavedPlacesTab tab = new SavedPlacesTab();
        args.putString(TabAdapter.TAB_TITLE, title);
        args.putInt(TAB_LAYOUT, layout);
        tab.setArguments(args);
        return tab;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApiClient = getApp().mainComponent.googleApiClient();
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

        if (List.class.isInstance(data) && SavedPlace.class.isInstance(((List) data).get(0))) {
            final List<SavedPlace> ids = (List<SavedPlace>) data;
            savedPlaces.clear();

            for (final SavedPlace id : ids) {
                PendingResult result = Places.GeoDataApi.getPlaceById(googleApiClient,
                        id.getPlaceId());

                result.setResultCallback(new ResultCallback() {
                    @Override
                    public void onResult(Result result1) {
                        String name;
                        PlaceBuffer placeBuffer = (PlaceBuffer) result1;
                        Place place = placeBuffer.get(0);
                        if (place.getPlaceTypes().contains(Place.TYPE_POLITICAL))
                            name = place.getAddress().toString();
                        else
                            name = place.getName().toString();

                        CharSequence attribution = placeBuffer.getAttributions();
                        if (attribution != null && !attributions.contains(attribution.toString())) {
                            attributions.add(attribution.toString());
                            adapter.notifyItemChanged(adapter.getItemCount() - 1);
                        }
                        placeBuffer.release();

                        synchronized (savedPlaces) {
                            savedPlaces.put(id, name);
                            if (savedPlaces.size() == ids.size())
                                adapter.notifyItemRangeInserted(0, savedPlaces.size());
                        }
                    }
                });
            }
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
                    PendingResult result = Places.GeoDataApi.getPlaceById(
                            getApp().mainComponent.googleApiClient(),
                            savedPlaces.keyAt(id).getPlaceId());

                    result.setResultCallback(new ResultCallback() {
                        @Override
                        public void onResult(Result result1) {
                            PlaceBuffer placeBuffer = (PlaceBuffer) result1;
                            Place place = placeBuffer.get(0);
                            LocationSettings.setPlace(place, true, placeBuffer.getAttributions());
                            placeBuffer.release();
                            getActivity().setResult(MainActivity.PLACE_SELECTED_CODE);
                            getActivity().finish();
                        }
                    });
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
                            .inflate(R.layout.attributions_google_and_third_party, viewGroup, false);
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
                    holder.nameView.setText(savedPlaces.valueAt(i));
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
