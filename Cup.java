import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
/* 
 *  Program: Rozne operacje na obiektach klasy Cup
 *     Plik: Cup.java
 *           definicja typu wyliczeniowego Material
 *           definicja klasy CupException
 *           definicja publicznej klasy Cup,zaimplementowanie interfejsu
 *           Serializable, przeciazenie metod klasy Object
 *           
 *    Autor:  Uladzimir Kaviaka 257276
 *     Data:  19 pazdziernik 2020 r.
 */

/* 
 *  Typ wyliczeniowy "Material" prezentuje z jakiego
 *  materialu zostala wykonana filizanka
 */
enum Material {
	UNKNOWN("undefined"), WOODEN("drewniana"), METALLIC("metalowa"), CERAMIC("ceramiczna"), GLASSY("szklana");

	String material_type;

	private Material(String material_type) {
		this.material_type = material_type;
	}

	@Override
	public String toString() {
		return material_type;
	}
}

/*
 * klasa do informowania o bledach podczas pracy nad obiektami klasy Cup
 */
class CupException extends Exception{

	private static final long serialVersionUID = 1L;

	public CupException(String Message) {
		super(Message);
	}
}

public class Cup implements Serializable, Comparable<Cup>{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private int size;
	private double cost;
	private Material material;

	public Cup(String name, double cost) throws CupException {
		setName(name);
		setCost(cost);
		material = Material.UNKNOWN;
	}

	public String getName() {
		return name;
	}

	public void setName(String value) throws CupException {
		if (value == null || value.equals(""))
			throw new CupException("Nazwa filizanki musi byc podana");
		name = value;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int value) throws CupException {
		if (value < 30)
			throw new CupException("Pojemnosc nie moze byc mniejsza niz 30 ");
		size = value;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double value) throws CupException {
		if (value == 0)
			throw new CupException("Cena nie moze wynosic 0");
		cost = value;
	}

	public Material getType() {
		return material;
	}

	public void setType(Material value) {
		material = value;
	}

	public void setType(String value) throws CupException {
		if (value == null || value.equals("")) {
			material = Material.UNKNOWN;
			return;
		}
		for (Material tmp : Material.values()) {
			if (tmp.toString().equals(value)) {
				material = tmp;
				return;
			}
		}
		throw new CupException("Takiego typu filizanek nie istnieje");
	}

	/* Przeciazanie metod klasy Object */
	@Override
	public String toString() {
		return name + " " + material.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Cup) || obj==null) return false;
		Cup tmp = (Cup)obj;
		if(name==tmp.name && size==tmp.size && cost==tmp.cost && material==tmp.material) return true;
		return false;
	}
	
	@Override
	public int compareTo(Cup o) {
		return this.name.compareTo(o.name); /* compareTo porzadkuje po nazwie zgodnie z alfabetem */
	}
	
	@Override
	public int hashCode() {
		int hashcode = name.hashCode();
	    hashcode = 31 * hashcode * size;
	    hashcode = 31 * hashcode * (int)Math.round(cost);
	    hashcode = 31 * hashcode * material.hashCode();
	    return hashcode;
	}
	/* Konice przeciazania metod klasy Object */
	

	public static void printToFile(String filename, Cup cup) throws CupException {
		try (PrintWriter writer = new PrintWriter(filename)) {
			writer.println(cup.name + "/" + cup.material + "/" + cup.size + "/" + cup.cost);
		} catch (FileNotFoundException ex) {
			throw new CupException("Wystapil blad podczas zapisywania do pliku");
		}
	}
	
	public static void printToFile(PrintWriter writer, Cup cup) throws CupException {
		try {
			writer.println(cup.name + "/" + cup.material + "/" + cup.size + "/" + cup.cost);
		} catch (Exception ex) {
			throw new CupException("Wystapil blad podczas zapisywania do pliku");
		}
	}
	
	public static Cup readFromFile(String filename) throws CupException {
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(filename)))) {
			String line = reader.readLine();
			String[] specifics = line.split("/");
			Cup cup = new Cup(specifics[0], Double.parseDouble(specifics[3]));
			cup.setType(specifics[1]);
			cup.setSize(Integer.parseInt(specifics[2]));
			return cup;
		} catch (FileNotFoundException ex) {
			throw new CupException("Plik nie zostal odnaleziony");
		} catch (IOException ex) {
			throw new CupException("Blad czytania z pliku");
		}
	}
	
	public static Cup readFromFile(final BufferedReader reader) throws CupException {
		try {
			String line = reader.readLine();
			if (line == null)
				return null;
			final String[] specifics = line.split("/");
			final Cup cup = new Cup(specifics[0], Double.parseDouble(specifics[3]));
			cup.setType(specifics[1]);
			cup.setSize(Integer.parseInt(specifics[2]));
			return cup;
		} catch (FileNotFoundException ex) {
			throw new CupException("Plik nie zostal odnaleziony");
		} catch (IOException ex) {
			throw new CupException("Blad czytania z pliku");
		}
	}
	
	/* Implementacja interfejsu Serializable */
	public static void writeObject(String filename, Cup cup) throws CupException {
		try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
			outputStream.writeObject(cup);
		} catch (Exception e) {
			throw new CupException("Blad podczas serializacji");
		}
	}
	
	
	public static Cup readObject(String filename) throws CupException {
		try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(filename))) {
            Cup tmpCup = (Cup)input.readObject();
            return tmpCup;
        } catch(Exception e) {
        	throw new CupException("Blad podczas deserializacji");
        }
	}
	/* Konice implementacji interfejsu Serializable */
} // koniec klasy Cup
