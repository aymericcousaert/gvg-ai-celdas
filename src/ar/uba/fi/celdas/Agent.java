package ar.uba.fi.celdas;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;


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
    protected TheoryPersistant theoryPersistant;

    protected Theories theories;

    private StateObservation lastState;
    private Types.ACTIONS lastAction;
    private Vector2d lastPosition;
    private Types.ACTIONS action;

    private Vector2d exit;

    private String charArrayToStr(char[][] charrarray){
        StringBuilder sb = new StringBuilder("");
        if(charrarray!=null){
            for(int i=0;i< charrarray.length; i++){
                for(int j=0;j<  charrarray[i].length; j++){
                    sb.append(charrarray[i][j]);
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }


    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        lastState = null;
        lastAction = null;
        lastPosition = null;
        randomGenerator = new Random();

        try {
            theories = TheoryPersistant.load();
        } catch (FileNotFoundException e) {
            theories = new Theories();
        }

        exit = so.getPortalsPositions()[0].get(0).position;

        planifier = new Planifier(theories);
        theoryMaker = new TheoryMaker();
        theoryPersistant = new TheoryPersistant();
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
        Vector2d currentPosition = currentState.getAvatarPosition();
        actions = stateObs.getAvailableActions();
        if (lastState == null && lastAction == null) {
            int index = randomGenerator.nextInt(actions.size());
            action = actions.get(index);
            lastState = currentState;
            lastAction = action;
            lastPosition = currentPosition;
            System.out.println(action);
            return action;
        }
        if (planifier.isFull()) {
            planifier.buildGraph();
            action = planifier.getNextActionOnPath(charArrayToStr(currentPerception.getLevel()).hashCode());
            lastState = currentState;
            lastAction = action;
            lastPosition = currentPosition;
            System.out.println("\n" + "***");
            System.out.println(currentPerception.toString());
            System.out.println(action);
            return action;
        }
        Perception lastPerception = new Perception(lastState);
        Theory theory = new Theory(lastPerception.getLevel(), lastAction, currentPerception.getLevel());
        this.theories = theoryMaker.updateTheories(theories, theory, true, !lastPosition.equals(stateObs.getAvatarPosition()), false, exit, stateObs.getAvatarPosition());
        List<Theory> theoriesAvailable = theories.getTheories().get(theory.hashCodeOnlyPredictedState());
        List<Types.ACTIONS> actionsDone = new ArrayList<>();
        if (theoriesAvailable != null) {
            for (Theory theo : theoriesAvailable) {
                actionsDone.add(theo.getAction());
            }
        }
        if (actionsDone.size() == actions.size()) {
            Theory bestTheory = theoryMaker.getBestTheory(theoriesAvailable);
            action = bestTheory.getAction();
        }
        else {
            List<Types.ACTIONS> otherActions = actions;
            otherActions.removeAll(actionsDone);
            int index = randomGenerator.nextInt(otherActions.size());
            action = otherActions.get(index);
        }
        lastState = currentState;
        lastAction = action;
        lastPosition = currentPosition;
        System.out.println("\n" + "***");
        System.out.println(currentPerception.toString());
        System.out.println(action);
        return action;
    }

    public void result(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer)
    {
        boolean GameOver = stateObs.isGameOver();
        Perception lastPerception = new Perception(lastState);
        Perception currentPerception = new Perception(stateObs);
        Theory theory = new Theory(lastPerception.getLevel(), lastAction, currentPerception.getLevel());
        theories = theoryMaker.updateTheories(theories, theory, stateObs.isAvatarAlive(), true, GameOver, exit, stateObs.getAvatarPosition());
        try {
            TheoryPersistant.save(theories);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
