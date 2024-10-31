package com.nsb.certiprint.nable;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrtermamount.AaArrTermAmountRecord;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class GetAmount1 extends RecordLifecycle {
    
    Session T24session = new Session((T24Context) this);
    String T24date = T24session.getCurrentVariable("!TODAY");
    TDate tdate = new TDate(T24date);
    
    DataAccess da = new DataAccess((T24Context) this);


    @Override
    public String formatDealSlip(String data, TStructure currentRecord, TransactionContext transactionContext) {
        
        String rtnValue="";
        DecimalFormat df = new DecimalFormat("0.00");
        Contract cnt = new Contract((T24Context) this);
        cnt.setContractId(data);
        
        try {
            
            List<BalanceMovement> PrincipleCurrentList = null;
            
            PrincipleCurrentList = cnt.getBalanceMovementsForPeriod("CURACCOUNT", "",tdate,tdate);
            for (BalanceMovement bl : PrincipleCurrentList) {
                String balance = bl.getBalance().toString();
                
                double outstandingBalIntSub = Double.parseDouble(balance);
                BigDecimal bigDecimal3 = new BigDecimal(outstandingBalIntSub);
                String befrtnValue = df.format(bigDecimal3);
                
                String[] parts = befrtnValue.split("\\.");
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedIntegerPart = decimalFormat.format(Long.parseLong(parts[0]));
                
                try{
                    
                  AaArrangementRecord AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", data));
                  String cuurencyType = AaArr.getCurrency().getValue();
                  
                  rtnValue = cuurencyType.trim()+" "+formattedIntegerPart + (parts.length > 1 ? "." + parts[1] : "");
                    
                }catch(Exception e){
                    
                }
                
            }
            
          }catch(Exception e){
              rtnValue="0.00";
          }
        
        return rtnValue;
    }
    
    

}
