package ly.img.android.rembrandt;

import android.graphics.Bitmap;

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
        TimeIt calc = new TimeIt("calc");
        final RembrandtColorDistanceMatrix colorDistanceMatrix =
                RembrandtColorDistanceMatrix.calculateColorDistanceMatrix(bitmap1, bitmap2);
        calc.logElapsedTime();

        final int width = colorDistanceMatrix.getWidth();
        final int height = colorDistanceMatrix.getHeight();
        final int pixelsInTotal = colorDistanceMatrix.getPixelsInTotal();
        final Bitmap.Config config = colorDistanceMatrix.getConfig();

        TimeIt set = new TimeIt("set");
        int differentPixels = 0;
        final Bitmap comparisonBitmap = Bitmap.createBitmap(width, height, config);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final double distance = colorDistanceMatrix.getDistanceAt(x, y);
                if (isColorDistanceAcceptable(distance)) {
                    comparisonBitmap.setPixel(x, y, compareOptions.getColorForEquality());
                } else {
                    comparisonBitmap.setPixel(x, y, compareOptions.getColorForDiversity());
                    differentPixels++;
                }
            }
        }
        set.logElapsedTime();

        final double percentageOfDifferentPixels = differentPixels / pixelsInTotal * 100.0f;
        boolean bitmapsSeemToBeEqual = isPercentageOfDifferentPixelsAcceptable(percentageOfDifferentPixels);
        return new RembrandtCompareResult(differentPixels, percentageOfDifferentPixels, comparisonBitmap, bitmapsSeemToBeEqual);
    }

    private boolean isColorDistanceAcceptable(final double colorDistance) {
        return colorDistance <= compareOptions.getMaximumColorDistance();
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
