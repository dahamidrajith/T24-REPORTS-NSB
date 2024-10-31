package com.nsb.nofilestruc;

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

import com.temenos.api.TDate;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class ApiTestingClass extends Enquiry {
    
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
        
        String rtnValue="";
        DecimalFormat df = new DecimalFormat("0.00");
        
        List<BalanceMovement> PrincipleCurrentList = null;
        
        Info("TEST DONE="+filterCriteria.size());

        try {
            
            for (int i=0;i<filterCriteria.size();i++) {
                Info("1111="+filterCriteria.get(i).toString());
                Contract cnt = new Contract((T24Context) this);
                cnt.setContractId(filterCriteria.get(i).toString());
                
                PrincipleCurrentList = cnt.getBalanceMovementsForPeriod("AVLACCOUNTBL", "",tdate,tdate);
                    
                for(BalanceMovement a1: PrincipleCurrentList){
                    
                    String balance = a1.getBalance().toString();
                    Info("balance="+balance);
                }

            }

        } catch (Exception e) {
            Info("Exception="+e);
        }

        return filterCriteria;
    }

}
