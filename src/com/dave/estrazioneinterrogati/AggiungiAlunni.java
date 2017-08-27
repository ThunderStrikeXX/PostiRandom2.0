package com.dave.estrazioneinterrogati;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.jasypt.util.text.BasicTextEncryptor;
import com.dave.libreriabase.LibreriaBase;
import javax.swing.JLabel;

public class AggiungiAlunni {

	//Dichiarazione controlli globali
	static JFrame Alunni;
	static JButton pulsanteAggiungi; 
	static JComboBox<String> comboAlunni;
	static JButton pulsanteRimuovi;
	static JTextField nomeDaAggiungere;
	static BasicTextEncryptor ekrittik;
	static JLabel lblCriptazione;
	static ImageIcon iconaCaricamento;

	static List<String> listaRandomizzata;

	//Metodo di accesso della classe
	public static void aggiungiAlunni(File fileDaLeggere, List<String> listaAlunni){

		initialize(fileDaLeggere, listaAlunni);

		ekrittik = new BasicTextEncryptor();
		char[] reload = "restart".toCharArray();
		ekrittik.setPasswordCharArray(reload);

		iconaCaricamento = new ImageIcon(Principale.class.getResource("/ajax-loader.gif"));

		try {
			//Inizializzo i controlli
			FileInputStream LettoreIniziale = new FileInputStream(fileDaLeggere);
			BufferedReader LettoreFinale = new BufferedReader(new InputStreamReader(LettoreIniziale));

			while(true){
				String NomeAlunno = LettoreFinale.readLine();
				if (NomeAlunno != null){
					comboAlunni.addItem(NomeAlunno);
				}
				else{
					LettoreFinale.close();
					break;
				}
			}
		}
		catch (IOException Errore) {
			JOptionPane.showMessageDialog(null, "Impossibile interagire con il file " + fileDaLeggere + ". Errore: \n" + Errore);
		}

		LibreriaBase.cambiaStileGUI();

		//Pulsante per aggiungere un alunno
		pulsanteAggiungi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (!nomeDaAggiungere.getText().toString().isEmpty()){

					listaAlunni.add(nomeDaAggiungere.getText().toString());

					comboAlunni.addItem(nomeDaAggiungere.getText().toString());

					comboAlunni.setSelectedIndex(comboAlunni.getItemCount() - 1);

					nomeDaAggiungere.setText("");
				}	
			}
		});

		//Pulsante per rimuovere un alunno
		pulsanteRimuovi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (listaAlunni.size() != 0){

					listaAlunni.remove(comboAlunni.getSelectedIndex());

					comboAlunni.removeItemAt(comboAlunni.getSelectedIndex());
				}
			}
		});	
	}	

	//Metodo che inizializza la GUI
	/**
	 * @wbp.parser.entryPoint
	 */
	public static void initialize(File fileDaLeggere, List<String> listaAlunni) {

		Alunni = new JFrame();
		Alunni.setTitle("Inserisci i nomi e i cognomi:");
		Alunni.setResizable(false);
		Alunni.setBounds(100, 100, 399, 120);
		Alunni.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Alunni.getContentPane().setLayout(null);

		nomeDaAggiungere = new JTextField();
		nomeDaAggiungere.setBounds(10, 11, 211, 31);
		nomeDaAggiungere.setColumns(1);
		Alunni.getContentPane().add(nomeDaAggiungere);

		pulsanteAggiungi = new JButton("Aggiungi Alunno");
		pulsanteAggiungi.setBounds(231, 11, 152, 31);
		Alunni.getContentPane().add(pulsanteAggiungi);

		comboAlunni = new JComboBox<String>();
		comboAlunni.setBounds(10, 53, 211, 31);
		Alunni.getContentPane().add(comboAlunni);

		pulsanteRimuovi = new JButton("Elimina Alunno");
		pulsanteRimuovi.setBounds(231, 53, 152, 31);
		Alunni.getContentPane().add(pulsanteRimuovi);

		Alunni.getRootPane().setDefaultButton(pulsanteAggiungi);

		lblCriptazione = new JLabel("");
		lblCriptazione.setBounds(10, 95, 373, 36);
		Alunni.getContentPane().add(lblCriptazione);

		pulsanteAggiungi.requestFocus();
		Alunni.setVisible(true);

		Alunni.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent){

				//Mostro la ConfirmDialog di uscita e salvataggio
				int salvare = JOptionPane.showConfirmDialog(Alunni, "Vuoi salvare le modifiche?", "Salvare il file?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

				switch(salvare){
				case(JOptionPane.YES_OPTION):

					if (listaAlunni.size() != 0){

						//Randomizzo gli indici della list
						listaRandomizzata = randomLista(listaAlunni);

						//Elimino e ricreo il file (così vuoto)
						fileDaLeggere.delete();

						try {
							fileDaLeggere.createNewFile();
							
							LibreriaBase.nascondiFile(fileDaLeggere);

							PrintStream scrittore = new PrintStream(fileDaLeggere);


							//Scrivo nel file il contenuto della comboBox
							for (int i = 0; i < listaRandomizzata.size(); i++){

								scrittore.println(ekrittik.encrypt(listaRandomizzata.get(i)));
							}

							scrittore.close();
				
						} catch (IOException e) {
					
							e.printStackTrace();
						}

						JOptionPane.showMessageDialog(null, "Al prossimo avvio potrai estrarre i nomi inseriti!");

						System.exit(0);

					} else {

						JOptionPane.showMessageDialog(null, "Non sono presenti alunni...");
					}
				break;

				case(JOptionPane.NO_OPTION):

					System.exit(0);

				break;

				default:
					//Non fa niente
				}
			}
		});
	}

	//Metodo che randomizza la lista (non mio proprio :D)
	public static List<String> randomLista(List<String> listaAlunni){

		int index;
		String temp;
		Random random = new Random();
		for (int i = listaAlunni.size() - 1; i > 0; i--)
		{
			index = random.nextInt(i + 1);
			temp = listaAlunni.get(index);
			listaAlunni.set(index, listaAlunni.get(i));
			listaAlunni.set(i, temp);
		}

		return listaAlunni;
	}
}
