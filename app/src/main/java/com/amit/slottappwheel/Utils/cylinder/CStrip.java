package com.amit.slottappwheel.Utils.cylinder;

import android.content.Context;

import com.amit.slottappwheel.Entity.Item;
import com.amit.slottappwheel.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 23/08/2015.
 */
public class CStrip {

    float m_fStartX = 0;
    float m_fHeight = 0;
    float m_fWidth = 0;
    public List<Item> runItems;
    private static final int m_nSlices = 9;
    private CSlice[] m_arrySlices = new CSlice[m_nSlices];
    private String[] m_strArray = new String[m_nSlices];
    // private float m_fRotateAngle = 0;

    Context mContext;

    public CStrip() {
        m_fStartX = 0;
        m_fHeight = 1f;
        m_fWidth = 1f;

        initStrip();
    }

    public CStrip(Context context, float fStartX, float fWidth, float fHeight) {
        mContext = context;
        m_fStartX = fStartX;
        m_fHeight = fHeight;
        m_fWidth = fWidth;
        runItems = new ArrayList<>();
        initStrip();
    }

    public void initStrip() {
        float fAngleStep = (float) (2f * Math.PI / (float) m_nSlices);
        float fAngle = 0;

        for (int i = 0; i < m_nSlices; i++) {
            m_arrySlices[i] = new CSlice(mContext, m_fStartX, m_fWidth, fAngle,
                    fAngleStep, m_fHeight);
            m_strArray[i] = String.format("drawable/n%d", i + 1);
            int rand = (int) ((Math.random() * 100) % MainActivity.itemsArr.size());
            if (rand < MainActivity.itemsArr.size()) {
                runItems.add(MainActivity.itemsArr.get(rand));
            }
            fAngle += fAngleStep;
        }
    }

    public void init(Context context) {
        if (m_arrySlices.length > 0 && m_strArray.length > 0
                && runItems.size() > 0) {
            for (int i = 0; i < m_nSlices; i++) {
                m_arrySlices[i].init(m_strArray[i],
                        runItems.get(i).getBitmap());//context
            }
        }
    }

    public void setRotateAngle(float fAngle) {
        for (int i = 0; i < m_nSlices; i++) {
            m_arrySlices[i].setAngle(fAngle);
        }
        // m_fRotateAngle = fAngle;
    }

    public void setRunApps() {

        for (int i = 0; i < m_nSlices; i++) {
            runItems.add(
                    0,
                    MainActivity.itemsArr.get((int) ((Math.random() * 100) % MainActivity.itemsArr.size())));
        }
        for (int i = runItems.size() - 1; i >= m_nSlices; i--) {
            runItems.remove(i);
        }

    }

    public void draw(float[] m) {
        for (int i = 0; i < m_nSlices; i++) {
            m_arrySlices[i].render(m);
        }
    }
}
