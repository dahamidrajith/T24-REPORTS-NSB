package com.nsb.nable.reports.m;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.ebcontractbalances.EbContractBalancesRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class BuildLnTriBalModUpzz extends Enquiry {
    
    DataAccess da = new DataAccess((T24Context) this);
    
    public static void Info(String line) {

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

        String accNumber = "";
        String fieldName = "@ID";
        String val="";
        
        DecimalFormat df = new DecimalFormat("0.00");
        String outstandingBal = "";
        
        List<BalanceMovement> PrincipleCurrentList = null;
        
        try{
            
            
            String a1 = filterCriteria.get(0).toString();
            JSONObject obj1= new JSONObject(a1);
            val= obj1.get("fieldname").toString();
            Info("val="+val);
            
        }catch(Exception e){
            val="";
            Info("Exceprtion="+e);
        }
        
        try {

            if(!val.equals("ARRANGEMENT.ID")){
            

                List<String> recarr = this.da.selectRecords("", "AA.ARRANGEMENT", "", "WITH PRODUCT.LINE EQ LENDING ");

            for (int j = 0; j < recarr.size(); j++) {
                
                Contract cnt = new Contract((T24Context) this);
                cnt.setContractId(recarr.get(j));
                
               
                
                try{
                   
                        PrincipleCurrentList = cnt.getContractBalanceMovements("CURACCOUNT", "");
                        for (BalanceMovement bl : PrincipleCurrentList) {
                            outstandingBal = bl.getBalance().toString();
                            
                            if (outstandingBal.contains("-")) {
                              int outstandingBalBefLen = outstandingBal.length();
                              outstandingBal = outstandingBal.substring(1, outstandingBalBefLen);

                              
                          }
                            
                            

                        }
                        
                            double outBalDoubleFin = Double.parseDouble(outstandingBal);
                            
                            if(outBalDoubleFin>0){
                                
                              FilterCriteria fc1 = new FilterCriteria();
                              fc1.setFieldname(fieldName);
                              fc1.setOperand("EQ");
                              fc1.setValue(recarr.get(j));
                              filterCriteria.add(fc1);

                              
                                
                            }


                }catch(Exception e){
                    continue;
                }

            }
            
        }

        } catch (Exception e) {
            
        }

        if (filterCriteria.size() == 0) {

            FilterCriteria fc1 = new FilterCriteria();

            fc1.setFieldname(fieldName);
            fc1.setOperand("EQ");
            fc1.setValue("XXXX");
            filterCriteria.add(fc1);

        }
        Info("COUNT-"+filterCriteria.size());
        return filterCriteria;
    }
    
}
