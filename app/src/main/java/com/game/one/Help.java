package com.game.one;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class Help extends Activity
{
    private MediaPlayer mediaPlayer;
    private Button leftLetterBtn;
    private Button middleLetterBtn;
    private Button rightLetterBtn;
    private Button pauseButton;
    private Dialog inGameMenu;
    private LinearLayout wordLayout;

    TimerExec mediaTimer = new TimerExec(500, -1, new TimerExecTask()
    {
        @Override
        public void onTick()
        {
           /* if (mediaTimer.getElapsedTime() >= (mediaPlayer.getDuration() - 4000))
            {
                onFinish();
            }*/

            if (mediaTimer.getElapsedTime() >= 4000 && mediaTimer.getElapsedTime() < 4400)
            {
                clickLeftButton();
            }

            if (mediaTimer.getElapsedTime() >= 5000 && mediaTimer.getElapsedTime() < 5400)
            {
                resetLeftButton();
            }

            if (mediaTimer.getElapsedTime() >= 6000 && mediaTimer.getElapsedTime() < 6400)
            {
                clickMiddleButton();
            }

            if (mediaTimer.getElapsedTime() >= 7000 && mediaTimer.getElapsedTime() < 7400)
            {
                resetMiddleButton();
            }

            if (mediaTimer.getElapsedTime() >= 8000 && mediaTimer.getElapsedTime() < 8400)
            {
                clickRightButton();
            }

            if (mediaTimer.getElapsedTime() >= 9000 && mediaTimer.getElapsedTime() < 9400)
            {
                resetRightButton();
            }

            if (mediaTimer.getElapsedTime() >= 10000 && mediaTimer.getElapsedTime() < 10400)
            {
                clickOpenDialogButton();
            }

            if (mediaTimer.getElapsedTime() >= 12000 && mediaTimer.getElapsedTime() < 12400)
            {
                clickExitButton();
            }

            if (mediaTimer.getElapsedTime() >= 13500)
            {
                exitHelp();
            }

            if (mediaTimer.getElapsedTime() >= 13500)
            {
                onFinish();
            }
        }

        @Override
        public void onFinish()
        {
            // stops the mediaPlayer.
            mediaPlayer.stop();
            mediaPlayer.reset();
            // releases the mediaPlayer instance.
            mediaPlayer.release();
            mediaPlayer = null;
            mediaTimer.cancel();
        }
    });

    @Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        //ImageView imageView = (ImageView) findViewById(R.id.imageView);

        inGameMenu = new Dialog(this);
        inGameMenu.setContentView(R.layout.ingamemenu);
        inGameMenu.setCancelable(true);

        ((Button) inGameMenu.findViewById(R.id.ingamemenuYes)).setTextSize(Util
                .getTextSize());
        ((Button) inGameMenu.findViewById(R.id.ingamemenuNo)).setTextSize(Util
                .getTextSize());
        ((Button) inGameMenu.findViewById(R.id.ingamemenuSettings))
                .setTextSize(Util.getTextSize());
        ((TextView) inGameMenu.findViewById(R.id.ingamemenuText))
                .setTextSize(Util.getTextSize());

        inGameMenu.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            public void onDismiss(DialogInterface dialog)
            {
                onResume();
            }
        });
        ((Button) inGameMenu.findViewById(R.id.ingamemenuYes))
                .setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        inGameMenu.dismiss();
                        finish();
                    }
                });
        ((Button) inGameMenu.findViewById(R.id.ingamemenuNo))
                .setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        inGameMenu.dismiss();
                    }
                });
        ((Button) inGameMenu.findViewById(R.id.ingamemenuSettings))
                .setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        inGameMenu.dismiss();
                        startActivity(new Intent("com.game.one.Config"));
                        finish();
                    }
                });

        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.TOP);
        mainLayout.setBackgroundColor(Color.BLUE);

        LinearLayout topLayout = new LinearLayout(this);

        wordLayout = new LinearLayout(this);

        LinearLayout levelLayout = new LinearLayout(this);

        LinearLayout timerLayout = new LinearLayout(this);

        LinearLayout starLayout = new LinearLayout(this);

        wordLayout.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);
        wordLayout.setBackgroundColor(Color.YELLOW);
        wordLayout.setPadding(20, 0, 20, 0);
        LinearLayout.LayoutParams wordParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        wordParams.setMargins(40, 0, 40, 0);
        wordLayout.setLayoutParams(wordParams);

        topLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        topLayout.setBackgroundColor(Color.TRANSPARENT);
        topLayout.setPadding(10, 0, 0, 0);

        leftLetterBtn = new Button(this);
        leftLetterBtn.setTextColor(Color.RED);
        leftLetterBtn.setPadding(0, 0, 20, 0);
        leftLetterBtn.setTypeface(Typeface.DEFAULT_BOLD);
        leftLetterBtn.setBackgroundColor(Color.YELLOW);
        leftLetterBtn.setText("C");
        leftLetterBtn.setTextSize(getResources().getDimension(
                R.dimen.textsize45sp));
        leftLetterBtn.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("DefaultLocale")
            public void onClick(View v)
            {
                int resID = getApplicationContext().getResources().getIdentifier("c",
                        "raw", getApplicationContext().getPackageName());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), resID);
                try
                {
                    // sets volume of mediaPlayer.
                    mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
                    mediaPlayer.start();
                } catch (IllegalStateException e)
                {
                }

            }
        });

        middleLetterBtn = new Button(this);
        middleLetterBtn.setTextColor(Color.RED);
        middleLetterBtn.setPadding(0, 0, 0, 0);
        middleLetterBtn.setTypeface(Typeface.DEFAULT_BOLD);
        middleLetterBtn.setBackgroundColor(Color.YELLOW);
        middleLetterBtn.setText("_");
        middleLetterBtn.setTextSize(getResources().getDimension(
                R.dimen.textsize45sp));
        middleLetterBtn.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("DefaultLocale")
            public void onClick(View v)
            {
                int resID = getApplicationContext().getResources().getIdentifier("a",
                        "raw", getApplicationContext().getPackageName());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), resID);
                try
                {
                    // sets volume of mediaPlayer.
                    mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
                    mediaPlayer.start();
                } catch (IllegalStateException e)
                {
                }
            }
        });

        rightLetterBtn = new Button(this);
        rightLetterBtn.setTextColor(Color.RED);
        rightLetterBtn.setPadding(20, 0, 20, 0);
        rightLetterBtn.setTypeface(Typeface.DEFAULT_BOLD);
        rightLetterBtn.setBackgroundColor(Color.YELLOW);
        rightLetterBtn.setText("T");
        rightLetterBtn.setTextSize(getResources().getDimension(
                R.dimen.textsize45sp));
        rightLetterBtn.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("DefaultLocale")
            public void onClick(View v)
            {
                int resID = getApplicationContext().getResources().getIdentifier("t",
                        "raw", getApplicationContext().getPackageName());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), resID);
                try
                {
                    // sets volume of mediaPlayer.
                    mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
                    mediaPlayer.start();
                } catch (IllegalStateException e)
                {
                }
            }
        });

        pauseButton = new Button(this);
        pauseButton.setText("  ||  ");
        pauseButton.setTextColor(Color.WHITE);
        pauseButton.setBackgroundColor(Color.BLACK);
        pauseButton.setTextSize(getResources().getDimension(
                R.dimen.textsize25sp));
        pauseButton.setPadding(10, 10, 10, 10);
        pauseButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                inGameMenu.show();
            }
        });

        levelLayout.setOrientation(LinearLayout.VERTICAL);
        levelLayout.setPadding(30, 0, 30, 0);

        TextView levelLabel = new TextView(this);
        levelLabel.setTextSize(getResources()
                .getDimension(R.dimen.textsize12sp));
        levelLabel.setTypeface(Typeface.DEFAULT_BOLD);
        levelLabel.setText("Level:");
        levelLabel.setTextColor(Color.WHITE);
        levelLabel.setBackgroundColor(Color.TRANSPARENT);
        levelLabel.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);

        TextView levelNumber = new TextView(this);
        levelNumber.setTextSize(getResources().getDimension(
                R.dimen.textsize30sp));
        levelNumber.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);
        levelNumber.setTextColor(Color.WHITE);
        levelNumber.setBackgroundColor(Color.TRANSPARENT);
        levelNumber.setText(Integer.toString(1));

        levelLayout.addView(levelLabel);
        levelLayout.addView(levelNumber);

        timerLayout.setBackgroundColor(Color.TRANSPARENT);
        timerLayout.setPadding(10, 10, 10, 10);
        LinearLayout.LayoutParams timerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        timerParams.setMargins(35, 0, 0, 0);
        timerLayout.setLayoutParams(timerParams);

        TextView timer = new TextView(this);
        timer.setTextSize(getResources().getDimension(R.dimen.textsize35sp));
        timer.setBackgroundColor(Color.TRANSPARENT);
        timer.setTextColor(Color.WHITE);
        timer.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        timerLayout.addView(timer);

        starLayout.setPadding(10, 10, 10, 10);
        starLayout.setBackgroundColor(Color.TRANSPARENT);
        starLayout.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams starParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        starParams.setMargins(10, 10, 10, 10);
        starLayout.setLayoutParams(starParams);

        RatingBar rateBar = new RatingBar(this, null,
                android.R.attr.ratingBarStyleIndicator);
        rateBar.setStepSize((float) 1.0);
        rateBar.setMax(4);
        rateBar.setNumStars(4);
        rateBar.setRating(0.0f);
        rateBar.setFocusable(false);
        rateBar.setBackgroundColor(Color.TRANSPARENT);
        LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(
                getResources().getColor(R.color.HummingbirdGreen),
                PorterDuff.Mode.SRC_ATOP);

        TextView stats = new TextView(this);
        stats.setTextSize(getResources().getDimension(R.dimen.textsize25sp));
        stats.setTextColor(Color.WHITE);

        topLayout.addView(pauseButton);
        topLayout.addView(stats);
        starLayout.addView(rateBar);
        topLayout.addView(starLayout);

        ImageView image = new ImageView(this);

        wordLayout.addView(leftLetterBtn);
        wordLayout.addView(middleLetterBtn);
        wordLayout.addView(rightLetterBtn);

        wordLayout.addView(image);
        topLayout.addView(wordLayout);

        topLayout.addView(levelLayout);
        topLayout.addView(timerLayout);

        mainLayout.addView(topLayout);

        Bitmap b;
        Drawable d;
        b = createBitmap(this.getResources().getDrawable(R.drawable.cat));
        d = new BitmapDrawable(b);
        image.setBackground(d);
        long millis = 70000;

        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);

        String ms = String.format("%02d:%02d", minutes, seconds);

        timer.setText(ms);
        stats.setText("   2/2");
        rateBar.setRating(2.0f);
        //leftLetterBtn.performClick();
        mediaTimer.start();

        //imageView.setBackgroundResource(R.drawable.scene);


    }

    private void clickLeftButton()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                leftLetterBtn.performClick();
                leftLetterBtn.invalidate();
                leftLetterBtn.setPressed(true);
                leftLetterBtn.setTextColor(getResources().getColor(R.color.Cranberry));
            }
        });
    }

    private void resetLeftButton()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                leftLetterBtn.setPressed(false);
                leftLetterBtn.invalidate();
                leftLetterBtn.setTextColor(Color.RED);
            }
        });
    }

    private void clickMiddleButton()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                middleLetterBtn.performClick();
                middleLetterBtn.invalidate();
                middleLetterBtn.setPressed(true);
                middleLetterBtn.setTextColor(getResources().getColor(R.color.Cranberry));
            }
        });
    }

    private void resetMiddleButton()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                middleLetterBtn.setPressed(false);
                middleLetterBtn.invalidate();
                middleLetterBtn.setTextColor(Color.RED);
            }
        });
    }

    private void clickRightButton()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                rightLetterBtn.performClick();
                rightLetterBtn.invalidate();
                rightLetterBtn.setPressed(true);
                rightLetterBtn.setTextColor(getResources().getColor(R.color.Cranberry));
            }
        });
    }

    private void resetRightButton()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                rightLetterBtn.setPressed(false);
                rightLetterBtn.invalidate();
                rightLetterBtn.setTextColor(Color.RED);
            }
        });
    }

    private void clickOpenDialogButton()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                pauseButton.setBackgroundColor(Color.LTGRAY);
                pauseButton.performClick();
            }
        });
    }

    private void clickExitButton()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                pauseButton.setPressed(false);
                pauseButton.invalidate();
                rightLetterBtn.setTextColor(Color.BLACK);
                inGameMenu.findViewById(R.id.ingamemenuYes).setBackgroundColor(Color.RED);
            }
        });
    }

    private void exitHelp()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                inGameMenu.findViewById(R.id.ingamemenuYes).performClick();
            }
        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);



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

    public static Bitmap createBitmap(Drawable drawable)
    {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bm = bd.getBitmap();
        return Bitmap.createScaledBitmap(bm,
                (int) ((Util.getScaleFactor() * Util.PIXEL_HEIGHT) / 11.8),
                (int) ((Util.getScaleFactor() * Util.PIXEL_HEIGHT) / 11.8),
                false);
    }
}
