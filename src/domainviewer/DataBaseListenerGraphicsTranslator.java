package domainviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import graphics.elements.Box;
import graphics.gui.GraphicsElementListener;
import graphics.gui.GraphicsInterface;
import graphics.management.GraphicsManager;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;

import databaselogger.DBLogger;
//import databaselogger.DerbyDBPersistance;
import domain.LiteratureGroupManager;
import domain.LiteratureGrouping;
import domain.LiteratureProductManager;
import domain.LiteratureReviewManager;

public class DataBaseListenerGraphicsTranslator implements DatabaseChanged {
	private DefaultListModel volBox = new DefaultListModel();
	private DefaultListModel projectionLists = new DefaultListModel();
	private DefaultListModel pipeLineElements = new DefaultListModel();
	private DefaultListModel gvsElements = new DefaultListModel();
	private DefaultListModel sceneElements = new DefaultListModel();
	private DefaultListModel scenes = new DefaultListModel();
	private String sceneID;
	private HashMap<String,Object> mapping = new HashMap<String,Object>();
	
	private GraphicsElementListener gel;
	
	public DataBaseListenerGraphicsTranslator(){}
	
	public void intialize(JFrame frame){
		/*
		 * Setup Models and register to the Graphics Interface
		 */
		gel=GraphicsElementListener.getInstance();
		GraphicsManager.getInstance().registerListener(gel);
		gel.addListModel(scenes);gel.addListModel(sceneElements);gel.addListModel(gvsElements);
		gel.addListModel(pipeLineElements);gel.addListModel(projectionLists);gel.addListModel(volBox);
		
		sceneID = GraphicsInterface.getInstance().createScene();
		GraphicsInterface.getInstance().registerSceneElementListener(sceneElements, sceneID);
		GraphicsInterface.getInstance().registerScenePipeLineListener(pipeLineElements, sceneID);
		GraphicsInterface.getInstance().registerSceneViewScreenListener(gvsElements, sceneID);
		
		DBLogger.getInstance().print("DBLGT", "Initialized Models");
		/*
		 * Setup frame and set scene and screen for draw panel
		 */
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().setSize(625, 625);
		frame.setSize(625, 625);
		
		//DrawPanel dp = new DrawPanel();
		DatabaseViewPanel dp = new DatabaseViewPanel();
		dp.setSize(600, 600);
		dp.setBorder(BorderFactory.createLineBorder (Color.blue, 2));
	    dp.setBackground(Color.white);
		frame.getContentPane().add(dp,BorderLayout.CENTER);
		
		
		String gvsViewScreenID = GraphicsInterface.getInstance().createGraphicsViewScreen(600, 600);
		GraphicsInterface.getInstance().registerCanvasWithViewer(dp, gvsViewScreenID, sceneID);
		/*
		 * step by step setup of graphics viewer
		 */
		String eyeID = GraphicsInterface.getInstance().createEye();
		String volumeBoxID = GraphicsInterface.getInstance().createVolumeBox(-4, 1, 1, -1);
		String orthoPipeline =GraphicsInterface.getInstance().createPipeLine(eyeID, volumeBoxID, gvsViewScreenID, 2);
		GraphicsInterface.getInstance().addElementToScene(sceneID, eyeID);
		GraphicsInterface.getInstance().addViewScreenToScene(sceneID, gvsViewScreenID);
		GraphicsInterface.getInstance().addPipeLineToScene(sceneID, orthoPipeline);
		//String boxID = GraphicsInterface.getInstance().createBox(-1, -1, -10, 1, 1, -9);
		//GraphicsInterface.getInstance().addElementToScene(sceneID, boxID);
		GraphicsInterface.getInstance().setElementSelectedInScene(sceneID, eyeID);
		GraphicsInterface.getInstance().updateSceneView(sceneID);
		
		/*
		 * Register to Managers lg, lp, lr
		 */
		LiteratureGroupManager.getInstance().registerDBChange(this);
		LiteratureProductManager.getInstance().registerDBChange(this);
		LiteratureReviewManager.getInstance().registerDBChange(this);
	}

	@Override
	public void dbupdates(int type,int op,Object obj) {
		/*
		 * Update graphical view
		 */
		DBLogger.getInstance().print("DBLGT", "Data Base Change Detected "+type);
		if(type==domain.System.Group){
			if(!mapping.containsValue(obj)&&op==0){
				String boxID = GraphicsInterface.getInstance().createBox(-1, -1, -20, 1, 1, -19);
				GraphicsInterface.getInstance().addElementToScene(sceneID, boxID);
				mapping.put(boxID, obj);
			}else
			if(!mapping.containsValue(obj)&&op==1){
				Set<String> set= mapping.keySet();
				Iterator<String> iSet=set.iterator();
				while(iSet.hasNext()){
					String key = (String)iSet.next();
					if(mapping.get(key)==obj){
						GraphicsInterface.getInstance().removeElementFromScene(sceneID, key);
						mapping.remove(key);
					}
				}
			}
		}/*else
		if(type==domain.System.LitProd){
			if(!mapping.containsValue(obj)&&op==0){
				String boxID = GraphicsInterface.getInstance().createBox(-0.5, -0.5, -20, 0.5, 0.5, -19);
				GraphicsInterface.getInstance().addElementToScene(sceneID, boxID);
				mapping.put(boxID, obj);
			}else
			if(!mapping.containsValue(obj)&&op==1){
				Set<String> set= mapping.keySet();
				Iterator<String> iSet=set.iterator();
				while(iSet.hasNext()){
					String key = (String)iSet.next();
					if(mapping.get(key)==obj){
						GraphicsInterface.getInstance().removeElementFromScene(sceneID, key);
						mapping.remove(key);
					}
				}
			}
		}else
		if(type==domain.System.LitRev&&op==0){
			if(!mapping.containsValue(obj)){
				String boxID = GraphicsInterface.getInstance().createBox(-0.5, -0.5, -20, 0.5, 0.5, -19);
				GraphicsInterface.getInstance().addElementToScene(sceneID, boxID);
				mapping.put(boxID, obj);
			}else
			if(!mapping.containsValue(obj)&&op==1){
				Set<String> set= mapping.keySet();
				Iterator<String> iSet=set.iterator();
				while(iSet.hasNext()){
					String key = (String)iSet.next();
					if(mapping.get(key)==obj){
						GraphicsInterface.getInstance().removeElementFromScene(sceneID, key);
						mapping.remove(key);
					}
				}
			}
		}else
		if(type==domain.System.Rev){
			if(!mapping.containsValue(obj)&&op==0){
				String boxID = GraphicsInterface.getInstance().createBox(-0.25, -0.25, -20, 0.25, 0.25, -19);
				GraphicsInterface.getInstance().addElementToScene(sceneID, boxID);
				mapping.put(boxID, obj);
			}else
			if(!mapping.containsValue(obj)&&op==1){
				Set<String> set= mapping.keySet();
				Iterator<String> iSet=set.iterator();
				while(iSet.hasNext()){
					String key = (String)iSet.next();
					if(mapping.get(key)==obj){
						GraphicsInterface.getInstance().removeElementFromScene(sceneID, key);
						mapping.remove(key);
					}
				}
			}
		}*/
		//change mapping positions for presentation in graph
		organize();
		GraphicsInterface.getInstance().updateSceneView();
	}
	
	private void organize(){
		/*
		 * Groups First
		 */
		int groupCount=0;
		for(int i=0;i<mapping.size();i++){
			Set<String> set= mapping.keySet();
			Iterator<String> iSet=set.iterator();
			while(iSet.hasNext()){
				String key = (String)iSet.next();//key is id of the graphical element
				if(mapping.get(key) instanceof LiteratureGrouping){
					Box box = (Box) GraphicsManager.getInstance().getElement(key);
					box.getOrigin().set3DCoords(1.25*groupCount, 1, -20);
					groupCount++;
				}
			}
		}
		/*
		 * Lit Revs next with Products
		 */
		
		/*
		 * Reveiews Next in heirarchy
		 */
	}
}
