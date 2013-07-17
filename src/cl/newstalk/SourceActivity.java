package cl.newstalk;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import cl.newstalk.library.DatabaseHandler;
import cl.newstalk.library.ListViewAdapter;

public class SourceActivity extends Activity {

	private ListView listSources;
	private Context _context;
	private DatabaseHandler db;
	private ListViewAdapter cursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_source);

		_context = this.getApplicationContext();

		listSources = (ListView) findViewById(R.id.listSources);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			ActionBar ab = getActionBar();
			ab.setDisplayHomeAsUpEnabled(true);
		}

		getSources();

		registerForContextMenu(listSources);
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

	private void getSources() {
		try {
			db = new DatabaseHandler(this);

			Cursor cursor = db.getItemsListView(DatabaseHandler.TABLE_SOURCE);

			String[] from = new String[] { DatabaseHandler.KEY_NAME };

			int[] to = new int[] { R.id.source_name };

			cursorAdapter = new ListViewAdapter(this,
					R.layout.row_list_sources, cursor, from, to);

			listSources.setAdapter(cursorAdapter);

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
		getSources();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.source, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			default:
				return false;
		}
	}

}
