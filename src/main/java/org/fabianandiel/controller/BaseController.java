package org.fabianandiel.controller;

import org.fabianandiel.interfaces.ControllerInterface;
import org.fabianandiel.interfaces.DAOInterface;


public abstract class BaseController<T,ID> implements ControllerInterface<T,ID> {

    private DAOInterface<T, ID> dao;

    public BaseController(DAOInterface<T, ID> dao) {
        this.dao = dao;
    }


    @Override
    public T getById(ID id, Class<T> entityClass ) {
        try {
            return dao.findById(id,entityClass);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    @Override
    public T create(T entity) {
        try {
            return dao.save(entity);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public T update(T entity) {
        try {
            return dao.update(entity);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;

    }



}
