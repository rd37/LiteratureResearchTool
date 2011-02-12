package domain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import databaselogger.DBLogger;
import databaselogger.DerbyDBPersistance;

public class Review {
	private String revName;
	private String text = new String();
	private String textFileLoc=null;
	
	public Review(String name){
		revName=new String("Review:"+this.hashCode());
	}
	
	public void setName(String name){
		revName=name;
	}
	
	public String getName(){
		return revName;
	}

	public void setText(String text) {
		this.text = text;
		/*
		 * Create a new file if file does not exist for the reference string.
		 * save file
		 * 
		 * update database with info.
		 */
		if(textFileLoc==null){
			textFileLoc="Review"+revName.hashCode();
		}
		File openFile = new File(textFileLoc);
		
		try {
			openFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		try {
			FileWriter writer = new FileWriter(openFile);
			writer.write(text);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		DBLogger.getInstance().print("Review", "wrote Review to file "+openFile.getAbsolutePath());
		textFileLoc=openFile.getAbsolutePath();
		DerbyDBPersistance.getInstance().updateReview(this.getName(),textFileLoc);
	}

	public String getText() {
		return text;
	}
	
	public String toString(){
		return revName;
	}
}
