package cl.newstalk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.ispeech.SpeechSynthesis;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cl.newstalk.library.NewsAdapter;
import cl.newstalk.library.UserFunctions;
import cl.newstalk.library.XMLParser;

public class MainActivity extends Activity implements
		TextToSpeech.OnInitListener {

	public static final String TAG = "NewsTalk";
	UserFunctions userFunctions;

	private static final String KEY_RETURN = "return";
	// Login Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_URL = "url";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_UID = "uid";
	private static final String KEY_CREATED_AT = "created_at";

	private SpeechSynthesis synthesis;
	Context _context;
	private TextToSpeech tts;
	private String speaking;

	private MainTask mTask = null;

	// XML node keys
	public static final String KEY_SONG = "item"; // parent node
	public static final String KEY_TITLE = "title";
	public static final String KEY_ARTIST = "description";
	public static final String KEY_DURATION = "pubDate";
	public static final String KEY_THUMB_URL = "g:image_link";

	ListView list;
	NewsAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_context = this.getApplicationContext();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			ActionBar ab = getActionBar();
			ab.setDisplayHomeAsUpEnabled(true);
		}

		/**
		 * Main Screen for the application
		 * */
		// Check login status in database
		userFunctions = new UserFunctions();

		if (userFunctions.isUserLoggedIn(getApplicationContext())) {
			setContentView(R.layout.activity_main);

			mTask = new MainTask(_context, this);
			mTask.execute(userFunctions.getUserLoggedIn(
					getApplicationContext(), KEY_UID));

			// IVONA
			tts = new TextToSpeech(this, this);
			// iSpeech
			// prepareTTSEngine();
			// synthesis.setStreamType(AudioManager.STREAM_MUSIC);

			// btnSpeak = (Button) findViewById(R.id.speak);

			// IVONA
			/*
			 * btnSpeak.setOnClickListener(new View.OnClickListener() {
			 * 
			 * @Override public void onClick(View arg0) { speakOut(); } });
			 */
			// iSpeech
			// btnSpeak.setOnClickListener(new OnSpeakListener());

		} else {
			// user is not logged in show login screen
			Intent login = new Intent(getApplicationContext(),
					LoginActivity.class);
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(login);
			// Closing main screen
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_logout:
				userFunctions.logoutUser(getApplicationContext());
				Intent login = new Intent(getApplicationContext(),
						LoginActivity.class);
				login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(login);
				// Closing main screen
				finish();

				return true;
			case R.id.action_settings:
				Intent preference = new Intent(getApplicationContext(),
						SettingsActivity.class);
				// preference .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(preference);
				// Closing main screen
				// finish();

				return true;
			case R.id.action_feeds:
				Intent feeds = new Intent(getApplicationContext(),
						FeedActivity.class);
				// preference .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(feeds);
				// Closing main screen
				// finish();

				return true;
			case R.id.action_sources:
				Intent sources = new Intent(getApplicationContext(),
						SourceActivity.class);
				// preference .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(sources);
				// Closing main screen
				// finish();

				return true;
			case android.R.id.home:
				Toast.makeText(getApplicationContext(), "HOME", Toast.LENGTH_SHORT)
						.show();
				return true;
			default:
				return false;
		}
	}

	// iSpeech
	/*
	 * private void prepareTTSEngine() { try { synthesis =
	 * SpeechSynthesis.getInstance(this);
	 * synthesis.setVoiceType("usspanishfemale");
	 * synthesis.setSpeechSynthesisEvent(new SpeechSynthesisEvent() {
	 * 
	 * public void onPlaySuccessful() { Log.i(TAG, "onPlaySuccessful"); }
	 * 
	 * public void onPlayStopped() { Log.i(TAG, "onPlayStopped"); }
	 * 
	 * public void onPlayFailed(Exception e) { Log.e(TAG, "onPlayFailed");
	 * 
	 * AlertDialog.Builder builder = new AlertDialog.Builder(
	 * MainActivity.this); builder.setMessage("Error[TTSActivity]: " +
	 * e.toString()) .setCancelable(false) .setPositiveButton("OK", new
	 * DialogInterface.OnClickListener() { public void onClick( DialogInterface
	 * dialog, int id) { } }); AlertDialog alert = builder.create();
	 * alert.show(); }
	 * 
	 * public void onPlayStart() { Log.i(TAG, "onPlayStart"); }
	 * 
	 * @Override public void onPlayCanceled() { Log.i(TAG, "onPlayCanceled"); }
	 * 
	 * });
	 * 
	 * } catch (InvalidApiKeyException e) { Log.e(TAG, "Invalid API key\n" +
	 * e.getStackTrace()); Toast.makeText(_context, "ERROR: Invalid API key",
	 * Toast.LENGTH_LONG).show(); }
	 * 
	 * }
	 * 
	 * private class OnSpeakListener implements OnClickListener { public void
	 * onClick(View v) { try { synthesis.speak(mNameView.getText().toString());
	 * } catch (BusyException e) { Log.e(TAG, "SDK is busy");
	 * e.printStackTrace(); Toast.makeText(_context, "ERROR: SDK is busy",
	 * Toast.LENGTH_LONG).show(); } catch (NoNetworkException e) { Log.e(TAG,
	 * "Network is not available\n" + e.getStackTrace());
	 * Toast.makeText(_context, "ERROR: Network is not available",
	 * Toast.LENGTH_LONG).show(); } } }
	 * 
	 * public class OnStopListener implements OnClickListener {
	 * 
	 * public void onClick(View v) { if (synthesis != null) { synthesis.stop();
	 * } } }
	 * 
	 * @Override protected void onPause() { synthesis.stop(); // Optional to
	 * stop the playback when the activity is // paused super.onPause(); }
	 */
	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public void onInit(int status) {
		speaking = "";
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(new Locale("spa", "usa", "penelope"));

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e(TAG, "This Language is not supported");
			} else {
				// btnSpeak.setEnabled(true);
				// speakOut("");
			}
		} else {
			Log.e(TAG, "Initilization Failed!");
		}
	}

	private void speakOut(String title, String description) {
		if (tts != null) {
			tts.stop();
		}
		if (!speaking.equalsIgnoreCase(title)) {
			speaking = title;
			tts.speak(title, TextToSpeech.QUEUE_FLUSH, null);

			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());

			boolean url = sharedPref.getBoolean("speak_description", false);
			if (url) {
				while (tts.isSpeaking())
					;
				tts.speak(description, TextToSpeech.QUEUE_FLUSH, null);
			}
		} else {
			speaking = "";
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	private class MainTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		Context context;
		Activity main;

		public MainTask(Context context, Activity main) {
			this.context = context;
			this.main = main;
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
			// TODO: attempt authentication against a network service.
			ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

			XMLParser parser = new XMLParser();
			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());

			String url = sharedPref.getString("actual_feed", "http://192.168.0.106/newstalk/rss.xml");
			String xml = parser.getXmlFromUrl(url); // getting XML from URL
			Document doc = parser.getDomElement(xml); // getting DOM element

			NodeList nl = doc.getElementsByTagName(KEY_SONG);
			// looping through all song nodes &lt;song&gt;
			for (int i = 0; i < nl.getLength(); i++) {
				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();
				Element e = (Element) nl.item(i);
				// adding each child node to HashMap key =&gt; value
				// map.put(KEY_ID, parser.getValue(e, KEY_ID));
				map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
				String desc = parser.getValue(e, KEY_ARTIST);

				String img = null;
				if (desc.indexOf("/>") > 0) {
					img = desc.substring(0, desc.indexOf("/>"));
					img = img.replaceFirst("(.*)src=\"", "");
					img = img.replaceFirst("\"(.*)", "");
				}

				String artist = Html.fromHtml(desc).toString();
				if (img != null)
					artist = Html.fromHtml(desc).toString().substring(1).trim();

				// artist = UserFunctions.replace(artist);

				map.put(KEY_ARTIST, artist);
				map.put(KEY_DURATION, parser.getValue(e, KEY_DURATION));
				if (img != null)
					map.put(KEY_THUMB_URL, img);

				// adding HashList to ArrayList
				songsList.add(map);
			}

			return songsList;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(final ArrayList<HashMap<String, String>> songsList) {
			mTask = null;

			list = (ListView) findViewById(R.id.listNews);

			// Getting adapter by passing xml data ArrayList
			adapter = new NewsAdapter(this.main, songsList);
			list.setAdapter(adapter);

			// Click event for single list row
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					HashMap<String, String> aa = (HashMap<String, String>) list.getAdapter().getItem(position);
					Log.d(TAG, aa.get("description"));

					speakOut(aa.get("title"), aa.get("description"));
				}
			});
		}

		@Override
		protected void onCancelled() {
			mTask = null;
		}
	}

}
