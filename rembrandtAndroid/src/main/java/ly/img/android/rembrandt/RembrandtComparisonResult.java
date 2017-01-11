package ly.img.android.rembrandt;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by winklerrr on 13/12/2016.
 */

public class RembrandtComparisonResult {

    private final long differentPixels;
    private final double percentageOfDifferentPixels;
    private final Bitmap comparisionBitmap;
    private final boolean bitmapsEqual;

    public RembrandtComparisonResult(final long differentPixels, final double percentageOfDifferentPixels, final Bitmap comparisionBitmap, final boolean bitmapsEqual) {
        this.differentPixels = differentPixels;
        this.percentageOfDifferentPixels = percentageOfDifferentPixels;
        this.comparisionBitmap = comparisionBitmap;
        this.bitmapsEqual = bitmapsEqual;
    }

    public long getDifferentPixels() {
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