package helloandroid.ut3.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

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
}