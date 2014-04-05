package com.trafficsign.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import static com.trafficsign.ultils.Properties.serviceIp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trafficsign.ultils.Properties;
import com.trafficsign.activity.R;
import com.trafficsign.json.CategoryJSON;
import com.trafficsign.json.FavoriteJSON;
import com.trafficsign.json.TrafficInfoShortJSON;
import com.trafficsign.ultils.ConvertUtil;
import com.trafficsign.ultils.DBUtil;
import com.trafficsign.ultils.HttpAsyncUtil;
import com.trafficsign.ultils.HttpDatabaseUtil;
import com.trafficsign.ultils.HttpSyncUtil;
import com.trafficsign.ultils.MyInterface.IAsyncHttpImageListener;
import com.trafficsign.ultils.MyInterface.IAsyncHttpListener;
import com.trafficsign.ultils.NetUtil;
//import com.example.ultils.MyInterface.IAsyncFetchListener;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

public class MainActivity extends Activity {
	String user = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// init resource
		InputStream dbIS = getResources().openRawResource(R.raw.traffic_sign);
		InputStream settingIS = getResources().openRawResource(R.raw.setting);
		DBUtil.initResource(dbIS, settingIS, MainActivity.this);
		// get user
		final SharedPreferences pref = getSharedPreferences(
				Properties.SHARE_PREFERENCE_LOGIN, MODE_PRIVATE);
		user = pref
				.getString(Properties.SHARE_PREFERENCE__KEY_USER, "notLogin");
		if ("notLogin".equals(user) == false) {
			// sync favorite and history
			SharedPreferences sharedPreferences = getSharedPreferences(
					Properties.SHARE_PREFERENCE_LOGIN, MODE_PRIVATE);
			boolean isSync = sharedPreferences.getBoolean("isSync", true);
			if (isSync == false) {
				HttpSyncUtil httpSyncUtil = new HttpSyncUtil(MainActivity.this);
				httpSyncUtil.setUser(this.user);
				httpSyncUtil.setHttpListener(new IAsyncHttpListener() {

					@Override
					public void onComplete(String respond) {
						// TODO Auto-generated method stub
						Editor editor = pref.edit();
						editor.remove("isSync");
						editor.putBoolean("isSync", true);
						editor.commit();
					}
				});
				httpSyncUtil.execute();
			}
		}

		// Get and set event onclick for manual search button
		ImageButton imgBtnManualSearch = (ImageButton) findViewById(R.id.imageButtonManualSearch);
		imgBtnManualSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<CategoryJSON> listCategory = new ArrayList<CategoryJSON>();
				byte[] dataBytes;
				try {
					listCategory = DBUtil.getAllCategory();
					Intent nextScreen = new Intent(getApplicationContext(),
							CategoryActivity.class);
					dataBytes = ConvertUtil.object2Bytes(listCategory);
					nextScreen.putExtra("catList", dataBytes);
					startActivity(nextScreen);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		/* Set event onclick for auto search button */
		final ImageButton imgBtnAutoSearch = (ImageButton) findViewById(R.id.imageButtonAutoSearch);
		imgBtnAutoSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent nextScreen = new Intent(getApplicationContext(),
						CameraActivity.class);
				startActivity(nextScreen);

			}
		});
		/* Set event onclick for favorite button */
		ImageButton imgBtnFavorite = (ImageButton) findViewById(R.id.imageButtonFavorite);
		imgBtnFavorite.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ("notLogin".equals(user) == false) {
					Intent nextScreen = new Intent(getApplicationContext(),
							FavouriteActivity.class);
					startActivity(nextScreen);
				}

			}
		});
		/* Set event onclick for histoy button */
		ImageButton imgBtnHistory = (ImageButton) findViewById(R.id.imageButtonHistory);
		imgBtnHistory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ("notLogin".equals(user) == false) {
					Intent nextScreen = new Intent(getApplicationContext(),
							HistoryActivity.class);
					startActivity(nextScreen);
				}

			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences pref = getSharedPreferences(
				Properties.SHARE_PREFERENCE_LOGIN, MODE_PRIVATE);
		user = pref
				.getString(Properties.SHARE_PREFERENCE__KEY_USER, "notLogin");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		// if user has been logged in

		if ("notLogin".equals(user) == false) {
			getMenuInflater().inflate(R.menu.main_login, menu);
		} else {
			getMenuInflater().inflate(R.menu.main_not_login, menu);
		}

		return true;
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.action_checklogin) {
			// user has been logged in

			Intent nextScreen = new Intent(getApplicationContext(),
					LoginActivity.class);
			finish();
			startActivity(nextScreen);

		} else if (item.getItemId() == R.id.action_checkLogout) {
			SharedPreferences pref = getSharedPreferences(
					Properties.SHARE_PREFERENCE_LOGIN, MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.remove(Properties.SHARE_PREFERENCE__KEY_USER);
			editor.remove(Properties.SHARE_PREFERENCE__KEY_SYNC);
			editor.putBoolean(Properties.SHARE_PREFERENCE__KEY_SYNC, true);
			editor.commit();
			this.user = "notLogin";
		} else if (item.getItemId() == R.id.action_settings) {
			Intent nextScreen = new Intent(getApplicationContext(),
					SettingActivity.class);
			startActivity(nextScreen);
		} else if (item.getItemId() == R.id.action_register) {
			Intent nextScreen = new Intent(getApplicationContext(),
					RegisterActivity.class);
			startActivity(nextScreen);
		}
		return true;

	}

}
