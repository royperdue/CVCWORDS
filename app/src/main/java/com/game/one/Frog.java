package com.game.one;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;

public class Frog extends Sprite
{
	private final int NUMBER_OF_ROWS = 4;
	private final int NUMBER_OF_COLUMNS = 5;
	private Bitmap globalBitmap;
	private int animationRow = 0;
	private int currentFrame = 0;
	private int count = 0;
	private Game game;
	private MediaPlayer mediaPlayer;
	private boolean visible = false;

	public Frog(GameView view, Context context)
	{
		super(view, context);

		this.game = view.getGame();

		loadBitmap();

		this.x = ((Util.PIXEL_WIDTH / 2) - (Util.PIXEL_WIDTH / 40 * 3))
				- (Util.PIXEL_HEIGHT / 80);
		this.y = ((Util.PIXEL_HEIGHT / 4) + (Util.PIXEL_HEIGHT / 90));
	}

	public void loadBitmap()
	{
		globalBitmap = createBitmap(context.getResources().getDrawable(
				R.drawable.frog_watching));

		this.bitmap = globalBitmap;
		this.width = this.bitmap.getWidth() / NUMBER_OF_COLUMNS;
		this.height = this.bitmap.getHeight() / NUMBER_OF_ROWS;

		setVisible(true);
	}

	@Override
	public void draw(Canvas canvas)
	{
		if(visible == true)
		{
			update();
			int srcX = currentFrame * width;
			int srcY = animationRow * height;
			Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
			Rect dst = new Rect(x, y, x + width, y + height);
			canvas.drawBitmap(bitmap, src, dst, null);
		}
	}

	private void update()
	{
		count++;

		if(count > 0 && count <= 5)
			animationRow = 0;
		if(count >= 5 && count <= 10)
			animationRow = 1;
		if(count >= 10 && count <= 15)
			animationRow = 2;
		if(count >= 15 && count <= 20)
		{
			animationRow = 3;

			if(count == 20)
				count = 0;
		}

		currentFrame = ++currentFrame % NUMBER_OF_COLUMNS;
	}

	@SuppressLint("DefaultLocale")
	public boolean checkWord(String id)
	{
		// checks if word contains the fly id which is the letter of the fly.
		if(game.getWord().contains(id) == true)
		{
			// THIS IS WHERE TO MAKE FROG REACT TO CORRECT SELECTION.
		
			v.getChompingFrog().setVisible(false);
			v.getChompingFrog().unloadBitmap();

			v.getFatFrog().loadBitmap();
			v.getFatFrog().setVisible(true);

			// uses the current word to retrieve the correct mp3 file from the
			// raw folder.
			int resID = context.getResources().getIdentifier(
					game.getWord().toLowerCase().replaceAll(" ", ""), "raw",
					context.getPackageName());
			// creates a mediaPlayer instance with the correct mp3 file.
			mediaPlayer = MediaPlayer.create(context, resID);
			try
			{
				// sets volume of mediaPlayer.
				mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
			}
			catch(IllegalStateException e)
			{
			}

            // sets the status sprite to visible.
			v.getStatus().setVisible(true);

			gameTimer1.start();



			return true;
		}
		else
		{
			// OR HERE IF WRONG SELECTION.

			v.getStatus().setVisible(false);

			v.getSpittingFrog().loadBitmap();
			v.getSpittingFrog().setVisible(true);

			v.getChompingFrog().setVisible(false);
			v.getChompingFrog().unloadBitmap();

			int resID = context.getResources().getIdentifier("cartoon_spit",
					"raw", context.getPackageName());
			mediaPlayer = MediaPlayer.create(context, resID);
			try
			{
				// sets volume of mediaPlayer.
				mediaPlayer.setVolume(Util.soundVolume, Util.soundVolume);
			}
			catch(IllegalStateException e)
			{
			}

			gameTimer2.start();
			return true;
		}
	}

	// a timer is used to ensure that the sound has been played before the word
	// and word pic are updated.
	TimerExec gameTimer1 = new TimerExec(50, -1, new TimerExecTask()
	{
		@Override
		public void onTick()
		{
			if(gameTimer1.getElapsedTime() >= 1000
					&& gameTimer1.getElapsedTime() <= 1600)
			{
				mediaPlayer.start();

			}

            if(gameTimer1.getElapsedTime() > mediaPlayer.getDuration() + 1500)
            {
               onFinish();
            }

		}

		@Override
		public void onFinish()
		{
			loadBitmap();
			setVisible(true);

			v.getFatFrog().setVisible(false);
			v.getFatFrog().unloadBitmap();
			v.resetFlys();

            if(game.getUpdateLevel() == true)
            {
                game.updateLevel1();
                game.setUpdateLevel(false);
            }
			else
			    game.updateWordBoxText();

			// stops the mediaPlayer.
			mediaPlayer.stop();
			mediaPlayer.reset();
			// releases the mediaPlayer instance.
			mediaPlayer.release();
			mediaPlayer = null;
            v.getStatus().setVisible(false);
			// cancels the timer.
			gameTimer1.cancel();
		}
	});

	TimerExec gameTimer2 = new TimerExec(50, -1, new TimerExecTask()
	{
		@Override
		public void onTick()
		{
			if(gameTimer2.getElapsedTime() >= 300)
			{
				mediaPlayer.start();
				loadBitmap();
				setVisible(true);

				v.getSpittingFrog().setVisible(false);
				v.getSpittingFrog().unloadBitmap();

				v.setSpitOut(true);
				v.resetFlys();
				mediaPlayer.stop();
				onFinish();
			}
		}

		@Override
		public void onFinish()
		{
			v.setSpitOut(false);
			mediaPlayer.reset();
			// releases the mediaPlayer instance.
			mediaPlayer.release();
			mediaPlayer = null;
			// sets the status sprite visibility to false.
			gameTimer2.cancel();
		}
	});
	
	@Override
	public void setVisible(boolean v)
	{
		visible = v;
	}

	@Override
	public boolean getVisible()
	{
		return visible;
	}

	public void setChomping()
	{
		v.getChompingFrog().loadBitmap();
		v.getChompingFrog().setVisible(true);

		this.setVisible(false);
		this.unloadBitmap();
	}
}

