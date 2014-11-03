package main.schedul.joakim.information;

/**
 * Created by NegatioN on 02.11.2014.
 * defines a combo-achievement. get x combo in a chain, get x-combos going in total across chains. etc
 */
public class ComboAchievement extends Achievement{

    private int comboGoal;

    public ComboAchievement(String name, int comboGoal){
        super(name);
        this.comboGoal = comboGoal;
    }

    public boolean calculateAchieved(int combo){
        return combo >= comboGoal;
    }

}