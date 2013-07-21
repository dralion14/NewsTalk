/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * 
 * Modified by: Gabriel Salazar
 * */
package cl.newstalk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cl.newstalk.library.ConnectionDetector;
import cl.newstalk.library.DatabaseHandler;
import cl.newstalk.library.FragmentAlertDialog;
import cl.newstalk.library.UserFunctions;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */

	// JSON Response node names
	private static String KEY_RETURN = "return";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_PASSWORD = "password";
	private static String KEY_CREATED_AT = "created_at";
	private static String KEY_ID = "id";
	private static String KEY_SOURCE_ID = "source_id";
	private static String KEY_URL = "url";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mEmailView = (EditText) findViewById(R.id.email);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(
				new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.sign_in, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				Intent preference = new Intent(getApplicationContext(), SettingsActivity.class);
				// preference .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(preference);
				// Closing login screen
				// finish();
				return true;
			default:
				return false;
		}
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}
cancel = false;
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
			if (cd.isConnectingToInternet()) {
				// Show a progress spinner, and kick off a background task to
				// perform the user login attempt.
				mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
				showProgress(true);
				mAuthTask = new UserLoginTask();
				//mAuthTask.execute(mEmail, mPassword);
				mAuthTask.execute("dralion@newstalk.cl", "dralion");
			} else {
				Log.e(MainActivity.TAG, "No hay conexion");
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
				// showDialog(R.string.network_error, R.string.network_error_msg);
			}
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {

						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {

						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO: attempt authentication against a network service.
			JSONObject json = null;

			// Verify network access.
			ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
			if (cd.isConnectingToInternet()) {
				UserFunctions userFunction = new UserFunctions();

				json = userFunction.loginUser(params[0], params[1]);
			} else {
				Log.e(MainActivity.TAG, "No hay conexion");
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
				// showDialog(R.string.network_error, R.string.network_error_msg);
			}

			return json;
		}

		@Override
		protected void onPostExecute(final JSONObject json) {
			mAuthTask = null;
			showProgress(false);

			if (json != null) {
				// Log.d(MainActivity.TAG, json.toString());
				int success, i, total;
				UserFunctions userFunction = new UserFunctions();

				try {
					success = json.getInt(KEY_RETURN);

					if (success == 0) {
						// Login correcto
						// Clear all previous data in database
						DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						JSONObject json_aux = json.getJSONObject("user");

						userFunction.logoutUser(getApplicationContext());

						db.addUser(json_aux.getString(KEY_NAME),
								json_aux.getString(KEY_EMAIL),
								json_aux.getString(KEY_UID),
								json_aux.getString(KEY_CREATED_AT));

						JSONArray json_sources = json.getJSONArray("sources");
						total = json_sources.length();
						for (i = 0; i < total; i++) {
							json_aux = (JSONObject) json_sources.get(i);
							db.addSource(json_aux.getInt(KEY_ID),
									json_aux.getString(KEY_NAME),
									json_aux.getString(KEY_URL));
						}

						JSONArray json_feeds = json.getJSONArray("feeds");
						total = json_feeds.length();
						for (i = 0; i < total; i++) {
							json_aux = (JSONObject) json_feeds.get(i);
							db.addFeed(json_aux.getInt(KEY_ID),
									json_aux.getInt(KEY_SOURCE_ID),
									json_aux.getString(KEY_NAME),
									json_aux.getString(KEY_URL));
						}

						JSONArray json_feeds_active = json.getJSONArray("feed_user");
						total = json_feeds_active.length();
						for (i = 0; i < total; i++) {
							json_aux = (JSONObject) json_sources.get(i);
							db.activeFeed(json_aux.getInt(KEY_ID));
						}

						// Launch Main Screen
						Intent main = new Intent(getApplicationContext(), MainActivity.class);
						main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(main);
						finish();

					} else if (success == 1) {
						// Email no registrado
						// Launch Register Screen
						Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
						register.putExtra(KEY_EMAIL, mEmail);
						register.putExtra(KEY_PASSWORD, mPassword);
						register.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(register);
						finish();

					} else if (success == 2) {
						// Password incorrecto
						mPasswordView.setError(getString(R.string.error_incorrect_password));
						mPasswordView.requestFocus();
					}
				} catch (JSONException e) {
					Log.e(MainActivity.TAG, "Error JSON");
				} catch (Exception e) {
					Log.e(MainActivity.TAG, "Error");
				}
			} else {
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
				// showDialog(R.string.network_error, R.string.json_error_msg);
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	// BEGIN_INCLUDE(activity)
	void showDialog(int title, int msg) {
		DialogFragment newFragment = FragmentAlertDialog.newInstance(title, msg);
		newFragment.show(getFragmentManager(), "dialog");
	}

	public void doPositiveClick() {
		// Do stuff here.
		Log.i(MainActivity.TAG, "Positive click!");
	}

	public void doNegativeClick() {
		// Do stuff here.
		Log.i(MainActivity.TAG, "Negative click!");
	}

}