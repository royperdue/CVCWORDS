package com.game.one;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;

public class Util
{
	// ---------------------------------------------
	// ----------------constants--------------------
	// ---------------------------------------------

	public static final String VERSION = "1.00";
    public static final String RES_PREFIX = "android.resource://com.game.one/";
	// Device settings.
	public static float DENSITY = 1;
	public static float DISPLAX_SIZE = 5;
	public static int PIXEL_WIDTH = 800;
	public static int PIXEL_HEIGHT = 480;
	public static int ORIENTATION = 1;
	// System settings.
	public static short DEFAULT_FONT_SIZE = 20; // Motorola Milestone 2
	public static final short UPDATE_INTERVAL = 30;
	public static final byte TO_DEGREE = 90;
	public static final float DISTANCE_COLLISION_FACTOR = 0.4f;

	// Game config.
	public static final short MOVEMENTS_FOR_DISPLAY_HEIGHT_FOR_10_DEGREE = 10;
	public static final int AMOUNT_OF_FLYS = 5;

	// ---------------------------------------------
	// ------------------variables------------------
	// ---------------------------------------------
	public static short lvl = 1;
	public static short DefaultXAngle = 0;
	public static short DefaultYAngle = 0;
	public static float musicVolume = 0.5f;
	public static float soundVolume = 0.5f;
	public static float FLY_COLLISION_FACTOR = DISTANCE_COLLISION_FACTOR;
	public static float FLY_SPEED_FACTOR = 1f;
	public static MediaPlayer musicPlayer;

	public static void initMusicPlayer(Context context)
	{
		musicPlayer = MediaPlayer.create(context, R.raw.theme);
		musicPlayer.setLooping(true);
		musicPlayer.setVolume(musicVolume, musicVolume);
	}

	public static void updateMusicVolume()
	{
		if(musicPlayer != null)
		{
			musicPlayer.setVolume(musicVolume, musicVolume);
		}
	}

	public static boolean isTablet(Activity res)
	{
		boolean xlarge = ((res.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((res.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return(xlarge || large);
	}

	public static float getScaleFactor()
	{
		return PIXEL_HEIGHT / 128f / 3.75f;
	}

	public static short getTextSize()
	{
		return (short) (17f / 320f * PIXEL_HEIGHT - 10);
	}

	public static float getSpeedFactor()
	{
		return 0.1f * PIXEL_HEIGHT / MOVEMENTS_FOR_DISPLAY_HEIGHT_FOR_10_DEGREE
				/ 10;
	}

}
