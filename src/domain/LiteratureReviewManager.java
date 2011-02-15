package domain;

import java.util.LinkedList;

import javax.swing.DefaultListModel;

import databaselogger.DBLogger;
import databaselogger.DerbyDBPersistance;
import domainviewer.DatabaseChanged;

public class LiteratureReviewManager {
	private static LiteratureReviewManager manager = new LiteratureReviewManager();
	private DefaultListModel litreviewmodel = new DefaultListModel();
	private DefaultListModel reviewmodel = new DefaultListModel();
	
	private LiteratureReviewManager(){};
	
	public static LiteratureReviewManager getInstance(){return manager;}
	
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
    
	/*
	 * Used by DerbyDB to initialize the system
	 */
	public void setLitRev(LinkedList<LiteratureReview> list){
		for(int i=0;i<list.size();i++){
			litreviewmodel.addElement(list.get(i));
		}
		this.updateListeners(System.LitRev,litreviewmodel);
	}
	
	/*
	 * Used by DerbyDB to initialize the system
	 */
	public void setReview(LinkedList<Review> list){
		for(int i=0;i<list.size();i++){
			reviewmodel.addElement(list.get(i));
		}
		this.updateListeners(System.Rev,reviewmodel);
	}
	
	
	public DefaultListModel getLitRevModel(){
		return litreviewmodel;
	}
	
	public DefaultListModel getRevModel(){
		return reviewmodel;
	}
	
	public void addLiteratureReview(LiteratureReview litreview){
		litreviewmodel.addElement(litreview);
		DerbyDBPersistance.getInstance().newTableEntry(DerbyDBPersistance.LITREVS, litreview);
		this.updateListeners(System.LitRev,litreviewmodel);
	}
	
	public void removeLiteratureReview(LiteratureReview litreview){
		litreviewmodel.removeElement(litreview);
		DerbyDBPersistance.getInstance().removeLiteratureReview(litreview.getName());
		this.updateListeners(System.LitRev,litreviewmodel);
	}
	
	public void addReview(Review review){
		reviewmodel.addElement(review);
		DerbyDBPersistance.getInstance().newTableEntry(DerbyDBPersistance.REVIEWS, review);
		this.updateListeners(System.Rev,reviewmodel);
	}
	
	public void removeReview(Review review){
		reviewmodel.removeElement(review);
		this.updateListeners(System.Rev,reviewmodel);
	}
	
	public boolean checkLitRevID(String litrev){
		if(this.findLiteratureReview(litrev)==null){
			return true;
		}else{
			return false;
		}
	}
	
	public LiteratureReview findLiteratureReview(String id){
		for(int i=0;i<litreviewmodel.getSize();i++){
			LiteratureReview litrev = (LiteratureReview)litreviewmodel.get(i);
			if(litrev.toString().equals(id))
				return litrev;
		}
		DBLogger.getInstance().print("LiteratureReviewManager", "Unable to find lit review id "+id);
		return null;
	}
	
	public boolean checkReviewID(String rev){
		if(this.findReview(rev)==null){
			return true;
		}else{
			return false;
		}
	}
	
	public Review findReview(String id){
		for(int i=0;i<reviewmodel.getSize();i++){
			Review rev = (Review)reviewmodel.get(i);
			if(rev.toString().equals(id))
				return rev;
		}
		DBLogger.getInstance().print("ReviewManager", "Unable to find review id "+id);
		return null;
	}
}
