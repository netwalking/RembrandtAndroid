package ly.img.android.rembrandt;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Type;

import ly.img.android.rembrandt.exceptions.BitmapsUncomparableException;

import static ly.img.android.rembrandt.helpers.BitmapHelper.areBitmapsComparable;

/**
 * Created by winklerrr on 13/12/2016.
 */

public class Rembrandt {

    /**
     * Tag for TimmingLogger. Log needs to be activated through:
     * adb shell setprop log.tag.Rembrandt VERBOSE
     */
    private static final String TAG = "Rembrandt";

    private final RembrandtCompareOptions compareOptions;
    private final RenderScript renderScript;

    private ScriptC_rembrandt rembrandtRenderScript;

    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap comparisonBitmap;

    private int[] numberOfDifferentPixels = new int[1];

    private Allocation allocationBitmap1;
    private Allocation allocationBitmap2;
    private Allocation allocationComparisonBitmap;
    private Allocation allocationDifferences;

    public Rembrandt(final Context context) {
        this(context, RembrandtCompareOptions.createDefaultOptions());
    }

    public Rembrandt(final Context context, final RembrandtCompareOptions compareOptions) {
        this.renderScript = RenderScript.create(context);
        this.compareOptions = compareOptions;
    }

    public RembrandtComparisonResult compareBitmaps(final Bitmap bitmap1, final Bitmap bitmap2) {
        if (areBitmapsComparable(bitmap1, bitmap2)) {
            this.bitmap1 = bitmap1;
            this.bitmap2 = bitmap2;
            return compare();
        } else {
            throw new BitmapsUncomparableException(bitmap1, bitmap2);
        }
    }

    private RembrandtComparisonResult compare() {
        comparisonBitmap = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());

        createAllocations();
        initRenderScript();
        runRenderScript();

        final int pixelsInTotal = bitmap1.getWidth() * bitmap1.getHeight();
        final double percentageOfDifferentPixels = numberOfDifferentPixels[0] * 100f / pixelsInTotal;
        final boolean bitmapsSeemToBeEqual = isPercentageOfDifferentPixelsAcceptable(percentageOfDifferentPixels);
        return new RembrandtComparisonResult(numberOfDifferentPixels[0], percentageOfDifferentPixels, comparisonBitmap, bitmapsSeemToBeEqual);
    }

    private void createAllocations() {
        allocationBitmap1 = Allocation.createFromBitmap(renderScript, bitmap1);
        allocationBitmap2 = Allocation.createFromBitmap(renderScript, bitmap2);
        allocationComparisonBitmap = Allocation.createFromBitmap(renderScript, comparisonBitmap);
        allocationDifferences = Allocation.createTyped(renderScript, Type.createX(renderScript, Element.I32(renderScript), 1));
    }

    private void initRenderScript() {
        rembrandtRenderScript = new ScriptC_rembrandt(renderScript);

        rembrandtRenderScript.set_rsAllocationBitmap1(allocationBitmap1);
        rembrandtRenderScript.set_rsAllocationBitmap2(allocationBitmap2);
        rembrandtRenderScript.set_rsAllocationComparisonBitmap(allocationComparisonBitmap);
        rembrandtRenderScript.set_rsAllocationDifferences(allocationDifferences);

        rembrandtRenderScript.set_colorEquality(compareOptions.getColorForEquality());
        rembrandtRenderScript.set_colorDiversity(compareOptions.getColorForDiversity());
        rembrandtRenderScript.set_allowedColorDistance(compareOptions.getMaximumColorDistance());
    }

    private void runRenderScript() {
        rembrandtRenderScript.forEach_compareBitmaps(allocationComparisonBitmap);
        allocationComparisonBitmap.syncAll(Allocation.USAGE_SCRIPT);
        allocationComparisonBitmap.copyTo(comparisonBitmap);
        allocationDifferences.copyTo(numberOfDifferentPixels);
    }

    private boolean isPercentageOfDifferentPixelsAcceptable(final double percentageOfDifferentPixels) {
        return percentageOfDifferentPixels <= compareOptions.getMaximumPercentageOfDifferentPixels();
    }

    public RembrandtCompareOptions getCompareOptions() {
        return compareOptions;
    }
}
