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
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;
import com.temenos.t24.api.records.company.CompanyRecord;

/**
 * TODO: Document me!
 *
 * @author manethm
 *
 */
public class CompanyFilter extends Enquiry {
    
    private DataAccess da = new DataAccess((T24Context)this);
    
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

    public FilterCriteria companyFilter(FilterCriteria filterCriteria, String companyId) throws Exception{  
            FilterCriteria returnValue = new FilterCriteria();
            CompanyRecord company= new CompanyRecord(this.da.getRecord("COMPANY", companyId));
            if(company.getCompanyName().toString().toLowerCase().contains("head office")){
                returnValue.setFieldname(filterCriteria.getFieldname());
                returnValue.setOperand(filterCriteria.getOperand());
                returnValue.setValue(filterCriteria.getValue());
                return returnValue;
            }
            if(company.getCompanyName().toString().toLowerCase().contains("region")){
                if(companyId.substring(0, 6).equals(filterCriteria.getValue().substring(0, 6))){
                    returnValue.setFieldname(filterCriteria.getFieldname());
                    returnValue.setOperand(filterCriteria.getOperand());
                    returnValue.setValue(filterCriteria.getValue());
                    return returnValue; 
                }
                else{
                    throw new Exception("Branch not in Region");
                }
            }
            if(!companyId.equals(filterCriteria.getValue())){
                throw new Exception("User does not have access to branch");
            }
            returnValue.setFieldname(filterCriteria.getFieldname());
            returnValue.setOperand(filterCriteria.getOperand());
            returnValue.setValue(filterCriteria.getValue());
            return returnValue;
        }


}
