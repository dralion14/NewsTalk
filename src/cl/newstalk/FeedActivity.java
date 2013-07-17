package cl.newstalk;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cl.newstalk.library.DatabaseHandler;
import cl.newstalk.library.ListViewAdapter;

public class FeedActivity extends Activity {

	private ListView listFeeds;
	private Context _context;
	private DatabaseHandler db;
	private ListViewAdapter cursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);

		_context = this.getApplicationContext();

		listFeeds = (ListView) findViewById(R.id.listFeeds);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			ActionBar ab = getActionBar();
			ab.setDisplayHomeAsUpEnabled(true);
		}

		getFeeds();

		registerForContextMenu(listFeeds);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		;
	}

	private void getFeeds() {
		try {
			db = new DatabaseHandler(this);

			Cursor cursor = db.getItemsListView(DatabaseHandler.TABLE_FEED);

			String[] from = new String[] { DatabaseHandler.KEY_NAME, DatabaseHandler.KEY_SOURCE_ID };

			int[] to = new int[] { R.id.feed_name, R.id.feed_source_name };

			cursorAdapter = new ListViewAdapter(this, R.layout.row_list_feeds, cursor, from, to);

			listFeeds.setAdapter(cursorAdapter);

			// Click event for single list row
			listFeeds.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> l, View v, int position, long id) {
					// Do something when a list item is clicked
					SQLiteCursor cursor = (SQLiteCursor) l.getItemAtPosition(position);

					SharedPreferences settings = PreferenceManager
							.getDefaultSharedPreferences(getApplicationContext());
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("actual_feed", cursor.getString(cursor.getColumnIndex("url")));
					// Commit the edits!
					editor.commit();
					// Reemplazar lo siguiente por un SlidingView:
					Intent intent = new Intent(_context, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}
			});

		} catch (Exception e) {
			Log.d(MainActivity.TAG, "El mensaje de error es: " + e.getMessage());
		} finally {
			db.close();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		getFeeds();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feed, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				return true;
			default:
				return false;
		}
	}

}
