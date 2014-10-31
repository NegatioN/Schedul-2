package main.schedul.joakim.information;

import java.util.List;

/**
 * Created by NegatioN on 31.10.2014.
 * A child class used to keep track of achievements involving experience
 */
public class ExperienceAchievement extends Achievement {

    private long experienceGoal, chainExperienceGoal;

    //set total-experience goal or chainexperience goal depending on what type it should be
    public ExperienceAchievement(String name, long experienceGoal, long chainExperienceGoal){
        super(name);
        this.experienceGoal = experienceGoal;
        this.chainExperienceGoal = chainExperienceGoal;

    }

    //has the achievement been achieved this time? Call on all achievements when time up and xp up.
    public void calculateAchieved(List<Chain> chains){
        if(chainExperienceGoal != 0)
            setAchieved(reachedChainExperience(chains));
        else
            setAchieved(reachedTotalExperience(chains));
    }


    //has all the chains combined reached the experience goal the achievement requires?
    private boolean reachedTotalExperience(List<Chain> chains){
        if(experienceGoal == 0)
            return false;
        long totalXP = 0;
        for(Chain chain : chains){
            totalXP += chain.getCurrentExperience();
        }

        //is the total experience greater than the goal of the achievement?
        return totalXP > experienceGoal;
    }

    //has a chain reached the chainexperience-goal of this achievement? ex: "User has gotten 1000 xp in Exercise"
    private boolean reachedChainExperience(List<Chain> chains){
        if(chainExperienceGoal == 0)
            return false;

        for(Chain chain : chains){
            if(chain.getCurrentExperience() > chainExperienceGoal)
                return true;
        }
        return false;
    }





}
