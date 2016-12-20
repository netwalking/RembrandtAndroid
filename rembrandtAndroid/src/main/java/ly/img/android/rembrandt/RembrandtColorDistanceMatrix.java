package ly.img.android.rembrandt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptC;
import android.support.v8.renderscript.Type;

import ly.img.android.ScriptC_ColorDistance;
import ly.img.android.rembrandt.exceptions.UnequalBitmapConfigException;
import ly.img.android.rembrandt.exceptions.UnequalBitmapSizesException;

/**
 * Created by winklerrr on 16/12/2016.
 */

public class RembrandtColorDistanceMatrix {

    private Bitmap bitmap1;
    private Bitmap bitmap2;

    private int height;
    private int width;
    private Bitmap.Config config;

    private float[] colorDistanceMatrix;

    private RembrandtColorDistanceMatrix(final Bitmap bitmap1, final Bitmap bitmap2) {
        this.bitmap1 = bitmap1;
        this.bitmap2 = bitmap2;

        if (!areConfigsEqual()) {
            throw new UnequalBitmapConfigException(bitmap1, bitmap2);
        } else {
            config = bitmap1.getConfig();
        }

        if (!areSizesEqual()) {
            throw new UnequalBitmapSizesException(bitmap1, bitmap2);
        } else {
            height = bitmap1.getHeight();
            width = bitmap1.getWidth();
        }

        colorDistanceMatrix = new float[width * height];
    }

    public static RembrandtColorDistanceMatrix calculateColorDistanceMatrix(final Bitmap bitmap1, final Bitmap bitmap2) {
        RembrandtColorDistanceMatrix colorDistanceMatrix = new RembrandtColorDistanceMatrix(bitmap1, bitmap2);
        colorDistanceMatrix.calculate();
        return colorDistanceMatrix;
    }

    private void calculate() {
        // TODO move rs to consttructor
        RenderScript rs = RenderScript.create(Rembrandt.getContext());
        Allocation input1 = Allocation.createFromBitmap(rs, bitmap1);
        Allocation input2 = Allocation.createFromBitmap(rs, bitmap2);
        Allocation output = Allocation.createTyped(rs, Type.createXY(rs, Element.F32(rs), width, height));

        ScriptC_ColorDistance colorDistance = new ScriptC_ColorDistance(rs);
        colorDistance.set_rsAllocationA(input1);
        colorDistance.set_rsAllocationB(input2);
        colorDistance.set_rsAllocationC(output);

        colorDistance.forEach_calculateColorDistance(output);

        output.syncAll(Allocation.USAGE_SCRIPT);
        output.copyTo(colorDistanceMatrix);
    }

    private double calculateColorDistanceAt(final int x, final int y) {
        final int color1 = bitmap1.getPixel(x, y);
        final int color2 = bitmap2.getPixel(x, y);

        return calculateColorDistance(color1, color2);
    }

    private static double calculateColorDistance(final int color1, final int color2) {
        final int deltaAlpha = Color.alpha(color1) - Color.alpha(color2);
        final int deltaRed = Color.red(color1) - Color.red(color2);
        final int deltaGreen = Color.green(color1) - Color.green(color2);
        final int deltaBlue = Color.blue(color1) - Color.blue(color2);

        double distance = 0;
        distance += Math.pow(deltaAlpha, 2);
        distance += Math.pow(deltaRed, 2);
        distance += Math.pow(deltaGreen, 2);
        distance += Math.pow(deltaBlue, 2);
        distance = Math.sqrt(distance  * 255);
        return distance;
    }

    public boolean areConfigsEqual() {
        return bitmap1.getConfig() == bitmap2.getConfig();
    }

    public boolean areSizesEqual() {
        return bitmap1.getHeight() == bitmap2.getHeight() && bitmap1.getWidth() == bitmap2.getWidth();
    }

    public double getDistanceAt(final int x, final int y) {
        return colorDistanceMatrix[x + y * width];
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getPixelsInTotal() {
        return height * width;
    }

    public Bitmap.Config getConfig() {
        return config;
    }
}
