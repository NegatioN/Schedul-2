package main.schedul.joakim.information;

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
    when useer adds a task to current chain we calculate all stats
     */
    //TODO  add user stats i bar.
    public void doTask(int minutes, User user){
        if(isChained()) {
            currentChain++;
        }
        else
            currentChain = 1;

        Log.d("doTask", currentChain + " combo");
        int taskXp = xp.calculateExperience(minutes,currentChain);
        totalMins+=minutes;
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

        long chainInMillis = 1000 * 60 * 60  * 24 * mustChainDays;
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


    //TODO make this display in proper double-form. Not only as whole integers with .00 behind
    public String getTotalHours(){
        DecimalFormat df = new DecimalFormat("#.00");
        double hours = getTotalMins() / 60;

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



}
