package com.nsb.nable.reports.v9.update;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TDate;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.party.Account;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class StatementEntryBuildRoutineNewUp418 extends Enquiry {
    
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
            e.getStackTrace();
        } 
    }

    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
       
        TDate startTDate = null;
        TDate endTDate = null;
        
        Session T24session = new Session((T24Context) this);
        String myCoCode=T24session.getCompanyId().toString();
        List<FilterCriteria> additionalCriteria = new ArrayList<>();
        try{
            
            for (FilterCriteria criteria:filterCriteria){
                log(criteria.toString());
                log("MycoCode="+myCoCode);
                String onlyCocodeNumber = myCoCode.substring(2);

                if(criteria.getFieldname().equals("BOOKING.DATE")){
                    
                    String dateRange=criteria.getValue();
                    String[] parts = dateRange.split("\\s+");
                    String startDate=parts[0];
                    String endDate=parts[1];
                    startTDate = new TDate(startDate);
                    endTDate = new TDate(endDate);
                    
                    //need to split
                    
                }else if(criteria.getFieldname().equals("ACCOUNT.NUMBER")){
                    String value=criteria.getValue();
                    int branchValue=Integer.parseInt(onlyCocodeNumber); 
                    
                    CompanyRecord company = new CompanyRecord(this.da.getRecord("COMPANY", myCoCode));
                    String compType=company.getCompanyName().toString().toLowerCase();
                    
                    if (compType.contains("sit")) {
                        
                        for(int i=1;i<800;i++){
                            
                            log("value-"+value);
                            log("branchValue-"+branchValue);
                            String finalValue=value+"000"+branchValue+"";
                            
                            Account ac = new Account((T24Context)this);
                            ac.setAccountId(finalValue);
                            
                            List<String> ids = ac.getEntries("BOOK", " ", " ", " ", startTDate, endTDate);
                            log("ids="+ids);
                            for(String idz:ids){
                                FilterCriteria fc1 = new FilterCriteria(); 
                                fc1.setFieldname("@ID");
                                fc1.setOperand("EQ");
                                fc1.setValue(idz);
                                additionalCriteria.add(fc1);
                            }
                            branchValue++;
                        }
                        
                    }else if(compType.contains("region")) {
                       
                        for(int i=1;i<50;i++){
                            FilterCriteria fc1 = new FilterCriteria(); 
                            String finalValue=value+"000"+branchValue+"";
                            
                            Account ac = new Account((T24Context)this);
                            ac.setAccountId(finalValue);
                            
                            List<String> ids = ac.getEntries("BOOK", " ", " ", " ", startTDate, endTDate);
                            
                            for(String idz:ids){
                                fc1.setFieldname("@ID");
                                fc1.setOperand("EQ");
                                fc1.setValue(idz);
                                additionalCriteria.add(fc1);
                            }
                            
                            branchValue++;
                        }
                       
                        
                    }else{
                        
                        FilterCriteria fc1 = new FilterCriteria();
                        
                        String finalValue=value+"000"+branchValue+"";
                        fc1.setFieldname("ACCOUNT.NUMBER");
                        fc1.setOperand("CT");
                        fc1.setValue(finalValue);
                        additionalCriteria.add(fc1);
                        
                        
                    }
                    
                }
                
            }
            
        }catch(Exception e){
            log("Exception-"+e);
        }
        log("additionalCriteria-"+additionalCriteria);
        log("----------------");
        filterCriteria.addAll(additionalCriteria);
        log("filterCriteria-"+filterCriteria);
        log("filterCriteriaLength-"+filterCriteria.size());
        
        return filterCriteria;
    }
    
    

}
