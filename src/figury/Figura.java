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
	protected int delay;
	protected int width;
	protected int height;
	protected Color clr;
	protected int positionX;
	protected int positionY;

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

		clr = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
		// reszta musi być zawarta w realizacji klasy Figure
		// (tworzenie figury i przygotowanie transformacji)

	}

	@Override
	public void run() {
		// przesuniecie na srodek
		aft.translate(300, 100);
		area.transform(aft);
		shape = area;
		//positionX;
		//positionY;

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
		int cx = bounds.x + bounds.width / 2;
		int cy = bounds.y + bounds.height / 2;
		// odbicie
		if (cx < 0 || cx > width)
			dx = -dx;
		if (cy < 0 || cy > height)
			dy = -dy;
		// zwiekszenie lub zmniejszenie
		if (bounds.height > height / 3 || bounds.height < 10)
			sf = 1 / sf;
		// konstrukcja przeksztalcenia
		aft.translate(cx, cy);
		aft.scale(sf, sf);
		aft.rotate(an);
		aft.translate(-cx, -cy);
		aft.translate(dx, dy);
		// przeksztalcenie obiektu
		area.transform(aft);
		//System.out.println(aft);
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
		//width = 20;
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
	protected Elipsa closest;
	public Racket(Graphics2D buf, int del, int w, int h, int num) {
		super(buf, del, w, h);
		number = num;
		dx = 0;
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
		Rectangle bounds = area.getBounds();
		/*int cx;
		if(number == 1)		cx = 21;
		else cx = 381;*/
		//System.out.println(bounds.y + " " + bounds.height/2);
		int cy = bounds.y + bounds.height / 2;
		// odbicie
		if (cy < height/2 || cy > 219 - bounds.height/2)
			dy = -dy;
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
	}

	@Override
	public void run() {
		// przesuniecie na srodek
		aft.translate(100, 100);
		if(area == null) {
			Ellipse2D.Float a = new Ellipse2D.Float(rand.nextInt(100),rand.nextInt(100), rand.nextInt(100), rand.nextInt(100));
			area = new Area(a);
		}
		area.transform(aft);
		shape = area;
		positionX = 100;
		positionY = 100;

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