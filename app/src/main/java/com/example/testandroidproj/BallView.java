package com.example.testandroidproj;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BallView extends View {

    private Paint paint;
    private float xPos = -1;
    private float yPos = -1;
    private static final int BALL_RADIUS = 50;

    public BallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(0xFFFF0000); // Red
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (xPos == -1 && yPos == -1) {
            xPos = getWidth() / 2f;
            yPos = getHeight() / 2f;
        }
        canvas.drawCircle(xPos, yPos, BALL_RADIUS, paint);
    }

    public void updatePosition(float x, float y) {
        xPos -= x * 2;
        yPos += y * 2;

        if (xPos < BALL_RADIUS) {
            xPos = BALL_RADIUS;
        }
        if (xPos > getWidth() - BALL_RADIUS) {
            xPos = getWidth() - BALL_RADIUS;
        }
        if (yPos < BALL_RADIUS) {
            yPos = BALL_RADIUS;
        }
        if (yPos > getHeight() - BALL_RADIUS) {
            yPos = getHeight() - BALL_RADIUS;
        }
        invalidate();
    }
}
