package helloandroid.ut3.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

public class AccelerometerUtils {
    private static final Handler accelerometerHandler = new Handler();
    private static AccelerometerCallback accelerometerCallback;
    private static SensorManager sensorManager;
    private static Sensor accelerometerSensor;
    private static boolean isMonitoring;
    private static int orientation;
    private static final SensorEventListener accelerometerListener = new SensorEventListener() {
        private static final long MINIMUM_PROCESSING_DELAY = 50;
        private long lastProcessedTimestamp = 0;

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (isMonitoring) {
                //Fonctionne pas avec postDelayed, on doit faire ainsi...
                long currentTimestamp = System.currentTimeMillis();
                if (currentTimestamp - lastProcessedTimestamp < MINIMUM_PROCESSING_DELAY) {
                    return;
                }
                lastProcessedTimestamp = currentTimestamp;
                float acceleration = calculateAcceleration(event.values);
                accelerometerCallback.onAccelerationChanged(acceleration);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Handle accuracy changes if needed
        }
    };

    public static void setAccelerometerCallback(AccelerometerCallback callback) {
        accelerometerCallback = callback;
    }

    public static void startAccelerometer(Context context) {
        orientation = context.getResources().getConfiguration().orientation;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometerSensor != null) {
                isMonitoring = true;
                sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    public static void stopAccelerometer() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(accelerometerListener);
            sensorManager = null;
            accelerometerSensor = null;
        }
        accelerometerHandler.removeCallbacksAndMessages(null);
        isMonitoring = false;
    }

    private static float calculateAcceleration(float[] values) {// Map the acceleration value based on the orientation
        float acceleration;

        // Map X-axis acceleration to [0, 100]
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            acceleration = map(values[1], -9.8f, 9.8f, 0f, 100f);  // Map Y-axis acceleration to [0, 100]
            // Add more cases for other orientations if needed
        } else {
            acceleration = map(values[0], -9.8f, 9.8f, 0f, 100f);  // Default to X-axis
        }

        return acceleration;
    }

    private static float map(float value, float fromSource, float toSource, float fromTarget, float toTarget) {
        return (value - fromSource) / (toSource - fromSource) * (toTarget - fromTarget) + fromTarget;
    }

    public interface AccelerometerCallback {
        void onAccelerationChanged(float acceleration);
    }
}
