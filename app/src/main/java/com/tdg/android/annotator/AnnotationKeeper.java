package com.tdg.android.annotator;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AnnotationKeeper {

    private String LOG = "AnnotationKeeper";
    private ArrayList<DateAndAnnotation> listOfAnnotations;
    private String ID_RATER, ID_SUBJECT, ADDITIONAL_DATA, FREITEXT;
    private SimpleDateFormat DATE_FORMAT;
    private String TIME_START, TIME_END;
    private String KEY_END_FULL = ";;;;;;;";
    private String[] categories = new String[8];
    private boolean isUebung = false;
    private AnnotationStatistics annotationStatistics;
    private int[] histogram = new int[8];
    private Context context;

    AnnotationKeeper(Context ctx) {
        context = ctx;
        annotationStatistics = new AnnotationStatistics();
        reset();
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT);
        categories = context.getResources().getStringArray(R.array.categories);
    }

    void setRaterID(String string) {
        ID_RATER = string;
    }

    void setSubjectID(String string) {
        ID_SUBJECT = string;
    }

    void setAdditionalData(String string) {
        ADDITIONAL_DATA = string;
    }

    void setFreiText(String string) {
        FREITEXT = string;
    }

    void setTimeStart() {
        TIME_START = generateTimeNowUTC();
    }

    void setTimeEnd() {
        TIME_END = generateTimeNowUTC();
    }

    void setUebung(boolean uebung) {
        isUebung = uebung;
    }

    private String uebungOderMessung() {
        if (isUebung) {
            return context.getString(R.string.button_Ubeung);
        } else {
            return context.getString(R.string.button_Messung);
        }
    }

    private String getHistogram() {
        histogram = annotationStatistics.getHist(listOfAnnotations);
        return "" + histogram[0] + "; " + histogram[1] + "; " + histogram[2] + "; "
                + histogram[3] + "; " + histogram[4] + "; " + histogram[5] + "; " +
                histogram[6] + "; " + histogram[7];
    }

    public String getRaterID() {
        return ID_RATER;
    }

    public String getSubjectID() {
        return ID_SUBJECT;
    }

    public ArrayList<DateAndAnnotation> getListOfAnnotations() {
        return listOfAnnotations;
    }

    void addAnnotation(int code) {
        listOfAnnotations.add(new DateAndAnnotation(generateTimeNowUTC(), code));
    }

    void removeLastAnnotation() {
        if (getNumberOfAnnotations() > 0) {
            listOfAnnotations.remove(getNumberOfAnnotations() - 1);
        }
    }

    String flushResults() {
        String streamOfAnnotations = "";

        streamOfAnnotations += context.getString(R.string.result_ID_Rater) + ": ;";
        streamOfAnnotations += ID_RATER;
        streamOfAnnotations += KEY_END_FULL;
        streamOfAnnotations += "\n";
        streamOfAnnotations += context.getString(R.string.result_ID_Proband) + ": ;";
        streamOfAnnotations += ID_SUBJECT;
        streamOfAnnotations += KEY_END_FULL;
        streamOfAnnotations += "\n";
        streamOfAnnotations += context.getString(R.string.result_Situation) + ": ;";
        streamOfAnnotations += uebungOderMessung();
        streamOfAnnotations += KEY_END_FULL;
        streamOfAnnotations += "\n";
        streamOfAnnotations += context.getString(R.string.result_Gesamtanzahl) + ": ;";
        streamOfAnnotations += "" + getNumberOfAnnotations();
        streamOfAnnotations += KEY_END_FULL;
        streamOfAnnotations += "\n";
        streamOfAnnotations += context.getString(R.string.result_Haeufigkeit) + ": ;";
        streamOfAnnotations += getHistogram();
        streamOfAnnotations += "\n";
        streamOfAnnotations += context.getString(R.string.result_Zusatzinformationen) + ": ;";
        streamOfAnnotations += ADDITIONAL_DATA;
        streamOfAnnotations += ";;;;";
        streamOfAnnotations += "\n";
        streamOfAnnotations += context.getString(R.string.result_freiText) + ": ;";
        streamOfAnnotations += FREITEXT;
        streamOfAnnotations += KEY_END_FULL;
        streamOfAnnotations += "\n";
        streamOfAnnotations += context.getString(R.string.result_Zeit_Start) + ": ;";
        streamOfAnnotations += TIME_START.replace('T','_');
        streamOfAnnotations += KEY_END_FULL;
        streamOfAnnotations += "\n";
        streamOfAnnotations += context.getString(R.string.result_Zeit_Ende) + ": ;";
        streamOfAnnotations += TIME_END.replace('T','_');
        streamOfAnnotations += KEY_END_FULL;
        streamOfAnnotations += "\n";
        streamOfAnnotations += "-----------------------------";
        streamOfAnnotations += KEY_END_FULL + ";";
        streamOfAnnotations += "\n";

        for (int iAnnotation = 0; iAnnotation < listOfAnnotations.size(); iAnnotation++) {
            streamOfAnnotations += ""+(iAnnotation+1)+", ";
            streamOfAnnotations += listOfAnnotations.get(iAnnotation).date.replace('T','_');
            streamOfAnnotations += ", " + listOfAnnotations.get(iAnnotation).annotation;
            streamOfAnnotations += ", " + categories[listOfAnnotations.get(iAnnotation).annotation-1];
            streamOfAnnotations += KEY_END_FULL + ";";
            streamOfAnnotations += "\n";
        }

        return streamOfAnnotations;
    }

    String getFileName() {
        return generateTimeNowUTC() + "_" + ID_RATER + "_" + ID_SUBJECT + ".csv";
    }

    int getNumberOfAnnotations() {
        return listOfAnnotations.size();
    }

    void reset() {
        listOfAnnotations = new ArrayList<>();
        annotationStatistics.reset();
        setSubjectID("");
        setRaterID("");
        setAdditionalData("");
        setFreiText("");
        isUebung = false;
    }

    private String generateTimeNowUTC() {
        Calendar dateTime = Calendar.getInstance();
        return DATE_FORMAT.format(dateTime.getTime());
    }
}
