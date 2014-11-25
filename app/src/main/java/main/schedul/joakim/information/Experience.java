package main.schedul.joakim.information;

import android.util.Log;

import java.util.List;

/**
 * Created by NegatioN on 21.09.2014.
 * The Experience object takes care of logging total experience and logic for experience-increase
 */
public class Experience {
    private long totalXp;

    public Experience(){
        totalXp = 0;
    }

    //database constructor
    public Experience(int experience){
        totalXp = experience;
    }

    //takes in the user-defined minutes that the current task lasted.
    //gets the current chain and calculates + adds experience to total
    //returns: current task experience
    public int calculateExperience(int minutes, int combo){
        double multiplier;
        if(combo == 0)
            multiplier = 1.2;
        else{
            multiplier = (1 + (combo * 0.1));
        }
        int taskXp = (int) multiplier*minutes;
        updateTotalXp(taskXp);
        return taskXp;
    }

    public int applyResetExperience(int minutesRemoved, int combo){
        double multiplier;
        if(combo == 0)
            multiplier = 1.2;
        else{
            multiplier = (1 + (combo * 0.1));
        }
        int dayXp =(int) multiplier*minutesRemoved;
        setTotalXp(totalXp-dayXp);
        return dayXp;
    }

    //has user already input 24 hours today?
    public static boolean daySpent(List<Chain> allChains, int inputMinutes){
        double collectiveTimeSpent = inputMinutes;

        for(Chain chain : allChains){
            collectiveTimeSpent += chain.getMinutesSpentToday();
        }

        Log.d("Experience.daySpent", "Minutes spent:" + collectiveTimeSpent);
        if(collectiveTimeSpent > 24*60 )
            return true;
        return false;
    }


    public long getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(long totalXp) {
        this.totalXp = totalXp;
    }

    //simply adds xp to the total count
    public void updateTotalXp(double taskXp) {
        this.totalXp += (long) taskXp;
    }
}
