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

    private static final Color APP_BG = new Color(241, 244, 249);
    private static final Color SIDEBAR_BG = new Color(255, 255, 255);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color PRIMARY = new Color(33, 132, 255);
    private static final Color PRIMARY_DARK = new Color(20, 95, 190);
    private static final Color MUTED_TEXT = new Color(105, 112, 123);
    private static final Color MAIN_TEXT = new Color(29, 33, 40);

    private final LoginController loginController = new LoginController();
    private final AdminController adminController = new AdminController();
    private final ExamController examController = new ExamController();
    private final ExamDAO examDAO = new ExamDAO();
    private final ResultDAO resultDAO = new ResultDAO();

    private final CardLayout appCards = new CardLayout();
    private final JPanel appContainer = new JPanel(appCards);

    private User currentUser;
    private JLabel roleTitleLabel;
    private DefaultListModel<String> examModel;
    private DefaultListModel<String> resultModel;
    private JList<String> examList;

    public ModernExamApp() {
        setTitle("CAPSTONE PROJECT JAVA");
        setSize(1280, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        appContainer.add(buildAuthScreen(), "AUTH");
        appContainer.add(buildDashboardScreen(), "DASH");

        add(appContainer);
        appCards.show(appContainer, "AUTH");
    }

    private JPanel buildAuthScreen() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(APP_BG);

        JPanel authCard = roundedPanel(new BorderLayout(0, 20));
        authCard.setPreferredSize(new Dimension(540, 590));
        authCard.setBorder(new EmptyBorder(26, 28, 26, 28));

        JLabel title = new JLabel("CAPSTONE PROJECT JAVA", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(MAIN_TEXT);

        JLabel subtitle = new JLabel("Online Examination Management", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(MUTED_TEXT);

        JPanel heading = new JPanel(new GridLayout(2, 1));
        heading.setOpaque(false);
        heading.add(title);
        heading.add(subtitle);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.addTab("Login", buildLoginTab());
        tabs.addTab("Register", buildRegisterTab());

        authCard.add(heading, BorderLayout.NORTH);
        authCard.add(tabs, BorderLayout.CENTER);
        root.add(authCard);
        return root;
    }

    private JPanel buildLoginTab() {
        JPanel panel = formPanel();
        GridBagConstraints gbc = formGbc();

        JTextField emailField = styledTextField();
        JPasswordField passwordField = styledPasswordField();

        addFormField(panel, gbc, "Email", emailField, 0);
        addFormField(panel, gbc, "Password", passwordField, 2);

        JButton loginButton = primaryButton("Sign In");
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            User user = loginController.login(emailField.getText().trim(), new String(passwordField.getPassword()));
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentUser = user;
            roleTitleLabel.setText("admin".equalsIgnoreCase(user.getRole()) ? "Admin Workspace" : "Student Workspace");
            refreshDataModels();
            appCards.show(appContainer, "DASH");
        });

        return panel;
    }

    private JPanel buildRegisterTab() {
        JPanel panel = formPanel();
        GridBagConstraints gbc = formGbc();

        JTextField nameField = styledTextField();
        JTextField emailField = styledTextField();
        JPasswordField passwordField = styledPasswordField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"student", "admin"});

        addFormField(panel, gbc, "Full Name", nameField, 0);
        addFormField(panel, gbc, "Email", emailField, 2);
        addFormField(panel, gbc, "Password", passwordField, 4);
        addFormField(panel, gbc, "Role", roleBox, 6);

        JButton registerBtn = primaryButton("Create Account");
        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(registerBtn, gbc);

        registerBtn.addActionListener(e -> {
            boolean registered = loginController.register(
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    new String(passwordField.getPassword()),
                    String.valueOf(roleBox.getSelectedItem())
            );

            JOptionPane.showMessageDialog(
                    this,
                    registered ? "Registration successful. You can now login." : "Registration failed.",
                    registered ? "Success" : "Error",
                    registered ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
            );
        });

        return panel;
    }

    private JPanel buildDashboardScreen() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(APP_BG);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainContent(), BorderLayout.CENTER);

        return root;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(new EmptyBorder(20, 18, 20, 18));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel appName = new JLabel("CAPSTONE");
        appName.setFont(new Font("SansSerif", Font.BOLD, 32));
        appName.setForeground(PRIMARY_DARK);

        JLabel projectLabel = new JLabel("PROJECT JAVA");
        projectLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        projectLabel.setForeground(MAIN_TEXT);

        top.add(appName);
        top.add(projectLabel);
        top.add(Box.createVerticalStrut(26));

        top.add(sidebarItem("Dashboard"));
        top.add(sidebarItem("All Exams"));
        top.add(sidebarItem("Question Bank"));
        top.add(sidebarItem("Results"));
        top.add(sidebarItem("Settings"));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton logoutButton = secondaryButton("Logout");
        logoutButton.addActionListener(e -> {
            currentUser = null;
            appCards.show(appContainer, "AUTH");
        });

        bottom.add(logoutButton, BorderLayout.CENTER);

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel buildMainContent() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(APP_BG);
        content.setBorder(new EmptyBorder(16, 18, 16, 20));

        content.add(buildHeroBanner(), BorderLayout.NORTH);
        content.add(buildWorkspace(), BorderLayout.CENTER);

        return content;
    }

    private JPanel buildHeroBanner() {
        GradientPanel banner = new GradientPanel();
        banner.setLayout(new BorderLayout());
        banner.setPreferredSize(new Dimension(0, 130));
        banner.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel mini = new JLabel("Smart exam management for administrators and students");
        mini.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mini.setForeground(new Color(54, 54, 66));

        roleTitleLabel = new JLabel("Workspace");
        roleTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        roleTitleLabel.setForeground(new Color(20, 20, 28));

        left.add(mini);
        left.add(Box.createVerticalStrut(4));
        left.add(roleTitleLabel);

        JButton helperButton = new JButton("AI Assistant");
        helperButton.setFocusPainted(false);
        helperButton.setBackground(new Color(247, 248, 255));
        helperButton.setForeground(new Color(52, 58, 72));
        helperButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        banner.add(left, BorderLayout.WEST);
        banner.add(helperButton, BorderLayout.EAST);

        return banner;
    }

    private JPanel buildWorkspace() {
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);

        JPanel menu = roundedPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JButton refresh = secondaryButton("Refresh Data");
        JButton takeExam = primaryButton("Take Selected Exam");
        JButton createExam = primaryButton("Create Exam");
        JButton addQuestion = secondaryButton("Add Question");

        menu.add(refresh);
        menu.add(takeExam);
        menu.add(createExam);
        menu.add(addQuestion);

        JPanel cards = new JPanel(new GridLayout(1, 3, 14, 0));
        cards.setOpaque(false);

        examModel = new DefaultListModel<>();
        resultModel = new DefaultListModel<>();

        examList = new JList<>(examModel);
        examList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cards.add(featureCard("All Exams", "Browse available exams.", new JScrollPane(examList)));
        cards.add(featureCard("Your Results", "Track your latest scores.", new JScrollPane(new JList<>(resultModel))));
        cards.add(featureCard("Quick Notes", "Use actions above to create exams/questions.", infoPanel()));

        refresh.addActionListener(e -> refreshDataModels());

        takeExam.addActionListener(e -> {
            if (currentUser == null || "admin".equalsIgnoreCase(currentUser.getRole())) {
                JOptionPane.showMessageDialog(this, "Only students can take exams.");
                return;
            }
            String selected = getSelectedExamText();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select an exam from the All Exams card.");
                return;
            }
            int examId = Integer.parseInt(selected.split(" ")[0]);
            launchExam(examId);
            refreshDataModels();
        });

        createExam.addActionListener(e -> {
            if (currentUser == null || !"admin".equalsIgnoreCase(currentUser.getRole())) {
                JOptionPane.showMessageDialog(this, "Only admins can create exams.");
                return;
            }
            showCreateExamDialog();
            refreshDataModels();
        });

        addQuestion.addActionListener(e -> {
            if (currentUser == null || !"admin".equalsIgnoreCase(currentUser.getRole())) {
                JOptionPane.showMessageDialog(this, "Only admins can add questions.");
                return;
            }
            showAddQuestionDialog();
        });

        body.add(menu, BorderLayout.NORTH);
        body.add(cards, BorderLayout.CENTER);
        return body;
    }

    private String getSelectedExamText() {
        return examList == null ? null : examList.getSelectedValue();
    }

    private JPanel infoPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(infoItem("• Blue actions are primary operations."));
        panel.add(infoItem("• Use Refresh Data to sync with database."));
        panel.add(infoItem("• Student and admin permissions are separated."));
        panel.add(infoItem("• App Name: CAPSTONE PROJECT JAVA."));

        return panel;
    }

    private JLabel infoItem(String text) {
        JLabel item = new JLabel(text);
        item.setFont(new Font("SansSerif", Font.PLAIN, 14));
        item.setForeground(MUTED_TEXT);
        item.setBorder(new EmptyBorder(8, 0, 0, 0));
        return item;
    }

    private JPanel featureCard(String title, String subtitle, JComponent content) {
        JPanel card = roundedPanel(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(MAIN_TEXT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(MUTED_TEXT);

        header.add(titleLabel);
        header.add(subtitleLabel);

        card.add(header, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JPanel roundedPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JPanel formPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        return panel;
    }

    private GridBagConstraints formGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        return gbc;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        JLabel label = new JLabel(labelText);
        label.setForeground(MAIN_TEXT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(label, gbc);

        gbc.gridy = row + 1;
        panel.add(field, gbc);
    }

    private JTextField styledTextField() {
        JTextField field = new JTextField(24);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return field;
    }

    private JPasswordField styledPasswordField() {
        JPasswordField field = new JPasswordField(24);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return field;
    }

    private JLabel sidebarItem(String text) {
        JLabel item = new JLabel(text);
        item.setFont(new Font("SansSerif", Font.PLAIN, 16));
        item.setForeground(new Color(76, 83, 95));
        item.setBorder(new EmptyBorder(12, 4, 12, 4));
        return item;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(PRIMARY_DARK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        return button;
    }

    private void refreshDataModels() {
        if (examModel != null) {
            examModel.clear();
            for (Exam exam : examDAO.getAllExams()) {
                examModel.addElement(exam.getId() + " - " + exam.getTitle() + " (" + exam.getDuration() + " min)");
            }
        }

        if (resultModel != null) {
            resultModel.clear();
            if (currentUser != null) {
                for (Result r : resultDAO.getResultsByStudent(currentUser.getId())) {
                    resultModel.addElement("Exam " + r.getExamId() + " | Score " + r.getScore() + " | " + r.getExamDate());
                }
            }
        }
    }

    private void showCreateExamDialog() {
        JTextField titleField = new JTextField();
        JTextField durationField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 6));
        panel.add(new JLabel("Exam Title"));
        panel.add(titleField);
        panel.add(new JLabel("Duration (minutes)"));
        panel.add(durationField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create Exam", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                boolean created = adminController.createExam(titleField.getText().trim(), Integer.parseInt(durationField.getText().trim()));
                JOptionPane.showMessageDialog(this, created ? "Exam created." : "Failed to create exam.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Duration must be numeric.");
            }
        }
    }

    private void showAddQuestionDialog() {
        JTextField examId = new JTextField();
        JTextField question = new JTextField();
        JTextField a = new JTextField();
        JTextField b = new JTextField();
        JTextField c = new JTextField();
        JTextField d = new JTextField();
        JComboBox<String> correct = new JComboBox<>(new String[]{"A", "B", "C", "D"});

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 5));
        panel.add(new JLabel("Exam ID"));
        panel.add(examId);
        panel.add(new JLabel("Question"));
        panel.add(question);
        panel.add(new JLabel("Option A"));
        panel.add(a);
        panel.add(new JLabel("Option B"));
        panel.add(b);
        panel.add(new JLabel("Option C"));
        panel.add(c);
        panel.add(new JLabel("Option D"));
        panel.add(d);
        panel.add(new JLabel("Correct Answer"));
        panel.add(correct);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Question", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                boolean added = adminController.addQuestion(
                        Integer.parseInt(examId.getText().trim()),
                        question.getText().trim(),
                        a.getText().trim(),
                        b.getText().trim(),
                        c.getText().trim(),
                        d.getText().trim(),
                        String.valueOf(correct.getSelectedItem())
                );
                JOptionPane.showMessageDialog(this, added ? "Question added." : "Failed to add question.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Exam ID must be numeric.");
            }
        }
    }

    private void launchExam(int examId) {
        if (currentUser == null) {
            return;
        }

        List<Question> questions = examController.getExamQuestions(examId);
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions available for selected exam.");
            return;
        }

        Map<Integer, String> answers = new HashMap<>();

        for (Question q : questions) {
            String[] options = new String[]{
                    "A. " + q.getOptionA(),
                    "B. " + q.getOptionB(),
                    "C. " + q.getOptionC(),
                    "D. " + q.getOptionD()
            };

            String selected = (String) JOptionPane.showInputDialog(
                    this,
                    q.getQuestionText(),
                    "Exam Question",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (selected != null) {
                answers.put(q.getId(), selected.substring(0, 1));
            }
        }

        int score = examController.calculateScore(questions, answers);
        boolean saved = examController.saveResult(currentUser.getId(), examId, score);
        JOptionPane.showMessageDialog(this,
                "Score: " + score + " / " + questions.size() + (saved ? "\nResult saved." : "\nResult save failed."),
                "Completed",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(206, 221, 255),
                    getWidth(), getHeight(), new Color(241, 202, 229)
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
