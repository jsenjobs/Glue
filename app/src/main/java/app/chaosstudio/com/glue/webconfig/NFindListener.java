package app.chaosstudio.com.glue.webconfig;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.WebView;

import app.chaosstudio.com.glue.eventb.FragmentAction;

/**
 * Created by jsen on 2018/1/26.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class NFindListener implements WebView.FindListener {
    @Override
    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
        FragmentAction.Companion.fire(FragmentAction.ACTION.ON_FIND_NUM, numberOfMatches);
    }

}
