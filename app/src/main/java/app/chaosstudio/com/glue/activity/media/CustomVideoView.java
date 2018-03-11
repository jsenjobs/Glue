package app.chaosstudio.com.glue.activity.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import io.vov.vitamio.widget.VideoView;

/**
 * Created by jsen on 2018/2/3.
 */

public class CustomVideoView extends VideoView {
    public static final int VIDEO_LAYOUT_MINI = 5;
    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * Set the display options
     *
     * @param layout      <ul>
     *                    <li>{@link #VIDEO_LAYOUT_ORIGIN}
     *                    <li>{@link #VIDEO_LAYOUT_SCALE}
     *                    <li>{@link #VIDEO_LAYOUT_STRETCH}
     *                    <li>{@link #VIDEO_LAYOUT_FIT_PARENT}
     *                    <li>{@link #VIDEO_LAYOUT_ZOOM}
     *                    </ul>
     * @param aspectRatio video aspect ratio, will audo detect if 0.
     */
    @Override
    public void setVideoLayout(int layout, float aspectRatio) {
        super.setVideoLayout(layout, aspectRatio);
        if (VIDEO_LAYOUT_MINI == layout) {
            ViewGroup.LayoutParams lp = getLayoutParams();
            lp.width = 1;
            lp.height = 1;
            setLayoutParams(lp);
        }
    }
}
