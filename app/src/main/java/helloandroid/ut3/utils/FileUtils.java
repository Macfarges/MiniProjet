package helloandroid.ut3.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

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

    //Source : https://xjaphx.wordpress.com/learning/tutorials/
    public static Bitmap applyDankFilter(Bitmap source, float effectLevel) {
        Bitmap resultBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();

        ColorMatrix colorMatrix = new ColorMatrix();

        // Adjust saturation
        colorMatrix.setSaturation(effectLevel);

        // Adjust contrast
        colorMatrix.set(new float[]{
                2f, 0f, 0f, 0f, 0f,
                0f, 2f, 0f, 0f, 0f,
                0f, 0f, 2f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
        });

        // Adjust brightness
        colorMatrix.postConcat(new ColorMatrix(new float[]{
                1f, 0f, 0f, 0f, effectLevel,
                0f, 1f, 0f, 0f, effectLevel,
                0f, 0f, 1f, 0f, effectLevel,
                0f, 0f, 0f, 1f, 0f
        }));

        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(source, 0, 0, paint);

        return resultBitmap;
    }

    public static Bitmap applyRandomColorFilter(Bitmap source, float intensity) {
        Bitmap resultBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[]{
                0.393f + 0.607f * intensity, 0.769f - 0.769f * intensity, 0.189f - 0.189f * intensity, 0, 0,
                0.349f - 0.349f * intensity, 0.686f + 0.314f * intensity, 0.168f - 0.168f * intensity, 0, 0,
                0.272f - 0.272f * intensity, 0.534f - 0.534f * intensity, 0.131f + 0.869f * intensity, 0, 0,
                0, 0, 0, 1, 0
        });

        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(source, 0, 0, paint);

        return resultBitmap;
    }

    public static Bitmap getBitmapFromImageView(ImageView imageView) {
        Bitmap bitmap = Bitmap.createBitmap(
                imageView.getWidth(),
                imageView.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        imageView.draw(canvas);
        return bitmap;
    }

}

