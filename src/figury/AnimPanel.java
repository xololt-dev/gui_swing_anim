package figury;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

public class AnimPanel extends JPanel implements ActionListener {
	/**
	 * 
	 */
	ArrayList<Racket> rackets = new ArrayList<>();
	ArrayList<Elipsa> elipses = new ArrayList<>();
	private static final long serialVersionUID = 1L;

	// bufor
	Image image;
	// wykreslacz ekranowy
	Graphics2D device;
	// wykreslacz bufora
	Graphics2D buffer;

	private int delay = 10;

	private Timer timer;

	private static int numer = 0;

	public AnimPanel() {
		super();
		setBackground(Color.WHITE);
		timer = new Timer(delay, this);
	}

	public void initialize() {
		int width = getWidth();
		int height = getHeight();

		image = createImage(width, height);
		buffer = (Graphics2D) image.getGraphics();
		buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		device = (Graphics2D) getGraphics();
		device.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	void addFig() {
		Elipsa fig = new Elipsa(buffer, delay, getWidth(), getHeight());
		elipses.add(fig);
		timer.addActionListener(fig);
		new Thread(fig).start();
	}

	void addRacket(int number)
	{
		Racket fig = new Racket(buffer, delay, 20, 100, number);
		rackets.add(fig);
		timer.addActionListener(fig);
		new Thread(fig).start();
	}
	/*void addFig() {
		Figura fig = (numer++ % 2 == 0) ? new Racket(buffer, delay, getWidth(), getHeight(), numer%2)
				: new Elipsa(buffer, delay, getWidth(), getHeight());
		/*Figura fig = (numer++ % 2 == 0) ? new Kwadrat(buffer, delay, getWidth(), getHeight())
				: new Elipsa(buffer, delay, getWidth(), getHeight());*/
		/*timer.addActionListener(fig);
		new Thread(fig).start();
	}*/

	void animate() {
		if (timer.isRunning()) {
			timer.stop();
		} else {
			timer.start();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		checkBounce();
		checkClosest();
		device.drawImage(image, 0, 0, null);
		buffer.clearRect(0, 0, getWidth(), getHeight());
	}

	public void checkClosest(){
		// 0 Closest goes to left, 1 goes to right
		// Temporary elipses
		Elipsa tempL = null;
		Elipsa tempR = null;
		for(int i = 0; i < elipses.size(); i++){
			Elipsa a = elipses.get(i);
			if(a.dx > 0){
				// If no object selected, select as first. Otherwise check conditions if swap needed
				if(tempR == null){
					tempR = a;
				}
				else{ // If incoming object is gonna be the first to get to the right side, swap objects
					if(((381 - tempR.area.getBounds2D().getMaxX())/tempR.dx) > ((381 - a.area.getBounds2D().getMaxX())/a.dx)) tempR = a;
				}
			}
			else{
				if(tempL == null){
					tempL = a;
				}
				else{ // If incoming object is gonna be the first to get to the right side, swap objects
					if((41 - tempL.area.getBounds2D().getX())/tempL.dx > (41 - a.area.getBounds2D().getX())/a.dx) tempL = a;
				}
			}
		}
		Racket racketL = rackets.get(0);
		Racket racketR = rackets.get(1);
		racketR.closest = tempR;
		racketL.closest = tempL;
		rackets.set(0, racketL);
		rackets.set(1, racketR);
	}

	void checkBounce(){
		// 0 Closest goes to left, 1 goes to right
		// Creating temporary variables
		Elipsa temp;
		boolean bounce = false;
		for(int i = 0; i < elipses.size(); i++) {
			temp = elipses.get(i);
			for (int j = 0; j < rackets.size(); j++) {
				// Checking if ball hits the racket
				if (temp.area.intersects(rackets.get(j).area.getBounds2D())) {
					if((temp.dx < 0) && j == 0){
						// If it does and racket is not going off the edge, bounce = true
						if(!temp.bounceR){
							//temp.dx = -temp.dx;
							temp.bounceR = true;
							bounce = true;
						}
					}
					else if((temp.dx > 0) && j == 1){
						if(!temp.bounceR){
							//temp.dx = -temp.dx;
							temp.bounceR = true;
							bounce = true;
						}
					}
				}
			}
			if(bounce){
				elipses.set(i, temp);
			}
		}
	}
}
