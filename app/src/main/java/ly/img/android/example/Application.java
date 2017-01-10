package ly.img.android.example;

import ly.img.android.rembrandt.Rembrandt;

/**
 * Created by winklerrr on 20/12/2016.
 */

public class Application extends android.app.Application {

    private static android.app.Application context;

    @Override
    public void onCreate() {
        super.onCreate();
        Rembrandt.init(this);
    }
}
