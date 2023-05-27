package server;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import server.eventi.Evento;
import server.worker.AddEventoWorker;
import server.worker.RemoveEventoWorker;

public class ServerGUI extends JFrame {

    public Server server;
    public Semaphore sem;

    private JLabel infoText;
    private JTable eventiList;
    private JScrollPane scrollInfoPane;

    public ServerGUI(Server server, Semaphore sem) throws RemoteException {
        super("Server");
        this.server = server;
        this.sem = sem;

        server.setFrame(this);
        this.setTitle("Server");
        this.setSize(800, 600);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        /* PANNELLO INFOBOX */
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder((Border) new LineBorder(Color.BLACK, 1));
        infoPanel.setBounds(10, 10, 315, 135);
        infoPanel.setToolTipText("Server Info");
        infoPanel.setLayout(null);
        getContentPane().add(infoPanel);

        /* ELEMENTI DEL PANNELLO INFOBOX */
        infoText = new JLabel(server.getInfo());
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
        JLabel GestioneEventiLabel = new JLabel("Gestione Eventi");
        GestioneEventiLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        GestioneEventiLabel.setToolTipText("Gestione Eventi");
        GestioneEventiLabel.setBounds(5, 5, 150, 40);

        JTextField nomeEventoField = new JTextField("Nome Evento");
        nomeEventoField.setBounds(5, 55, 140, 30);
        nomeEventoField.setToolTipText("Nome Evento");
        nomeEventoField.setColumns(25);

        JTextField numeroPostiField = new JTextField("Numero Posti");
        numeroPostiField.setBounds(5, 90, 140, 30);
        numeroPostiField.setToolTipText("Numero Posti");
        numeroPostiField.setColumns(25);

        JButton addButton = new JButton("Aggiungi Evento");
        addButton.setToolTipText("Aggiungi Evento");
        addButton.setBounds(150, 50, 135, 80);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String nomeEvento = nomeEventoField.getText();
                String postiLiberi = numeroPostiField.getText();

                if (nomeEvento.equals("") || nomeEvento.equals("Nome Evento")) {
                    setInfoText("<font color=\"red\">Inserire il nome dell'Evento</font>");
                    return;
                }

                if (postiLiberi.equals("") || postiLiberi.equals("Numero Posti")) {
                    setInfoText("<font color=\"red\">Inserire il numero di posti liberi</font>");
                    return;
                }

                if (nomeEvento.isEmpty() || nomeEvento.isBlank()) {
                    setInfoText("<font color=\"red\">Inserire il nome dell'Evento</font>");
                    return;
                }

                if (postiLiberi.isEmpty() || postiLiberi.isBlank()) {
                    setInfoText("<font color=\"red\">Inserire il numero di posti liberi</font>");
                    return;
                }

                int postiLiberiInt = Integer.parseInt(postiLiberi);
                if (nomeEvento == null || nomeEvento.equals(""))
                    throw new IllegalArgumentException("Si vuole creare un evento con un nome nullo o vuoto!");

                new AddEventoWorker(nomeEvento, postiLiberiInt, server).execute();
            }
        });

        JButton removeButton = new JButton("Rimuovi Evento");
        removeButton.setText("Rimuovi Evento");
        removeButton.setToolTipText("Rimuovi Evento");
        removeButton.setBounds(290, 50, 135, 80);

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String nomeEvento = nomeEventoField.getText();

                if (nomeEvento.equals("")) {
                    setInfoText("<font color=\"red\">Inserire il nome dell'Evento</font>");
                    return;
                }

                if (nomeEvento == null || nomeEvento.equals(""))
                    throw new IllegalArgumentException("Si vuole rimuovere un evento con un nome nullo o vuoto!");

                new RemoveEventoWorker(nomeEvento, server).execute();
            }
        });

        /* Aggiunta al Pannello GESTIONE EVENTI */
        gestioneEventiPanel.add(GestioneEventiLabel);
        gestioneEventiPanel.add(nomeEventoField);
        gestioneEventiPanel.add(numeroPostiField);
        gestioneEventiPanel.add(addButton);
        gestioneEventiPanel.add(removeButton);

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
                    server.updateEventiPanel();
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

        server.updateEventiPanel();
        this.setVisible(true);
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
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    eventiList.setModel(model);
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
