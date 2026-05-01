package aiss.dailymotionminer.service;

import aiss.dailymotionminer.model.dailymotion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DailymotionService {

    @Autowired
    RestTemplate restTemplate;

    public Channel getChannel(String userId) {
        Channel channel = null;
        String uri = "https://api.dailymotion.com/user/" + userId + "?fields=id,nickname,description,created_time";
        try {
            channel = restTemplate.getForObject(uri, Channel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    public VideoContainer getListVideos(String userId, Integer maxVideos) {
        VideoContainer videoContainer = null;
        if (maxVideos == null) {
            maxVideos = 10;
        }
        String uri = "https://api.dailymotion.com/user/" + userId
                + "/videos?fields=id,title,description,uploaded_time&limit=" + maxVideos;
        try {
            videoContainer = restTemplate.getForObject(uri, VideoContainer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoContainer;
    }

    public User getUser(String userId) {
        User user = null;
        String uri = "https://api.dailymotion.com/user/" + userId + "?fields=id,nickname,url,avatar_60_url";
        try {
            user = restTemplate.getForObject(uri, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public Tag getTags(String videoId) {
        Tag tags = null;
        String uri = "https://api.dailymotion.com/video/" + videoId + "?fields=tags";
        try {
            tags = restTemplate.getForObject(uri, Tag.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tags;
    }

    public SubtitleContainer getSubtitles(String videoId) {
        SubtitleContainer subtitles = null;
        String uri = "https://api.dailymotion.com/video/" + videoId + "/subtitles?fields=id,language_label,url";
        try {
            subtitles = restTemplate.getForObject(uri, SubtitleContainer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subtitles;
    }
}
