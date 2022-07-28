package com.urbantech.item;

public class ItemServices {
    private String sid, service_name, service_image,imageThumb;

    public String getSid() {
        return sid;
    }

    public String getService_name() {
        return service_name;
    }

    public String getService_image() {
        return service_image;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public void setService_image(String service_image) {
        this.service_image = service_image;
    }

    public ItemServices(String sid, String service_name, String service_image, String imageThumb) {
        this.sid = sid;
        this.service_name = service_name;
        this.service_image = service_image;
        this.imageThumb = imageThumb;
    }

    public String getImageThumb() {
        return imageThumb;
    }
}
