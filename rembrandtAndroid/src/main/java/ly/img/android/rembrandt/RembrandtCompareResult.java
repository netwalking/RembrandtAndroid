package ly.img.android.rembrandt;

import android.graphics.Bitmap;

/**
 * Created by winklerrr on 13/12/2016.
 */

public class RembrandtCompareResult {

    private final int differentPixels;
    private final double percentageOfDifferentPixels;
    private final Bitmap comparisionBitmap;
    private final boolean bitmapsEqual;

    public RembrandtCompareResult(final int differentPixels, final double percentageOfDifferentPixels, final Bitmap comparisionBitmap, final boolean bitmapsEqual) {
        this.differentPixels = differentPixels;
        this.percentageOfDifferentPixels = percentageOfDifferentPixels;
        this.comparisionBitmap = comparisionBitmap;
        this.bitmapsEqual = bitmapsEqual;
    }

    public int getDifferentPixels() {
        return differentPixels;
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
