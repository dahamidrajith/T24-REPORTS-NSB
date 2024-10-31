package com.nsb.certiprint.nable.ne;

import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.aaprddescustomer.AaPrdDesCustomerRecord;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class GetPassbookOI extends RecordLifecycle {
    
    Session T24session = new Session((T24Context) this);
    String T24date = T24session.getCurrentVariable("!TODAY");
    TDate tdate = new TDate(T24date);

    @Override
    public String formatDealSlip(String data, TStructure currentRecord, TransactionContext transactionContext) {
        
        System.out.println("CALLED");
        System.out.println("NEWJAR-"+data);
        Contract cnt = new Contract((T24Context) this);
        cnt.setContractId(data);
       
        String rtnValue="";
        
        try{
            
            AaPrdDesCustomerRecord obj= new AaPrdDesCustomerRecord(cnt.getConditionForPropertyEffectiveDate("CUSTOMER", tdate));
            rtnValue=obj.getLocalRefField("L.OPE.INST").toString();
            System.out.println("NEWRTNVALUE-"+rtnValue);
            
            
        }catch(Exception e){
            System.out.println("Exception-"+e);
            e.printStackTrace();
        }
        
        return rtnValue;
    }


}
