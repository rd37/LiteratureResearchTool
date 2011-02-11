package domain;

import java.util.LinkedList;

import javax.swing.DefaultListModel;

import databaselogger.DBLogger;
import databaselogger.DerbyDBPersistance;

public class LiteratureGroupManager {
	private static LiteratureGroupManager manager = new LiteratureGroupManager();
	private DefaultListModel model = new DefaultListModel();
	
	private LiteratureGroupManager(){};
	
	public static LiteratureGroupManager getInstance(){return manager;}
	
	public DefaultListModel getListModel(){
		return model;
	}
	
	/*
	 * Used by DerbyDB to initialize the system
	 */
	public void setGroup(LinkedList<LiteratureGrouping> list){
		for(int i=0;i<list.size();i++){
			model.addElement(list.get(i));
		}
	}
	
	public void addGroup(LiteratureGrouping grouping){
		model.addElement(grouping);
		DerbyDBPersistance.getInstance().newTableEntry(DerbyDBPersistance.GROUPS, grouping);
	}
	
	public void removeGroup(LiteratureGrouping grouping){
		model.removeElement(grouping);
		DerbyDBPersistance.getInstance().removeLiteratureGrouping(grouping.getName());
	}
	
	public LiteratureGrouping findLiteratureGrouping(String id){
		for(int i=0;i<model.getSize();i++){
			LiteratureGrouping grouping = (LiteratureGrouping)model.get(i);
			if(grouping.toString().equals(id))
				return grouping;
		}
		DBLogger.getInstance().print("LiteratureGroupManager", "Unable to find id "+id);
		return null;
	}
	
	public void removeLiteratureReview(LiteratureReview litrev){
		for(int i=0;i<model.getSize();i++){
			LiteratureGrouping grouping = (LiteratureGrouping)model.get(i);
			grouping.removeLiteratureReview(litrev);
		}
	}
}
