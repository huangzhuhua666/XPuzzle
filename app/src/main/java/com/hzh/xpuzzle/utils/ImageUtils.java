package com.hzh.xpuzzle.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.hzh.xpuzzle.R;
import com.hzh.xpuzzle.activity.PuzzleActivity;
import com.hzh.xpuzzle.bean.ItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 图像工具类，实现图像的分割与自适应
 */
public class ImageUtils {

    /**
     * 切图、初始状态（正常顺序）
     * @param context 上下文环境
     * @param picSelected 选择的图片
     * @param type 游戏难度
     */
    public void createInitBitmaps(Context context, Bitmap picSelected, int type) {
        ItemBean mItemBean;
        Bitmap bitmap;
        List<Bitmap> bitmapItems = new ArrayList<>();

        //图片切割的大小
        int itemWidth = picSelected.getWidth() / type;
        int itemHeight = picSelected.getHeight() / type;

        //生成切割好的图片
        for (int i = 1; i <= type; i++) {
            for (int j = 1; j <= type; j++) {
                bitmap = Bitmap.createBitmap(picSelected, (j - 1) * itemWidth, (i - 1) * itemHeight,
                        itemWidth, itemHeight);
                bitmapItems.add(bitmap);
                mItemBean = new ItemBean((i - 1) * type + j, (i - 1) * type + j, bitmap);
                GameUtils.mItemBeans.add(mItemBean);
            }
        }
        //移除切割好的最后一张图片
        PuzzleActivity.mLastBitmap = bitmapItems.get(type * type - 1);
        bitmapItems.remove(type * type - 1);
        GameUtils.mItemBeans.remove(type * type - 1);

        //添加一张空白的图片
        Bitmap blankBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blank);
        blankBitmap = Bitmap.createBitmap(blankBitmap, 0, 0, itemWidth, itemHeight);
        bitmapItems.add(blankBitmap);
        GameUtils.mItemBeans.add(new ItemBean(type * type, 0, blankBitmap));
        GameUtils.mBlankItemBean = GameUtils.mItemBeans.get((type * type - 1));
    }

    /**
     * 处理图片，放大、缩小到合适位置
     * @param bitmap 要处理的图片
     * @param newWidth newWidth
     * @param newHeight newHeight
     * @return 处理后的图片
     */
    public Bitmap resizeBitmap(Bitmap bitmap, float newWidth, float newHeight) {
        Matrix matrix = new Matrix();
        matrix.postScale(newWidth / bitmap.getWidth(), newHeight / bitmap.getHeight());
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
