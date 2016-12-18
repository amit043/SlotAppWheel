package com.amit.slottappwheel.Utils.cylinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by user on 23/08/2015.
 */
public class CSlice {

    private static final int VERTEX_MAGIC_NUMBER = 5;
    private static final int MAXIMUM_ALLOWED_DEPTH = 5;

    private static final int NUM_FLOATS_PER_VERTEX = 3;
    private static final int NUM_FLOATS_PER_TEXTURE = 2;

    private static final int N_DEPTH = 3;

    private int nRows = 0;
    private float m_fAngleRange = (float) (Math.PI * 2.0f);
    private float m_fHeight = 1.0f;
    private float m_fWidth = 1.0f;
    private float m_fStartX = 0.0f;
    private float m_fAngleStart = 0.0f;

    public static float vertices[];
    public static short indices[];
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;

    public static float uvs[];
    public FloatBuffer uvBuffer;
    private Context mContext = null;
    private final float[] mRotationMatrix = new float[16];
    private final int[] mTextures = new int[1];

    private float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float fAngle) {
        mAngle = fAngle;
    }

    public CSlice(Context context, float fStartX, float fWidth,
                  float fAngleStart, float fAngleRange, float fHeight) {
        mContext = context;
        mAngle = 0;
        final int d = Math.max(1, Math.min(MAXIMUM_ALLOWED_DEPTH, N_DEPTH));
        nRows = Maths.power(2, d - 1) * VERTEX_MAGIC_NUMBER;

        m_fAngleStart = fAngleStart;
        m_fAngleRange = fAngleRange;
        m_fHeight = fHeight;
        m_fStartX = fStartX;
        m_fWidth = fWidth;

    }

    public void init(String strResource, Bitmap bitmap) {
        initVertexBuffer();
        initTextureBuffer(strResource, bitmap);
    }

    void render(float[] m) {
        // get handle to vertex shader's vPosition member
        float[] scratch = new float[16];
        // float[] mat1 = new float[16];

        // GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // GLES20.glFrontFace(GLES20.GL_CCW);
        // Enable face culling.
        // GLES20.glEnable(GLES20.GL_CULL_FACE);
        // What faces to remove with the face culling.
        // GLES20.glCullFace(GLES20.GL_BACK);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);

        int mPositionHandle = GLES20.glGetAttribLocation(
                riGraphicTools.sp_Image, "vPosition");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT,
                false, 0, vertexBuffer);

        // Get handle to texture coordinates location
        int mTexCoordLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Image,
                "a_texCoord");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false,
                0, uvBuffer);

        // GLES20.glEnable(GLES20.GL_CULL_FACE);
        // specify which faces to not draw
        // GLES20.glCullFace(GLES20.GL_FRONT);

        // GLES20.glf
        // GLES20.glFrontFace(GLES20.GL_CW);//
        // gl.glFrontFace(GL10.GL_CW);

        // Matrix.setRotateM(mat1, 0, 90.0f, 0.0f, 1.0f, 0.0f);
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 1.0f, 0.0f, 0.0f);

        // mAngle += 1.0f;
        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        // Matrix.multiplyMM(m, 0, m, 0, mat1, 0);
        Matrix.multiplyMM(scratch, 0, m, 0, mRotationMatrix, 0);

        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image,
                "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, scratch, 0);

        // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Image,
                "s_texture");

        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i(mSamplerLoc, 0);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indices.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        // GLES20.glEnable(GLES20.GL_DEPTH_TEST);

    }

    public void initTextureBuffer(String strResource, Bitmap bmp) {
        // Create our UV coordinates.

        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

        // Generate Textures, if more needed, alter these numbers.
        // int[] texturenames = new int[1];
        GLES20.glGenTextures(1, mTextures, 0);

        // gl.glGenTextures(1, this.mTextures, 0);
        // gl.glBindTexture(GL10.GL_TEXTURE_2D, this.mTextures[0]);

        // Retrieve our image from resources.
		/*
		 * int id = mContext.getResources().getIdentifier(strResource, null,
		 * mContext.getPackageName());
		 *
		 * // Temporary create a bitmap Bitmap bmp =
		 * BitmapFactory.decodeResource(mContext.getResources(), id);
		 */

        // Bind texture to texturename
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Set wrapping mode
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        // Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() / 5,
        // bmp.getHeight() / 5);
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        // We are done using the bitmap so we should recycle it.
        // bmp.recycle();

    }

    public void initVertexBuffer() {
        final float fAngleStep = m_fAngleRange / (float) nRows;
        final float fRadius = m_fHeight / 2.0f;

        float fAngle = m_fAngleStart;
        // float x = 0.0f; // float y = 0.0f; float z = 0.0f;

        float fx1 = m_fStartX;
        float fx2 = fx1 + m_fWidth;
        float fSteptv = fAngleStep / m_fAngleRange;
        float ftv = 1.0f;
        // float fStepY = m_fHeight / (float)nRows;

        vertices = new float[4 * nRows * NUM_FLOATS_PER_VERTEX];
        indices = new short[6 * nRows];
        uvs = new float[4 * nRows * NUM_FLOATS_PER_TEXTURE];

        int vertexPos = 0;
        int texturePos = 0;

        for (short i = 0; i < nRows; i++) {
            // y += fStepY;
            vertices[vertexPos++] = fx1;
            vertices[vertexPos++] = (float) Math.sin(fAngle) * fRadius;
            vertices[vertexPos++] = (float) Math.cos(fAngle) * fRadius;
            uvs[texturePos++] = (fx2 - m_fStartX) / m_fWidth;
            uvs[texturePos++] = 1.0f - ftv; // m_fAngleRange;

            vertices[vertexPos++] = fx1;
            vertices[vertexPos++] = (float) Math.sin(fAngle - fAngleStep)
                    * fRadius;// y - fStepY;
            vertices[vertexPos++] = (float) Math.cos(fAngle - fAngleStep)
                    * fRadius;

            uvs[texturePos++] = (fx2 - m_fStartX) / m_fWidth;
            uvs[texturePos++] = 1.0f - (ftv + fSteptv);// m_fAngleRange;

            vertices[vertexPos++] = fx2;
            vertices[vertexPos++] = (float) Math.sin(fAngle - fAngleStep)
                    * fRadius;// y - fStepY;
            vertices[vertexPos++] = (float) Math.cos(fAngle - fAngleStep)
                    * fRadius;

            // vertices[vertexPos++] = y - fStepY;
            // vertices[vertexPos++] = z;
            uvs[texturePos++] = (fx1 - m_fStartX) / m_fWidth;
            uvs[texturePos++] = 1.0f - (ftv + fSteptv); // m_fAngleRange;

            vertices[vertexPos++] = fx2;

            // vertices[vertexPos++] = y;
            // vertices[vertexPos++] = z;
            vertices[vertexPos++] = (float) Math.sin(fAngle) * fRadius;// y -
            // fStepY;
            vertices[vertexPos++] = (float) Math.cos(fAngle) * fRadius;

            uvs[texturePos++] = (fx1 - m_fStartX) / m_fWidth;
            uvs[texturePos++] = 1.0f - ftv;// 1.0f - (ftv + fSteptv) ;///
            // m_fAngleRange;

            ftv -= fSteptv;
            fAngle += fAngleStep;

            indices[i * 6 + 0] = (short) (i * 4);
            indices[i * 6 + 1] = (short) (i * 4 + 1);
            indices[i * 6 + 2] = (short) (i * 4 + 2);

            indices[i * 6 + 3] = (short) (i * 4);
            indices[i * 6 + 4] = (short) (i * 4 + 2);
            indices[i * 6 + 5] = (short) (i * 4 + 3);

        }

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);

    }
}
