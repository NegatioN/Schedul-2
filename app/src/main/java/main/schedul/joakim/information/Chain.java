package main.schedul.joakim.information;

import android.text.format.Time;

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


    public Chain(String name, int priority, String description){
        this.name = name;
        this.priority = priority;
        totalMins = 0;
        xp = new Experience();
        this.description = description;
        currentChain = 0;

        Time fillerTime = new Time();
        fillerTime.set(0);
        lastUpdated = fillerTime;
    }

    /*
    when useer adds a task to current chain we calculate all stats
     */
    //TODO add oppdatering av view i list når xp går opp... add user stats i bar.
    public void doTask(int minutes, User user){
        if(isChained())
            currentChain++;
        else
            currentChain = 0;
        int taskXp = xp.calculateExperience(minutes,currentChain);
        totalMins+=minutes;
        user.updateLevel(taskXp);

        //set lastupdated to now
        Time t = new Time();
        t.setToNow();
        lastUpdated = t;
    }


    //TODO implement ability to user-define chainnable time. chain can be done every 1,2,3 day etc
    //is the task chained to it's last execution?
    private boolean isChained(){
        Time t = new Time();
        t.setToNow();
        //if more than 24 hours has passed since a user last chained a task, no bonus
        if((t.toMillis(false) - lastUpdated.toMillis(false) > Time.HOUR * 24))
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

    public void setTotalMins(long totalMins) {
        this.totalMins = totalMins;
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

    public String getTotalHours(){
        DecimalFormat df = new DecimalFormat("#.00");
        double hours = getTotalMins() / 60;

        return df.format(hours);

    }

    public void addChainToUser(User user){
        user.getUserChains().add(this);
    }



}
