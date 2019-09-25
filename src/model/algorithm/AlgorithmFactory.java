package model.algorithm;

public class AlgorithmFactory {
    private final static String RANDOM_SEARCH = "Random Search";
    private final static String HILL_CLIMBING = "Hill Climbing";
    private final static String MOST_PROMISING = "Most Promising";

    public Algorithm getAlgorithm(String tipo)
    {
        switch (tipo)
        {
            case RANDOM_SEARCH: return AlgorithmRandomSearch.getAlgorithm();
            case HILL_CLIMBING: return AlgorithmHillClimbing.getAlgorithm();
            case MOST_PROMISING: return AlgorithmMostPromising.getAlgorithm();
            default: return null;
        }
    }
}
