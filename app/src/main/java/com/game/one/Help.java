package com.game.one;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Help extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		((TextView) findViewById(R.id.help1)).setTextSize(Util.getTextSize());
		((TextView) findViewById(R.id.help2)).setTextSize(Util.getTextSize());
		((TextView) findViewById(R.id.help3)).setTextSize(Util.getTextSize());
		((TextView) findViewById(R.id.help4)).setTextSize(Util.getTextSize());
		((TextView) findViewById(R.id.help5)).setTextSize(Util.getTextSize());

		((ImageView) findViewById(R.id.helpImg)).setImageBitmap(Sprite
				.createBitmap(getResources().getDrawable(R.drawable.symb1)));

		ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				finish();
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if(Util.musicPlayer != null)
		{
			Util.musicPlayer.start();
		}
	}

	@Override
	protected void onPause()
	{
		if(Util.musicPlayer != null)
		{
			Util.musicPlayer.pause();
		}
		super.onPause();
	}
}
