package com.urbantech.item;

import android.net.Uri;

public class ItemGallery {

    private String id, image;
    private Uri uri;

    public ItemGallery(String id, String image) {
        this.id = id;
        this.image = image;
    }

    public ItemGallery(String image) {
        this.image = image;
    }

    public ItemGallery(Uri uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public Uri getUri() {
        return uri;
    }
}