package main.schedul.joakim.information;

import java.util.ArrayList;

/**
 * Created by NegatioN on 22.09.2014.
 */
public class User {

    private String name;
    private Level lvl;
    private ArrayList<Chain> userChains;
    private ArrayList<Achievement> userAchievements;

    public User(String name){
        userChains = new ArrayList<Chain>();
        this.name = name;
        lvl = new Level();

        defineAchievements(userAchievements);

    }

    //gets all user chains and computes total of each
    public long getTotalExperience() {
        long totalExperience = 0;
        if(userChains != null || userChains.size() != 0) {
            for (Chain chain : userChains) {
                totalExperience += chain.getCurrentExperience();
            }
        }

        return totalExperience;
    }

    public long getTotalTime(){
        long totalMinutes = 0;
        for(Chain chain : userChains){
            totalMinutes += chain.getTotalMins();
        }
        return totalMinutes;
    }

    //TODO create encouragement-message that does "math.random()" in addition to the title-message that's standard?
    //define all the achievements for a user on creation.
    private void defineAchievements(ArrayList<Achievement> achievements){
        //Experience Achievements
        achievements.add(new ExperienceAchievement("OVER NINE THOUSAND!!!", 9000, 0));
        achievements.add(new ExperienceAchievement("Leet", 1337, 0));
        achievements.add(new ExperienceAchievement("A god among men.", 100000, 0));
        achievements.add(new ExperienceAchievement("Keep it going!!", 0, 1000));

        //Combo Achievements
        achievements.add(new ComboAchievement("Ten in a row!", 10));
        achievements.add(new ComboAchievement("Twenty-five in a row!", 25));
        achievements.add(new ComboAchievement("FIFTY IN A ROW!!!!", 50));
        achievements.add(new ComboAchievement("ONE HUNDRED IN A ROW!!!??", 100));

        //Time Achievements
        achievements.add(new TimeAchievement("The first hour.", 1, 0));
        achievements.add(new TimeAchievement("Our finest hour.", 1000, 0));
        achievements.add(new TimeAchievement("A well-spent hour.", 0, 1));
        achievements.add(new TimeAchievement("Putting in the work.", 0, 10));
        achievements.add(new TimeAchievement("#Beastly #dedication!", 0, 1000));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLvl() {
        return lvl.getLevel();
    }

    public void updateLevel(int experience){
        lvl.passInExperience(experience);
    }

    public ArrayList<Chain> getUserChains() {
        return userChains;
    }
}
