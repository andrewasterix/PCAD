package client.logic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import client.worker.PrenotaEventoWorker;
import eventi.Evento;
import inteface.ServerInterface;

public class ClientGUI extends JFrame {

    private Client client;

    private JLabel infoText;
    private JScrollPane scrollInfoPane;
    private JTable eventiList;

    public ClientGUI(ServerInterface server, Client client) throws RemoteException {
        super("Client");

        this.client = client;
        this.client.setFrame(this);

        server.registerCallBack(client);
        
        Image img = Toolkit.getDefaultToolkit().getImage("src\\client\\img\\client.png");

        setTitle("Client");
        setResizable(false);
        setSize(800, 600);
        setIconImage(img);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
                try {
                    server.unregisterCallBack(client);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
		    }
		});

        /* PANNELLO INFOBOX */
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder((Border) new LineBorder(Color.BLACK, 1));
        infoPanel.setBounds(10, 10, 315, 135);
        infoPanel.setToolTipText("Client Info");
        infoPanel.setLayout(null);
        getContentPane().add(infoPanel);

        /* ELEMENTI DEL PANNELLO INFOBOX */
        infoText = new JLabel(client.getInfo());
        infoText.setBounds(5, 5, 305, 125);
        infoText.setVerticalAlignment(SwingConstants.TOP);

        scrollInfoPane = new JScrollPane(infoText);
        scrollInfoPane.setBounds(5, 5, 305, 125);
        scrollInfoPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        /* Aggiunta al Pannello INFOBOX */
        infoPanel.add(scrollInfoPane);

        /* PANNELLO GESTIONE EVENTI */
        JPanel gestioneEventiPanel = new JPanel();
        gestioneEventiPanel.setBorder((Border) new LineBorder(Color.BLACK, 1));
        gestioneEventiPanel.setBounds(335, 10, 435, 135);
        gestioneEventiPanel.setToolTipText("Gestione Eventi");
        gestioneEventiPanel.setLayout(null);
        getContentPane().add(gestioneEventiPanel);

        /* ELEMENTI DEL PANNELLO GESTIONE EVENTI */
        JLabel gestioneEventiLabel = new JLabel("Prenotazione Evento");
        gestioneEventiLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        gestioneEventiLabel.setToolTipText("Gestione Eventi");
        gestioneEventiLabel.setBounds(5, 5, 190, 40);

        JTextField nomeEventoField = new JTextField("Nome Evento");
        nomeEventoField.setBounds(5, 55, 140, 30);
        nomeEventoField.setToolTipText("Nome Evento");
        nomeEventoField.setColumns(25);

        JTextField numeroPostiField = new JTextField("Numero Posti");
        numeroPostiField.setBounds(5, 90, 140, 30);
        numeroPostiField.setToolTipText("Numero Posti");
        numeroPostiField.setColumns(25);

        Image imgTicket = Toolkit.getDefaultToolkit().getImage("src\\client\\img\\ticket.png");
        imgTicket = imgTicket.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JButton addButton = new JButton(new ImageIcon(imgTicket));
        addButton.setText("Prenota");
        addButton.setToolTipText("Prenota");
        addButton.setHorizontalTextPosition(SwingConstants.LEADING);
        addButton.setVerticalTextPosition(SwingConstants.CENTER);
        addButton.setBounds(150, 50, 135, 80);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String nomeEvento = nomeEventoField.getText().trim();
                String postiRichiesti = numeroPostiField.getText();

                if (nomeEvento.equals("") || nomeEvento.equals("Nome Evento")) {
                    setInfoText("<font color=\"red\">Inserire il nome dell'Evento</font>");
                    return;
                }

                if (postiRichiesti.equals("") || postiRichiesti.equals("Numero Posti")) {
                    setInfoText("<font color=\"red\">Inserire il numero di posti liberi</font>");
                    return;
                }

                if (nomeEvento.isEmpty() || nomeEvento.isBlank()) {
                    setInfoText("<font color=\"red\">Inserire il nome dell'Evento</font>");
                    return;
                }

                if (postiRichiesti.isEmpty() || postiRichiesti.isBlank() || !postiRichiesti.matches("[0-9]+")) {
                    setInfoText("<font color=\"red\">Inserire il numero di posti liberi</font>");
                    return;
                }

                if (!client.getEventi().containsKey(nomeEvento)){
                    setInfoText("<font color=\"red\">L'Evento " +nomeEvento+ " non è presente!</font>");
                    return;
                }

                if (nomeEvento == null || nomeEvento.equals(""))
                    throw new IllegalArgumentException("Si vuole creare un evento con un nome nullo o vuoto!");

                int postiRichiestiInt = Integer.parseInt(postiRichiesti);
                if (client.getEventi().get(nomeEvento).getPostiLiberi() < postiRichiestiInt) {
                    setInfoText("Numero di posti disponibili non sufficiente!\"");;
                    return;
                }

                new PrenotaEventoWorker(nomeEvento, postiRichiestiInt, server, client).execute();
            }
        });

        /* Aggiunta al Pannello GESTIONE EVENTI */
        gestioneEventiPanel.add(gestioneEventiLabel);
        gestioneEventiPanel.add(nomeEventoField);
        gestioneEventiPanel.add(numeroPostiField);
        gestioneEventiPanel.add(addButton);

        /* PANNELLO EVENTI */
        JPanel eventiPanel = new JPanel();
        eventiPanel.setBorder((Border) new LineBorder(Color.BLACK, 1));
        eventiPanel.setBounds(10, 155, 760, 395);
        eventiPanel.setToolTipText("Eventi");
        eventiPanel.setLayout(null);
        getContentPane().add(eventiPanel);

        /* ELEMENTI DEL PANNELLO EVENTI */
        JButton refreshButton = new JButton("Aggiorna");
        refreshButton.setToolTipText("Aggiorna");
        refreshButton.setBounds(10, 5, 740, 40);

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    client.updateEventiPanel();
                    eventiList.revalidate();
                    eventiList.repaint();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        });

        eventiList = new JTable();
        eventiList.setBounds(10, 55, 740, 335);
        eventiList.setRowHeight(30);
        eventiList.setFont(new Font("Arial", Font.PLAIN, 15));
        eventiList.setFillsViewportHeight(true);
        eventiList.setShowGrid(true);
        eventiList.setGridColor(Color.BLACK);
        eventiList.setShowVerticalLines(true);
        eventiList.setShowHorizontalLines(true);
        eventiList.setRowSelectionAllowed(false);
        eventiList.setCellSelectionEnabled(false);
        eventiList.setDragEnabled(false);
        eventiList.setTableHeader(new JTableHeader(eventiList.getColumnModel()) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = 30;
                return d;
            }
        });

        JScrollPane scrollEventiList = new JScrollPane(eventiList);
        scrollEventiList.setBounds(10, 55, 740, 335);
        scrollEventiList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        /* Aggiunta al Pannello EVENTI */
        eventiPanel.add(refreshButton);
        eventiPanel.add(scrollEventiList);

        client.updateEventiPanel();
        setVisible(true);
    }

    public void setEventiList(ConcurrentHashMap<String, Evento> eventi) {

        NonEditableTableModel model = new NonEditableTableModel();
        model.addColumn("Nome Evento");
        model.addColumn("Posti Liberi");

        for (Map.Entry<String, Evento> entry : eventi.entrySet()) {
            String nomeEvento = entry.getKey();
            int postiLiberi = entry.getValue().getPostiLiberi();
            model.addRow(new Object[] { nomeEvento, postiLiberi });
        }

        if (SwingUtilities.isEventDispatchThread()) {
            eventiList.setModel(model);
            setInfoText("<font color=\"green\">Eventi aggiornati!</font>");
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    eventiList.setModel(model);
                    setInfoText("<font color=\"green\">Eventi aggiornati!</font>");
                }
            });
        }
    }

    /*
     * Modifica il valore della 'infoText': se sono in EDT modifica diretta,
     * altrimenti lascio che SwingUtilities avvi un Runnable per modificare il testo
     */
    public void setInfoText(String text) {
        if (SwingUtilities.isEventDispatchThread()) {
            String temp = infoText.getText();
            infoText.setText(temp + "<br/>" + text);

            // Recupera la JViewport associata alla JScrollPane
            JViewport viewport = scrollInfoPane.getViewport();

            // Sposta la visualizzazione nella parte inferiore del contenuto
            Rectangle rect = infoText.getBounds();
            rect.y = rect.height - viewport.getExtentSize().height;
            viewport.scrollRectToVisible(rect);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String temp = infoText.getText();
                    infoText.setText(temp + "<br/>" + text);
                    // Recupera la JViewport associata alla JScrollPane
                    JViewport viewport = scrollInfoPane.getViewport();

                    // Sposta la visualizzazione nella parte inferiore del contenuto
                    Rectangle rect = infoText.getBounds();
                    rect.y = rect.height - viewport.getExtentSize().height;
                    viewport.scrollRectToVisible(rect);
                }
            });
        }
    }

    private class NonEditableTableModel extends DefaultTableModel {
        public NonEditableTableModel() {
            super();
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            // Restituisce sempre false per rendere le celle non modificabili
            return false;
        }
    }
}
