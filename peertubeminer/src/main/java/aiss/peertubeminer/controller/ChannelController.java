package aiss.peertubeminer.controller;

import aiss.peertubeminer.etl.Transformer;
import aiss.peertubeminer.model.peertube.Caption;
import aiss.peertubeminer.model.peertube.Comment;
import aiss.peertubeminer.model.peertube.Video;
import aiss.peertubeminer.model.videominer.Channel;
import aiss.peertubeminer.service.PeertubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/peertubeminer/channels")
public class ChannelController {

    @Autowired
    private PeertubeService peertubeService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${videominer.uri}")
    private String videoMinerUri;

    @Value("${peertubeminer.maxVideos}")
    private int defaultMaxVideos;

    @Value("${peertubeminer.maxComments}")
    private int defaultMaxComments;

    // GET
    @GetMapping("/{id}")
    public Channel getChannel(
            @PathVariable String id,
            @RequestParam(required = false) Integer maxVideos,
            @RequestParam(required = false) Integer maxComments) {

        int nVideos   = (maxVideos   != null) ? maxVideos   : defaultMaxVideos;
        int nComments = (maxComments != null) ? maxComments : defaultMaxComments;

        return buildVmChannel(id, nVideos, nComments);
    }

    // POST
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Channel sendChannel(
            @PathVariable String id,
            @RequestParam(required = false) Integer maxVideos,
            @RequestParam(required = false) Integer maxComments) {

        int nVideos   = (maxVideos   != null) ? maxVideos   : defaultMaxVideos;
        int nComments = (maxComments != null) ? maxComments : defaultMaxComments;

        Channel vmChannel = buildVmChannel(id, nVideos, nComments);

        restTemplate.postForObject(videoMinerUri, vmChannel, Channel.class);

        return vmChannel;
    }

    private Channel buildVmChannel(String channelId, int maxVideos, int maxComments) {

        aiss.peertubeminer.model.peertube.Channel ptChannel =
                peertubeService.getChannel(channelId);
        Channel vmChannel = Transformer.transformChannel(ptChannel);

        List<Video> ptVideos = peertubeService.getVideosByChannel(channelId, maxVideos);

        for (Video ptVideo : ptVideos) {

            aiss.peertubeminer.model.videominer.Video vmVideo =
                    Transformer.transformVideo(ptVideo);

            String uuid = ptVideo.getUuid();

            List<Caption> ptCaptions = peertubeService.getCaptionsByVideo(uuid);
            vmVideo.setCaptions(Transformer.transformCaptions(ptCaptions, uuid));


            List<Comment> ptComments = peertubeService.getCommentsByVideo(uuid, maxComments);
            vmVideo.setComments(Transformer.transformComments(ptComments));

            vmChannel.getVideos().add(vmVideo);
        }

        return vmChannel;
    }
}