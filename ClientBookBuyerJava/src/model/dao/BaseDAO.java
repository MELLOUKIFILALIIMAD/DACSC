package model.dao;

import model.network.ClientTCP;

public abstract class BaseDAO {

    protected final ClientTCP client;

    protected BaseDAO(ClientTCP client) {
        this.client = client;
    }
}