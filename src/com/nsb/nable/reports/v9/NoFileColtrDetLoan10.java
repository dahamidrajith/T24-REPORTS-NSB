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
import java.util.HashSet;
import java.util.List;

import org.json.*;

import com.temenos.api.TField;
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
public class NoFileColtrDetLoan10 extends Enquiry {
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
        log("Start CollatDet10");
        setFilterCriteria(filterCriteria);
        List<String> collateralIds = this.da.selectRecords("", "COLLATERAL", "","WITH COLLATERAL.TYPE EQ 603");
        int recarrSize = collateralIds.size();
        List<String> collatRights;
        log(String.valueOf(collateralIds.size()));
        HashSet<String> collRight = new HashSet<String>();
        log("A");
        for (int i = 0; i < recarrSize; i++) {
            log("B");
            List<TField> limitIds;
            try{
                if(!collateralIds.get(i).equals(this.collateral) && !this.collateral.equals("''")){
                    continue;
                }
                CollateralRecord collRec = new CollateralRecord(this.da.getRecord("COLLATERAL", collateralIds.get(i)));
                CollateralRightRecord collateralRight = new CollateralRightRecord(this.da.getRecord("COLLATERAL.RIGHT", collRec.getCollateralRightId().toString()));
                limitIds = collateralRight.getLimitId();
            }catch(Exception E){
                continue;
            }
            log("C");
            OUTER: for(TField id:limitIds){
                String arrangementId = "";
                String branchValue = "";
                String grantedAmountValue = "";
                String grantedDateValue = "";
                try{
                    log("D");
                    List<String> accountIds = this.da.selectRecords("", "ACCOUNT", "","WITH LIMIT.KEY EQ " + id.toString());
                    if(accountIds.size() == 0 || accountIds.equals(null)){
                        continue;
                    }
                    AccountRecord accountRecord = new AccountRecord(this.da.getRecord("ACCOUNT", accountIds.get(0)));
                    arrangementId = accountRecord.getArrangementId().toString();
                    if(!arrangementId.equals(this.arrangement) && !this.arrangement.equals("''")){
                        if(!accountIds.get(0).equals(this.arrangement) && !this.arrangement.equals("''")){
                            continue OUTER;
                        }
                    }
                    try{
                        Contract cnt = new Contract(this);
                        cnt.setContractId(arrangementId);
                        String prop = cnt.getPropertyIdsForPropertyClass("TERM.AMOUNT").get(0);
                        AaPrdDesTermAmountRecord aaPrdDesTermAmountRecord = new AaPrdDesTermAmountRecord(cnt.getConditionForProperty(prop));
                        grantedAmountValue  = aaPrdDesTermAmountRecord.getAmount().toString();
                    }catch(Exception e){
                        grantedAmountValue = "";
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
                }catch(Exception e){
                    log(e.toString());
                    continue;
                }
                log("E");
                log("F");
                JSONObject productList = null;
                AaArrangementRecord AaArr = null;
                try{
                    AaArr = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", arrangementId));    
                }catch(Exception e){                    
                }
                try {
                    productList = new JSONObject(AaArr.getProduct().get(0).toString());
                } catch (JSONException e) {
                    log(e.toString());
                }
                try{
                if(!AaArr.getProductGroup().toString().equals(this.productGroup) && !this.productGroup.equals("''")){
                    continue;
                }
                }catch(Exception e){                    
                }
                log("G");
                try {
                    if(!productList.get("product").toString().equals(this.product) && !this.product.equals("''")){
                        continue;
                    }
                } catch (JSONException e) {
                    log(e.toString());
                }
                log("H");
                branchValue = AaArr.getCoCodeRec().toString();
                if(!branchValue.equals(this.branch) && !this.branch.equals("''")){
                    continue;
                }
                log("I");
                String date1 = AaArr.getStartDate().toString();
                if (!checkDate(date1)){
                    continue;
                }
                log(cif);
                log(nic);
                for (int c = 0;c<AaArr.getCustomer().size();c++){
                    try{
                        log("J");
                        if(!AaArr.getCustomer(c).getCustomer().toString().equals(this.cif) && !this.cif.equals("''")){
                            if(c == AaArr.getCustomer().size() -1){
                                continue OUTER;
                            }
                            continue;
                        }
                        log("okay");
                        CustomerRecord customerClass = new CustomerRecord(this.da.getRecord("CUSTOMER", AaArr.getCustomer(c).getCustomer().toString()));
                        String customerNic = customerClass.getLegalId(0).getLegalId().toString();
                        log(customerNic);
                        if(!customerNic.equals(this.nic) && !this.nic.equals("''")){
                            continue OUTER;
                        }
                        log("K");
                    }catch(Exception e){
                        continue;
                    }
                }
                buildList(arrangementId, grantedDateValue , collateralIds.get(i),  grantedAmountValue);
            }
        }
        try{
        for(String out:output){
            log(out);
        }
        }catch(Exception e){
            log(e.toString());
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

            if (FieldName.equals("DATE") && !fieldNames.getValue().equals("''")){
                this.date = fieldNames.getValue();
                this.dateOperand = fieldNames.getOperand();
            }
            if (FieldName.equals("ARRANGEMENT.ID")){
                this.arrangement  = fieldNames.getValue();
            }
            if (FieldName.equals("COLLATERAL"))
                this.collateral = fieldNames.getValue();
        }
    }


    private boolean checkDate(String date1) {
        if (this.dateOperand.equals("''")){
            return true;
        }
        if (this.date.equals("''")){
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

    private void buildList(String arrangementId, String applicationNo, String collateralId, String grantedAmountValue) {
        String result = "";
        try {
            StringBuilder str = new StringBuilder();
            str.append(arrangementId);
            str.append("*" + applicationNo);
            str.append("*" + collateralId);
            str.append("*" + grantedAmountValue);
            result = str.toString();
            output.add(result);
        } catch (Exception e) {
            output.add("***");
        } 
    }

}