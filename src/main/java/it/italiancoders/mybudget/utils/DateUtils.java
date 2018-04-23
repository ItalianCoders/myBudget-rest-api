package it.italiancoders.mybudget.utils;

import java.util.Date;

public  class DateUtils {

    public static Long getUnixTime(Date date){
        if(date == null){
            return null;
        }
        return date.getTime()/1000;
    }

    public static Date getDate(Long unixTime){
        if(unixTime == null){
            return null;
        }
        return new java.util.Date((unixTime*1000));
    }



}
