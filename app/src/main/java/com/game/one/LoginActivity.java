package com.game.one;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity
{
	//public static Activity loginActivity;
	private Button btnLogin;
	private Button createButton;
	private EditText txtUsername, txtPassword, editScreenName, editPassword;
	private TextView statusText, usernameText, passwordText;
	// User Session Manager Class
	private UserSessionManager session;
	private SharedPreferences prefs;
	private Editor edit;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		editScreenName = (EditText) findViewById(R.id.editScreenName);
		editPassword = (EditText) findViewById(R.id.editPassword);

		statusText = (TextView) findViewById(R.id.statusText);
		usernameText = (TextView) findViewById(R.id.usernameText);
		passwordText = (TextView) findViewById(R.id.passwordText);

		btnLogin = (Button) findViewById(R.id.btnLogin);
		createButton = (Button) findViewById(R.id.createButton);

		prefs = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
		
		String loginPassword = prefs.getString("LOGIN_PASSWORD", "NO_CREDENTIALS");
		edit = prefs.edit();
		
		if(loginPassword.equals("NO_CREDENTIALS") == false)
		{
			createButton.setClickable(false);
			editScreenName.setFocusable(false);
			editPassword.setFocusable(false);
		}
		
		if(loginPassword.equals("NO_CREDENTIALS") == true)
		{
			btnLogin.setClickable(false);
			txtUsername.setFocusable(false);
			txtPassword.setFocusable(false);
			
			Toast.makeText(getApplicationContext(),
					"Please create login credentials.",
					Toast.LENGTH_LONG).show();
		}

		createButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// Get username, password from EditText
				String u = editScreenName.getText().toString();
				String p = editPassword.getText().toString();

				if(u != null && p != null)
				{
					edit.putString("LOGIN_USERNAME", u);
					edit.commit();

					edit.putString("LOGIN_PASSWORD",p);
					edit.commit();
					
					login(u, p);
				}
			}
		});

		btnLogin.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				// Get username, password from EditText
				String u = txtUsername.getText().toString();
				String p = txtPassword.getText().toString();

				login(u, p);
			}
		});
	}
	
	private void login(String username, String password)
	{
		String loginPassword = prefs.getString("LOGIN_PASSWORD",null);

		String loginUsername = prefs.getString("LOGIN_USERNAME", null);

		// Validate if username, password is filled
		if(username.trim().length() > 0 && password.trim().length() > 0)
		{
			if(username.equals("admin") && password.equals("admin")
					|| username.equals(loginUsername)
					&& password.equals(loginPassword))
			{
				edit.putBoolean("loggedIn", true);
				edit.commit();
				// Starting ViewDataActivity
				Intent i = new Intent(getApplicationContext(),
						ViewDataActivity.class);
				
				startActivity(i);

				finish();
			}
			else
			{
				// username / password doesn't match
				Toast.makeText(getApplicationContext(),
						"Username/Password is incorrect",
						Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			// user didn't entered username or password
			Toast.makeText(getApplicationContext(),
					"Please enter username and password",
					Toast.LENGTH_SHORT).show();
		}
	
	}
}