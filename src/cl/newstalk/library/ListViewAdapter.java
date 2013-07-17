package cl.newstalk.library;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class ListViewAdapter extends SimpleCursorAdapter{
	private Cursor cursor;

	public ListViewAdapter(Context contexto, int layout, Cursor cursor, String[] from,
			int[] to) {
		super(contexto, layout, cursor, from, to);
		this.cursor = cursor;
	}

}
