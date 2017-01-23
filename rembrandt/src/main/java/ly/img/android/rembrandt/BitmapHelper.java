package ly.img.android.rembrandt;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v8.renderscript.Short4;

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
        return areBitmapWidthsEqual(bitmap1, bitmap2) && areBitmapHeightsEqual(bitmap1, bitmap2) ;
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

}
