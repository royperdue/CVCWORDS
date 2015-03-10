package com.game.one;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserSessionManager
{
	private Context _context;
	private SharedPreferences prefs;
	private Editor edit;
	
	public UserSessionManager(Context context)
	{
		this._context = context;
		prefs = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
		edit = prefs.edit();
	}

	public boolean checkLogin()
	{
		// Check login status
		if(!this.isUserLoggedIn())
		{
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, LoginActivity.class);

			// Closing all the Activities from stack
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			// Staring Login Activity
			_context.startActivity(i);

			return true;
		}
		return false;
	}
	
	public void logoutUser()
	{
		edit.putBoolean("loggedIn", false);
		edit.commit();
		Intent i = new Intent(_context, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		_context.startActivity(i);
	}

	public boolean isUserLoggedIn()
	{
		return prefs.getBoolean("loggedIn", false);
	}
}
