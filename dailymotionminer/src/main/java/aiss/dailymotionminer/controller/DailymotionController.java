package aiss.dailymotionminer.controller;

import aiss.dailymotionminer.etl.Transformer;
import aiss.dailymotionminer.model.dailymotion.Subtitle;
import aiss.dailymotionminer.model.dailymotion.SubtitleContainer;
import aiss.dailymotionminer.model.dailymotion.Tag;
import aiss.dailymotionminer.model.dailymotion.VideoContainer;
import aiss.dailymotionminer.model.videominer.Caption;
import aiss.dailymotionminer.model.videominer.Channel;
import aiss.dailymotionminer.model.videominer.Comment;
import aiss.dailymotionminer.model.videominer.Video;
import aiss.dailymotionminer.service.DailymotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dailymotion/channels")
public class DailymotionController {

    @Autowired
    private DailymotionService dailymotionService;
    @Autowired
    private RestTemplate restTemplate;

    @Value("https://localhost:8080/videominer/channels")
    private String videoMinerURI;
    @Value("10")
    private Integer defaultMaxVideos;
    @Value("2")
    private Integer defaultMaxPages;

    @GetMapping("/{id}")
    public Channel getChannel(
            @PathVariable String id,
            @RequestParam(required = false) Integer maxVideos,
            @RequestParam(required = false) Integer maxPages) {

        int nVideos   = (maxVideos   != null) ? maxVideos   : defaultMaxVideos;
        int nComments = (maxPages != null) ? maxPages : defaultMaxPages;

        return buildChannel(id, nVideos, nComments);
    }


    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Channel createChannel(
            @PathVariable String id,
            @RequestParam(required=false) Integer maxVideos,
            @RequestParam(required = false) Integer maxPages) {
        int nVideos = (maxVideos != null) ? maxVideos : defaultMaxVideos;
        int nPages = (maxPages != null) ? maxPages : defaultMaxPages;

        Channel vChannel = buildChannel(id,nVideos,nPages);
        restTemplate.postForObject(videoMinerURI, vChannel, Channel.class);
        return vChannel;
    }

    private Channel buildChannel(String id, int nVideos, int nPages) {
        aiss.dailymotionminer.model.dailymotion.Channel dChannel = dailymotionService.getChannel(id);
        Channel vChannel = Transformer.transformChannel(dChannel);

        VideoContainer videoContainer = dailymotionService.getListVideos(id, nVideos);
        List<aiss.dailymotionminer.model.dailymotion.Video> dVideos = videoContainer.getList();

        List<Video> vVideos = new ArrayList<>();

        for (aiss.dailymotionminer.model.dailymotion.Video dVideo : dVideos) {
            Video vVideo = Transformer.transformVideo(dVideo);
            aiss.dailymotionminer.model.dailymotion.User dUser = dailymotionService.getUser(id);
            vVideo.setAuthor(Transformer.transformUser(dUser));

            Tag dTags = dailymotionService.getTags(dVideo.getId());
            List<Comment> vComments = new ArrayList<>();
            for(String tag: dTags.getTags()) {
                vComments.add(Transformer.transformComment(tag));
            }
            vVideo.setComments(vComments);

            SubtitleContainer dSubtitles = dailymotionService.getSubtitles(dVideo.getId());
            List<Caption> vCaptions = new ArrayList<>();
            if(dSubtitles != null && dSubtitles.getList() != null) {
                for(Subtitle sub: dSubtitles.getList()) {
                    vCaptions.add(Transformer.transformCaption(sub));
                }
            }
            vVideo.setCaptions(vCaptions);

            vVideos.add(vVideo);
        }
        vChannel.setVideos(vVideos);
        return vChannel;
    }

}
