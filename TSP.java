package apcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TSP {
    private int N;
    private Point[] points;
    private boolean[] marked;

    public TSP(String fileIn) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(fileIn));

        int numPoints = sc.nextInt();
        points = new Point[numPoints];
        marked = new boolean[numPoints];
        
        for (int i=0; i<numPoints; i++) 
            points[i] = new Point(sc.nextInt(), sc.nextInt());
    }

    public double distance (int i, int j) {
        return points[i].distance(points[j]);
    }

    public int findUnmarkedPointClosestToPoint(int j) {
        int closest = 0;
        double closestDistance = Integer.MAX_VALUE;

        for (int i = 0; i < marked.length; i++)
            if (!marked[i])
                if (distance(j, i) < closestDistance) { 
                    closestDistance = distance(j, i);
                    closest = i;
                }
        
        return closest;
    }

    private boolean hasUnmarked() {
        for (boolean val: marked)
            if (!val) return true;
        return false;
    }

    public void applyDoubleNeighborHeuristic() {
        int A = 0;
        marked[0] = true;

        int B = findUnmarkedPointClosestToPoint(A);
        marked[B] = true;

        double length_of_cycle = distance(A, B);
        ArrayList<Integer> path = new ArrayList<>();

        path.add(A);
        path.add(B);

        while (hasUnmarked()) {
            int p1 = findUnmarkedPointClosestToPoint(A);
            int p2 = findUnmarkedPointClosestToPoint(B);

            if (distance(A, p1) < distance(B, p2)) {
                length_of_cycle += distance(A, p1);
                A = p1;
                path.add(0, A);
                marked[A] = true;
            }
            else {
                length_of_cycle += distance(B, p2);
                B = p2;
                path.add(B);
                marked[B] = true;
            }
        }

        path.add(path.get(0));
        length_of_cycle += distance(path.get(0), path.get(path.size() - 2));


        System.out.println("+++++++++++++++++++++++++++++++\n" + "+Traveling Salesperson Problem+\n" + "+++++++++++++++++++++++++++++++\n" + "+  Double Neighbor Heuristic  +\n" + "+++++++++++++++++++++++++++++++");
        System.out.println("\nN = " + points.length + " points\n");

        System.out.println("--------|------------");
        System.out.println("index\t|  point");
        for (int i = 0; i < points.length; i++)
            System.out.println(i + "\t|  " + points[i]);
        
        System.out.println("\n+++++++++++++++\n" +
                        "+ Cycle found +\n" +
                        "+++++++++++++++");
        
        for (int i = 0; i < path.size() - 1; i++)
            System.out.print("\n" + points[path.get(i)] + " ->- " + Math.round(distance(path.get(i), path.get(i + 1)) * 100) / 100.0 + " ->- ");
        
        System.out.println(points[path.get(path.size() - 1)]);

        System.out.println("\n++++++++++++++++++++++\n" +
                        "+ Total Cycle Length +\n" +
                        "++++++++++++++++++++++\n" +
                        "");
        
        System.out.println(length_of_cycle);
    }


    // prim's algorithm
    // https://www.programiz.com/dsa/prim-algorithm
    public ArrayList<int[]> createSpanningTree(int start) {
        ArrayList<int[]> T = new ArrayList<>(); // empty set T denotes edges in the tree
        ArrayList<Integer> U = new ArrayList<>(); // set U = {start} denotes visited nodes
        ArrayList<Integer> V = new ArrayList<>(); // set V = {all points} denotes all unvisited nodes

        U.add(start);
        for (int i = 0; i < points.length; i++)
            if (i != start)
                V.add(i);
        
        while (V.size() > 0) {
            int[] edge = new int[2];
            double weight = Integer.MAX_VALUE;

            for (int x: U)
                for (int y: V)
                    if (distance(x, y) < weight) {
                        edge[0] = x;
                        edge[1] = y;
                        weight = distance(x, y);
                    }
                    
            U.add(edge[1]);
            V.remove(V.indexOf(edge[1]));
            T.add(edge);
        }

        return T;
    }

    private int countEdgesWithPoint(int index, ArrayList<int[]> MST) {
        int count = 0;
        for (int[] edge: MST)
            if (edge[0] == index || edge[1] == index)
                count++;
        
        return count;
    }

    private void print2DList(ArrayList<int[]> list) {
        System.out.print("[");
        for (int[] item: list)
            System.out.print(Arrays.toString(item) + ", ");
        System.out.println("]");
    }

    private void insertAtAscending(int[] edge, ArrayList<int[]> list) {
        if (list.size() == 1) {
            if (distance(edge[0], edge[1]) > distance(list.get(0)[0], list.get(0)[1]))
                list.add(edge);
            else
                list.add(0, edge);
        }
        else {
        for (int i = 0; i < list.size(); i++) {
            if (i == 0 && distance(edge[0], edge[1]) < distance(list.get(i)[0], list.get(i)[1])) {
                list.add(0, edge);
                break;
            }
            else if (
                distance(edge[0], edge[1]) < distance(list.get(i)[0], list.get(i)[1])
                && distance(edge[0], edge[1]) > distance(list.get(i - 1)[0], list.get(i - 1)[1])
            ) {
                list.add(i, edge);
                break;
            }
            else if (i == list.size() - 1) {
                list.add(edge);
                break;
            }
        }
        }
    }

    private boolean checkUsed(int[] edge, ArrayList<int[]> matches) {
        int x = edge[0];
        int y = edge[1];
        for (int[] e: matches)
            if (x == e[0] || x == e[1] || y == e[0] || y == e[1])
                return true;
        return false;
    }

    public static <E> boolean inList(E o, ArrayList<E> list) {
        for (E i: list)
            if (i.equals(o))
                return true;
        return false;
    }

    private boolean isTraversable(ArrayList<int[]> edgeList, int start) {
        ArrayList<int[]> visited = new ArrayList<>();
        ArrayList<Integer> nodesToCheck = new ArrayList<>();
        nodesToCheck.add(start);

        while (nodesToCheck.size() > 0) {
            int node = nodesToCheck.remove(0);

            ArrayList<int[]> edges = new ArrayList<>();
            for (int[] edge: edgeList) {
                if (edge[0] == node || edge[1] == node) 
                    edges.add(edge);
            }

            for (int[] edge: edges) {
                if (!inList(edge, visited)) {
                    visited.add(edge);
                    int next = node == edge[0] ? edge[1] : edge[0];
                    nodesToCheck.add(next);
                }
            }
        }

        return visited.size() == edgeList.size();
    }

    private int findNext(int index, ArrayList<int[]> edgeList) {
        ArrayList<Integer> nodes = new ArrayList<>();
        for (int[] edge: edgeList) {
            if (edge[0] == index) 
                nodes.add(edge[1]);
            else if (edge[1] == index) 
                nodes.add(edge[0]);
        }

        for (int i = 0; i < nodes.size(); i++) {
            int node = nodes.get(i);
            if (isBridge(index, node, edgeList) && nodes.size() > 1) {
                nodes.remove(i);
                i -= 1;
            }
        }

        return nodes.get(0);
    }

    private boolean isBridge(int start, int to, ArrayList<int[]> edgeList) {
        int edgeIndex = matchIndex(start, to, edgeList);
        int[] edgeVal = edgeList.get(edgeIndex);
        edgeList.remove(edgeIndex);
        if (isTraversable(edgeList, to)) {
            edgeList.add(edgeIndex, edgeVal);
        }
        else {
            edgeList.add(edgeIndex, edgeVal);
            return true;
        }

        for (int[] edge: edgeList)
            if (!((edge[0] == start && edge[1] == to) || (edge[0] == to && edge[1] == start))
            && (edge[0] == to || edge[1] == to))
                return false;
        return true;
    }

    private int matchIndex(int start, int end, ArrayList<int[]> list) {
        int index = 0;
        for (int i = 0; i < list.size(); i++)
            if ((list.get(i)[0] == start && list.get(i)[1] == end) || (list.get(i)[0] == end && list.get(i)[1] == start))
                index = i;
        return index;
    }

    public static <E> int countOcurrences(E o, ArrayList<E> list, int stopIndex) {
        int count = 0;
        for (int i = 0; i <= stopIndex; i++)
            if (list.get(i).equals(o))
                count ++;
        return count;
    }

    // https://cse442-17f.github.io/Traveling-Salesman-Algorithms/
    public void applyChristofides() {
        System.out.println("\n\n\nCHRISTOPHIDES APPROXIMATION\n");
        ArrayList<int[]> MST = new ArrayList<>();
        double cost = Integer.MAX_VALUE;
        for (int i = 0; i < points.length; i++) {
            ArrayList<int[]> T = createSpanningTree(i);
            double c = 0;
            for (int[] edge: T)
                c += distance(edge[0], edge[1]);
            if (c < cost) {
                MST = T;
                cost = c;
            }
        }

        System.out.println("EDGES: ");
        print2DList(MST);
        
        System.out.println("COST: " + cost);

        // odd nodes
        ArrayList<Integer> odd = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            if (countEdgesWithPoint(i, MST) % 2 == 1)
                odd.add(i);
        }

        System.out.println("ODD NODES: " + odd);
        
        // brute-forcing an implementation of the blossom algorithm for minimum cost perfect matching
        ArrayList<int[]> matches = new ArrayList<>();
        ArrayList<int[]> sampleSpace = new ArrayList<>();

        for (int i = 0; i < odd.size(); i++)
            for (int j = i + 1; j < odd.size(); j++) {
                int[] edge = {odd.get(i), odd.get(j)};
                sampleSpace.add(edge);
            }
        
        ArrayList<int[]> sortedSS = new ArrayList<>();
        sortedSS.add(sampleSpace.get(0));
        for (int i = 1; i < sampleSpace.size(); i++) {
            int[] edge = sampleSpace.get(i);
            insertAtAscending(edge, sortedSS);
        }

        double[] distances = new double[sortedSS.size()];
        for (int i = 0; i < sortedSS.size(); i++) {
            distances[i] = distance(sortedSS.get(i)[0], sortedSS.get(i)[1]);
        }

        // System.out.println("Sorted sample space: ");
        // print2DList(sortedSS);
        // System.out.println("Distances for each point above: ");
        // System.out.println(Arrays.toString(distances));
        for (int[] edge: sortedSS)
            if (!checkUsed(edge, matches))
                matches.add(edge);
        
        System.out.println("Shortest pairs: ");
        print2DList(matches);

        ArrayList<int[]> edgeList = MST;
        for (int[] edge: matches)
            edgeList.add(edge);

        // roughly apply fleury's algorithm to create a eulerian tour
        ArrayList<Integer> circuit = new ArrayList<>();
        int v = 0;
        while (edgeList.size() > 0) {
            int next = findNext(v, edgeList);
            circuit.add(next);
            edgeList.remove(matchIndex(v, next, edgeList));
            v = next;
        }
        System.out.println("Circuit:");
        System.out.println(circuit);

        // Create a Hamiltonian Circuit by removing all duplicate cities + looping back to the beginning
        System.out.println("Applying Hamiltonian correction...");
        for (int i = 0; i < circuit.size(); i++) {
            if (countOcurrences(circuit.get(i), circuit, i) > 1)
                circuit.remove(i--);
        }
        circuit.add(circuit.get(0));
        System.out.println("Corrected list: ");
        System.out.println(circuit);
        System.out.println("\nPath:");
        for (int i = 0; i < circuit.size() - 1; i++) {
            System.out.println(points[circuit.get(i)] + " ->- " + distance(circuit.get(i), circuit.get(i + 1)));
        }
        System.out.println(points[circuit.get(0)]);

        double totalDist = 0;
        for (int i = 0; i < circuit.size() - 1; i++)
            totalDist += distance(circuit.get(i), circuit.get(i + 1));
        System.out.println("Total Dist: " + totalDist);
    }

    public static void main(String[] args) throws FileNotFoundException {
        TSP client = new TSP("tspTest.txt");
        client.applyDoubleNeighborHeuristic();
        client.applyChristofides();
    }
}
