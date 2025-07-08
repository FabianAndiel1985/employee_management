package org.fabianandiel;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.fabianandiel.gui.LoginView;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("wifi-persistence-unit");
            emf.close(); // enough to trigger schema creation
        } catch (Exception e) {
            e.printStackTrace();
        }

        LoginView.launch(LoginView.class, args);
    }
}