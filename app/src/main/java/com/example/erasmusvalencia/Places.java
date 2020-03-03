package com.example.erasmusvalencia;

public class Places {
    private int id;
    private String name;
    private String location;
    private String url;
    private int imagesrc;

    public Places() {
        id = -1;
        name = "no name";
        location = "nowhere street";
        url = "ecosia.org";
        imagesrc = 0;
    }

    public Places(int id, String name, String location, String url, int imagesrc){
        this.id = id;
        this.name = name;
        this.location = location;
        this.url = url;
        this.imagesrc = imagesrc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getImagesrc() {
        return imagesrc;
    }

    public void setImagesrc(int imagesrc) {
        this.imagesrc = imagesrc;
    }
}
