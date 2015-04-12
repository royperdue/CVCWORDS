package com.gbdynamicsgame.one;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Fish extends Sprite
{
    private Bitmap globalBitmap;
    private int animationRow = 0;
    private int currentFrame = 0;
    private boolean visible = false;
    private boolean loc1 = true;
    private boolean loc2 = false;
    private boolean loc3 = false;

    public Fish(GameView view, Context context)
    {
        super(view, context);
    }

    public void loadBitmap()
    {
        globalBitmap = createBitmap(context.getResources().getDrawable(
                R.drawable.fish));

        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth();
        this.height = this.bitmap.getHeight();

        if (loc1 == true)
        {
            this.x = Util.PIXEL_WIDTH / 3;
            this.y = ((Util.PIXEL_HEIGHT / 2) + (Util.PIXEL_HEIGHT / 14));
        }

        if (loc2 == true)
        {
            this.x = (Util.PIXEL_WIDTH / 2);
            this.y = ((Util.PIXEL_HEIGHT / 2) + (Util.PIXEL_HEIGHT / 4));
        }

        if (loc3 == true)
        {
            x = ((Util.PIXEL_WIDTH) - (Util.PIXEL_WIDTH / 5));
            y = (Util.PIXEL_HEIGHT / 2) + (Util.PIXEL_HEIGHT / 16);
        }
    }

    @Override
    public void draw(Canvas canvas)
    {
        if (visible == true)
        {
            int srcX = currentFrame * width;
            int srcY = animationRow * height;
            Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
            Rect dst = new Rect(x, y, x + width, y + height);
            canvas.drawBitmap(bitmap, src, dst, null);
        }
    }

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

    public void setLoc1(boolean b)
    {
        loc1 = b;
    }

    public void setLoc2(boolean b)
    {
        loc2 = b;
    }

    public void setLoc3(boolean b)
    {
        loc3 = b;
    }
}

