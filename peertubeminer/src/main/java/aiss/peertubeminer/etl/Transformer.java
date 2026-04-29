package aiss.peertubeminer.etl;

import aiss.peertubeminer.model.peertube.Caption;
import aiss.peertubeminer.model.peertube.Comment;
import aiss.peertubeminer.model.peertube.Video;
import aiss.peertubeminer.model.peertube.Channel;

import java.util.ArrayList;
import java.util.List;

public class Transformer {

    private static final String PEERTUBE_BASE_URL = "https://peertube.tv";

    // Channel PeerTube → Channel VideoMiner
    public static aiss.peertubeminer.model.videominer.Channel transformChannel(Channel ptChannel) {

        aiss.peertubeminer.model.videominer.Channel vmChannel =
                new aiss.peertubeminer.model.videominer.Channel();

        vmChannel.setId(String.valueOf(ptChannel.getId()));
        vmChannel.setName(ptChannel.getDisplayName());
        vmChannel.setDescription(ptChannel.getDescription());
        vmChannel.setCreatedTime(ptChannel.getCreatedAt());

        return vmChannel;
    }

    // Video PeerTube → Video VideoMiner
    public static aiss.peertubeminer.model.videominer.Video transformVideo(Video ptVideo) {

        aiss.peertubeminer.model.videominer.Video vmVideo =
                new aiss.peertubeminer.model.videominer.Video();

        vmVideo.setId(String.valueOf(ptVideo.getId()));
        vmVideo.setName(ptVideo.getName());
        vmVideo.setDescription(ptVideo.getDescription());
        vmVideo.setReleaseTime(ptVideo.getPublishedAt());
        vmVideo.setComments(new ArrayList<>());
        vmVideo.setCaptions(new ArrayList<>());

        if (ptVideo.getAccount() != null) {
            vmVideo.setAuthor(transformUser(ptVideo.getAccount()));
        }

        return vmVideo;
    }

    // User (Account) PeerTube → User VideoMiner
    public static aiss.peertubeminer.model.videominer.User
    transformUser(aiss.peertubeminer.model.peertube.User ptUser) {

        aiss.peertubeminer.model.videominer.User vmUser =
                new aiss.peertubeminer.model.videominer.User();

        vmUser.setName(ptUser.getName());
        vmUser.setUser_link(ptUser.getUrl());

        if (ptUser.getAvatars() != null && !ptUser.getAvatars().isEmpty()) {
            vmUser.setPicture_link(PEERTUBE_BASE_URL + ptUser.getAvatars().get(0).getPath());
        }

        return vmUser;
    }

    // Caption PeerTube → Caption VideoMiner
    public static aiss.peertubeminer.model.videominer.Caption
    transformCaption(Caption ptCaption, String videoUuid) {

        aiss.peertubeminer.model.videominer.Caption vmCaption =
                new aiss.peertubeminer.model.videominer.Caption();

        String langId = ptCaption.getLanguage() != null
                ? ptCaption.getLanguage().getId() : "unknown";

        vmCaption.setId(videoUuid + "-" + langId);
        vmCaption.setName(PEERTUBE_BASE_URL + ptCaption.getCaptionPath());
        vmCaption.setLanguage(langId);

        return vmCaption;
    }

    // Comment PeerTube → Comment VideoMiner
    public static aiss.peertubeminer.model.videominer.Comment transformComment(Comment ptComment) {

        aiss.peertubeminer.model.videominer.Comment vmComment =
                new aiss.peertubeminer.model.videominer.Comment();

        vmComment.setId(String.valueOf(ptComment.getId()));
        vmComment.setText(ptComment.getText());
        vmComment.setCreatedOn(ptComment.getCreatedAt());

        return vmComment;
    }

    // Trasforma una lista de Caption
    public static List<aiss.peertubeminer.model.videominer.Caption>
    transformCaptions(List<Caption> ptCaptions, String videoUuid) {
        List<aiss.peertubeminer.model.videominer.Caption> result = new ArrayList<>();
        if (ptCaptions != null) {
            for (Caption c : ptCaptions) {
                result.add(transformCaption(c, videoUuid));
            }
        }
        return result;
    }

    // Trasforma una lista de Comment
    public static List<aiss.peertubeminer.model.videominer.Comment>
    transformComments(List<Comment> ptComments) {
        List<aiss.peertubeminer.model.videominer.Comment> result = new ArrayList<>();
        if (ptComments != null) {
            for (Comment c : ptComments) {
                result.add(transformComment(c));
            }
        }
        return result;
    }
}