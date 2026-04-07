package ui;

import controller.AdminController;
import controller.ExamController;
import controller.LoginController;
import dao.ExamDAO;
import dao.ResultDAO;
import model.Exam;
import model.Question;
import model.Result;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModernExamApp extends JFrame {

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color PRIMARY = new Color(76, 110, 245);
    private static final Color PRIMARY_DARK = new Color(54, 79, 199);
    private static final Color TEXT = new Color(33, 37, 41);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 26);
    private static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 14);

    private final LoginController loginController;
    private final AdminController adminController;
    private final ExamController examController;
    private final ExamDAO examDAO;
    private final ResultDAO resultDAO;

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    private User currentUser;

    public ModernExamApp() {
        this.loginController = new LoginController();
        this.adminController = new AdminController();
        this.examController = new ExamController();
        this.examDAO = new ExamDAO();
        this.resultDAO = new ResultDAO();

        setTitle("Online Exam System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BACKGROUND);

        contentPanel.add(buildAuthPanel(), "AUTH");
        contentPanel.add(buildAdminPanel(), "ADMIN");
        contentPanel.add(buildStudentPanel(), "STUDENT");

        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "AUTH");
    }

    private JPanel buildAuthPanel() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BACKGROUND);

        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setPreferredSize(new Dimension(440, 500));
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setBackground(CARD);

        JLabel title = new JLabel("Welcome to Online Exam", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(BODY_FONT);

        tabs.addTab("Login", createLoginTab());
        tabs.addTab("Register", createRegisterTab());

        card.add(title, BorderLayout.NORTH);
        card.add(tabs, BorderLayout.CENTER);

        root.add(card);
        return root;
    }

    private JPanel createLoginTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = baseGbc();

        JTextField emailField = textField();
        JPasswordField passwordField = passwordField();

        addField(panel, gbc, "Email", emailField, 0);
        addField(panel, gbc, "Password", passwordField, 2);

        JButton loginBtn = primaryButton("Sign In");
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            User user = loginController.login(email, password);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.currentUser = user;
            if ("admin".equalsIgnoreCase(user.getRole())) {
                cardLayout.show(contentPanel, "ADMIN");
            } else {
                cardLayout.show(contentPanel, "STUDENT");
            }
        });

        return panel;
    }

    private JPanel createRegisterTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = baseGbc();

        JTextField nameField = textField();
        JTextField emailField = textField();
        JPasswordField passwordField = passwordField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"student", "admin"});

        addField(panel, gbc, "Name", nameField, 0);
        addField(panel, gbc, "Email", emailField, 2);
        addField(panel, gbc, "Password", passwordField, 4);
        addField(panel, gbc, "Role", roleBox, 6);

        JButton registerBtn = primaryButton("Create Account");
        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(registerBtn, gbc);

        registerBtn.addActionListener(e -> {
            boolean ok = loginController.register(
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    new String(passwordField.getPassword()),
                    String.valueOf(roleBox.getSelectedItem())
            );
            if (ok) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel buildAdminPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        root.add(createHeader("Admin Dashboard", true), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Create Exam", createExamForm());
        tabs.addTab("Add Question", createQuestionForm());
        root.add(tabs, BorderLayout.CENTER);

        return root;
    }

    private JPanel createExamForm() {
        JPanel panel = createCardPanel();
        GridBagConstraints gbc = baseGbc();

        JTextField titleField = textField();
        JTextField durationField = textField();

        addField(panel, gbc, "Exam Title", titleField, 0);
        addField(panel, gbc, "Duration (minutes)", durationField, 2);

        JButton saveBtn = primaryButton("Save Exam");
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                int duration = Integer.parseInt(durationField.getText().trim());
                boolean ok = adminController.createExam(title, duration);
                JOptionPane.showMessageDialog(this, ok ? "Exam created." : "Failed to create exam.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Duration must be a number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    private JComponent createQuestionForm() {
        JPanel panel = createCardPanel();
        GridBagConstraints gbc = baseGbc();

        JTextField examIdField = textField();
        JTextField questionField = textField();
        JTextField optionA = textField();
        JTextField optionB = textField();
        JTextField optionC = textField();
        JTextField optionD = textField();
        JComboBox<String> answer = new JComboBox<>(new String[]{"A", "B", "C", "D"});

        addField(panel, gbc, "Exam ID", examIdField, 0);
        addField(panel, gbc, "Question", questionField, 2);
        addField(panel, gbc, "Option A", optionA, 4);
        addField(panel, gbc, "Option B", optionB, 6);
        addField(panel, gbc, "Option C", optionC, 8);
        addField(panel, gbc, "Option D", optionD, 10);
        addField(panel, gbc, "Correct Answer", answer, 12);

        JButton saveBtn = primaryButton("Add Question");
        gbc.gridy = 14;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            try {
                boolean ok = adminController.addQuestion(
                        Integer.parseInt(examIdField.getText().trim()),
                        questionField.getText().trim(),
                        optionA.getText().trim(),
                        optionB.getText().trim(),
                        optionC.getText().trim(),
                        optionD.getText().trim(),
                        String.valueOf(answer.getSelectedItem())
                );
                JOptionPane.showMessageDialog(this, ok ? "Question added." : "Failed to add question.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Exam ID must be numeric.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });

        return new JScrollPane(panel) {
            {
                setBorder(null);
                getViewport().setBackground(BACKGROUND);
            }
        };
    }

    private JPanel buildStudentPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        root.add(createHeader("Student Dashboard", true), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 14, 0));
        center.setOpaque(false);

        DefaultListModel<String> examListModel = new DefaultListModel<>();
        JList<String> examList = new JList<>(examListModel);
        examList.setFont(BODY_FONT);
        examList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton refreshBtn = secondaryButton("Refresh Exams");
        JButton takeBtn = primaryButton("Start Selected Exam");

        JPanel left = createCardPanel();
        left.setLayout(new BorderLayout(0, 10));
        left.add(new JLabel("Available Exams"), BorderLayout.NORTH);
        left.add(new JScrollPane(examList), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.setOpaque(false);
        btns.add(refreshBtn);
        btns.add(takeBtn);
        left.add(btns, BorderLayout.SOUTH);

        DefaultListModel<String> resultModel = new DefaultListModel<>();
        JList<String> resultsList = new JList<>(resultModel);
        resultsList.setFont(BODY_FONT);

        JPanel right = createCardPanel();
        right.setLayout(new BorderLayout(0, 10));
        right.add(new JLabel("Your Results"), BorderLayout.NORTH);
        right.add(new JScrollPane(resultsList), BorderLayout.CENTER);

        center.add(left);
        center.add(right);
        root.add(center, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> {
            loadExams(examListModel);
            loadResults(resultModel);
        });

        takeBtn.addActionListener(e -> {
            String selected = examList.getSelectedValue();
            if (selected == null || currentUser == null) {
                JOptionPane.showMessageDialog(this, "Select an exam first.", "No Exam", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int examId = Integer.parseInt(selected.split(" ")[0]);
            startExam(examId);
            loadResults(resultModel);
        });

        return root;
    }

    private void startExam(int examId) {
        List<Question> questions = examController.getExamQuestions(examId);
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions found for selected exam.", "Empty Exam", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Map<Integer, String> answers = new HashMap<>();
        for (Question q : questions) {
            String[] options = {
                    "A. " + q.getOptionA(),
                    "B. " + q.getOptionB(),
                    "C. " + q.getOptionC(),
                    "D. " + q.getOptionD()
            };
            String selected = (String) JOptionPane.showInputDialog(
                    this,
                    q.getQuestionText(),
                    "Question " + q.getId(),
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (selected != null && !selected.isBlank()) {
                answers.put(q.getId(), selected.substring(0, 1));
            }
        }

        int score = examController.calculateScore(questions, answers);
        boolean saved = examController.saveResult(currentUser.getId(), examId, score);
        JOptionPane.showMessageDialog(this,
                "You scored " + score + " out of " + questions.size() + (saved ? "\nResult saved." : "\nCould not save result."),
                "Exam Completed",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createHeader(String titleText, boolean showLogout) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 14, 10, 14));
        header.setBackground(CARD);

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);

        if (showLogout) {
            JButton logoutBtn = secondaryButton("Logout");
            logoutBtn.addActionListener(e -> {
                currentUser = null;
                cardLayout.show(contentPanel, "AUTH");
            });
            header.add(logoutBtn, BorderLayout.EAST);
        }

        return header;
    }

    private void loadExams(DefaultListModel<String> model) {
        model.clear();
        List<Exam> exams = examDAO.getAllExams();
        for (Exam exam : exams) {
            model.addElement(exam.getId() + " - " + exam.getTitle() + " (" + exam.getDuration() + " min)");
        }
    }

    private void loadResults(DefaultListModel<String> model) {
        model.clear();
        if (currentUser == null) {
            return;
        }
        List<Result> results = resultDAO.getResultsByStudent(currentUser.getId());
        for (Result result : results) {
            model.addElement("Exam " + result.getExamId() + " | Score: " + result.getScore() + " | Date: " + result.getExamDate());
        }
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);
        panel.setBackground(CARD);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        return gbc;
    }

    private JTextField textField() {
        JTextField field = new JTextField(22);
        field.setFont(BODY_FONT);
        return field;
    }

    private JPasswordField passwordField() {
        JPasswordField field = new JPasswordField(22);
        field.setFont(BODY_FONT);
        return field;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        JLabel label = new JLabel(labelText);
        label.setForeground(TEXT);
        label.setFont(BODY_FONT);

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(label, gbc);

        gbc.gridy = row + 1;
        panel.add(field, gbc);
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, PRIMARY, Color.WHITE);
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, PRIMARY_DARK, Color.WHITE);
        return button;
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
