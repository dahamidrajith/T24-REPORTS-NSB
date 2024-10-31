package com.nsb.nable.reports.v9;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.*;

import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaprddestermamount.AaPrdDesTermAmountRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.collateral.CollateralRecord;
import com.temenos.t24.api.records.collateralright.CollateralRightRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.limit.CollatRightTopClass;
import com.temenos.t24.api.records.limit.LimitRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author manethm
 *
 */
public class NoFileColtrDetLoan7 extends Enquiry {
    private DataAccess da = new DataAccess((T24Context)this);

    private List<String> output = new ArrayList<>();

    private String productGroup = "''";

    private String product = "''";

    private String branch = "''";

    private String date = "''";

    private String cif = "''";

    private String nic = "''";

    private String collateral = "''";

    private String dateOperand = "''";

    private String arrangement = "''";

    private Object startdateOperand = "''";

    private Object startdate = "''";

    private String productval = "''";

    public static void log(String line) {
        String filePath = "/nsbt24/debug/logNBIM.txt";
        line = String.valueOf(String.valueOf(line)) + "\n";
        try {
            File myObj = new File(filePath);
            if (myObj.createNewFile()) {
                FileWriter fw = new FileWriter(filePath);
                fw.write("---" + line);
                fw.close();
            } else {
                Files.write(Paths.get(filePath, new String[0]), line.getBytes(), new OpenOption[] { StandardOpenOption.APPEND });
            } 
        } catch (Exception e) {
            e.getStackTrace();
        } 
    }

    @Override
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        log("Start CollatDet");
        setFilterCriteria(filterCriteria);
        List<String> arrangementIds = this.da.selectRecords("", "AA.ARRANGEMENT", "","WITH PRODUCT.GROUP EQ AL.PERSONAL.LOAN.NSB");
        int recarrSize = arrangementIds.size();
        List<String> collatRights;
        log(String.valueOf(arrangementIds.size()));
        OUTER: for (int i = 0; i < recarrSize; i++) {
            String branchValue = "";
            String applicationNo = "";
            String arrangementId = "";
            String grantedAmountValue = "";
            String grantedDateValue = "";
            try{
                arrangementId = arrangementIds.get(i);
                if(!arrangementId.equals(this.arrangement ) && !this.arrangement.equals("''")){
                    if(checkAccount(arrangementId)){
                    continue;
                    }
                    log("checkpassed2");
                }
                if(arrangementId != null && !arrangementId.equals("")){
                    Contract cnt = new Contract(this);
                    cnt.setContractId(arrangementId);
                    String prop = cnt.getPropertyIdsForPropertyClass("TERM.AMOUNT").get(0);
                    AaPrdDesTermAmountRecord aaPrdDesTermAmountRecord = new AaPrdDesTermAmountRecord(cnt.getConditionForProperty(prop));
                    grantedAmountValue  = aaPrdDesTermAmountRecord.getAmount().toString();
                }
                try{
                    AaAccountDetailsRecord AaAccdet = new AaAccountDetailsRecord(this.da.getRecord("AA.ACCOUNT.DETAILS", arrangementId));
                    grantedDateValue = AaAccdet.getContractDate().get(0).toString();
                }catch(Exception e){
                    grantedDateValue = "";
                }
                if(!grantedDateValue.equals(this.date) && !this.date.equals("''")){
                    continue;
                }
                AaArrangementRecord AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", arrangementId));
                JSONObject productList = new JSONObject(AaArr.getProduct().get(0).toString());
                if(!AaArr.getProductGroup().toString().equals(this.productGroup) && !this.productGroup.equals("''")){
                    continue;
                }
                if(!productList.get("product").toString().equals(this.product) && !this.product.equals("''")){
                    continue;
                }
                productval = productList.get("product").toString();
                branchValue = AaArr.getCoCodeRec().toString();
                if(!branchValue.equals(this.branch) && !this.branch.equals("''")){
                    continue;
                }
                String date1 = AaArr.getStartDate().toString();
                if (!checkDate(date1)){
                    continue;
                }
                log(cif);
                log(nic);
                for (int c = 0;c<AaArr.getCustomer().size();c++){
                    if(!AaArr.getCustomer(c).getCustomer().toString().equals(this.cif) && !this.cif.equals("''")){
                        log("1");
                        continue OUTER;
                    }
                    log("okay");
                    CustomerRecord customerClass = new CustomerRecord(this.da.getRecord("CUSTOMER", AaArr.getCustomer(c).getCustomer().toString()));
                    String customerNic = customerClass.getLegalId(0).getLegalId().toString();
                    log(customerNic);
                    if(!customerNic.equals(this.nic) && !this.nic.equals("''")){
                        log("1");
                        continue OUTER;
                    }
                }
                log("FILTERCRITERIADONE");
                applicationNo = AaArr.getLinkedAppl().get(0).getLinkedApplId().toString();

                AccountRecord accountRecord = new AccountRecord(this.da.getRecord("ACCOUNT", applicationNo));
                LimitRecord limitRecord = new LimitRecord(this.da.getRecord("LIMIT", accountRecord.getLimitKey().toString()));
                log("newneew");
                List<CollatRightTopClass> collateralRightRecords = limitRecord.getCollatRightTop();     
                collatRights = new ArrayList<>();
                for(CollatRightTopClass collateralRight:collateralRightRecords){
                    JSONObject collateralRightList = new JSONObject(collateralRight.toString());
                    log(collateralRightList.toString());
                    int length = collateralRightList.length();
                    JSONArray jsonArray = new JSONArray(collateralRightList.get("CollatRight").toString());
                    log("done");
                    for (int i1 = 0;i1<jsonArray.length();i1++){
                        log(String.valueOf(i1));
                        JSONObject jsonObject = new JSONObject(jsonArray.get(i1).toString());
                        log(jsonObject.toString());
                        log((String) jsonObject.get("collatRight"));
                        collatRights.add((String) jsonObject.get("collatRight"));
                    }
                }
                log("donedone");
            }catch(Exception e){
                log("p");
                log(e.toString());
                continue;
            }
            log("done3");
            for(String collatRight:collatRights){
                CollateralRightRecord collateralRight1;
                try{
                    collateralRight1 = new CollateralRightRecord(this.da.getRecord("COLLATERAL.RIGHT", collatRight));
                    if (!collateralRight1.getCollateralCode().toString().equals("600")){
                        continue;
                    }
                }catch(Exception e){
                    log("q");
                    log(e.toString());
                    continue; 
                }
                log("done4");
                //Iterating through collateral Records
                for(int j = 0; j<collateralRight1.getCollateralId().size(); j++){
                    String collateralId;
                    try{
                        collateralId = collateralRight1.getCollateralId().get(j).toString();
                        if(!collateralId.equals(this.collateral) && !this.collateral.equals("''")){
                            continue;
                        }
                        CollateralRecord collRec = new CollateralRecord(this.da.getRecord("COLLATERAL", collateralId));
                        if(!collRec.getCollateralType().toString().equals("603")){
                            continue;
                        }
                        if (j == 0){
                            buildList(arrangementId, grantedDateValue, collateralId,  grantedAmountValue, productval );
                        }
                        else{
                            buildList("", grantedDateValue, collateralId,  grantedAmountValue, productval );
                        }
                    }catch(Exception e){
                        log("r");
                        continue; 
                    }
                }
            }
        }
        for(String outpu:output){
            log(outpu);
        }
        return output;
    }

    private void setFilterCriteria(List<FilterCriteria> filtercriteria) {
        for (FilterCriteria fieldNames : filtercriteria) {

            String FieldName = fieldNames.getFieldname();

            if (FieldName.equals("PRODUCT.GROUP"))
                this.productGroup = fieldNames.getValue();

            if (FieldName.equals("PRODUCT"))
                this.product = fieldNames.getValue();

            if (FieldName.equals("BRANCH"))
                this.branch = fieldNames.getValue();

            if (FieldName.equals("CIF"))
                this.cif = fieldNames.getValue();

            if (FieldName.equals("NIC"))
                this.nic = fieldNames.getValue();

            if (FieldName.equals("ARRANGEMENT.ID"))
                this.arrangement = fieldNames.getValue();

            if (FieldName.equals("DATE") && !fieldNames.getValue().equals("''")){
                this.date = fieldNames.getValue();
                this.dateOperand = fieldNames.getOperand();
            }
            if (FieldName.equals("START.DATE") && !fieldNames.getValue().equals("''")){
                this.startdate = fieldNames.getValue();
                this.startdateOperand = fieldNames.getOperand();
            }
            if (FieldName.equals("COLLATERAL"))
                this.collateral = fieldNames.getValue();
        }
    }

    private void buildList(String arrangementId, String applicationNo, String collateralId, String grantedAmountValue, String productVal) {
        String result = "";
        try {
            StringBuilder str = new StringBuilder();
            str.append(arrangementId);
            str.append("*" + applicationNo);
            str.append("*" + collateralId);
            str.append("*" + grantedAmountValue);
            str.append("*" + productVal);
            result = str.toString();
            output.add(result);
        } catch (Exception e) {
            output.add("***");
        } 
    }
    private boolean checkDate(String date1) {
        if (this.startdateOperand.equals("''")){
            return true;
        }
        if (this.startdate.equals("''")){
            return false;
        }
        try {
            SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMdd");
            Date date;
            date = sdformat.parse(date1);
            Date inputDate = sdformat.parse(this.date);
            if(this.dateOperand.equals("5")) {
                return date.compareTo(inputDate) == 0;
            }
            if(this.dateOperand.equals("9")) {
                return date.compareTo(inputDate) >= 0;
            }
            if(this.dateOperand.equals("8")) {
                return date.compareTo(inputDate) <= 0;
            }
            if(this.dateOperand.equals("3")) {
                return date.compareTo(inputDate) < 0;
            }
            if(this.dateOperand.equals("4")) {
                return date.compareTo(inputDate) > 0;
            }
            if(this.dateOperand.equals("2")) {
                Date startDate = sdformat.parse(this.date.substring(0,8));
                Date endDate = sdformat.parse(this.date.substring(9,17));
                return ((date.compareTo(startDate)) > 0 && (date.compareTo(endDate) <0));
            }

        } catch (Exception e) {
            log(e.toString());
            return false;
        }
        return false;
    }
    
   private boolean checkAccount(String arrangement){
       log("check Start");
       AaArrangementRecord AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", arrangement));
       log(AaArr.getLinkedAppl().get(0).getLinkedApplId().toString() + "  " + this.arrangement);
       if (AaArr.getLinkedAppl().get(0).getLinkedApplId().toString().equals(this.arrangement)){
           log("checkPassed");
           return false;
       }
       return true;
   }
}