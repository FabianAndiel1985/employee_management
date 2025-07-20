package org.fabianandiel.controller;

import org.fabianandiel.dao.AddressDAO;
import org.fabianandiel.interfaces.DAOInterface;

public class AddressController<T, ID> extends BaseController<T, ID>  {

    private AddressDAO<T, ID> addressDAO;

    public AddressController(DAOInterface<T, ID> dao) {
        super(dao);
        if (dao instanceof AddressDAO) {
            this.addressDAO = (AddressDAO<T, ID>) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

}
