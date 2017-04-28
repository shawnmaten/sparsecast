package com.shawnaten.simpleweather.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;
import com.shawnaten.simpleweather.tools.Attributions;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity {

    public static final int[][] data = {
            {R.drawable.ic_map_black_24dp, R.array.attr_location},
            {R.drawable.ic_cloud_black_24dp, R.array.attr_forecast},
//            {R.drawable.ic_photo_black_24dp, R.array.attr_image}
    };

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.list) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new AttributionAdapter());

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    }

    public class AttributionViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.icon) ImageView imageView;
        @Bind(R.id.primary) TextView primaryText;
        @Bind(R.id.secondary) TextView secondaryText;

        String url;

        public AttributionViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });
        }
    }

    public class AttributionAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.attribution, viewGroup, false);
            return new AttributionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            AttributionViewHolder holder = (AttributionViewHolder) viewHolder;

            holder.imageView.setImageResource(data[i][0]);

            String strings[] = getResources().getStringArray(data[i][1]);

            holder.primaryText.setText(strings[0]);

            switch (data[i][1]) {
                case R.array.attr_location:
                    holder.secondaryText.setText(strings[1]);
                    if (Attributions.getSavedPlaces().size() != 0) {
                        holder.secondaryText.setMovementMethod(LinkMovementMethod.getInstance());
                        holder.secondaryText.append(" ");
                        for (String attr : Attributions.getSavedPlaces())
                            holder.secondaryText.append(Html.fromHtml(attr) + " ");
                    }
                    holder.url = strings[2];
                    break;
                case R.array.attr_forecast:
                    holder.secondaryText.setText(strings[1]);
                    holder.url = strings[2];
                    break;
                case R.array.attr_image:
                    holder.secondaryText.setText(strings[1]);
                    holder.secondaryText.append(" @" + Attributions.getInstagramUser() + ".");
                    holder.url = Attributions.getInstagramUrl();
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return data.length;
        }
    }
}


