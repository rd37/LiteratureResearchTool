package domainviewer;

import graphics.elements.Element;
import graphics.elements.Eye;
import graphics.elements.GraphicsScene;
import graphics.elements.GraphicsViewScreen;
import graphics.gui.GraphicsInterface;
import graphics.gui.panels.DrawPanel;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import databaselogger.DBLogger;

public class DatabaseViewPanel extends DrawPanel implements MouseListener,KeyListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1145936619312000634L;
	
	private GraphicsViewScreen gvs;
	private GraphicsScene scene;
	
	private int xPress;
	private int yPress;
	private int xRelease;
	private int yRelease;
	//private double x,y,z=0;
	private double xRot,yRot,zRot=0.0;
	
	public DatabaseViewPanel(){
		super();
		this.addMouseListener(this);
		//this.addKeyListener(this);
	}
	
	public void setGraphicsScene(GraphicsScene scene){
		this.scene = scene;
	}
	
	public void projectSceneElements(){
		GraphicsInterface.getInstance().updateSceneView(scene.toString(), gvs.toString());
	}
	
	public void setGraphicsViewScreen(GraphicsViewScreen gvs){
		this.gvs=gvs;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		//g.clearRect(0, 75, 500,500);
		if(g!=null){
			//g.drawString("hello",100,100);
			if(gvs!=null)
				gvs.drawPoints(g);
		}else{
			//System.out.println("g is null");
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		//System.out.println("Mouseclicked");
		this.projectSceneElements();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		//System.out.println("Mouse Pressed");
		xPress = arg0.getX();
		yPress = arg0.getY();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		xRelease = arg0.getX();
		yRelease = arg0.getY();
		rotateElement();
		this.projectSceneElements();
	}
	
	private void rotateElement(){
		if(scene.getElementSelected()!=null){
			double diffx = xPress-xRelease;
			double diffy = yPress-yRelease;
			double hyp = Math.pow((Math.pow(diffx, 2.0)+Math.pow(diffy, 2.0)), 0.5);
			
			double slope=-1;
			if(diffx!=0)
				slope = diffy/diffx;
			if(diffx==0 || Math.abs(slope)>5.0){
				if(diffy>0){//rotate positive around x axis
					GraphicsInterface.getInstance().rotateElement(scene.toString(), scene.getElementSelected().toString(), Element.ROTX, 0.01*hyp);
				}
				if(diffy<0){//rotate neg around x axis
					GraphicsInterface.getInstance().rotateElement(scene.toString(), scene.getElementSelected().toString(), Element.ROTX, -0.01*hyp);	
				}
				//System.out.println("Rotate X "+Math.abs(slope));
				return;
			}
			if(diffy==0 || Math.abs(slope)<0.5 ){
				if(diffx>0){//rotate positive around y axis
					GraphicsInterface.getInstance().rotateElement(scene.toString(), scene.getElementSelected().toString(), Element.ROTY, 0.01*hyp);
				}
				if(diffx<0){//rotate neg around y axis
					GraphicsInterface.getInstance().rotateElement(scene.toString(), scene.getElementSelected().toString(), Element.ROTY, -0.01*hyp);	
				}
				//System.out.println("Rotate Y "+Math.abs(slope));
				return;
			}
			if(diffx>0){//rotate positive around y axis
				GraphicsInterface.getInstance().rotateElement(scene.toString(), scene.getElementSelected().toString(), Element.ROTZ, 0.01*hyp);
			}
			if(diffx<0){//rotate neg around y axis
				GraphicsInterface.getInstance().rotateElement(scene.toString(), scene.getElementSelected().toString(), Element.ROTZ, -0.01*hyp);	
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		
		DBLogger.getInstance().print("DVP","Key pressed "+KeyEvent.getKeyText(arg0.getKeyCode()));
		String keyPressed=KeyEvent.getKeyText(arg0.getKeyCode());
		Eye eye = (Eye)scene.getElementSelected();
		if(scene.getElementSelected() instanceof Eye){
			DBLogger.getInstance().print("DVP", "Eye is selected");
		}
		if(keyPressed.equals("NumPad-8"))
			GraphicsInterface.getInstance().rotateElement(scene.toString(), scene.getElementSelected().toString(), Element.ROTX, xRot+=0.01);
		if(keyPressed.equals("NumPad-6"))
			GraphicsInterface.getInstance().rotateElement(scene.toString(), scene.getElementSelected().toString(), Element.ROTY, yRot+=0.01);
		if(keyPressed.equals("NumPad-7"))
			GraphicsInterface.getInstance().rotateElement(scene.toString(), scene.getElementSelected().toString(), Element.ROTZ, zRot+=0.01);
		if(keyPressed.equals("NumPad-2"))
			GraphicsInterface.getInstance().rotateElement(scene.toString(), scene.getElementSelected().toString(), Element.ROTX, xRot-=0.01);
		if(keyPressed.equals("NumPad-4"))
			GraphicsInterface.getInstance().rotateElement(scene.toString(), scene.getElementSelected().toString(), Element.ROTY, yRot-=0.01);
		if(keyPressed.equals("NumPad-9"))
			GraphicsInterface.getInstance().rotateElement(scene.toString(), scene.getElementSelected().toString(), Element.ROTZ, zRot-=0.01);
		
		if(keyPressed.equals("NumPad +")){
			eye.moveForwardAlongZBasis(1.0);
		}
		if(keyPressed.equals("NumPad -")){
			eye.moveForwardAlongZBasis(-1.0);
		}
		this.projectSceneElements();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
