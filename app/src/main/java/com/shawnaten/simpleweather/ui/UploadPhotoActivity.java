package com.shawnaten.simpleweather.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shawnaten.simpleweather.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UploadPhotoActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    private Fragment initialFrag;
    private Fragment confirmFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_upload);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initialFrag = UploadInitialFragment.newInstance();
        confirmFrag = UploadConfirmFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment, confirmFrag)
                .add(R.id.fragment, initialFrag)
                .detach(confirmFrag)
                .commit();
    }

    public void onSelectPhotoClick(View button) {
        Toast.makeText(this, "Select photo clicked!", Toast.LENGTH_SHORT).show();
        getSupportFragmentManager()
                .beginTransaction()
                .detach(initialFrag)
                .attach(confirmFrag)
                .commit();
    }

    public void onSubmitClick(View button) {
        Toast.makeText(this, "Submit clicked!", Toast.LENGTH_SHORT).show();
    }

}


