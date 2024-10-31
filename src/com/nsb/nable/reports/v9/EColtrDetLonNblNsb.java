package com.nsb.nable.reports.v9;

import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
import com.temenos.t24.api.records.collateralright.CollateralRightRecord;
import com.temenos.t24.api.system.DataAccess;
import java.util.ArrayList;
import java.util.List;
import com.temenos.tafj.api.client.impl.T24Context;

public class EColtrDetLonNblNsb extends Enquiry {
    private  DataAccess da = new DataAccess((T24Context)this);

    private List<String> output = new ArrayList<>();

    @Override
    public List<String> setValues(String value, String currentId, TStructure currentRecord, List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        String[] s1 =  new String[2];
        s1[0] = "";
        s1[1] = "";
        String[] s2;
        String grantedAmountValue = "";
        String grantedDateValue = "" ;
        String collateralRightRecords = "";
        String arrangementId = "";
        try{
            s1 = value.split("\\*",2);
            arrangementId = s1[0];
        }catch(Exception e){
            grantedDateValue = "";
            grantedAmountValue = "";
        }
        if(arrangementId != null && !arrangementId.equals("")){
            Contract cnt = new Contract(this);
            cnt.setContractId(arrangementId);
            try{
                String prop = cnt.getPropertyIdsForPropertyClass("TERM.AMOUNT").get(0);
                AaPrdDesTermAmountRecord aaPrdDesTermAmountRecord = new AaPrdDesTermAmountRecord(cnt.getConditionForProperty(prop));
                grantedAmountValue = aaPrdDesTermAmountRecord.getAmount().toString();
            }catch(Exception e){
                grantedAmountValue = "";
            }
            try{
                AaAccountDetailsRecord AaAccdet = new AaAccountDetailsRecord(this.da.getRecord("AA.ACCOUNT.DETAILS", arrangementId));
                grantedDateValue = AaAccdet.getContractDate().get(0).toString();
            }catch(Exception e){
                grantedDateValue = "";
            }
        }
        try{
            collateralRightRecords = s1[1];
            s2 = collateralRightRecords.split(" ");
            if(s2.length>0){
                for (int i = 0; i < s2.length; i++) {
                    if (!s2[i].equals("") && !s2[i].isEmpty() && s2[i] != null){
                        CollateralRightRecord collateralRightRecord = new CollateralRightRecord(this.da.getRecord("COLLATERAL.RIGHT", s2[i]));
                        for(int j =0; j<collateralRightRecord.getCollateralId().size();j++){
                            String collateralId = collateralRightRecord.getCollateralId().get(j).toString();
                            buildList(collateralId, grantedDateValue, grantedAmountValue);
                        }
                    }else{
                    }
                }
            }
        }catch(Exception e){
            buildList("", grantedDateValue, grantedAmountValue);
        }
        return output;
    }

    private void buildList(String collateralId, String grantedDateValue, String grantedAmountValue) {
        String result = "";
        try {
            StringBuilder str = new StringBuilder();
            str.append(collateralId);
            str.append("*" + grantedDateValue);
            str.append("*" + grantedAmountValue);
            result = str.toString();
            output.add(result);
        } catch (Exception e) {
            output.add(""+"*"+""+"*"+"");
        } 
    }
}


