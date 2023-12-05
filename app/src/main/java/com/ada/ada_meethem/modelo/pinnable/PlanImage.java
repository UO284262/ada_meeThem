package com.ada.ada_meethem.modelo.pinnable;

public class PlanImage implements Pinnable {
    private String url;

    public PlanImage(String url) {
        this.url = url;
    }

    public void setUrl(String url) {this.url = url;}

    public String getUrl() {return this.url;}
}
