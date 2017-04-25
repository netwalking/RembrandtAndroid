package ly.img.android.example;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ly.img.android.rembrandt.Rembrandt;
import ly.img.android.rembrandt.RembrandtComparisonResult;

public class MainActivity extends Activity {

    private TextView resultTextView;
    private TextView resultInfoTextView;

    private LinearLayout layout;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView comparisonImageView;

    private Button compareButton;
    private Button separateButton;
    private Animator currentAnimator;

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
        resultTextView = (TextView) findViewById(R.id.activity_main_textview_result);
        resultInfoTextView = (TextView) findViewById(R.id.activity_main_textview_result_info);

        layout = (LinearLayout) findViewById(R.id.activity_main_layout_imageviews);
        imageView1 = (ImageView) findViewById(R.id.activity_main_imageview_1);
        imageView2 = (ImageView) findViewById(R.id.activity_main_imageview_2);
        comparisonImageView = (ImageView) findViewById(R.id.activity_main_imageview_comparison);

        compareButton = (Button) findViewById(R.id.activity_main_button_compare);
        separateButton = (Button) findViewById(R.id.activity_main_button_separate);
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
        addCompareButtonOnClickListener();
        addSeparateButtonOnClickListener();
    }

    private void addCompareButtonOnClickListener() {
        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCompare();
            }
        });
    }

    private void addSeparateButtonOnClickListener() {
        separateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSeparate();
            }
        });
    }

    private void onClickCompare() {
        compareBitmaps();
        changeToSeparateButton();
        showResult();
    }

    private void onClickSeparate() {
        animateSeparation();
        changeToCompareButton();
    }

    private void compareBitmaps() {
        compareResult = new Rembrandt(this).compareBitmaps(bitmap1, bitmap2, true);
    }

    private void showResult() {
        animateComparison();
        showTextResults();
    }

    private void changeToSeparateButton() {
        compareButton.setVisibility(View.GONE);
        separateButton.setVisibility(View.VISIBLE);
    }

    private void animateSeparation() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(getFadeOutAnimator(), getMoveOutAnimator());

        if (currentAnimator != null) currentAnimator.cancel();
        animatorSet.start();
    }

    private void changeToCompareButton() {
        separateButton.setVisibility(View.GONE);
        compareButton.setVisibility(View.VISIBLE);
    }

    private void animateComparison() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageView1.getMeasuredWidth(), imageView1.getMeasuredHeight());
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        comparisonImageView.setLayoutParams(layoutParams);
        comparisonImageView.invalidate();
        comparisonImageView.setImageBitmap(compareResult.getComparisionBitmap());
        comparisonImageView.setAlpha(0f);
        comparisonImageView.setVisibility(View.VISIBLE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(getMoveInAnimator(), getFadeInAnimator());

        if (currentAnimator != null) currentAnimator.cancel();
        currentAnimator = animatorSet;
        animatorSet.start();
    }

    private void showTextResults() {
        updateResultTextView();
        updateResultInfoTextView();
    }

    private AnimatorSet getMoveInAnimator() {
        float deltaCenter = (layout.getHeight() - imageView2.getHeight()) / 2;

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(imageView1, "translationY", imageView1.getTranslationY(), deltaCenter),
                ObjectAnimator.ofFloat(imageView2, "translationY", imageView2.getTranslationY(), -deltaCenter)
        );
        animatorSet.setDuration(1000);
        return animatorSet;
    }

    private AnimatorSet getMoveOutAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(imageView1, "translationY", imageView1.getTranslationY(), 0),
                ObjectAnimator.ofFloat(imageView2, "translationY", imageView2.getTranslationY(), 0)
        );
        animatorSet.setDuration(1000);
        return animatorSet;
    }

    private AnimatorSet getFadeInAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(comparisonImageView, "alpha", comparisonImageView.getAlpha(), 1)
        );

        long alpha = (long) comparisonImageView.getAlpha() * 1000;
        animatorSet.setDuration(1000 - alpha);
        return animatorSet;
    }

    private AnimatorSet getFadeOutAnimator() {
        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(
                ObjectAnimator.ofFloat(comparisonImageView, "alpha", comparisonImageView.getAlpha(), 0)
        );

        long alpha = (long) comparisonImageView.getAlpha() * 1000;
        animator.setDuration(alpha);
        return animator;
    }

    private void updateResultTextView() {
        String text;
        int color;

        if (compareResult.areBitmapsEqual()) {
            text = getString(R.string.equality);
            color = Color.GREEN;
        } else {
            text = getString(R.string.diversity);
            color = Color.RED;
        }

        resultTextView.setText(text);
        resultTextView.setTextColor(color);
    }

    private void updateResultInfoTextView() {
        String differences = String.format(getString(R.string.differences), compareResult.getNumberOfDifferentPixels());
        String percentageDifferences = String.format(getString(R.string.percentageDifferences), compareResult.getPercentageOfDifferentPixels());
        resultInfoTextView.setText("(" + differences + ", " + percentageDifferences + ")");
    }
}
