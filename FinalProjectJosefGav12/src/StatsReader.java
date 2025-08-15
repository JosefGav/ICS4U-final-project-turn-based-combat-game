
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * For reading files from characters_info folder as well as files in general
 * Josef gav 2024/2025
 * */

public class StatsReader {
    public  JSONObject readJSON (String fileName) {
    	String jsonString = getGenericFileContents("characters_info/"+fileName);

        // Convert string to JSONObject
        JSONObject stats = new JSONObject(jsonString);
        
        return stats;
    }
    
    /**
     * returns a list of character json file names
     * */
    public String[] getCharacterList() {
    	ArrayList<String> contents = new ArrayList<String>();
   
    	try {
    		BufferedReader levelData = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("characters_info/character_list.csv")));
            String line;
    		while ((line = levelData.readLine()) != null) {
    			String[] temp = line.split(",");
    			for (String token:temp) {
    				String trimed = token.trim();
    				if (trimed != "boss.json") contents.add(trimed);
    			}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
         
        
        String[] contentsArr = contents.toArray(new String[contents.size()]);
        
        
        return contentsArr;
    }
//	  testing
//    public static void main(String[] args) {
//    	StatsReader reader = new StatsReader();
//    	reader.getCharacterList();
//    	
//    	printArr(reader.returnMapGrid());
//    }
    
    /**
     * returns the contents of a text file (no newlines)
     * */
    public String getGenericFileContents(String filePath) {
    	StringBuffer str = new StringBuffer();
   
    	
    	try {
    		BufferedReader levelData = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filePath)));
            String line;
    		while ((line = levelData.readLine()) != null) {
    			str.append(line); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return str.toString();
    }
    	
	

    /**
     * returns an integer array of (1,2,0) representing different tile types
     * */
    public  int[][] returnMapGrid() {
    	int rows = 24;
    	int cols = 24;
    	int j = 0;
    	
    	int[][] map = new int[24][24];

        
        try {
    		BufferedReader levelData = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("maps/default.csv")));
            String line;
    		while ((line = levelData.readLine()) != null) {
            	String[] temp = line.split(",");
            	
            	for (int i = 0; i < map[j].length; i++) {
                	map[j][i] = Integer.parseInt(temp[i].replace(",", "").trim());
            	}
            	
            	j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
       
        System.out.println(rows == j);
        
        return map;
        
    }
    
    public static void printArr(int[][] arr) {
    	for (int r = 0; r< arr.length; r++) {
    		for(int c  = 0; c < arr[r].length; c++) {
    			System.out.print(arr[r][c]+", ");
    		}
    		System.out.println();
    	}
    }
}
