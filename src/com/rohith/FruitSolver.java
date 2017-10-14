package com.rohith;

import java.io.FileWriter;
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
        this.score = score; // this in a state will be a difference in scores of min and max player
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
        if(point == null && score == null)
            return "";
        if(point == null)
            return "score: " + score.toString() + " ()";
        if(score == null)
            return "( " + point.toString() + " )";
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

    static Integer emptyFruit = -1;
    static Integer children_size_threshold = 5;


    public Input readInput(){
        Stream<String> rawData;
        List<String> data;
        String cur, val;
        Integer size, fruits, j, i;
        Integer [][] board;
        Double time;
        try{
            String curDir = System.getProperty("user.dir");
            rawData = Files.lines(Paths.get(curDir + "/data/input.txt"));
            data = rawData.collect(Collectors.toList());
            size = Integer.parseInt(data.remove(0));
            fruits = Integer.parseInt(data.remove(0));
            time = Double.parseDouble(data.remove(0));
            board = new Integer[size][size];
            for(i = 0; i<size; i++){
                cur = data.remove(0);
                for(j=0; j<size; j++){
                    val = String.valueOf(cur.charAt(j));
                    if(val.equals("*"))
                        board[i][j] = -1;
                    else{
                        board[i][j] = Integer.parseInt(val);
                    }
                }
            }
            return new Input(board, size, fruits, time);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void writeOutput(Point p, Integer [][] board, Integer size) throws IOException {
        removeFruit(board, p, size);
        p.x++; //check: to convert this to 1 based index
        String firstLine = String.valueOf((char)((int)'A' + p.y)) + p.x.toString() + "\n";
        String curDir = System.getProperty("user.dir");
        FileWriter writer = new FileWriter(curDir + "/data/output.txt");
//        System.out.println(firstLine);
        Integer i, j;
        StringBuilder boardString = new StringBuilder(firstLine);
        for(i=0;i<size;i++){
            StringBuilder cur = new StringBuilder("");
            for(j=0;j<size;j++){
                if(board[i][j] == -1)
                    cur.append("*");
                else
                    cur.append(String.valueOf(board[i][j]));
            }
            cur.append("\n");
            boardString.append(cur);
        }
        boardString.setLength(boardString.length() - 1);
        writer.write(boardString.toString());
        writer.close();
    }

    public void printBoard(Integer [][] board, Integer size){
        Integer i, j;
        for(i=0;i<size;i++){
            for(j=0;j<size;j++){
                if(board[i][j] == -1)
                    System.out.print("*");
                else
                    System.out.print(board[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
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

    // applies gravity, has side effect
    public void gravity(Integer [][] board, Integer size){
        Integer i, j, k=0;
        Integer [] valid = new Integer[size];
        for(j=0;j<size;j++){
            //for each row
            for(i=0;i<size;i++){
                if(board[i][j] != emptyFruit){
                    valid[k] = board[i][j];
                    k++;
                }
            }
            i=size-1;
            while(i>=0 && k>0){
                k--;
                board[i][j] = valid[k];
                i--;
            }
            while(i>=0){
                board[i][j] = emptyFruit;
                i--;
            }
        }
    }

    // has side effect
    public void removeFruit(Integer [][] board, Point point, Integer size){
        removeFruitRecr(board, point, board[point.x][point.y], size);
        gravity(board, size);
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

//        System.out.println(p1);

        if(p1.x == p2.x && p1.y == p2.y)
            return true;

        Integer x = p1.x, y = p1.y;
        if(x >= 0 && x < size && y >= 0 && y < size && visited[p1.x][p1.y] == 0){
            if(board[p1.x][p1.y] != board[p2.x][p2.y])
                return false;

            visited[x][y] = 1;

            return  checkIfConnectedPoints(board, visited, size, new Point(x+1, y), p2) ||
                    checkIfConnectedPoints(board, visited, size, new Point(x, y+1), p2) ||
                    checkIfConnectedPoints(board, visited, size, new Point(x-1, y), p2) ||
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

    public boolean checkIfEnd(Integer [][] board, Integer size){
        Integer i, j;
        for(i=size-1;i>=0;i--){
            for(j=size-1;j>=0;j--){
                if(board[i][j] != -1){
                    return true;
                }
            }
        }
        return false;
    }

    //has no side effects on board
    public Integer pointVal(Integer [][] board, Integer size, Point point){
        Integer [][] visited = zeroBoard(size);
        Integer f;
        if(board[point.x][point.y] == -1)
            return 0;
        f = pointValRecr(board, visited, size, point, board[point.x][point.y]);
        return f*f;
    }


    //function to assign value to a given board, doesn't have side effect
    public Integer boardValue(Integer [][] board, Integer size){
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

    public Integer utility(Integer [][] board, Integer size, Boolean maxTurn) {
        Integer[][] visited = new Integer[size][size];
        Integer i, j, max1C=0, max2C=0, current;

        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++)
                visited[i][j] = 0;

        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                if(board[i][j] != -1){
                    current = pointValRecr(board, visited, size, new Point(i, j), board[i][j]);
                    if (current > max1C) {
                        max2C = max1C;
                        max1C = current;
                    } else if (current <= max1C && current > max2C)
                        max2C = current;
                }
            }
        }

        if (maxTurn) {
            return max1C * max1C - max2C * max2C;
        } else {
            return max2C * max2C - max1C * max1C;
        }
    }

    public Integer utility(Integer [][] board, Integer size, Integer fruits, Boolean maxTurn){
        Integer [] counts = new Integer[10];
        Integer i, j;
        for(i=0;i<10;i++)
            counts[i]=0;

        for(i=0;i<size;i++)
            for(j=0;j<size;j++)
                if(board[i][j] != emptyFruit){
                    counts[board[i][j]] += 1;
                }

//        System.out.println(Arrays.toString(counts));

        Arrays.sort(counts, Collections.reverseOrder());
        if(maxTurn){
            return (counts[0]*counts[0]) - (counts[1] * counts[1]);
        }
        else{
            return (counts[1] * counts[1] ) - (counts[0] * counts[0]);
        }
        //ToDo: discuss the possible outcomes for this implementaiton
    }

    public ArrayList<Point> getChildren(Integer board[][], Integer size){

        Integer i, j, curCount, prev, l;
        ArrayList<Point> points = new ArrayList<>();

        // return all points if size is less then threshold
        if(size < children_size_threshold){
            for(i=0;i<size;i++)
                for(j=0;j<size;j++)
                    if(board[i][j] != -1)
                    points.add(new Point(i, j));
        }

        else{

            HashMap<Point, Integer> pointVal;
            //row wise max

            for(i=0;i<size;i++){
                j=1;
                prev = board[i][j-1];
                pointVal = new HashMap<>();
                curCount=1;
                while(j<size){
                    if(board[i][j] != prev){
                        if(board[i][j-1] != -1) // ensure -1 is not added
                            pointVal.put(new Point(i, j-1), curCount);
                        curCount = 0;
                        prev = board[i][j];
                    }
                    j++;
                    curCount++;
                }
                if(board[i][j-1] != -1) // ensure -1 is not added
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
                        if(board[j-1][i] != -1) // ensure -1 is not added
                            pointVal.put(new Point(j-1, i), curCount);
                        curCount = 0;
                        prev = board[j][i];
                    }
                    j++;
                    curCount++;
                }
                if(board[j-1][i] != -1) // ensure -1 is not added
                    pointVal.put(new Point(j-1, i), curCount);
                points.addAll(takeTop(pointVal));
            }
        }

//        System.out.println(points);
//        for(Point p: points){
//            System.out.println(p.toString() + board[p.x][p.y].toString());
//        }

        return removeDuplicates(points, board, size);
    }

    public ArrayList<Point> removeDuplicates(ArrayList<Point> points, Integer [][] board, Integer size){
//        System.out.println(points);
        Integer i, j, l=points.size();
        Point p1, p2;
        ArrayList<Point> uniquePoints = new ArrayList<>();
        for(i=0;i<l;i++){
            Boolean con = true;
            for(j=i+1;j<l;j++){
                p1 = points.get(i);
                p2 = points.get(j);
                if(p1.x == p2.x && p1.y == p2.y){
                    con = false;
//                    System.out.println(p1.toString() +  p2.toString());
                    break;
                }
            }
            if(con)
            uniquePoints.add(points.get(i));
        }
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
        if(list.size() > 2)
            return new ArrayList<>(list.subList(0, 2)); //taking top 2
        else
            return list;
    }

    public boolean terminalCondition(Integer [][] board, Integer size, ArrayList<Point> children){
        Integer [][] newBoard = deepcopy(board, size);
        if(children.size() <= 1)
            return true;

        removeFruit(newBoard, children.get(0), size);
        if(!checkIfEnd(newBoard, size))
            return true;

        return false;
    }

    public PointScore minNode(Integer [][] board, Integer size, Integer fruits, Integer depth, Integer curScore, Integer alpha, Integer beta){
        if(depth == 0)
            return new PointScore(curScore+utility(board, size, fruits, false), null);

        ArrayList<Point> children = getChildren(board, size);
        Integer i, s, cCount = children.size(), bestScore = Integer.MAX_VALUE;
        PointScore v = new PointScore(Integer.MAX_VALUE, null);
        Integer [][] newBoard;
        Point point;

        if(terminalCondition(board, size, children)){
            if(children.size() == 0)
                return new PointScore(curScore, null);
            return new PointScore(curScore + utility(board, size, fruits, false), children.get(0));
        }

        for(i=0;i<cCount;i++){
            point = children.get(i);
            s = pointVal(board, size, point);
            newBoard = deepcopy(board, size);
            removeFruit(newBoard, point, size);

            v = PointScore.min(v, maxNode(newBoard, size, fruits, depth-1, curScore-s, alpha, beta));
            if(v.score < bestScore){
                bestScore = v.score;
                v.point = point;
            }

            if(v.score <= alpha){
//                System.out.println("pruned");
                return v;
            }
            beta = Integer.min(v.score, beta);
        }
        return v;
    }

    public PointScore maxNode(Integer [][] board, Integer size, Integer fruits, Integer depth, Integer curScore, Integer alpha, Integer beta){
        // terminal check may include no branches from here on
        if(depth == 0)
            return new PointScore(curScore+ utility(board,size, fruits, true), null);

        ArrayList<Point> children = getChildren(board, size);
        Integer i, s, cCount = children.size(), bestScore = Integer.MIN_VALUE;
        PointScore v = new PointScore(Integer.MIN_VALUE, null);
        Integer [][] newBoard;
        Point point;

//        System.out.println(children);
        if(terminalCondition(board, size, children)){
            if(children.size() == 0)
                return new PointScore(curScore, null);
            return new PointScore(curScore + utility(board, size, fruits, true), children.get(0));
        }

        for(i=0;i<cCount;i++){
            point = children.get(i);
            s = pointVal(board, size, point); // current move score
            newBoard = deepcopy(board, size);
            removeFruit(newBoard, point, size);

            // check if terminal condition is reached
            v = PointScore.max(v, minNode(newBoard, size, fruits, depth-1, curScore+s, alpha, beta));

            // to ensure the point is copied in case of yielding best score
            if(v.score > bestScore){
               bestScore = v.score;
               v.point = point;
            }

            if(v.score >= beta){
//                System.out.println("pruned");
                return v;
            }
            alpha = Integer.max(alpha, v.score);


        }
        return v;
    }

    public Point alpha_beta(Input input, Integer depth){
        PointScore p = maxNode(input.board, input.size, input.fruits, depth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        System.out.println(p);
//        System.out.println(pointVal(input.board, input.size, p.point));
        return p.point;
    }

    public Point alpha_beta_iterative(Input input){
//        Integer depth = 2, depthLimit = 2; //loop will never be true
        Point p = alpha_beta(input, 3);
        System.out.println("point " + p.toString() + " score: " + pointVal(input.board, input.size, p));
//        while(depth < depthLimit){
//            p = alpha_beta(input, depth);
////            printBoard(input.board, input.size);
//            System.out.println("depth " + depth.toString() + " " + p.toString());
//            depth++;
//        }
        return p;
    }


    //ToDo: add time check, return random in case of any unforeseen failures
    public static void main(String [] args){
        FruitSolver fs = new FruitSolver();
        Input input = fs.readInput();
        Point ans = fs.alpha_beta_iterative(input);
        System.out.println(input.board[ans.x][ans.y]);
        try{
            fs.writeOutput(ans, input.board, input.size);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}

//ToDo: check for small test cases
//ToDo: discuss and implement iterative deepening
//ToDo: calibrate script
//ToDo: rule based mining for depth value
//ToDo: use slots/fruits ratio information
