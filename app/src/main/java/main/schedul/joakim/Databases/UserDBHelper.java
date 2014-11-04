package main.schedul.joakim.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import main.schedul.joakim.information.Chain;
import main.schedul.joakim.information.User;

/**
 * Created by NegatioN on 03.11.2014.
 */
public class UserDBHelper extends SQLiteOpenHelper {
    private static final String NAME = "SchedulDB";
    private final static String TABLE_USERS = "Users";
    private final static String TABLE_CHAINS = "Chains";
    private final static int DB_VERSION = 2;

    private final static String KEY_USERID = "_ID", KEY_USERNAME = "_NAME", KEY_LEVEL = "_LEVEL", KEY_LEVELXP = "_LEVELXP";
    private final static String KEY_CHAINID = "_ID", KEY_CHAINNAME = "_CHAIN", KEY_CHAINDESC = "_DESCRIPTION", KEY_CHAINMINUTES = "_MINUTES",
            KEY_CHAINCOMBO = "_COMBO", KEY_MUSTCHAINSETTING = "_MUSTCHAIN", KEY_LASTUPDATE = "_LASTUPDATE", KEY_MINUTESTODAY = "_MINSTODAY", KEY_CHAINPRIORITY = "_PRIORITY";


    private final static String DATEFORMAT = "yyyy-MM-dd-HH-mm-ss";


    public UserDBHelper(Context context) {
        super(context, NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_USERS + "(" +
                KEY_USERID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                KEY_USERNAME + " TEXT, " +
                KEY_LEVELXP + " INTEGER, " +
                KEY_LEVEL + " INTEGER)";
        sqLiteDatabase.execSQL(createTable);

        createTable = "CREATE TABLE " + TABLE_CHAINS + "(" +
                KEY_USERID + " INTEGER FOREIGN KEY, " +
                KEY_CHAINID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                KEY_CHAINNAME + " TEXT, " +
                KEY_CHAINDESC + " TEXT, " +
                KEY_CHAINMINUTES + " INTEGER, " +
                KEY_CHAINCOMBO + " INTEGER, " +
                KEY_MUSTCHAINSETTING + " INTEGER, " +
                KEY_MINUTESTODAY + " INTEGER, " +
                KEY_CHAINPRIORITY + " INTEGER, " +
                KEY_LASTUPDATE + " TEXT)";

        sqLiteDatabase.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(sqLiteDatabase);
    }

    //TODO create methods for CRUD user, chain, achievements
    //METODS FOR INTERACTING WITH USER-DATABASE

    public void addUser(User user){

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getName());
        //TODO create method for adding a list of chains in a different table, with userId as FK
        //TODO create method for adding a list of Achievements in a different table, with userId as FK
        values.put(KEY_LEVELXP, user.getLevel().getLevelXp());
        values.put(KEY_LEVEL, user.getLevel().getLevel());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_USERS, null, values);
        db.close();
    }



    //METHODS FOR INTEREACTING WITH CHAIN-DATABASE

    public void addChain(int userId, Chain chain){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, userId);
        values.put(KEY_CHAINNAME, chain.getName());
        values.put(KEY_CHAINDESC, chain.getDescription());
        values.put(KEY_CHAINMINUTES, chain.getTotalMins());
        values.put(KEY_CHAINCOMBO, chain.getCurrentChain());
        values.put(KEY_MINUTESTODAY, chain.getMinutesSpentToday());
        values.put(KEY_LASTUPDATE, makeTimeToString(chain.getLastUpdated()));
        values.put(KEY_CHAINPRIORITY, chain.getPriority());

        db.insert(TABLE_CHAINS, null, values);
        db.close();

    }

    //gets all chains stored for a given user
    public List<Chain> getChains(int userId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHAINS, new String[]{KEY_CHAINID, KEY_CHAINNAME, KEY_CHAINDESC, KEY_CHAINMINUTES, KEY_CHAINCOMBO, KEY_MUSTCHAINSETTING,
                        KEY_MINUTESTODAY, KEY_CHAINPRIORITY, KEY_LASTUPDATE}, KEY_USERID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null, null);

        List<Chain> chains = new ArrayList<Chain>();

        if(cursor != null) {
            cursor.moveToFirst();
            do {
                //get all parameters from cursor
                int chainId = cursor.getInt(0);
                String name = cursor.getString(1);
                String desc = cursor.getString(2);
                int chainmins = cursor.getInt(3);
                int combo = cursor.getInt(4);
                int mustchaindays = cursor.getInt(5);
                int minstoday = cursor.getInt(6);
                int priority = cursor.getInt(7);
                Time time = makeStringToTime(cursor.getString(8));

                //add to chains
                chains.add(new Chain(chainId,name, desc, priority, mustchaindays, chainmins, combo, minstoday, time));
            } while (cursor.moveToNext());
        }
        return chains;
    }

    //DATE HELPER METHODS

    private String makeTimeToString(Time time){
        SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT);
        return df.format(time);
    }

    private Time makeStringToTime(String dateFromDB){
        Time t = new Time();
        //this will only work if dateformat = "yyyy-MM-dd-HH-mm-ss";
        int year = Integer.parseInt(dateFromDB.substring(0,3));
        int month = Integer.parseInt(dateFromDB.substring(5,6));
        int day = Integer.parseInt(dateFromDB.substring(8,9));
        int hour = Integer.parseInt(dateFromDB.substring(11,12));
        int minutes = Integer.parseInt(dateFromDB.substring(14,15));
        int seconds = Integer.parseInt(dateFromDB.substring(17,18));


        t.set(seconds, minutes,hour,day,month,year);
        return t;
    }


}
