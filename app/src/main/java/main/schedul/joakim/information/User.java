package main.schedul.joakim.information;

import java.util.logging.Level;

/**
 * Created by NegatioN on 22.09.2014.
 */
public class User {

    //TODO skeleton

    private String name;
    private long totalExperience;
    private Level lvl;

    public User(String name){
        this.name = name;
        totalExperience = 0;
    }

    public long getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(long totalExperience) {
        this.totalExperience = totalExperience;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Level getLvl() {
        return lvl;
    }

    public void setLvl(Level lvl) {
        this.lvl = lvl;
    }
}
