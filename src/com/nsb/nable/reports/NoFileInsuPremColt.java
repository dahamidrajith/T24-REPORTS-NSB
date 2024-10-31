package com.nsb.nable.reports;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.temenos.api.TDate;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaprddescharge.AaPrdDesChargeRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.collateral.CollateralRecord;
import com.temenos.t24.api.records.collateralright.CollateralRightRecord;
import com.temenos.t24.api.records.limit.LimitRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class NoFileInsuPremColt extends Enquiry {
    
    DataAccess da = new DataAccess((T24Context) this);
    
    String dateInput;
    String insuType;
    String insuCompany;
    String branch;
    String process = null;
    String Result = null;
    List<String> RETURN_LIST = new ArrayList<>();

    @Override
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        
        try {

            List<String> selarr = getAccId(filterCriteria);

            try {

                this.dateInput = selarr.get(0);
                this.insuType = selarr.get(1);
                this.insuCompany = selarr.get(2);
                this.branch = selarr.get(3);

                String process = processarr(this.dateInput, this.insuType, this.insuCompany, this.branch, "");
               
            } catch (Exception e) {
                
                this.process=processarr("", "", "", "", "");
            }

        } catch (Exception e) {
        }
        
        return super.setIds(filterCriteria, enquiryContext);
    }
    
    private List<String> getAccId(List<FilterCriteria> filtercriteria) {
        
        for (FilterCriteria fieldNames : filtercriteria) {
            String FieldName = fieldNames.getFieldname();
//            if (FieldName.equals("DATE.INPUT"))
            if (FieldName.equals(""))
                this.dateInput = fieldNames.getValue();
            if (FieldName.equals(""))
                this.insuType = fieldNames.getValue();
            if (FieldName.equals(""))
                this.insuCompany = fieldNames.getValue();
            if (FieldName.equals(""))
                this.branch = fieldNames.getValue();
        }
        List<String> li = new ArrayList<>();
        li.add(this.dateInput);
        li.add(this.insuType);
        li.add(this.insuCompany);
        li.add(this.branch);
        return li;
    }
    
    private String processarr(String dateInputVal, String insuTypeVal, String insuCompanyVal, String branchVal,
            String PR_LIST) {
        
        String assignDate="";
        boolean status2=false;
        String accNumber = "";
        String collatIdArray="";
        String collatValue="";
        String titleype="";
        String titleTypeName="";
        String fireType="";
        String fireTypeName="";
        String dtaType="";
        String dtaTypeName="";
        String policePrem="";
        String commisionMount="";
        String appendDate="";
        String insuTyps="";
        String insuCompanys="";
        
        Session T24session = new Session((T24Context) this);
        
        if(dateInputVal==null || dateInputVal.trim().equals("")){
            assignDate="!TODAY";
        }else{
            assignDate=dateInputVal;
            status2=true;
        }
        
        String T24date = T24session.getCurrentVariable(assignDate);
        TDate tdate = new TDate(T24date);
        
        
        try{
            
            List<String> recarr = this.da.selectRecords("", "AA.ARRANGEMENT", "", "WITH PRODUCT.LINE EQ LENDING AND ARR.STATUS EQ CURRENT");
            
            for (int j = 0; j < recarr.size(); j++) {
                String accountNumber="";
                AaArrangementRecord AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", recarr.get(j)));
                
                accountNumber = AaArr.getLinkedAppl().toString();
                if(AaArr.getLinkedAppl().size()>0){
                    JSONArray jsonArrayOne = new JSONArray(accountNumber);
                    JSONObject explrObjectFour = jsonArrayOne.getJSONObject(0);
                    
                    if(explrObjectFour.has("linkedApplId")){
                    accNumber = explrObjectFour.get("linkedApplId").toString();
                    
                    if(!accNumber.equals("") || accNumber!=null){
                        
                        AccountRecord acont = new AccountRecord(this.da.getRecord("ACCOUNT", accNumber));
                        
                        try{
                            
                        String limitKey= acont.getLimitKey().toString();
                        
                        if(!limitKey.equals("") || limitKey!=null){
                            LimitRecord collRightRec = new LimitRecord(this.da.getRecord("LIMIT", limitKey));
                            String collatRight ="";
                            try{
                            collatRight = collRightRec.getCollatRightTop().toString();
                            
                            }catch(Exception e){
                                collatRight="";
                            }
                            
                            if(!collatRight.equals("") || collatRight!=null){
                                
                                CollateralRightRecord colRytRec = new CollateralRightRecord(
                                        this.da.getRecord("COLLATERAL.RIGHT", collatRight));

                                collatIdArray = colRytRec.getCollateralId().toString();

                                if (colRytRec.getCollateralId().size() > 0) {
                                    JSONArray jsonArray = new JSONArray(collatIdArray);
                                    for (int z = 0; z < jsonArray.length(); z++) {
                                        collatValue = jsonArray.get(z).toString();

                                        CollateralRecord collateralRec = new CollateralRecord(
                                                this.da.getRecord("COLLATERAL", collatValue));
                                        String valDate="";
                                        
                                        try{
                                            valDate=collateralRec.getValueDate().toString();
                                        }catch(Exception e){
                                            valDate="";
                                        }

                                        try {

                                            int titleTypeLength = collateralRec.getLocalRefGroups("L.T.IN.A.NSB").size();
                                            for (int k = 0; k < titleTypeLength; k++) {
                                                titleype = collateralRec.getLocalRefGroups("L.T.IN.A.NSB").get(k)
                                                        .getLocalRefField("L.T.IN.A.NSB").getValue();
                                                titleTypeName = "Title Insurance";
                                            }

                                        } catch (Exception e) {
                                            titleTypeName = "";
                                        }

                                        try {

                                            fireType = collateralRec.getLocalRefField("L.FIRE.I.AP.NSB").getValue();
                                            if (fireType.equals("Y")) {
                                                fireTypeName = "Fire Type";
                                            }

                                        } catch (Exception e) {
                                            fireTypeName = "";
                                        }

                                        try {

                                            int dtaTypeLength = collateralRec.getLocalRefGroups("L.DTA.IN.A.NSB").size();
                                            for (int w = 0; w < dtaTypeLength; w++) {
                                                dtaType = collateralRec.getLocalRefGroups("L.DTA.IN.A.NSB").get(w)
                                                        .getLocalRefField("L.DTA.IN.A.NSB").getValue();
                                                dtaTypeName = "DTA Insurance";
                                            }

                                        } catch (Exception e) {
                                            dtaTypeName = "";
                                        }

                                        try {

                                            String insuCompanyTitle = "";
                                            String insuCompanyFire = "";
                                            String insuCompanyDta = "";

                                            try {

                                                int insuCompanyLengthTitle = collateralRec.getLocalRefGroups("L.T.IN.IS.NSB").size();
                                                for (int w = 0; w < insuCompanyLengthTitle; w++) {
                                                    insuCompanyTitle = collateralRec.getLocalRefGroups("L.T.IN.IS.NSB").get(w)
                                                            .getLocalRefField("L.T.IN.IS.NSB").getValue();
                                                }
                                            } catch (Exception e) {
                                                insuCompanyTitle = "";
                                            }

                                            try {

                                                int insuCompanyLengthFire = collateralRec.getLocalRefGroups("L.FR.IN.IS.NSB").size();
                                                for (int w = 0; w < insuCompanyLengthFire; w++) {
                                                    insuCompanyFire = collateralRec.getLocalRefGroups("L.FR.IN.IS.NSB").get(w)
                                                            .getLocalRefField("L.FR.IN.IS.NSB").getValue();
                                                }
                                            } catch (Exception e) {
                                                insuCompanyFire = "";
                                            }

                                            try {

                                                int insuCompanyLengthDta = collateralRec.getLocalRefGroups("L.DTA.I.I.1.NSB").size();
                                                for (int w = 0; w < insuCompanyLengthDta; w++) {
                                                    insuCompanyDta = collateralRec.getLocalRefGroups("L.DTA.I.I.1.NSB").get(w)
                                                            .getLocalRefField("L.DTA.I.I.1.NSB").getValue();
                                                }
                                            } catch (Exception e) {
                                                insuCompanyDta = "";
                                            }

                                            insuCompanys = insuCompanyTitle + "|" + insuCompanyFire + "|" + insuCompanyDta;

                                        } catch (Exception e) {
                                            insuCompanys = "||";
                                        }
                                        
                                        try {
                                            
                                            String fireInsuPemLen="";
                                            String dtaInsuPremLen="";

                                            try {
                                                
                                                fireInsuPemLen = collateralRec.getLocalRefField("L.FR.SM.A.NSB").getValue();
                                            } catch (Exception e) {
                                                fireInsuPemLen="";
                                            }
                                            
                                            try {
                                                
                                                dtaInsuPremLen = collateralRec.getLocalRefField("L.DTA.S.A.1.NSB").getValue();
                                            } catch (Exception e) {
                                                dtaInsuPremLen="";
                                            }
                                            
                                            policePrem="|"+fireInsuPemLen + "|" + dtaInsuPremLen;

                                        } catch (Exception e) {
                                            policePrem = "||";
                                        }


                                        try {

                                            Contract cnt = new Contract((T24Context) this);
                                            cnt.setContractId(recarr.get(j));

                                            AaPrdDesChargeRecord prodChar = new AaPrdDesChargeRecord(
                                                    cnt.getConditionForPropertyEffectiveDate("SERVCHG", tdate));

                                            commisionMount = prodChar.getLocalRefField("L.INS.COMM.NSB").getValue();

                                        } catch (Exception e) {
                                            commisionMount = "";
                                        }

                                        insuTyps = titleTypeName + "|" + fireTypeName + "|" + dtaTypeName;

                                        appendDate = buildList(insuTyps, insuCompanys, commisionMount, policePrem);
                                        

                                    }
                                }else{
                                    insuTyps="||";
                                    insuCompanys = "||";
                                    policePrem = "||";
                                    
                                    appendDate = buildList(insuTyps, insuCompanys, commisionMount, policePrem);
                                    
                                }
                                
                            }
                        }
                    }catch(Exception e){
                        continue;
                    }
                        
                    }
                    
                    }
                }
            }
            
        }catch(Exception e){
            
        }
        
        return PR_LIST;
    }
    
    private String buildList(String insuTypeValueFin, String insuCompanyValueFin, String commisionMountValueFin,
            String policePremFinVAl) {

        String Result = "";

        try {

            StringBuilder str = new StringBuilder();
            str.append(insuTypeValueFin);
            str.append("*" + insuCompanyValueFin);
            str.append("*" + commisionMountValueFin);
            str.append("*" + policePremFinVAl);

            Result = str.toString();
            
        } catch (Exception e) {
            Result = "" + "*" + "" + "*" + "" + "*" + "";
        }
        return Result;
    }
    
}
