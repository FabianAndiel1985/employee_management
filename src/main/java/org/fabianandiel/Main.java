package org.fabianandiel;


import org.fabianandiel.gui.LoginView;
import org.fabianandiel.services.EntityManagerProvider;

public class Main {
    public static void main(String[] args) {
        EntityManagerProvider.launch();
        LoginView.launch(LoginView.class, args);
    }
}