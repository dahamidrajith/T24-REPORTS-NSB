package com.nsb.nable.reports.v9;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author manethm
 *
 */
public class StatementEntryBuildRoutine extends Enquiry {

    private DataAccess da = new DataAccess((T24Context)this);

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
        FilterCriteria returnVal = new FilterCriteria();
        boolean existAcct = false;
        try {
            for (FilterCriteria criteria:filterCriteria){
                log(criteria.toString());
                if(criteria.getFieldname().equals("ACCT.ID")){
                    existAcct = true;
                    returnVal.setFieldname(criteria.getFieldname());
                    returnVal.setOperand("EQ");
                    returnVal.setValue("LKR1513500010001");
                    filter.add(returnVal);
                }
                else{
                    filter.add(criteria);
                }
                if(criteria.getFieldname().equals("START.DATE")){
                    if(checkDate(criteria.getValue().toString())){
                        throw new T24CoreException("Date Range is Invalid");
                    }
                }
            }
            if(!existAcct){
                returnVal.setFieldname("ACCT.ID");
                returnVal.setOperand("EQ");
                returnVal.setValue("LKR1513500010001");
                filter.add(returnVal);
            }
        }catch(Exception e){
            log(e.toString());
        }
        return filter;
    }
    
    
    private boolean checkDate(String date){
        if(date.isEmpty()){
            return true;
        }
        return false;
    }
}
