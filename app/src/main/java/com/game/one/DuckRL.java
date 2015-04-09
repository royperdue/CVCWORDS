package com.game.one;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class DuckRL extends Sprite
{
    private static Bitmap globalBitmap;
    private boolean visible = false;

    public DuckRL(GameView view, Context context)
    {
        super(view, context);

        x = ((Util.PIXEL_WIDTH) - (Util.PIXEL_WIDTH / 5));
        y = Util.PIXEL_HEIGHT / 3;
        speedX = 4;
        speedY = 0;

    }

    public void loadBitmap()
    {
        globalBitmap = createBitmap(context.getResources().getDrawable(
                R.drawable.duck_rl));

        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth();
        this.height = this.bitmap.getHeight();
    }

    private void update()
    {

        if (x < ((Util.PIXEL_WIDTH / 2) - (Util.PIXEL_WIDTH / 3)))
        {
            setVisible(false);
        }
        x = x - speedX;
    }

    @Override
    public void draw(Canvas canvas)
    {
        if (visible == true)
        {
            update();
            int srcX = 0;
            int srcY = 0;
            Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
            Rect dst = new Rect(x, y, x + width, y + height);
            canvas.drawBitmap(bitmap, src, dst, null);

            if (visible == false)
                unloadBitmap();
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
