package com.nsb.nable.reports.v9;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;

/**
 * TODO: Document me!
 *
 * @author manethm
 *
 */
public class DateChangeArrActivityComp extends Enquiry {
    
    
    public static void log(String line) {
    String filePath = "/nsbt24/debug/logNBIM.txt";
    line = String.valueOf(String.valueOf(line)) + "\n";
    try {
        File myObj = new File(filePath);
        if (myObj.createNewFile()) {
            FileWriter fw = new FileWriter(filePath);
            fw.write("---" + line);
            fw.close();
        } else {
            Files.write(Paths.get(filePath, new String[0]), line.getBytes(), new OpenOption[] { StandardOpenOption.APPEND });
        } 
    } catch (Exception e) {
        
    }
    } 

    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        log("Start Maintenance Charges");
        try {
            for (FilterCriteria criteria:filterCriteria){
                log(criteria.toString());
                log(criteria.getFieldname().toUpperCase());
                if(criteria.getFieldname().toUpperCase().equals("DATE")){
                    String value = criteria.getValue().trim().toLowerCase().replace(" ", "");
                    log(value);
                    switch(value){
                    case "crib":
                        criteria.setValue("CRIBCHG");
                        break;
                    case "valuation":
                        criteria.setValue("VALUATIONCHG");
                        break;
                    case "inspection":
                        criteria.setValue("INSPECTIONCHG");
                        break;
                    case "court":
                        criteria.setValue("COURTCHG");
                        break;
                    case "dtainsurance":
                        criteria.setValue("DTAINSCHG");
                        break;
                    case "fireinsurance":
                        criteria.setValue("FIREINSCHG");
                        break;
                    case "hlvested":
                        criteria.setValue("HLVESTEDCHG");
                        break;
                    case "plvested":
                        criteria.setValue("PLVESTEDCHG");
                        break;
                    case "recovery":
                        criteria.setValue("RECOVERYCHG");
                        break;
                    case "service":
                        criteria.setValue("SERVICECHG");
                        break;
                    case "titleinsurance":
                        criteria.setValue("TITLEINSCHG");
                        break;
                    case "vlvested":
                        criteria.setValue("VLVESTEDCHG");
                    default:
                        break;
                    }
                }
            }
        }catch(Exception e){
        }
        return filterCriteria;
    }
    
}
