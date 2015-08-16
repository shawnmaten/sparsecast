package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.Response;
import com.shawnaten.simpleweather.backend.savedPlaceApi.model.SavedPlace;
import com.shawnaten.simpleweather.tools.Attributions;
import com.shawnaten.simpleweather.tools.LocationSettings;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.functions.Action1;

import static butterknife.ButterKnife.findById;

public class SearchTab extends Tab implements SearchView.OnQueryTextListener {

    static final LatLngBounds bounds = new LatLngBounds(new LatLng(-85,-180), new LatLng(85,180));

    @Inject ReactiveLocationProvider locationProvider;
    @Bind(R.id.list) RecyclerView recyclerView;

    private SearchAdapter adapter;
    private SearchView searchView;
    private ArrayList<String> placeIds = new ArrayList<>();
    private ArrayList<String> descriptions = new ArrayList<>();
    private List<SavedPlace> saved;

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

        getApp().getMainComponent().injectSearchTab(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        searchView = findById(getBaseActivity(), R.id.search_view);
        searchView.setOnQueryTextListener(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SearchAdapter();
        recyclerView.setAdapter(adapter);

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

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        locationProvider
                .getPlaceAutocompletePredictions(newText, bounds, null)
                .subscribe(new Action1<AutocompletePredictionBuffer>() {
                    @Override
                    public void call(AutocompletePredictionBuffer buffer) {

                        if (buffer.getCount() == 0) {
                            placeIds.clear();
                            descriptions.clear();

                            // adapter.notifyItemRangeRemoved(smaller.getCount(),larger.getCount());
                            // this should work but there's a bug in RecyclerView
                            // using notifyDataSetChanged instead for empty case
                            adapter.notifyDataSetChanged();

                            return;
                        }

                        for (int i = 0; i < Math.min(placeIds.size(), buffer.getCount()); i++) {
                            if (!placeIds.get(i).equals(buffer.get(i).getPlaceId())) {
                                placeIds.set(i, buffer.get(i).getPlaceId());
                                descriptions.set(i, buffer.get(i).getDescription());
                                adapter.notifyItemChanged(i);
                            }
                        }

                        if (placeIds.size() < buffer.getCount()) {
                            int index = placeIds.size();
                            int count = buffer.getCount() - placeIds.size();

                            for (int i = index; i < buffer.getCount(); i++) {
                                placeIds.add(i, buffer.get(i).getPlaceId());
                                descriptions.add(i, buffer.get(i).getDescription());
                            }

                            adapter.notifyItemRangeInserted(index, count);

                        } else {
                            int index = buffer.getCount();
                            int count = placeIds.size() - buffer.getCount();

                            for (int i = index; i < buffer.getCount(); i++) {
                                placeIds.remove(i);
                                descriptions.remove(i);
                            }

                            adapter.notifyItemRangeRemoved(index, count);
                        }

                        buffer.release();
                        recyclerView.smoothScrollToPosition(0);
                    }
                });

        return true;
    }

    @Override
    public void onNewData(Object data) {
        super.onNewData(data);

        if (data instanceof Response)
            saved = ((Response) data).getData();
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name) TextView nameView;
        int id;

        public NormalViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.root)
        @SuppressWarnings("unused")
        public void onClick(View view) {
            locationProvider
                    .getPlaceById(placeIds.get(id))
                    .subscribe(new Action1<PlaceBuffer>() {
                        @Override
                        public void call(PlaceBuffer places) {
                            Place place = places.get(0);

                            if (saved != null) {
                                for (SavedPlace save : saved)
                                    if (save.getPlaceId().equals(place.getId())) {
                                        LocationSettings.setPlace(save);
                                        Attributions.setCurrentPlace(save.getAttributions());
                                    }
                            } else
                                LocationSettings.setPlace(place, places.getAttributions());

                            places.release();
                            getActivity().setResult(MainActivity.PLACE_SELECTED_CODE);
                            getActivity().finish();
                        }
                    });
        }
    }

    public class SearchAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater
                    .from(viewGroup.getContext())
                    .inflate(R.layout.search_result, viewGroup, false);
            return new NormalViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            NormalViewHolder holder = (NormalViewHolder) viewHolder;
            holder.id = i;
            holder.nameView.setText(descriptions.get(i));
        }

        @Override
        public int getItemCount() {
            return placeIds.size();
        }
    }
}
