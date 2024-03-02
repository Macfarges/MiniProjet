// MicrophoneUtils.java

package helloandroid.ut3.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

import androidx.core.app.ActivityCompat;

public class MicrophoneUtils {
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final Handler handler = new Handler();
    private static final int YOUR_REQUEST_CODE = 15;
    private static boolean isMonitoring = false;
    private static AudioRecord audioRecord;
    private static MicrophoneCallback microphoneCallback;
    private static final Runnable audioMonitoringRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMonitoring) {
                short[] audioBuffer = new short[1024];
                int bytesRead = audioRecord.read(audioBuffer, 0, audioBuffer.length);

                if (bytesRead > 0) {
                    float volumeLevel = FileUtils.calculateVolumeLevel(audioBuffer, bytesRead);
                    if (microphoneCallback != null) {
                        microphoneCallback.onVolumeLevelChanged(volumeLevel);
                    }
                }

                handler.postDelayed(this, 0);
            }
        }
    };

    public static void setMicrophoneCallback(MicrophoneCallback callback) {
        microphoneCallback = callback;
    }

    public static void startRecording(Context context, Activity activity) {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, YOUR_REQUEST_CODE);
        }

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
        );
        isMonitoring = true;
        audioRecord.startRecording();
        handler.postDelayed(audioMonitoringRunnable, 0);
    }

    public static void stopRecording(StopRecordingCallback callback) {
        isMonitoring = false;

        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }

        handler.removeCallbacksAndMessages(null);
        // Introduce a small delay before applying the filter
        handler.postDelayed(() -> {

            // Notify the callback that recording has stopped
            if (callback != null) {
                callback.onRecordingStopped();
            }
        }, 100); // Adjust the delay time as needed
    }

    public interface StopRecordingCallback {
        void onRecordingStopped();
    }

    public interface MicrophoneCallback {
        void onVolumeLevelChanged(float volumeLevel);
    }
}
