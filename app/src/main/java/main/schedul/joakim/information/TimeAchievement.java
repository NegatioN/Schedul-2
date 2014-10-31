package main.schedul.joakim.information;

import java.util.List;

/**
 * Created by NegatioN on 31.10.2014.
 */
public class TimeAchievement extends Achievement {

    private long minutesChainGoal, minutesTotalGoal;


    //set one of the time-variables to 0, so we determine what type of achievement it is.
    public TimeAchievement(String name, long minutesChainGoal, long minutesTotalGoal){
        super(name);
        this.minutesChainGoal = minutesChainGoal;
        this.minutesTotalGoal = minutesTotalGoal;
    }

    private boolean reachedChainTime(User user){
        List<Chain> chains = user.getUserChains();

        for(Chain chain : chains){
            if(chain.getTotalMins() > minutesChainGoal)
                return true;
        }
        return false;
    }

    private boolean reachedTotalTime(User user){
        return user.getTotalTime() > minutesTotalGoal;
    }

}
