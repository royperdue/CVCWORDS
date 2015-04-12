package com.gbdynamicsgame.one;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class LizzardSitting extends Sprite
{
    private Bitmap globalBitmap;
    private int animationRow = 0;
    private int currentFrame = 0;
    private boolean visible = false;

    public LizzardSitting(GameView view, Context context)
    {
        super(view, context);

        this.x = ((Util.PIXEL_WIDTH / 4) - (Util.PIXEL_WIDTH / 12));
        this.y = ((Util.PIXEL_HEIGHT) - ((Util.PIXEL_HEIGHT / 3) + (Util.PIXEL_HEIGHT / 36)));
    }

    public void loadBitmap()
    {
        globalBitmap = createBitmap(context.getResources().getDrawable(
                R.drawable.lizzard_sitting));

        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth();
        this.height = this.bitmap.getHeight();
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

    public Bitmap getBitmap()
    {
        return bitmap;
    }
}

