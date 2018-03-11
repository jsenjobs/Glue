package app.chaosstudio.com.glue.webconfig;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.chaosstudio.com.glue.App;
import app.chaosstudio.com.glue.greendb.gen.LogModeDao;
import app.chaosstudio.com.glue.greendb.gen.OpendedUrlDao;

/**
 * Created by jsen on 2018/1/21.
 */

public class WebViewManager {

    private static List<NWebView> list = new ArrayList<>();
    private static NWebView currentActive = null;
    private static int currentActiveIndex = -1;

    public static List<NWebView> getAllNWebView() {return list;}
    public static NWebView getCurrentActive() {
        return currentActive;
    }

    public static int getCurrentActiveIndex() {
        return currentActiveIndex;
    }

    public static NWebView active(int i) {
        if (i < list.size() && i >= 0) {
            NWebView webView = list.get(i);
            if (webView != null) {
                currentActive = webView;
                currentActiveIndex = i;
                return webView;
            }
        }
        return null;
    }
    public static NWebView active(NWebView nWebView) {
        int index = list.indexOf(nWebView);
        if (index >= 0) {
            currentActive = nWebView;
            currentActiveIndex = index;
            return nWebView;
        }
        return null;
    }
    public static void reloadPreferences() {
        for (NWebView webView:list) {
            if (webView.getWvConfig() != null) {
                webView.getWvConfig().initPreferences(webView);
                webView.reload();
            }
        }
    }
    public static void remove(int i) {
        if (i < list.size() && i >= 0) {
            remove(list.get(i));
        }
    }
    public static void remove(NWebView webView) {

        if (webView!=null) {
            App.instances.getDaoSession().getOpendedUrlDao().queryBuilder().where(OpendedUrlDao.Properties.Uuid.eq(webView.toString())).buildDelete().executeDeleteWithoutDetachingEntities();
            App.instances.getDaoSession().getLogModeDao().queryBuilder().where(LogModeDao.Properties.Uuid.eq(webView.toString())).buildDelete().executeDeleteWithoutDetachingEntities();

            list.remove(webView);
            currentActiveIndex = -1;
            currentActive = null;
        }
    }
    public static void add(NWebView nWebView) {
        list.add(nWebView);
    }
    public static void clear() {
        for (NWebView webView:list) {
            App.instances.getDaoSession().getOpendedUrlDao().queryBuilder().where(OpendedUrlDao.Properties.Uuid.eq(webView.toString())).buildDelete().executeDeleteWithoutDetachingEntities();
            App.instances.getDaoSession().getLogModeDao().queryBuilder().where(LogModeDao.Properties.Uuid.eq(webView.toString())).buildDelete().executeDeleteWithoutDetachingEntities();
        }
        list.clear();
    }
    public static int rebuildIndex(int i) {
        int index = i;
        int size = list.size();
        while (index >= size) index--;
        if (active(index) != null) return index;
        return -1;
    }
    public static int getSize() {
        return list.size();
    }
    public static NWebView get(int i) {
        return list.get(i);
    }

    // global config

    private static boolean isNight = false;
    private static boolean noHis = true;

    public static boolean isIsNight() {
        return isNight;
    }

    public static void setIsNight(boolean isNight) {
        WebViewManager.isNight = isNight;
    }

    public static boolean isNoHis() {
        return noHis;
    }

    public static void setNoHis(boolean noHis) {
        WebViewManager.noHis = noHis;
    }
}
