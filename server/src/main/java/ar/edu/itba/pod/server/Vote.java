package ar.edu.itba.pod.server;

import java.util.List;

public class Vote {

    private Integer table;
    private Province province;
    private List<Party> choices;

    public Vote(Integer table, Province province, List choices) {
        this.table = table;
        this.province = province;
        this.choices = choices;
    }
}
