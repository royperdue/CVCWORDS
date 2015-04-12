package com.gbdynamicsgame.one;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public abstract class Sprite
{
	protected Bitmap bitmap;
	protected final int MAX_SPEED = 6;
	protected final int NUMBER_OF_ROWS = 2;
	protected final int NUMBER_OF_COLUMNS = 3;
	protected int height, width;
	protected int x;
	protected int y;
	protected int speedX;
	protected int speedY;
	protected Rect src;
	protected Rect dst;
	protected boolean isTimedOut = false;
	protected boolean beEaten = false;
	protected int currentFrame = 0;
	protected boolean visible = false;
	protected GameView v;
	protected Context context;

	protected Sprite(GameView view, Context context)
	{
		this.v = view;
		this.context = context;
	}

	protected void move()
	{
		if(beEaten == false)
		{
			if(x > v.getWidth() - width - speedX || x + speedX < 0)
			{
				speedX = -speedX;
			}
			x = x + speedX;
			if(y > v.getHeight() - height - speedY || y + speedY < 0)
			{
				speedY = -speedY;
			}
			y = y + speedY;

			currentFrame = ++currentFrame % NUMBER_OF_COLUMNS;
		}
		if(beEaten == true)
		{
			if(this.v.getFrog().getX() < this.x)
			{
				this.speedX--;
				if(this.speedX < -MAX_SPEED * Util.FLY_SPEED_FACTOR)
				{
					this.speedX = (int) (-MAX_SPEED * Util.FLY_SPEED_FACTOR);
				}
			}
			else
			{
				this.speedX++;
				if(this.speedX > MAX_SPEED * Util.FLY_SPEED_FACTOR)
				{
					this.speedX = (int) (MAX_SPEED * Util.FLY_SPEED_FACTOR);
				}
			}

			if(this.v.getFrog().getY() < this.y)
			{
				this.speedY--;
				if(this.speedY < -MAX_SPEED * Util.FLY_SPEED_FACTOR)
				{
					this.speedY = (int) (-MAX_SPEED * Util.FLY_SPEED_FACTOR);
				}
			}
			else
			{
				this.speedY++;
				if(this.speedY > MAX_SPEED * Util.FLY_SPEED_FACTOR)
				{
					this.speedY = (int) (MAX_SPEED * Util.FLY_SPEED_FACTOR);
				}
			}
			if(x >= this.v.getWidth() - width - speedX || x + speedX <= 0)
			{
				speedX = -speedX;
			}
			x = x + speedX;

			if(y >= this.v.getHeight() - height - speedY || y + speedY <= 0)
			{
				speedY = -speedY;
			}
			y = y + speedY;

			currentFrame = ++currentFrame % NUMBER_OF_COLUMNS;

			onCollision();
		}
	}

	// paint for line through center of tongue.
	protected Paint pLine = new Paint()
	{
		{
			setStyle(Paint.Style.STROKE);
			setAntiAlias(true);
			setStrokeWidth(1.5f);
			setColor(Color.BLACK);
		}
	};
	// paint for red part of tongue.
	protected Paint pLineBorder = new Paint()
	{
		{
			setStyle(Paint.Style.STROKE);
			setAntiAlias(true);
			setStrokeWidth(10.0f);
			setStrokeCap(Cap.ROUND);
			setColor(Color.RED);
		}
	};

	protected void draw(Canvas canvas)
	{
		if(visible == true)
		{
			if(beEaten == false)
			{
				move();

				int srcX = currentFrame * width;
				int srcY = getAnimationRow() * height;
				Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
				Rect dst = new Rect(x, y, x + width, y + height);
				canvas.drawBitmap(bitmap, src, dst, null);
			}
			if(beEaten == true)
			{
				move();

				// start and end points for drawing tongue.
				int startX = x + (bitmap.getWidth() / 4);
				int startY = y + (bitmap.getHeight() / 4);
				int endX = (this.v.getFrog().getX() + (v.getFrog().getWidth() / 2));
				int endY = (this.v.getFrog().getY()
						+ (v.getFrog().getHeight() / 2) - (v.getFrog()
						.getHeight() / 8));
				// path for drawing tongue.
				Path p = new Path();
				// point for x and y coordinates of mid point in tongue.
				Point mid = new Point();
				// point for x and y coordinates of start point in tongue.
				Point start = new Point();
				start.set(startX, startY);
				// point for x and y coordinates of end point in tongue.
				Point end = new Point();
				end.set(endX, endY);
				mid.set((start.x + end.x) / 2, (start.y + end.y) / 2);

				// Draw line connecting the two points:
				p.reset();
				p.moveTo(start.x, start.y);
				p.quadTo((start.x + mid.x) / 2, start.y, mid.x, mid.y);
				p.quadTo((mid.x + end.x) / 2, end.y, end.x, end.y);

				canvas.drawPath(p, pLineBorder);
				canvas.drawPath(p, pLine);

				// redraw fly.
				int srcX = currentFrame * width;
				int srcY = getAnimationRow() * height;
				Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
				Rect dst = new Rect(x, y, x + width, y + height);
				canvas.drawBitmap(bitmap, src, dst, null);
			}
		}
	}

	protected int getAnimationRow()
	{
		double dirDouble = (Math.atan2(speedX, speedY) / (Math.PI / 2));

		if(dirDouble < 0)
			return 0;
		else
			return 1;
	}

	public boolean isColliding(Sprite sprite)
	{
		return isColliding(sprite, Util.DISTANCE_COLLISION_FACTOR);
	}

	public boolean isColliding(Sprite sprite, float factor)
	{
		int m1x = this.x + (this.width >> 1);
		int m1y = this.y + (this.height >> 1);
		int m2x = sprite.x + (sprite.width >> 1);
		int m2y = sprite.y + (sprite.height >> 1);
		int dx = m1x - m2x;
		int dy = m1y - m2y;
		int d = (int) Math.sqrt(dy * dy + dx * dx);

		if(d < (this.width + sprite.width) * factor
				|| d < (this.height + sprite.height) * factor)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean isTouching(int x, int y)
	{
		return false;
	}

	public void unloadBitmap()
	{
		if(bitmap != null)
		{
			bitmap.recycle();
		}
		bitmap = null;

		System.gc();
	}

	protected void onCollision()
	{

	}

	public boolean isTimedOut()
	{
		return this.isTimedOut;
	}

	public void setVisible(boolean v)
	{
		visible = v;
	}

	public boolean getVisible()
	{
		return visible;
	}

	public void setBeEaten(boolean e)
	{
		beEaten = e;
	}

	public boolean getBeEaten()
	{
		return beEaten;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getSpeedX()
	{
		return speedX;
	}

	public void speedXUp(int s)
	{
		if(speedX >= 0)
			this.speedX = this.speedX + s;
		if(speedX <= 0)
			this.speedX = this.speedX - s;
	}
	
	public void speedXDown(int s)
	{
		if(speedX >= 0)
			this.speedX = this.speedX - s;
		if(speedX <= 0)
			this.speedX = this.speedX + s;
	}

	public int getSpeedY()
	{
		return speedY;
	}

	public void speedYUp(int s)
	{
		if(speedY >= 0)
			this.speedY = this.speedY + s;
		if(speedY <= 0)
			this.speedY = this.speedY - s;
	}
	
	public void speedYDown(int s)
	{
		if(speedY >= 0)
			this.speedY = this.speedY - s;
		if(speedY <= 0)
			this.speedY = this.speedY + s;
	}

	public int getWidth()
	{
		return this.width;
	}

	public int getHeight()
	{
		return this.height;
	}

	protected void playSound()
	{

	}

	public static Bitmap createBitmap(Drawable drawable)
	{
		BitmapDrawable bd = (BitmapDrawable) drawable;
		Bitmap bm = bd.getBitmap();
		return Bitmap.createScaledBitmap(bm,
				(int) (bm.getWidth() * Util.getScaleFactor()),
				(int) (bm.getHeight() * Util.getScaleFactor()), false);
	}
}
