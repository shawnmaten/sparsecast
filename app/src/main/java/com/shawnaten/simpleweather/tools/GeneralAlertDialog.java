package com.shawnaten.simpleweather.tools;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class GeneralAlertDialog extends DialogFragment {
    private static final String TAG = "tag";
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String NEGATIVE_BUTTON = "negative";
    private static final String POSITIVE_BUTTON = "positive";

    public static GeneralAlertDialog newInstance(String tag, String title, String message,
        String negativeString, String positiveString) {

        GeneralAlertDialog dialog = new GeneralAlertDialog();
        Bundle args = new Bundle();
        args.putString(TAG, tag);
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        if (negativeString != null)
            args.putString(NEGATIVE_BUTTON, negativeString);
        if (positiveString != null)
            args.putString(POSITIVE_BUTTON, positiveString);
        dialog.setArguments(args);
        return dialog;
    }

	@NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle args = getArguments();

        builder.setTitle(args.getString(TITLE));
        builder.setMessage(args.getString(MESSAGE));
        if (args.containsKey(NEGATIVE_BUTTON))
            builder.setNegativeButton(args.getString(NEGATIVE_BUTTON), new MyOnClickListener());
        if (args.containsKey(POSITIVE_BUTTON))
            builder.setNegativeButton(args.getString(POSITIVE_BUTTON), new MyOnClickListener());
        return builder.create();
    }

    private class MyOnClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    }
}
