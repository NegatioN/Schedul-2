package main.schedul.joakim.information;

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


    //TODO make it impossible to add more than 24hours of tasks in a single day. Method added, just needs to be used.
    //takes in the user-defined minutes that the current task lasted.
    //gets the current chain and calculates + adds experience to total
    //returns: current task experience
    public int calculateExperience(int minutes, int chain){
        double multiplier;
        if(chain == 0)
            multiplier = 1.2;
        else{
            multiplier = (1 + (chain * 0.1));
        }
        int taskXp = (int) multiplier*minutes;
        updateTotalXp(taskXp);
        return taskXp;
    }

    //has user already input 24 hours today?
    public static boolean daySpent(List<Chain> allChains, int inputMinutes){
        double collectiveTimeSpent = inputMinutes;

        for(Chain chain : allChains){
            collectiveTimeSpent += chain.getMinutesSpentToday();
        }

        if(collectiveTimeSpent > 24*60 )
            return true;
        return false;
    }


    public long getTotalXp() {
        return totalXp;
    }

    //simply adds xp to the total count
    public void updateTotalXp(double taskXp) {
        this.totalXp += (long) taskXp;
    }
}
