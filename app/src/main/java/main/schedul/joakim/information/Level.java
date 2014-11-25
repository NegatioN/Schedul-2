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
    //database-constructor
    public Level(int levelXp, int level){
        this.level = level;
        this.levelXp = levelXp;
    }


    public void passInExperience(int experience){
        //add experience
        levelXp+=experience;
        if(isLevel())
            //TODO some sort of animation for level-up
        return;

    }

    public void removeExperience(int experience){
        levelXp -= experience;
        if(isLevelDown())
            //DO SOMETHING
        return;
    }

    //what experience is required to surpass the user's current level?
    public int requiredXp(){
       return (int) (level*COEFFICIENT*BASE_EXPERIENCE);
    }

    //has the user leveled up?
    private boolean isLevel(){
        boolean isLevel = false;
        while(levelXp >= requiredXp()) {
            levelUp();
            isLevel = true;
        }
        return isLevel;
    }
    //has the user leveled down because of resetting a chain-xp for the day?
    private boolean isLevelDown(){

        boolean isLevelDown = false;
        while(levelXp < 0){
            levelDown();
            isLevelDown = true;
        }
        return isLevelDown;
    }

    //levels up the users, and transfers remaining xp to the next level.
    private void levelUp(){
        levelXp -= requiredXp();
        level++;
    }
    //levels the user down if reset xp
    private void levelDown(){
        level--;
        levelXp += requiredXp();

    }

    public int getLevel() {
        return level;
    }

    public int getLevelXp() {
        return levelXp;
    }
}
