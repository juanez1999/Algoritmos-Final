package bravo_correa_perez_quevedo_vivas;

import java.util.Iterator;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.sound.SoundFile;

public abstract class Mundo extends Thread {
	protected PApplet app;
	protected Jugador[] j;
	protected PImage fondo;
	protected PImage interfaz;
	protected LinkedList<Ovni> ovnis;
	protected LinkedList<Recogible> objetos;
	protected int contadorOvni;
	protected int contadorObj;
	protected int contadorTiempo;
	protected PFont mali;
	protected SoundFile mus;
	protected boolean vivo;
	protected boolean ganar;
	protected boolean matar;
	
	public Mundo(PApplet app) {
		this.app = app;
		j = new Jugador[2];
		j[0] = new Jugador(app, 1);
		j[0].start();
		j[1] = new Jugador(app, 2);
		j[1].start();
		vivo = true;
		ganar = false;
		matar = false;
		ovnis = new LinkedList<Ovni>();
		Ovni o = new Ovni(app, this);
		o.start();
		ovnis.add(o);
		objetos = new LinkedList<Recogible>();
		fondo = app.loadImage("fondo1.png");
		interfaz = app.loadImage("Interfaz1.png");
		contadorOvni = 0;
		contadorObj = 100;
		mali = app.loadFont("maliB_28.vlw");
		mus = new SoundFile(app, "musicaJuego.wav");
		mus.play();
		contadorTiempo = app.millis()+90000;
	}

	public void run() {
		while(vivo) {
			synchronized(ovnis) {
				//Crear Ovnis
				if(contadorOvni % 180 == 0) {
					Ovni o = new Ovni(app, this);
					o.start();
					ovnis.add(o);
				}
			}

			synchronized(objetos) {
				if(contadorObj % 60 == 0) {
					int ran = (int)app.random(20);
					if(ran == 0) {
						objetos.add(new Agujero(app));
					} else if(ran == 1) {
						Cometa c = new Cometa(app);
						c.getH().start();
						objetos.add(c);
					}else {
						objetos.add(new Estrella(app));
					}
					
				}
				
				Iterator<Recogible> it = objetos.iterator();
				while(it.hasNext()) {
					Recogible o = it.next();
					if(j[0].validarObj(o) || j[1].validarObj(o)) {
						it.remove();
					}
					
					if(o instanceof Cometa && ((Cometa)o).borrar()) {
						it.remove();
					}
				}
			}
					
			contadorOvni++;
			contadorObj++;
			
			try {
				sleep(16);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean terminarJuego() {
		if(contadorTiempo-app.millis() <= 0) {
			mus.stop();
			Ovni o = null;
			Iterator<Ovni> it = ovnis.iterator();
			if(it.hasNext()) {
				o = it.next();
			}
			while(it.hasNext()) {
				Ovni obj = it.next();
				
				if(o.getEstrellas()-obj.getEstrellas() <= 0) {
					o = obj;
				}
			}
			if(o.getEstrellas() - j[0].getEstrellas() <= 0) {
				ganar = true;
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public void pararMus() {
		if(mus.isPlaying()) {
			mus.stop();
		}		
	}
	
	public LinkedList<Recogible> getObjetos() {
		return objetos;
	}
	
	public LinkedList<Ovni> getOvnis() {
		return ovnis;
	}
	
	public Jugador[] getJ() {
		return j;
	}
	
	public Jugador getJu() {
		return j[0];
	}
	
	public boolean getGanar() {
		return ganar;
	}
	public boolean getMatar() {
		return matar;
	}
	
	public void setMatar(boolean b) {
		 matar = b;
	}
}
