package ly.img.android.example_rembrandt;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import ly.img.android.rembrandt.Rembrandt;
import ly.img.android.rembrandt.RembrandtCompareResult;

public class MainActivity extends Activity {

    Bitmap mitSticker, ohneSticker;
    ImageView iv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        ohneSticker = BitmapFactory.decodeResource(getResources(), R.drawable.imagea, options);
        mitSticker = BitmapFactory.decodeResource(getResources(), R.drawable.imageb, options);


        iv1 = (ImageView) findViewById(R.id.iv1);
        iv1.setImageBitmap(ohneSticker);
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        compare();
            }
        });

    }

    private void compare() {
        Rembrandt rembrandt = new Rembrandt();
        RembrandtCompareResult compareResult = rembrandt.compareBitmaps(ohneSticker, mitSticker);

        iv1.setImageBitmap(compareResult.getComparisionBitmap());
        Log.d("compareBitmaps", "compared!");
    }
}
