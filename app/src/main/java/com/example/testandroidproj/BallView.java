package com.example.testandroidproj;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BallView extends View {

    private Paint ballPaint;
    private Paint holePaint;
    private Paint wallPaint;
    private Paint finishPaint;
    private Paint startPaint;

    private float xPos = -1;
    private float yPos = -1;
    private float startX = -1;
    private float startY = -1;

    private static final int BALL_RADIUS = 40;
    private static final int HOLE_RADIUS = 55;
    private static final float TILT_SENSITIVITY = 4.0f;

    private List<PointF> holes = new ArrayList<>();
    private List<RectF> walls = new ArrayList<>();
    private RectF finishArea;
    private RectF startArea;

    public BallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ballPaint = new Paint();
        ballPaint.setColor(0xFFFF0000); // Red

        holePaint = new Paint();
        holePaint.setColor(Color.BLACK);

        wallPaint = new Paint();
        wallPaint.setColor(Color.DKGRAY);

        finishPaint = new Paint();
        finishPaint.setColor(Color.GREEN);

        startPaint = new Paint();
        startPaint.setColor(Color.BLUE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (startX == -1) {
            startX = w * 0.1f;
            startY = h * 0.1f;
        }
        resetBall();

         // Start and finish areas
        startArea = new RectF(0, 0, w * 0.2f, h * 0.2f);
        finishArea = new RectF(0, h * 0.8f, w * 0.2f, h);

        // --- A MAZE --- //

        // Redesigned holes
        holes.clear();
        holes.add(new PointF(w * 0.6f, h * 0.15f)); // Dead end near start
        holes.add(new PointF(w * 0.4f, h * 0.5f));  // Mid-path challenge
        holes.add(new PointF(w * 0.8f, h * 0.5f));  // Mid-path challenge
        holes.add(new PointF(w * 0.11f, h * 0.59f));  // The most left hole
        holes.add(new PointF(w * 0.5f, h * 0.9f));  // Bottom path hazard

        // Design of the walls
        walls.clear();
        // Top path walls
        walls.add(new RectF(0, h * 0.2f, w * 0.9f, h * 0.25f));
        walls.add(new RectF(w * 0.2f, h * 0.4f, w, h * 0.45f));
        walls.add(new RectF(w * 0.5f, h * 0.6f, w * 0.55f, h * 0.3f));
        
        // Middle vertical walls

        walls.add(new RectF(w * 0.3f, h * 0.2f, w * 0.35f, h * 0.1f));


        // Bottom path walls
        walls.add(new RectF(0, h * 0.75f, w * 0.65f, h * 0.8f));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(startArea, startPaint);
        canvas.drawRect(finishArea, finishPaint);

        for (RectF wall : walls) {
            canvas.drawRect(wall, wallPaint);
        }
        for (PointF hole : holes) {
            canvas.drawCircle(hole.x, hole.y, HOLE_RADIUS, holePaint);
        }
        
        canvas.drawCircle(xPos, yPos, BALL_RADIUS, ballPaint);
    }

    public void updatePosition(float x, float y) {
        float newX = xPos - x * TILT_SENSITIVITY;
        float newY = yPos + y * TILT_SENSITIVITY;

        RectF nextBallBounds = new RectF(newX - BALL_RADIUS, newY - BALL_RADIUS, newX + BALL_RADIUS, newY + BALL_RADIUS);

        for (RectF wall : walls) {
            if (RectF.intersects(nextBallBounds, wall)) {
                return;
            }
        }

        xPos = newX;
        yPos = newY;

        if (xPos < BALL_RADIUS) xPos = BALL_RADIUS;
        if (xPos > getWidth() - BALL_RADIUS) xPos = getWidth() - BALL_RADIUS;
        if (yPos < BALL_RADIUS) yPos = BALL_RADIUS;
        if (yPos > getHeight() - BALL_RADIUS) yPos = getHeight() - BALL_RADIUS;

        invalidate();
    }

    public boolean isBallInHole() {
        for (PointF hole : holes) {
            float dx = xPos - hole.x;
            float dy = yPos - hole.y;
            if (Math.sqrt(dx * dx + dy * dy) < HOLE_RADIUS) {
                return true;
            }
        }
        return false;
    }

    public boolean isBallInFinish() {
        return finishArea.contains(xPos, yPos);
    }

    public void resetBall() {
        xPos = startX;
        yPos = startY;
        invalidate();
    }
}
