package me.invakid.azran.service;

import me.invakid.azran.dao.AzranDAO;
import me.invakid.azran.entity.Client;
import me.invakid.azran.entity.Detections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AzranService {

    @Autowired
    AzranDAO dao;

    public boolean isTokenValid(String token) {
        return dao.isTokenValid(token);
    }

    public void removeToken(String token) {
        dao.removeToken(token);
    }

    public boolean isPwValid(String password) {
        return dao.isPwValid(password);
    }

    public String getUsername(String password) {
        return dao.getUsername(password);
    }

    public void removePw(String password) {
        dao.removePw(password);
    }

    public ArrayList<Detections> getDetections(int i) {
        return dao.getDetections(i);
    }

    public ArrayList<Client> getClients(int i) {
        return dao.getClients(i);
    }

    public ArrayList<Client> getExpireableClients(int i) {
        return dao.getExpireableClients(i);
    }

    public void removeClient(String s) {
        dao.removeClient(s);
    }

    public void addClient(String userId) {
        dao.addClient(userId);
    }

    public void addClientsFor30Days(String userId){
        dao.addClientsFor30Days(userId);
    }

    public void addPayment(String ticketId, String ticketName, String price) {
        dao.addPayment(ticketId, ticketName, price);
    }

    public void updatePayment(String ticketId, String email, String price) {
        dao.updatePayment(ticketId, email, price);
    }
}
