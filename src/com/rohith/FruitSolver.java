package com.rohith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class Point{
    Integer x, y;
    public Point(Integer x, Integer y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString(){
        return "(" + x.toString() + ", " + y.toString() + ") ";
    }
}

class PointScore{
    Integer score;
    Point point;
    public PointScore(Integer score, Point point){
        this.score = score;
        this.point = point;
    }

    public static PointScore max(PointScore p1, PointScore p2){
        if(p1 == null)
            return p2;
        if(p2 == null)
            return p1;
        if(p1.score >= p2.score)
            return p1;
        return p2;
    }

    public static PointScore min(PointScore p1, PointScore p2){
        if(p1 == null)
            return p2;
        if(p2 == null)
            return p1;

        if(p1.score < p2.score)
            return p1;
        return p2;
    }

    @Override
    public String toString(){
        return "score: " + score.toString() + " " + point.toString();
    }
}

class Input{
    Integer [][] board;
    Integer size, fruits;
    Double time;
    public Input(Integer [][] board, Integer size, Integer fruits, Double time){
        this.board = board;
        this.size = size;
        this.fruits = fruits;
        this.time = time;
    }
}

public class FruitSolver {


    public Input readInput(){
        Stream<String> rawData;
        List<String> data;
        String cur;
        Integer size, fruits, j, i;
        Integer [][] board;
        Double time;
        try{
            String curDir = System.getProperty("user.dir");
            rawData = Files.lines(Paths.get(curDir + "/data/test.txt"));
            data = rawData.collect(Collectors.toList());
            size = Integer.parseInt(data.remove(0));
            fruits = Integer.parseInt(data.remove(0));
            time = Double.parseDouble(data.remove(0));
            board = new Integer[size][size];
            for(i = 0; i<size; i++){
                cur = data.remove(0);
                for(j=0; j<size; j++){
                    board[i][j] = Integer.parseInt(String.valueOf(cur.charAt(j)));
                }
            }
            return new Input(board, size, fruits, time);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void printBoard(Integer [][] board, Integer size){
        Integer i, j;
        for(i=0;i<size;i++){
            for(j=0;j<size;j++){
                if(board[i][j] != -1)
                    System.out.print(board[i][j]);
                else
                    System.out.print("*");
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public void printBoard(Integer [] board, Integer size){
        Integer i;
        for(i = 0; i<size;i++)
            System.out.print(board[i].toString() + " ");
        System.out.println();
    }

    public void printPointMap(HashMap<Point, Integer> pointVal){
        Iterator<Map.Entry<Point, Integer>> e  = pointVal.entrySet().iterator();
        while (e.hasNext()){
            Map.Entry<Point, Integer> p = e.next();
            System.out.print("(" + p.getKey().x.toString() + ", " + p.getKey().y.toString() + "):" + p.getValue() + " ");
        }
        System.out.println();
    }

    // has side effect, makes changes into the data structure passed
    public void removeFruitRecr(Integer [][] board, Point point, Integer val, Integer size){
        Integer x = point.x, y = point.y;
        if(x >= 0 && x < size && y >= 0 && y < size && board[x][y] != -1) {
            if (board[x][y] == val) {
                board[x][y] = -1; // this represents that fruit has been claimed
                removeFruitRecr(board, new Point(x, y+1) , val, size);
                removeFruitRecr(board, new Point(x, y-1) , val, size);
                removeFruitRecr(board, new Point(x+1, y) , val, size);
                removeFruitRecr(board, new Point(x-1, y) , val, size);
            }
        }
    }

    public void removeFruit(Integer [][] board, Point point, Integer size){
        removeFruitRecr(board, point, board[point.x][point.y], size);
    }

    public Integer [][] deepcopy(Integer [][] board, Integer size){
        Integer [][] newBoard = new Integer[size][size];
        Integer i, j;
        for(i=0;i<size;i++)
            for(j=0;j<size;j++)
                newBoard[i][j] = board[i][j];
        return newBoard;
    }

    //has no side effects on board
    public Integer pointValRecr(Integer [][] board, Integer [][] visited, Integer size, Point point, Integer val){
        Integer x = point.x, y = point.y, cur;
        if(x >= 0 && x < size && y >= 0 && y < size && visited[x][y] == 0 && board[x][y] == val){
            visited[x][y] = 1;
            cur =   pointValRecr(board, visited, size, new Point(x+1, y), val) +
                    pointValRecr(board, visited, size, new Point(x-1, y), val) +
                    pointValRecr(board, visited, size, new Point(x, y+1), val) +
                    pointValRecr(board, visited, size, new Point(x, y-1), val);
            return 1+cur;
        }
        else
            return 0;
    }

    private Boolean checkIfConnectedPoints(Integer [][] board, Integer [][] visited, Integer size, Point p1, Point p2){

        Integer x = p1.x, y = p1.y;
//        System.out.println(p1);

        if(x >= 0 && x < size && y >= 0 && y < size && visited[p1.x][p1.y] == 0){
            if(board[p1.x][p1.y] != board[p2.x][p2.y])
                return false;

            if(p1.x == p2.x && p1.y == p2.y)
                return true;

            visited[p1.x][p1.y] = 1;

            return  checkIfConnectedPoints(board, visited, size, new Point(x+1, y), p2) ||
                    checkIfConnectedPoints(board, visited, size, new Point(x-1, y), p2) ||
                    checkIfConnectedPoints(board, visited, size, new Point(x, y+1), p2) ||
                    checkIfConnectedPoints(board, visited, size, new Point(x, y-1), p2);
        }
        return false;

    }

    public Boolean checkIfConnected(Integer [][] board, Integer size, Point p1, Point p2){
        Integer [][] visited = zeroBoard(size);
        return checkIfConnectedPoints(board, visited, size, p1, p2);
    }

    public Integer [][] zeroBoard(Integer size){
        Integer [][] visited = new Integer[size][size];
        Integer i, j;
        for(i=0;i<size;i++)
            for(j=0;j<size;j++)
                visited[i][j] = 0;
        return visited;
    }

    //has no side effects on board
    public Integer pointVal(Integer [][] board, Integer size, Point point){
        Integer [][] visited = zeroBoard(size);
        return pointValRecr(board, visited, size, point, board[point.x][point.y]);
    }


    //function to assign value to a given board, doesn't have side effect
    public Integer eval(Integer [][] board, Integer size){
        Integer i, j, max=0, cur;
        Point maxPoint = new Point(0, 0); // ToDo: look if you can initialize like this
        for(i=0;i<size;i++){
            for(j=0;j<size;j++){
                cur = pointVal(board, size, new Point(i, j));
                if(max < cur){
                    max = cur;
                    maxPoint = new Point(i, j);
                }
            }
        }
        return max;
    }

    public ArrayList<Point> getBranchPoints(Integer board[][], Integer size){

        Integer i, j, curCount, prev, l;
        ArrayList<Point> uniquePoints = new ArrayList<>(), points = new ArrayList<>();

        // return all points if size is less then threshold
        if(size < 0){
            for(i=0;i<size;i++)
                for(j=0;j<size;j++)
                    points.add(new Point(i, j));
            return points;
        }

        HashMap<Point, Integer> pointVal;
        //row wise max
        for(i=0;i<size;i++){
            j=1;
            prev = board[i][j-1];
            pointVal = new HashMap<>();
            curCount=1;
            while(j<size){
                if(board[i][j] != prev){
                    pointVal.put(new Point(i, j-1), curCount);
                    curCount = 0;
                    prev = board[i][j];
                }
                j++;
                curCount++;
            }
            pointVal.put(new Point(i, j-1), curCount);
            points.addAll(takeTop(pointVal));
        }

        //col wise max
        for(i=0;i<size;i++){
            j=1;
            prev = board[j-1][i];
            pointVal = new HashMap<>();
            curCount = 1;
            while (j<size){
                if(board[j][i] != prev){
                    pointVal.put(new Point(j-1, i), curCount);
                    curCount = 0;
                    prev = board[j][i];
                }
                j++;
                curCount++;
            }
            pointVal.put(new Point(j-1, i), curCount);
            points.addAll(takeTop(pointVal));
        }

        l = points.size();
//        System.out.println(points);
        for(i=0;i<l;i++){
            Boolean con = true;
            for(j=i+1;j<l;j++){
//                System.out.println(points.get(i).toString() +" " + points.get(j).toString() + " " + checkIfConnected(board, size, points.get(i), points.get(j)).toString());
                if(checkIfConnected(board, size, points.get(i), points.get(j))){
                    con = false;
                }
            }
            if(con)
                uniquePoints.add(points.get(i));
        }
        //ToDo: remove duplicates
        return uniquePoints;
    }

    //sorts and returns in descending order
    public ArrayList<Point> sortMap(HashMap<Point, Integer> map){
        List list = new LinkedList(map.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2){
                return ((Comparable)((Map.Entry)(o1)).getValue()).compareTo(((Map.Entry)(o2)).getValue());
            }
        });

        Collections.reverse(list);
        ArrayList<Point> points = new ArrayList<>();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            points.add((Point)entry.getKey());
        }
        return points;
    }

    public ArrayList<Point> takeTop(HashMap<Point, Integer> pointVal){
        ArrayList<Point> list = sortMap(pointVal);
        list.subList(0, 1); //taking top 2
        return list;
    }


    /**
     * @param turn return max node for +1, and min node for -1
     */
    private PointScore abPruning(Integer [][] board, Integer size, Integer depth, Integer alpha, Integer beta, Integer turn){

        ArrayList<Point> children = getBranchPoints(board, size);

        System.out.println("Evaluating at depth " + depth.toString());
        printBoard(board, size);
        System.out.println("alpha " + alpha.toString() + " beta " + beta.toString());
        System.out.println("children for current node");
        System.out.println(children);

        Point point;
        PointScore status, curStatus;
        HashMap<Point, Integer> scoreInfo = new HashMap<>();
        Integer i, j, s, l = children.size();
        Integer [][] newBoard;

        for(i=0;i<l;i++){
            point = children.get(i);
            s = pointVal(board, size, point);
            scoreInfo.put(point, s);
        }

        printPointMap(scoreInfo);


        children = sortMap(scoreInfo); // ordered children with descending eval value

        if(turn == 1) {
            if (depth == 0) {
                point = children.get(0);
                return new PointScore(scoreInfo.get(point), point);
            } else {
                curStatus = new PointScore(Integer.MIN_VALUE, null);
                for (i = 0; i < l; i++) {
                    newBoard = deepcopy(board, size);
                    removeFruit(newBoard, children.get(i), size);
                    status = abPruning(newBoard, size, depth-1, alpha, beta, turn*-1);
                    curStatus = PointScore.max(curStatus, status);
                    if(curStatus.score >= beta){
                        return curStatus;
                    }
                    alpha = Math.max(curStatus.score, alpha);
                }
                return curStatus;
            }
        }

        else {
            if (depth == 0) {
                point = children.get(children.size()-1);
                return new PointScore(scoreInfo.get(point), point);
            } else {
                curStatus = new PointScore(Integer.MAX_VALUE, null);
                // check in reverse order
                for (i = l-1; i >= 0; i--) {
                    newBoard = deepcopy(board, size);
                    removeFruit(newBoard, children.get(i), size);
                    status = abPruning(newBoard, size, depth-1, alpha, beta, turn*-1);
                    curStatus = PointScore.min(curStatus, status);
                    if(curStatus.score <= alpha)
                        return curStatus;
                    beta = Math.min(beta, curStatus.score);
                }
                return curStatus;
            }
        }
    }

    public void alpha_beta(Input input, Integer depth){
        PointScore decision = abPruning(input.board, input.size, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
        System.out.println(decision);
    }

}
