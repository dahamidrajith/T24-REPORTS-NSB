package com.nsb.nable.reports.v9;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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
public class MaintenanceChargesBuildRoutine extends Enquiry {
    
    private List<FilterCriteria> filter = new ArrayList<FilterCriteria>();
    
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
            e.getStackTrace();
        } 
    }


    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        log("Start Maintenance Charges");
        try {
            for (FilterCriteria criteria:filterCriteria){
                log(criteria.toString());
                if(criteria.getFieldname().toUpperCase().equals("PROPERTY")){
                    String value = criteria.getValue().trim().toLowerCase().replace(" ", "");
                    FilterCriteria newCriteria = new FilterCriteria();
                    newCriteria.setFieldname("PROPERTY");
                    newCriteria.setOperand("EQ");
                    log(value);
                    switch(value){
                    case "cribchg":
                        newCriteria.setValue("CRIBCHG");
                        break;
                    case "valuationchg":
                        newCriteria.setValue("VALUATIONCHG");
                        break;
                    case "inspectionchg":
                        newCriteria.setValue("INSPECTIONCHG");
                        break;
                    case "courtchg":
                        newCriteria.setValue("COURTCHG");
                        break;
                    case "dtainsurancechg":
                        newCriteria.setValue("DTAINSCHG");
                        break;
                    case "fireinsurancechg":
                        newCriteria.setValue("FIREINSCHG");
                        break;
                    case "hlvestedchg":
                        newCriteria.setValue("HLVESTEDCHG");
                        break;
                    case "plvestedchg":
                        newCriteria.setValue("PLVESTEDCHG");
                        break;
                    case "recoverychg":
                        newCriteria.setValue("RECOVERYCHG");
                        break;
                    case "servicechg":
                        newCriteria.setValue("SERVICECHG");
                        break;
                    case "titleinsurancechg":
                        newCriteria.setValue("TITLEINSCHG");
                        break;
                    case "vlvestedchg":
                        newCriteria.setValue("VLVESTEDCHG");
                    default:
                        break;
                    }
                    log("doneMaintenceTask");
                }
                else{
                    filter.add(criteria);
                }
            }
        }catch(Exception e){
            log(e.toString());
        }
        return filterCriteria;
    }

}
