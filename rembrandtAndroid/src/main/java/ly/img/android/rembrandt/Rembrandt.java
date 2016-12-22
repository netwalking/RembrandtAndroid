package ly.img.android.rembrandt;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Short4;
import android.util.Log;

/**
 * Created by winklerrr on 13/12/2016.
 */

public class Rembrandt {

    private static Application context;
    private RembrandtCompareOptions compareOptions;
    private RenderScript renderScript;

    public Rembrandt() {
        this(RembrandtCompareOptions.createDefaultOptions());
    }

    public Rembrandt(final RembrandtCompareOptions compareOptions) {
        if (context == null) {
            throw new NullPointerException("Context is null. Forgot to call init?");
        }

        this.compareOptions = compareOptions;
        renderScript = RenderScript.create(context);
    }

    public static void init(final Application context) {
        Rembrandt.context = context;
    }

    public synchronized RembrandtCompareResult compareBitmaps(final Bitmap bitmap1,
                                                              final Bitmap bitmap2) {
        final Allocation allocationBitmap1 = Allocation.createFromBitmap(renderScript, bitmap1);
        final Allocation allocationBitmap2 = Allocation.createFromBitmap(renderScript, bitmap2);

        final int width = bitmap1.getWidth();
        final int height = bitmap1.getHeight();
        final Bitmap.Config config = bitmap1.getConfig();

        final Bitmap comparisonBitmap = Bitmap.createBitmap(width, height, config);
        final Allocation allocationComparisonBitmap =
                Allocation.createFromBitmap(renderScript, comparisonBitmap);

        ScriptC_Rembrandt rembrandt = new ScriptC_Rembrandt(renderScript);
        rembrandt.set_bitmap1(allocationBitmap1);
        rembrandt.set_bitmap2(allocationBitmap2);
        rembrandt.set_comparison_bitmap(allocationComparisonBitmap);
        rembrandt.set_allowed_color_distance(compareOptions.getMaximumColorDistance());
        rembrandt.set_different_pixels(0);

        Short4 colorEquality = convertColor(compareOptions.getColorForEquality());
        Short4 colorDiversity = convertColor(compareOptions.getColorForDiversity());

        rembrandt.set_color_equality(colorEquality);
        rembrandt.set_color_diversity(colorDiversity);
        rembrandt.forEach_compareBitmaps(allocationComparisonBitmap);

        allocationComparisonBitmap.syncAll(Allocation.USAGE_SCRIPT);
        allocationComparisonBitmap.copyTo(comparisonBitmap);

        // TODO: get number of different pixels from renderscript
        final int pixelsInTotal = width * height;
        Log.i("here", "here");
        long differentPixels = rembrandt.get_different_pixels();
        final double percentageOfDifferentPixels = differentPixels / pixelsInTotal * 100.0f;
        boolean bitmapsSeemToBeEqual = isPercentageOfDifferentPixelsAcceptable(percentageOfDifferentPixels);
        return new RembrandtCompareResult(differentPixels, percentageOfDifferentPixels, comparisonBitmap, bitmapsSeemToBeEqual);
    }

    private static Short4 convertColor(final int color) {
        final short red = (short) Color.red(color);
        final short green = (short) Color.green(color);
        final short blue = (short) Color.blue(color);
        final short alpha = (short) Color.alpha(color);

        return new Short4(red, green, blue, alpha);
    }

    private boolean isPercentageOfDifferentPixelsAcceptable(final double percentageOfDifferentPixels) {
        return percentageOfDifferentPixels <= compareOptions.getMaximumPercentageOfDifferentPixels();
    }

    public RembrandtCompareOptions getCompareOptions() {
        return compareOptions;
    }

    public void setCompareOptions(RembrandtCompareOptions compareOptions) {
        this.compareOptions = compareOptions;
    }
}
