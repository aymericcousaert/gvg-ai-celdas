package ar.uba.fi.celdas;

import java.util.ArrayList;
import java.util.Random;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;



/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {
    /**
     * Random generator for the agent.
     */
    protected Random randomGenerator;
    /**
     * List of available actions for the agent
     */
    protected ArrayList<Types.ACTIONS> actions;

    protected Planifier planifier;
    protected TheoryMaker theoryMaker;

    protected Theories theories;
    protected StateObservation lastState;
    protected Types.ACTIONS lastAction;
    protected StateObservation lastPredictedState;


    
    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        lastState = null;
        lastAction = null;
        lastPredictedState = null;
        randomGenerator = new Random();
        actions = so.getAvailableActions();
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        StateObservation currentState = stateObs.copy();
        Perception currentPerception = new Perception(currentState);
        if (planifier.pathFounded()) {
            Types.ACTIONS actionToTake = planifier.getNextActionOnPath(currentState.hashCode());
            return actionToTake;
        }




    	actions = stateObs.getAvailableActions();
        int index = randomGenerator.nextInt(actions.size());
        return actions.get(index);
    }

}
