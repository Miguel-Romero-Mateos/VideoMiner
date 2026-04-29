package aiss.peertubeminer.model.peertube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoSearch extends Paging {

    @JsonProperty("data")
    private List<Video> data;

    public List<Video> getData() { return data; }
    public void setData(List<Video> data) { this.data = data; }
}