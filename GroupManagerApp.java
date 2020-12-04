import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/*
 * Program: Aplikacja okienkowa z GUI, pozwala na 
 * 			zarzadzanie grupami obietkow Cup
 *    Plik: GroupManagerApp.java
 *
 *   Autor: Uladzimir Kaviaka 257276
 *    Data: 3 grudnia 2020 r.
 */

public class GroupManagerApp extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final String GREETING_MESSAGE =
			"Program do zarzadzania grupami osob " +
	        "- wersja okienkowa\n\n" +
	        "Autor: Uladzimir Kaviaka\n" +
			"Data:  30 listopad 2020\n";

	public static void main(String[] args) {
		new GroupManagerApp();
	}

	private List<GroupOfCup> currentList = new ArrayList<GroupOfCup>();

	JMenuBar menuBar = new JMenuBar();
	JMenu menuGroups = new JMenu("Grupy");
	JMenu menuAbout = new JMenu("O programie");

	JMenuItem menuNewGroup = new JMenuItem("Nowa grupa");
	JMenuItem menuEditGroup = new JMenuItem("Edytuj grupe");
	JMenuItem menuDeleteGroup = new JMenuItem("Usun gupe");
	JMenuItem menuLoadGroup = new JMenuItem("Wczytaj grupe z pliku");
	JMenuItem menuSaveGroup = new JMenuItem("Zapisz grupe do pliku");

	JMenuItem menuAuthor = new JMenuItem("Autor");

	ViewGroupList viewList;

	public GroupManagerApp() {
		setLayout(new BorderLayout());
		setTitle("GroupManager - zarzadzanie grupami obiektow Cup");
		setSize(450, 400);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setJMenuBar(menuBar);
		menuBar.add(menuGroups);
		menuBar.add(menuAbout);

		menuGroups.add(menuNewGroup);
		menuGroups.add(menuEditGroup);
		menuGroups.add(menuDeleteGroup);
		menuGroups.addSeparator();
		menuGroups.add(menuLoadGroup);
		menuGroups.add(menuSaveGroup);

		menuAbout.add(menuAuthor);
		
		menuNewGroup.addActionListener(this);
		menuEditGroup.addActionListener(this);
		menuDeleteGroup.addActionListener(this);
		menuLoadGroup.addActionListener(this);
		menuSaveGroup.addActionListener(this);
		menuAuthor.addActionListener(this);
		
		viewList = new ViewGroupList(currentList, 400, 250);
		viewList.refreshView();
		
		JPanel panel = new JPanel(null);
		panel.setBackground(Color.pink);
		viewList.setBounds(10,10,410,320);
		panel.add(viewList);

		setContentPane(panel);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		try {
			if (source == menuNewGroup) {
				GroupOfCup group = GroupOfCupWindowDialog.createNewGroupOfCup(this);
				if (group != null) {
					currentList.add(group);
				}
			}

			if (source == menuEditGroup) {
				int index = viewList.getSelectedIndex();
				if (index >= 0) {
					Iterator<GroupOfCup> iterator = currentList.iterator();
					while (index-- > 0)
						iterator.next();
					new GroupOfCupWindowDialog(this, iterator.next());
				}
			}

			if (source == menuDeleteGroup) {
				int index = viewList.getSelectedIndex();
				if (index >= 0) {
					Iterator<GroupOfCup> iterator = currentList.iterator();
					while (index-- >= 0)
						iterator.next();
					iterator.remove();
				}
			}

			if (source == menuLoadGroup) {
				JFileChooser chooser = new JFileChooser(".");
				int returnVal = chooser.showOpenDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					GroupOfCup group = GroupOfCup.readFromFile(chooser.getSelectedFile().getName());
					currentList.add(group);
				}
			}

			if (source == menuSaveGroup) {
				int index = viewList.getSelectedIndex();
				if (index >= 0) {
					Iterator<GroupOfCup> iterator = currentList.iterator();
					while (index-- > 0)
						iterator.next();
					GroupOfCup group = iterator.next();

					JFileChooser chooser = new JFileChooser(".");
					int returnVal = chooser.showSaveDialog(this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						GroupOfCup.printToFile( chooser.getSelectedFile().getName(), group );
					}
				}
			}

			if (source == menuAuthor) {
				JOptionPane.showMessageDialog(this, GREETING_MESSAGE);
			}

		} catch (CupException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Blad", JOptionPane.ERROR_MESSAGE);
		}

		viewList.refreshView();
	}

} 
// koniec klasy GroupManagerApp



/*
 * Pomocnicza klasa do wyswietlania grupy list autor: Pawel Rogalinski
 */
class ViewGroupList extends JScrollPane {
	private static final long serialVersionUID = 1L;

	private List<GroupOfCup> list;
	private JTable table;
	private DefaultTableModel tableModel;

	public ViewGroupList(List<GroupOfCup> list, int width, int height) {
		this.list = list;
		setPreferredSize(new Dimension(width, height));
		setBorder(BorderFactory.createTitledBorder("Lista grup:"));

		String[] tableHeader = {"Nazwa grupy", "Typ kolekcji", "Liczba filizanek"};
		tableModel = new DefaultTableModel(tableHeader, 0);
		table = new JTable(tableModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false; // Brak edycji komorki
			}
		};
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(true);
		setViewportView(table);
	}

	void refreshView() {
		tableModel.setRowCount(0);
		for (GroupOfCup group : list) {
			if (group != null) {
				String[] row = { group.getName(), group.getType().toString(), "" + group.size() };
				tableModel.addRow(row);
			}
		}
	}

	int getSelectedIndex() {
		int index = table.getSelectedRow();
		if (index < 0) {
			JOptionPane.showMessageDialog(this, "Trzeba zaznaczyc grupe.", "Blad", JOptionPane.ERROR_MESSAGE);
		}
		return index;
	}

} // koniec klasy ViewGroupList
//Koniec klasy GroupManagerApp