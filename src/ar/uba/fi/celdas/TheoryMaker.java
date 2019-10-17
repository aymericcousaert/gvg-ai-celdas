package ar.uba.fi.celdas;

import tools.Vector2d;

import java.util.*;

public class TheoryMaker {
    public Theory getBestTheory(List<Theory> theories) {
        Random randomGenerator = new Random();
        Boolean found = false;
        int index = 0;
        while (!found) {
            index = randomGenerator.nextInt(theories.size());
            if (theories.get(index).getUtility() != 1000) {
                found = true;
            }
        }
        return theories.get(index);
    }

    public Theories updateTheories(Theories theories, Theory theory, Boolean isAlive, Boolean moved, Boolean end, Vector2d exit, Vector2d position) {
        Map<Integer, List<Theory>> theoriesMap = theories.getTheories();
        List<Theory> similarTheories = theoriesMap.get(theory.hashCodeOnlyCurrentState());
        List<Theory> newSimilarTheories = new ArrayList<>();
        if (theories.existsTheory(theory)) {
            for (Theory theo : similarTheories)  {
                if (theo.equals(theory)) {
                    theo.setUsedCount(theo.getUsedCount() + 1);
                    if (isAlive)
                        theo.setSuccessCount(theo.getSuccessCount() + 1);
                } else if (isAlive) {
                    theo.setUsedCount(theo.getUsedCount() + 1);
                }
                newSimilarTheories.add(theo);
            }
            theoriesMap.put(theory.hashCodeOnlyCurrentState(), newSimilarTheories);
            theories.setTheories(theoriesMap);
        }
        else {
            if (similarTheories != null && !similarTheories.isEmpty()) {
                for (Theory theo : similarTheories)  {
                    if (!theo.equals(theory)) {
                        theo.setUsedCount(theo.getUsedCount() + 1);
                    }
                    newSimilarTheories.add(theo);
                }
            }
            theoriesMap.put(theory.hashCodeOnlyCurrentState(), newSimilarTheories);
            theory.setUsedCount(1);
            if (moved && isAlive) {
                theory.setSuccessCount(1);
                theory.setUtility((float)exit.dist(position));
            }
            else {
                theory.setSuccessCount(0);
                theory.setUtility(1000);
            }
            if (end && isAlive) {
                theory.setUtility(0);
            }
            try {
                theories.add(theory);

            } catch (Exception e) {
                e.printStackTrace();
            }
            theories.setTheories(theoriesMap);
        }
        return theories;
    }
}
