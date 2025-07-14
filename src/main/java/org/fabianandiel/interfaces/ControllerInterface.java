package org.fabianandiel.interfaces;

public interface ControllerInterface<T> {

    /**
     * Speichert ein neues Entity-Objekt in der Datenbank.
     *
     * @param entity Das zu speichernde Entity-Objekt
     * @return Das gespeicherte Entity-Objekt mit generierter ID
     */
    T create(T entity);



    /**
     * Aktualisiert ein bestehendes Entity-Objekt in der Datenbank.
     *
     * @param entity Das zu aktualisierende Entity-Objekt
     * @return Das aktualisierte Entity-Objekt
     */
    T update (T entity);


}
