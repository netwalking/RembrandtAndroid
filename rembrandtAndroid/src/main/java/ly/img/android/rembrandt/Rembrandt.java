package ly.img.android.rembrandt;

import android.graphics.Bitmap;
import android.graphics.Color;

import ly.img.android.rembrandt.exceptions.UnequalBitmapDimensionsException;

/**
 * Created by winklerrr on 13/12/2016.
 */

public class Rembrandt {

    private RembrandtCompareOptions compareOptions;

    public Rembrandt() {
        this.compareOptions = RembrandtCompareOptions.createDefaultOptions();
    }

    public Rembrandt(final RembrandtCompareOptions compareOptions) {
        this.compareOptions = compareOptions;
    }

    public RembrandtCompareResult compareBitmaps(final Bitmap bitmap1, final Bitmap bitmap2) {
        if (!areBitmapDimensionsEqual(bitmap1, bitmap2)) {
            throw new UnequalBitmapDimensionsException(bitmap1, bitmap2);
        }

        final int height = bitmap1.getHeight();
        final int width = bitmap1.getWidth();

        int differentPixels = 0;
        final Bitmap comparisonBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (areBitmapPixelsEqualAt(bitmap1, bitmap2, x, y)) {
                    comparisonBitmap.setPixel(x, y, compareOptions.getColorBitmapPixelEqual());
                } else {
                    boolean test = areBitmapPixelsEqualAt(bitmap1, bitmap2, x, y);
                    differentPixels++;
                    comparisonBitmap.setPixel(x, y, compareOptions.getColorBitmapPixelDifferent());
                }
            }
        }

        final int pixelsInTotal = height * width;
        final double percentageOfDifferentPixels = differentPixels / pixelsInTotal * 100.0f;
        boolean bitmapsEqual = isPercentageOfDifferentPixelsAcceptable(percentageOfDifferentPixels);
        return new RembrandtCompareResult(differentPixels, percentageOfDifferentPixels, comparisonBitmap, bitmapsEqual);
    }

    private static boolean areBitmapDimensionsEqual(final Bitmap bitmap1, final Bitmap bitmap2) {
        return bitmap1.getHeight() == bitmap2.getHeight() && bitmap1.getWidth() == bitmap2.getWidth();
    }

    private boolean areBitmapPixelsEqualAt(final Bitmap bitmap1, final Bitmap bitmap2, final int x, final int y) {
        final int color1 = bitmap1.getPixel(x, y);
        final int color2 = bitmap2.getPixel(x, y);

        return areColorsEqual(color1, color2);
    }

    private boolean isPercentageOfDifferentPixelsAcceptable(final double percentageOfDifferentPixels) {
        return percentageOfDifferentPixels <= compareOptions.getMaximumPercentageOfDifferentPixels();
    }

    private boolean areColorsEqual(final int color1, final int color2) {
        double colorDistance = calculateColorDistance(color1, color2);
        if (isColorDistanceAcceptable(colorDistance)) {
            return true;
        } else {
            return false;
        }
    }

    private static double calculateColorDistance(final int color1, final int color2) {
        final int alphaDelta = Color.alpha(color1) - Color.alpha(color2);
        final int redDelta = Color.red(color1) - Color.red(color2);
        final int greenDelta = Color.green(color1) - Color.green(color2);
        final int blueDelta = Color.blue(color1) - Color.blue(color2);

        double distance = 0;
        distance += Math.pow(alphaDelta, 2);
        distance += Math.pow(redDelta, 2);
        distance += Math.pow(greenDelta, 2);
        distance += Math.pow(blueDelta, 2);
        distance = Math.sqrt(distance  * 255);
        return distance;
    }

    private boolean isColorDistanceAcceptable(final double colorDistance) {
        return colorDistance <= compareOptions.getMaximumColorDistance();
    }

    public RembrandtCompareOptions getCompareOptions() {
        return compareOptions;
    }

    public void setCompareOptions(RembrandtCompareOptions compareOptions) {
        this.compareOptions = compareOptions;
    }
}
