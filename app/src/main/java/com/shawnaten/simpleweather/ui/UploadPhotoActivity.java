package com.shawnaten.simpleweather.ui;

import android.content.Context;
import android.content.Intent;
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
import com.shawnaten.simpleweather.lib.model.APIKeys;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.filepicker.Filepicker;
import io.filepicker.models.FPFile;

public class UploadPhotoActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    private Fragment initialFrag;
    private Fragment confirmFrag;

    private static String[] services = {
            "GALLERY",
            "CAMERA",
            "FACEBOOK",
            "CLOUDDRIVE",
            "DROPBOX",
            "BOX",
            "INSTAGRAM",
            "FLICKR",
            "GOOGLE_DRIVE",
            "SKYDRIVE",
    };

    private static String[] mimetypes = {"image/*"};

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

        Filepicker.setKey(APIKeys.FILESTACK);
        Filepicker.setAppName(getString(R.string.app_name));
    }

    public void onSelectPhotoClick(View button) {
//        Toast.makeText(this, "Select photo clicked!", Toast.LENGTH_SHORT).show();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .detach(initialFrag)
//                .attach(confirmFrag)
//                .commit();
        Intent intent = new Intent(this, Filepicker.class);
        intent.putExtra("services", services);
        intent.putExtra("mimetype", mimetypes);
        startActivityForResult(intent, Filepicker.REQUEST_CODE_GETFILE);
    }

    public void onSubmitClick(View button) {
        Toast.makeText(this, "Submit clicked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Filepicker.REQUEST_CODE_GETFILE) {
            if(resultCode == RESULT_OK) {

                // Filepicker always returns array of FPFile objects
                ArrayList<FPFile> fpFiles = data.getParcelableArrayListExtra(Filepicker.FPFILES_EXTRA);

                // Option multiple was not set so only 1 object is expected
                FPFile file = fpFiles.get(0);
                String key = file.getKey();

                Toast.makeText(this, key != null ? key : "no key", Toast.LENGTH_SHORT).show();

                // Do something cool with the result
            } else {
                // Handle errors here
            }

        }
    }

}


