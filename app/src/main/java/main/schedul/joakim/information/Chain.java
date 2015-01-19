package main.schedul.joakim.information;

import android.content.Context;
import android.graphics.Color;
import android.text.format.Time;
import android.util.Log;

import java.text.DecimalFormat;

import main.schedul.joakim.schedul2.Schedul;

/**
 * Created by NegatioN on 21.09.2014.
 * The class represents a single user-defined chain that can generate experience based on doing the defined task.
 * These are the main component of the application,
 */
public class Chain {

    private String name, description;
    private long totalMins;
    private Experience xp;
    private int priority;


    private int chainid;


    private int currentChain;
    private Time lastUpdated;
    private int mustChainDays;
    private double minutesSpentToday;

    private static final long DAYMILLIS = 1000 * 60 * 60  * 24;
    private static final int HSV_TO_COLOR_HUE_INDEX = 0;
    private static final int HSV_TO_COLOR_SATURATION_INDEX = 1;
    private static final int HSV_TO_COLOR_VALUE_INDEX = 2;

    private static final int COLOR_FLOAT_TO_INT_FACTOR = 255;




    /**
     * @param name              The name of the Chain, ex "programming", "Soccer", "Japanese-studies"
     * @param priority          What the priority of the chain is. Will slightly affect experience-gain
     * @param description       A short description of the chain
     * @param mustChainDays     What the number of days we can "chain" this task within. ex. input 1 for every day. or 2 for every other day
     */

    //default constructor
    public Chain(String name, int priority, String description, int mustChainDays){
        this.name = name;
        this.priority = priority;
        totalMins = 0;
        xp = new Experience();
        this.description = description;
        currentChain = 0;

        this.mustChainDays = mustChainDays;

        Time fillerTime = new Time();
        fillerTime.set(0);
        lastUpdated = fillerTime;
    }

    /**
     * Database-constructor
     * @param chainid the id of the chain in the database
     * @param name name of the chain
     * @param description description of the chain
     * @param priority priority of the chain (currently not implemented)
     * @param mustChainDays What the number of days we can "chain" this task within. ex. input 1 for every day. or 2 for every other day
     * @param chainminutes How much time has the user logged in this chain? ex. chain contains 1 day, 3 hours of logged time. output in minutes.
     * @param chaincombo how many days (depending on mustchaindays-setting) has the user kept this chain going without breaking the combo?
     * @param minstoday Tells us how much time has been logged today by the user.
     * @param lastUpdated Tells us when the chain was last updated, in the form of a Time-object
     * @param experience Tells us how much experience has been logged into this chain.
     * @see android.text.format.Time
     */
    public Chain(int chainid, String name,  String description, int priority, int mustChainDays, int chainminutes, int chaincombo, int minstoday, Time lastUpdated, int experience){
        this.chainid = chainid;
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.mustChainDays = mustChainDays;
        this.minutesSpentToday =  minstoday;
        this.currentChain = chaincombo;
        this.totalMins = chainminutes;
        this.lastUpdated = lastUpdated;
        this.xp = new Experience(experience);
    }


    /**
     * when user adds a task to current chain we calculate all stats
     * @param minutes How many minutes the task took
     * @param user the user who did the task
     * @param context context of the application
     * @see android.content.Context
     */
    public void doTask(int minutes, User user, Context context){
        if(isChained()) {
            Time t = new Time();
            t.setToNow();

            //if user has already chained today, don't give additional chains to bonus the xp-gain.
            if(minutesSpentToday == 0){
                currentChain++;
            }

        }
        else
            currentChain = 1;

        Log.d("doTask", currentChain + " combo");
        int taskXp = xp.calculateExperience(minutes,currentChain);
        Log.d("doTask.XP", "XP: " + taskXp);
        totalMins+=minutes;
        minutesSpentToday += minutes;
        user.updateLevel(taskXp, context);

        //set lastupdated to now
        Time t = new Time();
        t.setToNow();
        lastUpdated = t;
    }

    /**
     * When calling this method, is the combo chained to the previous executed task?
     * Depends on the users settings of "mustChainDays"
     * @return Returs true if the task is comboed, false if not.
     */
    private boolean isChained(){
        Time t = new Time();
        t.setToNow();

        long chainInMillis = DAYMILLIS * mustChainDays;

        //if more than userdefined hours has passed since a user last chained a task, no bonus
        if((t.toMillis(false) - lastUpdated.toMillis(false) > chainInMillis))
            return false;
        return true;
    }

    /**
     *   resets the info that has been inputted today for this chain.
     */
    public void resetToday(){
        if(currentChain != 0)
            currentChain--;
        else
            return;
        //reset experience.
        int removedXp = xp.applyResetExperience((int)minutesSpentToday, currentChain);
        //remove xp from user.
        Schedul.CURRENTUSER.getLevel().removeExperience(removedXp);

        //sets mins to 0 and detracts from total
        totalMins -= minutesSpentToday;
        minutesSpentToday = 0;

        lastUpdated.set(0,0,0,lastUpdated.monthDay-(mustChainDays-1),lastUpdated.month, lastUpdated.year);
        lastUpdated.normalize(false);

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTotalMins() {
        return totalMins;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getCurrentExperience(){
        return xp.getTotalXp();
    }

    public Experience getExperience(){
        return xp;
    }

    public double getMinutesSpentToday() {
        return minutesSpentToday;
    }
    public int getCurrentChain() {
        return currentChain;
    }

    public Time getLastUpdated() {
        return lastUpdated;
    }

    public void setMinutesSpentToday(double minutesSpentToday) {
        this.minutesSpentToday = minutesSpentToday;
    }
    public int getMustChainDays() {
        return mustChainDays;
    }

    public void setMustChainDays(int mustChainDays) {
        this.mustChainDays = mustChainDays;
    }

    public int getId() {
        return chainid;
    }

    /**
     * Formats our total hours in double-form to a string
     * @return formatted text-string of minutes with double digit prescision
     */
    public String getTotalHours(){
        DecimalFormat df = new DecimalFormat("0.00");
        double hours = Double.parseDouble(getTotalMins()+"") / 60;

        return df.format(hours);

    }

    /**
     * adds this chain to a user
     * @param user user to add this chain to.
     */
    public void addChainToUser(User user){
        user.getUserChains().add(this);
    }

    /**
     * If this chain has already been updated today will return true
     * @return returns true if updated today.
     */
    public boolean isUpdatedToday(){
        Time today = new Time();
        today.setToNow();
       if(lastUpdated.yearDay == today.yearDay && lastUpdated.year == today.year)
           return true;
        else
           return false;
    }

    /**
     * returns the progress untill the chains times out and resets zero combo-counter
     * @return 0% for newly updated, 100% for timed out
     */
    private int getPercentageTimeout(){
        Time t = new Time();
        t.setToNow();

        long nowMinusLastUpdateMillis = t.toMillis(false) - lastUpdated.normalize(false);
        long mustMillis = mustChainDays * DAYMILLIS;
        int percentage = (int) ((nowMinusLastUpdateMillis*100) / mustMillis);
        Log.d("Chain Percentage", "Percentage: " + percentage + "\nMillis diff: " + nowMinusLastUpdateMillis);
        Log.d("Chain Percentage", "Now: " + t.toString() + "\n" + "Last: " + lastUpdated.toString());
        if(percentage > 100)
            return 100;
        return percentage;
    }

    /**
     * for setting colorfilter of widgets or listitems depending on how far the chain is from timing out.
     * gets a color from green to red depending on the state of the chain.
     * @return Color int in HSV-format.
     */
    public int getDisplayColor(){
        int percentage = 100 - getPercentageTimeout();
        //if at 0%, return a grey color to overlay.
        if(percentage == 0)
            return HSVToColor(100, 0, 0.50f);
        float hue = (float)Math.floor(percentage * 120 / 100);

        return HSVToColor(hue, 0.80f, 0.65f);
    }

    /**
     * method for easy input of hue, saturation and value
     * helper-method to getDisplayColor
     * @param pHue  Defines level of hue. 0-360 but uses float value of 1.0f as max
     * @param pSaturation defines level of saturation. 0-100 uses float value of 1.0f as max
     * @param pValue defines value of brightness. 0-100 uses float value of 1.0f as max
     * @return
     */
    private  static int HSVToColor(final float pHue, final float pSaturation, final float pValue) {
        float[] HSV_TO_COLOR = new float[3];
        HSV_TO_COLOR[HSV_TO_COLOR_HUE_INDEX] = pHue;
        HSV_TO_COLOR[HSV_TO_COLOR_SATURATION_INDEX] = pSaturation;
        HSV_TO_COLOR[HSV_TO_COLOR_VALUE_INDEX] = pValue;
        return Color.HSVToColor(HSV_TO_COLOR);
    }



}
