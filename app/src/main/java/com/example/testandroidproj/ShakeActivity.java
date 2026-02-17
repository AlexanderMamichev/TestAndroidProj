package com.example.testandroidproj;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ShakeActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ImageView dice1ImageView;
    private ImageView dice2ImageView;
    private MediaPlayer player;

    private static final float SHAKE_THRESHOLD = 15.0f;
    private long lastShakeTime = 0;
    private boolean isRolling = false;
    private final Random random = new Random();

    private final int[] diceImages = {
            R.drawable.dice_one,
            R.drawable.dice_two,
            R.drawable.dice_three,
            R.drawable.dice_four,
            R.drawable.dice_five,
            R.drawable.dice_six
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.project_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        dice1ImageView = findViewById(R.id.dice1);
        dice2ImageView = findViewById(R.id.dice2);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        // Use the correct sound file name
        player = MediaPlayer.create(this, R.raw.roll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (player.isPlaying()) {
            player.pause();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            if (!isRolling && (currentTime - lastShakeTime) > 800) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

                if (acceleration > SHAKE_THRESHOLD) {
                    lastShakeTime = currentTime;
                    startDiceRoll();
                }
            }
        }
    }

    private void startDiceRoll() {
        isRolling = true;
        if (player != null) {
            player.seekTo(0);
            player.start();
        }

        Handler handler = new Handler();
        // Animate the dice for 1 second
        for (int i = 0; i < 10; i++) {
            handler.postDelayed(() -> {
                dice1ImageView.setImageResource(diceImages[random.nextInt(6)]);
                dice2ImageView.setImageResource(diceImages[random.nextInt(6)]);
            }, i * 100L);
        }

        // Show the final result after the animation
        handler.postDelayed(() -> {
            dice1ImageView.setImageResource(diceImages[random.nextInt(6)]);
            dice2ImageView.setImageResource(diceImages[random.nextInt(6)]);
            isRolling = false;
            if (player != null && player.isPlaying()) {
                player.pause();
            }
        }, 1000);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}
