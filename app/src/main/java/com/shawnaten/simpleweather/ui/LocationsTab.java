package com.shawnaten.simpleweather.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;

public class LocationsTab extends Tab {
    LocationsAdapter adapter;

    public static LocationsTab newInstance(String title, int layout) {
        Bundle args = new Bundle();
        LocationsTab tab = new LocationsTab();
        args.putString(TabAdapter.TAB_TITLE, title);
        args.putInt(TAB_LAYOUT, layout);
        tab.setArguments(args);
        return tab;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.scroll);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new LocationsAdapter();
        recyclerView.setAdapter(adapter);
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        public TextView name;

        public ViewHolder(LinearLayout itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.name);
            name.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    private class LocationsAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            LinearLayout itemView = (LinearLayout) LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.location_list_item, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder holder = (ViewHolder) viewHolder;
            holder.name.setText("List Item #" + Integer.toString(i));
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }
}
