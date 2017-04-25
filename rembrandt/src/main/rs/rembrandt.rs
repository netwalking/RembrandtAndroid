#pragma version(1)
#pragma rs java_package_name(ly.img.android.rembrandt)
#pragma rs_fp_relaxed

int32_t differences = 0;
rs_allocation rsAllocationDifferencesCount;

float allowedColorDistance;
uchar4 colorEquality;
uchar4 colorDiversity;

rs_allocation rsAllocationBitmap1;
rs_allocation rsAllocationBitmap2;
rs_allocation rsAllocationComparisonBitmap;

void compareBitmaps(uchar4 *unused, uint32_t x, uint32_t y) {
    uchar4 color1 = rsGetElementAt_uchar4(rsAllocationBitmap1, x, y);
    uchar4 color2 = rsGetElementAt_uchar4(rsAllocationBitmap2, x, y);

    int deltaAlpha = color1.a - color2.a;
    int deltaRed   = color1.r - color2.r;
    int deltaGreen = color1.g - color2.g;
    int deltaBlue  = color1.b - color2.b;

    float colorDistance = sqrt((float)
          deltaAlpha * deltaAlpha
        + deltaRed   * deltaRed
        + deltaGreen * deltaGreen
        + deltaBlue  * deltaBlue
    );

    uchar4 colorToSet;
    if (colorDistance <= allowedColorDistance) {
        colorToSet = colorEquality;
    } else {
        colorToSet = colorDiversity;
        rsAtomicInc(&differences);
        rsSetElementAt_uint(rsAllocationDifferencesCount, differences, 0);
    }

    rsSetElementAt_uchar4(rsAllocationComparisonBitmap, colorToSet, x, y);
}

void compareBitmapsResultOnly(uchar4 *unused, uint32_t x, uint32_t y) {
    uchar4 color1 = rsGetElementAt_uchar4(rsAllocationBitmap1, x, y);
    uchar4 color2 = rsGetElementAt_uchar4(rsAllocationBitmap2, x, y);

    int deltaAlpha = color1.a - color2.a;
    int deltaRed   = color1.r - color2.r;
    int deltaGreen = color1.g - color2.g;
    int deltaBlue  = color1.b - color2.b;

    float colorDistance = sqrt((float)
          deltaAlpha * deltaAlpha
        + deltaRed   * deltaRed
        + deltaGreen * deltaGreen
        + deltaBlue  * deltaBlue
    );

    uchar4 colorToSet;
    if (colorDistance > allowedColorDistance) {
        rsAtomicInc(&differences);
        rsSetElementAt_uint(rsAllocationDifferencesCount, differences, 0);
    }

}
