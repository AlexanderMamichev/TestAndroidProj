package com.example.testandroidproj;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class FunActivity extends AppCompatActivity implements SensorEventListener, BallView.GameEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private BallView ballView;
    private Vibrator vibrator;
    private TextView timerView;
    private TextView lastTimeView;
    private long startTime = 0;
    private long lastTime = 0;
    private boolean isTimerRunning = false;
    private Handler timerHandler = new Handler();

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = SystemClock.elapsedRealtime() - startTime;
            timerView.setText(formatTime(millis));
            timerHandler.postDelayed(this, 50);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fun);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.project_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ballView = findViewById(R.id.ballView);
        ballView.setGameEventListener(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        timerView = findViewById(R.id.timerTextView);
        lastTimeView = findViewById(R.id.lastTimeTextView);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ballView.updatePosition(event.values[0], event.values[1]);

            if (ballView.isBallInHole()) {
                vibrate();
                ballView.resetBall();
                resetTimer();
            } else if (ballView.isBallInFinish()) {
                // You could add a more sophisticated win screen here
                Toast.makeText(this, "You Win!", Toast.LENGTH_SHORT).show();
                ballView.resetBall(); // Restart the game
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(200);
        }
    }

    @Override
    public void onGameStart() {
        if (!isTimerRunning) {
            if (lastTime != 0) {
                lastTimeView.setText("Last time: " + formatTime(lastTime));
            }
            startTime = SystemClock.elapsedRealtime();
            timerHandler.postDelayed(timerRunnable, 0);
            isTimerRunning = true;
        }
    }

    @Override
    public void onGameFinish() {
        if (isTimerRunning) {
            timerHandler.removeCallbacks(timerRunnable);
            lastTime = SystemClock.elapsedRealtime() - startTime;
            lastTimeView.setText("Time: " + formatTime(lastTime));
            isTimerRunning = false;
        }
    }

    private void resetTimer() {
        timerHandler.removeCallbacks(timerRunnable);
        isTimerRunning = false;
        timerView.setText("00:00:000");
    }

    private String formatTime(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long milliseconds = millis % 1000;
        return String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);
    }
}
