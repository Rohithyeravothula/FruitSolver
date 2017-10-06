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

    public Boolean checkIfConnected(Integer [][] board, Integer size, Point p1, Point p2){

        if(board[p1.x][p1.y] != board[p2.x][p2.y])
            return false;

        if(p1.x == p2.x && p1.y == p2.y)
            return true;

        Integer x = p1.x, y = p1.y;
        if(x >= 0 && x < size && y >= 0 && y < size){
            return  checkIfConnected(board, size, new Point(x+1, y), p2) ||
                    checkIfConnected(board, size, new Point(x-1, y), p2) ||
                    checkIfConnected(board, size, new Point(x, y+1), p2) ||
                    checkIfConnected(board, size, new Point(x, y-1), p2);
        }
        return false;

    }

    public Integer pointVal(Integer [][] board, Integer size, Point point){
        Integer [][] visited = new Integer[size][size];
        Integer i, j;
        for(i=0;i<size;i++)
            for(j=0;j<size;j++)
                visited[i][j] = 0;
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
        Integer i, j, curCount, max, prev, curVal;
        ArrayList<Point> points = new ArrayList<>();
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

        //ToDo: remove duplicates
        return points;
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
        ArrayList<Point> points = new ArrayList<>();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            points.add((Point)entry.getKey());
        }
        return points;
    }

    public void alphaBeta(Input input, Integer depth, Integer alpha, Integer beta){
        ArrayList<Point> children = getBranchPoints(input.board, input.size);
        HashMap<Point, Integer> scoreInfo = new HashMap<>();
        Integer i, j, k, s, l = children.size();
        Integer [][] newBoard;
        for(i=0;i<l;i++){
            s = eval(input.board, input.size);
            scoreInfo.put(children.get(i), s);
        }

        children = sortMap(scoreInfo); // ordered children with their eval value

        for(i=0;i<l;i++){
            newBoard = deepcopy(input.board, input.size);
            removeFruit(newBoard, children.get(i), input.size);
        }

    }

}
