import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

/*
 * Program: Aplikacja okienkowa z GUI, która umozliwia zarzadzanie
 *          grupami obiektow klasy Cup.
 *    Plik: GroupOfCup.java
 *
 *   Autor: Uladzimir Kaviaka 257276
 *    Data: 3 grudnia 2020 r.
 */

/*
 *     W programie dostepne zbiory oraz listy:
 *      Listy: klasa Vector, klasa ArrayList, klasa LinnkedList;
 *      Zbiory: klasa TreeSet, klasa HashSet.
 */
enum GroupType {
	
	VECTOR("Lista -> Vector"),
	ARRAY_LIST("Lista -> ArrayList"),
	LINKED_LIST("Lista -> LinkedList"),
	HASH_SET("Zbior -> klasa HashSet"),
	TREE_SET("Zbior -> klasa TreeSet");

	String typeName;

	private GroupType(String type_name) {
		typeName = type_name;
	}

	@Override
	public String toString() {
		return typeName;
	}

	public static GroupType find(String type_name) {
		for (GroupType type : values()) {
			if (type.typeName.equals(type_name)) {
				return type;
			}
		}
		return null;
	}
	
	public Collection<Cup> createCollection() throws CupException {
		switch (this) {
		case VECTOR:      return new Vector<Cup>();
		case ARRAY_LIST:  return new ArrayList<Cup>();
		case HASH_SET:    return new HashSet<Cup>();
		case LINKED_LIST: return new LinkedList<Cup>();
		case TREE_SET:    return new TreeSet<Cup>();
		default:          throw new CupException("Nie istnieje takiej kolekcji");
		}
	}

}  //Koniec groupType




/*
 * Klasa GroupOfCup reprezentuje grupy filizanek, ktore opisane 
 * za pomoca trzech atrybutow name, type oraz collection:
 *     name - nazwa grupy
 *     type - typ kolekcji
 *     collection - kolekcja obiektów klasy Cup, w ktorej umieszczone obecne obiekty
 */
public class GroupOfCup implements Iterable<Cup>, Serializable{

	private static final long serialVersionUID = 1L;

	private String name;
	private GroupType type;
	private Collection<Cup> collection;


	public GroupOfCup(GroupType type, String name) throws CupException {
		setName(name);
		if (type==null){
			throw new CupException("Nieprawid³owy typ kolekcji.");
		}
		this.type = type;
		collection = this.type.createCollection();
	}


	public GroupOfCup(String type_name, String name) throws CupException {
		setName(name);
		GroupType type = GroupType.find(type_name);
		if (type==null){
			throw new CupException("Nieprawid³owy typ kolekcji.");
		}
		this.type = type;
		collection = this.type.createCollection();
	}


	public String getName() {
		return name;
	}


	public void setName(String name) throws CupException {
		if ((name == null) || name.equals(""))
			throw new CupException("Nazwa grupy nie zostala wprowadzona");
		this.name = name;
	}


	public GroupType getType() {
		return type;
	}


	public void setType(GroupType type) throws CupException {
		if (type == null) {
			throw new CupException("Typ kolekcji nie zostal odnaleziony");
		}
		if (this.type == type)
			return;
		Collection<Cup> lastCollection = collection;
		collection = type.createCollection();
		this.type = type;
		for (Cup tmpCup : lastCollection)
			collection.add(tmpCup);
	}


	public void setType(String type_name) throws CupException {
		for(GroupType type : GroupType.values()){
			if (type.toString().equals(type_name)) {
				setType(type);
				return;
			}
		}
		throw new CupException("Takiego typu kolekcji nie odnaleziono");
	}
	

	/*Rownowaznosc dla settera */
	public boolean add(Cup tmp) {
		return collection.add(tmp);
	}

	public Iterator<Cup> iterator() {
		return collection.iterator();
	}

	public int size() {
		return collection.size();
	}

	/*Sortowanie alfabetyczne */
	public void sortName() throws CupException {
		if (type == GroupType.HASH_SET || type == GroupType.TREE_SET) {
			throw new CupException("Sortowanie dziala tylko dla list");
		}
		Collections.sort((List<Cup>) collection); /* Za pomoca Comparable w klasie Cup */
	}
	/* Sortowanie po cenie */
	public void sortCost() throws CupException {
		if (type == GroupType.HASH_SET || type == GroupType.TREE_SET) {
			throw new CupException("Sortowanie dziala tylko dla list");
		}
		Collections.sort((List<Cup>) collection, new Comparator<Cup>() { /* implementacja anonimowej klasy */
			@Override
			public int compare(Cup obj1, Cup obj2) {
				if (obj1.getCost() < obj2.getCost())
					return -1;
				if (obj1.getCost() > obj2.getCost())
					return 1;
				return 0;
			}
		});
	}
	/* Sortowanie materialowe */
	public void sortMaterial() throws CupException {
		if (type == GroupType.HASH_SET || type == GroupType.TREE_SET) {
			throw new CupException("Sortowanie dziala tylko dla list");
		}
		Collections.sort((List<Cup>) collection, new Comparator<Cup>() {

			@Override
			public int compare(Cup obj1, Cup obj2) {
				return obj1.getType().toString().compareTo(obj2.getType().toString());
			}

		});
	}

	@Override
	public String toString() {
		return name + "  [" + type + "]";
	}

	public static void printToFile(String file_name, GroupOfCup group) throws CupException {
		try (PrintWriter writer = new PrintWriter(file_name)) {
			writer.println(group.getName()); /*zapis nazwy grupy */
			writer.println(group.getType()); /*zapis typu grupy */
			for (Cup cup : group.collection) /*zapis kazdego elementu */
				Cup.printToFile(writer, cup);
		} catch (FileNotFoundException e){
			throw new CupException("Nie odnaleziono pliku " + file_name);
		}
	}

	public static GroupOfCup readFromFile(String file_name) throws CupException {
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(file_name)))) {
			String group_name = reader.readLine();
			String type_name = reader.readLine();
			GroupOfCup groupOfCup = new GroupOfCup(type_name, group_name);
			Cup tmpCup;
			while((tmpCup = Cup.readFromFile(reader)) != null)
				groupOfCup.collection.add(tmpCup);
			return groupOfCup;
		} catch (FileNotFoundException e){
			throw new CupException("Nie ma takiego pliku");
		} catch (IOException e){
			throw new CupException("Blad przy odczytywaniu z pliku");
		}
	}	
} // koniec klasy GroupOfCup
