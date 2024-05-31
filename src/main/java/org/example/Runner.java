package org.example;
import java.awt.Dimension;
import java.sql.SQLException;
import javax.swing.*;
public class Runner {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Client frame = new Client("Recipe Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1920, 1080));
        frame.setVisible(true);
        frame.setResizable(false);
    }
}