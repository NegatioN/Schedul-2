package main.schedul.joakim.information;

/**
 * Created by NegatioN on 21.09.2014.
 */
public class Experience {
    private long totalXp;
    private User user;

    public Experience(User user){
        this.user = user;
        totalXp = 0;
    }

    //takes in the user-defined minutes that the current task lasted.
    //gets the current chain and calulates extra xp based on this too.
    public void calculateExperience(int minutes, int chain){
        double multiplier;
        if(chain == 0)
            multiplier = 1.2;
        else{
            multiplier = (1 + (chain * 0.1));
        }

        updateTotalXp(multiplier*minutes);
    }


    public long getTotalXp() {
        return totalXp;
    }

    //simply adds xp to the total count
    public void updateTotalXp(double taskXp) {
        user.setTotalExperience(user.getTotalExperience() + (long) taskXp);
        this.totalXp += (long) taskXp;
    }
}
