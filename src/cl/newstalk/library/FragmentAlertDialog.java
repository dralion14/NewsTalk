package cl.newstalk.library;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import cl.newstalk.FeedActivity;
import cl.newstalk.LoginActivity;
import cl.newstalk.R;

/**
 * Demonstrates how to show an AlertDialog that is managed by a Fragment.
 */
public class FragmentAlertDialog extends DialogFragment {

	public static FragmentAlertDialog newInstance(int activity, int title, int msg) {
		FragmentAlertDialog frag = new FragmentAlertDialog();
		Bundle args = new Bundle();
		args.putInt("activity", activity);
		args.putInt("title", title);
		args.putInt("msg", msg);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int activity = getArguments().getInt("activity");
		int title = getArguments().getInt("title");
		int msg = getArguments().getInt("msg"); 
		
		Dialog dialog = null;
		
		if(activity == R.string.title_activity_feed) {
			dialog = new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.ic_stat_info)
			.setTitle(title)
			.setMessage(msg)
			.setPositiveButton(R.string.alert_dialog_ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							((FeedActivity) getActivity()).doPositiveClick();
						}
					})
			.setNegativeButton(R.string.alert_dialog_cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							((FeedActivity) getActivity()).doNegativeClick();
						}
					}).create();
		} else if(activity == R.string.title_activity_login) {
			dialog = new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.ic_stat_info)
			.setTitle(title)
			.setMessage(msg)
			.setPositiveButton(R.string.alert_dialog_ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							((LoginActivity) getActivity()).doPositiveClick();
						}
					})
			.setNegativeButton(R.string.alert_dialog_cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							((LoginActivity) getActivity()).doNegativeClick();
						}
					}).create();
		}
		
		return dialog;
	}
}
