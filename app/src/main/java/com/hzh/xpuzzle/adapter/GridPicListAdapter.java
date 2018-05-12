package com.hzh.xpuzzle.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.hzh.xpuzzle.utils.ScreenUtils;

import java.util.List;

/**
 * 程序主界面的数据适配器
 */
public class GridPicListAdapter extends BaseAdapter {

    private List<Bitmap> mPicList;
    private Context mContext;

    public GridPicListAdapter(Context context, List<Bitmap> picList) {
        mContext = context;
        mPicList = picList;
    }

    @Override
    public int getCount() {
        return mPicList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv_pic_item;
        Bitmap bitmap = mPicList.get(position);
        if (convertView == null) {
            iv_pic_item = new ImageView(mContext);
            iv_pic_item.setLayoutParams(new GridView.LayoutParams(ScreenUtils.dp2px(mContext, 80),
                    ScreenUtils.dp2px(mContext, 100)));
            iv_pic_item.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            iv_pic_item = (ImageView) convertView;
        }
        iv_pic_item.setImageBitmap(bitmap);
        return iv_pic_item;
    }
}
