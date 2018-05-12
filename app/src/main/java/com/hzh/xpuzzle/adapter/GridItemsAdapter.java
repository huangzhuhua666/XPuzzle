package com.hzh.xpuzzle.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * 拼图主界面数据适配器
 */
public class GridItemsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Bitmap> mBitmapItemList;

    public GridItemsAdapter(Context context, List<Bitmap> bitmapItemList) {
        mContext = context;
        mBitmapItemList = bitmapItemList;
    }

    @Override
    public int getCount() {
        return mBitmapItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBitmapItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv_pic_item;
        Bitmap bitmap = mBitmapItemList.get(position);
        if (convertView == null) {
            iv_pic_item = new ImageView(mContext);
            iv_pic_item.setLayoutParams(new GridView.LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
            iv_pic_item.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            iv_pic_item = (ImageView) convertView;
        }
        iv_pic_item.setImageBitmap(bitmap);
        return iv_pic_item;
    }
}
