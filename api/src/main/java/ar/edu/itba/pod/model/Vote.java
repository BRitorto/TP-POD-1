package ar.edu.itba.pod.model;

import java.io.Serializable;
import java.util.List;

public class Vote implements Serializable {

    //id
    private Long table;
    private Province province;
    private List<Party> choices;

    public Vote(Long table, Province province, List choices) {
        this.table = table;
        this.province = province;
        this.choices = choices;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public List<Party> getChoices() {
        return choices;
    }

    public void setChoices(List<Party> choices) {
        this.choices = choices;
    }

    public Long getTable() {
        return table;
    }

    public void setTable(Long table) {
        this.table = table;
    }
}
