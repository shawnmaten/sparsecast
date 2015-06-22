package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.SavedPlace;
import com.shawnaten.tools.LocationSettings;

import java.util.List;

public class SearchTab extends Tab implements SearchView.OnQueryTextListener,
        ResultCallback<AutocompletePredictionBuffer> {
    private SearchAdapter adapter;
    private AutocompletePredictionBuffer predictions;

    private SearchView searchView;
    private RecyclerView recyclerView;

    private List<SavedPlace> savedPlaces;

    public static SearchTab newInstance(String title, int layout) {
        Bundle args = new Bundle();
        SearchTab tab = new SearchTab();
        args.putString(TabAdapter.TAB_TITLE, title);
        args.putInt(TAB_LAYOUT, layout);
        tab.setArguments(args);
        return tab;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = (SearchView) getActivity().findViewById(R.id.search_view);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SearchAdapter();
        recyclerView.setAdapter(adapter);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            if (searchView != null) {
                searchView.setVisibility(View.VISIBLE);
                searchView.setIconified(false);
            }
        } else {
            if (searchView != null) {
                searchView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        PendingResult result = Places.GeoDataApi.getAutocompletePredictions(
            getApp().getNetworkComponent().googleApiClient(),
            newText,
            new LatLngBounds(new LatLng(-85, -180), new LatLng(85, 180)),
            null);
        result.setResultCallback(this);
        return false;
    }

    @Override
    public void onResult(AutocompletePredictionBuffer predictions) {
        if (this.predictions == null) {
            this.predictions = predictions;
            adapter.notifyItemRangeInserted(0, predictions.getCount());
        } else {
            AutocompletePredictionBuffer old, smaller, larger;

            old = this.predictions;

            if (this.predictions.getCount() <= predictions.getCount()) {
                smaller = this.predictions;
                larger = predictions;
            } else {
                smaller = predictions;
                larger = this.predictions;
            }

            this.predictions = predictions;

            for (int i = 0; i < smaller.getCount(); i++)
                if (!smaller.get(i).equals(larger.get(i)))
                    adapter.notifyItemChanged(i);
            if (smaller.getCount() != larger.getCount()) {
                if (old.equals(smaller))
                    adapter.notifyItemRangeInserted(smaller.getCount(),
                            larger.getCount() - smaller.getCount());
                else {
                    // This should work but there's a bug in RecyclerView, using
                    // notifyDataSetChanged instead
                    //adapter.notifyItemRangeRemoved(smaller.getCount(), larger.getCount());
                    adapter.notifyDataSetChanged();
                }
            }

            recyclerView.smoothScrollToPosition(0);
            old.release();
        }
    }

    private class NormalViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView;
        public View actionFavorite;
        public int id;
        public Place place;
        public SavedPlace savedPlace;
        public PlaceBuffer placeBuffer;

        public NormalViewHolder(View listItemView) {

            super(listItemView);
            this.nameView = (TextView) listItemView.findViewById(R.id.name);
            listItemView.setOnClickListener(view -> {
                PendingResult result = Places.GeoDataApi.getPlaceById(
                        getApp().getNetworkComponent().googleApiClient(),
                        predictions.get(id).getPlaceId());

                result.setResultCallback(result1 -> {
                    placeBuffer = (PlaceBuffer) result1;
                    place = placeBuffer.get(0);

                    LocationSettings.setPlace(place, null, placeBuffer.getAttributions());

                    for (SavedPlace savedPlace : savedPlaces) {
                        if (savedPlace.getPlaceId().equals(place.getId()))
                            LocationSettings.setPlace(place, savedPlace,
                                    placeBuffer.getAttributions());
                    }

                    placeBuffer.release();
                    getActivity().finish();
                });
            });
        }
    }

    private class AttributionsViewHolder extends RecyclerView.ViewHolder {
        public AttributionsViewHolder(View attributionsView) {
            super(attributionsView);
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter {
        private static final int NORMAL_TYPE = 0;
        private static final int ATTRIBUTIONS_TYPE = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            switch (viewType) {
                case ATTRIBUTIONS_TYPE:
                    View attributionView = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.attributions_google_only, viewGroup, false);
                    return new AttributionsViewHolder(attributionView);
                default:
                    View listItemView = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.search_result, viewGroup, false);
                    return new NormalViewHolder(listItemView);
            }
        }



        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            switch (getItemViewType(i)) {
                case ATTRIBUTIONS_TYPE:
                    break;
                default:
                    NormalViewHolder holder = (NormalViewHolder) viewHolder;
                    holder.id = i;
                    holder.nameView.setText(predictions.get(i).getDescription());
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return predictions != null && !predictions.isClosed() && predictions.getCount() > 0
                    ? predictions.getCount() + 1 : 0;
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (predictions != null)
            predictions.release();
    }

    @Override
    public void onNewData(Object data) {
        super.onNewData(data);

        if (List.class.isInstance(data) && SavedPlace.class.isInstance(((List) data).get(0))) {
            savedPlaces = (List<SavedPlace>) data;
        }
    }
}
