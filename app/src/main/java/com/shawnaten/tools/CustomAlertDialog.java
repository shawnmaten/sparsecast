package com.shawnaten.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.shawnaten.simpleweather.R;

import junit.framework.Assert;

public class CustomAlertDialog extends DialogFragment {
	private CustomAlertListener customAlertListener;
    private int code;

	@Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        code = args.getInt("code");
        builder.setTitle(args.getString("title"));
        builder.setMessage(args.getString("message"));
        Assert.assertNotNull(customAlertListener);
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                customAlertListener.onDialogClosed(code);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);

        try {
            customAlertListener = (CustomAlertListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement CustomAlertListener");
        }
    }

    public interface CustomAlertListener {
        public void onDialogClosed(int code);
    }
	
}
