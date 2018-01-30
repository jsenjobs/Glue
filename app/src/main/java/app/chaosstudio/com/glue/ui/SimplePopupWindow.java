package app.chaosstudio.com.glue.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by jsen on 2018/1/22.
 */

public class SimplePopupWindow extends PopupWindow {

    private int showX = 0;
    private int showY = 0;
    private View anchorView;
    public SimplePopupWindow(View anchorView, View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        setBackgroundDrawable(new ColorDrawable());

        // 设置好参数之后再show
        if (anchorView != null) {
            int windowPos[] = calculatePopWindowPos(anchorView, contentView, 20);
            showX = windowPos[0];
            showY = windowPos[1];
            this.anchorView = anchorView;
        }
    }
    public void updateAnchor(View anchorView) {
        // 设置好参数之后再show
        int windowPos[] = calculatePopWindowPos(anchorView, getContentView(), 20);
        showX = windowPos[0];
        showY = windowPos[1];
        this.anchorView = anchorView;
    }
    public void updateAnchor(View anchorView, int x, int y) {
        // 设置好参数之后再show
        int windowPos[] = calculatePopWindowPosCenter(anchorView, getContentView(), x, y, 20, 0);
        showX = windowPos[0];
        showY = windowPos[1];
        this.anchorView = anchorView;
    }
    public void updateAnchor(View anchorView, int x, int y, int offX, int offY) {
        // 设置好参数之后再show
        int windowPos[] = calculatePopWindowPosCenter(anchorView, getContentView(), x, y, offX, offY);
        showX = windowPos[0];
        showY = windowPos[1];
        this.anchorView = anchorView;
    }
    public void updateAnchor2(View anchorView, int x, int y) {
        // 设置好参数之后再show
        int windowPos[] = calculatePopWindowPosCenter2(anchorView, getContentView(), x, y, 20, 0);
        showX = windowPos[0];
        showY = windowPos[1];
        this.anchorView = anchorView;
    }
    public void updateCenter(View anchorView) {
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        // 测量contentView
        View contentView = getContentView();
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();

        showX = (screenWidth - windowWidth) / 2;
        showY = (screenHeight - windowHeight) / 2;
        this.anchorView = anchorView;
    }
    public void showAtLocation(int gravity) {
        // Gravity.TOP | Gravity.START
        showAtLocation(anchorView, gravity, showX, showY);
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     * @param anchorView  呼出window的view
     * @param contentView   window的内容布局
     * @return window显示的左上角的xOff,yOff坐标
     */
    private static int[] calculatePopWindowPos(final View anchorView, final View contentView, int xOff) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        // 测量contentView
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            // windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            // windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        if (anchorLoc[0] + windowWidth + xOff > screenWidth) {
            windowPos[0] = screenWidth - windowWidth - xOff;
        } else {
            windowPos[0] = anchorLoc[0] + xOff;
        }
        return windowPos;
    }
    private static int[] calculatePopWindowPosCenter(final View anchorView, final View contentView, int tX, int tY, int xOff, int yOff) {
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        // 测量contentView
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();

        tX -= windowWidth / 2;
        tY -= windowHeight / 2;

        if (tY < yOff) tY = yOff;
        if (tY + windowHeight + yOff > screenHeight) tY = screenHeight - windowHeight - yOff;

        if (tX < xOff) tX = xOff;
        if (tX + windowWidth + xOff > screenWidth) tX = screenWidth - windowWidth - xOff;

        return new int[]{tX, tY};
    }
    private static int[] calculatePopWindowPosCenter2(final View anchorView, final View contentView, int tX, int tY, int xOff, int yOff) {
        // 获取屏幕的高宽
        final int screenHeight = anchorView.getHeight();
        final int screenWidth = anchorView.getWidth();
        // 测量contentView
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();

        tX -= windowWidth / 2;
        tY -= windowHeight / 2;

        if (tY < yOff) tY = yOff;
        if (tY + windowHeight + yOff > screenHeight) tY = screenHeight - windowHeight - yOff;

        if (tX < xOff) tX = xOff;
        if (tX + windowWidth + xOff > screenWidth) tX = screenWidth - windowWidth - xOff;
        tX = (int)(anchorView.getX() + tX);
        tY = (int)(anchorView.getY() + tY);
        return new int[]{tX, tY};
    }

    /**
     * 获取屏幕高度(px)
     */
    private static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    /**
     * 获取屏幕宽度(px)
     */
    private static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    private static int dp2px(Context context, float dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()) + 0.5f);
    }
}
