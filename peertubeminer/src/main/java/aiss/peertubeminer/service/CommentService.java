package aiss.peertubeminer.service;

import aiss.peertubeminer.model.peertube.Comment;
import aiss.peertubeminer.model.peertube.CommentSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${peertubeminer.baseuri}")
    private String baseUri;

    public List<Comment> getCommentsByVideo(String videoUuid, int maxComments) {
        String url = baseUri + "/videos/" + videoUuid + "/comment-threads?count=" + maxComments;
        CommentSearch result = restTemplate.getForObject(url, CommentSearch.class);
        if (result == null || result.getData() == null) {
            return Collections.emptyList();
        }
        return result.getData();
    }
}