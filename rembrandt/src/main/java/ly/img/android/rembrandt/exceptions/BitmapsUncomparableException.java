package ly.img.android.rembrandt.exceptions;

import android.graphics.Bitmap;

/**
 * Created by winklerrr on 23/01/2017.
 */

public class BitmapsUncomparableException extends RuntimeException {

    public BitmapsUncomparableException() {
        super("Bitmap are uncomparable. Either their configs differ or their dimensions.");
    }

    public BitmapsUncomparableException(final Bitmap bitmap1, final Bitmap bitmap2) {
        super("Bitmap are uncomparable. Either their configs differ or their dimensions."
                + "\nConfigs: "
                + bitmap1.getConfig().toString() + " vs. "
                + bitmap2.getConfig().toString()
                + "\nDimensions: "
                + bitmap1.getHeight() + "x" + bitmap1.getWidth() + " vs. "
                + bitmap2.getHeight() + "x" + bitmap2.getWidth()
        );
    }
}
