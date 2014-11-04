package main.schedul.joakim.information;

import java.util.List;

/**
 * Created by NegatioN on 31.10.2014.
 * A child class used to keep track of achievements involving experience
 */
public class ExperienceAchievement extends Achievement {

    private long totalExperienceGoal, chainExperienceGoal;
    private boolean totalGoal;

    //set total-experience goal or chainexperience goal depending on what type it should be
    public ExperienceAchievement(String name, long totalExperienceGoal, long chainExperienceGoal){
        super(name);
        if(chainExperienceGoal != 0)
            totalGoal = false;
        else
            totalGoal = true;
        this.totalExperienceGoal = totalExperienceGoal;
        this.chainExperienceGoal = chainExperienceGoal;

    }

    //database-constructor
    public ExperienceAchievement(int id, String name, int goalNumber, boolean totalGoal, boolean achieved){
        super(name, achieved, id);
        this.totalGoal = totalGoal;
        if(totalGoal){
            totalExperienceGoal = goalNumber;
        }else{
            chainExperienceGoal = goalNumber;
        }
    }

    //has the achievement been achieved this time? Call on all achievements when time up and xp up.
    public void calculateAchieved(User user){
        if(totalGoal == false)
            setAchieved(reachedChainExperience(user));
        else
            setAchieved(reachedTotalExperience(user));
    }


    //has all the chains combined reached the experience goal the achievement requires?
    private boolean reachedTotalExperience(User user){
        return user.getTotalExperience() > totalExperienceGoal;
    }

    //has a chain reached the chainexperience-goal of this achievement? ex: "User has gotten 1000 xp in Exercise"
    private boolean reachedChainExperience(User user){
        List<Chain> chains = user.getUserChains();

        for(Chain chain : chains){
            if(chain.getCurrentExperience() > chainExperienceGoal)
                return true;
        }
        return false;
    }
    public boolean isTotalGoal() {
        return totalGoal;
    }

    @Override
    public int getGoalNumber() {
        if(totalGoal == true)
            return (int) totalExperienceGoal;
        else
            return (int) chainExperienceGoal;
    }



}
