package main.schedul.joakim.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by NegatioN on 03.11.2014.
 */
public class UserDBHelper extends SQLiteOpenHelper {
    private static final String NAME = "USER_DB";
    private final static String TABLE_USERS = "Users";
    private final static int DB_VERSION = 2;

    private final static String KEY_ID = "_ID", KEY_NAME = "_NAME", KEY_


    public UserDBHelper(Context context) {
        super(context, NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_USERS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_NAME + " TEXT, " +
                KEY_PHONE + " INTEGER, " +
                KEY_BIRTHDAY + " TEXT, " +
                KEY_MESSAGE + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
