package ly.img.android.rembrandt.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v8.renderscript.Short4;

import java.io.File;

/**
 * Created by winklerrr on 23/01/2017.
 */

public class BitmapHelper {

    public static boolean areBitmapsComparable(final Bitmap bitmap1, final Bitmap bitmap2) {
        return areBitmapConfigsEqual(bitmap1, bitmap2) && areBitmapDimensionsEqual(bitmap1, bitmap2);
    }

    public static boolean areBitmapConfigsEqual(final Bitmap bitmap1, final Bitmap bitmap2) {
        return bitmap1.getConfig() == bitmap2.getConfig();
    }

    public static boolean areBitmapDimensionsEqual(final Bitmap bitmap1, final Bitmap bitmap2) {
        return areBitmapWidthsEqual(bitmap1, bitmap2) && areBitmapHeightsEqual(bitmap1, bitmap2);
    }

    public static boolean areBitmapWidthsEqual(final Bitmap bitmap1, final Bitmap bitmap2) {
        return bitmap1.getWidth() == bitmap2.getWidth();
    }

    public static boolean areBitmapHeightsEqual(final Bitmap bitmap1, final Bitmap bitmap2) {
        return bitmap1.getHeight() == bitmap2.getHeight();
    }

    public static Short4 convertColorToShort4(final int color) {
        final short red = (short) Color.red(color);
        final short green = (short) Color.green(color);
        final short blue = (short) Color.blue(color);
        final short alpha = (short) Color.alpha(color);

        return new Short4(red, green, blue, alpha);
    }

    public static Bitmap scaleDownImageIfNeeded(Bitmap bitmap, int width, int height) {
        if (bitmap.getWidth() <= width && bitmap.getHeight() <= height) {
            return bitmap;
        } else {
            return Bitmap.createScaledBitmap(bitmap, width, height, true);
        }
    }

    public static int[] getSize(File file) {
        final BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
        return new int[]{opts.outWidth, opts.outHeight};
    }

    public static Bitmap loadScaled(File file, int width, int height) {
        final BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

        if (Math.abs(opts.outWidth / (float) opts.outHeight - width / (float) height) > 0.001) {
            return null;
        } else {
            final int size = Math.min(opts.outWidth, opts.outHeight);
            final int minSize = Math.min(width, height);
            if (size > minSize && minSize > 0) {
                opts.inSampleSize = size / minSize;
            } else {
                opts.inSampleSize = 1;
            }

            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            opts.inJustDecodeBounds = false;

            Bitmap
              result = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
            result = Bitmap.createScaledBitmap(result, width, height, true);
            result = set32BitColorIfNeeded(result);

            return result;
        }
    }

    public static Bitmap set32BitColorIfNeeded(Bitmap bitmap) {
        if (bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
            return bitmap;
        } else {
            return bitmap.copy(Bitmap.Config.ARGB_8888, true);
        }

    }
}
