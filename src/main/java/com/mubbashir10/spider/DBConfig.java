package com.mubbashir10.spider;

//packages/libraries
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConfig{
	
	//declaring variables
	Connection connection = null;

	//constructor
	DBConfig(String dbname, String usr, String pwd){

		//loading driver
		try{
			Class.forName("com.mysql.jdbc.Driver");
		} 
		catch (ClassNotFoundException e){
			System.out.println("JDBC Driver not found!");
			e.printStackTrace();
			return;
		}

		//making connection
		try {
			this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbname,usr,pwd);
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return;
		}

	}

}