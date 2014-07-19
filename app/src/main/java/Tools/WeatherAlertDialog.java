package Tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class WeatherAlertDialog extends DialogFragment {
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(args.getString("title")).setMessage(args.getString("message"))
        .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int id) {
        		
        	}
        	});
        return builder.create();
    }
	
}
