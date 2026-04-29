package aiss.peertubeminer.service;

import aiss.peertubeminer.model.peertube.Video;
import aiss.peertubeminer.model.peertube.VideoSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class VideoService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${peertubeminer.baseuri}")
    private String baseUri;

    public List<Video> getVideosByChannel(String channelId, int maxVideos) {
        String url = baseUri + "/video-channels/" + channelId + "/videos?count=" + maxVideos;
        VideoSearch result = restTemplate.getForObject(url, VideoSearch.class);
        if (result == null || result.getData() == null) {
            return Collections.emptyList();
        }
        return result.getData();
    }
}