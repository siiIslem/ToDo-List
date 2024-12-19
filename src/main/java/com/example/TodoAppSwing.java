package com.example;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.formdev.flatlaf.FlatDarkLaf;

public class TodoAppSwing {

    private ArrayList<Task> tasks = new ArrayList<>();
    private DefaultListModel<Task> taskListModel = new DefaultListModel<>();
    private JList<Task> taskList;
    private JLabel progressLabel;

    public TodoAppSwing() {
        // Create the main frame using the custom NablaFrame class
        JFrame frame = new JFrame("Todo List App");
        // Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int screenHeight = screenSize.height;

        // Set custom width (e.g., 800px) and maximum height
        int frameWidth = 650;  // Your desired width
        int frameHeight = (int) (screenHeight * 0.9);  // Maximum height

        frame.setSize(frameWidth, frameHeight);

        frame.setLocationRelativeTo(null);  // Center the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Todo List");

        try {
            // Load the icon image
            Image icon = ImageIO.read(getClass().getResource("/app.png"));
            frame.setIconImage(icon); // Set the icon for the JFrame
        } catch (IOException e) {
            System.out.println("Icon image could not be loaded.");
        }

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveTasks(); // Save tasks before closing
                System.exit(0); // Close the application
            }
        });
        // Layout for the frame
        frame.setLayout(new BorderLayout());

        // Top panel with vertical stacking
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(41, 41, 41)); // Background color for consistency

        // Header label
        JLabel headerLabel = new JLabel("ToDo List", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Candara", Font.PLAIN, 50));
        headerLabel.setForeground(new Color(230, 230, 250));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment for BoxLayout
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 0, 10)); // Padding for text

        // Instruction label
        JLabel instruction = new JLabel("Press F to Finish/Unfinish, D to delete, T to Toggle Priority", SwingConstants.CENTER);
        instruction.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        instruction.setForeground(new Color(230, 230, 250));
        instruction.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        inputPanel.setBackground(new Color(41, 41, 41));
        JTextField taskInput = new JTextField() {
            private final String placeholder = "What do you have planned?";

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(200, 200, 200, 100)); // Light gray and slightly transparent
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    FontMetrics metrics = g2.getFontMetrics();
                    int x = getInsets().left;
                    int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
                    g2.drawString(placeholder, x, y);
                }
            }

            @Override
            public void setText(String text) {
                super.setText(text);
                repaint(); // Make sure the placeholder gets redrawn if the text changes
            }
        };

        taskInput.setPreferredSize(new Dimension(500, 90));
        taskInput.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 17));
        taskInput.setForeground(Color.WHITE);
        taskInput.setBackground(new Color(60, 63, 65));
        taskInput.setCaretColor(Color.GRAY);
        taskInput.setBorder(new RoundedBorder(100));

        inputPanel.setBackground(new Color(41, 41, 41)); // Background Color for input area
        inputPanel.add(taskInput);

        inputPanel.setPreferredSize(new Dimension(600, 150));
        taskInput.setDocument(new JTextFieldLimit(50)); // Custom class to limit the number of characters

        // Progress label
        progressLabel = new JLabel("", SwingConstants.CENTER);
        progressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        progressLabel.setForeground(new Color(200, 200, 200));
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); //Padding for (top,left,bottom,right)

        // Add components to the top panel
        topPanel.add(Box.createVerticalStrut(0)); // Add spacing
        topPanel.add(headerLabel);
        topPanel.add(Box.createVerticalStrut(0)); // Add spacing
        topPanel.add(inputPanel);
        topPanel.add(Box.createVerticalStrut(0)); // Add spacing
        topPanel.add(instruction);
        topPanel.add(Box.createVerticalStrut(20)); // Add spacing
        topPanel.add(progressLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0)); //Padding for (top,left,bottom,right)
        topPanel.setPreferredSize(new Dimension(frameWidth, 300));

        // Center panel with task list
        taskList = new JList<>(taskListModel);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 20)); // Larger font for tasks
        taskList.setFixedCellHeight(70); // Increase task height for more spacing
        taskList.setCellRenderer(new TaskCellRenderer()); // Custom renderer for hover and color change
        taskList.setBackground(new Color(41, 41, 41)); // Light gray background for the task area
        taskList.setFixedCellHeight(90); // Adds spacing between tasks

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(null); // Remove default border
        // Apply custom scroll bar UI
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0)); // Adjust width of scrollbar
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 12)); // Adjust height for horizontal bar

        // Add MouseListener to clear selection when clicking outside the JList
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Check if the click occurred outside the JList bounds
                if (!taskList.getBounds().contains(e.getPoint())) {
                    taskList.clearSelection(); // Deselect any selected task
                }
            }
        });

        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());

        // Update the "Remove Finished Tasks" button
        JButton removeFinishedButton = new JButton("Delete Finished");
        removeFinishedButton.setHorizontalAlignment(SwingConstants.CENTER);  // Center text horizontally
        removeFinishedButton.setVerticalAlignment(SwingConstants.CENTER);    // Center text vertically
        removeFinishedButton.setFont(new Font("Microsoft Sans Serif ", Font.PLAIN, 16)); // Set a more modern font and size
        removeFinishedButton.setForeground(new Color(230, 230, 230)); // Text color
        removeFinishedButton.setBackground(new Color(60, 63, 65)); // Set a vibrant background color (orange-red)
        removeFinishedButton.setFocusPainted(false); // Remove the focus border when clicked
        removeFinishedButton.setPreferredSize(new Dimension(260, 100)); // Increase size for better visual balance
        removeFinishedButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); //Padding for (top,left,bottom,right)

        removeFinishedButton.setBorder(new RoundedBorder(100)); // Apply rounded corners (adjust radius as needed)

        JButton removeAllTasks = new JButton("Delete All Tasks");
        removeAllTasks.setHorizontalAlignment(SwingConstants.CENTER);  // Center text horizontally
        removeAllTasks.setVerticalAlignment(SwingConstants.CENTER);    // Center text vertically
        removeAllTasks.setFont(new Font("Microsoft Sans Serif ", Font.PLAIN, 16)); // Set a more modern font and size
        removeAllTasks.setForeground(new Color(230, 230, 230)); // Text color
        removeAllTasks.setBackground(new Color(60, 63, 65)); // Set a vibrant background color (orange-red)
        removeAllTasks.setFocusPainted(false); // Remove the focus border when clicked
        removeAllTasks.setPreferredSize(new Dimension(260, 100)); // Increase size for better visual balance
        removeAllTasks.setBorder(new RoundedBorder(100)); // Apply rounded corners (adjust radius as needed)

        JButton togglePriorityButton = new JButton("Toggle Priority (T)");
        togglePriorityButton.setHorizontalAlignment(SwingConstants.CENTER);  // Center text horizontally
        togglePriorityButton.setVerticalAlignment(SwingConstants.CENTER);    // Center text vertically

        togglePriorityButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex(); // Get the selected task's index

            if (selectedIndex >= 0) {
                Task selectedTask = tasks.get(selectedIndex); // Get the selected task

                // Count the number of unfinished tasks
                long unfinishedTaskCount = tasks.stream().filter(task -> !task.isCompleted()).count();

                // Block prioritization if this is the only unfinished task
                if (unfinishedTaskCount == 1 && !selectedTask.isPriority()) {
                    JOptionPane.showMessageDialog(frame, "The only task left is already a priority! 😊", "Error", JOptionPane.INFORMATION_MESSAGE);
                    return; // Exit without toggling priority
                }

                // Check if the task is completed
                if (selectedTask.isCompleted()) {
                    JOptionPane.showMessageDialog(frame, "Completed tasks cannot be prioritized.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Exit without toggling priority
                }

                tasks.remove(selectedIndex); // Remove the task from its current position

                if (selectedTask.isPriority()) {
                    // Unprioritize: Move to the end of non-priority tasks
                    selectedTask.setPriority(false);
                    int index = getLastNonCompletedIndex();
                    tasks.add(index, selectedTask);
                } else {
                    // Prioritize: Move to the top of the list
                    selectedTask.setPriority(true);
                    tasks.add(0, selectedTask);
                }

                refreshTaskList(); // Refresh the UI
            } else {
                JOptionPane.showMessageDialog(frame, "No task selected! Please select a task to toggle priority.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        togglePriorityButton.setFont(new Font("Microsoft Sans Serif ", Font.PLAIN, 16)); // Set a more modern font and size
        togglePriorityButton.setForeground(new Color(230, 230, 230)); // Text color
        togglePriorityButton.setBackground(new Color(60, 63, 65)); // Set a vibrant background color (orange-red)
        togglePriorityButton.setFocusPainted(false); // Remove the focus border when clicked
        togglePriorityButton.setPreferredSize(new Dimension(260, 100)); // Increase size for better visual balance
        togglePriorityButton.setBorder(new RoundedBorder(100)); // Apply rounded corners (adjust radius as needed)

        // Add a hover effect by using a MouseListener
        removeFinishedButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                removeFinishedButton.setBackground(new Color(80, 83, 85)); // Lighter orange on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                removeFinishedButton.setBackground(new Color(60, 63, 65)); // Original color when not hovered
            }
        });

        // Add a hover effect by using a MouseListener
        removeAllTasks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                removeAllTasks.setBackground(new Color(80, 83, 85)); // Lighter color on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                removeAllTasks.setBackground(new Color(60, 63, 65)); // Original color when not hovered
            }
        });

        bottomPanel.add(removeFinishedButton);
        //bottomPanel.add(togglePriorityButton);
        bottomPanel.add(removeAllTasks);
        bottomPanel.setBackground(new Color(41, 41, 41));

        // Add panels to the frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Automatically focus on the input field when the app opens
        SwingUtilities.invokeLater(taskInput::requestFocusInWindow);

        // Add Task with ENTER key
        taskInput.addActionListener(e -> {
            String taskTitle = taskInput.getText().trim();
            if (!taskTitle.isEmpty()) {
                Task newTask = new Task(taskTitle);

                // Check if the task already exists in the list
                if (tasks.contains(newTask)) {
                    JOptionPane.showMessageDialog(frame, "Task already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    tasks.add(newTask);
                    taskListModel.addElement(newTask);
                    taskInput.setText(""); // Clear input field
                    taskInput.requestFocusInWindow(); // Return focus to input field
                    arrangeTasks(); // Auto-arrange tasks
                    updateProgressLabel();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Task title cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add this KeyListener to the taskList
        taskList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int selectedIndex = taskList.getSelectedIndex(); // Get the selected task's index

                if (selectedIndex != -1) {
                    Task selectedTask = tasks.get(selectedIndex); // Get the selected task

                    // Handle 'T' for toggling priority
                    if (e.getKeyCode() == KeyEvent.VK_T) {
                        // Count the number of unfinished tasks
                        long unfinishedTaskCount = tasks.stream().filter(task -> !task.isCompleted()).count();

                        // Block prioritization if this is the only unfinished task
                        if (unfinishedTaskCount == 1 && !selectedTask.isPriority()) {
                            JOptionPane.showMessageDialog(
                                    frame,
                                    "The only unfinished task is already a priority 😊",
                                    "Error",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            return; // Exit without toggling priority
                        }

                        // Check if the task is completed
                        if (selectedTask.isCompleted()) {
                            JOptionPane.showMessageDialog(frame, "Completed tasks cannot be prioritized.", "Error", JOptionPane.ERROR_MESSAGE);
                            return; // Exit without toggling priority
                        }

                        tasks.remove(selectedIndex); // Remove the task from its current position

                        if (selectedTask.isPriority()) {
                            // Unprioritize: Move to the end of non-priority tasks
                            selectedTask.setPriority(false);
                            int index = getLastNonCompletedIndex();
                            tasks.add(index, selectedTask);
                        } else {
                            // Prioritize: Move to the top of the list
                            selectedTask.setPriority(true);
                            tasks.add(0, selectedTask);
                        }

                        refreshTaskList(); // Refresh the UI
                    }

                    // Handle 'F' for toggling completion
                    if (e.getKeyCode() == KeyEvent.VK_F) {
                        selectedTask.toggleCompletion(); // Toggle task completion
                        arrangeTasks(); // Auto-arrange tasks after toggling completion
                        updateProgressLabel();
                    }

                    // Handle 'D' for removing the task
                    if (e.getKeyCode() == KeyEvent.VK_D) {
                        tasks.remove(selectedIndex); // Remove the selected task
                        refreshTaskList(); // Refresh the list after removal
                        updateProgressLabel();

                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a task first.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Action for Remove Finished Tasks button
        removeFinishedButton.addActionListener(e -> {
            // Check if there are any finished tasks
            boolean hasFinishedTasks = tasks.stream().anyMatch(task -> task.isCompleted());

            if (tasks.isEmpty()) {
                // Display message with emoji if no tasks
                JOptionPane.showMessageDialog(frame, "No tasks to remove 😞", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else if (!hasFinishedTasks) {
                // Display message with emoji if no finished tasks
                JOptionPane.showMessageDialog(frame, "No finished tasks YET 😔", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Confirm removal with emoji in the message
                int confirm = JOptionPane.showConfirmDialog(frame, "Delete Finished Tasks ?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tasks.removeIf(task -> task.isCompleted());
                    refreshTaskList(); // Refresh the list to reflect the removal
                    updateProgressLabel();
                }
            }
        });

        // Action for Remove All Tasks button
        removeAllTasks.addActionListener(e -> {
            if (tasks.isEmpty()) {
                // Display message with emoji if no tasks
                JOptionPane.showMessageDialog(frame, "No tasks to remove 😞", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Confirm removal with emoji in the message
                // Message and title
                String message = "I hope you're not giving up 💔";
                String title = "Confirm";

                // Define custom options
                Object[] options = {"Delete", "Keep"};

                // Show the dialog with custom "Keep" and "Delete" buttons
                int confirm = JOptionPane.showOptionDialog(
                        frame, // Parent component (your frame)
                        message, // Message to display
                        title, // Title of the dialog
                        JOptionPane.DEFAULT_OPTION, // Use default option (we're using custom buttons)
                        JOptionPane.INFORMATION_MESSAGE, // Message type
                        null, // Icon (null means no custom icon)
                        options, // The custom options we defined
                        options[0] // Default selected option ("Keep")
                );

                // Handle the user's response
                if (confirm == 0) { // "Delete" button is clicked
                    tasks.clear(); // Clear all tasks
                    refreshTaskList(); // Refresh the list to reflect the removal
                    updateProgressLabel();
                }
            }
        });

        //make sure the input isnt focused as soon as u open the app
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // Remove the focus after the window opens
                SwingUtilities.invokeLater(() -> frame.requestFocus());
            }
        });

        // Display the frame
        frame.setVisible(true);
    }

    private void updateProgressLabel() {
        if (tasks.isEmpty()) {
            progressLabel.setText("0% Completed");
            return;
        }

        long completedTasks = tasks.stream().filter(Task::isCompleted).count();
        int percentage = (int) ((double) completedTasks / tasks.size() * 100);
        progressLabel.setText(percentage + "% Completed");
    }

    // Auto-arrange tasks so completed tasks appear at the end
    private void arrangeTasks() {
        // Sort tasks such that incomplete tasks appear first, and completed tasks at the end
        tasks.sort((t1, t2) -> Boolean.compare(t1.isCompleted(), t2.isCompleted()));
        refreshTaskList();
    }

    private void refreshTaskList() {
        taskListModel.clear(); // Clear the current list

        // Separate tasks into categories
        List<Task> priorityTasks = new ArrayList<>();
        List<Task> nonPriorityTasks = new ArrayList<>();
        List<Task> completedTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.isCompleted()) {
                completedTasks.add(task);
            } else if (task.isPriority()) {
                priorityTasks.add(task);
            } else {
                nonPriorityTasks.add(task);
            }
        }

        // Reorder and update the model
        priorityTasks.forEach(taskListModel::addElement);
        nonPriorityTasks.forEach(taskListModel::addElement);
        completedTasks.forEach(taskListModel::addElement);
    }

    private int getLastNonCompletedIndex() {
        // Loop through the tasks list in reverse to find the last non-completed task
        for (int i = tasks.size() - 1; i >= 0; i--) {
            Task task = tasks.get(i);
            if (!task.isCompleted()) {
                return i + 1; // Return the index just after the last non-completed task
            }
        }
        return tasks.size(); // If all tasks are completed, return the end of the list
    }

    // Custom renderer for hover effects and task colors
    private class TaskCellRenderer extends DefaultListCellRenderer {

        private int hoveredIndex = -1;

        public TaskCellRenderer() {
            // Handle mouse motion
            taskList.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int index = taskList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        Rectangle bounds = taskList.getCellBounds(index, index);
                        if (bounds != null && bounds.contains(e.getPoint())) {
                            if (index != hoveredIndex) {
                                hoveredIndex = index;
                                taskList.repaint();
                            }
                        } else {
                            resetHover();
                        }
                    } else {
                        resetHover();
                    }
                }
            });

            // Reset hover state when the mouse exits
            taskList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    resetHover();
                }
            });
        }

        private void resetHover() {
            if (hoveredIndex != -1) {
                hoveredIndex = -1;
                taskList.repaint();
            }
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Task task = (Task) value;

            // Create the main panel for the task
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 70, 70);
                }
            };

            panel.setOpaque(false);
            panel.setLayout(new BorderLayout());
            panel.setPreferredSize(new Dimension(550, 70));

            // Create a container panel for the task text and add padding
            JPanel taskPanel = new JPanel(new BorderLayout());
            taskPanel.setOpaque(false);

            // Label for the task text
            JLabel label = new JLabel(task.toString(), SwingConstants.LEFT);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            label.setBorder(BorderFactory.createEmptyBorder(0, 25, 28, 0)); // Padding for (top,left,bottom,right)
            label.setVerticalAlignment(SwingConstants.CENTER); // Center the text vertically
            taskPanel.add(label, BorderLayout.CENTER);

            // Add the date label at the top inside its own panel
            JPanel datePanel = new JPanel(new BorderLayout());
            datePanel.setOpaque(false);

            // Date label without padding
            JLabel dateLabel = new JLabel(task.getFormattedDate(), SwingConstants.RIGHT);
            dateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            dateLabel.setForeground(new Color(200, 200, 200));
            dateLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 32)); // Padding for (top,left,bottom,right)
            datePanel.add(dateLabel, BorderLayout.CENTER);

            // Add both taskPanel and datePanel to the main panel
            panel.add(datePanel, BorderLayout.NORTH);
            panel.add(taskPanel, BorderLayout.CENTER);

            panel.add(dateLabel, BorderLayout.NORTH);

            // Priority task styling (overrides all other states)
            if (task.isCompleted()) {
                // Completed tasks take precedence over priority
                if (isSelected) {
                    panel.setBackground(new Color(60, 150, 150)); // Selection color for completed tasks
                    label.setForeground(Color.WHITE);
                } else if (index == hoveredIndex) {
                    panel.setBackground(new Color(86, 178, 112)); // Hover color for completed tasks
                    label.setForeground(new Color(15, 15, 15)); // Dark text for contrast
                    dateLabel.setForeground(new Color(15, 15, 15));

                } else {
                    panel.setBackground(new Color(101, 206, 131)); // Default color for completed tasks
                    label.setForeground(new Color(15, 15, 15)); // Dark text for contrast
                    dateLabel.setForeground(new Color(15, 15, 15));

                }
            } else if (task.isPriority()) {
                // Priority tasks styling
                if (isSelected) {
                    panel.setBackground(new Color(108, 0, 193)); // Selection color for priority tasks
                    label.setForeground(Color.WHITE);
                } else if (index == hoveredIndex) {
                    panel.setBackground(new Color(253, 104, 120)); // Hover color for priority tasks
                    label.setForeground(Color.WHITE);
                } else {
                    panel.setBackground(new Color(160, 46, 46)); // Default color for priority tasks
                    label.setForeground(Color.WHITE);
                }
            } else if (isSelected) {
                // Non-priority selected tasks
                panel.setBackground(new Color(100, 102, 255)); // Selection color for uncompleted tasks
                label.setForeground(Color.WHITE);
            } else if (index == hoveredIndex) {
                // Hover state for non-priority, incomplete tasks
                panel.setBackground(new Color(80, 83, 85)); // Hover color for incomplete tasks
                label.setForeground(Color.WHITE);
            } else {
                // Default state for non-priority, incomplete tasks
                panel.setBackground(new Color(60, 63, 65)); // Default background for incomplete tasks
                label.setForeground(Color.WHITE);
            }

            // Outer container to center the panel
            JPanel container = new JPanel(new GridBagLayout());
            container.setBackground(new Color(41, 41, 41));
            container.add(panel);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 0, 0, 0); // Top, left, bottom, right padding
            container.add(panel, gbc);

            return container;
        }

    }

    static class RoundedBorder implements Border {

        private final int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 50, 0, 50);
        }

        @Override
        public boolean isBorderOpaque() {
            return false; // Border should be transparent
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Set the border color (for visibility)
            g2d.setColor(new Color(41, 41, 41)); // Match the field's background color

            // Draw the rounded border
            g2d.setStroke(new BasicStroke(40)); // Optional: set a thicker border stroke
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    public static class JTextFieldLimit extends PlainDocument {

        private final int limit;

        public JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) {
                return;
            }

            // Limit the number of characters that can be inserted
            if ((getLength() + str.length()) <= limit) {
                super.insertString(offs, str, a);
            }
        }
    }

    public void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tasks.ser"))) {
            oos.writeObject(tasks); // Save the list of tasks to a file
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving tasks", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTasks() {
        File file = new File("tasks.ser");
        if (file.exists()) { // Check if the file exists
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                tasks = (ArrayList<Task>) in.readObject();
                refreshTaskList();
                updateProgressLabel(); // Update the progress label based on loaded tasks
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Error loading tasks", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // If the file doesn't exist, start with an empty task list (no error message)
            tasks = new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        // Set Flatlaf as the Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());  // You can also use FlatDarkLaf() for dark mode
        } catch (UnsupportedLookAndFeelException e) {
            //e.printStackTrace();
        }

        // Run the application
        SwingUtilities.invokeLater(() -> {
            TodoAppSwing app = new TodoAppSwing();
            app.loadTasks(); // Load the tasks when the app starts
        });
    }
}
