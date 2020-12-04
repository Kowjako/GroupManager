import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/*
 * Program: Okno dialogowe umozliwiajaca tworzenie i modyfikowanie
 * 			pojedynczej grupy obiektow Cup.
 *    Plik: GroupOfCupWindowDialog.java
 *          
 *   Autor: Uladzimir Kaviaka 257276
 *    Data: 3 grudnia 2020 r.
 *
 *
 * Klasa CupWindowDialog umozliwia tworzenie nowych okien
 * do edycji oraz wprowadzenia danych dla obiektow klasy Cup
 */
public class GroupOfCupWindowDialog extends JDialog implements ActionListener {
	
	private static final String GREETING_MESSAGE =
			"Program do zarzadzania pojedyncza grupa " +
	        "- wersja okienkowa\n\n" +
	        "Autor: Uladzimir Kaviaka\n" +
			"Data:  30 listopada 2020 r.\n";
	private static final long serialVersionUID = 1L;
	Font font = new Font("Comic Sans", Font.BOLD, 12);
	
	Cup exampleCup;
	
	private GroupOfCup group;

	JLabel nameLabel = new JLabel("Nazwa grupy: ");
	JLabel typeLabel = new JLabel("Rodzaj kolekcji: ");
	JTextField nameField = new JTextField(10);
	JTextField typeField = new JTextField(10);

	JMenuBar menuBar = new JMenuBar();
	JMenu editBar = new JMenu("Edit group");
	JMenu sortBar = new JMenu("Sorting");
	JMenu settingBar = new JMenu("Properties");
	JMenu authorBar = new JMenu("Author");

	JMenuItem addCup = new JMenuItem("Dodaj nowa filizanke");
	JMenuItem editCup = new JMenuItem("Edytuj filizanke");
	JMenuItem deleteCup = new JMenuItem("Usun filizanke");
	JMenuItem writeCup = new JMenuItem("Zapisz filizanke");
	JMenuItem readCup = new JMenuItem("Wczytaj filizanke");
	
	JMenuItem alfabetSort = new JMenuItem("Sortowanie alfabetowe");
	JMenuItem costSort = new JMenuItem("Sortowanie po cenie");
	JMenuItem materialSort = new JMenuItem("Sortowanie materialowe");
	
	
	JMenuItem nameGroup = new JMenuItem("Zmien nazwe grupy");
	JMenuItem typeGroup = new JMenuItem("Zmien typ kolekcji");
	
	JMenuItem author = new JMenuItem("Autor");
	
	JComboBox<GroupType> groupTypeBox = new JComboBox<GroupType>(GroupType.values());
	
	ViewGroupOfCup currentGroup;
	
	public GroupOfCupWindowDialog(Window parent, GroupOfCup input_group) {
		super(parent, ModalityType.DOCUMENT_MODAL);
		setTitle("GroupOfCup zardzanie pojedyncza grupa");
		setSize(500, 500);
		setLayout(new BorderLayout());
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setJMenuBar(menuBar);

		menuBar.add(editBar);
		menuBar.add(sortBar);
		menuBar.add(settingBar);
		menuBar.add(authorBar);
		
		editBar.add(addCup);
		editBar.add(editCup);
		editBar.add(deleteCup);
		editBar.addSeparator();
		editBar.add(writeCup);
		editBar.add(readCup);
		
		sortBar.add(alfabetSort);
		sortBar.add(costSort);
		sortBar.add(materialSort);
		
		settingBar.add(nameGroup);
		settingBar.add(typeGroup);
		
		authorBar.add(author);
		
		addCup.addActionListener(this);
		editCup.addActionListener(this);
		deleteCup.addActionListener(this);
		writeCup.addActionListener(this);
		readCup.addActionListener(this);
		alfabetSort.addActionListener(this);
		costSort.addActionListener(this);
		materialSort.addActionListener(this);
		nameGroup.addActionListener(this);
		typeGroup.addActionListener(this);
		author.addActionListener(this);
		
		group=input_group; /*Zadanie grupy */
		
		
		currentGroup = new ViewGroupOfCup(group,400,300);
		currentGroup.refreshView();
		
		JPanel panel = new JPanel(null);
		panel.setBackground(Color.pink);
		
		nameLabel.setBounds(50,5,100,15);
		panel.add(nameLabel);
		nameField.setBounds(150,5,250,20);
		nameField.setText(group.getName());
		nameField.setEditable(false);
		panel.add(nameField);
		
		typeLabel.setBounds(50,30,100,15);
		panel.add(typeLabel);
		typeField.setBounds(150,30,250,20);
		typeField.setText(group.getType().toString());
		typeField.setEditable(false);
		panel.add(typeField);
		
		currentGroup.setBounds(10,55,460,370);
		panel.add(currentGroup);
		
		setContentPane(panel);
		setVisible(true);
	}

	public static GroupOfCup createNewGroupOfCup(Window parent) {
		final String name = JOptionPane.showInputDialog(parent, "Podaj nazwe nowej grupy: ");
		if (name == null || name.equals("")) {
			return null;
		}
		Object[] types = GroupType.values();
		final GroupType type = (GroupType)JOptionPane.showInputDialog(null, "Wybierz typ kolekcji :", "Zmien typ", 3, null, types, null);
		if (type == null) {
			return null;
		}
		GroupOfCup newGroup;
		try {
			newGroup = new GroupOfCup(type, name);
		} catch (CupException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Blad", 0);
			return null;
		}
		GroupOfCupWindowDialog dialog = new GroupOfCupWindowDialog(parent, newGroup);
		return dialog.group;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		try {
			if (source == addCup) {
				Cup tmpCup = CupWindowDialog.createNewCup(this);
				if (tmpCup != null)
					group.add(tmpCup);
			}
			if (source == editCup) {
				int index = currentGroup.getSelectedIndex();
				if (index >= 0) {
					Iterator<Cup> iterator = group.iterator();
					while (index-- > 0)
						iterator.next();
					CupWindowDialog.changeCupParameters(this, iterator.next());
				}
			}
			if (source == deleteCup) {
				int index = currentGroup.getSelectedIndex();
				if (index >= 0) {
					Iterator<Cup> iterator = group.iterator();
					while (index-- >= 0)
						iterator.next();
					iterator.remove();
				}
			}
			if (source == author) {
				JOptionPane.showMessageDialog(this, GREETING_MESSAGE);
			}
			if (source == alfabetSort) {
				try {
					group.sortName();
				} catch (CupException e) {
					JOptionPane.showMessageDialog(this, e.getMessage(), "Cos poszlo nie tak",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			if (source == costSort) {	
				try {
					group.sortCost();
				} catch (CupException e) {
					JOptionPane.showMessageDialog(this, e.getMessage(), "Cos poszlo nie tak",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			if (source == materialSort) {
				try {
					group.sortMaterial();
				} catch (CupException e) {
					JOptionPane.showMessageDialog(this, e.getMessage(), "Cos poszlo nie tak",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			if (source == nameGroup) {
				String groupName = JOptionPane.showInputDialog("Wpisz nowa nazwe grupy");
				if (groupName != "" && groupName != null) {
					group.setName(groupName);
					nameField.setText(groupName);
				} else
					return;
			}
			if (source == typeGroup) {
				Object[] types = GroupType.values();
		        final GroupType type = (GroupType)JOptionPane.showInputDialog(null, "Wybierz typ kolekcji :", "Zmien typ", 3, null, types, group.getType());
		        if (type == null) {
                    return;
                }
                this.group.setType(type);
                this.typeField.setText(type.toString());
			}
			if(source == writeCup) {
				int index = currentGroup.getSelectedIndex();
				if (index >= 0) {
					Iterator<Cup> iterator = group.iterator();
					while (index-- > 0) {
						iterator.next();
					}
                    Cup cup = iterator.next();
                    JFileChooser filechooser = new JFileChooser(".");
                    int returnVal2 = filechooser.showSaveDialog(this);
                    if (returnVal2 == 0) {
                        Cup.printToFile(filechooser.getSelectedFile().getName(), cup);
                    }
                }
			}
			if(source == readCup) {
				JFileChooser chooser = new JFileChooser(".");
                int returnVal = chooser.showOpenDialog(this);
                if (returnVal == 0) {
                    Cup cup = Cup.readFromFile(chooser.getSelectedFile().getName());
                    group.add(cup);
                }
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Blad", 0);
		}
		currentGroup.refreshView();
	}

}
/* Klasa ViewGroupList autor: Pawel Rogalinski*/
class ViewGroupOfCup extends JScrollPane {
private static final long serialVersionUID = 1L;

	private GroupOfCup list;
	private JTable table;
	private DefaultTableModel tableModel;

	public ViewGroupOfCup(GroupOfCup list, int width, int height){
		this.list = list;
		setPreferredSize(new Dimension(width, height));
		setBorder(BorderFactory.createTitledBorder("Lista osob:"));

		String[] tableHeader = { "Nazwa", "Cena", "Pojemnosc", "Material" };
		tableModel = new DefaultTableModel(tableHeader, 0);
		table = new JTable(tableModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false; // Blokada mo¿liwoœci edycji komórek tabeli
			}
		};
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(true);
		setViewportView(table);
	}

	void refreshView(){
		tableModel.setRowCount(0);
		for (Cup tmpCup : list) {
			if (tmpCup != null) {
				String[] row = { tmpCup.getName(),Double.toString(tmpCup.getCost()),Integer.toString(tmpCup.getSize()),tmpCup.getType().toString() };
				tableModel.addRow(row);
			}
		}
	}
	
	int getSelectedIndex(){
		int index = table.getSelectedRow();
		if (index<0) {
			JOptionPane.showMessageDialog(this, "Zaden element grupy nie jest zaznaczony", "Ostrzezenie", JOptionPane.ERROR_MESSAGE);
		}
		return index;
	}

} // koniec klasy ViewGroupList
