package main.schedul.joakim.information;

/**
 * Created by NegatioN on 22.09.2014.
 */
public class Level {
    //TODO skeleton, implement rules for leveling up lvl*qofficient = reqXP
    private int level;
    private int levelXp;


    //final static variables for computing the rule-set of experience
    private static final double COEFFICIENT = 1.5d;
    private static final int BASEEXPERIENCE = 100;


    public Level(){
        this.level = 0;
        this.levelXp = 0;

    }


    public void passInExperience(int experience){
        //add experience
        levelXp+=experience;
        if(isLevel())
            //TODO some sort of animation for level-up
        return;

    }

    //what experience is required to surpass the user's current level?
    public int requiredXp(){
       return (int) (level*COEFFICIENT*BASEEXPERIENCE);
    }

    //has the user leveled up?
    private boolean isLevel(){
        if(levelXp >= requiredXp()) {
            levelUp();
            return true;
        }
        else
            return false;
    }

    private void levelUp(){
        level++;
        levelXp = levelXp - requiredXp();
    }

}
