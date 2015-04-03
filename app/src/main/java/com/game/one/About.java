package com.game.one;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
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

		// text must be scaled for different screens
		((TextView) findViewById(R.id.version)).setTextSize(Util.getTextSize());
		((TextView) findViewById(R.id.developer)).setTextSize(Util
				.getTextSize());
        ((TextView) findViewById(R.id.eduContent)).setTextSize(Util
                .getTextSize());
        ((TextView) findViewById(R.id.eduContentName)).setTextSize(Util
                .getTextSize());
		((TextView) findViewById(R.id.graphics))
				.setTextSize(Util.getTextSize());
		((TextView) findViewById(R.id.audio)).setTextSize(Util.getTextSize());
		((TextView) findViewById(R.id.developerDescription)).setTextSize(Util
				.getTextSize());
		((TextView) findViewById(R.id.graphicsName)).setTextSize(Util
				.getTextSize());
		((TextView) findViewById(R.id.audioName)).setTextSize(Util.getTextSize());
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
