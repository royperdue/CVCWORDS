package com.game.one;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.TextView;

import com.game.one.model.UserData;
import com.game.one.persistence.DBAdapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

@SuppressLint(
        {"ClickableViewAccessibility", "DefaultLocale"})
public class Game extends Activity implements OnTouchListener
{
    public static Game theGame;
    private MediaPlayer mediaPlayer1;
    private MediaPlayer mediaPlayer2;
    private MediaPlayer mediaPlayer3;
    private MediaPlayer mediaPlayer4;
    private MediaPlayer mPlayerLevelOne;
    private boolean runAudio1 = true;
    private boolean runAudio2 = true;
    private boolean runAudio3 = true;
    private boolean runAudio4 = true;
    private boolean isResuming = false;
    private RunAudioLevelOne runLevelOneAudio;
    volatile private long wordDuration = 0L;
    private int level = 0;
    volatile private int starPoints = 0;
    private GameView view;
    private String flyId = "X";
    private TextView stats;
    private TextView levelLabel;
    private TextView levelNumber;
    private TextView timer;
    private ImageView image;
    private Context sharedContext = null;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;
    private RatingBar rateBar;
    private Button leftLetterBtn;
    private Button middleLetterBtn;
    private Button rightLetterBtn;
    private static Bitmap b;
    private Drawable d;
    volatile private int points = 0;
    private Dialog inGameMenu;
    private int attemptNumber = 0;
    private String word = "";
    private String[] words =
            {"C A B", "L A B", "C A P", "M A P", "N A P", "D A D", "M A D",
                    "P E N", "C O B", "P A D", "S A D", "C A R", "J A R", "B A G",
                    "T A G", "B A T", "C A T", "H A T", "M A T", "R A T", "H A M",
                    "J A M", "R A M", "C A N", "F A N", "M A N", "P A N", "V A N",
                    "B A R", "R A G", "B E D", "R E D", "W E D", "L E G", "H E N",
                    "M E N", "K I D", "L I D", "P I G", "F I G", "W I G", "F I N",
                    "P I N", "H I P", "L I P", "R I P", "T I P", "S I P", "B I T",
                    "F I T", "K I T", "S I T", "T E N", "D E N", "J E T", "V E T",
                    "F O X", "F O G", "H O G", "L O G", "J O G", "C O P", "H O P",
                    "M O P", "T O P", "P O P", "C O T", "D O T", "H O T", "P O T",
                    "B O X", "C U B", "H U B", "T U B", "B U D", "M U D", "B U G",
                    "C O B", "H U G", "J U G", "M U G", "R U G", "C U P", "P U P",
                    "C U T", "J O B", "S O B", "B O G", "D O G", "H U T", "T U G",
                    "W E T", "B A D", "B E G", "B E T", "B I B", "B I G", "B I N",
                    "B U N", "B U S", "C O G", "W I N", "D A M", "D I G", "D I M",
                    "D I P", "F E D", "F I B", "F U N", "G A G", "G A P", "W E B",
                    "G E T", "G O B", "G U M", "H I D", "H I M", "W A G", "H U M",
                    "J I G", "K E G", "L A D", "L E G", "L I T", "L O T", "M O B",
                    "M O M", "M U M", "N A G", "N E T", "N I P", "N U N", "N U T",
                    "P E G", "P E T", "R A N", "R A P", "R I B", "R I G", "R I M",
                    "R O B", "R O T", "R U B", "R U T", "S A P", "S E T", "S U B",
                    "S U M", "T A B", "T A P", "T I N"};

    TimerExec spriteTimer = new TimerExec(100, -1, new TimerExecTask()
    {
        @Override
        public void onTick()
        {
            checkTime();
        }

        @Override
        public void onFinish()
        {
        }
    });

    TimerExec wordTimer = new TimerExec(1000, wordDuration, new TimerExecTask()
    {
        @Override
        public void onTick()
        {
            updateTimer();
        }

        @Override
        public void onFinish()
        {
        }
    });

    TimerExec gameTimer1 = new TimerExec(500, -1, new TimerExecTask()
    {
        @Override
        public void onTick()
        {
            if (gameTimer1.getElapsedTime() == 1000)
            {
                Game.theGame.runWordSound();
                updateWordBoxText();
                updateStars();
                onFinish();
            }
        }

        @Override
        public void onFinish()
        {
            gameTimer1.cancel();
        }
    });

    TimerExec gameTimer2 = new TimerExec(200, -1, new TimerExecTask()
    {
        @Override
        public void onTick()
        {
            if (gameTimer2.getElapsedTime() == 400)
            {
                Game.theGame.runSpitSound();
                onFinish();
            }
        }

        @Override
        public void onFinish()
        {
            gameTimer2.cancel();
        }
    });

    private MediaPlayer createNewMediaPlayer(final int res) throws IOException
    {
        final MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), res);
        mPlayer.setVolume(Util.soundVolume, Util.soundVolume);

        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener()
        {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra)
            {
                Log.e(getPackageName(), String.format("Error(%s%s)", what, extra));

                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED || what == MediaPlayer.MEDIA_ERROR_UNKNOWN)
                {
                    mp.reset();
                    try
                    {
                        createNewMediaPlayer(res);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });

        return mPlayer;
    }

    // ############## ONCREATE ###############
    @Override
    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        theGame = this;

        prefs = getApplicationContext().getSharedPreferences("com.came.one",
                Context.MODE_PRIVATE);

        wordDuration = 70000;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        view = new GameView(this);

        level = 1;
        setLayouts();
        setupInGameMenu();

        setWord();
        view.setOnTouchListener(this);
        spriteTimer.start();
        wordTimer.start();

        edit = prefs.edit();
        edit.putBoolean("HAS_DATA", false);
        edit.commit();

        if (isPackageInstalled())
        {
            try
            {
                sharedContext = getApplicationContext().createPackageContext("com.gradebookdynamics.utility", Context.CONTEXT_INCLUDE_CODE);
                if (sharedContext == null)
                {
                    return;
                }
            } catch (Exception e)
            {

                return;
            }

            DBAdapter.init(sharedContext);

            String attempt = Integer.toString(0);

            String output = getDateTime() + " " + attempt + " " + "none" + " " + "none" + " false" +
                    " 3_b" + " ENGLISH" + " CVC_WORD_FROG";

            DBAdapter.addUserData(new UserData("com.game.one", output));

            Intent intent = new Intent();
            intent.setClassName("com.gradebookdynamics.utility", "com.gradebookdynamics.utility.TransmitData");

            startService(intent);
        }

        if (level == 1 && mPlayerLevelOne == null)
        {
            runLevelOneAudio = new RunAudioLevelOne();
            runLevelOneAudio.execute();
        }
    }

    private class RunAudioLevelOne extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                mPlayerLevelOne = createNewMediaPlayer(getResources().getIdentifier("level1", "raw", getApplicationContext().getPackageName()));
                mPlayerLevelOne.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        mPlayerLevelOne.stop();
                        mPlayerLevelOne.reset();
                        mPlayerLevelOne.release();
                        mPlayerLevelOne = null;
                    }
                });

                mPlayerLevelOne.setVolume(Util.soundVolume, Util.soundVolume);

                mPlayerLevelOne.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                {
                    @Override
                    public void onPrepared(MediaPlayer mp)
                    {
                        mPlayerLevelOne.start();
                    }
                });

            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onCancelled()
        {
            mPlayerLevelOne.stop();
            mPlayerLevelOne.reset();
            mPlayerLevelOne.release();
            mPlayerLevelOne = null;
        }
    }

    // ######### SETS UP THE DIALOG LAUNCHED BY THE PAUSE BUTTON ########
    private void setupInGameMenu()
    {
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

        inGameMenu.setOnDismissListener(new OnDismissListener()
        {
            public void onDismiss(DialogInterface dialog)
            {
                onResume();
            }
        });
        ((Button) inGameMenu.findViewById(R.id.ingamemenuYes))
                .setOnClickListener(new OnClickListener()
                {
                    public void onClick(View v)
                    {
                        isResuming = false;
                        inGameMenu.dismiss();
                        if (mediaPlayer1 != null)
                        {
                            mediaPlayer1.release();
                            mediaPlayer1 = null;
                        }
                        if (mPlayerLevelOne != null)
                            runLevelOneAudio.cancel(true);

                        finish();
                    }
                });
        ((Button) inGameMenu.findViewById(R.id.ingamemenuNo))
                .setOnClickListener(new OnClickListener()
                {
                    public void onClick(View v)
                    {
                        inGameMenu.dismiss();
                    }
                });
        ((Button) inGameMenu.findViewById(R.id.ingamemenuSettings))
                .setOnClickListener(new OnClickListener()
                {
                    public void onClick(View v)
                    {
                        inGameMenu.dismiss();
                        mediaPlayer1.release();
                        mediaPlayer1 = null;
                        startActivity(new Intent("com.game.one.Config"));
                        finish();
                    }
                });
    }

    // ######### SETS UP THE GAME SCORE SECTION AT TOP OF SCREEN ########
    private void setLayouts()
    {
        Typeface levelFont = Typeface.createFromAsset(getAssets(), "kidsFont.ttf");
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.TOP);
        mainLayout.setBackgroundColor(Color.WHITE);

        LinearLayout topLayout = new LinearLayout(this);

        LinearLayout wordLayout = new LinearLayout(this);

        LinearLayout levelLayout = new LinearLayout(this);

        LinearLayout timerLayout = new LinearLayout(this);

        LinearLayout starLayout = new LinearLayout(this);

        wordLayout.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);
        wordLayout.setBackgroundColor(Color.TRANSPARENT);
        wordLayout.setPadding(20, 0, 20, 0);
        LayoutParams wordParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        wordParams.setMargins(20, 0, 20, 0);
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
        leftLetterBtn.setTextSize(getResources().getDimension(
                R.dimen.textsize45sp));
        leftLetterBtn.setOnClickListener(new OnClickListener()
        {
            @SuppressLint("DefaultLocale")
            public void onClick(View v)
            {
                String w = word.toLowerCase();
                String[] letters = w.split(" ");

                try
                {
                    MediaPlayer mp = createNewMediaPlayer(getResources().getIdentifier(letters[0], "raw", getApplicationContext().getPackageName()));
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                    {
                        @Override
                        public void onCompletion(MediaPlayer mp)
                        {
                            mp.stop();
                            mp.reset();
                            mp.release();
                        }
                    });
                    mp.start();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        });

        middleLetterBtn = new Button(this);
        middleLetterBtn.setTypeface(levelFont);
        middleLetterBtn.setTextColor(Color.RED);
        middleLetterBtn.setPadding(0, 0, 0, 0);
        middleLetterBtn.setBackgroundColor(Color.TRANSPARENT);
        middleLetterBtn.setTextSize(getResources().getDimension(
                R.dimen.textsize45sp));
        middleLetterBtn.setOnClickListener(new OnClickListener()
        {
            @SuppressLint("DefaultLocale")
            public void onClick(View v)
            {
                String w = word.toLowerCase();
                String[] letters = w.split(" ");

                try
                {
                    MediaPlayer mp = createNewMediaPlayer(getResources().getIdentifier(letters[1], "raw", getApplicationContext().getPackageName()));
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                    {
                        @Override
                        public void onCompletion(MediaPlayer mp)
                        {
                            mp.stop();
                            mp.reset();
                            mp.release();
                        }
                    });
                    mp.start();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        rightLetterBtn = new Button(this);
        rightLetterBtn.setTypeface(levelFont);
        rightLetterBtn.setTextColor(Color.RED);
        rightLetterBtn.setPadding(20, 0, 20, 0);
        rightLetterBtn.setBackgroundColor(Color.TRANSPARENT);
        rightLetterBtn.setTextSize(getResources().getDimension(
                R.dimen.textsize45sp));
        rightLetterBtn.setOnClickListener(new OnClickListener()
        {
            @SuppressLint("DefaultLocale")
            public void onClick(View v)
            {

                String w = word.toLowerCase();
                String[] letters = w.split(" ");

                try
                {
                    MediaPlayer mp = createNewMediaPlayer(getResources().getIdentifier(letters[2], "raw", getApplicationContext().getPackageName()));
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                    {
                        @Override
                        public void onCompletion(MediaPlayer mp)
                        {
                            mp.stop();
                            mp.reset();
                            mp.release();
                        }
                    });
                    mp.start();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        final Button pauseButton = new Button(this);
        pauseButton.setText("  ||  ");
        pauseButton.setTextColor(Color.WHITE);
        pauseButton.setBackgroundColor(Color.BLACK);
        pauseButton.setTextSize(getResources().getDimension(
                R.dimen.textsize25sp));
        pauseButton.setPadding(10, 10, 10, 10);
        pauseButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                inGameMenu.show();
            }
        });

        levelLayout.setOrientation(LinearLayout.VERTICAL);
        levelLayout.setPadding(20, 0, 20, 0);

        levelLabel = new TextView(this);
        levelLabel.setTypeface(levelFont);
        levelLabel.setTextSize(getResources()
                .getDimension(R.dimen.textsize12sp));
        levelLabel.setText("Level:");
        levelLabel.setTextColor(Color.BLACK);
        levelLabel.setBackgroundColor(Color.TRANSPARENT);
        levelLabel.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);

        levelNumber = new TextView(this);
        levelNumber.setTypeface(levelFont);
        levelNumber.setTextSize(getResources().getDimension(
                R.dimen.textsize30sp));
        levelNumber.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);
        levelNumber.setTextColor(Color.BLACK);
        levelNumber.setBackgroundColor(Color.TRANSPARENT);
        levelNumber.setText(Integer.toString(level));

        levelLayout.addView(levelLabel);
        levelLayout.addView(levelNumber);

        timerLayout.setBackgroundColor(Color.TRANSPARENT);
        timerLayout.setPadding(10, 10, 10, 10);
        LayoutParams timerParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        timerParams.setMargins(25, 0, 0, 0);
        timerLayout.setLayoutParams(timerParams);

        timer = new TextView(this);
        timer.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);
        timer.setPadding(0, 0, 0, 15);
        timer.setTextSize(getResources().getDimension(R.dimen.textsize45sp));
        timer.setBackgroundColor(Color.TRANSPARENT);
        Typeface timerFont = Typeface.createFromAsset(getAssets(), "open24Hr.ttf");
        timer.setTextColor(getResources().getColor(R.color.HummingbirdGreen));
        timer.setTypeface(timerFont);
        this.resetTimeDisplay();

        timerLayout.addView(timer);

        starLayout.setPadding(10, 10, 10, 10);
        starLayout.setBackgroundColor(Color.TRANSPARENT);
        starLayout.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);
        LayoutParams starParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        starParams.setMargins(10, 10, 0, 10);
        starLayout.setLayoutParams(starParams);

        rateBar = new RatingBar(this, null,
                android.R.attr.ratingBarStyleIndicator);

        rateBar.setStepSize((float) 1.0);
        rateBar.setMax(4);
        rateBar.setNumStars(4);
        rateBar.setRating(0.0f);
        rateBar.setFocusable(false);
        rateBar.setBackgroundColor(Color.TRANSPARENT);

        setStarColor();

        stats = new TextView(this);
        stats.setTypeface(levelFont);
        stats.setGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.CENTER_VERTICAL);
        stats.setTextSize(getResources().getDimension(R.dimen.textsize25sp));
        stats.setTextColor(Color.BLACK);

        topLayout.addView(pauseButton);
        topLayout.addView(stats);
        starLayout.addView(rateBar);
        topLayout.addView(starLayout);

        image = new ImageView(this);

        wordLayout.addView(leftLetterBtn);
        wordLayout.addView(middleLetterBtn);
        wordLayout.addView(rightLetterBtn);

        wordLayout.addView(image);
        topLayout.addView(wordLayout);

        topLayout.addView(levelLayout);
        topLayout.addView(timerLayout);

        mainLayout.addView(topLayout);
        mainLayout.addView(view);
        setContentView(mainLayout);
    }

    public void updateLevel()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (level < 11)
                {
                    String audioFileName = "level" + Integer.toString(level);

                    int resID = getApplicationContext().getResources().getIdentifier(audioFileName,
                            "raw", getApplicationContext().getPackageName());


                    mediaPlayer4 = MediaPlayer.create(getApplicationContext(), resID);

                    mediaPlayer4.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                    {
                        @Override
                        public void onCompletion(MediaPlayer mp)
                        {
                            mediaPlayer4.stop();
                            mediaPlayer4.reset();
                            mediaPlayer4.release();
                            mediaPlayer4 = null;
                        }
                    });

                    mediaPlayer4.setVolume(Util.soundVolume, Util.soundVolume);

                    mediaPlayer4.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                    {
                        @Override
                        public void onPrepared(MediaPlayer mp)
                        {
                            if (runAudio4)
                                mediaPlayer4.start();
                        }
                    });

                    updateWordBoxText();
                }
                if (level == 11)
                {
                    mediaPlayer2.release();
                    mediaPlayer2 = null;
                    spriteTimer.cancel();
                    wordTimer.cancel();
                    timer.setText("00;00");
                    word = "- - -";
                    updateWordBoxText();

                    String[] audioFileNames = {"hopping", "out_of_this_world", "sooper", "super_duper", "stupendious", "yaaaa",
                            "way_to_go", "wonderful", "you_did_it", "you_got_it"};

                    Random r = new Random();
                    int pick = r.nextInt(9);

                    if (pick == 0)
                        mediaPlayer4 = MediaPlayer.create(getApplicationContext(), R.raw.hopping);
                    if (pick == 1)
                        mediaPlayer4 = MediaPlayer.create(getApplicationContext(), R.raw.out_of_this_world);
                    if (pick == 2)
                        mediaPlayer4 = MediaPlayer.create(getApplicationContext(), R.raw.sooper);
                    if (pick == 3)
                        mediaPlayer4 = MediaPlayer.create(getApplicationContext(), R.raw.super_duper);
                    if (pick == 4)
                        mediaPlayer4 = MediaPlayer.create(getApplicationContext(), R.raw.stupendious);
                    if (pick == 5)
                        mediaPlayer4 = MediaPlayer.create(getApplicationContext(), R.raw.yaaaa);
                    if (pick == 6)
                        mediaPlayer4 = MediaPlayer.create(getApplicationContext(), R.raw.way_to_go);
                    if (pick == 7)
                        mediaPlayer4 = MediaPlayer.create(getApplicationContext(), R.raw.wonderful);
                    if (pick == 8)
                        mediaPlayer4 = MediaPlayer.create(getApplicationContext(), R.raw.you_did_it);
                    if (pick == 9)
                        mediaPlayer4 = MediaPlayer.create(getApplicationContext(), R.raw.you_got_it);

                    mediaPlayer4.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                    {
                        @Override
                        public void onCompletion(MediaPlayer mp)
                        {
                            mediaPlayer4.stop();
                            mediaPlayer4.reset();
                            mediaPlayer4.release();
                            mediaPlayer4 = null;
                        }
                    });

                    mediaPlayer4.setVolume(Util.soundVolume, Util.soundVolume);

                    mediaPlayer4.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                    {
                        @Override
                        public void onPrepared(MediaPlayer mp)
                        {
                            mediaPlayer4.start();
                        }
                    });

                    view.setBackgroundPlayEnd(true);

                    TimerExec endGameTimer = new TimerExec(1000, 4000, new TimerExecTask()
                    {
                        @Override
                        public void onTick()
                        {

                        }

                        @Override
                        public void onFinish()
                        {
                            Game.theGame.finish();
                        }
                    });
                    endGameTimer.start();
                }
            }
        });
    }

    // ########## PRESENTS NEXT WORD TO PLAYER ###########
    public void updateWordBoxText()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (b != null && d != null)
                    unloadBackground();

                setWord();
                wordTimer.start();

                stats.setText("\t" + getPoints() + "/" + getAttemptNumber()
                        + " ");
            }
        });
    }

    // ########## SAVES RESULTS & UPDATES ATTEMPTS ###########
    public void updateAttempts()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                // makes sure that the current word contains the letter of or id
                // of the
                // selected fly. The flyId was set by the setFlyId() method in
                // this class.
                if (word.contains(flyId) == true)
                {
                    increasePoints(1);
                    increaseStarPoints();
                    String[] letters = word.split(" ");
                    leftLetterBtn.setText(letters[0]);
                    middleLetterBtn.setText(letters[1]);
                    rightLetterBtn.setText(letters[2]);


                    if (isPackageInstalled())
                    {
                        String attempt = Integer.toString(getAttemptNumber());

                        String output = getDateTime() + " " + attempt + " " + word.replaceAll(" ", "") + " " + flyId + " true" +
                                " 3_b" + " ENGLISH" + " CVC_WORD_FROG";

                        DBAdapter.addUserData(new UserData("com.game.one", output));

                        edit = prefs.edit();
                        edit.putBoolean("HAS_DATA", true);
                        edit.commit();
                    }

                    // sets the contents of the textbox holding the word to the
                    // word variable,
                    // the word variable contains the complete word.
                    // wordBox.setText(word);
                    // sets the flyId variable back to X so the next word will
                    // be sure not to
                    // contain it.
                    flyId = "X";
                }
                if (word.contains(flyId) == false && flyId.equals("X") == false)
                {
                    decreaseStarPoints();

                    if (isPackageInstalled())
                    {
                        String attempt = Integer.toString(getAttemptNumber());

                        String output = getDateTime() + " " + attempt + " " + word.replaceAll(" ", "") + " " + flyId + " false" +
                                " 3_b" + " ENGLISH" + " CVC_WORD_FROG";

                        DBAdapter.addUserData(new UserData("com.game.one", output));
                        edit = prefs.edit();
                        edit.putBoolean("HAS_DATA", true);
                        edit.commit();
                    }
                    flyId = "X";
                }

                stats.setText("\t" + getPoints() + "/" + getAttemptNumber()
                        + " ");

            }
        });

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

    // ########## UPDATES TEXTBOX THAT GOES WITH WORDTIMER ###########
    private void updateTimer()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (wordDuration - wordTimer.getElapsedTime() >= 0)
                {
                    long millis = wordDuration - wordTimer.getElapsedTime();

                    int seconds = (int) (millis / 1000) % 60;
                    int minutes = (int) ((millis / (1000 * 60)) % 60);

                    String ms = String.format("%02d:%02d", minutes, seconds);

                    timer.setText(ms);
                }

                if (wordDuration - wordTimer.getElapsedTime() == 0)
                {
                    if (level > 1)
                        decreaseStarPoints();

                    if (level == 1)
                    {
                        wordTimer.cancel();
                        wordTimer.start();
                        rateBar.setRating(0.0f);
                        resetTimeDisplay();
                    }
                    updateWordBoxText();
                }
            }
        });
    }

    public TextView getTimer()
    {
        return timer;
    }

    // ########## UPDATES SCORE FOR CORRECT SELECTION ###########
    private void updateScoreUp()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                setLevelUp();
                rateBar.setRating(0.0f);
                setStarColor();
                resetTimeDisplay();
                starPoints = 0;
                updateLevel();
            }
        });
    }

    // ########## UPDATES SCORE FOR INCORRECT SELECTION ###########
    private void updateScoreDown()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                setLevelDown();
                setStarColor();
                starPoints = 0;
                rateBar.setRating(0.0f);
                resetTimeDisplay();
            }
        });
    }

    private void resetTimeDisplay()
    {
        long millis = wordDuration;

        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);

        String ms = String.format("%02d:%02d", minutes, seconds);

        timer.setText(ms);
    }

    public void increasePoints(int p)
    {
        this.points += p;
    }

    private void increaseStarPoints()
    {
        wordTimer.cancel();

        mediaPlayer1 = MediaPlayer.create(getApplicationContext(), R.raw.applause);
        mediaPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                mediaPlayer1.stop();
                mediaPlayer1.reset();
                mediaPlayer1.release();
                mediaPlayer1 = null;
            }
        });

        mediaPlayer1.setVolume(Util.soundVolume, Util.soundVolume);

        mediaPlayer1.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                if (runAudio1)
                    mediaPlayer1.start();
            }
        });
        float rate = rateBar.getRating();
        float newRate = rate + 1.0f;
        rateBar.setRating(newRate);
    }


    private void updateStars()
    {
        if (rateBar.getRating() == 4.0f)
        {
            updateScoreUp();
            this.wordDuration = wordDuration - 5000;
        }
    }

    private void decreaseStarPoints()
    {
        wordTimer.cancel();
        float rate = rateBar.getRating();
        float newRate = rate - 1.0f;
        rateBar.setRating(newRate);

        if (newRate == 0.0f)
        {
            this.updateScoreDown();
            this.wordDuration = wordDuration + 5000;
        }
        wordTimer.start();
    }


    private void increaseFlySpeed()
    {
        view.getFlyA().speedXUp(2);
        view.getFlyA().speedYUp(2);
        view.getFlyE().speedXUp(2);
        view.getFlyE().speedYUp(2);
        view.getFlyI().speedXUp(2);
        view.getFlyI().speedYUp(2);
        view.getFlyO().speedXUp(2);
        view.getFlyO().speedYUp(2);
        view.getFlyU().speedXUp(2);
        view.getFlyU().speedYUp(2);
    }

    private void decreaseFlySpeed()
    {
        view.getFlyA().speedXDown(2);
        view.getFlyA().speedYDown(2);
        view.getFlyE().speedXDown(2);
        view.getFlyE().speedYDown(2);
        view.getFlyI().speedXDown(2);
        view.getFlyI().speedYDown(2);
        view.getFlyO().speedXDown(2);
        view.getFlyO().speedYDown(2);
        view.getFlyU().speedXDown(2);
        view.getFlyU().speedYDown(2);
    }

    private void setLevelUp()
    {
        if (level < 12)
            level++;

        if (level > 1 && level <= 10)
            levelNumber.setText(Integer.toString(level));

        increaseFlySpeed();
    }

    private void setLevelDown()
    {
        if (level > 1)
            level--;

        if ((level - 1) >= 0)
            levelNumber.setText(Integer.toString(level));

        decreaseFlySpeed();
    }

    private void setStarColor()
    {
        if (level == 1)
        {
            LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(
                    getResources().getColor(R.color.Blue),
                    Mode.SRC_ATOP);
        }

        if (level == 2)
        {
            LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(
                    getResources().getColor(R.color.Green_LimeGreen),
                    Mode.SRC_ATOP);
        }

        if (level == 3)
        {
            LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(
                    getResources().getColor(R.color.FireEngineRed),
                    Mode.SRC_ATOP);
        }

        if (level == 4)
        {
            LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(
                    getResources().getColor(R.color.Yellow),
                    Mode.SRC_ATOP);
        }

        if (level == 5)
        {
            LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(
                    getResources().getColor(R.color.NeonPink), Mode.SRC_ATOP);
        }

        if (level == 6)
        {
            LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(
                    getResources().getColor(R.color.PurpleMonster), Mode.SRC_ATOP);
        }

        if (level == 7)
        {
            LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(
                    getResources().getColor(R.color.orange), Mode.SRC_ATOP);
        }

        if (level == 8)
        {
            LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(
                    getResources().getColor(R.color.Blue_DodgerBlue), Mode.SRC_ATOP);
        }

        if (level == 9)
        {
            LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(
                    getResources().getColor(R.color.HotPink), Mode.SRC_ATOP);
        }

        if (level == 10)
        {
            LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(
                    getResources().getColor(R.color.AlienGreen), Mode.SRC_ATOP);
        }
    }

    // ########## SETS DATE FOR SAVED RESULTS ###########
    public static String getDateTime()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @SuppressWarnings("deprecation")
    public void setWord()
    {
        Random r = new Random();
        int index = r.nextInt(words.length);
        word = words[index];

        if (word.equals("B A G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bag));
        if (word.equals("B A R"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bar));
        if (word.equals("R A G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.rag));
        if (word.equals("C A B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.cab));
        if (word.equals("C A P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.cap));
        if (word.equals("C A R"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.car));
        if (word.equals("J A R"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.jar));
        if (word.equals("L A B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.lab));
        if (word.equals("M A D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.mad));
        if (word.equals("M A P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.map));
        if (word.equals("N A P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.nap));
        if (word.equals("P A D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.pad));
        if (word.equals("S A D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.sad));
        if (word.equals("T A G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.tag));
        if (word.equals("D A D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.dad));
        if (word.equals("B A T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bat));
        if (word.equals("C A T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.cat));
        if (word.equals("H A T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.hat));
        if (word.equals("M A T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.mat));
        if (word.equals("R A T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.rat));
        if (word.equals("H A M"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.ham));
        if (word.equals("J A M"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.jam));
        if (word.equals("R A M"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.ram));
        if (word.equals("C A N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.can));
        if (word.equals("F A N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.fan));
        if (word.equals("M A N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.man));
        if (word.equals("P A N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.pan));
        if (word.equals("V A N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.van));
        if (word.equals("B E D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bed));
        if (word.equals("R E D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.red));
        if (word.equals("W E D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.wed));
        if (word.equals("L E G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.leg));
        if (word.equals("H E N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.hen));
        if (word.equals("M E N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.men));
        if (word.equals("P E N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.pen));
        if (word.equals("T E N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.ten));
        if (word.equals("D E N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.den));
        if (word.equals("J E T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.jet));
        if (word.equals("V E T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.vet));
        if (word.equals("W E T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.wet));
        if (word.equals("K I D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.kid));
        if (word.equals("L I D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.lid));
        if (word.equals("P I G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.pig));
        if (word.equals("F I G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.fig));
        if (word.equals("W I G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.wig));
        if (word.equals("P I N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.pin));
        if (word.equals("F I N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.fin));
        if (word.equals("H I P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.hip));
        if (word.equals("L I P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.lip));
        if (word.equals("R I P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.rip));
        if (word.equals("T I P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.tip));
        if (word.equals("S I P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.sip));
        if (word.equals("B I T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bit));
        if (word.equals("F I T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.fit));
        if (word.equals("K I T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.kit));
        if (word.equals("S I T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.sit));
        if (word.equals("C O B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.cob));
        if (word.equals("J O B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.job));
        if (word.equals("S O B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.sob));
        if (word.equals("B O G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bog));
        if (word.equals("D O G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.dog));
        if (word.equals("F O G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.fog));
        if (word.equals("H O G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.hog));
        if (word.equals("L O G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.log));
        if (word.equals("J O G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.jog));
        if (word.equals("C O P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.cop));
        if (word.equals("H O P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.hop));
        if (word.equals("T O P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.top));
        if (word.equals("M O P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.mop));
        if (word.equals("P O P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.pop));
        if (word.equals("C O T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.cot));
        if (word.equals("D O T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.dot));
        if (word.equals("H O T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.hot));
        if (word.equals("P O T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.pot));
        if (word.equals("B O X"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.box));
        if (word.equals("F O X"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.fox));
        if (word.equals("C U B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.cub));
        if (word.equals("H U B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.hub));
        if (word.equals("T U B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.tub));
        if (word.equals("M U D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.mud));
        if (word.equals("B U D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bud));
        if (word.equals("B U G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bug));
        if (word.equals("H U G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.hug));
        if (word.equals("J U G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.jug));
        if (word.equals("M U G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.mug));
        if (word.equals("R U G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.rug));
        if (word.equals("C U P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.cup));
        if (word.equals("P U P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.pup));
        if (word.equals("H U T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.hut));
        if (word.equals("C U T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.cut));
        if (word.equals("T U G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.tug));
        if (word.equals("B A D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bad));
        if (word.equals("B E G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.beg));
        if (word.equals("B E T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bet));
        if (word.equals("B I B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bib));
        if (word.equals("B I G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.big));
        if (word.equals("B I N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bin));
        if (word.equals("B U N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bun));
        if (word.equals("B U S"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.bus));
        if (word.equals("C O G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.cog));
        if (word.equals("W I N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.win));
        if (word.equals("D A M"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.dam));
        if (word.equals("D I G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.dig));
        if (word.equals("D I M"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.dim));
        if (word.equals("D I P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.dip));
        if (word.equals("F E D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.fed));
        if (word.equals("F I B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.fib));
        if (word.equals("F U N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.fun));
        if (word.equals("G A G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.gag));
        if (word.equals("G A P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.gap));
        if (word.equals("G E T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.get));
        if (word.equals("G O B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.gob));
        if (word.equals("G U M"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.gum));
        if (word.equals("H I D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.hid));
        if (word.equals("H I M"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.him));
        if (word.equals("H U M"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.hum));
        if (word.equals("J I G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.jig));
        if (word.equals("K E G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.keg));
        if (word.equals("L A D"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.lad));
        if (word.equals("L E G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.leg));
        if (word.equals("L I T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.lit));
        if (word.equals("L O T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.lot));
        if (word.equals("M O B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.mob));
        if (word.equals("M O M"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.mom));
        if (word.equals("M U M"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.mum));
        if (word.equals("N A G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.nag));
        if (word.equals("N E T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.net));
        if (word.equals("N I P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.nip));
        if (word.equals("N U N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.nun));
        if (word.equals("N U T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.nut));
        if (word.equals("P E G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.peg));
        if (word.equals("P E T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.pet));
        if (word.equals("R A N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.ran));
        if (word.equals("R A P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.rap));
        if (word.equals("R I B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.rib));
        if (word.equals("R I G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.rig));
        if (word.equals("R I M"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.rim));
        if (word.equals("R O B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.rob));
        if (word.equals("R O T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.rot));
        if (word.equals("R U B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.rub));
        if (word.equals("R U T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.rut));
        if (word.equals("S A P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.sap));
        if (word.equals("S E T"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.set));
        if (word.equals("S U B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.sub));
        if (word.equals("S U M"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.sum));
        if (word.equals("T A B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.tab));
        if (word.equals("T A P"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.tap));
        if (word.equals("T I N"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.tin));
        if (word.equals("W A G"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.wag));
        if (word.equals("W E B"))
            b = createBitmap(this.getResources().getDrawable(R.drawable.web));

        d = new BitmapDrawable(b);
        image.setBackground(d);

        String[] letters = word.split(" ");
        letters[1] = "_";
        leftLetterBtn.setText(letters[0]);
        middleLetterBtn.setText(letters[1]);
        rightLetterBtn.setText(letters[2]);

        stats.setText("\t" + "0" + "/" + "0 ");
    }

    public void setAttemptNumber()
    {
        attemptNumber++;
        updateAttempts();
    }

    public int getAttemptNumber()
    {
        return attemptNumber;
    }

    public GameView getGameView()
    {
        return view;
    }

    public String getWord()
    {
        return word;
    }

    public void setFlyId(String i)
    {
        flyId = i;
    }

    public int getPoints()
    {
        return points;
    }

    public int getLevel()
    {
        return level;
    }

    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            view.Touched((int) event.getX(), (int) event.getY());
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        inGameMenu.show();
    }

    @Override
    protected void onPause()
    {
        if (mPlayerLevelOne != null && mPlayerLevelOne.isPlaying())
        {
            mPlayerLevelOne.pause();
            isResuming = true;
        }

        if (Util.musicPlayer != null)
        {
            Util.musicPlayer.pause();
        }
        if (mediaPlayer1 != null && mediaPlayer1.isPlaying())
        {
            mediaPlayer1.pause();
        }
        if (mediaPlayer1 == null)
            runAudio1 = false;

        if (mediaPlayer2 != null && mediaPlayer2.isPlaying())
        {
            mediaPlayer2.pause();
        }
        if (mediaPlayer2 == null)
            runAudio2 = false;

        if (mediaPlayer3 != null && mediaPlayer3.isPlaying())
        {
            mediaPlayer3.pause();
        }
        if (mediaPlayer3 == null)
            runAudio3 = false;

        if (mediaPlayer4 != null && mediaPlayer4.isPlaying())
        {
            mediaPlayer4.pause();
        }
        if (mediaPlayer4 == null)
            runAudio4 = false;

        wordTimer.pause();
        spriteTimer.pause();
        view.pause();

        super.onPause();
    }

    @Override
    protected void onResume()
    {
        view.resume();
        spriteTimer.start();
        wordTimer.start();
        runAudio1 = true;
        runAudio2 = true;
        runAudio3 = true;
        runAudio4 = true;

        if (Util.musicPlayer != null)
        {
            Util.musicPlayer.start();
        }

        if (mediaPlayer1 != null)
        {
            mediaPlayer1.start();
        }

        if (mediaPlayer2 != null)
        {
            mediaPlayer2.start();
        }

        if (mediaPlayer4 != null)
        {
            mediaPlayer4.start();
        }

        if (mPlayerLevelOne != null && isResuming == true)
        {
            mPlayerLevelOne.start();
        }

        super.onResume();
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

    // ########## UNLOADS IMAGE THAT REPRESENTS THE WORD ###########
    @SuppressWarnings("deprecation")
    public void unloadBackground()
    {
        if (d != null)
            image.setBackgroundDrawable(null);
        if (b != null)
        {
            b.recycle();
        }
        d = null;
        b = null;

        System.gc();
    }

    // ########## CHECKS TIME FOR SPRITE TIMER ###########
    private void checkTime()
    {
        // the duck appears 7 times, the fish appears 6 times.
        if (spriteTimer.getElapsedTime() == 5000)
        {
            view.getLizzardCrawlUp().loadBitmap();
            view.getLizzardCrawlUp().setVisible(true);
            // fish 1.
            view.getFish().loadBitmap();
            view.getFish().setVisible(true);
        }

        if (spriteTimer.getElapsedTime() == 5500)
        {
            view.getLizzard().loadBitmap();
            view.getLizzard().setVisible(true);
            view.getLizzardCrawlUp().setVisible(false);
            view.getLizzardCrawlUp().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 6500)
        {
            // fish 1.
            view.getFish().setVisible(false);
            view.getFish().unloadBitmap();
            view.getFish().setLoc1(false);
            view.getFish().setLoc2(true);
        }

        if (spriteTimer.getElapsedTime() == 15000)
        {
            view.getLizzardCrawlDown().loadBitmap();
            view.getLizzardCrawlDown().setVisible(true);
            view.getLizzard().setVisible(false);
            view.getLizzard().unloadBitmap();
            // duck 1.
            if (view.getDuckRL().getX() <= ((Util.PIXEL_WIDTH / 2) - (Util.PIXEL_WIDTH / 3))
                    || view.getDuckRL().getX() == (Util.PIXEL_WIDTH) - (Util.PIXEL_WIDTH / 5))
            {
                view.getDuckLR().loadBitmap();
                view.getDuckLR().setVisible(true);
            }
        }

        if (spriteTimer.getElapsedTime() == 15500)
        {
            view.getLizzardCrawlDown().setVisible(false);
            view.getLizzardCrawlDown().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 35000)
        {
            view.getLizzardCrawlUp().loadBitmap();
            view.getLizzardCrawlUp().setVisible(true);
            // fish 2.
            view.getFish().loadBitmap();
            view.getFish().setVisible(true);
        }

        if (spriteTimer.getElapsedTime() == 35500)
        {
            view.getLizzard().loadBitmap();
            view.getLizzard().setVisible(true);
            view.getLizzardCrawlUp().setVisible(false);
            view.getLizzardCrawlUp().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 36500)
        {
            // fish 2.
            view.getFish().setVisible(false);
            view.getFish().unloadBitmap();
            view.getFish().setLoc2(false);
            view.getFish().setLoc3(true);
        }

        if (spriteTimer.getElapsedTime() == 45000)
        {
            view.getLizzardCrawlDown().loadBitmap();
            view.getLizzardCrawlDown().setVisible(true);
            view.getLizzard().setVisible(false);
            view.getLizzard().unloadBitmap();
            // duck 2.
            if (view.getDuckLR().getX() >= (Util.PIXEL_WIDTH / 2)
                    + (Util.PIXEL_WIDTH / 4)
                    || view.getDuckLR().getX() == Util.PIXEL_WIDTH / 5)
            {
                view.getDuckRL().loadBitmap();
                view.getDuckRL().setVisible(true);
            }
        }

        if (spriteTimer.getElapsedTime() == 45500)
        {
            view.getLizzardCrawlDown().setVisible(false);
            view.getLizzardCrawlDown().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 65000)
        {
            view.getLizzardCrawlUp().loadBitmap();
            view.getLizzardCrawlUp().setVisible(true);
            // duck 3.
            if (view.getDuckRL().getX() <= ((Util.PIXEL_WIDTH / 2) - (Util.PIXEL_WIDTH / 3))
                    || view.getDuckRL().getX() == (Util.PIXEL_WIDTH) - (Util.PIXEL_WIDTH / 5))
            {
                view.resetDuckLR();
            }
        }

        if (spriteTimer.getElapsedTime() == 65500)
        {
            view.getLizzard().loadBitmap();
            view.getLizzard().setVisible(true);
            view.getLizzardCrawlUp().setVisible(false);
            view.getLizzardCrawlUp().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 75000)
        {
            view.getLizzardCrawlDown().loadBitmap();
            view.getLizzardCrawlDown().setVisible(true);
            view.getLizzard().setVisible(false);
            view.getLizzard().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 75500)
        {
            view.getLizzardCrawlDown().setVisible(false);
            view.getLizzardCrawlDown().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 95000)
        {
            view.getLizzardCrawlUp().loadBitmap();
            view.getLizzardCrawlUp().setVisible(true);
            // fish 3.
            view.getFish().loadBitmap();
            view.getFish().setVisible(true);
        }

        if (spriteTimer.getElapsedTime() == 95500)
        {
            view.getLizzard().loadBitmap();
            view.getLizzard().setVisible(true);
            view.getLizzardCrawlUp().setVisible(false);
            view.getLizzardCrawlUp().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 96500)
        {
            // fish 3.
            view.getFish().setVisible(false);
            view.getFish().unloadBitmap();
            view.getFish().setLoc3(false);
            view.getFish().setLoc1(true);
        }

        if (spriteTimer.getElapsedTime() == 115000)
        {
            view.getLizzardCrawlDown().loadBitmap();
            view.getLizzardCrawlDown().setVisible(true);
            view.getLizzard().setVisible(false);
            view.getLizzard().unloadBitmap();
            // duck 4.
            if (view.getDuckLR().getX() >= (Util.PIXEL_WIDTH / 2)
                    + (Util.PIXEL_WIDTH / 4)
                    || view.getDuckLR().getX() == Util.PIXEL_WIDTH / 5)
            {
                view.resetDuckRL();
            }
        }

        if (spriteTimer.getElapsedTime() == 115500)
        {
            view.getLizzardCrawlDown().setVisible(false);
            view.getLizzardCrawlDown().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 135000)
        {
            view.getLizzardCrawlUp().loadBitmap();
            view.getLizzardCrawlUp().setVisible(true);
        }

        if (spriteTimer.getElapsedTime() == 135500)
        {
            view.getLizzard().loadBitmap();
            view.getLizzard().setVisible(true);
            view.getLizzardCrawlUp().setVisible(false);
            view.getLizzardCrawlUp().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 145000)
        {
            view.getLizzardCrawlDown().loadBitmap();
            view.getLizzardCrawlDown().setVisible(true);
            view.getLizzard().setVisible(false);
            view.getLizzard().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 145500)
        {
            view.getLizzardCrawlDown().setVisible(false);
            view.getLizzardCrawlDown().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 165000)
        {
            view.getLizzardCrawlUp().loadBitmap();
            view.getLizzardCrawlUp().setVisible(true);
        }

        if (spriteTimer.getElapsedTime() == 165500)
        {
            view.getLizzard().loadBitmap();
            view.getLizzard().setVisible(true);
            view.getLizzardCrawlUp().setVisible(false);
            view.getLizzardCrawlUp().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 175000)
        {
            view.getLizzardCrawlDown().loadBitmap();
            view.getLizzardCrawlDown().setVisible(true);
            view.getLizzard().setVisible(false);
            view.getLizzard().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 175500)
        {
            view.getLizzardCrawlDown().setVisible(false);
            view.getLizzardCrawlDown().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 195000)
        {
            view.getLizzardCrawlUp().loadBitmap();
            view.getLizzardCrawlUp().setVisible(true);
            // fish 4.
            view.getFish().loadBitmap();
            view.getFish().setVisible(true);
        }

        if (spriteTimer.getElapsedTime() == 195500)
        {
            view.getLizzard().loadBitmap();
            view.getLizzard().setVisible(true);
            view.getLizzardCrawlUp().setVisible(false);
            view.getLizzardCrawlUp().unloadBitmap();
            // duck 5.
            if (view.getDuckRL().getX() <= ((Util.PIXEL_WIDTH / 2) - (Util.PIXEL_WIDTH / 3))
                    || view.getDuckRL().getX() == (Util.PIXEL_WIDTH) - (Util.PIXEL_WIDTH / 5))
            {
                view.resetDuckLR();
            }

        }

        if (spriteTimer.getElapsedTime() == 196500)
        {
            // fish 4.
            view.getFish().setVisible(false);
            view.getFish().unloadBitmap();
            view.getFish().setLoc1(false);
            view.getFish().setLoc2(true);
        }

        if (spriteTimer.getElapsedTime() == 205000)
        {
            view.getLizzardCrawlDown().loadBitmap();
            view.getLizzardCrawlDown().setVisible(true);
            view.getLizzard().setVisible(false);
            view.getLizzard().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 205500)
        {
            view.getLizzardCrawlDown().setVisible(false);
            view.getLizzardCrawlDown().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 225000)
        {
            view.getLizzardCrawlUp().loadBitmap();
            view.getLizzardCrawlUp().setVisible(true);
            // fish 5.
            view.getFish().loadBitmap();
            view.getFish().setVisible(true);
        }

        if (spriteTimer.getElapsedTime() == 225500)
        {
            view.getLizzard().loadBitmap();
            view.getLizzard().setVisible(true);
            view.getLizzardCrawlUp().setVisible(false);
            view.getLizzardCrawlUp().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 226500)
        {
            // fish 5.
            view.getFish().setVisible(false);
            view.getFish().unloadBitmap();
            view.getFish().setLoc2(false);
            view.getFish().setLoc3(true);
        }

        if (spriteTimer.getElapsedTime() == 235000)
        {
            view.getLizzardCrawlDown().loadBitmap();
            view.getLizzardCrawlDown().setVisible(true);
            view.getLizzard().setVisible(false);
            view.getLizzard().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 235500)
        {
            view.getLizzardCrawlDown().setVisible(false);
            view.getLizzardCrawlDown().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 245000)
        {
            view.getLizzardCrawlUp().loadBitmap();
            view.getLizzardCrawlUp().setVisible(true);
        }

        if (spriteTimer.getElapsedTime() == 245500)
        {
            view.getLizzard().loadBitmap();
            view.getLizzard().setVisible(true);
            view.getLizzardCrawlUp().setVisible(false);
            view.getLizzardCrawlUp().unloadBitmap();
            // duck 6.
            if (view.getDuckLR().getX() >= (Util.PIXEL_WIDTH / 2)
                    + (Util.PIXEL_WIDTH / 4)
                    || view.getDuckLR().getX() == Util.PIXEL_WIDTH / 5)
            {
                view.resetDuckRL();
            }
        }

        if (spriteTimer.getElapsedTime() == 255000)
        {
            view.getLizzardCrawlDown().loadBitmap();
            view.getLizzardCrawlDown().setVisible(true);
            view.getLizzard().setVisible(false);
            view.getLizzard().unloadBitmap();
            // fish 6.
            view.getFish().loadBitmap();
            view.getFish().setVisible(true);
        }

        if (spriteTimer.getElapsedTime() == 255500)
        {
            view.getLizzardCrawlDown().setVisible(false);
            view.getLizzardCrawlDown().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 256500)
        {
            // fish 6.
            view.getFish().setVisible(false);
            view.getFish().unloadBitmap();
            view.getFish().setLoc3(false);
            view.getFish().setLoc1(true);
        }

        if (spriteTimer.getElapsedTime() == 265000)
        {
            view.getLizzardCrawlUp().loadBitmap();
            view.getLizzardCrawlUp().setVisible(true);
        }

        if (spriteTimer.getElapsedTime() == 265500)
        {
            view.getLizzard().loadBitmap();
            view.getLizzard().setVisible(true);
            view.getLizzardCrawlUp().setVisible(false);
            view.getLizzardCrawlUp().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 275000)
        {
            view.getLizzardCrawlDown().loadBitmap();
            view.getLizzardCrawlDown().setVisible(true);
            view.getLizzard().setVisible(false);
            view.getLizzard().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 275500)
        {
            view.getLizzardCrawlDown().setVisible(false);
            view.getLizzardCrawlDown().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 295000)
        {
            view.getLizzardCrawlUp().loadBitmap();
            view.getLizzardCrawlUp().setVisible(true);
        }

        if (spriteTimer.getElapsedTime() == 295500)
        {
            view.getLizzard().loadBitmap();
            view.getLizzard().setVisible(true);
            view.getLizzardCrawlUp().setVisible(false);
            view.getLizzardCrawlUp().unloadBitmap();
            // duck 7.
            if (view.getDuckRL().getX() <= ((Util.PIXEL_WIDTH / 2) - (Util.PIXEL_WIDTH / 3))
                    || view.getDuckRL().getX() == (Util.PIXEL_WIDTH) - (Util.PIXEL_WIDTH / 5))
            {
                view.resetDuckLR();
            }
        }

        if (spriteTimer.getElapsedTime() == 305000)
        {
            view.getLizzardCrawlDown().loadBitmap();
            view.getLizzardCrawlDown().setVisible(true);
            view.getLizzard().setVisible(false);
            view.getLizzard().unloadBitmap();
        }

        if (spriteTimer.getElapsedTime() == 305500)
        {
            view.getLizzardCrawlDown().setVisible(false);
            view.getLizzardCrawlDown().unloadBitmap();
        }
    }

    public void runWordSound()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                int resID = getApplicationContext().getResources().getIdentifier(
                        word.toLowerCase().replaceAll(" ", ""), "raw",
                        getApplicationContext().getPackageName());

                mediaPlayer2 = MediaPlayer.create(getApplicationContext(), resID);

                mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    public void onCompletion(MediaPlayer mp)
                    {
                        mediaPlayer2.stop();
                        mediaPlayer2.reset();
                        mediaPlayer2.release();
                        mediaPlayer2 = null;
                    }
                });

                try
                {
                    mediaPlayer2.setVolume(Util.soundVolume, Util.soundVolume);
                } catch (IllegalStateException e)
                {
                }
                mediaPlayer2.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                {
                    @Override
                    public void onPrepared(MediaPlayer mp)
                    {
                        if (runAudio2)
                            mediaPlayer2.start();
                    }
                });
            }
        });
    }

    public void runSpitSound()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                int resID = getApplicationContext().getResources().getIdentifier("cartoon_spit",
                        "raw", getApplicationContext().getPackageName());

                mediaPlayer3 = MediaPlayer.create(getApplicationContext(), resID);

                mediaPlayer3.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    public void onCompletion(MediaPlayer mp)
                    {
                        mediaPlayer3.stop();
                        mediaPlayer3.reset();
                        mediaPlayer3.release();
                        mediaPlayer3 = null;
                    }
                });

                try
                {
                    mediaPlayer3.setVolume(Util.soundVolume, Util.soundVolume);
                } catch (IllegalStateException e)
                {
                }
                mediaPlayer3.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                {
                    @Override
                    public void onPrepared(MediaPlayer mp)
                    {
                        if (runAudio3)
                            mediaPlayer3.start();
                    }
                });
            }
        });
    }
}


