package ar.uba.fi.celdas;

import core.game.StateObservation;
import ontology.Types;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.Map;

public class Planifier {

    private Map<Integer, List<Theory>> theoriesMap;
    private Graph<Integer, DefaultEdge> graph;
    public boolean hasPath;
    private int endVertex;

    public Planifier(Theories someTheories) {
        theoriesMap = someTheories.getTheories();
        graph = new DefaultDirectedWeightedGraph<>(DefaultEdge.class);
        hasPath = true;
    }

    public void buildGraph() {
        for (List<Theory> theoryList : theoriesMap.values()) {
            for (Theory theo : theoryList) {
                int firstVertex = theo.hashCodeOnlyCurrentState();
                int secondVertex = theo.hashCodeOnlyPredictedState();
                graph.addVertex(firstVertex);
                graph.addVertex(secondVertex);
                DefaultEdge edge = graph.addEdge(firstVertex, secondVertex);
                if (edge != null) {
                    graph.setEdgeWeight(edge, theo.getSuccessCount() / theo.getUsedCount());
                }
            }
        }
    }

    public List<Integer> getShortestPath(Integer startVertex) {
        DijkstraShortestPath<Integer, DefaultEdge> dijkstra = new DijkstraShortestPath(graph);
        GraphPath<Integer,DefaultEdge> edges = dijkstra.getPath(startVertex,endVertex);
        return edges.getVertexList();
    }

    public Boolean pathFounded() {
        if (hasPath) return true;
        return false;
    }

    public Types.ACTIONS getNextActionOnPath(Integer startVertex) {
        Types.ACTIONS action = null;
        List<Theory> theories = theoriesMap.get(startVertex.hashCode());
        for (Theory theo : theories) {
            if (theo.getPredictedState().hashCode() == getShortestPath(startVertex).get(1)) {
                action = theo.getAction();
            }
        }
        return action;
    }





}
