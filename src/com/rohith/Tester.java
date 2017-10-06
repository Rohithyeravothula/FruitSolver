package com.rohith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tester {
    FruitSolver fs = new FruitSolver();

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
            rawData = Files.lines(Paths.get(curDir + "/data/test.txt"));
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

    public void test_getBranchPoints(){
        Input input = fs.readInput();
        ArrayList<Point> points = fs.getBranchPoints(input.board, input.size);
        Integer i;
        System.out.println(points);
    }

    public static void main(String[] args){
        Tester t = new Tester();
        t.test_getBranchPoints();
//        t.test_pickVal();
//        t.test_fruit_remover();
    }
}
