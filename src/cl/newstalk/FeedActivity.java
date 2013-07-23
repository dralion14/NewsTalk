package cl.newstalk;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import cl.newstalk.library.DatabaseHandler;
import cl.newstalk.library.FragmentAlertDialog;

public class FeedActivity extends Activity {

	// This is the Adapter being used to display the list's data
	private DatabaseHandler db;

	private ExpandableListView expListFeeds;

	private Cursor getFeeds;
	private Cursor getAllSources;
	MyExpandableListAdapter cursorTreeAdapter;

	LayoutInflater mInflator;

	String url = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);

		expListFeeds = (ExpandableListView) findViewById(R.id.expandableListFeeds);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			ActionBar ab = getActionBar();
			ab.setDisplayHomeAsUpEnabled(true);
		}
		mInflator = LayoutInflater.from(getApplicationContext());

		getFeeds();

		registerForContextMenu(expListFeeds);
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

	void showDialog(int title, int msg) {
		DialogFragment newFragment = FragmentAlertDialog.newInstance(R.string.title_activity_feed, title, msg);
		newFragment.show(getFragmentManager(), "dialog");
	}

	public void doPositiveClick() {
		if (url != null) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);
		}
	}

	public void doNegativeClick() {
		// Do stuff here.
		Log.i(MainActivity.TAG, "Negative click!");
	}

	private void getFeeds() {
		try {
			db = new DatabaseHandler(this);

			getAllSources = db.getItemsListView(DatabaseHandler.TABLE_SOURCE);

			cursorTreeAdapter = new MyExpandableListAdapter(
					getApplicationContext(),
					getAllSources,
					R.layout.row_list_sources,
					R.layout.row_list_sources,
					new String[] { "name", "_id" },
					new int[] { R.id.source_name },
					R.layout.row_list_feeds,
					R.layout.row_list_feeds,
					new String[] { "name", "active", "source_id", "_id" },
					new int[] { R.id.feed_name });

			expListFeeds.setAdapter(cursorTreeAdapter);

			expListFeeds.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

				@Override
				public boolean onChildClick(ExpandableListView parent,
						View v, int groupPosition, int childPosition,
						long id) {
					if (groupPosition == 0 && childPosition == 0) {

					}

					SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("actual_feed", cursorTreeAdapter.getFromChild(groupPosition, childPosition, "url"));
					// Commit the edits!
					editor.commit();
					// Reemplazar lo siguiente por un SlidingView:
					Intent main = new Intent(getApplicationContext(), MainActivity.class);
					main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(main);
					finish();

					return false;
				}
			});

			expListFeeds.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
					// TODO Auto-generated method stub
					Cursor item = (Cursor) expListFeeds.getItemAtPosition(index);
					String source = item.getString(item.getColumnIndex("name"));
					url = item.getString(item.getColumnIndex("url"));

					if (!url.startsWith("http://") && !url.startsWith("https://"))
						url = "http://" + url;
					String msg = "Visitar página de \"" + source + "\"";

					showDialog(R.string.url_title, R.string.url_msg);

					return true;
				}
			});

			// Los dos metodos siguientes son para mantenerlos siempre expandidos
			expListFeeds.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

				@Override
				public void onGroupCollapse(int groupPosition) {
					// TODO Auto-generated method stub
					expListFeeds.expandGroup(groupPosition);
				}
			});

			for (int i = 0; i < getAllSources.getCount(); i++) {
				expListFeeds.expandGroup(i);
			}

		} catch (Exception e) {
			Log.d(MainActivity.TAG, "El mensaje de error es: " + e.getMessage());
		} finally {
			db.close();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
				Intent main = new Intent(getApplicationContext(), MainActivity.class);
				main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(main);
				finish();
				return true;
			default:
				return false;
		}
	}

	public class MyExpandableListAdapter extends SimpleCursorTreeAdapter {

		public MyExpandableListAdapter(Context context, Cursor cursor, int collapsedGroupLayout, int expandedGroupLayout,
				String[] groupFrom, int[] groupTo, int childLayout, int lastChildLayout, String[] childFrom, int[] childTo) {
			super(context, cursor, collapsedGroupLayout, expandedGroupLayout, groupFrom, groupTo, childLayout, lastChildLayout,
					childFrom,
					childTo);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			getFeeds = db.getItemsListView(DatabaseHandler.TABLE_FEED, groupCursor.getString(groupCursor.getColumnIndex("_id")));

			return getFeeds;
		}

		public String getFromChild(int groupPosition, int childPosition, String column) {
			int columnIndex = this.getChild(groupPosition, childPosition).getColumnIndex(column);

			return this.getChild(groupPosition, childPosition).getString(columnIndex);
		}

		@Override
		protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
			// TODO Auto-generated method stub
			TextView tvA = (TextView) view.findViewById(R.id.feed_name);
			tvA.setText(cursor.getString(cursor.getColumnIndex("name")));
			CheckBox cb = (CheckBox) view.findViewById(R.id.feed_active);
			if (cursor.getString(cursor.getColumnIndex("active")).equals("0")) {
				cb.setChecked(false);
			} else {
				cb.setChecked(true);
			}
		}
		/*
		 * Sirve para CursorTreeAdapter
		 * 
		 * @Override
		 * protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
		 * // TODO Auto-generated method stub
		 * TextView tvA = (TextView) view.findViewById(R.id.source_name);
		 * tvA.setText(cursor.getString(cursor.getColumnIndex("name")));
		 * }
		 * 
		 * @Override
		 * protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
		 * // TODO Auto-generated method stub
		 * View mView = mInflator.inflate(R.layout.row_list_feeds, null);
		 * 
		 * final TextView tvChild = (TextView) mView.findViewById(R.id.feed_name);
		 * 
		 * tvChild.setText(cursor.getString(cursor.getColumnIndex("name")));
		 * 
		 * CheckBox chb = (CheckBox) mView.findViewById(R.id.feed_active);
		 * if (cursor.getString(cursor.getColumnIndex("active")).equals("0")) {
		 * chb.setChecked(false);
		 * } else {
		 * chb.setChecked(true);
		 * }
		 * 
		 * return mView;
		 * }
		 * 
		 * @Override
		 * protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
		 * // TODO Auto-generated method stub
		 * View mView = mInflator.inflate(
		 * R.layout.row_list_sources, null);
		 * TextView tvGrp = (TextView) mView.findViewById(R.id.source_name);
		 * tvGrp.setText(cursor.getString(cursor.getColumnIndex("name")));
		 * 
		 * return mView;
		 * }
		 */
	}

}
