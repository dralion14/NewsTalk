/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * 
 * Modified by: Gabriel Salazar
 * */
package cl.newstalk.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions {

	private JSONParser jsonParser;

	private static String baseURL = "http://www.newstalk.p.ht/android.php";

	private static String login_tag = "login";
	private static String register_tag = "register";
	private static String data_tag = "data";

	// constructor
	public UserFunctions() {
		jsonParser = new JSONParser();
	}

	/**
	 * function make Login Request
	 * 
	 * @param email
	 * @param password
	 * */
	public JSONObject loginUser(String email, String password) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));

		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(baseURL, params);

		return json;
	}

	/**
	 * function make Register Request
	 * 
	 * @param name
	 * @param email
	 * @param password
	 * */
	public JSONObject registerUser(String name, String email, String password) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));

		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(baseURL, params);

		return json;
	}

	/**
	 * Function get Login user
	 * */
	public HashMap<String, String> getUserLoggedIn(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);

		return db.getUserDetails();
	}

	/**
	 * Function get Login user
	 * */
	public String getUserLoggedIn(Context context, String key) {
		DatabaseHandler db = new DatabaseHandler(context);

		return db.getUserDetails(key);
	}

	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();

		if (count > 0) {
			// user logged in
			return true;
		}
		return false;
	}

	/**
	 * Function to logout user
	 * Reset Database
	 * */
	public boolean logoutUser(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}

	/**
	 * function make Group Request
	 * 
	 * @param email
	 * @param password
	 * */
	public JSONObject loadData(String uid, String url) {
		String serverURL = "http://" + url + baseURL;
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", data_tag));
		params.add(new BasicNameValuePair("uid", uid));

		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);

		return json;
	}
	
	public static String replace(String str) {
		String resp = str;
		//Log.i(MainActivity.TAG,resp);
		String[] olds = {"&#xF1;","&#34;","&#xF3;","&#xE9;","&#xE1;","&#xED;","&#xFA;"};
		String[] news = {"ñ","\"","ó","é","á","í","ú"};
		int pos = 0;
		
		while (pos < olds.length && resp.indexOf("&#") >= 0) {
			resp = resp.replaceAll(olds[pos], news[pos]);
			pos++;	
		}
		//Log.w(MainActivity.TAG,"Busque "+pos+" veces");
		return resp;
	}
}
