import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

interface menu
{
    void createMenu();
    void createSubMenuCheck(Menu dirMenu);
    void createSubMenuGoTo(Menu dirMenu);
}

interface directory
{
    void isDirectory(File dirPath);
    void showCurrentDir(File dirPath);
    void goToDir(File dirPath);
    void backToDir();
}

public class SysDir extends JFrame implements menu, directory
{
    // Variabiles for Window size
    final int width = 600;
    final int height = 400;

    // Variabiles for directors
    static File cDir = new File("C:\\");
    static File dDir = new File("D:\\");
    static File eDir = new File("E:\\");
    private File currentDir;

    JTable dirTable;
    DefaultTableModel tableModel;
    JTextField pathField;
    JButton backButton;

    // Constructor
    public SysDir()
    {
        // Set the configuration for window
        this.setTitle("Navigare Directoare");
        this.setSize(width, height);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        // Back Button
        backButton = new JButton("Inapoi");
        backButton.setEnabled(false);
        backButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent aEvent)
            {
                backToDir();
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);

        // TextField for current path
        pathField = new JTextField();
        pathField.setEditable(false);
        topPanel.add(pathField, BorderLayout.CENTER);
        this.add(topPanel, BorderLayout.NORTH);

        // Create the table
        createTabelDir();

        // Place the menu bar
        createMenu();

        // Set the current directory for now
        currentDir = cDir;
        showCurrentDir(currentDir);

        // Close the program
        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent wEvent)
            {
                confirmationExit();
            }
        });

        this.setVisible(true);
    }

    // Function to create the MenuBar
    @Override
    public void createMenu()
    {
        MenuBar menuBar = new MenuBar();
        Menu dirMenu = new Menu("Directoare");

        createSubMenuCheck(dirMenu);
        createSubMenuGoTo(dirMenu);

        menuBar.add(dirMenu);
        this.setMenuBar(menuBar);
    }

    // Function to create the subMenu to check dirs
    @Override
    public void createSubMenuCheck(Menu dirMenu)
    {
        Menu subMenuCheck = new Menu("Verifica");
        MenuItem checkC = new MenuItem("Verifica C");
        MenuItem checkD = new MenuItem("Verifica D");
        MenuItem checkE = new MenuItem("Verifica E");

        subMenuCheck.add(checkC);
        subMenuCheck.add(checkD);
        subMenuCheck.add(checkE);
        dirMenu.add(subMenuCheck);

        checkC.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent aEvent)
            {
                isDirectory(cDir);
            }
        });

        checkD.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent aEvent)
            {
                isDirectory(dDir);
            }
        });

        checkE.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent aEvent)
            {
                isDirectory(eDir);
            }
        });
    }

    // Function to create the subMenu for GoTo Dir...
    @Override
    public void createSubMenuGoTo(Menu dirMenu)
    {
        Menu subMenuGoTo = new Menu("Mergi la..");
        MenuItem goDirC = new MenuItem("Mergi in directorul C");
        MenuItem goDirD = new MenuItem("Mergi in directorul D");

        subMenuGoTo.add(goDirC);
        subMenuGoTo.add(goDirD);
        dirMenu.add(subMenuGoTo);

        goDirC.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent aEvent)
            {
                showCurrentDir(cDir);
            }
        });

        goDirD.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent aEvent)
            {
                showCurrentDir(dDir);
            }
        });
    }

    // Function to check directory
    @Override
    public void isDirectory(File dirPath)
    {
        String foundDir;
        if (dirPath.isDirectory())
        {
            int itemCount = dirPath.list().length;
            foundDir = dirPath + " este un director si contine " + itemCount + " elemente";
        }
        else
        {
            foundDir = dirPath + " nu este un director";
        }
        JOptionPane.showMessageDialog(this, foundDir, "Rezultatul verificarii", JOptionPane.INFORMATION_MESSAGE);
    }

    // Function to show content from current Directory
    @Override
    public void showCurrentDir(File dirPath)
    {
        // Update the current path
        // Activate and deactivate the back button
        // Reset the table after new path

        currentDir = dirPath;
        pathField.setText(currentDir.getAbsolutePath());
        backButton.setEnabled(currentDir.getParentFile() != null);

        File[] files = dirPath.listFiles();
        if (files != null)
        {
            tableModel.setRowCount(0);

            for (File file : files)
            {
                String name = file.getName();
                String type = file.isDirectory() ? "Director" : "Fisier";
                String size = file.isDirectory() ? "-" : formatSize(file.length());
                String elements = file.isDirectory() ? (file.list() != null ? String.valueOf(file.list().length) : "0") : "-";
                tableModel.addRow(new Object[] { name, type, size, elements });
            }
        }
    }

    // Function to go to next directory
    @Override
    public void goToDir(File dirPath)
    {
        if (dirPath.isDirectory())
        {
            showCurrentDir(dirPath);
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Acesta nu este un director.", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function to go back from where we was
    @Override
    public void backToDir()
    {
        if (currentDir.getParentFile() != null)
        {
            showCurrentDir(currentDir.getParentFile());
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Nu exista director parinte", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function to create the table
    private void createTabelDir()
    {
        tableModel = new DefaultTableModel(new Object[] { "Nume", "Tip", "Marime", "Elemente" }, 0);
        dirTable = new JTable(tableModel)
        {
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };

        dirTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dirTable.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent mEvent)
            {
                int row = dirTable.getSelectedRow();
                if (row != -1)
                {
                    String fileName = (String) tableModel.getValueAt(row, 0);
                    File fileSelected = new File(currentDir, fileName);
                    if (fileSelected.isDirectory())
                    {
                        goToDir(fileSelected);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(dirTable);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    // Function to convert the size of a file
    private String formatSize(long size)
    {
        if (size < 1024)
        {
            return size + " bytes";
        }
        else if (size < 1024 * 1024)
        {
            return String.format("%.2f KB", size / 1024.0);
        }
        else if (size < 1024 * 1024 * 1024)
        {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        }
        else
        {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    // Function for confirmation exit ( Yes or No )
    private void confirmationExit()
    {
        Label message = new Label("Esti sigur ca vrei sa parasesti programul?");
        Button yesButton = new Button("Da");
        Button noButton = new Button("Nu");

        Dialog confExitDialog = new Dialog(this, "Confirmare iesire", true);
        confExitDialog.setSize(350, 100);
        confExitDialog.setLayout(new FlowLayout());
        confExitDialog.setLocationRelativeTo(this);

        yesButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent aEvent)
            {
                System.exit(0);
            }
        });

        noButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent aEvent)
            {
                confExitDialog.setVisible(false);
            }
        });

        confExitDialog.add(message);
        confExitDialog.add(yesButton);
        confExitDialog.add(noButton);
        confExitDialog.setVisible(true);
    }

    // Main Function
    public static void main(String args[])
    {
        new SysDir();
    }
}