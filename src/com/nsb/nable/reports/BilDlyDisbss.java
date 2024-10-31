package com.nsb.nable.reports;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import com.temenos.api.TField;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TDate;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class BilDlyDisbss extends Enquiry {
    
    DataAccess da = new DataAccess((T24Context) this);
    
    public static void Info(String line) {

        // String filePath = "D:\\test.txt";
        String filePath = "/nsbt24/debug/logzDaham.txt";
        line = String.valueOf(line) + "\n";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        try {
            File myObj = new File(filePath);
            if (myObj.createNewFile()) {
                FileWriter fw = new FileWriter(filePath);
                fw.write("---" + line);
                fw.close();
            } else {
                Files.write(Paths.get(filePath, new String[0]), line.getBytes(),
                        new OpenOption[] { StandardOpenOption.APPEND });
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        
        
        
        Session T24session = new Session((T24Context) this);
        String T24date = T24session.getCurrentVariable("!TODAY");
        TDate tdate = new TDate(T24date);
        
        Info("getCompanyId="+T24session.getCompanyId());
        Info("getUserRoles="+T24session.getUserRoles());
        Info("getUserDispoRights="+T24session.getUserDispoRights());
        Info("getUserRecord="+T24session.getUserRecord());
        Info("getUserId="+T24session.getUserId());
        Info("getCompanyId="+T24session.getCompanyId());
  
 
        try{

            List<String> recarrr = this.da.selectRecords("", "EB.DISBURSEMENT.DETS.NSB", "", "WITH DISBURSEMENT.DT EQ 20200915");
                
                String fieldName = "@ID";
                
                for(int i=0; i<recarrr.size(); i++){
                   
                    FilterCriteria fc1 = new FilterCriteria();

                               fc1.setFieldname(fieldName);
                               fc1.setOperand("EQ");
                               fc1.setValue(recarrr.get(i));
                               filterCriteria.add(fc1);
                               
                }
                
        }catch(Exception e){
           
        }
        
        return filterCriteria;
    }
    
    

}
