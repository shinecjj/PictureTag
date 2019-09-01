package com.example.picturetag.UIUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UIUtils {

    public static final char ELLIPSIS_CHAR = '…';
    public static final int LAYOUT_PARAMS_KEEP_OLD = -3;
    public static final boolean API_ET_20;
    private static String sScreenResolution;
    private static int DPI;
    public static UIUtils.EllipsisMeasureResult sTempEllipsisResult;

    public UIUtils() {
    }

    public static float sp2px(Context context, float sp) {
        return TypedValue.applyDimension(2, sp, context.getResources().getDisplayMetrics());
    }

    public static float dip2Px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5F;
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }

    public static final int getScreenWidth(Context context) {
        if (context == null) {
            return 0;
        } else {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            return dm == null ? 0 : dm.widthPixels;
        }
    }

    public static final int getRatioOfScreen(Context context, float ratio) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm == null ? 0 : (int)((float)dm.widthPixels * ratio);
    }

    public static boolean isInUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static final int getScreenHeight(Context context) {
        if (context == null) {
            return 0;
        } else {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            return dm == null ? 0 : dm.heightPixels;
        }
    }

    public static int getDpi(Context context) {
        if (DPI == -1 && context != null) {
            DPI = context.getApplicationContext().getResources().getDisplayMetrics().densityDpi;
        }

        return DPI;
    }

    public static int getDiggBuryWidth(Context context) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenWidth = screenWidth * 1375 / 10000 + (int)dip2Px(context, 20.0F);
        return screenWidth;
    }

    public static final int getStatusBarHeight(Context context) {
        if (context == null) {
            return 0;
        } else {
            int result = 0;
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }

            return result;
        }
    }

    private static boolean visibilityValid(int visiable) {
        return visiable == 0 || visiable == 8 || visiable == 4;
    }

    public static final void setViewVisibility(View v, int visiable) {
        if (v != null && v.getVisibility() != visiable && visibilityValid(visiable)) {
            v.setVisibility(visiable);
        }
    }

    public static void getLocationInUpView(View upView, View view, int[] loc, boolean getCenter) {
        if (upView != null && view != null && loc != null && loc.length >= 2) {
            upView.getLocationInWindow(loc);
            int x1 = loc[0];
            int y1 = loc[1];
            view.getLocationInWindow(loc);
            int x2 = loc[0] - x1;
            int y2 = loc[1] - y1;
            if (getCenter) {
                int w = view.getWidth();
                int h = view.getHeight();
                x2 += w / 2;
                y2 += h / 2;
            }

            loc[0] = x2;
            loc[1] = y2;
        }
    }

    public static void updateLayout(View view, int w, int h) {
        if (view != null) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params != null && (params.width != w || params.height != h)) {
                if (w != -3) {
                    params.width = w;
                }

                if (h != -3) {
                    params.height = h;
                }

                view.setLayoutParams(params);
            }
        }
    }

    public static void updateLayoutMargin(View view, int l, int t, int r, int b) {
        if (view != null) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params != null) {
                if (params instanceof ViewGroup.MarginLayoutParams) {
                    updateMargin(view, (ViewGroup.MarginLayoutParams)params, l, t, r, b);
                }

            }
        }
    }

    private static void updateMargin(View view, ViewGroup.MarginLayoutParams params, int l, int t, int r, int b) {
        if (view != null && params != null && (params.leftMargin != l || params.topMargin != t || params.rightMargin != r || params.bottomMargin != b)) {
            if (l != -3) {
                params.leftMargin = l;
            }

            if (t != -3) {
                params.topMargin = t;
            }

            if (r != -3) {
                params.rightMargin = r;
            }

            if (b != -3) {
                params.bottomMargin = b;
            }

            view.setLayoutParams(params);
        }
    }

    public static void setTopMargin(View view, float topMarginInDp) {
        if (view != null) {
            DisplayMetrics dm = view.getContext().getResources().getDisplayMetrics();
            int topMargin = (int)TypedValue.applyDimension(1, topMarginInDp, dm);
            updateLayoutMargin(view, -3, topMargin, -3, -3);
        }
    }

    public static void setLayoutParams(View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            if (width != -2147483648) {
                params.width = width;
            }

            if (height != -2147483648) {
                params.height = height;
            }

        }
    }

    public static void setTxtAndAdjustVisible(TextView tv, CharSequence txt) {
        if (tv != null) {
            if (TextUtils.isEmpty(txt)) {
                setViewVisibility(tv, 8);
            } else {
                setViewVisibility(tv, 0);
                tv.setText(txt);
            }

        }
    }

    public static void setText(TextView textView, CharSequence text) {
        if (textView != null && !TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
    }

    @SuppressLint({"NewApi"})
    public static void setViewMinHeight(View view, int minHeight) {
        if (view != null) {
            if (Build.VERSION.SDK_INT < 16 || view.getMinimumHeight() != minHeight) {
                view.setMinimumHeight(minHeight);
            }
        }
    }

    public static int setColorAlpha(int color, int alpha) {
        if (alpha > 255) {
            alpha = 255;
        } else if (alpha < 0) {
            alpha = 0;
        }

        return color & 16777215 | alpha * 16777216;
    }

    public static boolean clearAnimation(View view) {
        if (view != null && view.getAnimation() != null) {
            view.clearAnimation();
            return true;
        } else {
            return false;
        }
    }

    public static void setClickListener(boolean clickable, View view, View.OnClickListener clickListener) {
        if (view != null) {
            if (clickable) {
                view.setOnClickListener(clickListener);
                view.setClickable(true);
            } else {
                view.setOnClickListener((View.OnClickListener)null);
                view.setClickable(false);
            }

        }
    }

    static {
        API_ET_20 = Build.VERSION.SDK_INT > 19;
        sScreenResolution = "";
        DPI = -1;
        sTempEllipsisResult = new UIUtils.EllipsisMeasureResult();
    }

    public static class EllipsisMeasureResult {
        public String ellipsisStr;
        public int length;

        public EllipsisMeasureResult() {
        }
    }

}
