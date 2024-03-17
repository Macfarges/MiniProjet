package helloandroid.ut3.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Pair;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

public class FileUtils {


    public static long getFileSizeFromUri(Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        try {
            InputStream inputStream = resolver.openInputStream(uri);
            if (inputStream != null) {
                long size = inputStream.available();
                inputStream.close();
                return size;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Uri saveBitmapToFile(Context context, Bitmap bitmap) {
        ContentResolver resolver = context.getContentResolver();

        // Create a file to save the image
        String fileName = "image_" + new Date().getTime() + ".jpg";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {
            if (imageUri != null) {
                OutputStream outputStream = resolver.openOutputStream(imageUri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                    return imageUri;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static float calculateVolumeLevel(short[] audioBuffer, int bytesRead) {
        // This is a basic example; you may need more sophisticated calculations
        long sum = 0;

        for (int i = 0; i < bytesRead; i++) {
            sum += Math.abs(audioBuffer[i]);
        }

        // Calculate the average amplitude
        float averageAmplitude = (float) sum / bytesRead;

        // You may further process averageAmplitude as needed
        return averageAmplitude;
    }

    public static Bitmap getBitmapFromImageView(ImageView pictureView) {
        Drawable drawable = pictureView.getDrawable();

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            // If the drawable is not a BitmapDrawable, create a new Bitmap
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    public static void applyStickersToImageView(ImageView pictureView, List<Pair<Drawable, float[]>> stickersList) {
        // Get the current Drawable of the pictureView
        Drawable currentDrawable = pictureView.getDrawable();

        // Create a new Bitmap and Canvas
        Bitmap bitmap = Bitmap.createBitmap(currentDrawable.getIntrinsicWidth(), currentDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the current Drawable onto the canvas
        currentDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        currentDrawable.draw(canvas);

        // Draw the stickers onto the canvas
        for (Pair<Drawable, float[]> stickerPair : stickersList) {
            Drawable stickerDrawable = stickerPair.first;
            float[] coordinates = stickerPair.second;

            // Create a new matrix for each sticker
            Matrix matrix = new Matrix();

            // Translate the matrix to the specified coordinates
            matrix.postTranslate(coordinates[0], coordinates[1]);

            // Apply the matrix to the canvas
            canvas.save();
            canvas.concat(matrix);

            // Draw the sticker onto the canvas
            stickerDrawable.setBounds(0, 0, stickerDrawable.getIntrinsicWidth(), stickerDrawable.getIntrinsicHeight());
            stickerDrawable.draw(canvas);

            // Restore the original canvas state
            canvas.restore();
        }

        // Set the modified bitmap to the pictureView
        pictureView.setImageBitmap(bitmap);
    }
}

