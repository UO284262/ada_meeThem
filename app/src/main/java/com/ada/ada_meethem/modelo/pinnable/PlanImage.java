package com.ada.ada_meethem.modelo.pinnable;

import java.util.UUID;

public class PlanImage implements Pinnable {
    private String url;

    private String id;

    public PlanImage() {}

    public PlanImage(String url) {
        this.url = url;
        this.id = "pli" + UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {this.url = url;}

    public String getUrl() {return this.url;}
}
