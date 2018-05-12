package com.hzh.xpuzzle.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hzh.xpuzzle.R;
import com.hzh.xpuzzle.adapter.GridPicListAdapter;
import com.hzh.xpuzzle.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RESULT_IMAGE = 100;
    private static final int RESULT_CAMERA = 200;
    private static final String IMAGE_TYPE = "image/*";
    public static String TEMP_IMAGE_PATH;

    private PopupWindow mPopupWindow;
    private TextView mTypeSelected;
    private View mPopupView;
    private int[] mResPicId;
    private List<Bitmap> mPicList;
    private int mType = 2;
    private String[] mCustomItem = new String[]{"本地图册", "相机拍照"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        //加载PopupWindow选项
        mPopupView = layoutInflater.inflate(R.layout.xpuzzle_main_type_selected, null);

        TextView tvTypeTest = mPopupView.findViewById(R.id.tv_main_type_2);
        tvTypeTest.setOnClickListener(this);

        TextView tvTypeEasy = mPopupView.findViewById(R.id.tv_main_type_3);
        tvTypeEasy.setOnClickListener(this);

        TextView tvTypeDifficult = mPopupView.findViewById(R.id.tv_main_type_4);
        tvTypeDifficult.setOnClickListener(this);

        mTypeSelected = findViewById(R.id.tv_puzzle_main_type_selected);
        mTypeSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示PopupWindow
                popupShow(v);
            }
        });

        GridView gridView = findViewById(R.id.gv_xpuzzle_main_pic_list);
        gridView.setAdapter(new GridPicListAdapter(this, mPicList));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mResPicId.length - 1) {
                    //选择本地图片、拍照
                    showDialogCustom();
                } else {
                    //选择的是默认图片
                    Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                    intent.putExtra("picSelectedID", mResPicId[position]);
                    intent.putExtra("mType", mType);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        TEMP_IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/temp.png";

        mPicList = new ArrayList<>();
        mResPicId = new int[]{R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4,
                R.drawable.pic5, R.drawable.pic6, R.drawable.pic7, R.drawable.pic8, R.drawable.pic9,
                R.drawable.pic10, R.drawable.pic11, R.drawable.pic12, R.drawable.pic13, R.drawable.pic14,
                R.drawable.pic15, R.mipmap.ic_launcher};
        Bitmap[] bitmaps = new Bitmap[mResPicId.length];
        for (int i = 0; i < bitmaps.length; i++) {
            bitmaps[i] = BitmapFactory.decodeResource(getResources(), mResPicId[i]);
            mPicList.add(bitmaps[i]);
        }
    }

    /**
     * 显示PopupWindow
     *
     * @param view view
     */
    private void popupShow(View view) {
        mPopupWindow = new PopupWindow(mPopupView, ScreenUtils.dp2px(this, 200),
                ScreenUtils.dp2px(this, 50));
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);

        //添加透明背景
        Drawable transparent = new ColorDrawable(Color.TRANSPARENT);

        mPopupWindow.setBackgroundDrawable(transparent);

        //获取显示位置
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY,
                location[0] - ScreenUtils.dp2px(this, 40),
                location[1] + ScreenUtils.dp2px(this, 30));
    }

    private void showDialogCustom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(mCustomItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {//本地图册
                            Intent intent = new Intent(Intent.ACTION_PICK, null);
                            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_TYPE);
                            startActivityForResult(intent, RESULT_IMAGE);
                        } else if (which == 1) {//相机拍照
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            Uri photoUri = Uri.fromFile(new File(TEMP_IMAGE_PATH));
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, RESULT_CAMERA);
                        }
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_IMAGE && data.getData() != null) {//相册图片
                Cursor cursor = this.getContentResolver().query(data.getData(), null,
                        null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
                    Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                    intent.putExtra("mPicPath", imagePath);
                    intent.putExtra("mType", mType);
                    cursor.close();
                    startActivity(intent);
                }
            } else if (requestCode == RESULT_CAMERA) {//相机
                Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                intent.putExtra("mPicPath", TEMP_IMAGE_PATH);
                intent.putExtra("mType", mType);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        //选择难度
        switch (v.getId()) {
            case R.id.tv_main_type_2://测试难度
                mTypeSelected.setText(R.string.type_test);
                mType = 2;
                break;
            case R.id.tv_main_type_3://简单难度
                mTypeSelected.setText(R.string.type_easy);
                mType = 3;
                break;
            case R.id.tv_main_type_4://困难难度
                mTypeSelected.setText(R.string.type_difficult);
                mType = 4;
                break;
            default:
                break;
        }
        mPopupWindow.dismiss();
    }
}
