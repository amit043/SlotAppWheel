package com.amit.slottappwheel.Utils.cylinder;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by user on 23/08/2015.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private Context mContext = null;

    float mScreenWidth = 1280 / 2;
    float mScreenHeight = 768 / 2;

    // long mLastTime;
    int mProgram;

    private final float[] mtrxProjection = new float[16];
    private final float[] mtrxView = new float[16];
    private final float[] mtrxProjectionAndView = new float[16];

    /** Perspective setup, field of view component. */
    private static final float FOVY = 2.5f;

    /** Perspective setup, near component. */
    private static final float Z_NEAR = 1.0f;

    /** Perspective setup, far component. */
    private static final float Z_FAR = 200.0f;

    private CSlice m_piece;

    private float mRotationAngle = 0.0f;

    public static final int m_nStrips = 5;
    public boolean[] isRunArray = new boolean[m_nStrips];
    public float[] mRotationAngleArray = new float[m_nStrips];
    public CStrip[] m_arrayStrips = new CStrip[m_nStrips];

    public MyGLRenderer(Context context) {
        mContext = context;
        // mLastTime = System.currentTimeMillis() + 100;

        float fx = -1.5f / 3.1f / 1.85f / 2f ;
        float fStepX = 3.0f / (float) m_nStrips / 2.88f / 4 ;
//		float fStepX2 = 3.0f / (float) m_nStrips / 2.95f / 5.5f ;
        float fH = 1.0f / 2.28f / 4 ;

        for (int i = 0; i < m_nStrips; i++) {
            m_arrayStrips[i] = new CStrip(context, fx, fStepX, fH);
            mRotationAngleArray[i] = 0;
            isRunArray[i] = false;
            fx += fStepX;
        }
    }

    public void replaceApps() {
        for (int i = 0; i < m_arrayStrips.length; i++) {
            m_arrayStrips[i].setRunApps();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        //Log.d("opengl","isNPOTSupported 3: "+isNPOTSupported(unused));

        for (int i = 0; i < m_nStrips; i++) {
            m_arrayStrips[i].init(mContext);
        }

        // m_piece.init();
        // SetupTriangle();
        // SetupImage();
        // Set the clear color to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Create the shaders, solid color
        int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                riGraphicTools.vs_SolidColor);
        int fragmentShader = riGraphicTools.loadShader(
                GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_SolidColor);

        riGraphicTools.sp_SolidColor = GLES20.glCreateProgram();
        GLES20.glAttachShader(riGraphicTools.sp_SolidColor, vertexShader);
        GLES20.glAttachShader(riGraphicTools.sp_SolidColor, fragmentShader);
        GLES20.glLinkProgram(riGraphicTools.sp_SolidColor);

        // Create the shaders, images
        vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,
                riGraphicTools.vs_Image);
        fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,
                riGraphicTools.fs_Image);

        riGraphicTools.sp_Image = GLES20.glCreateProgram();
        GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader);
        GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader);
        GLES20.glLinkProgram(riGraphicTools.sp_Image);

        // Set our shader programm
        GLES20.glUseProgram(riGraphicTools.sp_Image);

    }

    public void onPause() {
		/* Do stuff to pause the renderer */
    }

    public void onResume() {
		/* Do stuff to resume the renderer */
        // mLastTime = System.currentTimeMillis();
    }

	/*
	 * public void SetupImage() { // Create our UV coordinates. uvs = new
	 * float[] { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f };
	 *
	 * // The texture buffer ByteBuffer bb =
	 * ByteBuffer.allocateDirect(uvs.length * 4);
	 * bb.order(ByteOrder.nativeOrder()); uvBuffer = bb.asFloatBuffer();
	 * uvBuffer.put(uvs); uvBuffer.position(0);
	 *
	 * // Generate Textures, if more needed, alter these numbers. int[]
	 * texturenames = new int[1]; GLES20.glGenTextures(1, texturenames, 0);
	 *
	 * // Retrieve our image from resources. int id =
	 * mContext.getResources().getIdentifier("drawable/circle", null,
	 * mContext.getPackageName());
	 *
	 * // Temporary create a bitmap Bitmap bmp =
	 * BitmapFactory.decodeResource(mContext.getResources(), id);
	 *
	 * // Bind texture to texturename
	 * GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	 * GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
	 *
	 * // Set filtering GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
	 * GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	 * GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
	 * GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	 *
	 * // Set wrapping mode GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
	 * GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	 * GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
	 * GLES20.GL_CLAMP_TO_EDGE);
	 *
	 * // Load the bitmap into the bound texture.
	 * GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
	 *
	 * // We are done using the bitmap so we should recycle it. bmp.recycle();
	 *
	 * }
	 */

	/*
	 * public void SetupTriangle() { // We have create the vertices of our view.
	 * vertices = new float[] { -0.1f, 0.1f, 0.0f, -0.1f, -0.1f, 0.0f, 0.1f,
	 * -0.1f, 0.0f, 0.1f, 0.1f, 0.0f, };
	 *
	 * indices = new short[] {0, 1, 2, 0, 2, 3}; // The order of
	 * vertexrendering.
	 *
	 * // The vertex buffer. ByteBuffer bb =
	 * ByteBuffer.allocateDirect(vertices.length * 4);
	 * bb.order(ByteOrder.nativeOrder()); vertexBuffer = bb.asFloatBuffer();
	 * vertexBuffer.put(vertices); vertexBuffer.position(0);
	 *
	 * // initialize byte buffer for the draw list ByteBuffer dlb =
	 * ByteBuffer.allocateDirect(indices.length * 2);
	 * dlb.order(ByteOrder.nativeOrder()); drawListBuffer = dlb.asShortBuffer();
	 * drawListBuffer.put(indices); drawListBuffer.position(0);
	 *
	 * }
	 */

    private void Render(float[] m) {
        // GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT |
        // GLES20.GL_DEPTH_BUFFER_BIT);

        for (int i = 0; i < m_nStrips; i++) {
            m_arrayStrips[i].draw(m);
        }
        // m_piece.render(m);

    }


    public boolean isNPOTSupported(GL10 gl) {
        String extensions = gl.glGetString(GL10.GL_EXTENSIONS);
        return extensions.indexOf("GL_OES_texture_npot") != -1;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        //	Log.d("opengl","isNPOTSupported: "+isNPOTSupported(unused));
		/*
		 * long now = System.currentTimeMillis();
		 *
		 * // We should make sure we are valid and sane if (mLastTime > now)
		 * return;
		 */
        // Get the amount of time the last frame took.
        // long elapsed = now - mLastTime;

        // Update our example

        // mRotationAngle ++;
        for (int i = 0; i < m_nStrips; i++) {
            if (isRunArray[i])
                mRotationAngleArray[i] -= 15;
            else {
                float r = mRotationAngleArray[i] % 40;
                if(r!=0)
                    mRotationAngleArray[i] -= 1;
            }
            m_arrayStrips[i].setRotateAngle(mRotationAngleArray[i]);
            // if(i% 2 == 0)
            // {
            // m_arrayStrips[i].setRotateAngle( - mRotationAngle);
            // }
            // else
            // {
            // m_arrayStrips[i].setRotateAngle(mRotationAngle);
            // }
        }
        // Render our example
        Render(mtrxProjectionAndView);

        // Save the current time to see how long it took <img
        // src="http://androidblog.reindustries.com/wp-includes/images/smilies/icon_smile.gif"
        // alt=":)" class="wp-smiley"> .
        // mLastTime = now;
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        //	Log.d("opengl","isNPOTSupported 2: "+isNPOTSupported(unused));
        mScreenWidth = width;
        mScreenHeight = height;

        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, (int) mScreenWidth, (int) mScreenHeight);

        // Clear our matrices
        for (int i = 0; i < 16; i++) {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        final float fAspect = (float) width
                / (float) (height == 0 ? 1 : height);

        // gl.glViewport(0, 0, width, height);
        // gl.glMatrixMode(GL10.GL_PROJECTION);
        // gl.glLoadIdentity();
        // GLU.gluPerspective(gl, FOVY, aspectRatio, Z_NEAR, Z_FAR);

        // Setup our screen width and height for normal sprite translation.
        Matrix.perspectiveM(mtrxProjection, 0, FOVY, fAspect, Z_NEAR, Z_FAR);

        // Matrix.orthoM(mtrxProjection, 0, 0f, mScreenWidth, 0.0f,
        // mScreenHeight, 0, 50);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, -2.5f, 0f, 0f, 0f, 0f, 1.0f,
                0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0,
                mtrxView, 0);
        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p>
     * <strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.
     * </p>
     *
     * @param type
     *            - Vertex or fragment shader type.
     * @param shaderCode
     *            - String containing the shader code.
     * @return - Returns an id for the shader.
     */

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, &quot;vColor&quot;);
     * MyGLRenderer.checkGlError(&quot;glGetUniformLocation&quot;);
     * </pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation
     *            - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}
