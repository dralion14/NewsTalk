package cl.newstalk.library;

import cl.newstalk.LoginActivity;
import cl.newstalk.R;
import cl.newstalk.R.drawable;
import cl.newstalk.R.string;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Demonstrates how to show an AlertDialog that is managed by a Fragment.
 */
public class FragmentAlertDialog extends DialogFragment {

	public static FragmentAlertDialog newInstance(int title, int msg) {
		FragmentAlertDialog frag = new FragmentAlertDialog();
		Bundle args = new Bundle();
		args.putInt("title", title);
		args.putInt("msg", msg);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int title = getArguments().getInt("title");
		int msg = getArguments().getInt("msg"); 
		
		return new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.ic_stat_info)
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(R.string.alert_dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((LoginActivity) getActivity())
										.doPositiveClick();
							}
						})
				.setNegativeButton(R.string.alert_dialog_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((LoginActivity) getActivity())
										.doNegativeClick();
							}
						}).create();
	}
}
