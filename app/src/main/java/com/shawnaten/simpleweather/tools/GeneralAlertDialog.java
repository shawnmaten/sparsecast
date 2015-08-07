package com.shawnaten.simpleweather.tools;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.shawnaten.simpleweather.R;

public class GeneralAlertDialog extends DialogFragment {
    private static final String TAG = "tag", TITLE = "title", MESSAGE = "message",
            NEGATIVE_BUTTON = "negative", POSITIVE_BUTTON = "positive";

    public GeneralAlertDialog() {

    }

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

	@Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
        Bundle args = getArguments();
        builder.setTitle(args.getString(TITLE));
        builder.setMessage(args.getString(MESSAGE));
        if (args.containsKey(NEGATIVE_BUTTON)) {
            builder.setNegativeButton(args.getString(NEGATIVE_BUTTON), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        if (args.containsKey(POSITIVE_BUTTON)) {
            builder.setPositiveButton(args.getString(POSITIVE_BUTTON), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        dialog =  builder.create();
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    public interface OnClickListener {
        void onDialogClick(String tag, Boolean positive);
    }
	
}
