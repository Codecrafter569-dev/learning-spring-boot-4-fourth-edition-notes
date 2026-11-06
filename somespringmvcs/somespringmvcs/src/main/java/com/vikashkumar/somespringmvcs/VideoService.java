package com.vikashkumar.somespringmvcs;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
@Service
public class VideoService{
  private List<Video> videos = List.of(
    new Video("Need to do something ok ."),
    new Video("Don't do this to your owned Code"),
    new Video("Hello how to solve leetCode problem"),
    new Video("You have a any idiaa ")
    
    );
    
    public List<Video> getVideos(){
      return videos;
    }
    
    public Video create(Video newVideo){
      List<Video> exten = new ArrayList<>(videos);
      exten.add(newVideo);
      this.videos = List.copyOf(exten);
      return newVideo;
    }
}