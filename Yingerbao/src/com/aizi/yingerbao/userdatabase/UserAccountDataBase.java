package com.aizi.yingerbao.userdatabase;

import java.io.File;
import java.util.List;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Pair;

import com.aizi.yingerbao.logging.SLog;

public class UserAccountDataBase {
    private static InfoDbOpenHelper mDbHelper = null;
    private static DbErrorHandler mDbErrorHandler = null;
    private static Object myLock = new Object();
    private static final int DB_VERSION = 1;
    private static final String DB_DIR = "/database";
    private static final String DATA_DIR = "/data";
    private static final String TAG = "UserAccountDataBase";
    private static final String DB_NAME = "useraccountinfo.db";

    public static void close() {
        synchronized (myLock) {
            try {
                if (mDbHelper != null) {
                    mDbHelper.close();
                    mDbHelper = null;
                }
            } catch (Exception ex) {
                mDbHelper = null;
                SLog.e(TAG, "close db: " + ex);
            }
        }
    }

    // =================Private method=====================//
    // 内部操作，不加synchronous，防止死锁，同时db对象用传入的对象，也不做关闭操作

    public static SQLiteDatabase getDb(Context context) {
        InfoDbOpenHelper helper = getInfoDbOpenHelper(context);
        if (helper == null) {
            return null;
        }

        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
        } catch (Exception e) {
            SLog.e(TAG,  e);
        }
        return db;
    }

    private static InfoDbOpenHelper getInfoDbOpenHelper(Context context) {
        synchronized (myLock) {
            if (mDbHelper == null) {
                String dbName = null;
                File dbDir = new File(Environment.getDataDirectory().getAbsolutePath() + DATA_DIR + File.separator
                        + context.getPackageName() + DB_DIR);
                SLog.d(TAG, "File Path is  " + Environment.getDataDirectory().getAbsolutePath() 
                        + DATA_DIR + File.separator
                        + context.getPackageName() + DB_DIR);

                if (!dbDir.exists()) {
                    dbDir.mkdirs();
                }
                dbName = dbDir.getAbsolutePath() + File.separator + DB_NAME;
                SLog.d(TAG, "dbname is :" + dbName);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mDbErrorHandler = new DbErrorHandler();
                    mDbHelper = new InfoDbOpenHelper(context, dbName, DB_VERSION, mDbErrorHandler);
                } else {
                    mDbHelper = new InfoDbOpenHelper(context, dbName, null, DB_VERSION);
                }
            }
        }
        return mDbHelper;
    }

    // =================Public method=====================//
    // 都是synchronous方法，每个调用里面包含：获取db、使用db、关闭db 的组合操作，该组合操作是原子操作

    /**
     * 在数据库中插入UserAccountInfo
     * 
     * @param context
     * @param UserAccountInfo
     * @return 设置是否成功
     */
    public static synchronized long insertUserAccountInfo(Context context, UserAccountInfo useraccountinfo) {
        synchronized (myLock) {
            SQLiteDatabase db = getDb(context);
            if (db == null) {
                return -1;
            }
            ContentValues values = new ContentValues();
            values.put(UserAccountInfoEnum.UserAccountName.name(), useraccountinfo.getMobilePhoneNumber());
            values.put(UserAccountInfoEnum.UserAccountInfoPassWord.name(), useraccountinfo.getUserPassWord());
            values.put(UserAccountInfoEnum.UserAccountTimestamp.name(), useraccountinfo.mUserTimestamp);
            values.put(UserAccountInfoEnum.UserAccountPosition.name(), useraccountinfo.getUserPosition());
            
            long ret = -1;
            Cursor cs = null;
            try {
                ret = db.insert(UserAccountInfoEnum.TABLE_NAME, null, values);
                SLog.e(TAG, "UserAccountInfoEnum:  insert into database");
            } catch (Exception e) {
                SLog.e(TAG, e);
            } finally {
                if (null != cs && !cs.isClosed()) {
                    cs.close();
                }
                if (db != null) {
                    db.close();
                }
            }
            return ret;
        }
    }

    /**
     * 从数据库中查询用户名密码是否正确
     * 
     * @param context
     * @return PushInfoEnumClass
     */
    public static synchronized boolean checkUserAccountAndPassword(Context context, 
            String useraccountname, String userpassword) {
        synchronized (myLock) {
            SQLiteDatabase db = getDb(context);
            if (db == null) {
                return false;
            }
            
            Cursor cursor = null;
            try {
                cursor = db.query(UserAccountInfoEnum.TABLE_NAME, null, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    String  username = cursor.getString(cursor.getColumnIndex(UserAccountInfoEnum.UserAccountName.name()));
                    if (username.equals(useraccountname)) {
                        String password = cursor.getString(cursor.getColumnIndex(UserAccountInfoEnum.UserAccountInfoPassWord.name()));
                        if (password.equals(userpassword)) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                SLog.e(TAG, e);
            } finally {
                if (null != null && !cursor.isClosed()) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }
            }
        }
        return false;
    }
  
    
    /**
     * 从数据库中查询用户名是否存在
     * 
     * @param context
     * @return PushInfoEnumClass
     */
    public static synchronized boolean checkUserAccount(Context context, 
            String useraccountname) {
        synchronized (myLock) {
            SQLiteDatabase db = getDb(context);
            if (db == null) {
                return false;
            }
            
            Cursor cursor = null;
            try {
                cursor = db.query(UserAccountInfoEnum.TABLE_NAME, null, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    String  username = cursor.getString(cursor.getColumnIndex(UserAccountInfoEnum.UserAccountName.name()));
                    if (username.equals(useraccountname)) {
                        String password = cursor.getString(cursor.getColumnIndex(UserAccountInfoEnum.UserAccountInfoPassWord.name()));
                        if (!TextUtils.isEmpty(password)) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                SLog.e(TAG, e);
            } finally {
                if (null != null && !cursor.isClosed()) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }
            }
        }
        return false;
    }
    
    /**
     * 自定义数据库异常处理内部类 有的手机默认处理数据库异常时将导致错误 形成系统内部递归调用导致栈溢出如下：<br>
     * 数据文件异常-> ->SQLiteDatabaseCorruptException->onCorruption<br>
     * ->isDatabaseIntegrityOk->onCorruption<br>
     * ->isDatabaseIntegrityOk->onCorruption...<br>
     * 详情见push-sdk-714 StackOverflowError崩溃问题<br>
     * 
     * 处理方式为系统默认处理方式 即删除数据文件 将处理方式提取出来统一处理能够规避 上述错误
     * 
     * @author lihongbin02
     * 
     */
    private static class DbErrorHandler implements DatabaseErrorHandler {
        @Override
        public void onCorruption(SQLiteDatabase dbObj) {
            SLog.e(TAG, "Corruption reported by sqlite on database: " + dbObj.getPath());

            // is the corruption detected even before database could be
            // 'opened'?
            if (!dbObj.isOpen()) {
                // database files are not even openable. delete this database
                // file.
                // NOTE if the database has attached databases, then any of them
                // could be corrupt.
                // and not deleting all of them could cause corrupted database
                // file to remain and
                // make the application crash on database open operation. To
                // avoid this problem,
                // the application should provide its own {@link
                // DatabaseErrorHandler} impl class
                // to delete ALL files of the database (including the attached
                // databases).
                deleteDatabaseFile(dbObj.getPath());
                return;
            }

            List<Pair<String, String>> attachedDbs = null;
            try {
                // Close the database, which will cause subsequent operations to
                // fail.
                // before that, get the attached database list first.
                try {
                    attachedDbs = dbObj.getAttachedDbs();
                } catch (SQLiteException e) {
                    /* ignore */
                }
                try {
                    dbObj.close();
                } catch (SQLiteException e) {
                    /* ignore */
                }
            } finally {
                // Delete all files of this corrupt database and/or attached
                // databases
                if (attachedDbs != null) {
                    for (Pair<String, String> p : attachedDbs) {
                        deleteDatabaseFile(p.second);
                    }
                } else {
                    // attachedDbs = null is possible when the database is so
                    // corrupt that even
                    // "PRAGMA database_list;" also fails. delete the main
                    // database file
                    deleteDatabaseFile(dbObj.getPath());
                }
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        private void deleteDatabaseFile(String fileName) {
            if (fileName.equalsIgnoreCase(":memory:") || fileName.trim().length() == 0) {
                return;
            }
            SLog.e(TAG, "deleting the database file: " + fileName);
            try {
                // 虽然该api在16的时候就引入了
                // 但是通过阅读源码发现直到19才使用该逻辑处理
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    SQLiteDatabase.deleteDatabase(new File(fileName));
                } else {
                    new File(fileName).delete();
                }
            } catch (Exception e) {
                /* print warning and ignore exception */
                SLog.w(TAG, "delete failed: " + e.getMessage());
            }
        }
    }
    
    
    // ==================内部类===============
    private static class InfoDbOpenHelper extends SQLiteOpenHelper {

        public InfoDbOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        
        public InfoDbOpenHelper(Context context, String name, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, null, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            
            try {
                // 用户账号登录表
                db.execSQL("CREATE TABLE " + UserAccountInfoEnum.TABLE_NAME + " (" + UserAccountInfoEnum.UserAccountInfoId.name()
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, " + UserAccountInfoEnum.UserAccountName.name()
                        + " TEXT, " + UserAccountInfoEnum.UserAccountInfoPassWord.name()
                        + " TEXT, " + UserAccountInfoEnum.UserAccountTimestamp.name() 
                        + " LONG  NOT NULL DEFAULT ((0)), " + UserAccountInfoEnum.UserAccountPosition 
                        + " TEXT "
                        + ");");
                
                SLog.e(TAG,
                        "CREATE TABLE " + UserAccountInfoEnum.TABLE_NAME + " (" + UserAccountInfoEnum.UserAccountInfoId.name()
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, " + UserAccountInfoEnum.UserAccountName.name()
                        + " TEXT, " + UserAccountInfoEnum.UserAccountInfoPassWord.name()
                        + " TEXT, " + UserAccountInfoEnum.UserAccountTimestamp.name() 
                        + " LONG  NOT NULL DEFAULT ((0)), " + UserAccountInfoEnum.UserAccountPosition 
                        + " TEXT "
                        + ");");
              
            } catch (Exception e) {
                SLog.e(TAG, e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            dropTables(db);
            onCreate(db);
        }

        private void dropTables(SQLiteDatabase db) {
            try {
                db.execSQL("DROP TABLE IF EXISTS " + UserAccountInfoEnum.TABLE_NAME);
            } catch (Exception e) {
                SLog.e(TAG, e);
            }
        }
    }

    enum UserAccountInfoEnum {
        UserAccountInfoId, UserAccountName, UserAccountInfoPassWord, UserAccountTimestamp, UserAccountPosition;
        static final String TABLE_NAME = "UserAccountInfo";
    }
}
