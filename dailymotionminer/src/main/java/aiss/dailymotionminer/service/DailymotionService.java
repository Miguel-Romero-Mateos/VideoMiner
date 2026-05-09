package aiss.dailymotionminer.service;

import aiss.dailymotionminer.model.dailymotion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class DailymotionService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${dailymotionminer.baseuri}")
    private String baseUri;

    public Channel getChannel(String userId) {
        String uri = baseUri + "/user/" + userId
                + "?fields=id,nickname,description,created_time";

        try {
            return restTemplate.getForObject(uri, Channel.class);

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Dailymotion channel not found: " + userId,
                    e
            );

        } catch (HttpClientErrorException.BadRequest e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Dailymotion channel not found or invalid: " + userId,
                    e
            );

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Error retrieving Dailymotion channel: " + userId,
                    e
            );
        }
    }

    public List<Video> getListVideos(String userId, Integer maxVideos, Integer maxPages) {

        if (maxVideos == null || maxVideos <= 0) {
            maxVideos = 10;
        }

        if (maxPages == null || maxPages <= 0) {
            maxPages = 2;
        }

        List<Video> allVideos = new ArrayList<>();

        for (int page = 1; page <= maxPages; page++) {

            String uri = baseUri + "/user/" + userId + "/videos"
                    + "?fields=id,title,description,uploaded_time"
                    + "&limit=" + maxVideos
                    + "&page=" + page;

            try {
                VideoContainer videoContainer = restTemplate.getForObject(uri, VideoContainer.class);

                if (videoContainer == null
                        || videoContainer.getList() == null
                        || videoContainer.getList().isEmpty()) {
                    break;
                }

                allVideos.addAll(videoContainer.getList());

                if (videoContainer.getHasMore() != null && !videoContainer.getHasMore()) {
                    break;
                }

            } catch (HttpClientErrorException.NotFound e) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dailymotion videos not found for channel: " + userId,
                        e
                );

            } catch (HttpClientErrorException.BadRequest e) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dailymotion channel not found or invalid: " + userId,
                        e
                );

            } catch (Exception e) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Error retrieving Dailymotion videos for channel: " + userId,
                        e
                );
            }
        }

        return allVideos;
    }

    public User getUser(String userId) {
        String uri = baseUri + "/user/" + userId
                + "?fields=id,nickname,url,avatar_60_url";

        try {
            return restTemplate.getForObject(uri, User.class);

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Dailymotion user not found: " + userId,
                    e
            );

        } catch (HttpClientErrorException.BadRequest e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Dailymotion user not found or invalid: " + userId,
                    e
            );

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Error retrieving Dailymotion user: " + userId,
                    e
            );
        }
    }

    public Tag getTags(String videoId) {
        String uri = baseUri + "/video/" + videoId + "?fields=tags";

        try {
            return restTemplate.getForObject(uri, Tag.class);

        } catch (HttpClientErrorException.NotFound e) {
            return null;

        } catch (HttpClientErrorException.BadRequest e) {
            return null;

        } catch (Exception e) {
            return null;
        }
    }

    public SubtitleContainer getSubtitles(String videoId) {
        String uri = baseUri + "/video/" + videoId
                + "/subtitles?fields=id,language_label,url";

        try {
            return restTemplate.getForObject(uri, SubtitleContainer.class);

        } catch (HttpClientErrorException.NotFound e) {
            return null;

        } catch (HttpClientErrorException.BadRequest e) {
            return null;

        } catch (Exception e) {
            return null;
        }
    }
}