package domainviewer;

import java.awt.BorderLayout;
import java.awt.Color;

import graphics.gui.GraphicsElementListener;
import graphics.gui.GraphicsInterface;
import graphics.management.GraphicsManager;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;

import databaselogger.DBLogger;
//import databaselogger.DerbyDBPersistance;

public class DataBaseListenerGraphicsTranslator implements DatabaseChanged {
	private DefaultListModel volBox = new DefaultListModel();
	private DefaultListModel projectionLists = new DefaultListModel();
	private DefaultListModel pipeLineElements = new DefaultListModel();
	private DefaultListModel gvsElements = new DefaultListModel();
	private DefaultListModel sceneElements = new DefaultListModel();
	private DefaultListModel scenes = new DefaultListModel();
	private String sceneID;
	
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
		String boxID = GraphicsInterface.getInstance().createBox(-1, -1, -10, 1, 1, -9);
		GraphicsInterface.getInstance().addElementToScene(sceneID, boxID);
		GraphicsInterface.getInstance().setElementSelectedInScene(sceneID, eyeID);
		GraphicsInterface.getInstance().updateSceneView(sceneID);
		
		/*
		 * Register to Managers lg, lp, lr
		 */
		
	}

	@Override
	public void dbupdate(int type,DefaultListModel model) {
		/*
		 * Update graphical view
		 */
		DBLogger.getInstance().print("DBLGT", "Data Base Change Detected "+type);
		GraphicsInterface.getInstance().updateSceneView();
	}
}
