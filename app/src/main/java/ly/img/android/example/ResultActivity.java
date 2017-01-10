package ly.img.android.example;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import ly.img.android.rembrandt.RembrandtComparisonResult;

public class ResultActivity extends Activity {

    private ImageView resultImageView;

    private RembrandtComparisonResult compareResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initActivity();
    }

    private void initActivity() {
        initWidgets();
        initFromIntent();
        loadCompareResult();
    }

    private void initWidgets() {
        resultImageView = (ImageView) findViewById(R.id.activity_result_imageview);
    }

    private void initFromIntent() {
        final Intent intent = getIntent();
        compareResult = intent.getParcelableExtra("result");
    }

    private void loadCompareResult() {
        final Bitmap resultBitmap = compareResult.getComparisionBitmap();
        resultImageView.setImageBitmap(resultBitmap);
    }
}
