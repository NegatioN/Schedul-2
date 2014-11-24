package main.schedul.joakim.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import main.schedul.joakim.information.Achievement;
import main.schedul.joakim.information.Chain;
import main.schedul.joakim.information.ComboAchievement;
import main.schedul.joakim.information.ExperienceAchievement;
import main.schedul.joakim.information.Level;
import main.schedul.joakim.information.TimeAchievement;
import main.schedul.joakim.information.User;

/**
 * Created by NegatioN on 03.11.2014.
 * A class containing the definition of our entire database
 * The three tables are for Users, Chains and Achievements
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String NAME = "SchedulDB";
    private final static String TABLE_USERS = "Users";
    private final static String TABLE_CHAINS = "Chains";
    private final static String TABLE_ACHIEVEMENTS = "Achievements";
    private final static int DB_VERSION = 2;

    //Keys for user-table
    private final static String KEY_USERID = "_USERID", KEY_USERNAME = "_NAME", KEY_LEVEL = "_LEVEL", KEY_LEVELXP = "_LEVELXP";
    //Keys for Chain-table
    private final static String KEY_CHAINID = "_ID", KEY_CHAINNAME = "_CHAIN", KEY_CHAINDESC = "_DESCRIPTION", KEY_CHAINMINUTES = "_MINUTES",
            KEY_CHAINCOMBO = "_COMBO", KEY_MUSTCHAINSETTING = "_MUSTCHAIN", KEY_LASTUPDATE = "_LASTUPDATE", KEY_MINUTESTODAY = "_MINSTODAY", KEY_CHAINPRIORITY = "_PRIORITY", KEY_CHAINXP = "_XP";
    //Keys for achievement-table
    private final static String KEY_ACH_ID = "_ID", KEY_ACH_TYPE = "_TYPE", KEY_ACH_NAME = "_NAME", KEY_ACH_ACHIEVED = "_ACHIEVED", KEY_ACH_SIMPLE_OR_TOTAL = "_SIMPLEORTOTAL",
    KEY_ACH_GOAL = "_GOAL";
    //Achievement-types
    private static final int TYPE_COMBO = 1, TYPE_EXPERIENCE = 2, TYPE_TIME = 3;
    private static final int TYPE_SIMPLE = 4, TYPE_TOTAL = 5;


   // private final static String DATEFORMAT = "yyyy-MM-dd-HH-mm-ss";
    private final static String DATEFORMAT = "%Y.%m.%d.%H.%M.%S";


    public DBHelper(Context context) {
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
                KEY_USERID + " INTEGER, " +
                KEY_CHAINID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                KEY_CHAINNAME + " TEXT, " +
                KEY_CHAINDESC + " TEXT, " +
                KEY_CHAINMINUTES + " INTEGER, " +
                KEY_CHAINCOMBO + " INTEGER, " +
                KEY_MUSTCHAINSETTING + " INTEGER, " +
                KEY_MINUTESTODAY + " INTEGER, " +
                KEY_CHAINPRIORITY + " INTEGER, " +
                KEY_CHAINXP + " INTEGER, " +
                KEY_LASTUPDATE + " TEXT)";

        sqLiteDatabase.execSQL(createTable);

        createTable = "CREATE TABLE " + TABLE_ACHIEVEMENTS + "(" +
                KEY_USERID + " INTEGER, " +
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAINS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);
        onCreate(sqLiteDatabase);
    }

    //METODS FOR INTERACTING WITH USER-DATABASE

    public int addUser(User user){

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getName());
        //TODO create method for adding a list of Achievements in a different table, with userId as FK
        values.put(KEY_LEVELXP, user.getLevel().getLevelXp());
        values.put(KEY_LEVEL, user.getLevel().getLevel());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_USERS, null, values);
        db.close();

        int id = getLastInsertedUserId();
        //Add all chains to the user.
        addChainList(id ,user.getUserChains());
        return id;
    }

    //get a list of all users (for selecting user-profile etc)
    public List<User> getUsers(){
        List<User> users = new ArrayList<User>();
        String query = "SELECT * FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                int userid = cursor.getInt(0);
                String name = cursor.getString(1);
                int levelxp = cursor.getInt(2);
                int lvl = cursor.getInt(3);
                Level level = new Level(levelxp, lvl);

                users.add(new User(userid,name,level));
            }while(cursor.moveToNext());
        }
        return users;
    }

    //gets us a complete useable user-object with chains and achievements
    public User getEntireUser(int userId){
        Log.d("database.getUser", "UserId: " + userId);
        User user = getBaseUser(userId);
        user.setUserAchievements(getAchievements(userId));
        user.setUserChains(getChains(userId));

        return user;
    }

    public User getBaseUser(int userid){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USERID, KEY_USERNAME, KEY_LEVELXP, KEY_LEVEL}, KEY_USERID + "=?",
                new String[]{String.valueOf(userid)}, null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            String name = cursor.getString(1);
            int levelxp = cursor.getInt(2);
            int lvl = cursor.getInt(3);
            Level level = new Level(levelxp, lvl);

            return new User(userid,name,level);
        }

        db.close();
        return null;
    }

    //updates a user
    public int updateUser(User user){
        SQLiteDatabase db = getWritableDatabase();

        return db.update(TABLE_USERS, createUserValues(user), KEY_USERID + "=?", new String[]{String.valueOf(user.getId())});
    }

    public void deleteUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_USERS, KEY_USERID + " =?", new String[]{String.valueOf(user.getId())});
        db.close();
    }

    //returns the content values of a user
    private ContentValues createUserValues(User user){
        ContentValues values = new ContentValues();
        values.put(KEY_USERID, user.getId());
        values.put(KEY_USERNAME, user.getName());
        values.put(KEY_LEVELXP, user.getLevel().getLevelXp());
        values.put(KEY_LEVEL, user.getLevel().getLevel());
        return values;
    }



    //METHODS FOR INTEREACTING WITH CHAIN-DATABASE

    //TODO effectivize by making method with only one db-access?
    public void addChainList(int userid, List<Chain> chains){
        for(Chain chain : chains)
            addChain(userid,chain);
    }

    public void addChain(int userId, Chain chain){
        SQLiteDatabase db = this.getWritableDatabase();

        Log.d("addChain", "AddChain called");
        long position = db.insert(TABLE_CHAINS, null, createChainValues(chain, userId));
        Log.d("addChain", "Position?: " + position);
        db.close();

    }

    //gets all chains stored for a given user
    public List<Chain> getChains(int userId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHAINS, new String[]{KEY_CHAINID, KEY_CHAINNAME, KEY_CHAINDESC, KEY_CHAINMINUTES, KEY_CHAINCOMBO, KEY_MUSTCHAINSETTING,
                        KEY_MINUTESTODAY, KEY_CHAINPRIORITY, KEY_CHAINXP, KEY_LASTUPDATE}, KEY_USERID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null, null);

        List<Chain> chains = new ArrayList<Chain>();
        Log.d("getChains.Start", "Chain-getting started");

        if(cursor.moveToFirst()) {
            do {
                //add to chains
                chains.add(createChainFromCursor(cursor));
            } while (cursor.moveToNext());
        }else{
            Log.d("getChains.Error", "Chain length == 0?");
        }
        return chains;
    }

    //update a single chain
    public int updateChain(Chain chain){
        SQLiteDatabase db = getWritableDatabase();

        return db.update(TABLE_CHAINS, createChainValues(chain), KEY_CHAINID + "=?", new String[]{String.valueOf(chain.getId())});
    }

    public void deleteChain(Chain chain){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_CHAINS, KEY_CHAINID + " =?", new String[]{String.valueOf(chain.getId())});
        db.close();
    }

    //takes in cursor and outputs a chain
    private Chain createChainFromCursor(Cursor cursor){
        //get all parameters from cursor
        int chainId = cursor.getInt(0);
        String name = cursor.getString(1);
        String desc = cursor.getString(2);
        int chainmins = cursor.getInt(3);
        int combo = cursor.getInt(4);
        int mustchaindays = cursor.getInt(5);
        int minstoday = cursor.getInt(6);
        int priority = cursor.getInt(7);
        int experience = cursor.getInt(8);
        Time time = makeStringToTime(cursor.getString(9));
        time.normalize(false);

       return  new Chain(chainId,name, desc, priority, mustchaindays, chainmins, combo, minstoday, time, experience);
    }

    //overload for linking a chain with user on create
    private ContentValues createChainValues(Chain chain, int userId){
        ContentValues values = createChainValues(chain);
        values.put(KEY_USERID, userId);
        return values;
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
        values.put(KEY_CHAINXP, chain.getCurrentExperience());
        return values;
    }

    //METHODS TO INTERACT WITH ACHIEVEMENT-DB

    public void addAchievement(int userId, Achievement achievement){
        SQLiteDatabase db = getWritableDatabase();

        db.insert(TABLE_ACHIEVEMENTS, null, createAchievementValues(userId, achievement));
        db.close();

    }

    public void addAchievementList(int userId, List<Achievement> achievements){
        for(Achievement achievement : achievements)
            addAchievement(userId, achievement);
    }

    public int updateAchievement(int userId, Achievement achievement){
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_ACHIEVEMENTS, createAchievementValues(userId, achievement), KEY_ACH_ID + "=?", new String[]{String.valueOf(achievement.getId())});
    }

    //returns all achievements for a given user
    public List<Achievement> getAchievements(int userId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACHIEVEMENTS, new String[]{KEY_ACH_ID, KEY_ACH_ACHIEVED, KEY_ACH_GOAL, KEY_ACH_NAME, KEY_ACH_TYPE, KEY_ACH_SIMPLE_OR_TOTAL}, KEY_USERID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null, null);

        List<Achievement> achievements = new ArrayList<Achievement>();
        if(cursor.getCount() != 0){
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


    public void deleteAchievement(Achievement achievement){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ACHIEVEMENTS, KEY_ACH_ID + " =?", new String[]{String.valueOf(achievement.getId())});
        db.close();
    }


    //helper method for creating content-values for the achievement
    private ContentValues createAchievementValues(int userId, Achievement achievement){

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, userId);
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
       // SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT);
        Log.d("TimeBefore", time.toString());
        Log.d("makeTimeToString", time.format(DATEFORMAT));
        return time.format(DATEFORMAT);
        //return df.format(time);
    }

    private Time makeStringToTime(String dateFromDB){
        Time t = new Time();
        //this will only work if dateformat = "yyyy-MM-dd-HH-mm-ss";
        int year = Integer.parseInt(dateFromDB.substring(0,4));
        int month = Integer.parseInt(dateFromDB.substring(5,7));
        int day = Integer.parseInt(dateFromDB.substring(8,10));
        int hour = Integer.parseInt(dateFromDB.substring(11,13));
        int minutes = Integer.parseInt(dateFromDB.substring(14,16));
        int seconds = Integer.parseInt(dateFromDB.substring(17,19));
        Log.d("MakeStringToTime", "Year: " + year + " Month: " + month + " Day: " + day + "\nHour: " + hour + " Min: " + minutes + " Sec: " + seconds);

        t.set(seconds, minutes,hour,day,month,year);
        return t;
    }

    //Helper method for getting the last user inserted into db.
    //or for setting the correct userId in a newly created object.
    public int getLastInsertedUserId(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USERID},null, null, null, null, null);

        if(cursor != null) {
            cursor.moveToLast();
            int userid = cursor.getInt(0);
            Log.d("db.lastUser", "Last user added: " + userid);
            return userid;
        }

        return -1;
    }


}
