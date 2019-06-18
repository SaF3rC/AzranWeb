package me.invakid.azran.service;

import me.invakid.azran.entity.Videos;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    public List<Videos> getVideos() {
        return Videos.videos;
    }

    public Videos getVideo(int id) {
        return Videos.videos.get(id);
    }

}
