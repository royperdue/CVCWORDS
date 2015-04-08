package com.game.one;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by royperdue on 4/8/15.
 */
public class GameOver extends Sprite
{
    private Bitmap globalBitmap;
    private boolean visible = false;

    protected GameOver(GameView view, Context context)
    {
        super(view, context);
        this.v = view;
    }


    public void loadBitmap()
    {
        globalBitmap = createBitmap(context.getResources().getDrawable(
                R.drawable.game_over));

        this.bitmap = globalBitmap;
        this.width = this.bitmap.getWidth();
        this.height = this.bitmap.getHeight();
    }

    @Override
    public void draw(Canvas canvas)
    {
        if (visible == true)
        {
            Rect dst = new Rect(0, 0, v.getWidth(), v.getHeight());
            canvas.drawBitmap(this.globalBitmap, null, dst, null);
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
