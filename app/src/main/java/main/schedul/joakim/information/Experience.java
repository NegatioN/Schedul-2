package main.schedul.joakim.information;

/**
 * Created by NegatioN on 21.09.2014.
 * The Experience object takes care of logging total experience and logic for experience-increase
 */
public class Experience {
    private long totalXp;

    public Experience(){
        totalXp = 0;
    }


    //TODO make it impossible to add more than 24hours of tasks in a single day.
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


    public long getTotalXp() {
        return totalXp;
    }

    //simply adds xp to the total count
    public void updateTotalXp(double taskXp) {
        this.totalXp += (long) taskXp;
    }
}
