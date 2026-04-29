package aiss.peertubeminer.service;

import aiss.peertubeminer.model.peertube.Caption;
import aiss.peertubeminer.model.peertube.CaptionSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class CaptionService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${peertubeminer.baseuri}")
    private String baseUri;

    public List<Caption> getCaptionsByVideo(String videoUuid) {
        String url = baseUri + "/videos/" + videoUuid + "/captions";
        CaptionSearch result = restTemplate.getForObject(url, CaptionSearch.class);
        if (result == null || result.getData() == null) {
            return Collections.emptyList();
        }
        return result.getData();
    }
}