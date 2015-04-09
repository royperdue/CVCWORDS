package com.game.one;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class ChompingFrog extends Sprite
{
    private final int NUMBER_OF_ROWS = 4;
    private final int NUMBER_OF_COLUMNS = 5;
    private Bitmap globalBitmap;
    private int animationRow = 0;
    private int currentFrame = 0;
    private int count = 0;
    private boolean visible = false;

    public ChompingFrog(GameView view, Context context)
    {
        super(view, context);

        this.x = ((Util.PIXEL_WIDTH / 2) - (Util.PIXEL_WIDTH / 40 * 3))
                - (Util.PIXEL_HEIGHT / 80);
        this.y = ((Util.PIXEL_HEIGHT / 4) + (Util.PIXEL_HEIGHT / 90));
    }

    public void loadBitmap()
    {
        globalBitmap = createBitmap(context.getResources().getDrawable(
                R.drawable.chomping_frog));

        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth() / NUMBER_OF_COLUMNS;
        this.height = this.bitmap.getHeight() / NUMBER_OF_ROWS;
    }

    @Override
    public void draw(Canvas canvas)
    {
        if (visible == true)
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

        if (count > 0 && count <= 5)
            animationRow = 0;
        if (count >= 5 && count <= 10)
            animationRow = 1;
        if (count >= 10 && count <= 15)
            animationRow = 2;
        if (count >= 15 && count <= 20)
        {
            animationRow = 3;

            if (count == 20)
                count = 0;
        }
        currentFrame = ++currentFrame % NUMBER_OF_COLUMNS;
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
