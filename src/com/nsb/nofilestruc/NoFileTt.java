package com.nsb.nofilestruc;

import java.util.ArrayList;
import java.util.List;

import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class NoFileTt extends Enquiry {

    List<String> RETURN_LIST = new ArrayList<>();

    List<String> PR_LIST = new ArrayList<>();

    String arrid;

    String certno;

    String cusid;

    String customerid;

    String accnumber;

    String Result = null;

    String process = null;

    String finallst1 = null;

    @Override
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {

        try {

            List<String> selarr = getAccId(filterCriteria);

            this.arrid = selarr.get(0);

            this.cusid = selarr.get(1);

            this.process = processarr("", "");

        } catch (Exception e) {
            e.getMessage();
        }

        return this.RETURN_LIST;

    }

    private String processarr(String value1, String PR_LIST) {

        try {

            PR_LIST = buildList("firstValue", "secondValue");

        } catch (Exception e) {
            e.getMessage();
        }

        return PR_LIST;
    }

    private List<String> getAccId(List<FilterCriteria> filtercriteria) {
        for (FilterCriteria fieldNames : filtercriteria) {
            String FieldName = fieldNames.getFieldname();
            if (FieldName.equals("AA.ID"))
                this.arrid = fieldNames.getValue();

            if (FieldName.equals("CC.ID"))
                this.customerid = fieldNames.getValue();
        }
        List<String> li = new ArrayList<>();
        li.add(this.arrid);

        li.add(this.customerid);

        return li;
    }

    private String buildList(String firstValue, String secondValue) {
        try {

            StringBuilder str = new StringBuilder();
            str.append(firstValue);
            str.append("*" + secondValue);

            String Result = str.toString();

            this.RETURN_LIST.add(Result);

        } catch (Exception e) {
            e.getMessage();
        }
        return this.Result;
    }

}
