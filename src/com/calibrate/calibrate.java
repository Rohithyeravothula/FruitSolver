package com.calibrate;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import java.util.*;


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

class FruitSolver {

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

    public Integer fruitCount(Integer [][] board, Integer size){
        Integer i, j, c=0;
        Integer [] visited = new Integer[10];

        for(i=0;i<10;i++)
            visited[i] = 0;

        for(i=0;i<size;i++)
            for(j=0;j<size;j++)
                if(board[i][j] != -1)
                    visited[board[i][j]] = 1;

        for(i=0;i<10;i++)
            if(visited[i] != 0)
                c++;
        return c;
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

    public Integer utility(Integer [][] board, Integer size, Boolean maxTurn) {
        Integer[][] visited = new Integer[size][size];
        Integer i, j, max1C=0, max2C=0, current;

        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++)
                visited[i][j] = 0;

        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                if(board[i][j] != emptyFruit){
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

    public PointScore greedyNode(Integer [][] board, Integer size, Integer fruits){
        Integer i, j, current, maxC=0, maxI, maxJ;
        PointScore fn = fastNode(board, size, fruits);
        maxI = fn.point.x;
        maxJ = fn.point.y;
        Integer [][] visited = new Integer[size][size];
        for(i=0;i<size;i++)
            for(j=0;j<size;j++)
                visited[i][j] = 0;
        for(i=0;i<size;i++)
        {
            for(j=0;j<size;j++){
                if(board[i][j] != emptyFruit){
                    current = pointValRecr(board, visited, size, new Point(i, j), board[i][j]);
                    if(current > maxC){
                        maxC = current;
                        maxI = i;
                        maxJ = j;
                    }
                }
            }
        }
        return new PointScore(maxC*maxC, new Point(maxI, maxJ));
    }

    //returns any non * node which it first encounters while searching from bottem
    public PointScore fastNode(Integer [][] board, Integer size, Integer fruits){
        Integer i, j;
        //check from back, since beginning will be usually *
        for(i=size-1;i>=0;i--)
            for(j=size-1;j>=0;j--)
                if(board[i][j] != -1)
                    return new PointScore(0, new Point(i,j));

        return null;
    }

    public PointScore minNode(Integer [][] board, Integer size, Integer fruits, Integer depth, Integer curScore, Integer alpha, Integer beta){
        if(depth == 0)
            return new PointScore(curScore+utility(board, size, false), null);

        ArrayList<Point> children = getChildren(board, size);
        Integer i, s, cCount = children.size(), bestScore = Integer.MAX_VALUE;
        PointScore v = new PointScore(Integer.MAX_VALUE, null);
        Integer [][] newBoard;
        Point point;

        if(terminalCondition(board, size, children)){
            if(children.size() == 0)
                return new PointScore(curScore, null);
            return new PointScore(curScore + utility(board, size, false), children.get(0));
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
            return new PointScore(curScore+ utility(board,size, true), null);

        ArrayList<Point> children = getChildren(board, size);
        Integer i, s, cCount = children.size(), bestScore = Integer.MIN_VALUE;
        PointScore v = new PointScore(Integer.MIN_VALUE, null);
        Integer [][] newBoard;
        Point point;

//        System.out.println(children);
        if(terminalCondition(board, size, children)){
            if(children.size() == 0)
                return new PointScore(curScore, null);
            return new PointScore(curScore + utility(board, size, true), children.get(0));
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
        Integer [][] newBoard = deepcopy(input.board, input.size);
        // change the fruit count, as it will give max fruits only
        input.fruits = fruitCount(input.board, input.size);
        PointScore p = maxNode(input.board, input.size, input.fruits, depth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        System.out.println("score: " + pointVal(newBoard, input.size, p.point) + " " + p.point + " value: " + input.board[p.point.x][p.point.y]);
        return p.point;
    }

    //ToDo: add time check, return random in case of any unforeseen failures
    public static void main(String [] args){
        FruitSolver fs = new FruitSolver();
        Input input = fs.readInput();
        Point ans = fs.alpha_beta(input, 1);
//        System.out.println(ans);
        try{
            fs.writeOutput(ans, input.board, input.size);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}

public class calibrate {

    String input10_2 = "10\n2\n12\n0021001000\n0012220101\n1000121001\n0111001101\n0010011121\n1011011001\n1110112021\n0111112010\n1112101000\n1000022221";
    String input10_6 = "10\n6\n12\n1312202011\n0212101424\n1201302110\n0310200200\n1221204322\n1110221101\n1100052221\n2150213034\n0412041034\n5260226542";
    String input10_9 = "10\n9\n300\n3122018970\n7012151612\n0151402525\n0206414016\n2281901200\n0191002430\n1522510132\n2112006805\n1280221288\n3211322502";

    String input15_2 = "15\n2\n12\n101011200111101\n112020111201101\n000201211010001\n221100100110000\n001000012201012\n011110001100121\n002010001102010\n200101011102111\n101111000211011\n010011112100010\n110010100011200\n001201121111101\n101001002110011\n210111011210110\n000100111011010";
    String input15_6 = "15\n6\n12\n221010004011102\n211102141020201\n322300052012003\n322021210121220\n312010114060162\n530212201325210\n122001414021015\n121064126515211\n003011230113510\n251100014132510\n202221212221112\n010051100205111\n241201100261021\n121010322212354\n005102121011202";
    String input15_9 = "15\n9\n12\n520371260563991\n282323721252214\n163386703111172\n322530114631107\n231030121300203\n012711163031113\n200334321321343\n192002254123123\n026775215330034\n991132802653310\n290107011481302\n201101229192163\n320210122393313\n122063276738800\n321131432439805";

    String input20_2 = "20\n2\n12\n10000101010200002001\n11011101200200010101\n10002102102011110011\n10002110121020101201\n11011000102101000000\n22210000110101001000\n00011110001200010010\n12010101012112111001\n20010111000011010012\n20211200001011111010\n01100201120021010210\n12011000001120120010\n10100121110110010010\n10201010001001011111\n10021001021011110110\n10001000010011110112\n11012102120010201110\n00001000021001001121\n11211121120122110210\n20000000220010001110";
    String input20_6 = "20\n6\n12\n21601000115614110111\n22122223012611061402\n60024113022660205240\n22622201222123001400\n20221550501111516321\n52201100226241260040\n61204122510020120502\n12220522012040110000\n34210121222212001120\n02222401605602412220\n22422612022101135201\n14111123111510110002\n41430132222212205211\n21255205212023100226\n01456301120600125041\n03151002102211210141\n12322200362114201122\n13102220312112001010\n22114112400121220133\n16221212252062145116";
    String input20_9 = "20\n9\n12\n44016135122231363811\n33232189600326123619\n20220014022223037112\n31232281115239303032\n43222220015831122222\n21191200731028028225\n53187032131302262010\n01200932920202220223\n68071820330031326221\n10325232063038013020\n82303002203330319253\n23033000808123310120\n17513863600610372100\n32131213251103010021\n02525170102105200253\n01238103012112302220\n63235611210161711133\n31213312113208000707\n31010003380520880103\n21250002518023741001";

    String input26_2 = "26\n2\n12\n12110100120000101101111001\n10212020001100012011000000\n00101211110100001001221112\n00011210000211020020000001\n11100110001011100001010012\n10012000002011021022002011\n10100100211100011210201001\n11001100111101001010010020\n11220011100200211012100210\n00011002110100211200011010\n02112100110001011001111111\n11202110112101102110100010\n00001101010210210021101110\n11100201201000000112101102\n10210010102111101110012001\n10121001000201000210110210\n00000101001211210002111011\n22112220101000221001100000\n20101112000001110111010110\n21100000010100100000000010\n00100001021201001101111010\n01001011001120122001100111\n01001111100102101100101111\n10111011020010210111021101\n12020002100111121100201001\n11111111111100011200100000";
    String input26_6 = "26\n6\n12\n61001202000402612102212251\n62014263510141206020420121\n02016012114211000011126214\n11206211010631060311320223\n25051405320005232104204030\n50004000221203101523523112\n22110250011021160244214125\n61120026112322041214260000\n00302201400220422110321316\n22046401104203022010121210\n11523211360060100200022225\n00040103222252104410016011\n24103201100120510450202261\n14022164000510202420115020\n16422001115000025612002110\n21013212100011221245005102\n21010211225220151012111002\n00520110000421502621222262\n24110110116201620111152651\n61144200224012121120065121\n12261140012206322212110600\n12116140161121022043032210\n21002010022122116204511222\n50426202021251400120001321\n12012215224022222011022020\n52300102514220510324605121";
    String input26_9 = "26\n9\n12\n23139280071300215171516101\n32300342004025011470852110\n11312201371607113271122213\n11336121302411250311364011\n32103110136630003111979322\n92032232723903200005822301\n03343083233402311310023000\n40020133208122232083632226\n11222100330103202139330225\n21533829312316032156418215\n10513010111113213230213026\n13390310018139970333033942\n66332711333133320080801920\n02192002003403550181033225\n26776132210018121110362260\n01521035311360918750231213\n12312022211761292009920601\n10120601623313221200322690\n33223020159001113013711312\n11312013021050133840415200\n24523103112420632327023732\n11638224301335524333615312\n32160600161031411360703000\n59318613762010210010339190\n00911324553328211224021030\n09620391672333236230003394";


    ArrayList<String> inputs = new ArrayList<String>(Arrays.asList(input10_2, input10_6, input10_9, input15_2, input15_6, input15_9, input20_2, input20_6, input20_9, input26_2, input26_6, input26_9));

    ArrayList<String> information = new ArrayList<>();
    Long timeLimit = 285L;

    public Input constructInput(String input) {
        List<String> data;
        String cur, val;
        Integer size, fruits, j, i;
        Integer[][] board;
        Double time;
        data = new ArrayList<>(Arrays.asList(input.split("\n")));
        size = Integer.parseInt(data.remove(0));
        fruits = Integer.parseInt(data.remove(0));
        time = Double.parseDouble(data.remove(0));
        board = new Integer[size][size];
        for (i = 0; i < size; i++) {
            cur = data.remove(0);
            for (j = 0; j < size; j++) {
                val = String.valueOf(cur.charAt(j));
                if (val.equals("*"))
                    board[i][j] = -1;
                else {
                    board[i][j] = Integer.parseInt(val);
                }
            }
        }
        return new Input(board, size, fruits, time);
    }

    public Integer fruitCount(Integer [][] board, Integer size){
        Integer i, j, c=0;
        Integer [] visited = new Integer[10];
        for(i=0;i<size;i++)
            for(j=0;j<size;j++)
                if(board[i][j] != -1)
                    visited[board[i][j]] = 1;

        for(i=0;i<10;i++)
            if(visited[i] != 0)
                c++;
        return c;
    }

    public void writeBenchmarks(ArrayList<String> information) throws IOException {
        String curDir = System.getProperty("user.dir");
        FileWriter writer = new FileWriter(curDir + "/calibration.txt");
        StringBuilder infoString = new StringBuilder();
        for(String s: information){
            infoString.append(s + "\n");
        }
        writer.write(infoString.toString());
        writer.close();
    }

    public ArrayList<String> getBenchmarks() {
        Integer depth;

        for(String strInput: inputs){
            depth = 1;
            while(depth < 5){
                Input input = constructInput(strInput);
                FruitSolver fs = new FruitSolver();
                if(input.size < 26 || input.size >20 && depth < 4){
//                    System.out.println(input.size + ", " + input.fruits + ", " + depth);
                    Long startTime = System.currentTimeMillis();
                    Point p = fs.alpha_beta(input, depth);
                    Long end =  (System.currentTimeMillis() - startTime)/1000 + 1;
                    String result = input.size + "," + input.fruits + "," + depth.toString() + "," + end.toString();
//                    System.out.println(result);
                    information.add(result);
                }
                depth++;
            }
        }
        return information;
    }

    public static void main(String[] args) throws Exception {
        calibrate cb = new calibrate();
        try{
            calibrate.runTimeout(cb::getBenchmarks, cb.timeLimit);
        } catch (Exception e){
            e.printStackTrace();
        }

        try{
            cb.writeBenchmarks(cb.information);
            System.exit(0);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void runTimeout(final Runnable runnable, long timeout) throws Exception {
        timeout(() -> {
            runnable.run();
            return null;
        }, timeout);

    }

    public static <T> T timeout(Callable<T> callable, long time) throws Exception {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Future<T> future = executorService.submit(callable);
        try{
            return future.get(time, TimeUnit.SECONDS);
        } catch (TimeoutException te){
            future.cancel(true);
            executorService.shutdown();
            throw te;
        } catch (ExecutionException e){
            e.printStackTrace();
            throw e;
        }
    }
}
