package com.ap.menabev.dto;

import lombok.ToString;

@ToString
public class __metadata
{
    private String id;

    private String uri;

    private String type;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setUri(String uri){
        this.uri = uri;
    }
    public String getUri(){
        return this.uri;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
}
