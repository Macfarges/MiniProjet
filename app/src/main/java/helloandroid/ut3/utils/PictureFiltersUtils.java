package helloandroid.ut3.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class PictureFiltersUtils {

    private static final ColorMatrix colorMatrix = new ColorMatrix();
    private static final Paint paint = new Paint();
    private static final Canvas canvas = new Canvas();

    public static void applyFilter(ImageView imageView, float effectLevel, FilterType filterType) {
        // Get the drawable from the ImageView
        Drawable originalDrawable = imageView.getDrawable();

        if (originalDrawable instanceof BitmapDrawable) {
            Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap().copy(Bitmap.Config.ARGB_8888, true);

            // Apply the selected filter to the original image
            applyFilterInternal(originalBitmap, effectLevel, filterType);

            // Set the filtered bitmap to the ImageView
            imageView.setImageBitmap(originalBitmap);
        }
    }

    private static void applyFilterInternal(Bitmap source, float effectLevel, FilterType filterType) {
        // Reuse existing Canvas
        canvas.setBitmap(source);
        // Reset paint and color matrix
        switch (filterType) {
            case DANK:
                applyDankFilter(source, effectLevel);
                break;

            case RANDOM_COLOR:
                applyRandomColorFilter(source, effectLevel);
                break;

            // Add more cases for other filter types if needed

            default:
                // No filter applied
                break;
        }
    }

    private static void applyDankFilter(Bitmap resultBitmap, float effectLevel) {
        // Adjust saturation
        colorMatrix.reset();
        colorMatrix.setSaturation(effectLevel);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(resultBitmap, 0, 0, paint);

        // Adjust contrast
        colorMatrix.set(new float[]{
                2f, 0f, 0f, 0f, 0f,
                0f, 2f, 0f, 0f, 0f,
                0f, 0f, 2f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(resultBitmap, 0, 0, paint);

        // Adjust brightness
        colorMatrix.postConcat(new ColorMatrix(new float[]{
                1f, 0f, 0f, 0f, effectLevel,
                0f, 1f, 0f, 0f, effectLevel,
                0f, 0f, 1f, 0f, effectLevel,
                0f, 0f, 0f, 1f, 0f
        }));
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(resultBitmap, 0, 0, paint);
    }

    private static void applyRandomColorFilter(Bitmap resultBitmap, float effectLevel) {
        // Ensure effectLevel is within a valid range (0 to 100)
        effectLevel = Math.max(0, Math.min(100, effectLevel));

        // Calculate the swap ratios based on the clamped effectLevel
        float swapRatioRed = 1.0f - (effectLevel / 100.0f);
        float swapRatioGreen = 0.5f + 0.5f * (effectLevel / 100.0f);
        float swapRatioBlue = 0.2f + 0.8f * (effectLevel / 100.0f);

        // Adjust the swap ratios to ensure a minimum level of swapping
        swapRatioRed = 0.2f + swapRatioRed * 0.8f;
        swapRatioGreen = 0.2f + swapRatioGreen * 0.8f;
        swapRatioBlue = 0.2f + swapRatioBlue * 0.8f;

        // Swap Red, Green, and Blue channels based on the calculated swap ratios
        colorMatrix.set(new float[]{
                1 - swapRatioRed, 0f, swapRatioRed, 0f, 0f,
                0f, 1 - swapRatioGreen, swapRatioGreen, 0f, 0f,
                swapRatioBlue, 0f, 1 - swapRatioBlue, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
        });

        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);
    }

    public enum FilterType {
        DANK,
        RANDOM_COLOR
        // Add more filter types if needed
    }

}
