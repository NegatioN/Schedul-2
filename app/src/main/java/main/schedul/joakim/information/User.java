package main.schedul.joakim.information;

import java.util.ArrayList;

/**
 * Created by NegatioN on 22.09.2014.
 */
public class User {

    //TODO skeleton

    private String name;
    private Level lvl;
    private ArrayList<Chain> userChains;

    public ArrayList<Chain> getUserChains() {
        return userChains;
    }

    public User(String name){
        userChains = new ArrayList<Chain>();
        this.name = name;
        lvl = new Level();
    }

    //gets all user chains and computes total of each
    public long getTotalExperience() {
        long totalExperience = 0;
        for(Chain chain : userChains){
            totalExperience += chain.getCurrentExperience();
        }

        return totalExperience;
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
}
