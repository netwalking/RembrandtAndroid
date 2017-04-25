package ly.img.android.rembrandt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Type;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ly.img.android.rembrandt.exceptions.BitmapsUncomparableException;
import ly.img.android.rembrandt.helpers.BitmapHelper;

import static ly.img.android.rembrandt.helpers.BitmapHelper.areBitmapsComparable;
import static ly.img.android.rembrandt.helpers.BitmapHelper.scaleDownImageIfNeeded;
import static ly.img.android.rembrandt.helpers.BitmapHelper.set32BitColorIfNeeded;

/**
 * Created by winklerrr on 13/12/2016.
 */

public class Rembrandt {

    private final RembrandtCompareOptions compareOptions;
    private final RenderScript renderScript;

    private ScriptC_rembrandt rembrandtRenderScript;

    private boolean createVisualComapareResult = true;

    private Lock lock = new ReentrantLock();

    private int[]
      numberOfDifferentPixels = new int[1];


    public Rembrandt(final Context context) {
        this(context, RembrandtCompareOptions.createDefaultOptions());
    }

    public Rembrandt(final Context context, final RembrandtCompareOptions compareOptions) {
        this.renderScript = RenderScript.create(context);
        this.compareOptions = compareOptions;
        this.rembrandtRenderScript = new ScriptC_rembrandt(renderScript);
    }

    public RembrandtComparisonResult compareBitmaps(Bitmap bitmap1, Bitmap bitmap2, boolean allowDifferentConfig) {
        if (allowDifferentConfig) {
            bitmap1 = scaleDownImageIfNeeded(bitmap1, bitmap2.getWidth(), bitmap2.getHeight());
            bitmap2 = scaleDownImageIfNeeded(bitmap2, bitmap1.getWidth(), bitmap1.getHeight());

            bitmap1 = set32BitColorIfNeeded(bitmap1);
            bitmap2 = set32BitColorIfNeeded(bitmap2);
        } else if (!areBitmapsComparable(bitmap1, bitmap2)) {
            throw new BitmapsUncomparableException(bitmap1, bitmap2);
        }

        return compare(bitmap1, bitmap2);
    }

    public RembrandtComparisonResult compareBitmapWithFile(final Bitmap bitmap, final File imageFile, boolean allowDifferentConfig) {
        Bitmap bitmap1, bitmap2;
        int[] size = BitmapHelper.getSize(imageFile);

        if (allowDifferentConfig) {
            bitmap1 = scaleDownImageIfNeeded(bitmap, size[0], size[1]);
            bitmap1 = set32BitColorIfNeeded(bitmap1);
        } else if (size[0] != bitmap.getWidth() || size[1] != bitmap.getHeight()) {
            throw new BitmapsUncomparableException();
        } else {
            bitmap1 = bitmap;
        }

        bitmap2 = BitmapHelper.loadScaled(imageFile, bitmap1.getWidth(), bitmap1.getHeight());

        return compareBitmaps(bitmap1, bitmap2, false);
    }

    public RembrandtComparisonResult compareFiles(final File imageFile1, final File imageFile2, boolean allowDifferentConfig) {
        int[] size1 = BitmapHelper.getSize(imageFile1);
        int[] size2 = BitmapHelper.getSize(imageFile2);

        int compareWidth = size1[0];
        int compareHeight = size1[1];

        if (allowDifferentConfig) {
            compareWidth = Math.min(size1[0], size2[0]);
            compareHeight = Math.min(size1[1], size2[1]);
        } else if (size1[0] != size2[0] || size1[1] != size2[1]) {
            throw new BitmapsUncomparableException();
        }

        Bitmap bitmap1 = BitmapHelper.loadScaled(imageFile1, compareWidth, compareHeight);
        Bitmap bitmap2 = BitmapHelper.loadScaled(imageFile2, compareWidth, compareHeight);
        return compareBitmaps(bitmap1, bitmap2, false);
    }


    private RembrandtComparisonResult compare(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap comparisonBitmap = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());

        // Create allocations
        Allocation allocationBitmap1 = Allocation.createFromBitmap(renderScript, bitmap1);
        Allocation allocationBitmap2 = Allocation.createFromBitmap(renderScript, bitmap2);
        Allocation allocationDifferencesCount = Allocation.createTyped(renderScript, Type.createX(renderScript, Element.I32(renderScript), 1));
        Allocation allocationComparisonBitmap = null;

        // Set allocations
        rembrandtRenderScript.set_rsAllocationBitmap1(allocationBitmap1);
        rembrandtRenderScript.set_rsAllocationBitmap2(allocationBitmap2);
        rembrandtRenderScript.set_rsAllocationDifferencesCount(allocationDifferencesCount);

        // Create and set visible result allocation if needed.
        if (compareOptions.isCreateVisualResult()) {
            allocationComparisonBitmap = Allocation.createFromBitmap(renderScript, comparisonBitmap);
            rembrandtRenderScript.set_rsAllocationComparisonBitmap(allocationComparisonBitmap);

            // Set color settings
            rembrandtRenderScript.set_colorEquality(compareOptions.getColorForEquality());
            rembrandtRenderScript.set_colorDiversity(compareOptions.getColorForDiversity());
        }

        rembrandtRenderScript.set_allowedColorDistance(compareOptions.getMaximumColorDistance());

        // Compare and get lock to prevent concurrent use of the renderscript part.
        lock.lock();

        if (compareOptions.isCreateVisualResult()) {
            rembrandtRenderScript.forEach_compareBitmaps(allocationBitmap1);
            allocationComparisonBitmap.copyTo(comparisonBitmap);
        }else{
            rembrandtRenderScript.forEach_compareBitmapsResultOnly(allocationBitmap1);
        }


        allocationDifferencesCount.copyTo(numberOfDifferentPixels);

        final int pixelsInTotal = bitmap1.getWidth() * bitmap1.getHeight();
        final double percentageOfDifferentPixels = numberOfDifferentPixels[0] * 100f / pixelsInTotal;

        // release lock
        lock.unlock();

        final boolean bitmapsSeemToBeEqual = isPercentageOfDifferentPixelsAcceptable(percentageOfDifferentPixels);

        return new RembrandtComparisonResult(
          numberOfDifferentPixels[0],
          percentageOfDifferentPixels,
          comparisonBitmap,
          bitmapsSeemToBeEqual
        );
    }

    private boolean isPercentageOfDifferentPixelsAcceptable(final double percentageOfDifferentPixels) {
        return percentageOfDifferentPixels <= compareOptions.getMaximumPercentageOfDifferentPixels();
    }

    public RembrandtCompareOptions getCompareOptions() {
        return compareOptions;
    }
}
