import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;

public class FroggerClient extends Applet implements MouseListener {
	public static final int WINHEIGHT = 576;
	public static final int WINWIDTH = 800;
	
	private JPanel mainContain;
	private JButton testButton;
	private DrawObjects objDrawer;
	private FroggerGui myGui;
	private JButton theButton; 

	private ImageIcon myBackground;
	public static ImageIcon myFrogImg;
	public static ImageIcon myGatorBodyImg;
	public static ImageIcon gatorclosedImg;
	public static ImageIcon gatoropenImg;
	public static ImageIcon homeImg;
	public static ImageIcon myDeadFrogImg;
	public static ImageIcon car1RImg;
	public static ImageIcon car2RImg;
	public static ImageIcon car32Img;
	public static ImageIcon car3LImg;
	public static ImageIcon car4LImg;
	public static ImageIcon racecar32Img;
	public static ImageIcon snake1Img;
	public static ImageIcon snake2Img;
	public static ImageIcon tadpole32Img;
	public static ImageIcon lilypadImg;
	public static ImageIcon beaver32Img;
	public static ImageIcon logImg;
	
	private JLabel startBut;
	private Image tempImg;
	
	
	public void init(){
		if(Debug.debugOn == true){
			System.out.println("<<In FroggerClients's init()>>");
		}
		
		//Set up the applets display area
		
		
		mainContain = new JPanel(null);
		this.setLayout(new GridLayout(1,1));
		this.setBounds(0,0,WINWIDTH,WINHEIGHT);
	    mainContain.setBackground(Color.CYAN);
	    startBut = new JLabel("Click To Start");
	    Dimension size = startBut.getPreferredSize();
	    startBut.setBounds((WINWIDTH/2)- ((size.width)/2), (WINHEIGHT/2)- (size.width)/2, size.width, size.height);
	    
		
		
		
		loadGraphics();
		
		//initialize the backGround
		JLabel backLab = new JLabel(myBackground);
		backLab.setBounds(0,0,800,576);
		
		
		
		this.add(mainContain);
		mainContain.add(startBut);
		mainContain.addMouseListener(this);
		
		this.setSize(WINWIDTH+1,WINHEIGHT+1);
		
		//Start the game thread
		myGui = new FroggerGui(mainContain, 75, 33);
		this.addKeyListener(myGui);
		
		
		
		//mainContain.requestFocus();
		
				
		//add the background to the container
		mainContain.add(backLab);
		
		
	}
	
	
	/** allows accessability to graphics and loads all of them **/
	private void loadGraphics(){
		
		//background image
		myBackground = loadImage("./images/background.jpg");
		
		//frog image
		myFrogImg =loadImage("./images/froggy32.png");
		
		//gator body image
		myGatorBodyImg = loadImage("./images/gatorbody.png");
			
		//dead frog image
		myDeadFrogImg =loadImage("./images/deadfroggy32.png" );
			
		//car1 Right image
		 car1RImg = loadImage("./images/car1R.png");
		 
//		car2 Right image
		 car2RImg = loadImage("./images/car2R.png");
//		car  image
		 car32Img = loadImage("./images/car32.png");
//		car3 Left image
		 car3LImg = loadImage("./images/car3L.png");
//		car4 Left image
		 car4LImg = loadImage("./images/car4L.png");
		 
		 // gator closed head img
		 gatorclosedImg = loadImage("./images/gatorclosed.png");
		 
		 //gator open head img
		 gatoropenImg = loadImage ("./images/gatoropen.png");
		 
		 //home image
		 homeImg = loadImage ("./images/home.png");
		 
		 //race car image
		 //racecar32Img = loadImage("./images/racecar32.png");
		 
		 //snake 1 image
		// snake1Img = loadImage("./images/snake1.png");
		 
		 //snake 2 image
		 //snake2Img = loadImage("./images/snake2.png");
		 
		 //tadPole image
		 tadpole32Img = loadImage ("./images/tadpole32.png");
		 
		 //lilypad image
		 //lilypadImg = loadImage ("./images/lilypad.png");
		 
		 //beaver image
		 beaver32Img = loadImage ("./images/beaver32.png");
		 
		 //log image
		 logImg = loadImage("./images/log.png");
		 
		 
		 
		
		
		
		
	}
	
	public void paint(Graphics g){
			
		super.paint(g);
		
		
		//g.drawRect(10,10,400,400);
		
		
	}
	
	public ImageIcon loadImage(String imgName){
		
		if (Debug.debugOn){
			System.out.println("Loading Image :"+ imgName);
		}
		ImageIcon tempIco;
		URL myLoc = getClass().getClassLoader().getResource(imgName);
		tempImg = this.getImage(myLoc);
		
		tempIco = new ImageIcon();
		tempIco.setImage(tempImg);
		if (Debug.debugOn){
			System.out.println("Finished Image :"+ imgName);
		}
		return tempIco;
	}
	/**
	 * @param args
	 */
	
	public void mouseClicked(MouseEvent e){
		try{
			
		myGui.start();
		}catch (IllegalThreadStateException ev){
			
		}
		mainContain.remove(startBut);
		this.requestFocus();
	}
	public void mouseExited(MouseEvent e){
		
	}
	
	public void mouseEntered(MouseEvent e){
		
	}
	public void mousePressed(MouseEvent e){
		
	}
	
	public void mouseReleased(MouseEvent e){
		
	}

}
