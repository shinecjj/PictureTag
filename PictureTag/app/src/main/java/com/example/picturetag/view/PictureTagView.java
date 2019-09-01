package com.example.picturetag.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.picturetag.R;
import com.example.picturetag.UIUtils.UIUtils;
import com.example.picturetag.bean.ITagBean;

import static com.example.picturetag.bean.ITagBean.ARROW_LEFT;
import static com.example.picturetag.bean.ITagBean.ARROW_RIGHT;
import static com.example.picturetag.bean.ITagBean.TYPE_GENERAL;
import static com.example.picturetag.bean.ITagBean.TYPE_GOODS;


public class PictureTagView extends FrameLayout{


    private FrameLayout mPointFramLayout;

    /**
     * left view
     */
    private LinearLayout mTagLeftLayout;
    private ImageView mTagLeftImageView;
    private TextView mTagLeftTextView;
    private View mLeftWhiteLineView;

    /**
     * right view
     */
    private LinearLayout mTagRightLayout;
    private ImageView mTagRightImageView;
    private TextView mTagRightTextView;
    private View mRightWhiteLineView;

    /**
     * 为了增加铆点触区
     */
    private float dp5;

    /**
     * data
     */
    private ITagBean mTagBean;
    private int mViewWidth;
    private int mViewHeight;
    private float dp25;
    private float dp17;
    private float dp12;
    private boolean mHasByAdded = false;


    public PictureTagView(Context context, ITagBean tagBean) {
        super(context);
        mTagBean = tagBean;
        init(context);
    }

    public PictureTagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PictureTagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        if(context == null){
            return;
        }
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        dp25 = UIUtils.dip2Px(context, 25);
        dp17 = UIUtils.dip2Px(context, 17);
        dp12 = UIUtils.dip2Px(context, 12);
        dp5 = UIUtils.dip2Px(context, 5);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_picture_tag_view, this);
        initViews(view);
    }

    private void initViews(View itemView){

        mPointFramLayout =  itemView.findViewById(R.id.layout_point);

        //left
        mTagLeftLayout =  itemView.findViewById(R.id.layout_pic_tag_left);
        mTagLeftLayout.setVisibility(GONE);
        mLeftWhiteLineView = itemView.findViewById(R.id.view_white_line_left);
        mLeftWhiteLineView.setVisibility(GONE);
        mTagLeftImageView = itemView.findViewById(R.id.image_pic_tag_left);
        mTagLeftTextView = itemView.findViewById(R.id.text_pic_tag_left);

        //right
        mTagRightLayout =  itemView.findViewById(R.id.layout_pic_tag_right);
        mTagRightLayout.setVisibility(GONE);
        mRightWhiteLineView = itemView.findViewById(R.id.view_white_line_right);
        mRightWhiteLineView.setVisibility(GONE);
        mTagRightImageView = itemView.findViewById(R.id.image_pic_tag_right);
        mTagRightTextView = itemView.findViewById(R.id.text_pic_tag_right);

        //加载动画
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.circle);
        mPointFramLayout.startAnimation(animation);

        setContentImageVisible(GONE);
        update();
    }

    private void update(){
        if(mTagBean == null){
            return;
        }

        if(TYPE_GENERAL == mTagBean.getType()){
            setContentImageVisible(GONE);
        } else if(TYPE_GOODS == (mTagBean.getType())){
            setContentImageVisible(VISIBLE);
        }

        if(ARROW_RIGHT == getArrow()){
            setLeftVisibleRightGone(false);
        } else if(ARROW_LEFT == getArrow()){
            setLeftVisibleRightGone(true);
        }

        setTagName(mTagBean.getTagName());

        measureViewWH();
    }

    private void setLeftVisibleRightGone(boolean visible){
        if(visible) {
            mTagLeftLayout.setVisibility(VISIBLE);
            mLeftWhiteLineView.setVisibility(VISIBLE);

            mTagRightLayout.setVisibility(GONE);
            mRightWhiteLineView.setVisibility(GONE);
        }else {
            mTagLeftLayout.setVisibility(GONE);
            mLeftWhiteLineView.setVisibility(GONE);

            mTagRightLayout.setVisibility(VISIBLE);
            mRightWhiteLineView.setVisibility(VISIBLE);
        }
    }

    private void setTagName(String tagName){
        if(!TextUtils.isEmpty(tagName)) {
            if(tagName.length() > 10) {
                tagName = tagName.substring(0,10) + "...";
            }
            mTagLeftTextView.setText(tagName);
            mTagRightTextView.setText(tagName);
        }
    }

    private void setContentImageVisible(int visible){

        mTagLeftImageView.setVisibility(visible);
        mTagRightImageView.setVisibility(visible);

        if(mTagLeftTextView.getLayoutParams() instanceof LinearLayout.LayoutParams
                && mTagRightTextView.getLayoutParams() instanceof LinearLayout.LayoutParams) {

            LinearLayout.LayoutParams lpLeft = (LinearLayout.LayoutParams) mTagLeftTextView.getLayoutParams();
            LinearLayout.LayoutParams lpRight = (LinearLayout.LayoutParams) mTagRightTextView.getLayoutParams();

            if (visible == GONE) {
                lpLeft.setMargins((int) dp12, 0, (int) dp12, 0);
                lpRight.setMargins((int) dp12, 0, (int) dp12, 0);
            } else {
                lpLeft.setMargins(0, 0, (int) dp12, 0);
                lpRight.setMargins(0, 0, (int) dp12, 0);
            }
        }
    }

    private void measureViewWH(){
        int w = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        this.measure(w, h);
        mViewWidth = this.getMeasuredWidth();
        mViewHeight = this.getMeasuredHeight();
    }

    public void setHasByAdded(boolean hasByAdded){
        mHasByAdded = hasByAdded;
    }

    public boolean isHasByAdded() {
        return mHasByAdded;
    }

    public ITagBean getTagBean(){
        return mTagBean;
    }

    public void setTagBean(ITagBean tagBean){
        mTagBean = tagBean;
    }

    private int getArrow(){
        if(mTagBean != null){
            return mTagBean.getArrow();
        }else {
            return -1;
        }
    }

    public void setArrow(int arrow){
        if(mTagBean != null){
            mTagBean.setArrow(arrow);
        }
    }

    public boolean isLeftArrow(){
        if(ARROW_LEFT == getArrow()){
            return true;
        }else {
            return false;
        }
    }

    public boolean isRightArrow(){
        if(ARROW_RIGHT == getArrow()){
            return true;
        }else {
            return false;
        }
    }

    public void directionChange() {

        if(ARROW_RIGHT == getArrow()){
            setArrow(ARROW_LEFT);
            setLeftVisibleRightGone(true);

        } else if(ARROW_LEFT == getArrow()){
            setArrow(ARROW_RIGHT);
            setLeftVisibleRightGone(false);
        }

        measureViewWH();
    }


    /**
     * 锚点坐标 转换为 左上角坐标
     */
    public float[] pointXY2LTXY(float pointX, float pointY){
        float[] ltXY = new float[2];
        if(ARROW_RIGHT == getArrow()){
             ltXY[0] = pointX - dp17/2 - dp5;
             ltXY[1] = pointY - dp25/2;
        } else if(ARROW_LEFT == getArrow()){
            ltXY[0] = pointX - (mViewWidth - dp5 - dp17/2);
            ltXY[1] = pointY - dp25/2;
        }
        return ltXY;
    }


    /**
     * 左上角坐标 转换为 锚点坐标
     */
    public float[] ltXY2PointXY(float ltX, float ltY){
        float[] pointXY = new float[2];
        if(ARROW_RIGHT == getArrow()){
            pointXY[0] = ltX + dp17/2 + dp5;
            pointXY[1] = ltY + dp25/2;
        } else if(ARROW_LEFT == getArrow()){
            pointXY[0] = ltX + (mViewWidth - dp5 -  dp17/2);
            pointXY[1] = ltY + dp25/2;
        }
        return pointXY;
    }
}
