package com.example.picturetag.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.picturetag.UIUtils.UIUtils;
import com.example.picturetag.bean.ITagBean;

import java.util.ArrayList;
import java.util.List;

import static com.example.picturetag.bean.ITagBean.ARROW_LEFT;
import static com.example.picturetag.bean.ITagBean.ARROW_RIGHT;

@SuppressLint("NewApi")
public class PictureTagFrameLayout extends FrameLayout{
    private static final int CLICKRANGE = 5;

    /**
     * view
     */
    private PictureTagView mTouchView;
    private List<PictureTagView> mTagViewList;

    /**
     * data
     */
    private List<ITagBean> mTagBeanList;
    private ITagLayoutCallBack mTagLayoutCallBack;
    private RectF mPhotoRectF;           //图片相对于framlayout的左上右下
    private float mXUp, mYUp;
    private float mStartX, mStartY;
    private float mXDown, mYDown;
    private float mTouchX, mTouchY;
    private float dp27, dp25;
    private float TAG_VIEW_HEIGHT;
    private float TAG_VIEW_POINT_WIDTH;
    private float mImageWHRatio;


    public PictureTagFrameLayout(Context context) {
        this(context, null);
    }

    public PictureTagFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        if(context == null){
            return;
        }
        setClipChildren(false);
        setClipToPadding(false);

        dp27 = UIUtils.dip2Px(context, 27);
        dp25 = UIUtils.dip2Px(context, 25);
        TAG_VIEW_HEIGHT = dp25;
        TAG_VIEW_POINT_WIDTH = dp27;
    }

    public void setTagLayoutCallBack(ITagLayoutCallBack tagLayoutCallBack){
        mTagLayoutCallBack = tagLayoutCallBack;
    }

    public void notifyAddTagViewBasePhotoRect(RectF rect){
        if(rect == null || rect.height() == 0 || rect.width() == 0){
            return;
        }

        mPhotoRectF = rect;
        if (mTagViewList != null && mTagViewList.size() > 0) {
            for (PictureTagView pictureTagView : mTagViewList) {
                if(pictureTagView != null) {
                    setTagViewLocation(pictureTagView);
                    addTagView(pictureTagView);
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            int contentH = h - getPaddingBottom();
            if(contentH > 0) {
                float layoutWHRatio = 1.0f * w / contentH;
                float left = 0, top = 0, right = w, bottom = contentH;
                if (layoutWHRatio < mImageWHRatio) {

                    if(mImageWHRatio > 0) {
                        float imageHeight = w / mImageWHRatio;
                        top = (contentH - imageHeight) / 2f;
                        bottom = imageHeight + top;
                    }

                } else if (layoutWHRatio > mImageWHRatio) {

                    float imageWidth = contentH * mImageWHRatio;
                    left = (w - imageWidth) / 2f;
                    right = left + imageWidth;

                }

                mPhotoRectF = new RectF(left, top, right, bottom);

                if (mTagViewList != null && mTagViewList.size() > 0) {
                    for (PictureTagView pictureTagView : mTagViewList) {
                        if(pictureTagView != null) {
                            setTagViewLocation(pictureTagView);
                            addTagView(pictureTagView);
                        }
                    }
                }
            }
        }
    }

    public void updateTagViews(List<ITagBean> list, float imageWHRatio){
        if(list == null){
            list = new ArrayList<>();

        }
        mTagBeanList = list;
        mImageWHRatio = imageWHRatio;

        /**
         * 优化：初始化时先生成Views放在List中，后面滑到该页面可直接使用
         */
        if(list != null && !list.isEmpty()) {
            if (mTagViewList == null) {
                mTagViewList = new ArrayList<>();
            }
            for(int i = 0; i < list.size() && i < ITagBean.MAX_TAG_COUNT; i++) {
                PictureTagView pictureTagView = createTagView(list.get(i));
                if(pictureTagView != null) {
                    mTagViewList.add(pictureTagView);
                }
            }
        }
    }

    /**
     * 得到：左上角相对frameLayout的x,y坐标
     */
    private float[] getLtXY(PictureTagView pictureTagView){

        if(pictureTagView == null || mPhotoRectF == null){
            return new float[2];
        }

        ITagBean bean = pictureTagView.getTagBean();
        if(bean == null){
            return new float[2];
        }

        /**
         * 计算铆点坐标
         */
        float x4Photo = bean.getSx() * mPhotoRectF.width();    //铆点相对图片的x坐标
        float y4Photo = bean.getSy() * mPhotoRectF.height();   //铆点相对图片的y坐标
        float x4Layout = mPhotoRectF.left + x4Photo;      //铆点相对frameLayout的x坐标
        float y4Layout = mPhotoRectF.top + y4Photo;       //铆点相对frameLayout的y坐标

        /**
         * 铆点坐标 转换为 左上角坐标
         */
        return pictureTagView.pointXY2LTXY(x4Layout, y4Layout);
    }

    private void moveView(float x, float y) {

        if (mTouchView == null || mPhotoRectF == null) {
            return;
        }

        /**
         * 1、计算手指拖动距离
         */
        float dragX = x - mTouchX;
        float dragY = y - mTouchY;

        /**
         * 2、move事件的x,y出图片区域的处理
         */
        if(x >= mPhotoRectF.left && x <= mPhotoRectF.right) {
            mTouchX = x;
        }else if(x < mPhotoRectF.left){
            mTouchX = mPhotoRectF.left;
        }else if(x > mPhotoRectF.right){
            mTouchX = mPhotoRectF.right;
        }

        if(y >= mPhotoRectF.top && y <= getBottom()) {
            mTouchY = y;
        }else if(y < mPhotoRectF.top){
            mTouchY = mPhotoRectF.top;
        }else if(y > getBottom()){
            mTouchY = getBottom();
        }

        /**
         * 3、计算新的左上角坐标
         */
        float[] oldLTXY = getLtXY(mTouchView);                       //旧的左上角坐标
        float[] newLTXY = {oldLTXY[0] + dragX, oldLTXY[1] + dragY};  //新的左上角坐标

        /**
         * 4、限制tagView在图片内移动
         */
         handleNewLTXY(mTouchView, newLTXY);
        float newLTX = newLTXY[0];
        float newLTY = newLTXY[1];

        /**
         * 5、计算新铆点坐标
         */
        float[] newPointXY = mTouchView.ltXY2PointXY(newLTX, newLTY);

        /**
         * 6、计算新铆点坐标比例，并更新数据
         */
        if(newPointXY != null && newPointXY.length >= 2
                && mPhotoRectF != null && mPhotoRectF.width() > 0 && mPhotoRectF.height() > 0) {

            float newXRatio = (newPointXY[0] - mPhotoRectF.left) / mPhotoRectF.width();
            float newYRatio = (newPointXY[1] - mPhotoRectF.top) / mPhotoRectF.height();
            ITagBean tagBean = mTouchView.getTagBean();
            if (tagBean != null) {
                tagBean.setSx(newXRatio);
                tagBean.setSy(newYRatio);
            }
        }

        /**
         * 7、更新tag位置
         */
        setTagViewLocation(mTouchView);

        if(mTagLayoutCallBack != null){
            mTagLayoutCallBack.onTagViewMoving();
        }
    }


    private boolean deleteTagView(PictureTagView tagView){
        if(tagView == null){
            return false;
        }

        float[] ltXY = getLtXY(tagView);
        if(ltXY[1] > getBottom() - getPaddingBottom()){
            if(mTagViewList != null){
                mTagViewList.remove(tagView);
            }
            if(mTagBeanList != null){
                mTagBeanList.remove(tagView.getTagBean());
            }
            PictureTagFrameLayout.this.removeView(tagView);
            tagView = null;
            return true;
        }
        return false;
    }


    /**
     * 根据tagView不超出mPhotoRectF边界为原则，处理变换后的左上角的坐标
     * @param tagView
     * @param newLTXY
     */
    private void handleNewLTXY(PictureTagView tagView, float[] newLTXY){

        if(tagView == null || newLTXY == null || newLTXY.length < 2 || mPhotoRectF == null){
            return;
        }

        /**
         * 1、计算tagView的l,t,r,b, 得到
         */
        float newL = newLTXY[0];        //新的相对于framlayout的left
        float newT = newLTXY[1];        //新的相对于framlayout的top
        float newR = newL + tagView.getMeasuredWidth();         //新的相对于framlayout的right
        float newB = newT + tagView.getMeasuredHeight();        //新的相对于framlayout的bottom


        /**
         * 2、判断是否在photo的RectF内
         *  getBottom() 是因为 需要可以拖到删除区域
         */
        if(newL < mPhotoRectF.left){
            newLTXY[0] = mPhotoRectF.left;
        }else if(newR > mPhotoRectF.right){
            newLTXY[0] = mPhotoRectF.right - tagView.getMeasuredWidth() ;
        }

        if(newT < mPhotoRectF.top){
            newLTXY[1] = mPhotoRectF.top;
        }else if(newB > getBottom()){
            newLTXY[1] = getBottom() - tagView.getMeasuredHeight();
        }
    }


    private boolean checkTagBean(ITagBean bean){
        if(bean == null || TextUtils.isEmpty(bean.getTagName())){
            return false;
        }
        if(bean.getSx() <= 0 || bean.getSx() >= 1){
            return false;
        }
        if(bean.getSy() <= 0 || bean.getSy() >= 1){
            return false;
        }
        return true;
    }

    public void addItem(ITagBean bean) {

        if(!checkTagBean(bean)){
            return;
        }

        /**
         * 1、生成tagView
         */
        PictureTagView tagView = createTagView(bean);

        /**
         * 2、设置坐标
         */
        setTagViewLocation(tagView);

        if (mTagViewList == null) {
            mTagViewList = new ArrayList<>();
        }else if(mTagViewList.size() >= ITagBean.MAX_TAG_COUNT){
            Toast.makeText(getContext(), "最多可添加15个标签", Toast.LENGTH_LONG);
            return;
        }
        mTagViewList.add(tagView);

        if(mTagBeanList == null) {
            mTagBeanList = new ArrayList<>();
        }else if(mTagBeanList.size() >= ITagBean.MAX_TAG_COUNT){
            Toast.makeText(getContext(), "最多可添加15个标签", Toast.LENGTH_LONG);
            return;
        }
        mTagBeanList.add(bean);

        addTagView(tagView);
    }

    private boolean addTagView(PictureTagView tagView){
        if(tagView == null){
            return false;
        }
        if(!tagView.isHasByAdded()){
            addView(tagView);
            tagView.setHasByAdded(true);
            return true;
        }
        return false;
    }

    private PictureTagView createTagView(ITagBean bean){

        if(bean == null || bean.isHasAdded()){
            return null;
        }

        PictureTagView tagView = null;
        if(ARROW_RIGHT == bean.getArrow() || ARROW_LEFT == bean.getArrow()) {
            tagView = new PictureTagView(getContext(), bean);
        }else {
            if (bean.getSx() > 0.5) {//Right是指 点在右边
                bean.setArrow(ARROW_LEFT);
                tagView = new PictureTagView(getContext(), bean);
            } else {
                bean.setArrow(ARROW_RIGHT);
                tagView = new PictureTagView(getContext(), bean);
            }
        }
        bean.setHasAdded(true);
        return tagView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        mXDown = ev.getX();
        mYDown = ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchView = null;
                /**
                 * 1、点击到tag上后，拦截事件
                 * 2、禁止父view拦截事件（防止父view -- viewPage 拦截事件进行横划操作）
                 */
                if (hasView(mXDown, mYDown)) {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                //手指拖动前坐标
                mTouchX = mStartX;
                mTouchY = mStartY;

                break;
            case MotionEvent.ACTION_MOVE:

                /**
                 * 随手指滑动
                 */
                if(!isSingleClick(mStartX, event.getX(), mStartY, event.getY())) {
                    moveView(event.getX(), event.getY());
                }

                break;
            case MotionEvent.ACTION_UP:

                mXUp = (int) event.getX();
                mYUp = (int) event.getY();

                if(mTagLayoutCallBack != null){
                    mTagLayoutCallBack.onTagViewStopMoving();
                }

                /**
                 * 单击事件，并且单击到铆点上，则转换方向
                 */
                if (mTouchView != null && isSingleClick(mStartX, mXUp, mStartY, mYUp)) {
                    if (isOnViewPoint(mXUp, mYUp)) {
                        changeTagViewDirection(mTouchView);
                    }
                }

                /**
                 * 滑动到底部删除区域则进行删除
                 */
                if(!deleteTagView(mTouchView)) {
                    /**
                     * 滑动超出photo下面区域，up时重置其位置
                     */
                    resetTagViewLocationWhenUp(mTouchView);
                }

                break;
        }
        return true;
    }

    private void changeTagViewDirection(PictureTagView tagView){
        if(tagView == null){
            return;
        }

        /**
         * 1、转换方向
         */
        tagView.directionChange();

        /**
         * 2、左上角相对frameLayout的x,y坐标
         */
        float[] newLTXY = getLtXY(tagView);

        /**
         * 3、根据不超出边界重新赋值newLTXY
         */
        handleNewLTXY(tagView, newLTXY);

        /**
         * 4、根据重新赋值的newLTXY重新set铆点坐标比例
         */
        float[] pointXY = tagView.ltXY2PointXY(newLTXY[0], newLTXY[1]);
        ITagBean tagBean = tagView.getTagBean();
        if(tagBean != null && pointXY != null && pointXY.length >= 2
                && mPhotoRectF != null && mPhotoRectF.width() > 0 && mPhotoRectF.height() > 0){
            float newXRatio = (pointXY[0] - mPhotoRectF.left) / mPhotoRectF.width();
            float newYRatio = (pointXY[1] - mPhotoRectF.top) / mPhotoRectF.height();
            tagBean.setSx(newXRatio);
            tagBean.setSy(newYRatio);
        }

        /**
         * 5、设置tagView的位置
         */
        setTagViewLocation(tagView);
    }

    private void resetTagViewLocationWhenUp(PictureTagView tagView){
        if(tagView == null || mPhotoRectF == null){
            return;
        }

        float[] newLTXY = getLtXY(tagView);

        /**
         * 1、计算tagView的l,t,r,b, 得到
         */
        float newT = newLTXY[1];        //新的相对于framlayout的top
        float newB = newT + tagView.getMeasuredHeight();        //新的相对于framlayout的bottom


        /**
         * 2、判断是否超过了photo的RectF的底部
         */
        if(newB > mPhotoRectF.bottom) {

            newLTXY[1] = mPhotoRectF.bottom - tagView.getMeasuredHeight();

            /**
             * 3、根据重新赋值的newLTXY重新set铆点坐标比例
             */
            float[] pointXY = tagView.ltXY2PointXY(newLTXY[0], newLTXY[1]);
            ITagBean tagBean = tagView.getTagBean();
            if (tagBean != null && pointXY != null && pointXY.length >= 2
                    && mPhotoRectF != null && mPhotoRectF.width() > 0 && mPhotoRectF.height() > 0) {
                float newXRatio = (pointXY[0] - mPhotoRectF.left) / mPhotoRectF.width();
                float newYRatio = (pointXY[1] - mPhotoRectF.top) / mPhotoRectF.height();
                tagBean.setSx(newXRatio);
                tagBean.setSy(newYRatio);
            }

            /**
             * 4、重新设置位置
             */
            setTagViewLocation(tagView);
        }
    }

    private void setTagViewLocation(PictureTagView tagView){
        if(tagView == null){
            return;
        }

        /**
         * 得到：左上角相对frameLayout的x,y坐标
         */
        float[] ltXY = getLtXY(tagView);
        if(ltXY != null) {
            tagView.setX(ltXY[0]);
            tagView.setY(ltXY[1]);
        }
    }

    /**
     * 循环获取子view，判断xy是否在子view上，即判断是否按住了子view
     */
    private boolean hasView(float x, float y) {
        for (int index = 0; index < this.getChildCount(); index++) {
            View view = this.getChildAt(index);

            if (!(view instanceof PictureTagView)) {
                continue;
            }

            float left =  view.getX();
            float top =  view.getY();
            float right = view.getWidth() + left;
            float bottom = view.getHeight() + top;

            RectF rectf = new RectF(left, top, right, bottom);
            boolean contains = rectf.contains(x, y);
            if (contains) {
                mTouchView = (PictureTagView) view;
                mTouchView.bringToFront();
                return true;
            }
        }
        mTouchView = null;
        return false;
    }

    /**
     * 循环获取子view，判断xy是否在tagView的铆点区域上
     */
    private boolean isOnViewPoint(float x, float y) {
        for (int index = 0; index < this.getChildCount(); index++) {
            View view = this.getChildAt(index);

            if (!(view instanceof PictureTagView)) {
                continue;
            }

            /**
             * 1、计算tagView左上角的坐标（相对于此framLayout）
             */
            float ltX = view.getX();          //tagView左上角的x坐标
            float ltY = view.getY();          //tagView左上角的y坐标

            /**
             * 2、计算铆点区域的RectF
             */
            RectF rectF = new RectF();
            if(((PictureTagView) view).isRightArrow()){
                float pointLeft = ltX;
                float pointTop = ltY;
                float pointRight = pointLeft + TAG_VIEW_POINT_WIDTH;
                float pointBottom = pointTop + TAG_VIEW_HEIGHT;
                rectF.set(pointLeft, pointTop, pointRight, pointBottom);
            }else if(((PictureTagView) view).isLeftArrow()){
                float pointLeft = ltX + view.getMeasuredWidth() - TAG_VIEW_POINT_WIDTH;
                float pointTop = ltY;
                float pointRight = pointLeft + TAG_VIEW_POINT_WIDTH;
                float pointBottom = pointTop + TAG_VIEW_HEIGHT;
                rectF.set(pointLeft, pointTop, pointRight, pointBottom);
            }

            /**
             * 3、判断点击位置是否在rectF中
             */
            boolean contains = rectF.contains(x, y);
            if (contains) {
                mTouchView = (PictureTagView) view;
                mTouchView.bringToFront();
                return true;
            }
        }
        mTouchView = null;
        return false;
    }

    private boolean isSingleClick(float startX, float endX, float startY, float endY){
        if(Math.abs(endX - startX) < CLICKRANGE && Math.abs(endY - startY) < CLICKRANGE){
            return true;
        }else {
            return false;
        }
    }

    public interface ITagLayoutCallBack {
        void onSingleClick(float x, float y);
        void onTagViewMoving();
        void onTagViewStopMoving();
    }
}

