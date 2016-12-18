package com.amit.slottappwheel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.amit.slottappwheel.Entity.Item;
import com.amit.slottappwheel.Utils.cylinder.MyGLSurfaceView;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {

    public static ArrayList<Item> itemsArr;
    private RelativeLayout cylindersView;
    private MyGLSurfaceView mGlSurfaceView;
    private Button spinBtn;
    private int w, h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemsArr = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            int drawableId = getResources().getIdentifier("app" + (i + 1), "drawable", getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    drawableId);
            itemsArr.add(new Item(bitmap));
        }
        mGlSurfaceView = new MyGLSurfaceView(this);
        cylindersView = (RelativeLayout) findViewById(R.id.cylinders_view);
        spinBtn = (Button) findViewById(R.id.spin_btn);
        spinBtn.setOnClickListener(this);
        ViewTreeObserver vto = cylindersView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    cylindersView.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                } else {
                    cylindersView.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                }
                w = cylindersView.getMeasuredWidth() - 100;
                h = cylindersView.getMeasuredHeight() - 100;

            }
        });

        RelativeLayout.LayoutParams glParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        cylindersView.addView(mGlSurfaceView, glParams);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void spin(){
        mGlSurfaceView.onPause();
        mGlSurfaceView.onResume();
        mGlSurfaceView.startRun();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mGlSurfaceView.stopRun();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.spin_btn:
                spin();
                break;
        }
    }
}
