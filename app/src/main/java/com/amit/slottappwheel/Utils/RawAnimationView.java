package com.amit.slottappwheel.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import com.amit.slottappwheel.R;

public class RawAnimationView extends View {

    Paint paint;

    Bitmap bm;
    int bm_offsetX, bm_offsetY;

    Path animPath;
    PathMeasure pathMeasure;
    float pathLength;

    float step; // distance each step
    float distance; // distance moved

    float[] pos;
    float[] tan;

    Matrix matrix;

    public RawAnimationView(Context context) {
        super(context);
        initMyView();
    }

    public RawAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMyView();
    }

    public RawAnimationView(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMyView();
    }

    public void initMyView() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);

        bm = BitmapFactory.decodeResource(getResources(), R.drawable.new_led);
        bm_offsetX = bm.getWidth() / 2;
        bm_offsetY = bm.getHeight() / 2;

        int w = getContext().getResources().getInteger(R.integer.aaat);

        animPath = new Path();
        animPath.moveTo(10, 10);
        animPath.lineTo(w - 10, 10);
        // animPath.close();

        pathMeasure = new PathMeasure(animPath, false);
        pathLength = pathMeasure.getLength();


        step = 3f;
        distance = 0;
        pos = new float[2];
        tan = new float[2];

        matrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // canvas.drawPath(animPath, paint);
        for (float i = distance; i + 5 < pathLength; i += 20) {
            pathMeasure.getPosTan(i, pos, tan);

            matrix.reset();
            float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
            matrix.postRotate(degrees, bm_offsetX, bm_offsetY);
            matrix.postTranslate(pos[0] - bm_offsetX, pos[1] - bm_offsetY);

            canvas.drawBitmap(bm, matrix, null);
        }
        if (distance < 20) {
            distance += step;
        } else {
            distance = 0;
        }

        invalidate();
    }
}
