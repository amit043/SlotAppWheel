package com.amit.slottappwheel.Utils.cylinder;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

/**
 * Created by user on 23/08/2015.
 */
public class MyGLSurfaceView extends GLSurfaceView {
    private MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        // setBackgroundColor(Color.TRANSPARENT);
        // Set the Renderer for drawing on the GLSurfaceView
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mRenderer = new MyGLRenderer(context);
        setRenderer(mRenderer);
        // setZOrderOnTop(true);
        setKeepScreenOn(true);
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void onPause() {
        super.onPause();
        mRenderer.onPause();
//		Toast.makeText(getContext(), "onPause", 2000).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRenderer.onResume();
//		Toast.makeText(getContext(), "onResume", 2000).show();
    }

    public void startRun() {
        mRenderer.replaceApps();
        for (int i = 0; i < mRenderer.isRunArray.length; i++)
            mRenderer.isRunArray[i] = true;
    }

    public void stopRun() {

        for (int i = mRenderer.isRunArray.length - 1; i >= 0; i--) {
            try {
                Thread.sleep((long) (((Math.random() * 1000) % 400) + 300));
                mRenderer.isRunArray[i] = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    public void checkWin() {
//        for (int i = 0; i < MyGLRenderer.m_nStrips; i++) {
//            float angle = (mRenderer.mRotationAngleArray[i] % 360) / 40;
//            angle += 12;
//            for (int j = 0; j < 3; j++) {
//                GlobalVars.AppArray[j][4 - i] = mRenderer.m_arrayStrips[i].runApps
//                        .get((int) ((angle + j) <= 8 ? angle + j : angle + j
//                                - 9)).mNum;
//            }
//        }
//    }

//    public float checkLines(int mLines) {
//        GlobalVars.sumJack = 0;
//        float sumScore = 0;
//        GlobalVars.linsWin.clear();
//        for (int i = 0; i <= mLines; i++) {
//            int nunAppInRow = GlobalVars.AppArray[GlobalVars.LinesArray[i][0]][0];
//            boolean isSet = true;
//            int j = 1;
//            for (; j < 5 && isSet; j++)
//                if (!(nunAppInRow == GlobalVars.AppArray[GlobalVars.LinesArray[i][j]][j]))
//                    isSet = false;
//            if (isSet)
//                j -= 1;
//            else
//                j -= 2;
//            sumScore += GlobalVars.winArray[nunAppInRow][j];
//            if (GlobalVars.winArray[nunAppInRow][j] > 0) {
//                GlobalVars.sumJack += j;
//                markLine(i, j);
//            }
//        }
//        return sumScore;
//    }

//    private void markLine(int i, int j) {
//        GlobalVars.linsWin.add(new LineWin(i, j));
//    }
}
