package com.game.one;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class About extends Activity
{
	private ImageButton backButton;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

        Typeface kidsFont = Typeface.createFromAsset(getAssets(), "kidsFont.ttf");
		backButton = (ImageButton) findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				finish();
			}
		});

		try
		{
			((TextView) findViewById(R.id.version))
					.setText("Version: "
							+ getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName);
		}
		catch(NameNotFoundException e)
		{
			e.printStackTrace();
		}

        ((TextView) findViewById(R.id.textView2)).setTypeface(kidsFont);
		((TextView) findViewById(R.id.version)).setTextSize(Util.getTextSize());
        ((TextView) findViewById(R.id.version)).setTypeface(kidsFont);
		((TextView) findViewById(R.id.developer)).setTextSize(Util
				.getTextSize());
        ((TextView) findViewById(R.id.developer)).setTypeface(kidsFont);
        ((TextView) findViewById(R.id.eduContent)).setTextSize(Util
                .getTextSize());
        ((TextView) findViewById(R.id.eduContent)).setTypeface(kidsFont);
        ((TextView) findViewById(R.id.eduContentName)).setTextSize(Util
                .getTextSize());
        ((TextView) findViewById(R.id.eduContentName)).setTypeface(kidsFont);
		((TextView) findViewById(R.id.graphics))
				.setTextSize(Util.getTextSize());
        ((TextView) findViewById(R.id.graphics)).setTypeface(kidsFont);
		((TextView) findViewById(R.id.audio)).setTextSize(Util.getTextSize());
        ((TextView) findViewById(R.id.audio)).setTypeface(kidsFont);
		((TextView) findViewById(R.id.developerDescription)).setTextSize(Util
				.getTextSize());
        ((TextView) findViewById(R.id.developerDescription)).setTypeface(kidsFont);
		((TextView) findViewById(R.id.graphicsName)).setTextSize(Util
				.getTextSize());
        ((TextView) findViewById(R.id.graphicsName)).setTypeface(kidsFont);
		((TextView) findViewById(R.id.audioName)).setTextSize(Util.getTextSize());
        ((TextView) findViewById(R.id.audioName)).setTypeface(kidsFont);
        ((TextView) findViewById(R.id.about_description1)).setTextSize(Util.getTextSize());
        ((TextView) findViewById(R.id.about_description1)).setTypeface(kidsFont);
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
