package aiss.peertubeminer.model.peertube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentSearch extends Paging {

    @JsonProperty("data")
    private List<Comment> data;

    public List<Comment> getData() { return data; }
    public void setData(List<Comment> data) { this.data = data; }
}