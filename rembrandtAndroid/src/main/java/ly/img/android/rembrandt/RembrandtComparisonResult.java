package ly.img.android.rembrandt;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by winklerrr on 13/12/2016.
 */

public class RembrandtComparisonResult implements Parcelable {

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

    protected RembrandtComparisonResult(Parcel in) {
        differentPixels = in.readLong();
        percentageOfDifferentPixels = in.readDouble();
        comparisionBitmap = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        bitmapsEqual = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(differentPixels);
        dest.writeDouble(percentageOfDifferentPixels);
        dest.writeValue(comparisionBitmap);
        dest.writeByte((byte) (bitmapsEqual ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RembrandtComparisonResult> CREATOR = new Parcelable.Creator<RembrandtComparisonResult>() {
        @Override
        public RembrandtComparisonResult createFromParcel(Parcel in) {
            return new RembrandtComparisonResult(in);
        }

        @Override
        public RembrandtComparisonResult[] newArray(int size) {
            return new RembrandtComparisonResult[size];
        }
    };
}