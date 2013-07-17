package cl.newstalk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cl.newstalk.library.ConnectionDetector;
import cl.newstalk.library.DatabaseHandler;
import cl.newstalk.library.UserFunctions;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class RegisterActivity extends Activity {

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
	private String mName;
	private String mEmail;
	private String mPassword;
	private String mRepeatPassword;

	// UI references.
	private EditText mNameView;
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mRepeatPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);

		Bundle bundle = getIntent().getExtras();

		// Set up the register form.
		mNameView = (EditText) findViewById(R.id.name);
		mNameView.setText(bundle.getString("email").substring(0, bundle.getString("email").indexOf("@")));

		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(bundle.getString("email"));

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText(bundle.getString("password"));

		mRepeatPasswordView = (EditText) findViewById(R.id.repeat_password);
		mRepeatPasswordView.setOnEditorActionListener(
				new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.register || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});
		mRepeatPasswordView.requestFocus();

		mLoginFormView = findViewById(R.id.register_form);
		mLoginStatusView = findViewById(R.id.register_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.register_status_message);

		findViewById(R.id.register_button).setOnClickListener(
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
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
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
		mName = mNameView.getText().toString();
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mRepeatPassword = mRepeatPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mRepeatPassword)) {
			mRepeatPasswordView.setError(getString(R.string.error_register_field_required));
			focusView = mRepeatPasswordView;
			cancel = true;
		} else if (!mRepeatPassword.equals(mPassword)) {
			mRepeatPasswordView.setError(getString(R.string.error_register_incorrect_password));
			focusView = mRepeatPasswordView;
			cancel = true;
		}

		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_register_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_register_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_register_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_register_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		// Check for a valid name.
		if (TextUtils.isEmpty(mName)) {
			mNameView.setError(getString(R.string.error_register_field_required));
			focusView = mNameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_register);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute(mName, mEmail, mPassword);
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

				json = userFunction.registerUser(params[0], params[1], params[2]);
			} else {
				Log.e(MainActivity.TAG, "No hay conexion");
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
				// showDialog(R.string.network_error, R.string.network_error_msg);
			}
			// TODO: register the new account here.
			return json;
		}

		@Override
		protected void onPostExecute(final JSONObject json) {
			mAuthTask = null;
			showProgress(false);

			if (json != null) {
				int success, i, total;
				UserFunctions userFunction = new UserFunctions();

				try {
					success = json.getInt(KEY_RETURN);
					if (success == 0) {
						// Registro correcto
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
						// Launch Main Screen
						Intent main = new Intent(getApplicationContext(), MainActivity.class);
						main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(main);
						finish();
					} else if (success == 1) {
						// Error en registro
						Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();

					} else if (success == 2) {
						// Email ya registrado
						Toast.makeText(getApplicationContext(), "Usuario ya registrado", Toast.LENGTH_LONG).show();
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
}
