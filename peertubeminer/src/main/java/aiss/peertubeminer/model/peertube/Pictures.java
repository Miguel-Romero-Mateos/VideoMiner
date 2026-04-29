package aiss.peertubeminer.model.peertube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pictures {

    @JsonProperty("path")
    private String path;

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}