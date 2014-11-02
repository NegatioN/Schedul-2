package main.schedul.joakim.information;

import android.media.Image;

/**
 * Created by NegatioN on 31.10.2014.
 * A class used to define a single achievement, and the conditions a user needs to meet.
 */

//TODO define a premade list in onCreate somewhere where achievements are spawned.
//TODO make "badge-page" ala fitocracy for achievements with icons
    //TODO popup on add time-dialog finished with list of badges achieved
public class Achievement {

    private boolean achieved;
    private String name;
    private Image badgeIcon;

    public Achievement(String name){
        this.name = name;
        achieved = false;
    }

    //if not achieved: calculate if achievedNow() on xp-add.
    public boolean isAchieved(){
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }
}
