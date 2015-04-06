package com.game.one;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
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
    private Button leftLetterBtn;
    private Button middleLetterBtn;
    private Button rightLetterBtn;
    private Button pauseButton;
    private ImageView imageView;
    private Dialog inGameMenu;
    private LinearLayout wordLayout;
    private MediaPlayer mediaPlayer;
    private boolean start = true;

    TimerExec mediaTimer = new TimerExec(500, -1, new TimerExecTask()
    {
        @Override
        public void onTick()
        {
            if (mediaTimer.getElapsedTime() == 1000)
            {
                clickLeftButton();
            }

            if (mediaTimer.getElapsedTime() == 2000)
            {
                resetLeftButton();
            }

            if (mediaTimer.getElapsedTime() == 2500)
            {
                clickMiddleButton();
            }

            if (mediaTimer.getElapsedTime() == 3000)
            {
                resetMiddleButton();
            }

            if (mediaTimer.getElapsedTime() == 3500)
            {
                clickRightButton();
            }

            if (mediaTimer.getElapsedTime() == 4000)
            {
                resetRightButton();
                toExit();
            }

            if (mediaTimer.getElapsedTime() == 11500)
            {
               clickOpenDialogButton();
            }

            if (mediaTimer.getElapsedTime() == 13000)
            {
                clickExitButton();
            }

            if (mediaTimer.getElapsedTime() == 14000)
            {
                exitHelp();
            }

            if (mediaTimer.getElapsedTime() > 14000)
            {
                onFinish();
            }
        }

        @Override
        public void onFinish()
        {
            mediaTimer.cancel();
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        imageView = (ImageView) findViewById(R.id.imageView);

        inGameMenu = new Dialog(this);
        inGameMenu.setContentView(R.layout.ingamemenu);
        inGameMenu.setCancelable(false);

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
                Util.musicPlayer.start();
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

                    }
                });
        Typeface levelFont = Typeface.createFromAsset(getAssets(), "kidsFont.ttf");
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.TOP);
        mainLayout.setBackgroundColor(Color.WHITE);

        LinearLayout topLayout = new LinearLayout(this);

        wordLayout = new LinearLayout(this);

        LinearLayout levelLayout = new LinearLayout(this);

        LinearLayout timerLayout = new LinearLayout(this);

        LinearLayout starLayout = new LinearLayout(this);

        wordLayout.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);
        wordLayout.setBackgroundColor(Color.TRANSPARENT);
        wordLayout.setPadding(20, 0, 20, 0);
        LinearLayout.LayoutParams wordParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        wordParams.setMargins(20, 0, 30, 0);
        wordLayout.setLayoutParams(wordParams);

        topLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        topLayout.setBackgroundColor(Color.TRANSPARENT);
        topLayout.setBackground(getResources().getDrawable(R.drawable.score_bar));
        topLayout.setPadding(30, 0, 0, 0);

        leftLetterBtn = new Button(this);
        leftLetterBtn.setTypeface(levelFont);
        leftLetterBtn.setTextColor(Color.RED);
        leftLetterBtn.setPadding(0, 0, 20, 0);
        leftLetterBtn.setBackgroundColor(Color.TRANSPARENT);
        leftLetterBtn.setText("C");
        leftLetterBtn.setTextSize(getResources().getDimension(
                R.dimen.textsize45sp));
        leftLetterBtn.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("DefaultLocale")
            public void onClick(View v)
            {
                final MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.c);
                try
                {
                    mPlayer.setVolume(Util.soundVolume, Util.soundVolume);
                } catch (IllegalStateException e)
                {
                }
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    public void onCompletion(MediaPlayer mp)
                    {
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayer.release();
                    }
                });
                mPlayer.start();
            }
        });


        middleLetterBtn = new Button(this);
        middleLetterBtn.setTypeface(levelFont);
        middleLetterBtn.setTextColor(Color.RED);
        middleLetterBtn.setPadding(0, 0, 0, 0);
        middleLetterBtn.setBackgroundColor(Color.TRANSPARENT);
        middleLetterBtn.setText("_");
        middleLetterBtn.setTextSize(getResources().getDimension(
                R.dimen.textsize45sp));
        middleLetterBtn.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("DefaultLocale")
            public void onClick(View v)
            {
                final MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.a);
                try
                {
                    mPlayer.setVolume(Util.soundVolume, Util.soundVolume);
                } catch (IllegalStateException e)
                {
                }

                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    public void onCompletion(MediaPlayer mp)
                    {
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayer.release();
                    }
                });
                mPlayer.start();
            }
        });

        rightLetterBtn = new Button(this);
        rightLetterBtn.setTypeface(levelFont);
        rightLetterBtn.setTextColor(Color.RED);
        rightLetterBtn.setPadding(20, 0, 20, 0);
        rightLetterBtn.setBackgroundColor(Color.TRANSPARENT);
        rightLetterBtn.setText("T");
        rightLetterBtn.setTextSize(getResources().getDimension(
                R.dimen.textsize45sp));
        rightLetterBtn.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("DefaultLocale")
            public void onClick(View v)
            {
                final MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.t);
                try
                {
                    mPlayer.setVolume(Util.soundVolume, Util.soundVolume);
                } catch (IllegalStateException e)
                {
                }

                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    public void onCompletion(MediaPlayer mp)
                    {
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayer.release();
                    }
                });
                mPlayer.start();
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
        levelLayout.setPadding(20, 10, 30, 10);

        TextView levelLabel = new TextView(this);
        levelLabel.setTypeface(levelFont);
        levelLabel.setTextSize(getResources()
                .getDimension(R.dimen.textsize12sp));
        levelLabel.setTypeface(Typeface.DEFAULT_BOLD);
        levelLabel.setText("Level:");
        levelLabel.setTextColor(Color.BLACK);
        levelLabel.setBackgroundColor(Color.TRANSPARENT);
        levelLabel.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);

        TextView levelNumber = new TextView(this);
        levelNumber.setTypeface(levelFont);
        levelNumber.setTextSize(getResources().getDimension(
                R.dimen.textsize30sp));
        levelNumber.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);
        levelNumber.setTextColor(Color.BLACK);
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
        timer.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);
        timer.setPadding(0, 0, 0, 15);
        timer.setTextSize(getResources().getDimension(R.dimen.textsize45sp));
        timer.setBackgroundColor(Color.TRANSPARENT);
        Typeface timerFont = Typeface.createFromAsset(getAssets(), "open24Hr.ttf");
        timer.setTextColor(getResources().getColor(R.color.HummingbirdGreen));
        timer.setTypeface(timerFont);

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
                getResources().getColor(R.color.Blue),
                PorterDuff.Mode.SRC_ATOP);

        TextView stats = new TextView(this);
        stats.setTypeface(levelFont);
        stats.setTextSize(getResources().getDimension(R.dimen.textsize25sp));
        stats.setTextColor(Color.BLACK);

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
    }

    private void welcome()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.welcome);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    public void onCompletion(MediaPlayer mp)
                    {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        howToPlay();
                    }
                });

                try
                {
                    mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
                } catch (IllegalStateException e)
                {
                }

                mediaPlayer.start();
            }
        });
    }

    private void howToPlay()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.to_play);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    public void onCompletion(MediaPlayer mp)
                    {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        needHelp();
                    }
                });

                try
                {
                    mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
                } catch (IllegalStateException e)
                {
                }

                mediaPlayer.start();
            }
        });
    }

    private void needHelp()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.help);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    public void onCompletion(MediaPlayer mp)
                    {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaTimer.start();
                    }
                });

                try
                {
                    mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
                } catch (IllegalStateException e)
                {
                }

                mediaPlayer.start();
            }
        });
    }

    private void toExit()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.exit);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    public void onCompletion(MediaPlayer mp)
                    {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }
                });

                try
                {
                    mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
                } catch (IllegalStateException e)
                {
                }

                mediaPlayer.start();
            }
        });
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
                pauseButton.callOnClick();
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

    public void onWindowFocusChanged(boolean hasFocus)
    {

        super.onWindowFocusChanged(hasFocus);

        imageView.setBackgroundResource(R.drawable.help_animation);
        AnimationDrawable helpAnimation = (AnimationDrawable) imageView.getBackground();
        helpAnimation.start();

        if(hasFocus == true && start == true)
        {
            start = false;
            welcome();
        }
    }

    protected void onDestroy()
    {
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
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
