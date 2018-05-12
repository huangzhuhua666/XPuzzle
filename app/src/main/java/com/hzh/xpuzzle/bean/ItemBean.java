package com.hzh.xpuzzle.bean;

import android.graphics.Bitmap;

/**
 * 拼图Item类
 */
public class ItemBean {

    private int mItemId;
    private int mBitmapId;
    private Bitmap mBitmap;

    public ItemBean() {

    }

    public ItemBean(int itemId, int bitmapId, Bitmap bitmap) {
        mItemId = itemId;
        mBitmapId = bitmapId;
        mBitmap = bitmap;
    }

    public int getItemId() {
        return mItemId;
    }

    public void setItemId(int itemId) {
        mItemId = itemId;
    }

    public int getBitmapId() {
        return mBitmapId;
    }

    public void setBitmapId(int bitmapId) {
        mBitmapId = bitmapId;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}
