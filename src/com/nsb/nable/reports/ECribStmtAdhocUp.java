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

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.acchargerequest.AcChargeRequestRecord;
import com.temenos.t24.api.records.fundstransfer.FundsTransferRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class ECribStmtAdhocUp extends Enquiry {
    
    DataAccess da = new DataAccess((T24Context)this);
    
    String appendDate = "";
    
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
    public String setValue(String value, String currentId, TStructure currentRecord,
            List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        
        String accNumber="";
        
        try{
            
            Info("NEW VALE-"+value);
            
            if(!value.equals("")){
                
                String codeType=value.substring(0,3);
                        
                       if(codeType.equals("AAA")){
                           
                           AaArrangementActivityRecord aaActivity=new AaArrangementActivityRecord(this.da.getRecord("AA.ARRANGEMENT.ACTIVITY",value));
                           String arrId=aaActivity.getArrangement().toString();
                           
                           AaArrangementRecord AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT",arrId));
                           String accountNumber = AaArr.getLinkedAppl().toString();
                           
                           JSONArray jsonArrayTwo = new JSONArray(accountNumber);
                           JSONObject explrObjectFour = jsonArrayTwo.getJSONObject(0);
                           accNumber = explrObjectFour.get("linkedApplId").toString();
                           Info("accNumber-"+accNumber);

                           
                       }else if(codeType.equals("CHG")){
                           
                           AcChargeRequestRecord chargeRecord = new AcChargeRequestRecord(this.da.getRecord("AC.CHARGE.REQUEST",value));
                           accNumber=chargeRecord.getExtraDetails(0).toString();
                           Info("accNumber-"+accNumber);
                           
                       }else if(codeType.contains("FT")){
                           
                           FundsTransferRecord fndTrans=new FundsTransferRecord(this.da.getRecord("", "FUNDS.TRANSFER", "",value));
                           accNumber=fndTrans.getChargesAcctNo().toString();
                           Info("accNumber-"+accNumber);
                           
                       }
                       
                        }
               
        }catch(Exception e){
            Info("Exception-"+e);
        }
        
        appendDate = buildList(accNumber);
        
        return appendDate;
    }
    
    private String buildList(String accNumberFin) {

        String Result = "";

        try {

            StringBuilder str = new StringBuilder();
            str.append(accNumberFin);
            
            Result = str.toString();
         
        } catch (Exception e) {
            Result = "";
        }
        return Result;
    }
    
    

}
