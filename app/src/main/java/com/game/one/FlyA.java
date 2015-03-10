package com.game.one;

import java.util.Random;
import android.content.Context;
import android.graphics.Bitmap;

public class FlyA extends Sprite
{
	private String flyId = "";
	private static Bitmap globalBitmap;
	// used to ensure that setAttemptNumber is only called on the first tap.
	private boolean eat = false;

	public FlyA(GameView view, Context context, String id, boolean spitOut)
	{
		super(view, context);
		
		this.flyId = id;

		Random rnd = new Random();

		if(spitOut == false)
		{
			x = rnd.nextInt(Util.PIXEL_WIDTH / 2);
			y = rnd.nextInt(Util.PIXEL_HEIGHT / 2);
			speedX = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
			speedY = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
		}
		if(spitOut == true)
		{
			int fortiethWidth = Util.PIXEL_WIDTH / 40;
			this.x = (Util.PIXEL_WIDTH / 2) - fortiethWidth;
			this.y = Util.PIXEL_HEIGHT / 5;
			speedX = 14;
			speedY = 14;
			gameTimer.start();
		}
	}
	
	public void loadBitmap()
	{
		globalBitmap = createBitmap(context.getResources().getDrawable(
				R.drawable.flya));

		this.bitmap = globalBitmap;
		this.width = this.bitmap.getWidth() / NUMBER_OF_COLUMNS;
		this.height = this.bitmap.getHeight() / NUMBER_OF_ROWS;
	}
	
	@Override
	public void onCollision()
	{
		Frog sprite = this.v.getFrog();

		if(isColliding(sprite))
		{
			this.isTimedOut = sprite.checkWord(flyId);
			this.beEaten = this.isTimedOut;
		}
	}

	public boolean isTouching(int x2, int y2)
	{
		if(x2 > x && x2 < x + width && y2 > y && y2 < y + height)
		{
			if(v.getFlyE().getBeEaten() == false
					&& v.getFlyI().getBeEaten() == false
					&& v.getFlyO().getBeEaten() == false
					&& v.getFlyU().getBeEaten() == false && eat == false)
			{
				Game.theGame.setFlyId("A");
				v.getFrog().setChomping();
				
				// calls the setAttemptNumber() in Game which calls updateAttempts
				// in
				// Game which
				// updates the number of attempts and fills in the missing letter
				// for
				// the current
				// word if the correct letter has been selected.
				Game.theGame.setAttemptNumber();
				setEat(true);
				beEaten = true;
			}
			return true;
		}
		else
			return false;
	}

	private void setEat(boolean e)
	{
		eat = e;
	}
	
	TimerExec gameTimer = new TimerExec(100, -1, new TimerExecTask()
	{
		@Override
		public void onTick()
		{
			if(gameTimer.getElapsedTime() >= 1000)
			{
				Random rnd = new Random();
				speedX = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
				speedY = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
				onFinish();
			}
		}

		@Override
		public void onFinish()
		{
			gameTimer.cancel();
		}
	});
}
