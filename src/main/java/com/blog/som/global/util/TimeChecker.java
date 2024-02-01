package com.blog.som.global.util;

public class TimeChecker {

  private Long startTime;
  private Long finishTime;

  public static TimeChecker start() {
    TimeChecker timeChecker = new TimeChecker();
    timeChecker.startTime = System.currentTimeMillis();
    return timeChecker;
  }
  
  public void finish(){
    this.finishTime = System.currentTimeMillis();
    System.out.println("startTime = " + startTime);
    System.out.println("finishTime = " + finishTime);
    System.out.println("duration time : " + (finishTime - startTime)+ "millisecond");
  }

}
