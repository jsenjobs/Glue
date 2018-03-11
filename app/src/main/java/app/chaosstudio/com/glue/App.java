package app.chaosstudio.com.glue;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

import app.chaosstudio.com.glue.greendb.gen.DaoMaster;
import app.chaosstudio.com.glue.greendb.gen.DaoSession;

/**
 * Created by jsen on 2018/1/21.
 */
public class App extends Application {
    private DaoMaster.OpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    public static App instances;

    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        setDatabase();
    }
    public static App getInstances(){
        return instances;
    }
    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        if (mDaoMaster == null) {
            synchronized (DaoMaster.class) {
                if (mDaoMaster == null) {
                    mHelper = new DaoMaster.OpenHelper(this, "glue-db", null) {
                        @Override
                        public void onUpgrade(Database db, int oldVersion, int newVersion) {
                            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
                            /*
                            Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name;", new String[]{});
                            while (cursor.moveToNext()) {
                                Log.e("MARK", cursor.getString(0));
                            }
                            */
                            // String update = "drop table DOWNLOAD_MODE;";
                            // db.execSQL(update);
                            DaoMaster.createAllTables(db, true);
                        }
                    };
                    db = mHelper.getWritableDatabase();
                    // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
                    mDaoMaster = new DaoMaster(db);
                    mDaoSession = mDaoMaster.newSession();
                }
            }
        }
        // mHelper = new DaoMaster.DevOpenHelper(this, "glue-db", null);
        // db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        // mDaoMaster = new DaoMaster(db);
        // mDaoSession = mDaoMaster.newSession();
    }
    public DaoSession getDaoSession() {
        return mDaoSession;
    }
    public SQLiteDatabase getDb() {
        return db;
    }
}