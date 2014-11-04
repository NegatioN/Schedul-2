package main.schedul.joakim.information;

/**
 * Created by NegatioN on 02.11.2014.
 * defines a combo-achievement. get x combo in a chain, get
 */
public class ComboAchievement extends Achievement{

    private int comboGoal;

    public ComboAchievement(String name, int comboGoal){
        super(name);
        this.comboGoal = comboGoal;
    }

    //database-constructor
    public ComboAchievement(int id, String name, int comboGoal, boolean achieved){
        super(name, achieved,id);
        this.comboGoal = comboGoal;
    }

    public void calculateAchieved(int combo){
        setAchieved(combo >= comboGoal);
    }

    @Override
    public int getGoalNumber() {
        return comboGoal;
    }

}
