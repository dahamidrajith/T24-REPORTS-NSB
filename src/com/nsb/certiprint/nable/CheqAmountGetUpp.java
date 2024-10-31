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
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class CheqAmountGetUpp extends RecordLifecycle {


    @Override
    public String formatDealSlip(String data, TStructure currentRecord, TransactionContext transactionContext) {
        
        String rtnValue="";
        DecimalFormat df = new DecimalFormat("0.00");
      
        try {
            
                double dataDoub = Double.parseDouble(data);
                BigDecimal bigDecimal3 = new BigDecimal(dataDoub);
                String befrtnValue = df.format(bigDecimal3);
                
                String[] parts = befrtnValue.split("\\.");
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedIntegerPart = decimalFormat.format(Long.parseLong(parts[0]));
                
                rtnValue = formattedIntegerPart + (parts.length > 1 ? "." + parts[1] : "");
            
          }catch(Exception e){
              rtnValue="0.00";
          }
        
        return rtnValue;
    }

}
