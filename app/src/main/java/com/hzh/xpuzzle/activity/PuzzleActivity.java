package com.hzh.xpuzzle.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzh.xpuzzle.R;
import com.hzh.xpuzzle.adapter.GridItemsAdapter;
import com.hzh.xpuzzle.bean.ItemBean;
import com.hzh.xpuzzle.utils.GameUtils;
import com.hzh.xpuzzle.utils.ImageUtils;
import com.hzh.xpuzzle.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 拼图逻辑主界面：面板显示
 */
public class PuzzleActivity extends AppCompatActivity implements View.OnClickListener,Runnable{

    //游戏难度
    public static int TYPE = 2;
    //步数
    public static int COUNT_INDEX = 0;
    //计时
    public static int TIME_INDEX = 0;
    // 拼图完成时显示的最后一个图片
    public static Bitmap mLastBitmap;
    //选择的图片
    private Bitmap mPicSelected;
    //切割后的图片
    private List<Bitmap> mBitmapItemLists = new ArrayList<>();
    private GridView mGridView;
    private GridItemsAdapter mAdapter;
    private String mPicPath;
    private ImageView mImageView;
    //flag是否已显示原图
    private boolean isShow = false;
    private TextView mTv_count;
    private TextView mTv_time;
    //UI更新Handler
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        initData();
        initView();
        generateGame();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //获取选择的图片
        Bitmap picSelectedTemp;
        int resId = getIntent().getIntExtra("picSelectedID", 0);
        mPicPath = getIntent().getStringExtra("mPicPath");
        if (resId != 0) {//选择的是默认的图片
            picSelectedTemp = BitmapFactory.decodeResource(getResources(), resId);
        } else {//选择的是相册图片或拍摄的图片
            picSelectedTemp = BitmapFactory.decodeFile(mPicPath);
        }
        //获取游戏难度
        TYPE = getIntent().getIntExtra("mType", 2);
        //处理图片
        handlerImage(picSelectedTemp);
    }

    private void initView() {
        //显示原图按钮
        Button btn_image = findViewById(R.id.btn_puzzle_main_img);
        btn_image.setOnClickListener(this);

        //重置按钮
        Button btn_restart = findViewById(R.id.btn_puzzle_main_restart);
        btn_restart.setOnClickListener(this);

        //返回按钮
        Button btn_back = findViewById(R.id.btn_puzzle_main_back);
        btn_back.setOnClickListener(this);

        //显示步数
        mTv_count = findViewById(R.id.tv_puzzle_main_counts);
        mTv_count.setText("" + COUNT_INDEX);

        //显示时间
        mTv_time = findViewById(R.id.tv_puzzle_main_time);
        mTv_time.setText("" + TIME_INDEX + "秒");

        mGridView = findViewById(R.id.gv_puzzle_main_detail);
        mGridView.setNumColumns(TYPE);
        RelativeLayout.LayoutParams gridParams = new RelativeLayout.LayoutParams(mPicSelected.getWidth(),
                mPicSelected.getHeight());
        gridParams.addRule(RelativeLayout.BELOW, R.id.ll_puzzle_main_spinner);
        gridParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mGridView.setLayoutParams(gridParams);
        mGridView.setHorizontalSpacing(0);
        mGridView.setVerticalSpacing(0);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断可不可以移动
                if (GameUtils.canMove(position)) {
                    //可以移动，交换点击的图片和空白图片
                    GameUtils.swapItems(GameUtils.mItemBeans.get(position), GameUtils.mBlankItemBean);
                    //重新获取图片
                    recreateData();
                    //更新UI
                    mAdapter.notifyDataSetChanged();
                    //更新步数
                    COUNT_INDEX++;
                    mTv_count.setText("" + COUNT_INDEX);
                    //检测是否拼图成功
                    if (GameUtils.isSuccess()) {
                        //成功，补全最后一张图片
                        recreateData();
                        mBitmapItemLists.remove(TYPE * TYPE - 1);
                        mBitmapItemLists.add(mLastBitmap);
                        //更新UI
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(PuzzleActivity.this, "成功！", Toast.LENGTH_SHORT).show();
                        mGridView.setEnabled(false);
                        //取消计时
                        stop();
                    }
                }
            }
        });
        //添加显示原图的view
        addImageView();
    }

    /**
     * 生成游戏数据
     */
    private void generateGame() {
        //切图，获取初始（正常顺序）的拼图数据
        new ImageUtils().createInitBitmaps(this, mPicSelected, TYPE);
        //生成随机顺序
        GameUtils.getPuzzleGenerator();
        //获取Bitmap集合
        mBitmapItemLists.clear();
        for (ItemBean item : GameUtils.mItemBeans) {
            mBitmapItemLists.add(item.getBitmap());
        }
        mAdapter = new GridItemsAdapter(this, mBitmapItemLists);
        mGridView.setAdapter(mAdapter);
        //开始计时
        start();
    }

    /**
     * 重新获取图片
     */
    private void recreateData() {
        mBitmapItemLists.clear();
        for (ItemBean item : GameUtils.mItemBeans) {
            mBitmapItemLists.add(item.getBitmap());
        }
    }

    /**
     * 清空相关设置
     */
    private void cleanConfig() {
        //清空相关设置
        GameUtils.mItemBeans.clear();
        //停止计时
        stop();
        COUNT_INDEX = 0;
        TIME_INDEX = 0;
        //清除拍摄的图片
        if (mPicPath != null) {
            //删除图片
            File file = new File(mPicPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 处理图片，自适应大小
     *
     * @param bitmap 图片
     */
    private void handlerImage(Bitmap bitmap) {
        int screenWidth = ScreenUtils.getScreenSize(this).widthPixels;
        int screenHeight = ScreenUtils.getScreenSize(this).heightPixels;
        mPicSelected = new ImageUtils().resizeBitmap(bitmap, screenWidth * 0.8f,
                screenHeight * 0.6f);
    }

    /**
     * 添加显示原图的view
     */
    private void addImageView() {
        RelativeLayout relativeLayout = findViewById(R.id.rl_puzzle_main_layout);
        mImageView = new ImageView(this);
        mImageView.setImageBitmap(mPicSelected);
        int x = (int) (mPicSelected.getWidth() * 0.9f);
        int y = (int) (mPicSelected.getHeight() * 0.9f);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(x, y);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView.setLayoutParams(params);
        relativeLayout.addView(mImageView);
        mImageView.setVisibility(View.GONE);
    }

    /**
     * 开始计时
     */
    private void start(){
        stop();
        mHandler.post(this);
    }

    /**
     * 停止计时
     */
    private void stop(){
        mHandler.removeCallbacks(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanConfig();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_puzzle_main_img:
                Animation animShow = AnimationUtils.loadAnimation(PuzzleActivity.this,
                        R.anim.image_show_anim);
                Animation animHide = AnimationUtils.loadAnimation(PuzzleActivity.this,
                        R.anim.image_hide_anim);
                if (isShow) {
                    mImageView.startAnimation(animHide);
                    mImageView.setVisibility(View.GONE);
                    isShow = false;
                } else {
                    mImageView.startAnimation(animShow);
                    mImageView.setVisibility(View.VISIBLE);
                    isShow = true;
                }
                break;
            case R.id.btn_puzzle_main_restart:
                cleanConfig();
                generateGame();
                mTv_count.setText("" + COUNT_INDEX);
                mTv_time.setText("" + TIME_INDEX + "秒");
                mAdapter.notifyDataSetChanged();
                mGridView.setEnabled(true);
                break;
            case R.id.btn_puzzle_main_back:
                PuzzleActivity.this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void run() {
        TIME_INDEX++;
        mTv_time.setText("" + TIME_INDEX + "秒");
        mHandler.postDelayed(this, 1000);
    }
}
