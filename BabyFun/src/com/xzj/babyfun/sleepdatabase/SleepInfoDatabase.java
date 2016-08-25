package com.xzj.babyfun.sleepdatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
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
import android.util.Pair;

import com.xzj.babyfun.logging.SLog;

public class SleepInfoDatabase {
    private static InfoDbOpenHelper mDbHelper = null;
    private static DbErrorHandler mDbErrorHandler = null;
    private static Object myLock = new Object();
    private static final int DB_VERSION = 1;
    private static final String DB_DIR = "/database";
    private static final String DATA_DIR = "/data";
    private static final String TAG = "SleepInfoDatabase";
    private static final String DB_NAME = "sleepinfo.db";

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
     * 初始化数据库
     * 
     * @param context
     * @return
     */
    public static synchronized void initSleepInfoDataBase(Context context) {
        synchronized (myLock) {
            try {
                SleepInfo sleepinfo = new SleepInfo();
                sleepinfo.mSleepTimestamp = System.currentTimeMillis();
                sleepinfo.mSleepYear = 0;
                sleepinfo.mSleepMonth = 0;
                sleepinfo.mSleepDay = 0;
                sleepinfo.mSleepMinute = 0;
                sleepinfo.mSleepValue = 3;
      
                insertSleepInfo(context, sleepinfo);

                SLog.d(TAG, "initPushInfoDataBase with initValue");
            } catch (Exception e) {
                // TODO: handle exception
                SLog.e(TAG, e);
            }
        }
    }

    /**
     * 在数据库中插入SleepInfo
     * 
     * @param context
     * @param sleepinfo
     * @return 设置是否成功
     */
    public static synchronized long insertSleepInfo(Context context, SleepInfo sleepinfo) {
        synchronized (myLock) {
            SQLiteDatabase db = getDb(context);
            if (db == null) {
                return -1;
            }
            ContentValues values = new ContentValues();
            values.put(SleepInfoEnum.SleepTimestamp.name(), sleepinfo.mSleepTimestamp);
            values.put(SleepInfoEnum.SleepYear.name(), sleepinfo.mSleepYear);
            values.put(SleepInfoEnum.SleepMonth.name(), sleepinfo.mSleepMonth);
            values.put(SleepInfoEnum.SleepDay.name(), sleepinfo.mSleepDay);
            values.put(SleepInfoEnum.SleepMinute.name(), sleepinfo.mSleepMinute);
            values.put(SleepInfoEnum.SleepValue.name(), sleepinfo.mSleepValue);
            
            long ret = -1;
            Cursor cs = null;
            try {
              
                ret = db.insert(SleepInfoEnum.TABLE_NAME, null, values);
                SLog.d(TAG, "SleepInfoEnum:  insert into database");
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
     * 在数据库中插入BreathInfo
     * 
     * @param context
     * @param sleepinfo
     * @return 设置是否成功
     */
    public static synchronized long insertBreathInfo(Context context, BreathStopInfo breathinfo) {
        synchronized (myLock) {
            SQLiteDatabase db = getDb(context);
            if (db == null) {
                return -1;
            }
            ContentValues values = new ContentValues();
            values.put(BreathInfoEnum.BreathTimestamp.name(), breathinfo.mBreathTimestamp);
            values.put(BreathInfoEnum.BreathIsAlarm.name(), breathinfo.mBreathIsAlarm);
            values.put(BreathInfoEnum.BreathDuration.name(), breathinfo.mBreathDuration);
            

            long ret = -1;
            Cursor cs = null;
            try {
              
                ret = db.insert(BreathInfoEnum.TABLE_NAME, null, values);
                SLog.d(TAG, "BreathInfoEnum:  insert into database");
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
     * 在数据库中插入TemperatureInfo
     * 
     * @param context
     * @param sleepinfo
     * @return 设置是否成功
     */
    public static synchronized long insertTemperatureInfo(Context context, TemperatureInfo temperatureinfo) {
        synchronized (myLock) {
            SQLiteDatabase db = getDb(context);
            if (db == null) {
                return -1;
            }
            ContentValues values = new ContentValues();
            values.put(TemperatureInfoEnum.TemperatureTimestamp.name(), temperatureinfo.mTemperatureTimestamp);
            values.put(TemperatureInfoEnum.TemperatureValue.name(), temperatureinfo.mTemperatureValue);
            
            long ret = -1;
            Cursor cs = null;
            try {
                ret = db.insert(TemperatureInfoEnum.TABLE_NAME, null, values);
                SLog.d(TAG, "pushadvertiseinfo:  insert into database");
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
     * 从数据库中取出SleepInfoEnumClass
     * 
     * @param context
     * @param currentTime
     *            当前时间
     * @param lastSendTime
     *            上次发送时间
     * @return SleepInfoEnumClass list
     */
    public static List<SleepInfoEnumClass> getSleepInfoEnumClassList(Context context, long currentTime,
            long lastSendTime, int offset, int count) {
        synchronized (myLock) {
            SQLiteDatabase db = getDb(context);
            if (db == null) {
                return null;
            }
            List<SleepInfoEnumClass> values = new ArrayList<SleepInfoEnumClass>();

            String selection = "SELECT * FROM " + SleepInfoEnum.TABLE_NAME
                    + " WHERE " + SleepInfoEnum.SleepTimestamp.name()
                    + " < " + currentTime
                    + " AND " + SleepInfoEnum.SleepTimestamp.name()
                    + " >= " + lastSendTime
                    + " LIMIT " + count
                    + " OFFSET " + offset + ";";

            Cursor cursor = null;
            try {
                cursor = db.rawQuery(selection, null);

                while (cursor.moveToNext()) {
                    SleepInfoEnumClass sleepvalues = new SleepInfoEnumClass();
                    sleepvalues.setSleepTimestamp(cursor.getLong(cursor.getColumnIndex(SleepInfoEnum.SleepTimestamp.name())));
                    sleepvalues.setSleepYear(cursor.getInt(cursor.getColumnIndex(SleepInfoEnum.SleepYear.name())));
                    sleepvalues.setSleepMonth(cursor.getInt(cursor.getColumnIndex(SleepInfoEnum.SleepMonth.name())));
                    sleepvalues.setSleepDay(cursor.getInt(cursor.getColumnIndex(SleepInfoEnum.SleepDay.name())));
                    sleepvalues.setSleepMinute(cursor.getInt(cursor.getColumnIndex(SleepInfoEnum.SleepMinute.name())));
                    sleepvalues.setSleepValue(cursor.getInt(cursor.getColumnIndex(SleepInfoEnum.SleepValue.name())));
                    
                    values.add(sleepvalues);
                }
            } catch (Exception e) {
                SLog.d(TAG, "e getADBehaviorEnumClassList " + e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }
            }

            return values;
        }
    }

    /**
     * 从数据库中取出某一天的睡眠数据
     * 
     * @param context
     * @param year 年
     * @param month 月
     * @param day 日
     *           
     * @return SleepInfoEnumClass list
     */
    public static List<SleepInfoEnumClass> getSleepInfoEnumList(Context context, int year,
            int month, int day) {
        synchronized (myLock) {
            SQLiteDatabase db = getDb(context);
            if (db == null) {
                return null;
            }
            List<SleepInfoEnumClass> values = new ArrayList<SleepInfoEnumClass>();

            String selection = "SELECT * FROM " + SleepInfoEnum.TABLE_NAME
                    + " WHERE " + SleepInfoEnum.SleepYear.name()
                    + " = " + year
                    + " AND " + SleepInfoEnum.SleepMonth.name()
                    + " = " + month
                    + " AND " + SleepInfoEnum.SleepDay.name()
                    + " = " + day
                    + ";";

            Cursor cursor = null;
            try {
                cursor = db.rawQuery(selection, null);

                while (cursor.moveToNext()) {
                    SleepInfoEnumClass sleepvalues = new SleepInfoEnumClass();
                    sleepvalues.setSleepTimestamp(cursor.getLong(cursor.getColumnIndex(SleepInfoEnum.SleepTimestamp.name())));
                    sleepvalues.setSleepYear(cursor.getInt(cursor.getColumnIndex(SleepInfoEnum.SleepYear.name())));
                    sleepvalues.setSleepMonth(cursor.getInt(cursor.getColumnIndex(SleepInfoEnum.SleepMonth.name())));
                    sleepvalues.setSleepDay(cursor.getInt(cursor.getColumnIndex(SleepInfoEnum.SleepDay.name())));
                    sleepvalues.setSleepMinute(cursor.getInt(cursor.getColumnIndex(SleepInfoEnum.SleepMinute.name())));
                    sleepvalues.setSleepValue(cursor.getInt(cursor.getColumnIndex(SleepInfoEnum.SleepValue.name())));
                    
                    values.add(sleepvalues);
                }
            } catch (Exception e) {
                SLog.d(TAG, "e getADBehaviorEnumClassList " + e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }
            }

            return values;
        }
    }

    
    /**
     * 从数据库中取出BreathInfoEnumClass
     * 
     * @param context
     * @param currentTime
     *            当前时间
     * @param lastSendTime
     *            上次发送时间
     * @return BreathInfoEnumClass list
     */
    public static List<BreathInfoEnumClass> getBreathInfoEnumClassList(Context context, long currentTime,
            long lastSendTime, int offset, int count) {
        synchronized (myLock) {
            SQLiteDatabase db = getDb(context);
            if (db == null) {
                return null;
            }
            List<BreathInfoEnumClass> values = new ArrayList<BreathInfoEnumClass>();

            String selection = "SELECT * FROM " + BreathInfoEnum.TABLE_NAME
                    + " WHERE " + BreathInfoEnum.BreathTimestamp.name()
                    + " < " + currentTime
                    + " AND " + BreathInfoEnum.BreathTimestamp.name()
                    + " >= " + lastSendTime
                    + " LIMIT " + count
                    + " OFFSET " + offset + ";";

            Cursor cursor = null;
            try {
                cursor = db.rawQuery(selection, null);

                while (cursor.moveToNext()) {
                    BreathInfoEnumClass breathvalues = new BreathInfoEnumClass();
                    breathvalues.setBreathTimestamp(cursor.getLong(cursor.getColumnIndex(BreathInfoEnum.BreathTimestamp.name())));
                    breathvalues.setBreathIsAlarm(cursor.getInt(cursor.getColumnIndex(BreathInfoEnum.BreathIsAlarm.name())));
                    breathvalues.setBreathDuration(cursor.getInt(cursor.getColumnIndex(BreathInfoEnum.BreathDuration.name())));
                    
                    values.add(breathvalues);

                }
            } catch (Exception e) {
                SLog.d(TAG, "e getADBehaviorEnumClassList " + e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }
            }

            return values;
        }
    }
    
    
    /**
     * 从数据库中取出TemperatureInfoEnumClass
     * 
     * @param context
     * @param currentTime
     *            当前时间
     * @param lastSendTime
     *            上次发送时间
     * @return BreathInfoEnumClass list
     */
    public static List<TemperatureInfoEnumClass> getTemperatureInfoEnumClassList(Context context, long currentTime,
            long lastSendTime, int offset, int count) {
        synchronized (myLock) {
            SQLiteDatabase db = getDb(context);
            if (db == null) {
                return null;
            }
            List<TemperatureInfoEnumClass> values = new ArrayList<TemperatureInfoEnumClass>();

            String selection = "SELECT * FROM " + TemperatureInfoEnum.TABLE_NAME
                    + " WHERE " + TemperatureInfoEnum.TemperatureTimestamp.name()
                    + " < " + currentTime
                    + " AND " + TemperatureInfoEnum.TemperatureTimestamp.name()
                    + " >= " + lastSendTime
                    + " LIMIT " + count
                    + " OFFSET " + offset + ";";

            Cursor cursor = null;
            try {
                cursor = db.rawQuery(selection, null);

                while (cursor.moveToNext()) {
                    TemperatureInfoEnumClass temperaturevalues = new TemperatureInfoEnumClass();
                    temperaturevalues.setTemperatureTimestamp(cursor.getLong(cursor.getColumnIndex(TemperatureInfoEnum.TemperatureTimestamp.name())));
                    temperaturevalues.setTemperatureValue(cursor.getInt(cursor.getColumnIndex(TemperatureInfoEnum.TemperatureValue.name())));
                    
                    values.add(temperaturevalues);

                }
            } catch (Exception e) {
                SLog.d(TAG, "e getADBehaviorEnumClassList " + e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }
            }

            return values;
        }
    }
    
    /**
     * 从数据库中取出PushInfoEnumClass
     * 
     * @param context
     * @return PushInfoEnumClass
     */
    public static synchronized SleepInfoEnumClass getSleepInfoEnumClass(Context context) {
        synchronized (myLock) {
            SQLiteDatabase db = getDb(context);
            if (db == null) {
                return null;
            }
            SleepInfoEnumClass values = new SleepInfoEnumClass();
            Cursor cursor = null;
            try {
                cursor = db.query(SleepInfoEnum.TABLE_NAME, null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    values.setSleepTimestamp(cursor
                            .getLong(cursor.getColumnIndex(SleepInfoEnum.SleepTimestamp.name())));
                    values.setSleepYear(cursor
                            .getInt(cursor.getColumnIndex(SleepInfoEnum.SleepYear.name())));
                    values.setSleepMonth(cursor
                            .getInt(cursor.getColumnIndex(SleepInfoEnum.SleepMonth.name())));
                    values.setSleepDay(cursor
                            .getInt(cursor.getColumnIndex(SleepInfoEnum.SleepDay.name())));
                    values.setSleepMinute(cursor
                            .getInt(cursor.getColumnIndex(SleepInfoEnum.SleepMinute.name())));
                    values.setSleepValue(cursor
                            .getInt(cursor.getColumnIndex(SleepInfoEnum.SleepValue.name())));
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
            return values;
        }
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
                // 睡眠数据表
                db.execSQL("CREATE TABLE " + SleepInfoEnum.TABLE_NAME + " (" 
                        + SleepInfoEnum.SleepInfoId.name() + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
                        + SleepInfoEnum.SleepTimestamp.name() + " LONG  NOT NULL DEFAULT ((0)), " 
                        + SleepInfoEnum.SleepYear.name() + " INTEGER DEFAULT ((0)), " 
                        + SleepInfoEnum.SleepMonth.name() + " INTEGER DEFAULT ((0)), "
                        + SleepInfoEnum.SleepDay.name() + " INTEGER DEFAULT ((0)), "
                        + SleepInfoEnum.SleepMinute.name() + " INTEGER DEFAULT ((0)), "
                        + SleepInfoEnum.SleepValue.name() + " INTEGER DEFAULT ((0)) " + ");");
                
                SLog.e(TAG,
                        "CREATE TABLE " + SleepInfoEnum.TABLE_NAME + " (" 
                                + SleepInfoEnum.SleepInfoId.name() + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
                                + SleepInfoEnum.SleepTimestamp.name() + " LONG  NOT NULL DEFAULT ((0)), " 
                                + SleepInfoEnum.SleepYear.name() + " INTEGER DEFAULT ((0)), " 
                                + SleepInfoEnum.SleepMonth.name() + " INTEGER DEFAULT ((0)), "
                                + SleepInfoEnum.SleepDay.name() + " INTEGER DEFAULT ((0)), "
                                + SleepInfoEnum.SleepMinute.name() + " INTEGER DEFAULT ((0)), "
                                + SleepInfoEnum.SleepValue.name() + " INTEGER DEFAULT ((0)) " + ");");
                
                // 呼吸数据表
                db.execSQL("CREATE TABLE " + BreathInfoEnum.TABLE_NAME + " (" + BreathInfoEnum.BreathInfoId.name()
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, " + BreathInfoEnum.BreathTimestamp.name()
                        + " LONG  NOT NULL DEFAULT ((0)), " + BreathInfoEnum.BreathIsAlarm.name()
                        + " INTEGER DEFAULT ((0)), " + BreathInfoEnum.BreathDuration.name() + " INTEGER DEFAULT ((0)) " + ");");
                
                SLog.e(TAG,
                        "CREATE TABLE " + BreathInfoEnum.TABLE_NAME + " (" + BreathInfoEnum.BreathInfoId.name()
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, " + BreathInfoEnum.BreathTimestamp.name()
                        + " LONG  NOT NULL DEFAULT ((0)), " + BreathInfoEnum.BreathIsAlarm.name()
                        + " INTEGER DEFAULT ((0)), " + BreathInfoEnum.BreathDuration.name() + " INTEGER DEFAULT ((0)) " + ");");
                
                // 温度数据表
                db.execSQL("CREATE TABLE " + TemperatureInfoEnum.TABLE_NAME + " (" + TemperatureInfoEnum.TemperatureInfoId.name()
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TemperatureInfoEnum.TemperatureTimestamp.name()
                        + " LONG  NOT NULL DEFAULT ((0)), " + TemperatureInfoEnum.TemperatureValue.name()
                        + " INTEGER DEFAULT ((0)) " + ");");
                
                SLog.e(TAG,
                        "CREATE TABLE " + TemperatureInfoEnum.TABLE_NAME + " (" + TemperatureInfoEnum.TemperatureInfoId.name()
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TemperatureInfoEnum.TemperatureTimestamp.name()
                        + " LONG  NOT NULL DEFAULT ((0)), " + TemperatureInfoEnum.TemperatureValue.name()
                        + " INTEGER DEFAULT ((0)) " + ");");
            } catch (Exception e) {
                // TODO: handle exception
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
                db.execSQL("DROP TABLE IF EXISTS " + SleepInfoEnum.TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + BreathInfoEnum.TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + TemperatureInfoEnum.TABLE_NAME);
            } catch (Exception e) {
                SLog.d(TAG, "dropTables Exception: " + e);
            }
        }
    }

    enum SleepInfoEnum {
        SleepInfoId, SleepTimestamp, SleepYear, SleepMonth, SleepDay, SleepMinute, SleepValue;
        static final String TABLE_NAME = "SleepInfo";
    }
    
    enum BreathInfoEnum {
        BreathInfoId, BreathTimestamp, BreathIsAlarm, BreathDuration;
        static final String TABLE_NAME = "BreathInfo";
    }
    
    enum TemperatureInfoEnum {
        TemperatureInfoId, TemperatureTimestamp, TemperatureValue;
        static final String TABLE_NAME = "TemperatureInfo";
    }
}
