package org.fabianandiel.controller;

import org.fabianandiel.interfaces.ControllerInterface;
import org.fabianandiel.interfaces.DAOInterface;

import java.util.UUID;

public class BaseController<T,ID> implements ControllerInterface {

    private DAOInterface<T, ID> dao;

    public BaseController(DAOInterface<T, ID> dao) {
        this.dao = dao;
    }

}
