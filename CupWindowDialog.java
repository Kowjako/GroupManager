import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


/*
 * Program: Aplikacja okienkowa z GUI, która umo¿liwia testowanie 
 *          operacji wykonywanych na obiektach klasy Cup.
 *    Plik: CupWindowDialog.java
 *          
 *   Autor: Uladzimir Kaviaka 257276
 *    Data: 16 listopad 2020 r.
 *
 *
 * Klasa CupWindowDialog umozliwia tworzenie nowych okien
 * do edycji oraz wprowadzenia danych dla obiektow klasy Cup
 */

public class CupWindowDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	Font font = new Font("Comic Sans", Font.BOLD, 12);
	
	private Cup currentCup;
	
	JLabel nameLabel = new JLabel("Nazwa: ");
	JLabel sizeLabel = new JLabel("Pojemnosc: ");
	JLabel costLabel = new JLabel("Cena: ");
	JLabel materialLabel = new JLabel("Material: ");
	
	JTextField nameField = new JTextField(10);
	JTextField sizeField = new JTextField(10);
	JTextField costField = new JTextField(10);
	JComboBox<Material> materialBox = new JComboBox<Material>(Material.values());
	
	JButton OKButton = new JButton("Zatwierdz");
	JButton ExitButton = new JButton("Cofnij");
	
	public CupWindowDialog(Window parent, Cup cup) {
		super(parent, Dialog.ModalityType.DOCUMENT_MODAL);
		
		setLayout(new BorderLayout());
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); /* generacja okna */
		setSize(300, 230);
		setLocationRelativeTo(parent);
		
		this.currentCup = cup;
		
		if (currentCup == null) {
			setTitle("Utworzenie nowej filizanki");
		} else {
			setTitle(currentCup.toString());
			nameField.setText(currentCup.getName());
			sizeField.setText(Integer.toString((currentCup.getSize())));
			costField.setText("" + currentCup.getCost());
			materialBox.setSelectedItem(currentCup.getType());
		}
		
		OKButton.addActionListener(this);
		ExitButton.addActionListener(this);

		JPanel panel = new JPanel(null); /* utowrzenie nowego FlayLayout */
		panel.setBackground(Color.PINK);

		nameLabel.setBounds(10, 10, 100, 10);
		sizeLabel.setBounds(10, 38, 100, 15);
		costLabel.setBounds(10, 70, 100, 10);
		materialLabel.setBounds(10, 100, 100, 10);
		nameField.setBounds(120, 5, 150, 20);
		sizeField.setBounds(120, 35, 150, 20);
		costField.setBounds(120, 65, 150, 20);
		materialBox.setBounds(120,95,150,20);
		OKButton.setBounds(10, 130, 260, 24);
		ExitButton.setBounds(10, 160, 260, 24);
		
		panel.add(nameLabel);
		panel.add(nameField);

		panel.add(sizeLabel);
		panel.add(sizeField);

		panel.add(costLabel);
		panel.add(costField);

		panel.add(materialLabel);
		panel.add(materialBox);

		panel.add(OKButton);
		panel.add(ExitButton);
		
		setContentPane(panel); //umieszczenie panelu w oknie dialogowym
		setVisible(true); //widocznosc okna przy blokowaniu okna rodzica
		
	}
	@Override
	public void actionPerformed(ActionEvent arg) {
		Object sender = arg.getSource();
		if (sender == OKButton) {
			try {
				if (currentCup == null) {
					currentCup = new Cup(nameField.getText(), Double.parseDouble(costField.getText()));
				} else {
					currentCup.setName(nameField.getText());
					currentCup.setCost(Double.parseDouble(costField.getText()));
				}
				currentCup.setSize(Integer.parseInt(sizeField.getText()));
				currentCup.setType((Material) materialBox.getSelectedItem());
				dispose();
			} catch (CupException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Cos poszlo nie tak", JOptionPane.ERROR_MESSAGE);
			}
		}
		if (sender == ExitButton)
			dispose();
	}

	public static Cup createNewCup(Window parent) {
		CupWindowDialog dialog = new CupWindowDialog(parent, null);
		return dialog.currentCup;
	}

	public static void changeCupParameters(Window parent, Cup cup) {
		new CupWindowDialog(parent, cup);
	}

}
// Koniec klasy CupWindowDialog