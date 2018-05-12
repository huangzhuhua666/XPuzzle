package com.hzh.xpuzzle.utils;

import com.hzh.xpuzzle.activity.PuzzleActivity;
import com.hzh.xpuzzle.bean.ItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 拼图工具类：实现拼图的交换与生成算法
 */
public class GameUtils {

    // 游戏信息单元格Bean
    public static List<ItemBean> mItemBeans = new ArrayList<>();
    // 空格单元格
    public static ItemBean mBlankItemBean;

    /**
     * 生成随机的Item
     */
    public static void getPuzzleGenerator() {
        int index;
        //随机打乱顺序
        for (int i = 0; i < mItemBeans.size(); i++) {
            index = (int) (Math.random() * PuzzleActivity.TYPE * PuzzleActivity.TYPE);
            swapItems(mItemBeans.get(index), GameUtils.mBlankItemBean);
        }

        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < mItemBeans.size(); i++) {
            data.add(mItemBeans.get(i).getBitmapId());
        }

        //判断生成的游戏是否有解
        if (canSolve(data)) {
            return;
        } else {
            getPuzzleGenerator();
        }
    }

    /**
     * 交换点击的图片和空白图的位置
     *
     * @param from  点击的图片
     * @param blank 空白图片
     */
    public static void swapItems(ItemBean from, ItemBean blank) {
        ItemBean temp = new ItemBean();

        //交换BitmapId
        temp.setBitmapId(from.getBitmapId());
        from.setBitmapId(blank.getBitmapId());
        blank.setBitmapId(temp.getBitmapId());

        //交换Bitmap
        temp.setBitmap(from.getBitmap());
        from.setBitmap(blank.getBitmap());
        blank.setBitmap(temp.getBitmap());

        //设置新的空图片
        mBlankItemBean = from;
    }

    /**
     * 检测数据是否有解
     *
     * @param data 拼图数据
     * @return true：有解    false：无解
     */
    private static boolean canSolve(List<Integer> data) {
        //获取空图片所在位置
        int blankId = GameUtils.mBlankItemBean.getItemId();

        //检测
        if (data.size() % 2 == 1) {//数组宽度为奇数
            //判断序列的倒置和是否为偶数
            return getInversions(data) % 2 == 0;
        } else {
            //从下往上数，空图片位于奇数行
            if (((blankId - 1) / PuzzleActivity.TYPE) % 2 == 1) {
                //判断序列的倒置和是否为偶数
                return getInversions(data) % 2 == 0;
            } else {
                //从下往上数，空图片位于偶数行
                //判断序列的倒置和是否为奇数
                return getInversions(data) % 2 == 1;
            }
        }
    }

    /**
     * 计算倒置和算法
     *
     * @param data 拼图数据
     * @return 该序列的倒置和
     */
    private static int getInversions(List<Integer> data) {
        int inversions = 0;
        int inversionCount = 0;
        for (int i = 0; i < data.size(); i++) {
            for (int j = i + 1; j < data.size(); j++) {
                int index = data.get(i);
                if (data.get(j) != 0 && data.get(j) < index) {
                    inversionCount++;
                }
            }
            inversions += inversionCount;
            inversionCount = 0;
        }
        return inversions;
    }

    /**
     * 判断点击的图片可否移动
     *
     * @param position 点击的位置
     * @return true：可以移动    false：不能移动
     */
    public static boolean canMove(int position) {
        int type = PuzzleActivity.TYPE;
        //获取空图片的itemId
        int blankId = GameUtils.mBlankItemBean.getItemId() - 1;
        //不同行相差为type
        if (Math.abs(blankId - position) == type) {
            return true;
        }
        //相同行相差为1
        return (blankId / type == position / type) && Math.abs(blankId - position) == 1;
    }

    /**
     * 是否成功完成拼图
     */
    public static boolean isSuccess() {
        int type = PuzzleActivity.TYPE;
        for (ItemBean item : mItemBeans) {
            if (item.getBitmapId() != 0 && item.getItemId() == item.getBitmapId()) {
                continue;
            } else if (item.getBitmapId() == 0 && item.getItemId() == type * type) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

}
