package main.schedul.joakim.information;

import android.graphics.Color;
import android.text.format.Time;
import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by NegatioN on 21.09.2014.
 * The class represents a single user-defined chain that can generate experience based on doing the defined task.
 */
public class Chain {

    private String name, description;
    private long totalMins;
    private Experience xp;
    private int priority, currentChain;
    private Time lastUpdated;
    private int mustChainDays;
    private double minutesSpentToday;

    private static final long DAYMILLIS = 1000 * 60 * 60  * 24;
    private static final int HSV_TO_COLOR_HUE_INDEX = 0;
    private static final int HSV_TO_COLOR_SATURATION_INDEX = 1;
    private static final int HSV_TO_COLOR_VALUE_INDEX = 2;

    private static final int COLOR_FLOAT_TO_INT_FACTOR = 255;



    public int getMustChainDays() {
        return mustChainDays;
    }

    /**
     * @param name              The name of the Chain, ex "programming", "Soccer", "Japanese-studies"
     * @param priority          What the priority of the chain is. Will slightly affect experience-gain
     * @param description       A short description of the chain
     * @param mustChainDays     What the number of days we can "chain" this task within. ex. input 1 for every day. or 2 for every other day
     */

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


    /*
        when user adds a task to current chain we calculate all stats
         */
    //TODO  add user stats i bar.
    //TODO create percentage until chain reverted to 0, for displaying colors correctly.
    public void doTask(int minutes, User user){
        if(isChained()) {
            Time t = new Time();
            t.setToNow();

            //if user has already chained today, don't give additional chains to bonus the xp-gain.
            if((t.toMillis(false) - lastUpdated.toMillis(false)) > DAYMILLIS){
                currentChain++;
            }
            //it's a new day, minutesSpentToday set to 0
            if(lastUpdated.yearDay < t.yearDay || t.yearDay == 0)
                minutesSpentToday = 0;

        }
        else
            currentChain = 1;

        Log.d("doTask", currentChain + " combo");
        int taskXp = xp.calculateExperience(minutes,currentChain);
        totalMins+=minutes;
        minutesSpentToday += minutes;
        user.updateLevel(taskXp);

        //set lastupdated to now
        Time t = new Time();
        t.setToNow();
        lastUpdated = t;
    }


    //is the task chained to it's last execution?
    private boolean isChained(){
        Time t = new Time();
        t.setToNow();

        long chainInMillis = DAYMILLIS * mustChainDays;

        //if more than userdefined hours has passed since a user last chained a task, no bonus
        if((t.toMillis(false) - lastUpdated.toMillis(false) > chainInMillis))
            return false;
        return true;
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

    public double getMinutesSpentToday() {
        return minutesSpentToday;
    }

    public String getTotalHours(){
        DecimalFormat df = new DecimalFormat("0.00");
        double hours = Double.parseDouble(getTotalMins()+"") / 60;

        return df.format(hours);

    }

    public void addChainToUser(User user){
        user.getUserChains().add(this);
    }

    //has our chain already been
    public boolean isUpdatedToday(){
        Time today = new Time();
        today.setToNow();
       if(lastUpdated.yearDay == today.yearDay && lastUpdated.year == today.year)
           return true;
        else
           return false;
    }

    //returns the progress untill the chains times out and resets to 0 chain-combo
    //0% for newly updated, 100% for "done"
    private int getPercentageTimeout(){
        Time t = new Time();
        t.setToNow();

        int percentage = (int) ((t.toMillis(false) - lastUpdated.toMillis(false)) / (mustChainDays * DAYMILLIS)) * 100;
        Log.d("Chain Percentage", "Percentage: " + percentage);
        if(percentage > 100)
            return 100;
        return percentage;
    }

    //for setting background-color of widgets or listitems depending on how far the chain is from timing out.
    //gets a color from green to red depending on the state of the chain.
    public int getDisplayColor(){
        float hue = (float)Math.floor((100 - getPercentageTimeout()) * 120 / 100);

        return HSVToColor(hue, 70, 90);
    }

    //method for easy input of hue, saturation and value
    private  static int HSVToColor(final float pHue, final float pSaturation, final float pValue) {
        float[] HSV_TO_COLOR = new float[3];
        HSV_TO_COLOR[HSV_TO_COLOR_HUE_INDEX] = pHue;
        HSV_TO_COLOR[HSV_TO_COLOR_SATURATION_INDEX] = pSaturation;
        HSV_TO_COLOR[HSV_TO_COLOR_VALUE_INDEX] = pValue;
        return Color.HSVToColor(HSV_TO_COLOR);
    }



}
