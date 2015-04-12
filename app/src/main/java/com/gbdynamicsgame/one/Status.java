package com.gbdynamicsgame.one;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;

public class Status extends Sprite
{
    private final int NUMBER_OF_ROWS = 4;
    private final int NUMBER_OF_COLUMNS = 4;
    private static Bitmap globalBitmap;
    private int animationRow = 0;
    private int currentFrame = 0;
    private boolean visible = false;
    private int count = 0;

    public Status(GameView view, Context context)
    {
        super(view, context);
        if (globalBitmap == null)
        {
            globalBitmap = createBitmap(context.getResources().getDrawable(
                    R.drawable.status_hdpi));
        }

        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth() / NUMBER_OF_COLUMNS;
        this.height = this.bitmap.getHeight() / NUMBER_OF_ROWS;

        Random rnd = new Random();
        x = rnd.nextInt((Util.PIXEL_WIDTH - (Util.PIXEL_WIDTH / 3))
                - (Util.PIXEL_WIDTH / 3))
                + (Util.PIXEL_WIDTH / 3);
        y = rnd.nextInt(Util.PIXEL_HEIGHT / 2);
    }

    @Override
    public void move()
    {
        count++;

        if (count > 0 && count <= 4)
            animationRow = 0;
        if (count >= 4 && count <= 8)
            animationRow = 1;
        if (count >= 8 && count <= 12)
            animationRow = 2;
        if (count >= 12 && count <= 16)
        {
            animationRow = 3;

            if (count == 16)
                count = 0;
        }

        Random rnd = new Random();
        x = rnd.nextInt((Util.PIXEL_WIDTH - (Util.PIXEL_WIDTH / 3))
                - (Util.PIXEL_WIDTH / 3))
                + (Util.PIXEL_WIDTH / 3);
        y = rnd.nextInt(Util.PIXEL_HEIGHT / 2);

        currentFrame = ++currentFrame % NUMBER_OF_COLUMNS;
        super.move();
    }

    @Override
    public void draw(Canvas canvas)
    {
        move();

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
}
