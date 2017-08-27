package com.dave.estrazioneinterrogati;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Font;
import java.awt.Color;
import org.jasypt.util.text.BasicTextEncryptor;
import com.dave.libreriabase.LibreriaBase;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Principale {

	static JFrame finestraPrincipale;
	static JButton pulsanteEstrai;
	static JEditorPane nomiStampati;
	static JButton pulsanteResetta;
	static JScrollPane scrollBar;
	static JLabel lblEstrazioneNomi;
	static ImageIcon estrazioneInCorso;

	public static void main(String[] args) {

		//Dichiarazione variabili
		Random rnd = new Random();
		File nomiAlunni = new File(System.getenv("APPDATA") + "/fltk.org/" + "fltk.prefs");
		List<String> listaAlunni = new ArrayList<String>();

		initialize();

		LibreriaBase.cambiaStileGUI();

		BasicTextEncryptor ekrittik = new BasicTextEncryptor();
		String reload = "restart";
		ekrittik.setPassword(reload);

		//Inizializzazione programma
		if (nomiAlunni.exists() && ottieniNumeroRighe(nomiAlunni) > 0){

			aggiungiAlunni(listaAlunni, nomiAlunni, ekrittik);

			finestraPrincipale.setVisible(true);

		} if (!nomiAlunni.exists()){

			nomiAlunni.getParentFile().mkdirs(); 

			try {
				nomiAlunni.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			LibreriaBase.nascondiFile(nomiAlunni);

		} if (ottieniNumeroRighe(nomiAlunni) == 0){

			finestraPrincipale.setVisible(false);

			AggiungiAlunni.aggiungiAlunni(nomiAlunni, listaAlunni);
		}

		/*("Ajola Ludovica", "Alessandro Arcangeli", "Andrea Pelliccioni", "Andrea Tomaciello",
		"Antonio Puzalkov", "Chiara Perugini", "Filippo Casadei", "Francesco Santullo", "Gabriele Santini", 
		"Gaetano D'Astice", "Luca Barbieri", "Luca Sanchini", "Marco Tonti", "Matteo Guglielmetti", 
	    "Nicholas Tamagnini", "Nicola Paolone", "Oscar Semprucci", "Riccardo Raimondi", "Michele Tombesi", 
	    "Matteo Buldrini", "Matteo Maggioli", "Davide Venturini");*/

		//Pulsante che estrae un nome
		pulsanteEstrai.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (listaAlunni.size() > 0 && lblEstrazioneNomi.getText().toString().equals("Pronto!")){

					lblEstrazioneNomi.setText("Estrazione nome...");
					lblEstrazioneNomi.setIcon(estrazioneInCorso);

					Timer t = new Timer();
					TimerTask tm = new TimerTask() {
						@Override
						public void run() {
							
							int numero = rnd.nextInt(listaAlunni.size());
							
							nomiStampati.setText(nomiStampati.getText().toString() + listaAlunni.get(numero) + "\r\n");

							listaAlunni.remove(numero);

							lblEstrazioneNomi.setText("Pronto!");
							lblEstrazioneNomi.setIcon(null);	
						}
					};
					t.schedule(tm, 2000);

				} else if (listaAlunni.size() <= 0){

					pulsanteEstrai.setText("Finito!");
				}
			}
		});

		//Pulsante che estrae un nome
		pulsanteResetta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				JPasswordField campo = new JPasswordField(10);

				int azione = JOptionPane.showConfirmDialog(finestraPrincipale, campo, "Inserisci la password:",JOptionPane.OK_CANCEL_OPTION);

				if (azione == JOptionPane.OK_OPTION){

					String inserita = String.copyValueOf(campo.getPassword());

					if (inserita.equals(reload)){

						nomiAlunni.delete();

						JOptionPane.showMessageDialog(finestraPrincipale, "Al prossimo avvio potrai re-inserire i nomi della classe");

						System.exit(0);

					} else {

						JOptionPane.showMessageDialog(finestraPrincipale, "Password sbagliata!");
					}
				} 
			}
		});

		finestraPrincipale.getRootPane().setDefaultButton(pulsanteEstrai);
		pulsanteEstrai.requestFocus();
	}

	public static void initialize(){

		//Creo l'interfaccia
		finestraPrincipale = new JFrame();
		finestraPrincipale.setFont(new Font("Times New Roman", Font.BOLD, 14));
		finestraPrincipale.setResizable(false);
		finestraPrincipale.setBounds(100, 100, 212, 458);
		finestraPrincipale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		finestraPrincipale.getContentPane().setLayout(null);	

		nomiStampati = new JEditorPane();
		nomiStampati.setFont(new Font("Times New Roman", Font.BOLD, 14));
		nomiStampati.setEditable(false);
		nomiStampati.setBounds(10, 11, 185, 136);
		finestraPrincipale.getContentPane().add(nomiStampati);

		scrollBar = new JScrollPane(nomiStampati);
		scrollBar.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollBar.setBounds(10, 45, 185, 240);
		finestraPrincipale.getContentPane().add(scrollBar);

		pulsanteEstrai = new JButton("Estrai");
		pulsanteEstrai.setFont(new Font("Tahoma", Font.BOLD, 16));
		pulsanteEstrai.setBackground(Color.RED);
		pulsanteEstrai.setBounds(10, 296, 185, 85);
		finestraPrincipale.getContentPane().add(pulsanteEstrai);

		pulsanteResetta = new JButton("Resetta i nomi");
		pulsanteResetta.setBounds(10, 11, 185, 23);
		pulsanteResetta.setBackground(Color.GRAY);
		finestraPrincipale.getContentPane().add(pulsanteResetta);

		estrazioneInCorso = new ImageIcon(Principale.class.getResource("/ajax-loader.gif"));
		lblEstrazioneNomi = new JLabel("Pronto!");
		lblEstrazioneNomi.setHorizontalAlignment(SwingConstants.CENTER);
		lblEstrazioneNomi.setFont(new Font("Tahoma", Font.ITALIC, 12));
		lblEstrazioneNomi.setBounds(10, 390, 185, 28);
		finestraPrincipale.getContentPane().add(lblEstrazioneNomi);
	}

	//Metodo che restituisce il numero degli alunni da un file
	public static int ottieniNumeroRighe(File fileDaLeggere){
		//Creo e inizializzo la variabile che contiene il numero degli alunni
		int numeroAlunni = 0;

		try {
			//Inizializzo i controlli
			FileInputStream lettoreIniziale = new FileInputStream(fileDaLeggere);
			BufferedReader lettoreFinale = new BufferedReader(new InputStreamReader(lettoreIniziale));

			//Creo e inizializzo la variabile che controlla se la linea è piena o vuota
			String lineaPienaVuota;

			while(true){
				lineaPienaVuota = lettoreFinale.readLine();
				if (lineaPienaVuota != null){
					numeroAlunni++;
				}
				else{
					lettoreFinale.close();
					break;
				}
			}
		}
		catch (IOException Errore) {
			JOptionPane.showMessageDialog(null, "Impossibile interagire con il file " + fileDaLeggere + ". Errore: \n" + Errore);
		}
		return numeroAlunni;
	} 

	//Metodo che aggiunge elementi alla list per ogni riga del file
	public static void aggiungiAlunni(List<String> listaNomi, File fileDaLeggere, BasicTextEncryptor ekrittik){
		try {
			//Inizializzo i controlli
			FileInputStream lettoreIniziale = new FileInputStream(fileDaLeggere);
			BufferedReader lettoreFinale = new BufferedReader(new InputStreamReader(lettoreIniziale));

			while(true){
				String nomeAlunno = lettoreFinale.readLine();
				if (nomeAlunno != null){
					listaNomi.add(ekrittik.decrypt(nomeAlunno));
				}
				else{
					lettoreFinale.close();
					break;
				}
			}
		}
		catch (IOException Errore) {
			JOptionPane.showMessageDialog(null, "Impossibile interagire con il file " + fileDaLeggere + ". Errore: \n" + Errore);
		}
	} 
}


