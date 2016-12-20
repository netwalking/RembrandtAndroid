#pragma version(1)
#pragma rs java_package_name(ly.img.android)
#pragma rs_fp_relaxed

rs_allocation rsAllocationA;
rs_allocation rsAllocationB;
rs_allocation rsAllocationC;

void calculateColorDistance(float *v_out, uint32_t x, uint32_t y) {

    uchar4 color1 = rsGetElementAt_uchar4(rsAllocationA, x, y);
    uchar4 color2 = rsGetElementAt_uchar4(rsAllocationB, x, y);

    int deltaAlpha = color1.a - color2.a;
    int deltaRed   = color1.r - color2.r;
    int deltaGreen = color1.g - color2.g;
    int deltaBlue  = color1.b - color2.b;

    float dist = 0;
    dist += deltaAlpha * deltaAlpha;
    dist += deltaRed * deltaRed;
    dist += deltaGreen * deltaGreen;
    dist += deltaBlue * deltaBlue;
    dist = sqrt(dist * 255);

    rsSetElementAt_float(rsAllocationC, dist, x, y);
}