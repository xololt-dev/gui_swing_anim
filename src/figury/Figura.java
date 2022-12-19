/**
 * 
 */
package figury;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author tb
 *
 */
public abstract class Figura implements Runnable, ActionListener/*, Shape*/ {
	// wspolny bufor
	protected Graphics2D buffer;
	protected Area area;
	// do wykreslania
	protected Shape shape;
	// przeksztalcenie obiektu
	protected AffineTransform aft;
	// przesuniecie
	protected int dx, dy;
	// rozciaganie
	protected double sf;
	// kat obrotu
	protected double an;
	// Bounce off of walls
	protected boolean bounce;
	// Does it bounce off of racket
	protected boolean bounceR;
	protected int delay;
	protected int width;
	protected int height;
	protected Color clr;

	protected static final Random rand = new Random();

	public Figura(){}
	public Figura(Graphics2D buf, int del, int w, int h) {
		delay = del;
		buffer = buf;
		width = w;
		height = h;

		dx = 1 + rand.nextInt(5);
		dy = 1 + rand.nextInt(5);
		sf = 1 + 0.05 * rand.nextDouble();
		an = 0.1 * rand.nextDouble();

		//aft = new AffineTransform(1,0,0,1,0,0);
		aft = new AffineTransform();

		clr = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255), 255);
		// reszta musi być zawarta w realizacji klasy Figure
		// (tworzenie figury i przygotowanie transformacji)

	}

	@Override
	public void run() {
		// przesuniecie na srodek
		aft.translate(300, 100);
		area.transform(aft);
		shape = area;

		while (true) {
			// przygotowanie nastepnego kadru
			shape = nextFrame();
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
			}
		}
	}

	protected Shape nextFrame() {
		// zapamietanie na zmiennej tymczasowej
		// aby nie przeszkadzalo w wykreslaniu
		area = new Area(area);
		aft = new AffineTransform();
		Rectangle bounds = area.getBounds();
		//int cx = bounds.x + bounds.width / 2;
		//int cy = bounds.y + bounds.height / 2;
		int cx = (int)bounds.getMaxX();
		int cy = (int)bounds.getMaxY();
		// odbicie
		if (bounds.getX() < 0 || cx > width)
		{
			bounce = true;
			if(bounds.getX() < 0)
			{
				cx = 0;
			}
			else if(cx > width){
				cx = width;
			}
		}
		if((bounce && !bounceR) || (!bounce && bounceR)){
			if(bounce){
				if(((cx == width) && (dx > 0)) ||((cx == 0)&&(dx < 0))){
					dx = -dx;
				}
			}
			else{
				dx = -dx;
			}
			//System.out.println("cx: " + cx + " width:" + width + " dx: " + dx);
			bounce = false;
			bounceR = false;
		}

		if (bounds.getY() < 0 || cy > height)
		{
			//System.out.println("cy: " + bounds.getY() + " height:" + height);
			if(bounds.getY() < 0){
				cy = 0;
				if(dy < 0){
					dy = -dy;
				}
			}
			else if (cy > height){
				cy = height;
				if(dy > 0){
					dy = -dy;
				}
			}
		}

		// zwiekszenie lub zmniejszenie
		//if (bounds.height > height / 3 || bounds.height < 10)
		//	sf = 1 / sf;
		// konstrukcja przeksztalcenia
		aft.translate(cx, cy);
		aft.scale(sf, sf);
		//aft.rotate(an);
		aft.translate(-cx, -cy);
		aft.translate(dx, dy);
		// przeksztalcenie obiektu
		area.transform(aft);
		return area;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		// wypelnienie obiektu
		buffer.setColor(clr.brighter());
		buffer.fill(shape);
		// wykreslenie ramki
		buffer.setColor(clr.darker());
		buffer.draw(shape);
	}

}

class Kwadrat extends Figura{
	//public Kwadrat(){super();}
	public Kwadrat(Graphics2D buf, int del, int w, int h) {
		super(buf, del, w, h);
	}

	@Override
	public void run() {
		// przesuniecie na srodek
		aft.translate(100, 100);
		if(area == null) {
			Rectangle a = new Rectangle(rand.nextInt(100),rand.nextInt(100));
			area = new Area(a);
		}
		area.transform(aft);
		shape = area;

		while (true) {
			// przygotowanie nastepnego kadru
			shape = nextFrame();
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
			}
		}
	}
}

class Racket extends Kwadrat{
	protected int number;
	protected Elipsa closest = null;
	public Racket(Graphics2D buf, int del, int w, int h, int num) {
		super(buf, del, w, h);
		number = num;
		dx = 0;
		dy = 5;
		clr = new Color(0, 255, 255, 255);
		//clr = new Color(255,192,203, 255);
	}

	@Override
	public void run() {
		// przesuniecie na srodek
		// To delete when doing pong
		if(number == 0)	aft.translate(21, 0);
		else aft.translate(381, 0);
		if(area == null) {
			Rectangle a = new Rectangle(20,100);
			area = new Area(a);
		}
		area.transform(aft);
		shape = area;

		while (true) {
			// przygotowanie nastepnego kadru
			shape = nextFrame();
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	protected Shape nextFrame() {
		// zapamietanie na zmiennej tymczasowej
		// aby nie przeszkadzalo w wykreslaniu
		area = new Area(area);
		aft = new AffineTransform();
		Elipsa closestTemp = closest;
		Rectangle bounds = area.getBounds();

		/*int cx;
		if(number == 1)		cx = 21;
		else cx = 381;*/
		//System.out.println(bounds.y + " " + bounds.height/2);
		int cy = bounds.y + bounds.height / 2;
		boolean rebound = false;
		// odbicie
		if ((cy < height/2) || (cy > 219 - bounds.height/2)) {
			//if(cy < height/2){
			//	cy = height/2;
			//}
			//else{
			//	cy = 219 - bounds.height/2;
			//}
			rebound = true;
		}
		if(closestTemp != null && !rebound){
			if(closestTemp.area.getBounds2D().getY() < area.getBounds2D().getY()){
				//if((closestTemp.dy < 0 && dy > 0) || (closestTemp.dy > 0 && dy < 0)) {
				if((closestTemp.dy < 0 && dy > 0) || (closestTemp.dy > 0 && dy > 0)) {
					rebound = true;
				}
				if(dy == 0) dy = 5;
			} else if (closestTemp.area.getBounds2D().getMaxY() > area.getBounds2D().getMaxY()) {
				if((closestTemp.dy < 0 && dy < 0) || (closestTemp.dy > 0 && dy < 0)){
					rebound = true;
				}
				if(dy == 0) dy = 5;
			} else if((closestTemp.area.getBounds2D().getY() > area.getBounds2D().getY()) && (closestTemp.area.getBounds2D().getMaxY() < area.getBounds2D().getMaxY())) {
				// Stop the racket
				if (dy != 0) dy = 0;
			}
			//System.out.println(closestTemp.area.getBounds2D().getY() + " " + closestTemp.dy + " racket: " + area.getBounds2D().getY() + " " + dy);
		}
		if(rebound){
			if(dy == 0) {
				dy = 5;
			}
			else{
				dy = -dy;
			}
		}
		else if(!rebound && closestTemp == null) {dy = 0;}
		// konstrukcja przeksztalcenia
		aft.translate(dx, dy);
		// przeksztalcenie obiektu
		area.transform(aft);
		return area;
	}
}

class Elipsa extends Figura{
	public Elipsa(Graphics2D buf, int del, int w, int h) {
		super(buf, del, w, h);
		dx = 1;
		sf = 1;
	}

	@Override
	public void run() {
		// przesuniecie na srodek
		aft.translate(211, 110);
		if(area == null) {
			Ellipse2D.Float a = new Ellipse2D.Float(rand.nextInt(100),rand.nextInt(100), 10, 10);
			area = new Area(a);
		}
		area.transform(aft);
		shape = area;

		while (true) {
			// przygotowanie nastepnego kadru
			shape = nextFrame();
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
			}
		}
	}
}