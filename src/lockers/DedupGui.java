package lockers;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
 
/* DedupGui.java requires no other files. */
public class DedupGui extends JPanel
                      implements ListSelectionListener {
    private JList list;
    private DefaultListModel listModel;
 
    private static JFileChooser fileChooser = new JFileChooser();
    private static JTextArea textDisplay =  new JTextArea("Show File Here");
    private static File chosenFile;
    private static String chosenFileString;
    
    private static final String AddFileString = "Add File";
    private static final String retrieveFileString = "Retrieve File";
    private static final String selectFileString = "Select File";
    private JButton retrieveButton;
    private JButton selectFileButton;
    private JTextField filenameRetrieve;
    private JLabel progressLabel, fileListLabel, fileDisplayLabel;
    private static Locker storage; 
    private JProgressBar progressBar;
    private static int lockerSize;
 
    public DedupGui() {
        super(new BorderLayout());
        setPreferredSize( new Dimension( 640, 480 ) );
        listModel = new DefaultListModel();

        chosenFile = new File("");
        lockerSize = 0;
        
        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(10);
        JScrollPane listScrollPane = new JScrollPane(list);
 
        JButton addFileButton = new JButton(AddFileString);
        addFileListener addFileListener = new addFileListener(addFileButton);
        addFileButton.setActionCommand(AddFileString);
        addFileButton.addActionListener(addFileListener);
        addFileButton.setEnabled(false);
        
        // add a double bevel border to the center pane
        Border raisedbevel = BorderFactory.createRaisedBevelBorder();
        Border loweredbevel = BorderFactory.createLoweredBevelBorder();
        Border compound = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
        
        selectFileButton = new JButton(selectFileString);
        selectFileButton.setActionCommand(selectFileString);
        selectFileButton.addActionListener(new selectFileListener());
       
        /* Setting Current Directory */
        fileChooser.setCurrentDirectory(new File("C:\\Documents and Settings"));

        
        retrieveButton = new JButton(retrieveFileString);
        retrieveButton.setActionCommand(retrieveFileString);
        retrieveButton.addActionListener(new retrieveFileListener());
 
        filenameRetrieve = new JTextField();
        
        //filenameRetrieve.setMaximumSize(newDim);
        filenameRetrieve.addActionListener(addFileListener);
        filenameRetrieve.getDocument().addDocumentListener(addFileListener);
       
 
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        JPanel progressPane =  new JPanel();
        JPanel centerPane =  new JPanel();
        JPanel topPane = new JPanel();
        
        
        topPane.setLayout(new BoxLayout(topPane,
                BoxLayout.Y_AXIS));
       
        progressPane.setLayout(new BoxLayout(progressPane,
                BoxLayout.Y_AXIS));
        
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        centerPane.setLayout(new BoxLayout(centerPane,
                BoxLayout.Y_AXIS));
        
        // Locker fill progress 0 to 20MB
        progressBar = new JProgressBar(0, 20000000);
        // Change this to the value of the current locker contents
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressLabel = new JLabel("File Locker Usage");
        JLabel lockerLabel = new JLabel("File Locker");
        progressPane.add(progressLabel);
        progressPane.add(progressBar);
        
        topPane.add(lockerLabel);
        topPane.add(listScrollPane);
        topPane.add(progressPane);
        topPane.setBorder(compound);
        
        
        buttonPane.add(retrieveButton);
        buttonPane.add(selectFileButton);
        //buttonPane.add(filenameRetrieve);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        
        buttonPane.add(addFileButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        
        centerPane.add(Box.createHorizontalStrut(5));
        centerPane.add(filenameRetrieve);
        
        buttonPane.setBorder(compound);
        

        

        add(buttonPane, BorderLayout.SOUTH);
        add(topPane, BorderLayout.NORTH);
        add(centerPane, BorderLayout.CENTER);
    }
 
    class retrieveFileListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Remove the file from the locker
        	chosenFileString = list.getSelectedValue().toString();
        	System.out.print("\n" + chosenFileString + "\n");
        	Path chosenPath = Paths.get(chosenFileString);
        	chosenFileString = chosenPath.getFileName().toString();
        	// Retrieve the file from the locker
        	try {
				storage.retrieve(chosenFileString);
				System.out.print("\n File retrieved \n");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        	/*****************************/
        	// Update the progress bar
        	//lockerSize = storage.getSize();
        	
        	System.out.print(storage);
        	//This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            int index = list.getSelectedIndex();
            listModel.remove(index);
            progressBar.setValue(lockerSize-=2000000);
 
            int size = listModel.getSize();
 
            if (size == 0) { //Nobody's left, disable firing.
                retrieveButton.setEnabled(false);
 
            } else { //Select an index.
                if (index == listModel.getSize()) {
                    //removed item in last position
                    index--;
                }
 
                list.setSelectedIndex(index);
                list.ensureIndexIsVisible(index);
            }
        }
    }
    
    
    /////////////////////////////////////////////////////
    class selectFileListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            if (command.equals(selectFileString)) {
                    fileChooser.showDialog(fileChooser,
                                    "Select File");
                    chosenFile = fileChooser.getSelectedFile();
                    
                    filenameRetrieve.setText(chosenFile.getPath());
                    /* Wait to implement file content display
                    BufferedReader buffReader = null;
                    try {

                      FileReader fileReader = new FileReader(chosenFile);
                      buffReader = new BufferedReader(fileReader);
                      textDisplay.read(buffReader, "file.txt");
                    }  catch (Exception e) {
                      e.printStackTrace();
                    } finally {
                      // close your BufferedReader
                    }
                    
                    */

            }
        }
    }
 
    ///////////////////////////////////////////////////////////////////////
    //This listener is for the add file button.
    class addFileListener implements ActionListener, DocumentListener {
        private boolean alreadyEnabled = false;
        private JButton button;
 
        public addFileListener(JButton button) {
            this.button = button;
        }
 
        //Required by ActionListener.
        public void actionPerformed(ActionEvent e) {
            
        	String command = e.getActionCommand();
            if (command.equals(AddFileString)) {
        	
            	String name = filenameRetrieve.getText();
            	try{
                    storage.insert(chosenFile.toString());
                    System.out.print("File put in locker");
                    } finally {
                    	// Not sure what to put here
                    	
                    }
 
            //User didn't type in a unique name...
            if (name.equals("") || alreadyInList(name)) {
                Toolkit.getDefaultToolkit().beep();
                filenameRetrieve.requestFocusInWindow();
                filenameRetrieve.selectAll();
                return;
            }
            
            // Insert the file into the locker
            // add a try/catch
            
            
            int index = list.getSelectedIndex(); //get selected index
            if (index == -1) { //no selection, so insert at beginning
                index = 0;
            } else {           //add after the selected item
                index++;
            }
 
            listModel.insertElementAt(filenameRetrieve.getText(), index);
            //If we just wanted to add to the end, we'd do this:
            //listModel.addElement(filenameRetrieve.getText());
 
            //Reset the text field.
            filenameRetrieve.requestFocusInWindow();
            filenameRetrieve.setText("");
 
            //Select the new item and make it visible.
            list.setSelectedIndex(index);
            list.ensureIndexIsVisible(index);
            
            //Update progress bar: for now simple 
            //progressBar.setValue(list.getModel().getSize() / 10);
            progressBar.setIndeterminate(false);
            progressBar.setValue(lockerSize+=2000000);
            }
            
        }
 
        //This method tests for string equality. You could certainly
        //get more sophisticated about the algorithm.  For example,
        //you might want to ignore white space and capitalization.
        protected boolean alreadyInList(String name) {
            return listModel.contains(name);
        }
 
        //Required by DocumentListener.
        public void insertUpdate(DocumentEvent e) {
            enableButton();
        }
 
        //Required by DocumentListener.
        public void removeUpdate(DocumentEvent e) {
            handleEmptyTextField(e);
        }
 
        //Required by DocumentListener.
        public void changedUpdate(DocumentEvent e) {
            if (!handleEmptyTextField(e)) {
                enableButton();
            }
        }
 
        private void enableButton() {
            if (!alreadyEnabled) {
                button.setEnabled(true);
            }
        }
 
        private boolean handleEmptyTextField(DocumentEvent e) {
            if (e.getDocument().getLength() <= 0) {
                button.setEnabled(false);
                alreadyEnabled = false;
                return true;
            }
            return false;
        }
    }
 
    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
 
            if (list.getSelectedIndex() == -1) {
            //No selection, disable retrieve
                retrieveButton.setEnabled(false);
 
            } else {
            //Selection, enable the retrieve button.
                retrieveButton.setEnabled(true);
            }
        }
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("DedupGui");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new DedupGui();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException {
        //Create the locker fill with the current database
    	storage = new FastStupidLocker();
		long start = System.currentTimeMillis();
		
		//
		long end = System.currentTimeMillis();
		System.out.println("Total time: " + (double)(end - start)/1000 + "s\n");
		
	

    	
        //creating and showing the GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}


