import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.awt.image.BufferedImage; // Ajoutez cette importation
import java.io.File;
import java.io.IOException;

public class TaskManager {
    private static final String DEFAULT_JSON_FILE = "Tasks/Tasks.json";
    private static JPanel panel;
    private static List<Task> tasks;
    private static JTextField taskNameField;
    private static JTextField taskDescriptField;
    private static String nomFichier = DEFAULT_JSON_FILE;
    private static ImageIcon redIcon;
    private static ImageIcon yellowIcon;
    private static ImageIcon greenIcon;
     // Icônes pour les voyants de couleur


     public static void main(String[] args) {
        panel = new JPanel();
        JFrame frame = new JFrame("Task Manager");
        panel.setLayout(new GridLayout(0, 1));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pagemade();
        frame.add(panel);
        frame.pack();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int frameWidth = 700;
        int frameHeight = 500;
        int x = centerPoint.x - frameWidth / 2;
        int y = centerPoint.y - frameHeight / 2;
        frame.setBounds(x, y, frameWidth, frameHeight);
        frame.setVisible(true);
    }
    

    public static void ajouterTache(String nouvelleTache,  String description, int difficulty) {
        FileManager fileManager = new FileManager(nomFichier);
        LocalDate dateActuelle = LocalDate.now();
        String dateFormatee = dateActuelle.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Date date = java.sql.Date.valueOf(dateFormatee);
        List<SubTask> subTasks = new ArrayList<>();
        Task nouvelleTask = new Task(generateId(), nouvelleTache, description, false, date, "", difficulty, subTasks);
        tasks.add(nouvelleTask);
        fileManager.writeTasksToFile(tasks);
        rafraichirAffichageTaches();
    }
    public static void ajouterSousTache(Task parentTask, String nouvelleTache, String description, int difficulty) {
        LocalDate dateActuelle = LocalDate.now();
        String dateFormatee = dateActuelle.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Date date = java.sql.Date.valueOf(dateFormatee);
        SubTask nouvelleSousTache = new SubTask(generateId(), nouvelleTache, description, false, date, difficulty);
        parentTask.getSubTasks().add(nouvelleSousTache);
        FileManager fileManager = new FileManager(nomFichier);
        fileManager.writeTasksToFile(tasks);
        rafraichirAffichageTaches();
    }
    private static void afficherInfosTache(Task task) {
        StringBuilder message = new StringBuilder();
        message.append("Nom de la tâche: ").append(task.getName()).append("\n");
        message.append("Description: ").append(task.getDescript()).append("\n");
        message.append("Date: ").append(task.getDate()).append("\n");
        message.append("Difficulté: ");
        switch (task.getDificult()) {
            case 1:
                message.append("Easy");
                break;
            case 2:
                message.append("Medium");
                break;
            case 3:
                message.append("Hard");
                break;
            default:
                message.append("Unknown");
                break;
        }
        JButton modifyButton = new JButton("Modifier");
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lancer une nouvelle boîte de dialogue ou fenêtre modale pour modifier les informations de la sous-tâche
                modifierInfosTache(task);
            }
        });
    
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JTextArea(message.toString()));
        panel.add(modifyButton, BorderLayout.SOUTH);
    
        JOptionPane.showMessageDialog(null, panel, "Informations sur la tâche", JOptionPane.CLOSED_OPTION);
    }
    private static void modifierInfosTache(Task task) {
        // Création des champs éditables pour les informations de la tâche
        JTextField taskNameField = new JTextField(task.getName(), 20);
        JTextField descriptionField = new JTextField(task.getDescript(), 20);
        JTextField dateField = new JTextField(task.getDate().toString(), 20);
    
        // Création d'un panneau pour organiser les composants
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nom de la tâche:"));
        panel.add(taskNameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
    
        // Affichage de la boîte de dialogue modale
        int result = JOptionPane.showConfirmDialog(null, panel, "Modifier la tâche",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
        // Si l'utilisateur appuie sur OK, mettez à jour les informations de la tâche
        if (result == JOptionPane.OK_OPTION) {
            // Mise à jour des informations de la tâche avec les valeurs des champs éditables
            task.setName(taskNameField.getText());
            task.setDescript(descriptionField.getText());
            // Parse la date
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date newDate = dateFormat.parse(dateField.getText());
                task.setDate(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Format de date invalide. Veuillez utiliser le format YYYY-MM-DD.",
                        "Erreur de format de date", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Mettre à jour les informations dans le fichier de sauvegarde
            FileManager fileManager = new FileManager(nomFichier);
            fileManager.writeTasksToFile(tasks);
    
            // Afficher un message de confirmation
            JOptionPane.showMessageDialog(null, "Informations de la tâche modifiées avec succès.",
                    "Modification réussie", JOptionPane.INFORMATION_MESSAGE);
        }
        rafraichirAffichageTaches();
    }
    private static void afficherInfosSousTache(SubTask subTask) {
        StringBuilder message = new StringBuilder();
        message.append("Nom de la tâche: ").append(subTask.getTaskName()).append("\n");
        message.append("Description: ").append(subTask.getDescription()).append("\n");
        message.append("Date: ").append(subTask.getTimeToDo()).append("\n");
        message.append("Difficulté: ");
        switch (subTask.getDifficulty()) {
            case 1:
                message.append("Easy");
                break;
            case 2:
                message.append("Medium");
                break;
            case 3:
                message.append("Hard");
                break;
            default:
                message.append("Unknown");
                break;
        }
    
        // Ajouter un bouton de modification à la boîte de dialogue
        JButton modifyButton = new JButton("Modifier");
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lancer une nouvelle boîte de dialogue ou fenêtre modale pour modifier les informations de la sous-tâche
                modifierInfosSousTache(subTask);
            }
        });
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JTextArea(message.toString()));
        panel.add(modifyButton, BorderLayout.SOUTH);
    
        JOptionPane.showMessageDialog(null, panel, "Informations sur la tâche", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private static void modifierInfosSousTache(SubTask subTask) {
        // Création des champs éditables pour les informations de la sous-tâche
        JTextField taskNameField = new JTextField(subTask.getTaskName(), 20);
        JTextField descriptionField = new JTextField(subTask.getDescription(), 20);
        JTextField dateField = new JTextField(subTask.getTimeToDo().toString(), 20);
    
        // Création d'un panneau pour organiser les composants
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nom de la sous-tâche:"));
        panel.add(taskNameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
    
        // Affichage de la boîte de dialogue modale
        int result = JOptionPane.showConfirmDialog(null, panel, "Modifier la sous-tâche",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
        // Si l'utilisateur appuie sur OK, mettez à jour les informations de la sous-tâche
        if (result == JOptionPane.OK_OPTION) {
            // Mise à jour des informations de la sous-tâche avec les valeurs des champs éditables
            subTask.setTaskName(taskNameField.getText());
            subTask.setDescription(descriptionField.getText());
            // Parse la date
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date newDate = dateFormat.parse(dateField.getText());
                subTask.setTimeToDo(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Format de date invalide. Veuillez utiliser le format YYYY-MM-DD.",
                        "Erreur de format de date", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Mettre à jour les informations dans le fichier de sauvegarde
            FileManager fileManager = new FileManager(nomFichier);
            fileManager.writeTasksToFile(tasks);
    
            // Afficher un message de confirmation
            JOptionPane.showMessageDialog(null, "Informations de la sous-tâche modifiées avec succès.",
                    "Modification réussie", JOptionPane.INFORMATION_MESSAGE);
        }
        rafraichirAffichageTaches();
    }

    private static int generateId() {
        int maxId = 0;
    
        // Parcourir toutes les tâches existantes pour trouver le plus grand ID
        for (Task task : tasks) {
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
            // Si la tâche a des sous-tâches, vérifier aussi les identifiants des sous-tâches
            for (SubTask subTask : task.getSubTasks()) {
                if (subTask.getId() > maxId) {
                    maxId = subTask.getId();
                }
            }
        }
    
        // L'ID suivant devrait être supérieur au plus grand ID trouvé
        return maxId + 1;
    }
   

    public static void pagemade() {
        ImageIcon redIcon = createColorIcon(Color.RED);
        ImageIcon yellowIcon = createColorIcon(Color.YELLOW);
        ImageIcon greenIcon = createColorIcon(Color.GREEN);
        JPanel filePanel = new JPanel();
        FileManager fileManager = new FileManager(nomFichier);
        List<Integer> selectedTaskIds = new ArrayList<>();
        List<Integer> selectedSubTaskIds = new ArrayList<>();
        tasks = fileManager.readTasksFromFile();
        JButton changeFileButton = new JButton("Changer le fichier JSON");
        JButton createFileButton = new JButton("Créer un nouveau fichier JSON");
        changeFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("Tasks/")); // Répertoire par défaut
                int result = fileChooser.showOpenDialog(panel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    nomFichier = selectedFile.getAbsolutePath();
                    tasks = new FileManager(nomFichier).readTasksFromFile(); // Mettre à jour les tâches avec le nouveau fichier
                    refreshTasks(); // Rafraîchir l'affichage avec les nouvelles données
                }
            }
        });
        createFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newFileName = JOptionPane.showInputDialog(panel, "Entrez le nom du nouveau fichier JSON:");
                if (newFileName != null && !newFileName.isEmpty()) {
                    File newFile = new File("Tasks/" + newFileName + ".json");
                    try {
                        if (newFile.createNewFile()) {
                            JOptionPane.showMessageDialog(panel, "Nouveau fichier JSON créé avec succès: " + newFile.getAbsolutePath());
                            // Mettre à jour le nom de fichier actuel
                            nomFichier = newFile.getAbsolutePath();
                            // Rafraîchir l'affichage avec les nouvelles données
                            refreshTasks();
                        } else {
                            int choice = JOptionPane.showConfirmDialog(panel, "Le fichier existe déjà. Voulez-vous le remplacer?");
                            if (choice == JOptionPane.YES_OPTION) {
                                if (newFile.delete()) {
                                    if (newFile.createNewFile()) {
                                        JOptionPane.showMessageDialog(panel, "Fichier existant remplacé avec succès: " + newFile.getAbsolutePath());
                                        // Mettre à jour le nom de fichier actuel
                                        nomFichier = newFile.getAbsolutePath();
                                        // Rafraîchir l'affichage avec les nouvelles données
                                        refreshTasks();
                                    } else {
                                        JOptionPane.showMessageDialog(panel, "Impossible de remplacer le fichier.");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(panel, "Impossible de supprimer le fichier existant.");
                                }
                            }
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(panel, "Erreur lors de la création du fichier.");
                    }
                }
            }
        });
        filePanel.add(changeFileButton);
        filePanel.add(createFileButton);
        panel.add(filePanel);
        //chaque tache
        for (Task task : tasks) {
            JPanel taskPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JCheckBox multiSelected = new JCheckBox();
            String taskName = task.getName();
            JCheckBox CheckTerm = new JCheckBox("", task.isCompleted());
            JLabel taskNameLabel = new JLabel(taskName);
            JLabel dificultTaskLabel = new JLabel();
            // Définir l'icône en fonction du niveau de difficulté de la tâche
            switch (task.getDificult()) {
                case 1:
                    dificultTaskLabel.setIcon(greenIcon); // Vert pour une faible difficulté
                    break;
                case 2:
                    dificultTaskLabel.setIcon(yellowIcon); // Jaune pour une difficulté moyenne
                    break;
                case 3:
                    dificultTaskLabel.setIcon(redIcon); // Rouge pour une difficulté élevée
                    break;
                default:
                    dificultTaskLabel.setIcon(greenIcon); // Vert pour une faible difficulté
                    break;
            }
            taskNameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    afficherInfosTache(task);
                }
            });
            
            multiSelected.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        selectedTaskIds.add(task.getId());
                    } else {
                        selectedTaskIds.remove(Integer.valueOf(task.getId()));
                    }
                }
            });
            CheckTerm.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    task.setCompleted(e.getStateChange() == ItemEvent.SELECTED);
                    fileManager.writeTasksToFile(tasks);
                }
            }); 
            taskPanel.add(multiSelected);
            taskPanel.add(CheckTerm);
            taskPanel.add(dificultTaskLabel);
            taskPanel.add(taskNameLabel);
           
            panel.add(taskPanel);
            for (SubTask subTask : task.getSubTasks()) {
                JPanel subTaskPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                String subeTaskName = subTask.getTaskName();
                JLabel spaceLabel = new JLabel("        "); // Espace vide pour le décalage
                JCheckBox multiSelectedSubTask = new JCheckBox();
                JCheckBox subTaskCheckBox = new JCheckBox("", subTask.isCompleted());
                JLabel subTaskNameLabel = new JLabel(subeTaskName);
                JLabel dificultSubTaskLabel = new JLabel();
                
                // Définir l'icône en fonction du niveau de difficulté de la tâche
                switch (subTask.getDifficulty()) {
                    case 1:
                        dificultSubTaskLabel.setIcon(greenIcon); // Rouge pour une difficulté élevée
                        break;
                    case 2:
                        dificultSubTaskLabel.setIcon(yellowIcon); // Jaune pour une difficulté moyenne
                        break;
                    case 3:
                        dificultSubTaskLabel.setIcon(redIcon); // Vert pour une faible difficulté
                        break;
                    default:
                        dificultSubTaskLabel.setIcon(greenIcon); // Vert pour une faible difficulté
                        break;
                }
                subTaskNameLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        afficherInfosSousTache(subTask);
                    }
                });
                multiSelectedSubTask.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            selectedSubTaskIds.add(subTask.getId());
                        } else {
                            selectedSubTaskIds.remove(Integer.valueOf(subTask.getId()));
                        }
                    }
                });
                subTaskCheckBox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        subTask.setCompleted(e.getStateChange() == ItemEvent.SELECTED);
                        fileManager.writeTasksToFile(tasks);
                    }
                });
                subTaskPanel.add(spaceLabel);
                subTaskPanel.add(multiSelectedSubTask);
                subTaskPanel.add(subTaskCheckBox);
                subTaskPanel.add(dificultSubTaskLabel);
                subTaskPanel.add(subTaskNameLabel);
                panel.add(subTaskPanel);
            }
            
        }
        //ajoute une tache
        JButton addButton = new JButton("Ajouter une tâche");
        
        JRadioButton highButton = new JRadioButton("High");
        JRadioButton mediumButton = new JRadioButton("Medium");
        JRadioButton hardButton = new JRadioButton("Hard");

        taskNameField = new JTextField(20);
        taskDescriptField = new JTextField(20);

        ButtonGroup difficultyGroup = new ButtonGroup();
        difficultyGroup.add(highButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);

        final int[] difficulty = {0}; // Utilisation d'un tableau pour contourner les contraintes de modification des variables locales
    
        highButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Définissez la difficulté sur 3 (ou une autre valeur selon votre choix)
                difficulty[0] = 3;
            }
        });
        mediumButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Définissez la difficulté sur 2 (ou une autre valeur selon votre choix)
                difficulty[0] = 2;
            }
        });
        hardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Définissez la difficulté sur 1 (ou une autre valeur selon votre choix)
                difficulty[0] = 1;
            }
        });
        // Ajoutez les boutons radio au panneau
        JPanel difficultyPanel = new JPanel();
        JPanel creatTaskPanel = new JPanel();
        creatTaskPanel.setLayout(new BoxLayout(creatTaskPanel, BoxLayout.Y_AXIS));
        JPanel upTaskJPanel = new JPanel();
        JPanel dawnTaskJPanel = new JPanel();
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nouvelleTache = taskNameField.getText();
                String description = taskDescriptField.getText();
                boolean  alreadyAdd = false;
                for (Task task : tasks) {
                    if (selectedTaskIds.contains(task.getId())) {
                        Task parentTask = task;
                        ajouterSousTache(parentTask, nouvelleTache, description, difficulty[0]);
                        alreadyAdd = true;
                    }
                }
                if (alreadyAdd == false) {
                    ajouterTache(nouvelleTache, description, difficulty[0]);
                }
            }
        });
        difficultyPanel.add(highButton);
        difficultyPanel.add(mediumButton);
        difficultyPanel.add(hardButton);
        // Ajoutez le panneau de difficulté à votre fenêtre
        upTaskJPanel.add(taskNameField);
        upTaskJPanel.add(difficultyPanel);
        upTaskJPanel.setBackground(Color.RED);
        dawnTaskJPanel.add(taskDescriptField);
        dawnTaskJPanel.add(addButton);
        dawnTaskJPanel.setBackground(Color.RED);
        creatTaskPanel.add(upTaskJPanel);
        creatTaskPanel.add(dawnTaskJPanel);
        
        //supprime tout les tache selectonners
        JButton deleteButton = new JButton("Supprimer les tâches sélectionnées");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Créer une liste pour stocker les tâches à supprimer
                List<Task> tasksToRemove = new ArrayList<>();
                // Parcourir les tâches et leurs panneaux associés
                for (Task task : tasks) {
                    // Vérifier si l'identifiant de la tâche est dans la liste des tâches sélectionnées
                    if (selectedTaskIds.contains(task.getId())) {
                        tasksToRemove.add(task);
                    } else {
                        // Si la tâche n'est pas sélectionnée, parcourir ses sous-tâches
                        Iterator<SubTask> subTaskIterator = task.getSubTasks().iterator();
                        while (subTaskIterator.hasNext()) {
                            SubTask subTask = subTaskIterator.next();
                            // Vérifier si l'identifiant de la sous-tâche est dans la liste des sous-tâches sélectionnées
                            if (selectedSubTaskIds.contains(subTask.getId())) {
                                subTaskIterator.remove(); // Supprimer la sous-tâche de la liste de sous-tâches de la tâche
                            }
                        }
                    }
                }
                // Supprimer les tâches de la liste principale
                tasks.removeAll(tasksToRemove);
                // Rafraîchir l'affichage et écrire les modifications dans le fichier
                panel.revalidate();
                panel.repaint();
                fileManager.writeTasksToFile(tasks);
                // Vider la liste des identifiants des tâches sélectionnées
                selectedTaskIds.clear();
                // Vider la liste des identifiants des sous-tâches sélectionnées
                selectedSubTaskIds.clear();
                rafraichirAffichageTaches();
            }
        });
        //panneaux de config des option
        JPanel optPanel = new JPanel();
        JPanel DatePanel = new JPanel();
        JTextField dateField = new JTextField("YYYY-MM-DD", 10);
        DatePanel.add(dateField);
        JButton changeDateButton = new JButton("Changer la date");
        changeDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dateString = dateField.getText();
                LocalDate newDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                for (Task task : tasks) {
                    if (selectedTaskIds.contains(task.getId())) {
                        task.setDate(java.sql.Date.valueOf(newDate));
                    }
                    Iterator<SubTask> subTaskIterator = task.getSubTasks().iterator();
                    while (subTaskIterator.hasNext()) {
                        SubTask subTask = subTaskIterator.next();
                        if (selectedSubTaskIds.contains(subTask.getId())) {
                            subTask.setTimeToDo(java.sql.Date.valueOf(newDate));
                        }
                    }
                }
                panel.revalidate();
                panel.repaint();
                fileManager.writeTasksToFile(tasks);
                selectedTaskIds.clear();
              
                rafraichirAffichageTaches();
            }
        });
        DatePanel.add(changeDateButton);
        JPanel CategoryPanel = new JPanel();
        JTextField categoryField = new JTextField("Entrez la catégorie", 10);
        JButton changeCategoryButton = new JButton("Changer la catégorie");
        changeCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String category = categoryField.getText();
                for (Task task : tasks) {
                    if (selectedTaskIds.contains(task.getId())) {
                        task.setCategory(category);
                    }
                }
                panel.revalidate();
                panel.repaint();
                fileManager.writeTasksToFile(tasks);
                selectedTaskIds.clear();
                rafraichirAffichageTaches();
            }
        });
        CategoryPanel.add(categoryField);
        CategoryPanel.add(changeCategoryButton);
        optPanel.add(deleteButton);
        optPanel.add(DatePanel);
        optPanel.add(CategoryPanel);
        creatTaskPanel.setBackground(Color.BLUE);
        optPanel.setBackground(Color.BLUE);
        panel.add(creatTaskPanel);
        panel.add(optPanel);
        
    }
    public static void rafraichirAffichageTaches() {
        panel.removeAll();
        pagemade();
        panel.revalidate();
        panel.repaint();
    }
    public static void refreshTasks() {
        // Utiliser le nouveau nom de fichier pour lire les tâches
        FileManager fileManager = new FileManager(nomFichier);
        tasks = fileManager.readTasksFromFile();
        // Rafraîchir l'affichage avec les nouvelles données
        panel.removeAll();
        pagemade(); // Réajouter les composants avec les nouvelles données
        panel.revalidate();
        panel.repaint();
    }
    private static ImageIcon createColorIcon(Color color) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.fillOval(0, 0, 16, 16);
        graphics.dispose();
        return new ImageIcon(image);
    }
}
//javac -cp ".;lib/*" *.java -d build
//java -cp "build;lib/*" TaskManager