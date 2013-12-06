/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw.saarland.gamedev.nap.launcher;

import com.smartfoxserver.v2.exceptions.SFSException;
import de.htw.saarland.gamedev.nap.NetworkConstants;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.entities.Room;
import sfs2x.client.entities.User;
import sfs2x.client.requests.JoinRoomRequest;

/**
 * The Launcher Frame. Receives also network events for launcher based tasks
 * 
 * @author Pascal
 */
public class FrameLauncher extends javax.swing.JFrame implements IEventListener {
    
    private SmartFox sfClient;
    
    private DefaultListModel<String> usersInRoom;
    private DefaultListModel<String> availableRooms;
    
    /**
     * Creates new form FrameLauncher
     */
    public FrameLauncher(SmartFox sfClient) {
        this.sfClient = sfClient;
        
        sfClient.addEventListener(SFSEvent.CONNECTION_LOST, this);
        sfClient.addEventListener(SFSEvent.ROOM_JOIN, this);
        sfClient.addEventListener(SFSEvent.ROOM_JOIN_ERROR, this);
        sfClient.addEventListener(SFSEvent.SOCKET_ERROR, this);
        sfClient.addEventListener(SFSEvent.PUBLIC_MESSAGE, this);
        sfClient.addEventListener(SFSEvent.ROOM_ADD, this);
        sfClient.addEventListener(SFSEvent.ROOM_CREATION_ERROR, this);
        sfClient.addEventListener(SFSEvent.USER_ENTER_ROOM, this);
        sfClient.addEventListener(SFSEvent.USER_EXIT_ROOM, this);
        sfClient.addEventListener(SFSEvent.PING_PONG, this);
        
        usersInRoom = new DefaultListModel<>();
        availableRooms = new DefaultListModel<>();
        initComponents();
        
        //join lobby
        sfClient.enableLagMonitor(true);
        sfClient.send(new JoinRoomRequest(NetworkConstants.LOBBY_ROOM_NAME));
    }
    
    public void messageReceived(String from, String text) {
        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        
        textAreaChatLog.setText(textAreaChatLog.getText() + 
                "[" + formatter.format(new Date(System.currentTimeMillis())) + "] " +
                from + ": " + text + "\n");
    }
    
    @Override
    public void dispatch(BaseEvent be) throws SFSException {
        switch(be.getType()) {
            case SFSEvent.CONNECTION_LOST:
                JOptionPane.showMessageDialog(this,
                        "Connection to server lost!\n" + be.getArguments().get(NetworkConstants.ERROR_MESSAGE_KEY),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                
                System.exit(-1);
                break;
            case SFSEvent.ROOM_JOIN:
                // TODO show room info (game info if room isGame), update room user list
                break;
            case SFSEvent.ROOM_JOIN_ERROR:
                JOptionPane.showMessageDialog(this,
                        "Error joining a room!\n" + be.getArguments().get(NetworkConstants.ERROR_MESSAGE_KEY),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                break;
            case SFSEvent.SOCKET_ERROR:
                JOptionPane.showMessageDialog(this,
                        "There was a problem with your connection!\n" + be.getArguments().get(NetworkConstants.ERROR_MESSAGE_KEY),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                break;
            case SFSEvent.PUBLIC_MESSAGE:
                publicMessageReceived(be);
                break;
            case SFSEvent.ROOM_ADD:
                // TODO room added, add to room list
                break;
            case SFSEvent.ROOM_CREATION_ERROR:
                JOptionPane.showMessageDialog(this,
                        "Error creating room!\n" + be.getArguments().get(NetworkConstants.ERROR_MESSAGE_KEY),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                break;
            case SFSEvent.USER_ENTER_ROOM:
                // TODO user entered room add to users in room and add to team if room isGame
                break;
            case SFSEvent.USER_EXIT_ROOM:
                // TODO remove user from userlist of room and from the team if room isGame
                break;
            case SFSEvent.PING_PONG:
                pingReceived((int)be.getArguments().get(NetworkConstants.LAG_VALUE_KEY));
                break;
            default:
                System.out.println("Server Packet not handled: " + be.getType());
                break;
        }
    }
    
    private void pingReceived(final int lagValue) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblPing.setText(Integer.toString(lagValue) + " ms");
            }
        });
    }
    
    private void publicMessageReceived(BaseEvent be) {
        final String username = ((User)be.getArguments().get(NetworkConstants.SENDER_KEY)).getName();
        final String message = (String)be.getArguments().get(NetworkConstants.MESSAGE_KEY);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                messageReceived(username, message);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        MainPanel = new javax.swing.JPanel();
        InfoPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblPing = new javax.swing.JLabel();
        ContentPanel = new javax.swing.JPanel();
        MatchSelectorPanel = new javax.swing.JPanel();
        MatchSelectorButtonPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listRooms = new javax.swing.JList();
        ChatAndSettingsPanel = new javax.swing.JPanel();
        SettingsButtonPanel = new javax.swing.JPanel();
        ChatPanel = new javax.swing.JPanel();
        textFieldChat = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textAreaChatLog = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        listUsers = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Nap Launcher");
        setMaximumSize(new java.awt.Dimension(1280, 720));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        MainPanel.setLayout(new java.awt.BorderLayout());

        InfoPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        InfoPanel.setMinimumSize(new java.awt.Dimension(100, 30));
        InfoPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Ping:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 8);
        InfoPanel.add(jLabel1, gridBagConstraints);

        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        InfoPanel.add(jPanel1, gridBagConstraints);

        lblPing.setText("0 ms");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        InfoPanel.add(lblPing, gridBagConstraints);

        MainPanel.add(InfoPanel, java.awt.BorderLayout.NORTH);

        ContentPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout ContentPanelLayout = new javax.swing.GroupLayout(ContentPanel);
        ContentPanel.setLayout(ContentPanelLayout);
        ContentPanelLayout.setHorizontalGroup(
            ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 712, Short.MAX_VALUE)
        );
        ContentPanelLayout.setVerticalGroup(
            ContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 428, Short.MAX_VALUE)
        );

        MainPanel.add(ContentPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(MainPanel, java.awt.BorderLayout.CENTER);

        MatchSelectorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        MatchSelectorPanel.setMinimumSize(new java.awt.Dimension(150, 450));
        MatchSelectorPanel.setPreferredSize(new java.awt.Dimension(150, 450));
        MatchSelectorPanel.setLayout(new java.awt.BorderLayout());

        MatchSelectorButtonPanel.setPreferredSize(new java.awt.Dimension(150, 100));
        MatchSelectorButtonPanel.setLayout(new java.awt.BorderLayout());
        MatchSelectorPanel.add(MatchSelectorButtonPanel, java.awt.BorderLayout.SOUTH);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        listRooms.setModel(availableRooms);
        jScrollPane1.setViewportView(listRooms);

        MatchSelectorPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(MatchSelectorPanel, java.awt.BorderLayout.EAST);

        ChatAndSettingsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        ChatAndSettingsPanel.setMinimumSize(new java.awt.Dimension(800, 150));
        ChatAndSettingsPanel.setPreferredSize(new java.awt.Dimension(800, 150));
        ChatAndSettingsPanel.setLayout(new java.awt.BorderLayout());

        SettingsButtonPanel.setMinimumSize(new java.awt.Dimension(150, 100));
        SettingsButtonPanel.setPreferredSize(new java.awt.Dimension(150, 146));
        SettingsButtonPanel.setLayout(new java.awt.BorderLayout());
        ChatAndSettingsPanel.add(SettingsButtonPanel, java.awt.BorderLayout.EAST);

        ChatPanel.setLayout(new java.awt.BorderLayout());

        textFieldChat.setMinimumSize(new java.awt.Dimension(6, 25));
        textFieldChat.setPreferredSize(new java.awt.Dimension(59, 25));
        ChatPanel.add(textFieldChat, java.awt.BorderLayout.SOUTH);

        jPanel9.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        textAreaChatLog.setEditable(false);
        textAreaChatLog.setColumns(20);
        textAreaChatLog.setLineWrap(true);
        textAreaChatLog.setRows(5);
        jScrollPane2.setViewportView(textAreaChatLog);

        jPanel9.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setMinimumSize(new java.awt.Dimension(150, 24));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(150, 4));

        listUsers.setModel(usersInRoom);
        jScrollPane3.setViewportView(listUsers);

        jPanel9.add(jScrollPane3, java.awt.BorderLayout.EAST);

        ChatPanel.add(jPanel9, java.awt.BorderLayout.CENTER);

        ChatAndSettingsPanel.add(ChatPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(ChatAndSettingsPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            sfClient.disconnect();
        }
        finally {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ChatAndSettingsPanel;
    private javax.swing.JPanel ChatPanel;
    private javax.swing.JPanel ContentPanel;
    private javax.swing.JPanel InfoPanel;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JPanel MatchSelectorButtonPanel;
    private javax.swing.JPanel MatchSelectorPanel;
    private javax.swing.JPanel SettingsButtonPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblPing;
    private javax.swing.JList listRooms;
    private javax.swing.JList listUsers;
    private javax.swing.JTextArea textAreaChatLog;
    private javax.swing.JTextField textFieldChat;
    // End of variables declaration//GEN-END:variables
}
