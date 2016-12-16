package ly.img.android.rembrandt.exceptions;

import android.graphics.Bitmap;

/**
 * Created by winklerrr on 16/12/2016.
 */

public class UnequalBitmapDimensionsException extends RuntimeException {

    public UnequalBitmapDimensionsException(final Bitmap bitmap1, final Bitmap bitmap2) {
        super("Dimensions unequal: "
                + bitmap1.getHeight() + "x" + bitmap1.getWidth() + " vs. "
                + bitmap2.getHeight() + "x" + bitmap2.getWidth());
    }
}
