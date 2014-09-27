package main.schedul.joakim.information;

/**
 * Created by NegatioN on 22.09.2014.
 */
public class Level {
    private int level;
    private int levelXp;


    //final static variables for computing the rule-set of experience
    private static final double COEFFICIENT = 1.5d;
    private static final int BASE_EXPERIENCE = 100;


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
       return (int) (level*COEFFICIENT*BASE_EXPERIENCE);
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

    //levels up the users, and transfers remaining xp to the next level.
    private void levelUp(){
        level++;
        levelXp = levelXp - requiredXp();
    }

    public int getLevel() {
        return level;
    }

    public int getLevelXp() {
        return levelXp;
    }
}
