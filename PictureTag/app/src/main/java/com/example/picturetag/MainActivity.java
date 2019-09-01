package com.example.picturetag;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.picturetag.UIUtils.UIUtils;
import com.example.picturetag.bean.ChooserModel;
import com.example.picturetag.bean.IChooserModel;
import com.example.picturetag.bean.ITagBean;
import com.example.picturetag.bean.TagBean;
import com.example.picturetag.view.PictureTagFrameLayout;
import com.example.picturetag.viewpager.ImagePagerAdapterForPublish;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements PictureTagFrameLayout.ITagLayoutCallBack {

    public static int REQUEST_CODE_GET_TAG = 101;
    public static int RESULT_CODE_GET_TAG = 110;
    public static String BUNDLE_TAG_BEAN = "tag_bean";

    private ViewPager mViewPager;
    private float mPointX, mPointY;
    private ImagePagerAdapterForPublish mImageAdapter;
    private static Map<Integer, List<ITagBean>> mTagBeansMap;
    private int[] imageResId = new int[]{R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        mViewPager = findViewById(R.id.view_page);
        Fresco.initialize(this);
        mImageAdapter = new ImagePagerAdapterForPublish(this, this);
        mViewPager.setAdapter(mImageAdapter);
        update();
    }

    private void update(){
        List<IChooserModel> mMediaTotal = new ArrayList<>();

        for(int i = 0; i < imageResId.length; i++) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), imageResId[i] , options);

            ChooserModel chooserModel = new ChooserModel();
            chooserModel.setId(imageResId[i]);
            chooserModel.setHeight(options.outHeight);
            chooserModel.setWidth(options.outWidth);
            mMediaTotal.add(chooserModel);
        }
        mImageAdapter.setData(mMediaTotal, mTagBeansMap, (int)UIUtils.dip2Px(this, 20));
    }

    public void onTagSelected(ITagBean tagBean) {
        if(tagBean != null && mImageAdapter != null) {
            View itemView = mImageAdapter.getPrimaryItem();
            if (itemView instanceof PictureTagFrameLayout) {
                tagBean.setSx(mPointX);
                tagBean.setSy(mPointY);
                ((PictureTagFrameLayout) itemView).addItem(tagBean);
            }
        }
    }

    @Override
    public void onSingleClick(float x, float y) {
        mPointX = x;
        mPointY = y;
        openTagActvty();
    }


    private void openTagActvty(){
        Intent intent = new Intent(MainActivity.this, TagsActivity.class);
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_GET_TAG) {
            if (data != null) {
                TagBean tagBean = data.getParcelableExtra(BUNDLE_TAG_BEAN);
                if (tagBean != null) {
                    onTagSelected(tagBean);
                }
            }
        }
    }

    @Override
    public void onTagViewMoving() {

    }

    @Override
    public void onTagViewStopMoving() {

    }
}
