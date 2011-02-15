package domain;

import java.util.LinkedList;

import javax.swing.DefaultListModel;

import databaselogger.DBLogger;
import databaselogger.DerbyDBPersistance;
import domainviewer.DatabaseChanged;

public class LiteratureGroupManager {
	private static LiteratureGroupManager manager = new LiteratureGroupManager();
	private DefaultListModel model = new DefaultListModel();
	
	private LiteratureGroupManager(){};
	
	public static LiteratureGroupManager getInstance(){return manager;}
	
	private LinkedList<DatabaseChanged> dbListeners = new LinkedList<DatabaseChanged>();
    
    public void registerDBChange(DatabaseChanged listener){
    	dbListeners.add(listener);
    }
    
    public void removeDBChange(DatabaseChanged listener){
    	dbListeners.remove(listener);
    }
    
    private void updateListeners(int type,DefaultListModel records){
    	for(int i=0;i<dbListeners.size();i++){
    		dbListeners.get(i).dbupdate(type,records);
    	}
    }
	public DefaultListModel getListModel(){
		return model;
	}
	
	public boolean checkID(String groupName){
		LiteratureGrouping grp = this.findLiteratureGrouping(groupName);
		if(grp==null)
			return true;
		else
			return false;
	}
	/*
	 * Used by DerbyDB to initialize the system
	 */
	public void setGroup(LinkedList<LiteratureGrouping> list){
		for(int i=0;i<list.size();i++){
			model.addElement(list.get(i));
		}
		this.updateListeners(System.Group,model);
	}
	
	public void addGroup(LiteratureGrouping grouping){
		model.addElement(grouping);
		DerbyDBPersistance.getInstance().newTableEntry(DerbyDBPersistance.GROUPS, grouping);
		this.updateListeners(System.Group,model);
	}
	
	public void removeGroup(LiteratureGrouping grouping){
		model.removeElement(grouping);
		DerbyDBPersistance.getInstance().removeLiteratureGrouping(grouping.getName());
		this.updateListeners(System.Group,model);
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
		this.updateListeners(System.Group,model);
	}
}
