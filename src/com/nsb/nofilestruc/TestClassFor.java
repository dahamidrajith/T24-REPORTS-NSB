package com.nsb.nofilestruc;

import java.util.List;

import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.ebcontractbalances.EbContractBalancesRecord;
import com.temenos.tafj.api.client.impl.T24Context;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class TestClassFor extends Enquiry {

    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        
        
        for(FilterCriteria fieldNames : filterCriteria){
            
            String FieldName = fieldNames.getFieldname();
            String operand= fieldNames.getOperand();
            String value= fieldNames.getValue();
            
            //////////////////////////////////////////////////////////////////////
            
            
//            give your logic
            
            
            ////////////////////////////////////////////////////////////////////
            
            FilterCriteria fc1 = new FilterCriteria();
            fc1.setFieldname("");
            fc1.setOperand("");
            fc1.setValue("");
            filterCriteria.add(fc1); 

        }

        
        return filterCriteria;
    }
    
    

}
