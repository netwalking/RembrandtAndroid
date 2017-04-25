package ly.img.android.rembrandt;

import android.graphics.Color;
import android.support.v8.renderscript.Short4;

import ly.img.android.rembrandt.helpers.BitmapHelper;

import static ly.img.android.rembrandt.helpers.BitmapHelper.*;

/**
 * Created by winklerrr on 13/12/2016.
 */

public class RembrandtCompareOptions {

    public static final float DEFAULT_MAXIMUM_COLOR_DISTANCE = 25.0f;
    public static final float DEFAULT_MAXIMUM_PERCENTAGE_OF_DIFFERENT_PIXELS = 1.0f;
    public static final int DEFAULT_COLOR_BITMAP_PIXEL_EQUAL = Color.TRANSPARENT;
    public static final int DEFAULT_COLOR_BITMAP_PIXEL_DIFFERENT = Color.RED;

    private final float maximumColorDistance;
    private final float maximumPercentageOfDifferentPixels;
    private final Short4 colorForEquality;
    private final Short4 colorForDiversity;

    private final boolean createVisualResult;

    public RembrandtCompareOptions(final float maximumColorDistance, final float maximumPercentageDifference, final int colorForEquality, final int colorForDiversity, final boolean createVisualResult) {
        this.maximumColorDistance = maximumColorDistance;
        this.maximumPercentageOfDifferentPixels = maximumPercentageDifference;
        this.colorForEquality = convertColorToShort4(colorForEquality);
        this.colorForDiversity = convertColorToShort4(colorForDiversity);
        this.createVisualResult = createVisualResult;
    }

    public static RembrandtCompareOptions createDefaultOptions() {
        return new RembrandtCompareOptions(
          DEFAULT_MAXIMUM_COLOR_DISTANCE,
          DEFAULT_MAXIMUM_PERCENTAGE_OF_DIFFERENT_PIXELS,
          DEFAULT_COLOR_BITMAP_PIXEL_EQUAL,
          DEFAULT_COLOR_BITMAP_PIXEL_DIFFERENT,
          true
        );
    }

    public boolean isCreateVisualResult() {
        return createVisualResult;
    }

    public float getMaximumColorDistance() {
        return maximumColorDistance;
    }

    public float getMaximumPercentageOfDifferentPixels() {
        return maximumPercentageOfDifferentPixels;
    }

    public Short4 getColorForEquality() {
        return colorForEquality;
    }


    public Short4 getColorForDiversity() {
        return colorForDiversity;
    }
}
