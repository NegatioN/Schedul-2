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

import main.schedul.joakim.information.Achievement;
import main.schedul.joakim.information.Chain;
import main.schedul.joakim.information.ComboAchievement;
import main.schedul.joakim.information.ExperienceAchievement;
import main.schedul.joakim.information.TimeAchievement;
import main.schedul.joakim.information.User;

/**
 * Created by NegatioN on 03.11.2014.
 */
public class UserDBHelper extends SQLiteOpenHelper {
    private static final String NAME = "SchedulDB";
    private final static String TABLE_USERS = "Users";
    private final static String TABLE_CHAINS = "Chains";
    private final static String TABLE_ACHIEVEMENTS = "Achievements";
    private final static int DB_VERSION = 2;

    //Keys for user-table
    private final static String KEY_USERID = "_USERID", KEY_USERNAME = "_NAME", KEY_LEVEL = "_LEVEL", KEY_LEVELXP = "_LEVELXP";
    //Keys for Chain-table
    private final static String KEY_CHAINID = "_ID", KEY_CHAINNAME = "_CHAIN", KEY_CHAINDESC = "_DESCRIPTION", KEY_CHAINMINUTES = "_MINUTES",
            KEY_CHAINCOMBO = "_COMBO", KEY_MUSTCHAINSETTING = "_MUSTCHAIN", KEY_LASTUPDATE = "_LASTUPDATE", KEY_MINUTESTODAY = "_MINSTODAY", KEY_CHAINPRIORITY = "_PRIORITY";
    //Keys for achievement-table
    private final static String KEY_ACH_ID = "_ID", KEY_ACH_TYPE = "_TYPE", KEY_ACH_NAME = "_NAME", KEY_ACH_ACHIEVED = "_ACHIEVED", KEY_ACH_SIMPLE_OR_TOTAL = "_SIMPLEORTOTAL",
    KEY_ACH_GOAL = "_GOAL";
    //Achievement-types
    private static final int TYPE_COMBO = 1, TYPE_EXPERIENCE = 2, TYPE_TIME = 3;
    private static final int TYPE_SIMPLE = 4, TYPE_TOTAL = 5;


    private final static String DATEFORMAT = "yyyy-MM-dd-HH-mm-ss";


    public UserDBHelper(Context context) {
        super(context, NAME, null, DB_VERSION);
    }


    //create our three main tables if they are not already present
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

        createTable = "CREATE TABLE " + TABLE_ACHIEVEMENTS + "(" +
                KEY_USERID + " INTEGER FOREIGN KEY, " +
                KEY_ACH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                KEY_ACH_TYPE + " INTEGER NOT NULL, " +
                KEY_ACH_NAME + " TEXT, " +
                KEY_ACH_ACHIEVED + " INTEGER NOT NULL, " +
                KEY_ACH_SIMPLE_OR_TOTAL + " INTEGER, " +
                KEY_ACH_GOAL + " INTEGER)";

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
        //TODO create method for adding a list of Achievements in a different table, with userId as FK
        values.put(KEY_LEVELXP, user.getLevel().getLevelXp());
        values.put(KEY_LEVEL, user.getLevel().getLevel());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_USERS, null, values);
        db.close();

        //Add all chains to the user.
        addChainList(getLastInsertedUserId() ,user.getUserChains());
    }



    //METHODS FOR INTEREACTING WITH CHAIN-DATABASE

    //TODO effectivize by making method with only one db-access?
    public void addChainList(int userid, List<Chain> chains){
        for(Chain chain : chains)
            addChain(userid,chain);
    }

    public void addChain(int userId, Chain chain){
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_CHAINS, null, createChainValues(chain));
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

    //update a single chain
    public int updateChain(Chain chain){
        SQLiteDatabase db = getWritableDatabase();

        return db.update(TABLE_CHAINS, createChainValues(chain), KEY_CHAINID + "=?", new String[]{String.valueOf(chain.getChainid())});
    }

    //help method for creating chain content values.
    private ContentValues createChainValues(Chain chain){
        ContentValues values = new ContentValues();
        values.put(KEY_CHAINNAME, chain.getName());
        values.put(KEY_CHAINDESC, chain.getDescription());
        values.put(KEY_CHAINMINUTES, chain.getTotalMins());
        values.put(KEY_CHAINCOMBO, chain.getCurrentChain());
        values.put(KEY_MINUTESTODAY, chain.getMinutesSpentToday());
        values.put(KEY_LASTUPDATE, makeTimeToString(chain.getLastUpdated()));
        values.put(KEY_CHAINPRIORITY, chain.getPriority());
        values.put(KEY_MUSTCHAINSETTING, chain.getMustChainDays());
        return values;
    }

    //METHODS TO INTERACT WITH ACHIEVEMENT-DB

    public void addAchievement(Achievement achievement){
        SQLiteDatabase db = getWritableDatabase();

        db.insert(TABLE_ACHIEVEMENTS, null, createAchievementValues(achievement));
        db.close();

    }

    public int updateAchievement(Achievement achievement){
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_ACHIEVEMENTS, createAchievementValues(achievement), KEY_ACH_ID + "=?", new String[]{String.valueOf(achievement.getId())});
    }

    //returns all achievements for a given user
    public List<Achievement> getAchievements(int userId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACHIEVEMENTS, new String[]{KEY_ACH_ID, KEY_ACH_ACHIEVED, KEY_ACH_GOAL, KEY_ACH_NAME, KEY_ACH_TYPE, KEY_ACH_SIMPLE_OR_TOTAL}, KEY_USERID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null, null);

        List<Achievement> achievements = new ArrayList<Achievement>();
        if(cursor != null){
            cursor.moveToFirst();
            do{
                int id = cursor.getInt(0);
                boolean achieved = cursor.getInt(1)==1?true:false; //return true if value is 1, else false
                int goalNumber = cursor.getInt(2);
                String name = cursor.getString(3);
                int type = cursor.getInt(4);
                int simpleOrTotal = cursor.getInt(5);

                switch (type){
                    case TYPE_COMBO:
                        achievements.add(new ComboAchievement(id, name, goalNumber, achieved));
                        break;
                    case TYPE_EXPERIENCE:
                        achievements.add(new ExperienceAchievement(id, name, goalNumber, simpleOrTotal==TYPE_TOTAL ? true: false, achieved));
                        break;
                    default:
                        achievements.add(new TimeAchievement(id, name, goalNumber, simpleOrTotal==TYPE_TOTAL ? true : false, achieved));
                }

            }while(cursor.moveToNext());
        }

        db.close();
        return achievements;
    }


    //helper method for creating content-values for the achievement
    private ContentValues createAchievementValues(Achievement achievement){

        ContentValues values = new ContentValues();
        values.put(KEY_ACH_ACHIEVED, achievement.isAchieved()?1:0); //use 1 or 0 as boolean values
        values.put(KEY_ACH_GOAL, achievement.getGoalNumber());
        values.put(KEY_ACH_NAME, achievement.getName());


        //sets the achievement-type
        if(achievement instanceof ComboAchievement) {
            values.put(KEY_ACH_TYPE, TYPE_COMBO);
        }
        else if(achievement instanceof ExperienceAchievement) {
            values.put(KEY_ACH_TYPE, TYPE_EXPERIENCE);
            boolean totalGoal = ((ExperienceAchievement) achievement).isTotalGoal();
            values.put(KEY_ACH_SIMPLE_OR_TOTAL, totalGoal ? TYPE_TOTAL : TYPE_SIMPLE);

        }
        else if(achievement instanceof TimeAchievement) {
            boolean totalGoal = ((TimeAchievement) achievement).isTotalGoal();
            values.put(KEY_ACH_SIMPLE_OR_TOTAL, totalGoal ? TYPE_TOTAL : TYPE_SIMPLE);
            values.put(KEY_ACH_TYPE, TYPE_TIME);
        }
        return values;
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

    //Helper method for getting the last user inserted into db.
    private int getLastInsertedUserId(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USERID},null, null, null, null, null);

        if(cursor != null) {
            cursor.moveToLast();
            return cursor.getInt(0);
        }

        return -1;
    }


}
