package domain;

import java.util.LinkedList;

import javax.swing.DefaultListModel;

import databaselogger.DBLogger;
import databaselogger.DerbyDBPersistance;

public class LiteratureReviewManager {
	private static LiteratureReviewManager manager = new LiteratureReviewManager();
	private DefaultListModel litreviewmodel = new DefaultListModel();
	private DefaultListModel reviewmodel = new DefaultListModel();
	
	private LiteratureReviewManager(){};
	
	public static LiteratureReviewManager getInstance(){return manager;}
	
	/*
	 * Used by DerbyDB to initialize the system
	 */
	public void setLitRev(LinkedList<LiteratureReview> list){
		for(int i=0;i<list.size();i++){
			litreviewmodel.addElement(list.get(i));
		}
	}
	
	/*
	 * Used by DerbyDB to initialize the system
	 */
	public void setReview(LinkedList<Review> list){
		for(int i=0;i<list.size();i++){
			reviewmodel.addElement(list.get(i));
		}
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
	}
	
	public void removeLiteratureReview(LiteratureReview litreview){
		litreviewmodel.removeElement(litreview);
		DerbyDBPersistance.getInstance().removeLiteratureReview(litreview.getName());
	}
	
	public void addReview(Review review){
		reviewmodel.addElement(review);
		DerbyDBPersistance.getInstance().newTableEntry(DerbyDBPersistance.REVIEWS, review);
	}
	
	public void removeReview(Review review){
		//DBLogger.getInstance().print("LitRevMan", "delete type "+review);
		reviewmodel.removeElement(review);
		//DerbyDBPersistance.getInstance().removeReviewEntry(this.)
	}
	
	public LiteratureReview findLiteratureReview(String id){
		for(int i=0;i<litreviewmodel.getSize();i++){
			LiteratureReview litrev = (LiteratureReview)litreviewmodel.get(i);
			if(litrev.toString().equals(id))
				return litrev;
		}
		DBLogger.getInstance().print("LiteratureReviewManager", "Unable to find id "+id);
		return null;
	}
	
	public Review findReview(String id){
		for(int i=0;i<reviewmodel.getSize();i++){
			Review rev = (Review)reviewmodel.get(i);
			if(rev.toString().equals(id))
				return rev;
		}
		DBLogger.getInstance().print("ReviewManager", "Unable to find id "+id);
		return null;
	}
}
