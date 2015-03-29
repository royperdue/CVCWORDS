package com.game.one;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable
{
	private Game game;
	private Thread t;
	private SurfaceHolder holder;
	volatile private boolean shouldRun = false;
	volatile private int touchX, touchY;
	volatile private boolean isTouched = false;
	private boolean spitOut = false;
	private Frog frog;
	private FatFrog fatty;
	private ChompingFrog chomper;
	private SpittingFrog spitter;
	private LizzardCrawlUp lizzardUp;
	private LizzardSitting lizzard;
	private LizzardCrawlDown lizzardDown;
	private DuckLR duckLR;
	private DuckRL duckRL;
	private Fish fish;
	private FlyA flyA;
	private FlyE flyE;
	private FlyI flyI;
	private FlyO flyO;
	private FlyU flyU;
	private Background bg;
	private Weeds weeds;
    private GameWon gameWon;
	private Status status;

	private GameView(Context context)
	{
		super(context);
	}

	private GameView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	private GameView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	// @param activity the game, class:Game
	public GameView(Activity activity)
	{
		super(activity);

		this.game = (Game) activity;
		this.shouldRun = true;
		holder = getHolder();
		
		bg = new Background(this, activity);
		weeds = new Weeds(this, activity);
        gameWon = new GameWon(this, activity);
		
		frog = new Frog(this, activity);
		fatty = new FatFrog(this, activity);
		chomper = new ChompingFrog(this, activity);
		spitter = new SpittingFrog(this, activity);
		
		lizzardUp = new LizzardCrawlUp(this, activity);
		lizzard = new LizzardSitting(this, activity);
		lizzardDown = new LizzardCrawlDown(this, activity);
		
		fish = new Fish(this, activity);
		duckLR = new DuckLR(this, activity);
		duckRL = new DuckRL(this, activity);
		
		flyA = new FlyA(this, this.getContext(), "A", spitOut);
		flyA.loadBitmap();
		flyA.setVisible(true);
		
		flyE = new FlyE(this, this.getContext(), "E", spitOut);
		flyE.loadBitmap();
		flyE.setVisible(true);
		
		flyI = new FlyI(this, this.getContext(), "I", spitOut);
		flyI.loadBitmap();
		flyI.setVisible(true);
		
		flyO = new FlyO(this, this.getContext(), "O", spitOut);
		flyO.loadBitmap();
		flyO.setVisible(true);
		
		flyU = new FlyU(this, this.getContext(), "U", spitOut);
		flyU.loadBitmap();
		flyU.setVisible(true);
		
		bg.loadBitmap();
		bg.setVisible(true);
		weeds.loadBitmap();
		weeds.setVisible(true);
        gameWon.loadBitmap();
        gameWon.setVisible(false);
		status = new Status(this, this.getContext());
	}


	public void gameOver()
	{
		this.shouldRun = false;
		bg.setVisible(false);
		bg.unloadBitmap();
	}
	
	@SuppressLint("WrongCall")
	public void run()
	{
		while(shouldRun)
		{

			if(!holder.getSurface().isValid())
			{
				continue;
			}

			if(this.isTouched)
			{
				checkTouchedSprites();
				this.isTouched = false;
			}

			checkTimeOut();
			move();

			// draw
			Canvas c = holder.lockCanvas();
			onDraw(c);

			holder.unlockCanvasAndPost(c);

			// sleep
			try
			{
				Thread.sleep(Util.UPDATE_INTERVAL);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean isRunning()
	{
		return shouldRun;
	}

	public void pause()
	{
		shouldRun = false;
		
		while(t != null)
		{
			try
			{
				t.join();
				break;
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		t = null;
	}

	public void resume()
	{
		shouldRun = false;
		
		while(t != null)
		{
			try
			{
				t.join();
				break;
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		shouldRun = true;
		t = new Thread(this);
		t.start();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		bg.draw(canvas);
		duckLR.draw(canvas);
		duckRL.draw(canvas);
		fish.draw(canvas);
		weeds.draw(canvas);
		frog.draw(canvas);
		chomper.draw(canvas);
		spitter.draw(canvas);
		fatty.draw(canvas);
		lizzardUp.draw(canvas);
		lizzard.draw(canvas);
		lizzardDown.draw(canvas);
		flyA.draw(canvas);
		flyE.draw(canvas);
		flyI.draw(canvas);
		flyO.draw(canvas);
		flyU.draw(canvas);
		status.draw(canvas);
        gameWon.draw(canvas);
	}

	private void checkTimeOut()
	{
		if(this.flyA.isTimedOut())
		{
			flyA.setVisible(false);
			flyA.unloadBitmap();
		}
		if(this.flyE.isTimedOut())
		{
			flyE.setVisible(false);
			flyE.unloadBitmap();
		}
		if(this.flyI.isTimedOut())
		{
			flyI.setVisible(false);
			flyI.unloadBitmap();
		}
		if(this.flyO.isTimedOut())
		{
			flyO.setVisible(false);
			flyO.unloadBitmap();
		}
		if(this.flyU.isTimedOut())
		{
			flyU.setVisible(false);
			flyU.unloadBitmap();
		}
	}

	public void resetFlys()
	{
		if(flyA.getVisible() == false)
		{
			flyA = new FlyA(this, this.getContext(), "A", spitOut);
			flyA.loadBitmap();
			flyA.setVisible(true);
			flyA.speedXUp(Game.theGame.getLevel());
			flyA.speedYUp(Game.theGame.getLevel());
		}
		if(flyE.getVisible() == false)
		{
			flyE = new FlyE(this, this.getContext(), "E", spitOut);
			flyE.loadBitmap();
			flyE.setVisible(true);
			flyE.speedXUp(Game.theGame.getLevel());
			flyE.speedYUp(Game.theGame.getLevel());
		}
		if(flyI.getVisible() == false)
		{
			flyI = new FlyI(this, this.getContext(), "I", spitOut);
			flyI.loadBitmap();
			flyI.setVisible(true);
			flyI.speedXUp(Game.theGame.getLevel());
			flyI.speedYUp(Game.theGame.getLevel());
		}
		if(flyO.getVisible() == false)
		{
			flyO = new FlyO(this, this.getContext(), "O", spitOut);
			flyO.loadBitmap();
			flyO.setVisible(true);
			flyO.speedXUp(Game.theGame.getLevel());
			flyO.speedYUp(Game.theGame.getLevel());
		}
		if(flyU.getVisible() == false)
		{
			flyU = new FlyU(this, this.getContext(), "U", spitOut);
			flyU.loadBitmap();
			flyU.setVisible(true);
			flyU.speedXUp(Game.theGame.getLevel());
			flyU.speedYUp(Game.theGame.getLevel());
		}
		// back up in case this same method call in the frog class, gameTimer1,
		// onFinish(),
		// fails to set visible to false in status.
		this.getStatus().setVisible(false);

	}
	
	public void resetDuckLR()
	{
		if(duckLR.getVisible() == false)
		{
			duckLR = new DuckLR(this, this.getContext());
			duckLR.loadBitmap();
			duckLR.setVisible(true);
		}
	}
	
	public void resetDuckRL()
	{
		if(duckRL.getVisible() == false)
		{
			duckRL = new DuckRL(this, this.getContext());
			duckRL.loadBitmap();
			duckRL.setVisible(true);
		}
	}

	private void move()
	{
	}

	public void Touched(int x, int y)
	{
		this.touchX = x;
		this.touchY = y;
		this.isTouched = true;
	}

	private void checkTouchedSprites()
	{

		// calls each fly and passes the x, y where the touch event
		// occurred.
		if(this.flyA.isTouching(touchX, touchY))
		{
		}
		if(this.flyE.isTouching(touchX, touchY))
		{
		}
		if(this.flyI.isTouching(touchX, touchY))
		{
		}
		if(this.flyO.isTouching(touchX, touchY))
		{
		}
		if(this.flyU.isTouching(touchX, touchY))
		{
		}
	}

	public Frog getFrog()
	{
		return this.frog;
	}

	public FatFrog getFatFrog()
	{
		return this.fatty;
	}

	public ChompingFrog getChompingFrog()
	{
		return this.chomper;
	}

	public SpittingFrog getSpittingFrog()
	{
		return this.spitter;
	}

	public Game getGame()
	{
		return this.game;
	}

	public FlyA getFlyA()
	{
		return flyA;
	}

	public FlyE getFlyE()
	{
		return flyE;
	}

	public FlyI getFlyI()
	{
		return flyI;
	}

	public FlyO getFlyO()
	{
		return flyO;
	}

	public FlyU getFlyU()
	{
		return flyU;
	}

	public Status getStatus()
	{
		return status;
	}

	public Background getBg()
	{
		return bg;
	}

	public Weeds getWeeds()
	{
		return weeds;
	}

    public GameWon getGameWon()
    {
        return gameWon;
    }
	
	public LizzardCrawlUp getLizzardCrawlUp()
	{
		return lizzardUp;
	}
	
	public LizzardCrawlDown getLizzardCrawlDown()
	{
		return lizzardDown;
	}
	
	public LizzardSitting getLizzard()
	{
		return lizzard;
	}
	
	public Fish getFish()
	{
		return fish;
	}
	
	public DuckLR getDuckLR()
	{
		return duckLR;
	}
	
	public DuckRL getDuckRL()
	{
		return duckRL;
	}
	
	public void setSpitOut(boolean s)
	{
		spitOut = s;
	}

	public void setShouldRun(boolean b)
	{
		shouldRun = b;
	}
	
	// when a touch event occurs calls Touched and passes x, y of where event
	// occurred.
	// Touched stores the x, y and sets the boolean isTouched to true.
	// in thread if boolean isTouched equals true then checkTouchedSprites.
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		Touched((int) event.getX(), (int) event.getY());
		return true;

	}
}
