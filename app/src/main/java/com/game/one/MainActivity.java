package com.game.one;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity
{
    private ImageButton exitButton;
    private ImageButton helpButton;
    private ImageButton settingsButton;
    private ImageButton playButton;
    private ImageButton about;
    private Context sharedContext = null;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setDisplaySpecs();
        setContentView(R.layout.activity_main);
        setUp();
        if(Util.musicPlayer == null)
        {
            Config.readVolume(this);
            Util.initMusicPlayer(this);
            Util.musicPlayer.start();
        }
        prefs = getApplicationContext().getSharedPreferences("com.came.one",
                Context.MODE_PRIVATE);
    }

    private void setDisplaySpecs()
    {
        Util.DENSITY = getResources().getDisplayMetrics().density;
        Util.DISPLAX_SIZE = getResources().getDisplayMetrics().heightPixels
                / getResources().getDisplayMetrics().densityDpi;
        Util.PIXEL_HEIGHT = getResources().getDisplayMetrics().heightPixels;
        Util.PIXEL_WIDTH = getResources().getDisplayMetrics().widthPixels;
        Util.ORIENTATION = getWindow().getWindowManager().getDefaultDisplay()
                .getRotation();
    }

    private void setUp()
    {
        exitButton = (ImageButton) findViewById(R.id.exitButton);
        exitButton.setImageBitmap(Sprite.createBitmap(getResources()
                .getDrawable(R.drawable.close)));
        exitButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (Game.theGame != null)
                    Game.theGame.getGameView().gameOver();

                // CHECK IF DATA IS AVAILABLE BEFORE LAUNCHING UTILITY APP.
                if (isPackageInstalled() && prefs.getBoolean("HAS_DATA", false) == true)
                {
                    edit = prefs.edit();
                    edit.putBoolean("HAS_DATA", false);
                    edit.commit();

                    Intent intent = new Intent();
                    intent.setClassName("com.gradebookdynamics.utility", "com.gradebookdynamics.utility.AddGame");

                    startService(intent);
                }


                finish();
                System.exit(0);
            }
        });

        helpButton = (ImageButton) findViewById(R.id.helpButton);
        helpButton.setImageBitmap(Sprite.createBitmap(getResources()
                .getDrawable(R.drawable.help_button_hdpi)));
        helpButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (Util.musicPlayer != null)
                {
                    Util.musicPlayer.pause();
                }
                startActivity(new Intent(getApplicationContext(), Help.class));
            }
        });

        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setImageBitmap(Sprite.createBitmap(getResources()
                .getDrawable(R.drawable.settings_btn)));
        settingsButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), Config.class));
            }
        });

        playButton = (ImageButton) findViewById(R.id.playButton);
        playButton.setImageBitmap(Sprite.createBitmap(getResources()
                .getDrawable(R.drawable.play_btn_green)));
        playButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), Game.class);
                startActivity(i);
            }
        });

        about = (ImageButton) findViewById(R.id.about);
        about.setImageBitmap(Sprite.createBitmap(getResources().getDrawable(
                R.drawable.about)));
        about.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), About.class));
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        if (Util.musicPlayer != null)
        {
            Util.musicPlayer.stop();
            Util.musicPlayer.release();
            Util.musicPlayer = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (Util.musicPlayer != null)
        {
            Util.musicPlayer.start();
        }
    }

    @Override
    protected void onPause()
    {
        if (Util.musicPlayer != null)
        {
            Util.musicPlayer.pause();
        }
        super.onPause();
    }

    private boolean isPackageInstalled()
    {
        PackageManager pm = getApplicationContext().getPackageManager();

        try
        {
            pm.getPackageInfo("com.gradebookdynamics.utility", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }
}
