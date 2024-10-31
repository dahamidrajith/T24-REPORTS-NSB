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
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author manethm
 *
 */
public class AaAccountBuildRoutine3 extends Enquiry{

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
        log("Start AAtoAccount Charges");
        String LoanId = "";
        Session session = new Session((T24Context)this);
        try{
        log(session.getCompanyId().toString());
        CompanyRecord company= new CompanyRecord(this.da.getRecord("COMPANY", session.getCompanyId().toString()));
        FilterCriteria returnVal = new FilterCriteria();
        try {
            for (FilterCriteria criteria:filterCriteria){
                log(criteria.toString());
                if(criteria.getFieldname().toUpperCase().equals("BRANCH") || criteria.getFieldname().toUpperCase().equals("COMPANY")){
                    if(criteria.getValue().equals("")){
                        if(company.getCompanyName(0).toString().toLowerCase().contains("region")){
                            returnVal.setFieldname(criteria.getFieldname());
                            returnVal.setOperand("SW");
                            returnVal.setValue(session.getCompanyId().toString().substring(0,6));
                        }
                        else if(company.getCompanyName(0).toString().toLowerCase().contains("head office")){
                            returnVal.setFieldname(criteria.getFieldname());
                            returnVal.setOperand(criteria.getOperand());
                            returnVal.setValue("");
                        }
                        else{
                            returnVal.setFieldname(criteria.getFieldname());
                            returnVal.setOperand("EQ");
                            returnVal.setValue(session.getCompanyId().toString());
                        }       
                    }
                
                    else{
                    filter.add(companyFilter(criteria, returnVal, company, session.getCompanyId().toString()));
                    }
                }
                if(criteria.getFieldname().toUpperCase().equals("ARRANGEMENT.ID") || criteria.getFieldname().toUpperCase().equals("ID.COMP.1")){
                    log("Staratras");
                    if (criteria.getValue().equals("") || criteria.getValue().contains("AA")){
                        return filterCriteria;
                    }
                    String value = criteria.getValue();
                    List<String> arrangementIds = this.da.selectRecords("", "AA.ARRANGEMENT", "","WITH LINKED.APPL.ID EQ " + value);
                    for(String id:arrangementIds){
                        LoanId = id;
                    }
                    log("Arrangement Id" + LoanId);
                    FilterCriteria newCriteria = new FilterCriteria();
                    newCriteria.setFieldname(criteria.getFieldname());
                    newCriteria.setOperand(criteria.getOperand());
                    newCriteria.setValue(LoanId);
                    filter.add(newCriteria);
                    log("doneAccountTask");
                }else{
                    filter.add(criteria);
                }
                return filter;
            }
        }catch(Exception e){
            log(e.toString());
        }
        }catch(Exception e){
            log(e.toString());
        }
        return filterCriteria;
    }

    
    
    public FilterCriteria companyFilter(FilterCriteria filterCriteria, FilterCriteria returnValue, CompanyRecord company, String companyId) throws Exception{  
        if(companyId.toLowerCase().contains("head office")){
            returnValue.setFieldname(filterCriteria.getFieldname());
            returnValue.setOperand(filterCriteria.getOperand());
            returnValue.setValue(filterCriteria.getValue());
            return returnValue;
        }
        if(companyId.toLowerCase().contains("region")){
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
