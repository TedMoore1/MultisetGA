package org.evosuite.ga.multisetga;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Comparator;
import java.util.Collections;
import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.execution.ExecutionTrace;

public class MultisetDiversitySelectionFunction<T extends Chromosome<T>> {
    private List<Integer> rankedIndexes = new ArrayList<>();
    private static double swapProbability = 0.05;

    // Rank chromosomes by diversity for a single goal.
    MultisetDiversitySelectionFunction(List<T> population, String goalId) {
        HashMap<Integer, Double> diversityMap = new HashMap<>();
        List<Integer> indexMap = new ArrayList<>();

        for (int i=0; i < population.size(); i++) {
            indexMap.add(i);
            T solution = population.get(i);

            double sumDiversity = 0;
            int totalVectorCount = 0;

            TestChromosome chromosome = (TestChromosome) solution;
            ExecutionTrace trace = chromosome.getLastExecutionResult().getTrace();

            String bff_id = goalId;
            if (!trace.getHitInstrumentationPoints().contains(bff_id)) {
                diversityMap.put(i, 0.0); // nothing was captured for this goal by this chromosome.
            } else {
                for (List<Integer> v: trace.getHitInstrumentationData(bff_id)) {
                    Vector vec = new Vector(v);
                    sumDiversity += vec.internalDiversity();
                    totalVectorCount ++;
                }

                double diversity = sumDiversity / totalVectorCount;
                diversityMap.put(i, diversity);
            }
        }

        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Double.compare(diversityMap.get(o1), diversityMap.get(o2));
            }
        };

        Collections.sort(indexMap, Collections.reverseOrder(comparator));
        Random r = new Random();
        for (int i = 0; i < indexMap.size() / 2 ; i++) {
            if (r.nextFloat() < swapProbability) {
                // Swap chromosomes at i, indexMap.size() - 1 - i
                int t = indexMap.get(i);
                indexMap.set(i, indexMap.get(indexMap.size() - 1 - i));
                indexMap.set(indexMap.size() - 1 - i, t);
            }
        }

        this.rankedIndexes = indexMap;

    }

    MultisetDiversitySelectionFunction(List<T> population) {
        HashMap<Integer, Double> diversityMap = new HashMap<>();

        List<Integer> indexMap = new ArrayList<>();
        for (int i =0; i< population.size(); i++) {
            indexMap.add(i);
            T solution = population.get(i);

            double sumDiversity = 0;
            int totalVectorCount = 0;

            // Cast to chromosome
            TestChromosome chromosome = (TestChromosome) solution;
            ExecutionTrace trace = chromosome.getLastExecutionResult().getTrace();
            Set<String> hitGoals = trace.getHitInstrumentationPoints();
            for (String goal: hitGoals) {
                for (List<Integer> v: trace.getHitInstrumentationData(goal)) {
                    Vector vec = new Vector(v);
                    sumDiversity += vec.internalDiversity();
                    totalVectorCount++;
                }
            }

            double diversity = sumDiversity / totalVectorCount;
            diversityMap.put(i, diversity);
        }

        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Double.compare(diversityMap.get(o1), diversityMap.get(o2));
            }
        };
        Collections.sort(indexMap, Collections.reverseOrder(comparator));
        this.rankedIndexes = indexMap;


        this.rankedIndexes = indexMap;
    }

    public int getNextIndex() {
        return rankedIndexes.remove(0);
    }
}
