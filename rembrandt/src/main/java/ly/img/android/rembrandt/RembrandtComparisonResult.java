package ly.img.android.rembrandt;

import android.graphics.Bitmap;

/**
 * Created by winklerrr on 13/12/2016.
 */

public class RembrandtComparisonResult {

    private final int numberOfDifferentPixels;
    private final double percentageOfDifferentPixels;
    private final Bitmap comparisionBitmap;
    private final boolean bitmapsEqual;

    public RembrandtComparisonResult(final int numberOfDifferentPixels, final double percentageOfDifferentPixels, final Bitmap comparisionBitmap, final boolean bitmapsEqual) {
        this.numberOfDifferentPixels = numberOfDifferentPixels;
        this.percentageOfDifferentPixels = percentageOfDifferentPixels;
        this.comparisionBitmap = comparisionBitmap;
        this.bitmapsEqual = bitmapsEqual;
    }

    public long getNumberOfDifferentPixels() {
        return numberOfDifferentPixels;
    }

    public double getPercentageOfDifferentPixels() {
        return percentageOfDifferentPixels;
    }

    public Bitmap getComparisionBitmap() {
        return comparisionBitmap;
    }

    public boolean areBitmapsEqual() {
        return bitmapsEqual;
    }

}