package com.game.one;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Config extends Activity
{
	private ImageButton backButton;
	private ImageButton muteButton;
	private ImageButton viewDataButton;
	private SeekBar mVolume;
	private SeekBar sVolume;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config);

		((TextView) findViewById(R.id.tvMusic)).setTextSize(Util.getTextSize());
		((TextView) findViewById(R.id.tvSound)).setTextSize(Util.getTextSize());

		backButton = (ImageButton) findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				finish();
			}
		});

		muteButton = (ImageButton) findViewById(R.id.muteButton);
		muteButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Util.musicVolume = 0.0f;
				Util.soundVolume = 0.0f;
				Util.updateMusicVolume();
				mVolume.setProgress((int) (0));
				sVolume.setProgress((int) (0));
			}
		});

		mVolume = (SeekBar) findViewById(R.id.sbMusicVolume);
		mVolume.setProgress((int) (Util.musicVolume * 100));
		mVolume.setMinimumHeight(Util.getTextSize());
		mVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			public void onStopTrackingTouch(SeekBar seekBar)
			{
			}

			public void onStartTrackingTouch(SeekBar seekBar)
			{
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				Util.musicVolume = (float) (progress / 100.0);
				Util.updateMusicVolume();
			}
		});

		sVolume = (SeekBar) findViewById(R.id.sbSoundVolume);
		sVolume.setProgress((int) (Util.soundVolume * 100));
		sVolume.setMinimumHeight(Util.getTextSize());
		sVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			public void onStopTrackingTouch(SeekBar seekBar)
			{
			}

			public void onStartTrackingTouch(SeekBar seekBar)
			{
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				Util.soundVolume = (float) (progress / 100.0);
			}
		});
		
		viewDataButton = (ImageButton) findViewById(R.id.viewDataButton);
		viewDataButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(getApplicationContext(), ViewDataActivity.class);
			
			    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			    startActivity(intent);
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

		saveVolume(this);
	}

	// shared preferences.
	private static final String prefsName = "CONFIG";
	private static final String musicVolume = "musicVolume";
	private static final String soundVolume = "soundVolume";

	public static void saveVolume(Activity activity)
	{
		SharedPreferences saves = activity.getSharedPreferences(prefsName, 0);
		SharedPreferences.Editor editor = saves.edit();
		editor.putFloat(musicVolume, Util.musicVolume);
		editor.putFloat(soundVolume, Util.soundVolume);
		editor.commit();
	}

	public static void readVolume(Activity activity)
	{
		Util.musicVolume = activity.getSharedPreferences(prefsName, 0)
				.getFloat(musicVolume, 0.5f);
		Util.soundVolume = activity.getSharedPreferences(prefsName, 0)
				.getFloat(soundVolume, 0.5f);
	}
}
