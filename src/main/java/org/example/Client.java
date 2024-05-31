package org.example;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
/*Combine check boxes, labels, and text boxes into one component;
provide two methods respectively for getting text and selected states
*/
public class Client extends JFrame {
    public static JTextArea textarea;
    // In total columns to display.
    private final int COLUMN = 7;
    // column names
    private final List<String> TITLES = Arrays.asList( "id",
            "dishTitle", "difficulty", "cuisineType", "year", "instruction", "cookingTime");
    // dataModel is a container to store the data to display.
    private Vector<Vector<String>> dataModel = new Vector<Vector<String>>();
    private QueryItem id = new QueryItem("ID：", 10);
    private QueryItem dishTitle = new QueryItem("Title：", 10);
    private QueryItem difficulty = new QueryItem("Difficulty：", 5);
    private QueryItem2 cookingTime = new QueryItem2("cookingTime From：", "to", 5);
    private QueryItem cuisineType = new QueryItem("Type：", 5);
    private QueryItem year = new QueryItem("Year：", 5);
    private QueryItem instruction = new QueryItem("Instruction：", 20);
    private JButton readBtn = new JButton("Search");
    private JButton updateBtn = new JButton("Update");
    private JButton createBtn = new JButton("Create");
    private JButton deleteBtn = new JButton("Delete");

    public static JTable table;
    // This is the database connection.
    private Connection conn;
    public Client(String title) {
        super(title);

        Vector<String> titles = new Vector<String>(TITLES);
        // Bind table with dataModel to display data;
        table = new JTable(dataModel, titles);
        // Set column names to the table.
        table.getColumnModel().getColumn(0).setPreferredWidth(20);
        table.getColumnModel().getColumn(1).setPreferredWidth(20);
        table.getColumnModel().getColumn(2).setPreferredWidth(20);
        table.getColumnModel().getColumn(3).setPreferredWidth(20);
        table.getColumnModel().getColumn(4).setPreferredWidth(20);
        table.getColumnModel().getColumn(5).setPreferredWidth(210);
        table.getColumnModel().getColumn(6).setPreferredWidth(20);
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(id);
        controlPanel.add(dishTitle);
        controlPanel.add(difficulty);
        controlPanel.add(cookingTime);
        controlPanel.add(cuisineType);
        controlPanel.add(year);
        controlPanel.add(instruction);
        controlPanel.add(readBtn);
        controlPanel.add(updateBtn);
        controlPanel.add(createBtn);
        controlPanel.add(deleteBtn);
        controlPanel.setPreferredSize(new Dimension(0, 130));

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        tablePanel.add(table.getTableHeader());
        tablePanel.add(new JScrollPane(table));

        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.add(tablePanel, BorderLayout.CENTER);

        this.add(controlPanel, BorderLayout.NORTH);
        this.add(container, BorderLayout.CENTER);
        this.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.WEST);
        this.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.EAST);
        this.add(Box.createRigidArea(new Dimension(0, 20)), BorderLayout.SOUTH);
        // Use DAO static method to get db connection.
        conn = DAO.connectToDB();
        setActionListener();
    }

    private void setActionListener() {
        // Read button listener here
        readBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // This is to store the sql query sentences.
                ArrayList<String> conditions = new ArrayList<String>();
                if (id.isSelected()) {
                    conditions.add("(id = '" + id.getText() + "')");
                }
                if (dishTitle.isSelected()) {
                    conditions.add("(dishTitle = '" + dishTitle.getText() + "')");
                }
                if (difficulty.isSelected()) {
                    conditions.add("(difficulty = '" + difficulty.getText() + "')");
                }
                if (cuisineType.isSelected()) {
                    conditions.add("(cuisineType = '" + cuisineType.getText() + "')");
                }
                if (year.isSelected()) {
                    conditions.add("(year = '" + year.getText() + "')");
                }
                if (instruction.isSelected()) {
                    conditions.add("(instruction = '" + instruction.getText() + "')");
                }
                if (cookingTime.isSelected()) {
                    conditions.add("(cookingTime >= " + cookingTime.getText() + " AND " + "cookingTime <= " + cookingTime.getText2() + ")");
                }
                // Put sql together
                StringBuilder sb = new StringBuilder();
                sb.append("select * from recipe");
                if (conditions.size() > 0) {
                    sb.append(" where ");
                }
                for (int i = 0; i < conditions.size() - 1; i ++) {
                    sb.append(conditions.get(i));
                    sb.append(" AND ");
                }
                if (conditions.size() > 0) {
                    sb.append(conditions.get(conditions.size() - 1));
                }
                sb.append(";");
                String queryString = sb.toString();
                // Refresh the table.
                dataModel.clear();
                // Execute the query.
                ResultSet rs = DAO.executeQuery(conn, queryString);
                Vector<String> record;
                try {
                    // Add data into data model to be shown in the table
                    while (rs.next()) {
                        record = new Vector<String>();
                        for (int i = 0; i < COLUMN; i++) {
                            record.add(rs.getString(i + 1));
                        }
                        dataModel.add(record);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                table.validate();
                table.updateUI();
            }
        });

        // Create button listener here;
        createBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sql = "insert into recipe values (?,?,?,?,?,?,?);";
                PreparedStatement ps;
                // Use prepareStatement to substitute question mark with values and excute it.
                try {
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, id.getText());
                    ps.setString(2, dishTitle.getText());
                    ps.setString(3, difficulty.getText());
                    ps.setString(4, cuisineType.getText());
                    ps.setString(5, year.getText());
                    ps.setString(6, instruction.getText());
                    ps.setInt(7, Integer.parseInt(cookingTime.getText()));
                    ps.executeUpdate();
                    // Add data in the table.
                    dataModel.add(new Vector<String>(Arrays.asList(
                            id.getText(), dishTitle.getText(), difficulty.getText(), cuisineType.getText(), year.getText(), instruction.getText(), cookingTime.getText())));

                    table.validate();
                    table.updateUI();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Delete button listener here.
        deleteBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                String id = dataModel.get(row).get(0);
                // Delete the row that is selected in the table.
                String sql = "delete from recipe where id = '" + id + "';";
                Statement stmt;
                try {
                    stmt = conn.createStatement();
                    if (stmt.executeUpdate(sql) == 0) return;
                    dataModel.remove(row);

                    // refresh the table.
                    table.validate();
                    table.updateUI();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Update button listener here.
        updateBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected row number and column number.
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                if (row == -1 || column == 0) {
                    return;
                }
                // Get the updated value from the table.
                String value = dataModel.get(row).get(column);
                // Get the id from that row.
                String id = dataModel.get(row).get(0);
                // Delete it in database.
                String sql = "update recipe set " + TITLES.get(column) + " = ? where id = ?;";

                // Use PreparedStatement to execute 'delete' command.
                PreparedStatement ps;
                try {
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, value);
                    ps.setString(2, id);
                    ps.executeUpdate();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}