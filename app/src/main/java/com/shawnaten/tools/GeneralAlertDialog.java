package com.shawnaten.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class GeneralAlertDialog extends DialogFragment {
    private static final String TAG = "tag", TITLE = "title", MESSAGE = "message",
            NEGATIVE_BUTTON = "negative", POSITIVE_BUTTON = "positive";

    public GeneralAlertDialog() {

    }

    public static GeneralAlertDialog newInstance(String tag, String title, String message, String negativeButton, String positiveButton) {
        GeneralAlertDialog dialog = new GeneralAlertDialog();
        Bundle args = new Bundle();
        args.putString(TAG, tag);
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        if (negativeButton != null)
            args.putString(NEGATIVE_BUTTON, negativeButton);
        if (positiveButton != null)
            args.putString(POSITIVE_BUTTON, positiveButton);
        dialog.setArguments(args);
        return dialog;
    }

	@Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Holo_Dialog);
        Bundle args = getArguments();
        builder.setTitle(args.getString(TITLE));
        builder.setMessage(args.getString(MESSAGE));
        if (args.containsKey(NEGATIVE_BUTTON)) {
            builder.setNegativeButton(args.getString(NEGATIVE_BUTTON), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((OnClickListener) getActivity()).onDialogClick(getArguments().getString(TAG), false);
                }
            });
        }
        if (args.containsKey(POSITIVE_BUTTON)) {
            builder.setPositiveButton(args.getString(POSITIVE_BUTTON), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((OnClickListener) getActivity()).onDialogClick(getArguments().getString(TAG), true);
                }
            });
        }
        dialog =  builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    public interface OnClickListener {
        public void onDialogClick(String tag, Boolean positive);
    }
	
}
