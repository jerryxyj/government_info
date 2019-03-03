package com.jerry.jerry.knowyourgovernment;

import java.io.Serializable;

/**
 * Created by jerry on 8/1/2017.
 */

public class Official implements Serializable {
    private String title;
    private String name;
    private String address;
    private String party;
    private String number;
    private String website;
    private String email;
    private String photoUrl;
    private String channelGid;
    private String channelFid;
    private String channelTid;
    private String channelYid;
    private String location;


    Official(String title,String name, String address, String party,String number,String website,String email,String photoUrl,String channelGid,String channelFid,String channelTid,String channelYid,String location){
        this.title=title;
        this.name=name;
        this.address=address;
        this.party=party;
        this.number=number;
        this.website=website;
        this.email=email;
        this.photoUrl=photoUrl;
        this.channelGid=channelGid;
        this.channelFid=channelFid;
        this.channelTid=channelTid;
        this.channelYid=channelYid;
        this.location=location;

    }
    String getTitle(){return title;}
    public void setTitle(String title){this.title=title;}
    String getName(){return name;}
    public void setName(String name){this.name=name;}
    String getAddress(){return address;}
    public  void setAddress(String address){this.address=address;}
    String getParty(){return party;}
    public void setParty(String party){this.party=party;}
    String getNumber(){return number;}
    public void setNumber(String number){this.number=number;}
    String getWebsite(){return  website;}
    public void setWebsite(String website){this.website=website;}
    String getEmail(){return email;}
    public void setEmail(String email){this.email=email;}
    String getPhotoUrl(){return photoUrl;}
    public void setPhotoUrl(String photoUrl){this.photoUrl=photoUrl;}
    String getChannelGid(){return channelGid;}
    public  void setChannelGid(String channelGid){this.channelGid=channelGid;}
    String getChannelFid(){return channelFid;}
    public  void setChannelFid(String channelFid){this.channelFid=channelFid;}
    String getChannelTid(){return channelTid;}
    public  void setChannelTid(String channelTid){this.channelTid=channelTid;}
    String getChannelYid(){return channelYid;}
    public  void setChannelYid(String channelYid){this.channelYid=channelYid;}
    String getLocation(){return location;}
    public  void setLocation(String location){this.location=location;}


    public String toString(){
        return title+name+party;
    }

}
