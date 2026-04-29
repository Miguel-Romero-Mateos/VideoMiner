package aiss.peertubeminer.service;

import aiss.peertubeminer.model.peertube.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChannelService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${peertubeminer.baseuri}")
    private String baseUri;

    public Channel getChannel(String channelId) {
        String url = baseUri + "/video-channels/" + channelId;
        return restTemplate.getForObject(url, Channel.class);
    }
}