package com.nsb.nable.reports;
import java.text.ParseException;
import java.text.SimpleDateFormat; 
import java.util.Date;  
/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class RgetDateRangeUpdate {
    
    public int givenDates(String startDate,String endDate,String givenDate) throws ParseException{
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");  
        boolean status1=false;
        boolean status2=false;
        int rtnValue=0;
        
        Date dateStart = sdf.parse(startDate);  
        Date dateEnd = sdf.parse(endDate);
        Date dateGiven = sdf.parse(givenDate);
        
        if(dateStart.compareTo(dateGiven) < 0 ){
            //dateStart comes before dateGiven
        status1=true;
        }
        
        if(dateGiven.compareTo(dateEnd) < 0 ){
             //dateGiven comes before dateEnd
        status2=true;
        }
          
        if(status1==true && status2==true){
            rtnValue=1;
        }
        
        return rtnValue;
    }

}
