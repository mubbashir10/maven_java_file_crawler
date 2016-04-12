package com.mubbashir10.spider;

//importing required libraries
import java.util.*;
import java.io.*;
import java.io.File;
import java.sql.*;	

//class for frame and threads
public class SmartCrawler extends Thread{

    //constructor
    String rootName;
    String threadName;
    int i = 0;
    static DBConfig db = new DBConfig("FileIndex","root","ralf19");

    //constructor
    SmartCrawler(String root){

        this.rootName = root;
    }

    //DBConnection Test
    public boolean DBStatus(String table, String usr, String pwd){

        DBConfig tmpDB = new DBConfig(table, usr, pwd);
        if(tmpDB!=null)
            return true;
        else
            return false;
    }

    //extension method
    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int extensionPos = filename.lastIndexOf('.');
        int lastUnixPos = filename.lastIndexOf('/');
        int lastWindowsPos = filename.lastIndexOf('\\');
        int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);

        int index = lastSeparator > extensionPos ? -1 : extensionPos;
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    //overriding run method
    public void run(){

        //thread name
        System.out.println(this.getName());

        //making file object
        File dir = new File(this.rootName);
        if (dir.exists()){

            //File array
            File[] list = dir.listFiles();

            //empty directory
            if (list == null){
                System.out.println("The entered directory is empty!");
            }   

            //walking
            for ( File f : list ) {


                //directory
                if ( f.isDirectory() ){
                    //crawling
                    SmartCrawler t = new SmartCrawler(f.getAbsolutePath());
                    t.start();
                    try{
                        t.join();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    //updating DB
                    try{
                    
                        Statement statement = db.connection.createStatement();
                        String query = "INSERT INTO data VALUES ('"+i+"','"+f.getName()+"','"+f.getAbsolutePath()+"','"+f.listFiles().length+"', 'N/A','"+f.length()+"')";
                        statement.executeUpdate(query);
                    }
                    catch(SQLException exception){

                        exception.printStackTrace();
                    }
                }
                //file
                else {

                    //updating DB
                    try{
                    
                        Statement statement = db.connection.createStatement();
                        String query = "INSERT INTO data VALUES ('"+i+"','"+f.getName()+"','"+f.getAbsolutePath()+"','0','N/A','"+f.length()+"')";
                        statement.executeUpdate(query);
                    }
                    catch(SQLException exception){

                        exception.printStackTrace();
                    }
                    

                    //if text file is found
                    if(getExtension(f.getName()).equals("txt")){

                        //reading text file
                        try{

                            //reading file
                            String content = new Scanner(f).useDelimiter("\\Z").next();

                            //making array
                            String[] tmpData = content.split(" ");

                            //traversing array
                            for (String s: tmpData) {   
                                //updating DB
                                try{
                                
                                    Statement statement = db.connection.createStatement();
                                    String query = "INSERT INTO data VALUES ('"+i+"','"+s+"','"+f.getAbsolutePath()+"','0','"+content+"','"+f.length()+"')";
                                    statement.executeUpdate(query);
                                }
                                catch(SQLException exception){

                                    exception.printStackTrace();
                                }
                            }
                        }
                        catch(Exception e){

                        }                           
                    }
                }
            }   
        }
        else{
            System.out.println("Directory doesn't exist!");
            System.exit(0);
        }
        i++;
    }


	//main method
	public static void main(String[] args){

        //creating objects
        Scanner input = new Scanner(System.in);
        System.out.println("Enter directory name:");
        String dirName = input.nextLine();
        SmartCrawler sc = new SmartCrawler(dirName);
        sc.start();

        try{
            
            sc.join();
            System.out.println("Indexing is completed.\nEnter the key for which you would like to search");
            String key = input.nextLine();

            Statement statement = db.connection.createStatement();
            String query = "Select * from data where keyterm like '%"+key+"%'";
            ResultSet rs = statement.executeQuery(query);

            //printing results
            while (rs.next()){
                
                String keyname = rs.getString(2);
                String path = rs.getString(3);
                String child = rs.getString(4);
                String content = rs.getString(5);
                Double size = rs.getDouble(6);
                System.out.println("Key Name: "+keyname+"\nPath: "+path+"\nContent: "+content+"\nSub Items: "+child+"\nSize: "+size+"bytes\n");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        

	}//end main

}//end Crawler class

