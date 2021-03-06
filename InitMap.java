package peekaboo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

//地圖配置

public class InitMap {

    public String fileName = "MAPS/map.txt";
    public ArrayList<String> list = new ArrayList<String>();
    public int[][] map = null;

    public InitMap() {
    }

    public InitMap(String mapFile) {
        this.fileName = mapFile;
    }

    public int[][] readMap() throws Exception {// return 2D-array

        // 構造文件輸入流
        FileInputStream file = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(file);
        BufferedReader br = new BufferedReader(isr);

        String value = br.readLine();// 讀取一行數據
        while (value != null) {// 還有內容
            list.add(value);// 將讀取到的一行數據加入到容器中
            value = br.readLine();
        }
        br.close();

        int row = list.size();// 得到多少行多少列
        int cloum = 0;
        for (int i = 0; i < 1; i++) {
            String str = list.get(i);// list[i]
            String[] values = str.split(",");
            cloum = values.length;// 1 row 幾個 elements
        }
        map = new int[row][cloum];
        // 將讀到的字符創轉換成整數，並賦值給二位數組map
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            String[] values = str.split(",");
            for (int j = 0; j < values.length; j++) {
                map[i][j] = Integer.parseInt(values[j]);
            }
        }
        return map;
    }
}
