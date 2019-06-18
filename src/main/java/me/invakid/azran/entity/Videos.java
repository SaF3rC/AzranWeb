package me.invakid.azran.entity;

import java.util.ArrayList;
import java.util.List;

public class Videos {

    public static List<Videos> videos = new ArrayList<>();

    static {
        new Videos(0, "Azran vs Indigo", "https://www.youtube.com/watch?v=zYdpfjzRXFE", "https://i.imgur.com/fS5nOkZ.png");
        new Videos(1, "Azran vs Demon Client", "demon.mp4", "https://i.imgur.com/u6cw3vl.png");
        new Videos(2, "Azran vs Vape Lite", "https://www.youtube.com/watch?v=Ur77JwX3UNE", "https://i.imgur.com/qHkuZdI.png");
    }

    private int id;
    private String videoName;
    private String fileName;
    private String icon;


    public Videos(int id, String videoName, String fileName, String icon){
        this.id = id;
        this.videoName = videoName;
        this.fileName = fileName;
        this.icon = icon;
        videos.add(this);
    }

    public String getFileName() {
        return fileName;
    }

    public String getVideoName() {
        return videoName;
    }

    public int getId() {
        return id;
    }

    public String getIcon() {
        return this.icon;
    }

}
