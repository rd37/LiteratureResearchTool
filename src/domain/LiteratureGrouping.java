package domain;

import javax.swing.DefaultListModel;

import databaselogger.DBLogger;
import databaselogger.DerbyDBPersistance;

public class LiteratureGrouping {
	private LiteratureReview selected;
	private String groupName;
	private DefaultListModel litrevgrouping = new DefaultListModel();
	
	public LiteratureGrouping(String name){
		//groupName=name;
		groupName=new String("group:"+this.hashCode());
	}
	
	public LiteratureReview findReview(String id){
		for(int i=0;i<litrevgrouping.size();i++){
			LiteratureReview litrev = (LiteratureReview)litrevgrouping.get(i);
			if(litrev.toString().equals(id))
				return litrev;
		}
		return null;
	}
	
	public void setName(String name,boolean updatedb) throws Exception{
		if(updatedb&&!name.equals(groupName))
			DerbyDBPersistance.getInstance().replaceID(DerbyDBPersistance.GROUPS, name, groupName);
		groupName=name;
	}
	
	public String getName(){
		return groupName;
	}
	
	public void removeLiteratureReview(LiteratureReview litrev){
		litrevgrouping.removeElement(litrev);
		DerbyDBPersistance.getInstance().updateGroupRemoveLitRev(this.getName(),litrev.getName());
	}
	
	public void removeLiteratureReview(String id){
		for(int i=0;i<litrevgrouping.size();i++){
			LiteratureReview litreview = (LiteratureReview)litrevgrouping.get(i);
			if(litreview.toString().equals(id)){
				litrevgrouping.removeElement(litreview);
				return;
			}
		}
	}
	
	public void add(LiteratureReview litrev,boolean update){
		if(!litrevgrouping.contains(litrev)){
			litrevgrouping.addElement(litrev);
			if(update)
				DerbyDBPersistance.getInstance().updateGroupAddLitRev(this.getName(), litrev.getName());
		}
	}
	
	public LiteratureReview getLitReview(String id){
		for(int i=0;i<litrevgrouping.size();i++){
			LiteratureReview litrev = (LiteratureReview)litrevgrouping.get(i);
			if(litrev.toString().equals(id))
				return litrev;
		}
		DBLogger.getInstance().print("LiteratureGrouping", "Lit rev id not found "+id);
		return null;
	}
	
	public LiteratureReview getLitReview(int index){
		if(index>=0 && index<litrevgrouping.size())
			return (LiteratureReview)litrevgrouping.get(index);
		DBLogger.getInstance().print("LiteratureGrouping", "invalid index "+index);
		return null;
	}
	
	public String toString(){
		return groupName;
	}

	public void setSelected(LiteratureReview selected) {
		this.selected = selected;
	}

	public LiteratureReview getSelected() {
		return selected;
	}

	public DefaultListModel getLitrevgrouping() {
		return litrevgrouping;
	}
}
