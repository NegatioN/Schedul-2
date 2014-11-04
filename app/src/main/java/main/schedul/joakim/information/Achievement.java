package main.schedul.joakim.information;

import android.media.Image;

/**
 * Created by NegatioN on 31.10.2014.
 * A class used to define a single achievement, and the conditions a user needs to meet.
 */

    //TODO make "badge-page" ala fitocracy for achievements with icons
    //TODO popup on add time-dialog finished with list of badges achieved
public abstract class Achievement {

    private boolean achieved;
    private String name;
    private Image badgeIcon;
    private int id;


    public Achievement(String name){
        this.name = name;
        achieved = false;
    }

    //database-constructor
    public Achievement(String name, boolean isAcheved, int id){
        this.name = name;
        setAchieved(isAcheved);
        this.id = id;
    }

    //if not achieved: calculate if achievedNow() on xp-add.
    public boolean isAchieved(){
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }

    public abstract int getGoalNumber();

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
