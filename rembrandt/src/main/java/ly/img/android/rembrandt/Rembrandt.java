package ly.img.android.rembrandt;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Short4;
import android.support.v8.renderscript.Type;

import ly.img.android.rembrandt.exceptions.UnequalBitmapConfigException;
import ly.img.android.rembrandt.exceptions.UnequalBitmapSizesException;

/**
 * Created by winklerrr on 13/12/2016.
 */

public class Rembrandt {

    private RembrandtCompareOptions compareOptions;
    private RenderScript renderScript;
    private ScriptC_rembrandt rembrandtRenderScript;

    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap comparisonBitmap;
    private Bitmap.Config config;
    private int width;
    private int height;

    private Allocation allocationBitmap1;
    private Allocation allocationBitmap2;
    private Allocation allocationComparisonBitmap;
    private Short4 colorEquality;
    private Short4 colorDiversity;

    private int[] numberOfDifferentPixels = new int[1];
    private Allocation allocationDifferences;

    public Rembrandt(final Context context) {
        this(context, RembrandtCompareOptions.createDefaultOptions());
    }

    public Rembrandt(final Context context, final RembrandtCompareOptions compareOptions) {
        this.renderScript = RenderScript.create(context);
        this.compareOptions = compareOptions;
    }

    public RembrandtComparisonResult compareBitmaps(final Bitmap bitmap1, final Bitmap bitmap2) {
        this.bitmap1 = bitmap1;
        this.bitmap2 = bitmap2;

        prepareComparison();
        compare();
        return createComparisonResult();
    }

    private void prepareComparison() {
        createComparisonBitmap();
        createRembrandtRenderScript();
    }

    private void compare() {
        rembrandtRenderScript.forEach_compareBitmaps(allocationComparisonBitmap);
        allocationComparisonBitmap.syncAll(Allocation.USAGE_SCRIPT);
        allocationComparisonBitmap.copyTo(comparisonBitmap);
        allocationDifferences.copyTo(numberOfDifferentPixels);

    }

    private RembrandtComparisonResult createComparisonResult() {
        final int pixelsInTotal = width * height;
        final double percentageOfDifferentPixels = numberOfDifferentPixels[0] * 100f/ pixelsInTotal;
        final boolean bitmapsSeemToBeEqual = isPercentageOfDifferentPixelsAcceptable(percentageOfDifferentPixels);
        return new RembrandtComparisonResult(numberOfDifferentPixels[0], percentageOfDifferentPixels, comparisonBitmap, bitmapsSeemToBeEqual);
    }

    private void createComparisonBitmap() {
        readBitmapProperties();
        comparisonBitmap = Bitmap.createBitmap(width, height, config);
    }

    private void createRembrandtRenderScript() {
        createAllocations();
        initRembrandtRenderScript();
    }

    private void readBitmapProperties() {
        readBitmapConfig();
        readBitmapDimensions();
    }

    private void createAllocations() {
        allocationBitmap1 = Allocation.createFromBitmap(renderScript, bitmap1);
        allocationBitmap2 = Allocation.createFromBitmap(renderScript, bitmap2);
        allocationComparisonBitmap = Allocation.createFromBitmap(renderScript, comparisonBitmap);

        allocationDifferences = Allocation.createTyped(renderScript, Type.createX(renderScript, Element.I32(renderScript), 1));
        colorEquality = convertColorToShort4(compareOptions.getColorForEquality());
        colorDiversity = convertColorToShort4(compareOptions.getColorForDiversity());
    }

    private void initRembrandtRenderScript() {
        rembrandtRenderScript = new ScriptC_rembrandt(renderScript);

        rembrandtRenderScript.set_rsAllocationBitmap1(allocationBitmap1);
        rembrandtRenderScript.set_rsAllocationBitmap2(allocationBitmap2);
        rembrandtRenderScript.set_rsAllocationComparisonBitmap(allocationComparisonBitmap);

        rembrandtRenderScript.set_rsAllocationDifferences(allocationDifferences);
        rembrandtRenderScript.set_colorEquality(colorEquality);
        rembrandtRenderScript.set_colorDiversity(colorDiversity);

        rembrandtRenderScript.set_allowedColorDistance(compareOptions.getMaximumColorDistance());
    }

    private void readBitmapConfig() {
        if (areBitmapConfigsEqual(bitmap1, bitmap2)) {
            config = bitmap1.getConfig();
        } else {
            throw new UnequalBitmapConfigException(bitmap1, bitmap2);
        }
    }

    private void readBitmapDimensions() {
        if (areBitmapDimensionsEqual(bitmap1, bitmap2)) {
            width = bitmap1.getWidth();
            height = bitmap1.getHeight();
        } else {
            throw new UnequalBitmapSizesException(bitmap1, bitmap2);
        }
    }

    private static boolean areBitmapsComparable(final Bitmap bitmap1, final Bitmap bitmap2) {
        return areBitmapConfigsEqual(bitmap1, bitmap2) && areBitmapDimensionsEqual(bitmap1, bitmap2);
    }

    private static boolean areBitmapConfigsEqual(final Bitmap bitmap1, final Bitmap bitmap2) {
        return bitmap1.getConfig() == bitmap2.getConfig();
    }

    private static boolean areBitmapDimensionsEqual(final Bitmap bitmap1, final Bitmap bitmap2) {
         return areBitmapWidthsEqual(bitmap1, bitmap2) && areBitmapHeightsEqual(bitmap1, bitmap2) ;
    }

    private static boolean areBitmapWidthsEqual(final Bitmap bitmap1, final Bitmap bitmap2) {
        return bitmap1.getWidth() == bitmap2.getWidth();
    }

    private static boolean areBitmapHeightsEqual(final Bitmap bitmap1, final Bitmap bitmap2) {
        return bitmap1.getHeight() == bitmap2.getHeight();
    }

    private static Short4 convertColorToShort4(final int color) {
        final short red = (short) Color.red(color);
        final short green = (short) Color.green(color);
        final short blue = (short) Color.blue(color);
        final short alpha = (short) Color.alpha(color);

        // RenderScript uses RGBA for colors
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
