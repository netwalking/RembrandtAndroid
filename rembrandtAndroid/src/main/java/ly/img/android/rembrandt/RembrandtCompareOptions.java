package ly.img.android.rembrandt;

import android.graphics.Color;

/**
 * Created by winklerrr on 13/12/2016.
 */

public class RembrandtCompareOptions {

    public static final double DEFAULT_MAXIMUM_COLOR_DISTANCE = 25.0f;
    public static final double DEFAULT_MAXIMUM_PERCENTAGE_OF_DIFFERENT_PIXELS = 1.0f;
    public static final int DEFAULT_COLOR_BITMAP_PIXEL_EQUAL = Color.WHITE;
    public static final int DEFAULT_COLOR_BITMAP_PIXEL_DIFFERENT = Color.BLACK;

    private final double maximumColorDistance;
    private final double maximumPercentageOfDifferentPixels;
    private final int colorBitmapPixelEqual;
    private final int colorBitmapPixelDifferent;

    public RembrandtCompareOptions(final double maximumColorDistance, final double maximumPercentageDifference, final int colorBitmapPixelEqual, final int colorBitmapPixelDifferent) {
        this.maximumColorDistance = maximumColorDistance;
        this.maximumPercentageOfDifferentPixels = maximumPercentageDifference;
        this.colorBitmapPixelEqual = colorBitmapPixelEqual;
        this.colorBitmapPixelDifferent = colorBitmapPixelDifferent;
    }

    public static RembrandtCompareOptions createDefaultOptions() {
        return new RembrandtCompareOptions(DEFAULT_MAXIMUM_COLOR_DISTANCE,
                DEFAULT_MAXIMUM_PERCENTAGE_OF_DIFFERENT_PIXELS,
                DEFAULT_COLOR_BITMAP_PIXEL_EQUAL,
                DEFAULT_COLOR_BITMAP_PIXEL_DIFFERENT);
    }

    public double getMaximumColorDistance() {
        return maximumColorDistance;
    }

    public double getMaximumPercentageOfDifferentPixels() {
        return maximumPercentageOfDifferentPixels;
    }

    public int getColorBitmapPixelEqual() {
        return colorBitmapPixelEqual;
    }

    public int getColorBitmapPixelDifferent() {
        return colorBitmapPixelDifferent;
    }
}
