package main.schedul.joakim.information;

import java.util.List;

/**
 * Created by NegatioN on 31.10.2014.
 */
public class TimeAchievement extends Achievement {

    private long minutesChainGoal, minutesTotalGoal;
    private boolean totalGoal;


    //set one of the time-variables to 0, so we determine what type of achievement it is.
    public TimeAchievement(String name, long minutesTotalGoal,long minutesChainGoal){
        super(name);
        if(minutesTotalGoal != 0)
            totalGoal = true;
        else
            totalGoal = false;
        this.minutesChainGoal = minutesChainGoal;
        this.minutesTotalGoal = minutesTotalGoal;
    }

    //Database-conscturctor
    public TimeAchievement(int id, String name, int goalNumber, boolean totalGoal, boolean achieved){
        super(name, achieved, id);
        this.totalGoal = totalGoal;
        if(totalGoal == true){
            minutesTotalGoal = goalNumber;
        }else{
            minutesChainGoal = goalNumber;
        }
    }

    public boolean achievedTime(User user){
        if(totalGoal == true)
            return reachedTotalTime(user);
        else
            return reachedChainTime(user);
    }

    private boolean reachedChainTime(User user){
        List<Chain> chains = user.getUserChains();

        for(Chain chain : chains){
            if(chain.getTotalMins() > minutesChainGoal)
                return true;
        }
        return false;
    }
    public boolean isTotalGoal() {
        return totalGoal;
    }

    private boolean reachedTotalTime(User user){
        return user.getTotalTime() > minutesTotalGoal;
    }

    @Override
    public int getGoalNumber() {
        if(totalGoal == true)
            return (int)minutesTotalGoal;
        else
            return (int)minutesChainGoal;

    }
}
