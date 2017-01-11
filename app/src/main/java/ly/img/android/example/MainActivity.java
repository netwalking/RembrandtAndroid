package ly.img.android.example;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ly.img.android.rembrandt.Rembrandt;
import ly.img.android.rembrandt.RembrandtComparisonResult;

public class MainActivity extends Activity {

    private LinearLayout layout;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView comparisonImageView;
    private Button compareButton;

    private Bitmap bitmap1;
    private Bitmap bitmap2;

    private RembrandtComparisonResult compareResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActivity();
    }

    private void initActivity() {
        initWidgets();
        initBitmaps();
        loadBitmaps();
        addListeners();
    }

    private void initWidgets() {
        layout = (LinearLayout) findViewById(R.id.activity_main_layout_imageviews);
        imageView1 = (ImageView) findViewById(R.id.activity_main_imageview_1);
        imageView2 = (ImageView) findViewById(R.id.activity_main_imageview_2);
        comparisonImageView = (ImageView) findViewById(R.id.activity_main_imageview_comparison);
        compareButton = (Button) findViewById(R.id.activity_main_button_compare);
    }

    private void initBitmaps() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.image1, options);
        bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.image2, options);
    }

    private void loadBitmaps() {
        imageView1.setImageBitmap(bitmap1);
        imageView2.setImageBitmap(bitmap2);
    }

    private void addListeners() {
        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compareBitmapsAndShowResult();
            }
        });
    }

    private void compareBitmapsAndShowResult() {
        compareBitmaps();
        showResult();
    }

    private void compareBitmaps() {
        compareResult = new Rembrandt().compareBitmaps(bitmap1, bitmap2);
    }

    private void showResult() {
        comparisonImageView.setImageBitmap(compareResult.getComparisionBitmap());
        comparisonImageView.setAlpha(0f);
        comparisonImageView.setVisibility(View.VISIBLE);

        float parentY = layout.getY() + layout.getHeight() / 2;
        float child1Y = imageView1.getY() + imageView1.getHeight() / 2;
        float child2Y = imageView2.getY() + imageView2.getHeight() / 2;

        AnimatorSet moveAnimator = new AnimatorSet();
        moveAnimator.playTogether(
                ObjectAnimator.ofFloat(imageView1, "translationY", imageView1.getTranslationY(), parentY - child1Y),
                ObjectAnimator.ofFloat(imageView2, "translationY", imageView2.getTranslationY(), parentY - child2Y)
        );
        moveAnimator.setDuration(1000);

        AnimatorSet fadeAnimator = new AnimatorSet();
        fadeAnimator.playTogether(
                ObjectAnimator.ofFloat(comparisonImageView, "alpha", comparisonImageView.getAlpha(), 1)
        );
        fadeAnimator.setDuration(1000);

        AnimatorSet sequence = new AnimatorSet();
        sequence.playSequentially(moveAnimator, fadeAnimator);
        sequence.start();
    }

}
