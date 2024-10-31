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
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;
import com.temenos.t24.api.records.fundstransfer.FundsTransferRecord;
import com.temenos.t24.api.records.standingorder.StandingOrderRecord;

/**
 * TODO: Document me!
 *
 * @author manethm
 *
 */

public class currDateStoBuildRoutine2 extends Enquiry {

    private DataAccess da = new DataAccess((T24Context)this);

    List<String> idList = new ArrayList<>();

    private String date = "''";

    private String dateOperand = "''";

    private String finalList = "";

    private int dateIndex;

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
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        log("Start Curr Date");
        setFilterCriteria(filterCriteria);
        if(this.date == "''" || this.dateOperand == "''"){
            return filterCriteria;
        }
        try {
            idList = this.da.selectRecords("", "FUNDS.TRANSFER", "$NAU", "");
            log(String.valueOf(idList.size()));
        }catch(Exception e){
            log(e.toString());
        }
        for (String id:idList){
            try{
                FundsTransferRecord fTRec = new FundsTransferRecord(this.da.getRecord("","FUNDS.TRANSFER", "$NAU", id));
                String inwardPay = fTRec.getInwardPayType().toString();
                String[] inwardStrList = new String[3];
                inwardStrList = inwardPay.split("-");
                log(inwardStrList[2] + "." + inwardStrList[3]);
                StandingOrderRecord StRec = new StandingOrderRecord(this.da.getRecord("STANDING.ORDER", inwardStrList[2] + "." + inwardStrList[3]));
                log(StRec.getCurrFreqDate().toString());
                if(!checkDate(StRec.getCurrFreqDate().toString())){
                    continue;
                }
                if(finalList.isEmpty()){
                    finalList.concat(id);
                }
                else{
                    finalList.concat(" " + id);
                }
            }catch(Exception e){
                log(e.toString());
                continue;
            }
        }
        try{
            FilterCriteria newCriteria = new FilterCriteria();
            newCriteria.setFieldname("@ID");
            newCriteria.setOperand("EQ");
            log(finalList);
            newCriteria.setValue(finalList);
            log(newCriteria.toString());
            filterCriteria.add(newCriteria);
            filterCriteria.remove(dateIndex);
        }catch(Exception e){
            log(e.toString());
        }
        return filterCriteria;
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


    private void setFilterCriteria(List<FilterCriteria> filtercriteria) {
        int i = 0;
        for (FilterCriteria fieldNames : filtercriteria) {
            i++;
            String FieldName = fieldNames.getFieldname();
            log(FieldName);
            if (FieldName.equals("PROCESSING.DATE") && !fieldNames.getValue().equals("''")){
                this.dateIndex  = i-1;
                this.date = fieldNames.getValue();
                this.dateOperand = fieldNames.getOperand();
            }
        }
    }
}

