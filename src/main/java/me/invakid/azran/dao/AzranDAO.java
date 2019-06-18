package me.invakid.azran.dao;

import me.invakid.azran.entity.Client;
import me.invakid.azran.entity.Detections;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Repository
public class AzranDAO {

    private ExecutorService es = Executors.newFixedThreadPool(2);
    private Connection connection;

    private void vruzka() {
        try {
            String host = "51.38.107.96";
            String database = "azran";
            String username = "azran";
            String password = "1c2o3m4p5l6ic7a8t9e0d";
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + 3306 + "/" + database+"?useSSL=false", username,
                    password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void spri() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isTokenValid(String token) {
        Future<Boolean> result = es.submit(() -> {
            boolean valid = false;
            vruzka();
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `tokens` WHERE `token`=?;");
            sql.setString(1, token);
            ResultSet rs = sql.executeQuery();
            if (rs.next()) valid = true;
            rs.close();
            sql.close();
            spri();
            return valid;
        });
        boolean bool = false;
        try {
            bool = result.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

    public void removeToken(String token) {
        new Thread(() -> {
            vruzka();
            try {
                PreparedStatement sql = connection.prepareStatement("DELETE FROM `tokens` WHERE `token`=?;");
                sql.setString(1, token);
                sql.execute();
                sql.close();
            } catch (Exception ignored) {
            }
            spri();
        }).start();
    }


    public boolean isPwValid(String password) {
        Future<Boolean> result = es.submit(() -> {
            boolean valid = false;
            vruzka();
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `pw` WHERE `pin`=?;");
            sql.setString(1, password);
            ResultSet rs = sql.executeQuery();
            if (rs.next()) valid = true;
            rs.close();
            sql.close();
            spri();
            return valid;
        });
        boolean bool = false;
        try {
            bool = result.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

    public String getUsername(String password) {
        String member = null;
        Future<String> result = es.submit(() -> {
            vruzka();
            String user = null;
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `pw` WHERE `pin`=?;");
            sql.setString(1, password);
            ResultSet rs = sql.executeQuery();
            if (rs.next()) user = rs.getString("member");
            rs.close();
            sql.close();
            spri();
            return user;
        });
        try {
            member = result.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return member;
    }

    public void removePw(String password) {
        new Thread(() -> {
            vruzka();
            try {
                PreparedStatement sql = connection.prepareStatement("DELETE FROM `pw` WHERE `pin`=?;");
                sql.setString(1, password);
                sql.execute();
                sql.close();
            } catch (Exception ignored) {
            }
            spri();
        }).start();
    }

    public ArrayList<Detections> getDetections(int i) {
        ArrayList<Detections> decs = new ArrayList<>();
        Future<ArrayList<Detections>> result = es.submit(() -> {
            vruzka();
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `detections` ORDER BY time DESC LIMIT " + i + ";");
            ArrayList<Detections> detections = new ArrayList<>();
            ResultSet rs = sql.executeQuery();
            while (rs.next()) {
                detections.add(new Detections(
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("time")),
                        rs.getString("ign"),
                        rs.getString("ip"),
                        rs.getString("name"),
                        rs.getString("line"),
                        rs.getString("string"),
                        rs.getString("pin"),
                        rs.getString("guild"),
                        rs.getString("channel"),
                        rs.getString("member"),
                        rs.getString("process")
                ));
            }
            rs.close();
            sql.close();
            spri();
            return detections;
        });

        try {
            decs = result.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decs;
    }

    public ArrayList<Client> getClients(int i) {
        ArrayList<Client> decs = new ArrayList<>();
        Future<ArrayList<Client>> result = es.submit(() -> {
            vruzka();
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `clients` WHERE expire IS NULL ORDER BY bought DESC LIMIT " + i + ";");
            ArrayList<Client> clients = new ArrayList<>();
            ResultSet rs = sql.executeQuery();
            while (rs.next()) {
                clients.add(new Client(
                        rs.getString("userId"),
                        rs.getString("userName"),
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("bought")), ""
                ));
            }
            rs.close();
            sql.close();
            spri();
            return clients;
        });

        try {
            decs = result.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decs;
    }

    public ArrayList<Client> getExpireableClients(int i) {
        ArrayList<Client> decs = new ArrayList<>();
        Future<ArrayList<Client>> result = es.submit(() -> {
            vruzka();
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `clients` WHERE expire IS NOT NULL ORDER BY expire ASC LIMIT " + i + ";");
            ArrayList<Client> clients = new ArrayList<>();
            ResultSet rs = sql.executeQuery();
            while (rs.next()) {
                clients.add(new Client(
                        rs.getString("userId"),
                        rs.getString("userName"),
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("bought")),
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp("expire"))
                ));
            }
            rs.close();
            sql.close();
            spri();
            return clients;
        });

        try {
            decs = result.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decs;
    }

    public void removeClient(String id) {
        new Thread(() -> {
            vruzka();
            try {
                PreparedStatement sql = connection.prepareStatement("DELETE FROM `clients` WHERE `userId`=?;");
                sql.setString(1, id);
                sql.execute();
                sql.close();
            } catch (Exception ignored) {
            }
            spri();
        }).start();
    }

    public void addClient(String id) {
        new Thread(() -> {
            vruzka();
            try {
                PreparedStatement sql = connection.prepareStatement("INSERT INTO `clients` (`userId`, `userName`) VALUES (?, ?);");
                sql.setString(1, id);
                sql.setString(2, "loading"+id);
                sql.execute();
                sql.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            spri();
        }).start();
    }

    public void addClientsFor30Days(String id) {
        new Thread(() -> {
            vruzka();
            try {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Calendar cal = Calendar.getInstance();
                cal.setTime(timestamp);
                cal.add(Calendar.DAY_OF_WEEK, 30);
                timestamp.setTime(cal.getTime().getTime());
                System.out.println(timestamp);
                PreparedStatement sql = connection.prepareStatement("INSERT INTO `clients` (`userId`, `userName`, `expire`) VALUES (?, ?, ?);");
                sql.setString(1, id);
                sql.setString(2, "loading-" + id);
                sql.setTimestamp(3, timestamp);
                sql.execute();
                sql.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            spri();
        }).start();
    }

    public void addPayment(String ticketId, String ticketName, String price) {
        new Thread(() -> {
            vruzka();
            try {
                PreparedStatement sql = connection.prepareStatement("INSERT INTO `azran`.`transactions` (`ticketId`, `ticketName`, `price`) VALUES (?, ?, ?);");
                sql.setString(1, ticketId);
                sql.setString(2, ticketName);
                sql.setString(3, price);
                sql.execute();
                sql.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            spri();
        }).start();
    }

    public void updatePayment(String ticketId, String email, String price) {
        new Thread(() -> {
            vruzka();
            try {
                PreparedStatement sql = connection.prepareStatement("UPDATE `azran`.`transactions` SET `email`=?, `complete`='1', price=? WHERE  `ticketId`=? LIMIT 1;");
                sql.setString(1, email);
                sql.setString(2, price);
                sql.setString(3, ticketId);
                sql.execute();
                sql.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            spri();
        }).start();
    }
}
