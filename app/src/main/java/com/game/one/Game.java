package com.game.one;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.game.one.model.UserData;
import com.game.one.persistence.DBAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

@SuppressLint(
	{"ClickableViewAccessibility", "DefaultLocale"})
public class Game extends Activity implements OnTouchListener
{
	public static Game theGame;
	private MediaPlayer mediaPlayer;
	volatile private long wordDuration = 60000;
	private int level = 0;
	volatile private int starPoints = 0;
	private boolean OK = true;
	private GameView view;
	private String flyId = "X";
	private TextView stats;
	private TextView levelLabel;
	private TextView levelNumber;
	private TextView timer;
	private ImageView image;
    private Context sharedContext = null;
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
				"G E T", "G O B", "G U M", "G U N", "H I D", "H I M", "W A G",
				"H U M", "J I G", "K E G", "L A D", "L E G", "L I T", "L O T",
				"M O B", "M O M", "M U M", "N A G", "N E T", "N I P", "N U N",
				"N U T", "P E G", "P E T", "R A N", "R A P", "R I B", "R I G",
				"R I M", "R O B", "R O T", "R U B", "R U T", "S A G", "S A P",
				"S E T", "S U B", "S U M", "T A B", "T A P", "T I N"};

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

	TimerExec wordTimer = new TimerExec(1000, -1, new TimerExecTask()
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

	TimerExec mediaTimer = new TimerExec(1000, -1, new TimerExecTask()
	{
		@Override
		public void onTick()
		{
			if(mediaTimer.getElapsedTime() >= mediaPlayer.getDuration())
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
			// cancels the timer.
			leftLetterBtn.setClickable(true);
			middleLetterBtn.setClickable(true);
			rightLetterBtn.setClickable(true);
			mediaTimer.cancel();
		}
	});

	TimerExec starTimer = new TimerExec(1000, -1, new TimerExecTask()
	{
		@Override
		public void onTick()
		{
			if(mediaTimer.getElapsedTime() >= 1000)
			{
				onFinish();
			}
		}

		@Override
		public void onFinish()
		{
			updateScoreUp();
			starTimer.cancel();
		}
	});

	// ############## ONCREATE ###############
	@Override
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		theGame = this;

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

        if(isPackageInstalled())
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
        }
		// System.out.println("The game has " + words.length + " words.");
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
						theGame.view.gameOver();
						inGameMenu.dismiss();
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
						startActivity(new Intent("com.game.one.Config"));
						finish();
					}
				});
	}

	// ######### SETS UP THE GAME SCORE SECTION AT TOP OF SCREEN ########
	private void setLayouts()
	{
		LinearLayout mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setGravity(Gravity.TOP);
		mainLayout.setBackgroundColor(Color.BLUE);

		LinearLayout topLayout = new LinearLayout(this);

		LinearLayout wordLayout = new LinearLayout(this);

		LinearLayout levelLayout = new LinearLayout(this);

		LinearLayout timerLayout = new LinearLayout(this);

		LinearLayout starLayout = new LinearLayout(this);

		wordLayout.setGravity(Gravity.CENTER_HORIZONTAL
				| Gravity.CENTER_VERTICAL);
		wordLayout.setBackgroundColor(Color.YELLOW);
		wordLayout.setPadding(20, 0, 20, 0);
		LayoutParams wordParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
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
		leftLetterBtn.setTextSize(getResources().getDimension(
				R.dimen.textsize45sp));
		leftLetterBtn.setOnClickListener(new OnClickListener()
		{
			@SuppressLint("DefaultLocale")
			public void onClick(View v)
			{
				String w = word.toLowerCase();
				String[] letters = w.split(" ");

				int resID = getApplicationContext().getResources()
						.getIdentifier(letters[0], "raw",
								getApplicationContext().getPackageName());
				// creates a mediaPlayer instance with the correct mp3 file.
				mediaPlayer = MediaPlayer
						.create(getApplicationContext(), resID);
				try
				{
					// sets volume of mediaPlayer.
					mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
					mediaPlayer.start();
					mediaTimer.start();
				}
				catch(IllegalStateException e)
				{
				}

			}
		});

		middleLetterBtn = new Button(this);
		middleLetterBtn.setTextColor(Color.RED);
		middleLetterBtn.setPadding(0, 0, 0, 0);
		middleLetterBtn.setTypeface(Typeface.DEFAULT_BOLD);
		middleLetterBtn.setBackgroundColor(Color.YELLOW);
		middleLetterBtn.setTextSize(getResources().getDimension(
				R.dimen.textsize45sp));
		middleLetterBtn.setOnClickListener(new OnClickListener()
		{
			@SuppressLint("DefaultLocale")
			public void onClick(View v)
			{
				String w = word.toLowerCase();
				String[] letters = w.split(" ");

				int resID = getApplicationContext().getResources()
						.getIdentifier(letters[1], "raw",
								getApplicationContext().getPackageName());
				// creates a mediaPlayer instance with the correct mp3 file.
				mediaPlayer = MediaPlayer
						.create(getApplicationContext(), resID);
				try
				{
					// sets volume of mediaPlayer.
					mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
					mediaPlayer.start();
					mediaTimer.start();
				}
				catch(IllegalStateException e)
				{
				}
			}
		});

		rightLetterBtn = new Button(this);
		rightLetterBtn.setTextColor(Color.RED);
		rightLetterBtn.setPadding(20, 0, 20, 0);
		rightLetterBtn.setTypeface(Typeface.DEFAULT_BOLD);
		rightLetterBtn.setBackgroundColor(Color.YELLOW);
		rightLetterBtn.setTextSize(getResources().getDimension(
				R.dimen.textsize45sp));
		rightLetterBtn.setOnClickListener(new OnClickListener()
		{
			@SuppressLint("DefaultLocale")
			public void onClick(View v)
			{
				String w = word.toLowerCase();
				String[] letters = w.split(" ");

				int resID = getApplicationContext().getResources()
						.getIdentifier(letters[2], "raw",
								getApplicationContext().getPackageName());
				// creates a mediaPlayer instance with the correct mp3 file.
				mediaPlayer = MediaPlayer
						.create(getApplicationContext(), resID);
				try
				{
					// sets volume of mediaPlayer.
					mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
					mediaPlayer.start();
					mediaTimer.start();
				}
				catch(IllegalStateException e)
				{
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
				onPause();
				inGameMenu.show();
			}
		});

		levelLayout.setOrientation(LinearLayout.VERTICAL);
		levelLayout.setPadding(30, 0, 30, 0);

		levelLabel = new TextView(this);
		levelLabel.setTextSize(getResources()
				.getDimension(R.dimen.textsize12sp));
		levelLabel.setTypeface(Typeface.DEFAULT_BOLD);
		levelLabel.setText("Level:");
		levelLabel.setTextColor(Color.WHITE);
		levelLabel.setBackgroundColor(Color.TRANSPARENT);
		levelLabel.setGravity(Gravity.CENTER_HORIZONTAL
				| Gravity.CENTER_VERTICAL);

		levelNumber = new TextView(this);
		levelNumber.setTextSize(getResources().getDimension(
				R.dimen.textsize30sp));
		levelNumber.setGravity(Gravity.CENTER_HORIZONTAL
				| Gravity.CENTER_VERTICAL);
		levelNumber.setTextColor(Color.WHITE);
		levelNumber.setBackgroundColor(Color.TRANSPARENT);
		levelNumber.setText(Integer.toString(level));

		levelLayout.addView(levelLabel);
		levelLayout.addView(levelNumber);

		timerLayout.setBackgroundColor(Color.TRANSPARENT);
		timerLayout.setPadding(10, 10, 10, 10);
		LayoutParams timerParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		timerParams.setMargins(35, 0, 0, 0);
		timerLayout.setLayoutParams(timerParams);

		timer = new TextView(this);
		timer.setTextSize(getResources().getDimension(R.dimen.textsize35sp));
		timer.setBackgroundColor(Color.TRANSPARENT);
		timer.setTextColor(Color.WHITE);
		timer.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

		this.resetTimeDisplay();

		timerLayout.addView(timer);

		starLayout.setPadding(10, 10, 10, 10);
		starLayout.setBackgroundColor(Color.TRANSPARENT);
		starLayout.setGravity(Gravity.CENTER_HORIZONTAL
				| Gravity.CENTER_VERTICAL);
		LayoutParams starParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		starParams.setMargins(10, 10, 10, 10);
		starLayout.setLayoutParams(starParams);

		rateBar = new RatingBar(this, null,
				android.R.attr.ratingBarStyleIndicator);
		rateBar.setStepSize((float) 1.0);
		rateBar.setMax(4);
		rateBar.setNumStars(4);
		rateBar.setRating(0.0f);
		rateBar.setFocusable(false);
		rateBar.setBackgroundColor(Color.TRANSPARENT);

		rateBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener()
		{
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser)
			{
				if(rateBar.getRating() >= 4)
				{
					Toast.makeText(
							getApplicationContext(),
							"Great Job! You have reached level "
									+ Integer.toString(level + 1),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		setStarColor();

		stats = new TextView(this);
		stats.setTextSize(getResources().getDimension(R.dimen.textsize35sp));
		stats.setTextColor(Color.WHITE);

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

	// ########## PRESENTS NEXT WORD TO PLAYER ###########
	public void updateWordBoxText()
	{
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				if(b != null && d != null)
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
				if(word.contains(flyId) == true)
				{
					increasePoints(1);
					increaseStarPoints();
					String[] letters = word.split(" ");
					leftLetterBtn.setText(letters[0]);
					middleLetterBtn.setText(letters[1]);
					rightLetterBtn.setText(letters[2]);


                    if(isPackageInstalled())
                    {
                        String attempt = Integer.toString(getAttemptNumber());

                        String output = attempt + " " + word.replaceAll(" ", "") + " " + flyId + " true";

                        DBAdapter.addUserData(new UserData("com.game.one", output));
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
				if(word.contains(flyId) == false && flyId.equals("X") == false)
				{
					decreaseStarPoints();

                    if(isPackageInstalled())
                    {
                        String attempt = Integer.toString(getAttemptNumber());

                        String output = attempt + " " + word.replaceAll(" ", "") + " " + flyId + " false";

                        DBAdapter.addUserData(new UserData("com.game.one", output));
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
        }
        catch (PackageManager.NameNotFoundException e)
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
				if(wordDuration - wordTimer.getElapsedTime() >= 0)
				{
					long millis = wordDuration - wordTimer.getElapsedTime();

					int seconds = (int) (millis / 1000) % 60;
					int minutes = (int) ((millis / (1000 * 60)) % 60);

					String ms = String.format("%02d:%02d", minutes, seconds);

					timer.setText(ms);
				}

				if(wordDuration - wordTimer.getElapsedTime() == 0)
				{
					if(level > 1)
						decreaseStarPoints();

					if(level == 1)
					{
						wordTimer.cancel();
						wordTimer.start();
						resetTimeDisplay();
					}
					updateWordBoxText();
				}
			}
		});
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
		long millis = 60000;

		int seconds = (int) (millis / 1000) % 60;
		int minutes = (int) ((millis / (1000 * 60)) % 60);

		String ms = String.format("%02d:%02d", minutes, seconds);

		timer.setText(ms);
	}

	public void increasePoints(int p)
	{
		this.points += p;
	}

	// ##### INCREASE STAR POINTS #####
	private void increaseStarPoints()
	{
		float remainingTime = 60000 - wordTimer.getElapsedTime();
		float usedTime = 60000 - remainingTime;
		float percentUsed = (usedTime / 60000) * 100;
		wordTimer.cancel();

		if(percentUsed <= 10 && OK == true)
		{
			this.starPoints = this.starPoints + 90;

			leftLetterBtn.setClickable(false);
			middleLetterBtn.setClickable(false);
			rightLetterBtn.setClickable(false);

			mediaPlayer = MediaPlayer.create(this, R.raw.burp);
			mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
			mediaPlayer.start();
			mediaTimer.start();

			if(starPoints / 30 >= 4)
			{
				rateBar.setRating(4.0f);
				starTimer.start();
			}

			if(starPoints / 30 < 4 && starPoints / 30 > 0)
			{
				float rate = rateBar.getRating();
				int rateIncrease = starPoints / 30;
				float newRate = rate + rateIncrease;

				if(newRate > 4)
				{
					rateBar.setRating(4);
					starTimer.start();
				}

				if(newRate < 4)
				{
					rateBar.setRating(newRate);
					resetTimeDisplay();
				}
			}

			if(starPoints / 30 < 1)
			{
				resetTimeDisplay();
			}

			OK = false;
		}

		if(percentUsed <= 20 && OK == true)
		{
			this.starPoints = this.starPoints + 80;

			leftLetterBtn.setClickable(false);
			middleLetterBtn.setClickable(false);
			rightLetterBtn.setClickable(false);

			mediaPlayer = MediaPlayer.create(this, R.raw.burp);
			mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
			mediaPlayer.start();
			mediaTimer.start();

			if(starPoints / 30 >= 4)
			{
				rateBar.setRating(4.0f);
				starTimer.start();
			}

			if(starPoints / 30 < 4 && starPoints / 30 > 0)
			{
				float rate = rateBar.getRating();
				int rateIncrease = starPoints / 30;
				float newRate = rate + rateIncrease;

				/*
				 * System.out.println("star points " + starPoints);
				 * System.out.println("rate " + rate);
				 * System.out.println("rate increase " + rateIncrease);
				 * System.out.println("new rate " + newRate);
				 */

				if(newRate > 4)
				{
					rateBar.setRating(4);
					starTimer.start();
				}

				if(newRate < 4)
				{
					rateBar.setRating(newRate);
					resetTimeDisplay();
				}
			}

			if(starPoints / 30 < 1)
			{
				resetTimeDisplay();
			}

			OK = false;
		}

		if(percentUsed <= 30 && OK == true)
		{
			this.starPoints = this.starPoints + 70;

			leftLetterBtn.setClickable(false);
			middleLetterBtn.setClickable(false);
			rightLetterBtn.setClickable(false);

			mediaPlayer = MediaPlayer.create(this, R.raw.burp);
			mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
			mediaPlayer.start();
			mediaTimer.start();

			if(starPoints / 30 >= 4)
			{
				rateBar.setRating(4.0f);
				starTimer.start();
			}

			if(starPoints / 30 < 4 && starPoints / 30 > 0)
			{
				float rate = rateBar.getRating();
				int rateIncrease = starPoints / 30;
				float newRate = rate + rateIncrease;

				if(newRate > 4)
				{
					rateBar.setRating(4);
					starTimer.start();
				}

				if(newRate < 4)
				{
					rateBar.setRating(newRate);
					resetTimeDisplay();
				}
			}

			if(starPoints / 30 < 1)
			{
				resetTimeDisplay();
			}

			OK = false;
		}

		if(percentUsed <= 40 && OK == true)
		{
			this.starPoints = this.starPoints + 60;

			leftLetterBtn.setClickable(false);
			middleLetterBtn.setClickable(false);
			rightLetterBtn.setClickable(false);

			mediaPlayer = MediaPlayer.create(this, R.raw.burp);
			mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
			mediaPlayer.start();
			mediaTimer.start();

			if(starPoints / 30 >= 4)
			{
				rateBar.setRating(4.0f);
				starTimer.start();
			}

			if(starPoints / 30 < 4 && starPoints / 30 > 0)
			{
				float rate = rateBar.getRating();
				int rateIncrease = starPoints / 30;
				float newRate = rate + rateIncrease;

				if(newRate > 4)
				{
					rateBar.setRating(4);
					starTimer.start();
				}

				if(newRate < 4)
				{
					rateBar.setRating(newRate);
					resetTimeDisplay();
				}
			}

			if(starPoints / 30 < 1)
			{
				resetTimeDisplay();
			}

			OK = false;
		}

		if(percentUsed <= 50 && OK == true)
		{
			this.starPoints = this.starPoints + 50;

			leftLetterBtn.setClickable(false);
			middleLetterBtn.setClickable(false);
			rightLetterBtn.setClickable(false);

			mediaPlayer = MediaPlayer.create(this, R.raw.burp);
			mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
			mediaPlayer.start();
			mediaTimer.start();

			if(starPoints / 30 >= 4)
			{
				rateBar.setRating(4.0f);
				starTimer.start();
			}

			if(starPoints / 30 < 4 && starPoints / 30 > 0)
			{
				float rate = rateBar.getRating();
				int rateIncrease = starPoints / 30;
				float newRate = rate + rateIncrease;

				if(newRate > 4)
				{
					rateBar.setRating(4);
					starTimer.start();
				}

				if(newRate < 4)
				{
					rateBar.setRating(newRate);
					resetTimeDisplay();
				}
			}

			if(starPoints / 30 < 1)
			{
				resetTimeDisplay();
			}

			OK = false;
		}

		if(percentUsed <= 60 && OK == true)
		{
			this.starPoints = this.starPoints + 40;

			leftLetterBtn.setClickable(false);
			middleLetterBtn.setClickable(false);
			rightLetterBtn.setClickable(false);

			mediaPlayer = MediaPlayer.create(this, R.raw.burp);
			mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
			mediaPlayer.start();
			mediaTimer.start();

			if(starPoints / 30 >= 4)
			{
				rateBar.setRating(4.0f);
				starTimer.start();
			}

			if(starPoints / 30 < 4 && starPoints / 30 > 0)
			{
				float rate = rateBar.getRating();
				int rateIncrease = starPoints / 30;
				float newRate = rate + rateIncrease;

				if(newRate > 4)
				{
					rateBar.setRating(4);
					starTimer.start();
				}

				if(newRate < 4)
				{
					rateBar.setRating(newRate);
					resetTimeDisplay();
				}
			}

			if(starPoints / 30 < 1)
			{
				resetTimeDisplay();
			}

			OK = false;
		}

		if(percentUsed <= 70 && OK == true)
		{
			this.starPoints = this.starPoints + 10;

			leftLetterBtn.setClickable(false);
			middleLetterBtn.setClickable(false);
			rightLetterBtn.setClickable(false);

			mediaPlayer = MediaPlayer.create(this, R.raw.burp);
			mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
			mediaPlayer.start();
			mediaTimer.start();

			if(starPoints / 30 >= 4)
			{
				rateBar.setRating(4.0f);
				starTimer.start();
			}

			if(starPoints / 30 < 4 && starPoints / 30 > 0)
			{
				float rate = rateBar.getRating();
				int rateIncrease = starPoints / 30;
				float newRate = rate + rateIncrease;

				if(newRate > 4)
				{
					rateBar.setRating(4);
					starTimer.start();
				}

				if(newRate < 4)
				{
					rateBar.setRating(newRate);
					resetTimeDisplay();
				}
			}

			if(starPoints / 30 < 1)
			{
				resetTimeDisplay();
			}

			OK = false;
		}

		if(percentUsed <= 80 && OK == true)
		{
			this.starPoints = this.starPoints + 10;

			leftLetterBtn.setClickable(false);
			middleLetterBtn.setClickable(false);
			rightLetterBtn.setClickable(false);

			mediaPlayer = MediaPlayer.create(this, R.raw.burp);
			mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
			mediaPlayer.start();
			mediaTimer.start();

			if(starPoints / 30 >= 4)
			{
				rateBar.setRating(4.0f);
				starTimer.start();
			}

			if(starPoints / 30 < 4 && starPoints / 30 > 0)
			{
				float rate = rateBar.getRating();
				int rateIncrease = starPoints / 30;
				float newRate = rate + rateIncrease;

				if(newRate > 4)
				{
					rateBar.setRating(4);
					starTimer.start();
				}

				if(newRate < 4)
				{
					rateBar.setRating(newRate);
					resetTimeDisplay();
				}
			}

			if(starPoints / 30 < 1)
			{
				resetTimeDisplay();
			}

			OK = false;
		}

		if(percentUsed <= 90 && OK == true)
		{
			this.starPoints = this.starPoints + 10;

			leftLetterBtn.setClickable(false);
			middleLetterBtn.setClickable(false);
			rightLetterBtn.setClickable(false);

			mediaPlayer = MediaPlayer.create(this, R.raw.burp);
			mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
			mediaPlayer.start();
			mediaTimer.start();

			if(starPoints / 30 >= 4)
			{
				rateBar.setRating(4.0f);
				starTimer.start();
			}

			if(starPoints / 30 < 4 && starPoints / 30 > 0)
			{
				float rate = rateBar.getRating();
				int rateIncrease = starPoints / 30;
				float newRate = rate + rateIncrease;

				if(newRate > 4)
				{
					rateBar.setRating(4);
					starTimer.start();
				}

				if(newRate < 4)
				{
					rateBar.setRating(newRate);
					resetTimeDisplay();
				}
			}

			if(starPoints / 30 < 1)
			{
				resetTimeDisplay();
			}

			OK = false;
		}
		OK = true;
	}

	// ##### DECREASE STAR POINTS #####
	private void decreaseStarPoints()
	{
		float remainingTime = 60000 - wordTimer.getElapsedTime();
		float usedTime = 60000 - remainingTime;
		float percentUsed = (usedTime / 60000) * 100;
		wordTimer.cancel();

		if(percentUsed <= 10 || percentUsed <= 20 && OK == true)
		{
			float currentRating = rateBar.getRating();

			if(currentRating - 3 <= 0)
			{
				updateScoreDown();
				wordTimer.start();
			}

			if(currentRating - 3 >= 1)
			{
				rateBar.setRating(0.0f);
				rateBar.setRating(currentRating - 3);
				resetTimeDisplay();
				wordTimer.start();
			}

			OK = false;
		}

		if(percentUsed <= 30 || percentUsed <= 40 && OK == true)
		{
			float currentRating = rateBar.getRating();

			if(currentRating - 2 <= 0)
			{
				updateScoreDown();
				wordTimer.start();
			}

			if(currentRating - 2 >= 1)
			{
				rateBar.setRating(0.0f);
				rateBar.setRating(currentRating - 2);
				resetTimeDisplay();
				wordTimer.start();
			}

			OK = false;
		}

		if(percentUsed <= 50 || percentUsed <= 60 || percentUsed <= 70
				|| percentUsed <= 80 || percentUsed <= 90 && OK == true)
		{
			float currentRating = rateBar.getRating();

			if(currentRating - 1 <= 0)
			{
				updateScoreDown();
				wordTimer.start();
			}

			if(currentRating - 1 >= 1)
			{
				rateBar.setRating(0.0f);
				rateBar.setRating(currentRating - 1);
				resetTimeDisplay();
				wordTimer.start();
			}

			OK = false;
		}
		OK = true;
	}

	private void increaseFlySpeed()
	{
		view.getFlyA().speedXUp(level);
		view.getFlyA().speedYUp(level);
		view.getFlyE().speedXUp(level);
		view.getFlyE().speedYUp(level);
		view.getFlyI().speedXUp(level);
		view.getFlyI().speedYUp(level);
		view.getFlyO().speedXUp(level);
		view.getFlyO().speedYUp(level);
		view.getFlyU().speedXUp(level);
		view.getFlyU().speedYUp(level);
	}

	private void decreaseFlySpeed()
	{
		view.getFlyA().speedXDown(level);
		view.getFlyA().speedYDown(level);
		view.getFlyE().speedXDown(level);
		view.getFlyE().speedYDown(level);
		view.getFlyI().speedXDown(level);
		view.getFlyI().speedYDown(level);
		view.getFlyO().speedXDown(level);
		view.getFlyO().speedYDown(level);
		view.getFlyU().speedXDown(level);
		view.getFlyU().speedYDown(level);
	}

	private void setLevelUp()
	{
		if(level < 6)
			level++;

		if(level > 1 && level <= 6)
			levelNumber.setText(Integer.toString(level));

		increaseFlySpeed();
	}

	private void setLevelDown()
	{
		if(level > 1)
			level--;

		if((level - 1) >= 0)
			levelNumber.setText(Integer.toString(level));

		decreaseFlySpeed();
	}

	private void setStarColor()
	{
		if(level == 1)
		{
			LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
			stars.getDrawable(2).setColorFilter(
					getResources().getColor(R.color.HummingbirdGreen),
					Mode.SRC_ATOP);
		}

		if(level == 2)
		{
			LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
			stars.getDrawable(2).setColorFilter(
					getResources().getColor(R.color.ConstructionConeOrange),
					Mode.SRC_ATOP);
		}

		if(level == 3)
		{
			LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
			stars.getDrawable(2).setColorFilter(
					getResources().getColor(R.color.FireEngineRed),
					Mode.SRC_ATOP);
		}

		if(level == 4)
		{
			LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
			stars.getDrawable(2).setColorFilter(
					getResources().getColor(R.color.PurpleMonster),
					Mode.SRC_ATOP);
		}

		if(level == 5)
		{
			LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
			stars.getDrawable(2).setColorFilter(
					getResources().getColor(R.color.NeonPink), Mode.SRC_ATOP);
		}

		if(level == 6)
		{
			LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
			stars.getDrawable(2).setColorFilter(
					getResources().getColor(R.color.BrightGold), Mode.SRC_ATOP);
		}
	}

	// ########## SETS DATE FOR SAVED RESULTS ###########
    public static String getDateTime()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

	@SuppressWarnings("deprecation")
	public void setWord()
	{
		Random r = new Random();
		int index = r.nextInt(words.length);
		word = words[index];

		if(word.equals("B A G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bag));
		if(word.equals("B A R"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bar));
		if(word.equals("R A G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.rag));
		if(word.equals("C A B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.cab));
		if(word.equals("C A P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.cap));
		if(word.equals("C A R"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.car));
		if(word.equals("J A R"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.jar));
		if(word.equals("L A B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.lab));
		if(word.equals("M A D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.mad));
		if(word.equals("M A P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.map));
		if(word.equals("N A P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.nap));
		if(word.equals("P A D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.pad));
		if(word.equals("S A D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.sad));
		if(word.equals("T A G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.tag));
		if(word.equals("D A D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.dad));
		if(word.equals("B A T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bat));
		if(word.equals("C A T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.cat));
		if(word.equals("H A T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.hat));
		if(word.equals("M A T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.mat));
		if(word.equals("R A T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.rat));
		if(word.equals("H A M"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.ham));
		if(word.equals("J A M"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.jam));
		if(word.equals("R A M"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.ram));
		if(word.equals("C A N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.can));
		if(word.equals("F A N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.fan));
		if(word.equals("M A N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.man));
		if(word.equals("P A N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.pan));
		if(word.equals("V A N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.van));
		if(word.equals("B E D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bed));
		if(word.equals("R E D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.red));
		if(word.equals("W E D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.wed));
		if(word.equals("L E G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.leg));
		if(word.equals("H E N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.hen));
		if(word.equals("M E N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.men));
		if(word.equals("P E N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.pen));
		if(word.equals("T E N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.ten));
		if(word.equals("D E N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.den));
		if(word.equals("J E T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.jet));
		if(word.equals("V E T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.vet));
		if(word.equals("W E T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.wet));
		if(word.equals("K I D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.kid));
		if(word.equals("L I D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.lid));
		if(word.equals("P I G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.pig));
		if(word.equals("F I G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.fig));
		if(word.equals("W I G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.wig));
		if(word.equals("P I N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.pin));
		if(word.equals("F I N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.fin));
		if(word.equals("H I P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.hip));
		if(word.equals("L I P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.lip));
		if(word.equals("R I P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.rip));
		if(word.equals("T I P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.tip));
		if(word.equals("S I P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.sip));
		if(word.equals("B I T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bit));
		if(word.equals("F I T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.fit));
		if(word.equals("K I T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.kit));
		if(word.equals("S I T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.sit));
		if(word.equals("C O B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.cob));
		if(word.equals("J O B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.job));
		if(word.equals("S O B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.sob));
		if(word.equals("B O G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bog));
		if(word.equals("D O G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.dog));
		if(word.equals("F O G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.fog));
		if(word.equals("H O G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.hog));
		if(word.equals("L O G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.log));
		if(word.equals("J O G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.jog));
		if(word.equals("C O P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.cop));
		if(word.equals("H O P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.hop));
		if(word.equals("T O P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.top));
		if(word.equals("M O P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.mop));
		if(word.equals("P O P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.pop));
		if(word.equals("C O T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.cot));
		if(word.equals("D O T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.dot));
		if(word.equals("H O T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.hot));
		if(word.equals("P O T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.pot));
		if(word.equals("B O X"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.box));
		if(word.equals("F O X"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.fox));
		if(word.equals("C U B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.cub));
		if(word.equals("H U B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.hub));
		if(word.equals("T U B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.tub));
		if(word.equals("M U D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.mud));
		if(word.equals("B U D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bud));
		if(word.equals("B U G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bug));
		if(word.equals("H U G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.hug));
		if(word.equals("J U G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.jug));
		if(word.equals("M U G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.mug));
		if(word.equals("R U G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.rug));
		if(word.equals("C U P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.cup));
		if(word.equals("P U P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.pup));
		if(word.equals("H U T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.hut));
		if(word.equals("C U T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.cut));
		if(word.equals("T U G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.tug));
		if(word.equals("B A D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bad));
		if(word.equals("B E G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.beg));
		if(word.equals("B E T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bet));
		if(word.equals("B I B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bib));
		if(word.equals("B I G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.big));
		if(word.equals("B I N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bin));
		if(word.equals("B U N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bun));
		if(word.equals("B U S"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.bus));
		if(word.equals("C O G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.cog));
		if(word.equals("W I N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.win));
		if(word.equals("D A M"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.dam));
		if(word.equals("D I G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.dig));
		if(word.equals("D I M"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.dim));
		if(word.equals("D I P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.dip));
		if(word.equals("F E D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.fed));
		if(word.equals("F I B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.fib));
		if(word.equals("F U N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.fun));
		if(word.equals("G A G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.gag));
		if(word.equals("G A P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.gap));
		if(word.equals("G E T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.get));
		if(word.equals("G O B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.gob));
		if(word.equals("G U M"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.gum));
		if(word.equals("G U N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.gun));
		if(word.equals("H I D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.hid));
		if(word.equals("H I M"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.him));
		if(word.equals("H U M"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.hum));
		if(word.equals("J I G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.jig));
		if(word.equals("K E G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.keg));
		if(word.equals("L A D"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.lad));
		if(word.equals("L E G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.leg));
		if(word.equals("L I T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.lit));
		if(word.equals("L O T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.lot));
		if(word.equals("M O B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.mob));
		if(word.equals("M O M"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.mom));
		if(word.equals("M U M"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.mum));
		if(word.equals("N A G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.nag));
		if(word.equals("N E T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.net));
		if(word.equals("N I P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.nip));
		if(word.equals("N U N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.nun));
		if(word.equals("N U T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.nut));
		if(word.equals("P E G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.peg));
		if(word.equals("P E T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.pet));
		if(word.equals("R A N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.ran));
		if(word.equals("R A P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.rap));
		if(word.equals("R I B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.rib));
		if(word.equals("R I G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.rig));
		if(word.equals("R I M"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.rim));
		if(word.equals("R O B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.rob));
		if(word.equals("R O T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.rot));
		if(word.equals("R U B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.rub));
		if(word.equals("R U T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.rut));
		if(word.equals("S A G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.sag));
		if(word.equals("S A P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.sap));
		if(word.equals("S E T"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.set));
		if(word.equals("S U B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.sub));
		if(word.equals("S U M"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.sum));
		if(word.equals("T A B"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.tab));
		if(word.equals("T A P"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.tap));
		if(word.equals("T I N"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.tin));
		if(word.equals("W A G"))
			b = createBitmap(this.getResources().getDrawable(R.drawable.wag));
		if(word.equals("W E B"))
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
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			view.Touched((int) event.getX(), (int) event.getY());
		}
		return true;
	}

	@Override
	public void onBackPressed()
	{
		this.onPause();
		inGameMenu.show();
	}

	@Override
	protected void onPause()
	{
		view.pause();
		if(Util.musicPlayer != null)
		{
			Util.musicPlayer.pause();
		}
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		inGameMenu.cancel();
		view.resume();
		if(Util.musicPlayer != null)
		{
			Util.musicPlayer.start();
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
		if(d != null)
			image.setBackgroundDrawable(null);
		if(b != null)
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
		if(spriteTimer.getElapsedTime() == 5000)
		{
			view.getLizzardCrawlUp().loadBitmap();
			view.getLizzardCrawlUp().setVisible(true);
			// fish 1.
			view.getFish().loadBitmap();
			view.getFish().setVisible(true);
		}

		if(spriteTimer.getElapsedTime() == 5500)
		{
			view.getLizzard().loadBitmap();
			view.getLizzard().setVisible(true);
			view.getLizzardCrawlUp().setVisible(false);
			view.getLizzardCrawlUp().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 6500)
		{
			// fish 1.
			view.getFish().setVisible(false);
			view.getFish().unloadBitmap();
			view.getFish().setLoc1(false);
			view.getFish().setLoc2(true);
		}

		if(spriteTimer.getElapsedTime() == 15000)
		{
			view.getLizzardCrawlDown().loadBitmap();
			view.getLizzardCrawlDown().setVisible(true);
			view.getLizzard().setVisible(false);
			view.getLizzard().unloadBitmap();
			// duck 1.
			if(view.getDuckRL().getX() <= ((Util.PIXEL_WIDTH / 2) - (Util.PIXEL_WIDTH / 3))
					|| view.getDuckRL().getX() == (Util.PIXEL_WIDTH) - (Util.PIXEL_WIDTH / 5))
			{
				view.getDuckLR().loadBitmap();
				view.getDuckLR().setVisible(true);
			}
		}

		if(spriteTimer.getElapsedTime() == 15500)
		{
			view.getLizzardCrawlDown().setVisible(false);
			view.getLizzardCrawlDown().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 35000)
		{
			view.getLizzardCrawlUp().loadBitmap();
			view.getLizzardCrawlUp().setVisible(true);
			// fish 2.
			view.getFish().loadBitmap();
			view.getFish().setVisible(true);
		}

		if(spriteTimer.getElapsedTime() == 35500)
		{
			view.getLizzard().loadBitmap();
			view.getLizzard().setVisible(true);
			view.getLizzardCrawlUp().setVisible(false);
			view.getLizzardCrawlUp().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 36500)
		{
			// fish 2.
			view.getFish().setVisible(false);
			view.getFish().unloadBitmap();
			view.getFish().setLoc2(false);
			view.getFish().setLoc3(true);
		}

		if(spriteTimer.getElapsedTime() == 45000)
		{
			view.getLizzardCrawlDown().loadBitmap();
			view.getLizzardCrawlDown().setVisible(true);
			view.getLizzard().setVisible(false);
			view.getLizzard().unloadBitmap();
			// duck 2.
			if(view.getDuckLR().getX() >= (Util.PIXEL_WIDTH / 2)
					+ (Util.PIXEL_WIDTH / 4)
					|| view.getDuckLR().getX() == Util.PIXEL_WIDTH / 5)
			{
				view.getDuckRL().loadBitmap();
				view.getDuckRL().setVisible(true);
			}
		}

		if(spriteTimer.getElapsedTime() == 45500)
		{
			view.getLizzardCrawlDown().setVisible(false);
			view.getLizzardCrawlDown().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 65000)
		{
			view.getLizzardCrawlUp().loadBitmap();
			view.getLizzardCrawlUp().setVisible(true);
			// duck 3.
			if(view.getDuckRL().getX() <= ((Util.PIXEL_WIDTH / 2) - (Util.PIXEL_WIDTH / 3))
					|| view.getDuckRL().getX() == (Util.PIXEL_WIDTH) - (Util.PIXEL_WIDTH / 5))
			{
				view.resetDuckLR();
			}
		}

		if(spriteTimer.getElapsedTime() == 65500)
		{
			view.getLizzard().loadBitmap();
			view.getLizzard().setVisible(true);
			view.getLizzardCrawlUp().setVisible(false);
			view.getLizzardCrawlUp().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 75000)
		{
			view.getLizzardCrawlDown().loadBitmap();
			view.getLizzardCrawlDown().setVisible(true);
			view.getLizzard().setVisible(false);
			view.getLizzard().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 75500)
		{
			view.getLizzardCrawlDown().setVisible(false);
			view.getLizzardCrawlDown().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 95000)
		{
			view.getLizzardCrawlUp().loadBitmap();
			view.getLizzardCrawlUp().setVisible(true);
			// fish 3.
			view.getFish().loadBitmap();
			view.getFish().setVisible(true);
		}

		if(spriteTimer.getElapsedTime() == 95500)
		{
			view.getLizzard().loadBitmap();
			view.getLizzard().setVisible(true);
			view.getLizzardCrawlUp().setVisible(false);
			view.getLizzardCrawlUp().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 96500)
		{
			// fish 3.
			view.getFish().setVisible(false);
			view.getFish().unloadBitmap();
			view.getFish().setLoc3(false);
			view.getFish().setLoc1(true);
		}

		if(spriteTimer.getElapsedTime() == 115000)
		{
			view.getLizzardCrawlDown().loadBitmap();
			view.getLizzardCrawlDown().setVisible(true);
			view.getLizzard().setVisible(false);
			view.getLizzard().unloadBitmap();
			// duck 4.
			if(view.getDuckLR().getX() >= (Util.PIXEL_WIDTH / 2)
					+ (Util.PIXEL_WIDTH / 4)
					|| view.getDuckLR().getX() == Util.PIXEL_WIDTH / 5)
			{
				view.resetDuckRL();
			}
		}

		if(spriteTimer.getElapsedTime() == 115500)
		{
			view.getLizzardCrawlDown().setVisible(false);
			view.getLizzardCrawlDown().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 135000)
		{
			view.getLizzardCrawlUp().loadBitmap();
			view.getLizzardCrawlUp().setVisible(true);
		}

		if(spriteTimer.getElapsedTime() == 135500)
		{
			view.getLizzard().loadBitmap();
			view.getLizzard().setVisible(true);
			view.getLizzardCrawlUp().setVisible(false);
			view.getLizzardCrawlUp().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 145000)
		{
			view.getLizzardCrawlDown().loadBitmap();
			view.getLizzardCrawlDown().setVisible(true);
			view.getLizzard().setVisible(false);
			view.getLizzard().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 145500)
		{
			view.getLizzardCrawlDown().setVisible(false);
			view.getLizzardCrawlDown().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 165000)
		{
			view.getLizzardCrawlUp().loadBitmap();
			view.getLizzardCrawlUp().setVisible(true);
		}

		if(spriteTimer.getElapsedTime() == 165500)
		{
			view.getLizzard().loadBitmap();
			view.getLizzard().setVisible(true);
			view.getLizzardCrawlUp().setVisible(false);
			view.getLizzardCrawlUp().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 175000)
		{
			view.getLizzardCrawlDown().loadBitmap();
			view.getLizzardCrawlDown().setVisible(true);
			view.getLizzard().setVisible(false);
			view.getLizzard().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 175500)
		{
			view.getLizzardCrawlDown().setVisible(false);
			view.getLizzardCrawlDown().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 195000)
		{
			view.getLizzardCrawlUp().loadBitmap();
			view.getLizzardCrawlUp().setVisible(true);
			// fish 4.
			view.getFish().loadBitmap();
			view.getFish().setVisible(true);
		}

		if(spriteTimer.getElapsedTime() == 195500)
		{
			view.getLizzard().loadBitmap();
			view.getLizzard().setVisible(true);
			view.getLizzardCrawlUp().setVisible(false);
			view.getLizzardCrawlUp().unloadBitmap();
			// duck 5.
			if(view.getDuckRL().getX() <= ((Util.PIXEL_WIDTH / 2) - (Util.PIXEL_WIDTH / 3))
					|| view.getDuckRL().getX() == (Util.PIXEL_WIDTH) - (Util.PIXEL_WIDTH / 5))
			{
				view.resetDuckLR();
			}

		}

		if(spriteTimer.getElapsedTime() == 196500)
		{
			// fish 4.
			view.getFish().setVisible(false);
			view.getFish().unloadBitmap();
			view.getFish().setLoc1(false);
			view.getFish().setLoc2(true);
		}

		if(spriteTimer.getElapsedTime() == 205000)
		{
			view.getLizzardCrawlDown().loadBitmap();
			view.getLizzardCrawlDown().setVisible(true);
			view.getLizzard().setVisible(false);
			view.getLizzard().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 205500)
		{
			view.getLizzardCrawlDown().setVisible(false);
			view.getLizzardCrawlDown().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 225000)
		{
			view.getLizzardCrawlUp().loadBitmap();
			view.getLizzardCrawlUp().setVisible(true);
			// fish 5.
			view.getFish().loadBitmap();
			view.getFish().setVisible(true);
		}

		if(spriteTimer.getElapsedTime() == 225500)
		{
			view.getLizzard().loadBitmap();
			view.getLizzard().setVisible(true);
			view.getLizzardCrawlUp().setVisible(false);
			view.getLizzardCrawlUp().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 226500)
		{
			// fish 5.
			view.getFish().setVisible(false);
			view.getFish().unloadBitmap();
			view.getFish().setLoc2(false);
			view.getFish().setLoc3(true);
		}

		if(spriteTimer.getElapsedTime() == 235000)
		{
			view.getLizzardCrawlDown().loadBitmap();
			view.getLizzardCrawlDown().setVisible(true);
			view.getLizzard().setVisible(false);
			view.getLizzard().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 235500)
		{
			view.getLizzardCrawlDown().setVisible(false);
			view.getLizzardCrawlDown().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 245000)
		{
			view.getLizzardCrawlUp().loadBitmap();
			view.getLizzardCrawlUp().setVisible(true);
		}

		if(spriteTimer.getElapsedTime() == 245500)
		{
			view.getLizzard().loadBitmap();
			view.getLizzard().setVisible(true);
			view.getLizzardCrawlUp().setVisible(false);
			view.getLizzardCrawlUp().unloadBitmap();
			// duck 6.
			if(view.getDuckLR().getX() >= (Util.PIXEL_WIDTH / 2)
					+ (Util.PIXEL_WIDTH / 4)
					|| view.getDuckLR().getX() == Util.PIXEL_WIDTH / 5)
			{
				view.resetDuckRL();
			}
		}

		if(spriteTimer.getElapsedTime() == 255000)
		{
			view.getLizzardCrawlDown().loadBitmap();
			view.getLizzardCrawlDown().setVisible(true);
			view.getLizzard().setVisible(false);
			view.getLizzard().unloadBitmap();
			// fish 6.
			view.getFish().loadBitmap();
			view.getFish().setVisible(true);
		}

		if(spriteTimer.getElapsedTime() == 255500)
		{
			view.getLizzardCrawlDown().setVisible(false);
			view.getLizzardCrawlDown().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 256500)
		{
			// fish 6.
			view.getFish().setVisible(false);
			view.getFish().unloadBitmap();
			view.getFish().setLoc3(false);
			view.getFish().setLoc1(true);
		}

		if(spriteTimer.getElapsedTime() == 265000)
		{
			view.getLizzardCrawlUp().loadBitmap();
			view.getLizzardCrawlUp().setVisible(true);
		}

		if(spriteTimer.getElapsedTime() == 265500)
		{
			view.getLizzard().loadBitmap();
			view.getLizzard().setVisible(true);
			view.getLizzardCrawlUp().setVisible(false);
			view.getLizzardCrawlUp().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 275000)
		{
			view.getLizzardCrawlDown().loadBitmap();
			view.getLizzardCrawlDown().setVisible(true);
			view.getLizzard().setVisible(false);
			view.getLizzard().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 275500)
		{
			view.getLizzardCrawlDown().setVisible(false);
			view.getLizzardCrawlDown().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 295000)
		{
			view.getLizzardCrawlUp().loadBitmap();
			view.getLizzardCrawlUp().setVisible(true);
		}

		if(spriteTimer.getElapsedTime() == 295500)
		{
			view.getLizzard().loadBitmap();
			view.getLizzard().setVisible(true);
			view.getLizzardCrawlUp().setVisible(false);
			view.getLizzardCrawlUp().unloadBitmap();
			// duck 7.
			if(view.getDuckRL().getX() <= ((Util.PIXEL_WIDTH / 2) - (Util.PIXEL_WIDTH / 3))
					|| view.getDuckRL().getX() == (Util.PIXEL_WIDTH) - (Util.PIXEL_WIDTH / 5))
			{
				view.resetDuckLR();
			}
		}

		if(spriteTimer.getElapsedTime() == 305000)
		{
			view.getLizzardCrawlDown().loadBitmap();
			view.getLizzardCrawlDown().setVisible(true);
			view.getLizzard().setVisible(false);
			view.getLizzard().unloadBitmap();
		}

		if(spriteTimer.getElapsedTime() == 305500)
		{
			view.getLizzardCrawlDown().setVisible(false);
			view.getLizzardCrawlDown().unloadBitmap();
		}
	}
}
