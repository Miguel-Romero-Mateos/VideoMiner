package aiss.peertubeminer.model.peertube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Paging {

    @JsonProperty("total")
    private Integer total;

    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
}
