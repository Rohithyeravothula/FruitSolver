package com.rohith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tester {
    homework fs = new homework();

    public ArrayList<Input> readTestInput(){
        ArrayList<Input> allInputs = new ArrayList<>();
        Stream<String> rawData;
        List<String> data;
        String cur;
        Integer size, fruits, j, i;
        Integer [][] board;
        Double time;
        try{
            String curDir = System.getProperty("user.dir");
            rawData = Files.lines(Paths.get(curDir + "/data/input.txt"));
            data = rawData.collect(Collectors.toList());
            while(!data.isEmpty()){
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
                allInputs.add(new Input(board, size, fruits, time));
                data.remove(0); // remove space between two test cases
            }
            return allInputs;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
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

    public void main_test(){
        ArrayList<Input> inputs = readTestInput();
        while (!inputs.isEmpty()){
            Input input = inputs.remove(0);
            fs.printBoard(input.board, input.size);
            fs.removeFruitRecr(input.board, new Point(0,0), 1, input.size);
            fs.printBoard(input.board, input.size);
        }
    }

    public void test_fruit_remover(){
        Input input = fs.readInput();
        fs.printBoard(input.board, input.size);
        System.out.println();
        fs.removeFruitRecr(input.board, new Point(9,3), 0, input.size);
        fs.printBoard(input.board, input.size);
    }

    public void test_pickVal(){
        Input input = fs.readInput();
        Integer ans, i, j;
        for(i=0;i<input.size;i++){
            for(j=0;j<input.size;j++){
                ans = fs.pointVal(input.board, input.size, new Point(i, j));
                System.out.println("(" + i.toString() + " " + j.toString()+ ") value is " + ans.toString());
            }
        }
    }

    public void test_getChildren(){
        Input input = fs.readInput();
        ArrayList<Point> points = fs.getChildren(input.board, input.size);
        Integer i;
        System.out.println(points);
    }

    public void test_gravity(){
        Input input = fs.readInput();
        fs.gravity(input.board, input.size);
        fs.printBoard(input.board, input.size);
    }

    // not properly written
    @Deprecated
    public void test_checkIfConnected(){
        Input input = fs.readInput();
        Point p1 = new Point(3,4), p2 = new Point(4,4);
        Boolean ans = fs.checkIfConnected(input.board, input.size, p1, p2);
        System.out.println(ans.toString() + " " + p1.toString() + " " + p2.toString());
    }

    public void time_est_checkIfConnected(){
        Input input = fs.readInput();
        Long start = System.currentTimeMillis();
        Integer i1,j1, i2, j2,c=0;
        Random ran = new Random();
        while(c<100000000){
            c+=1;
            i1=ran.nextInt(input.size);
            j1=ran.nextInt(input.size);
            i2=ran.nextInt(input.size);
            j2=ran.nextInt(input.size);
            fs.checkIfConnected(input.board, input.size, new Point(i1, j1), new Point(i2, j2));
        }
        System.out.println("total : " + (System.currentTimeMillis() - start)/1000.0);
    }

    public void test_alpha_beta(){
        Input input = fs.readInput();
        Long start = System.currentTimeMillis();
        Point p = fs.alpha_beta(input, 6);
        System.out.println("total time " + (System.currentTimeMillis() - start)/1000.0);
//        System.out.println(p);
    }

    public void test_zeroboard(){
        int board[][] = new int[4][4];
//        System.out.println(board[0]);
//        fs.printBoard(board, 4);
    }

    public void test_utility(){
        Input input = fs.readInput();
        Integer ans = fs.utility(input.board, input.size, true);
        System.out.println(ans);
    }

    public void test_fastNode(){
        Input input = fs.readInput();
        PointScore p =  fs.fastNode(input.board, input.size, input.fruits);
        System.out.println(p);
    }

    public void test_greedyNode(){
        Input input = fs.readInput();
        PointScore p = fs.greedyNode(input.board, input.size, input.fruits);
        System.out.println(p);
    }

    public void test_readBenchmarks() throws IOException {
        ArrayList<ArrayList<Integer>> info = fs.readBenchmarks();
        for(ArrayList<Integer> i: info){
            System.out.println(i.toString());
        }
    }

    public void test_getBenchmark(){
        ArrayList<ArrayList<Integer>> information = fs.readBenchmarks();
        System.out.println(information.size());
        Integer i,j,k;
//        for(i=1;i<26;i++)
//            for(j=1;j<10;j++)
//                for(k=1;k<10;k++)
//                    System.out.println("size: " + i + " fruits: " + j + " time: " + k + " depth: " + fs.getBenchmark(i, j, k*1.0,information));

//        System.out.println(fs.getBenchmark(13,9,120.1,information));
    }

    public static void main(String[] args) throws IOException {
        Tester t = new Tester();
        t.test_getBenchmark();
//        t.test_readBenchmarks();
//        t.test_utility();
//        t.time_est_checkIfConnected();
//        t.test_getChildren();
//        t.test_alpha_beta();
//        t.test_gravity();
//        t.test_checkIfConnected();
//        t.test_alpha_beta();
//        t.test_pickVal();
//        t.test_fruit_remover();
//        t.test_zeroboard();
//        t.test_fastNode();
//        t.test_greedyNode();
    }
}