package ly.img.android.example;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
        float parentY = layout.getY() + layout.getHeight() / 2;
        float child1Y = imageView1.getY() + imageView1.getHeight() / 2;
        float child2Y = imageView2.getY() + imageView2.getHeight() / 2;

        TranslateAnimation animation1 = new TranslateAnimation(0, 0, 0, parentY - child1Y);
        animation1.setDuration(1000);
        animation1.setFillAfter(true);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addComparisonImageView();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        TranslateAnimation animation2 = new TranslateAnimation(0, 0, 0, parentY - child2Y);
        animation2.setDuration(1000);
        animation2.setFillAfter(true);

        imageView1.startAnimation(animation1);
        imageView2.startAnimation(animation2);
    }

    private void addComparisonImageView() {
        comparisonImageView = new ImageView(this);
        comparisonImageView.setImageBitmap(compareResult.getComparisionBitmap());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        layoutParams.gravity = Gravity.CENTER;
        comparisonImageView.setLayoutParams(layoutParams);

        layout.addView(comparisonImageView);

        AlphaAnimation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(3000);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        comparisonImageView.startAnimation(animation);
    }
}
