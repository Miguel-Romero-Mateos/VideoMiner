package aiss.peertubeminer.service;

import aiss.peertubeminer.model.peertube.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestTemplate;


import java.util.Collections;
import java.util.List;

@Service
public class PeertubeService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${peertubeminer.baseuri}")
    private String baseUri;

    public Channel getChannel(String channelId) {

        String url = baseUri + "/video-channels/" + channelId;

        try {
            return restTemplate.getForObject(url, Channel.class);

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "PeerTube channel not found: " + channelId,
                    e
            );

        } catch (HttpClientErrorException.BadRequest e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "PeerTube channel not found or invalid: " + channelId,
                    e
            );

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Error retrieving PeerTube channel: " + channelId,
                    e
            );
        }
    }

    public List<Video> getVideosByChannel(String channelId, int maxVideos) {

        String url = baseUri + "/video-channels/" + channelId + "/videos?count=" + maxVideos;

        try {
            VideoSearch result = restTemplate.getForObject(url, VideoSearch.class);

            if (result == null || result.getData() == null) {
                return Collections.emptyList();
            }

            return result.getData();

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "PeerTube videos not found for channel: " + channelId,
                    e
            );

        } catch (HttpClientErrorException.BadRequest e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "PeerTube channel not found or invalid: " + channelId,
                    e
            );

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Error retrieving PeerTube videos for channel: " + channelId,
                    e
            );
        }
    }

    public List<Caption> getCaptionsByVideo(String videoUuid) {

        String url = baseUri + "/videos/" + videoUuid + "/captions";

        try {
            CaptionSearch result = restTemplate.getForObject(url, CaptionSearch.class);

            if (result == null || result.getData() == null) {
                return Collections.emptyList();
            }

            return result.getData();

        } catch (HttpClientErrorException.NotFound e) {
            return Collections.emptyList();

        } catch (HttpClientErrorException.BadRequest e) {
            return Collections.emptyList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Comment> getCommentsByVideo(String videoUuid, int maxComments) {

        String url = baseUri + "/videos/" + videoUuid + "/comment-threads?count=" + maxComments;

        try {
            CommentSearch result = restTemplate.getForObject(url, CommentSearch.class);

            if (result == null || result.getData() == null) {
                return Collections.emptyList();
            }

            return result.getData();

        } catch (HttpClientErrorException.NotFound e) {
            return Collections.emptyList();

        } catch (HttpClientErrorException.BadRequest e) {
            return Collections.emptyList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}

