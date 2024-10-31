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

import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aabilldetails.AaBillDetailsRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author manethm
 *
 */
public class NoFileInspacChargesArrangement extends Enquiry{
    private DataAccess da = new DataAccess((T24Context)this);

    private List<String> output = new ArrayList<>();

    private List<String> BillIds = new ArrayList<>() ;
    private String branch = "''";

    private String date = "''";

    private String cif = "''";

    private String dateOperand = "''";

    private String arrangementId  = "''";

    private String loanId  = "''";

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

    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        log("Start Loan Maintenance Fee");
        setFilterCriteria(filterCriteria);
        BillIds = this.da.selectRecords("", "AA.BILL.DETAILS","", " WITH BILL.TYPE EQ ACT.CHARGE AND PROPERTY EQ CRIBCHG");
        OUTER:
            for(String billId:BillIds){
                try{
                    AaBillDetailsRecord billRecord = new AaBillDetailsRecord(this.da.getRecord("AA.BILL.DETAILS", billId));
                    if(!billRecord.getCoCode().toString().equals(this.branch) && !this.branch.equals("''")){
                        log(billRecord.getCoCode() + " != " + this.branch);
                        continue; 
                    }
                    String date1 = billRecord.getPaymentDate().toString();
                    if (!checkDate(date1)){
                        continue;
                    }
                    if(!billRecord.getArrangementId().toString().equals(this.arrangementId) && !this.arrangementId.equals("''")){
                        continue;
                    }
                    AaArrangementRecord arrangementRecord = new AaArrangementRecord(this.da.getRecord("AA.ARRANGEMENT", billRecord.getArrangementId().toString()));
                    List<String> customer  = new ArrayList<>();;
                    for(int i = 0;i<arrangementRecord.getCustomer().size();i++){
                        customer.add(arrangementRecord.getCustomer().get(i).getCustomer().toString());
                        if(arrangementRecord.getCustomer().get(i).getCustomer().toString().equals(this.cif) || this.cif.equals("''")){
                            break;
                        }
                        if(i == arrangementRecord.getCustomer().size() -1){
                            continue OUTER;
                        }
                    }
                    List<String> account = new ArrayList<>();
                    for(int i = 0;i<arrangementRecord.getLinkedAppl().size();i++){
                        account .add(arrangementRecord.getLinkedAppl().get(i).getLinkedApplId().toString());
                        if(arrangementRecord.getLinkedAppl(i).getLinkedAppl().toString().equals(this.loanId) || this.loanId.equals("''")){
                            break;
                        }
                        if(i == arrangementRecord.getLinkedAppl().size() -1){
                            continue OUTER;
                        }
                    }
                    log("FilterCriteriaDone");
                    output.add(billRecord.getProperty(0).getProperty().toString() + "*" + billRecord.getArrangementId().toString() +  "*" + arrangementRecord.getCustomer().get(0).getCustomer().toString()
                            + "*" + billRecord.getPaymentDate().toString() +"*" +  billRecord.getOrTotalAmount().toString() + 
                            "*" + arrangementRecord.getLinkedAppl(0).getLinkedAppl().toString() + "*" + billRecord.getCoCode().toString());
                    int max = customer.size();
                    int min = account.size();
                    int name = 0;
                    if(account.size()>max){
                        max = account.size();
                        min = customer.size();
                        name = 1;
                    }
                    for (int j = 1;j<min; j++){
                        output.add("" + "*" + "" + "*" + customer.get(j) + "*" + "" + "*" + "" + "*" + account.get(j) + "*" + "");
                    }
                    for (int i = 1;i<max; i++){
                        if (name == 0){
                            output.add("" + "*" + "" + "*" + customer.get(i + min) + "*" + "" + "*" + "" + "*" + "" + "*" + "");
                        }
                        if (name == 1){
                            output.add("" + "*" + "" + "*" + "" + "*" + "" + "*" + "" + "*" + account.get(i + min) + "*" + "");
                        }
                    }
                }catch(Exception e){
                    log(e.toString());
                    continue;
                }
            }
        return output;
    }

    private void setFilterCriteria(List<FilterCriteria> filtercriteria) {
        for (FilterCriteria fieldNames : filtercriteria) {

            String FieldName = fieldNames.getFieldname();

            if (FieldName.equals("BRANCH"))
                this.branch = fieldNames.getValue();

            if (FieldName.equals("ARRANGEMENTID"))
                this.arrangementId = fieldNames.getValue();

            if (FieldName.equals("LOANID"))
                this.loanId = fieldNames.getValue();

            if (FieldName.equals("CIF"))
                this.cif = fieldNames.getValue();

            if (FieldName.equals("DATE") && !fieldNames.getValue().equals("''")){
                this.date = fieldNames.getValue();
                this.dateOperand = fieldNames.getOperand();
            }
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
                return ((date.compareTo(startDate)) > 0 && (date.compareTo(endDate) < 0));
            }

        } catch (Exception e) {
            log(e.toString());
            return false;
        }
        return false;
    }
}
