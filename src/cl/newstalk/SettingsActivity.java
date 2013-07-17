package cl.newstalk;

import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;

public class SettingsActivity extends PreferenceActivity {
	final static String ACTION_PREFS_ONE = "cl.newstalk.PREFS_ONE";
	final static String ACTION_PREFS_TWO = "cl.newstalk.PREFS_TWO";

	public final static String KEY_URL_SERVER = "url_server";
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    String action = getIntent().getAction();
	    if (action != null && action.equals(ACTION_PREFS_ONE)) {
	        addPreferencesFromResource(R.xml.preference_fragmented_one_legacy);
	    }
	    else if (action != null && action.equals(ACTION_PREFS_TWO)) {
	        addPreferencesFromResource(R.xml.preference_fragmented_two_legacy);
	    }
	    else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
	        // Load the legacy preferences headers
			addPreferencesFromResource(R.xml.preference_headers_legacy);
	    }
	}

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    /**
     * This fragment shows the preferences for the first header.
     */
    public static class SettingsFragmentOne extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
            //PreferenceManager.setDefaultValues(getActivity(),R.xml.advanced_preferences, false);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference_fragmented_one);
        }
    }

    /*
     * This fragment contains a second-level set of preference that you
     * can get to by tapping an item in the first preferences fragment.
     *
    public static class Prefs1FragmentInner extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Can retrieve arguments from preference XML.
            Log.i("NewsTalk", "Arguments: " + getArguments());

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragmented_preferences_inner);
        }
    }

    /**
     * This fragment shows the preferences for the second header.
     */
    public static class SettingsFragmentTwo extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Can retrieve arguments from headers XML.
            Log.i("NewsTalk", "Arguments: " + getArguments());

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference_fragmented_two);
        }
    }
    
}
