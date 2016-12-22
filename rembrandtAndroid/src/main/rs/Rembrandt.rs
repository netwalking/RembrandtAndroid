#pragma version(1)
#pragma rs java_package_name(ly.img.android.rembrandt)
#pragma rs_fp_relaxed

uint different_pixels;
float allowed_color_distance;
uchar4 color_equality;
uchar4 color_diversity;
rs_allocation bitmap1;
rs_allocation bitmap2;
rs_allocation comparison_bitmap;

void compareBitmaps(uchar4 *unused, uint x, uint y) {
    uchar4 color1 = rsGetElementAt_uchar4(bitmap1, x, y);
    uchar4 color2 = rsGetElementAt_uchar4(bitmap2, x, y);

    int delta_alpha = color1.a - color2.a;
    int delta_red   = color1.r - color2.r;
    int delta_green = color1.g - color2.g;
    int delta_Blue  = color1.b - color2.b;

    float color_distance = 0;
    color_distance += delta_alpha * delta_alpha;
    color_distance += delta_red * delta_red;
    color_distance += delta_green * delta_green;
    color_distance += delta_Blue * delta_Blue;
    color_distance = sqrt(color_distance * 255);

    uchar4 color_to_set;
    if (color_distance < allowed_color_distance) {
        color_to_set = color_equality;
    } else {
        color_to_set = color_diversity;
        rsAtomicInc(&different_pixels);
    }

    rsSetElementAt_uchar4(comparison_bitmap, color_to_set, x, y);
}
