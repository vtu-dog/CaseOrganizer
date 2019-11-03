package app.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


class DateAdapter extends XmlAdapter <String, Date> {
    private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public Date unmarshal (String xml) throws Exception {
        return dateFormat.parse(xml);
    }

    @Override
    public String marshal (Date object) throws Exception {
        return dateFormat.format(object);
    }

}


@XmlRootElement(name = "case")
@XmlAccessorType(XmlAccessType.FIELD)
public class BasicCase {

    Boolean isActive = true;
    Boolean isPriority = false;
    Boolean isPending = false;
    Boolean isArchived = false;

    String company = "";
    String from = "";
    String concerning = "";
    String inspectionPeriod = "";
    String plannedEndDate = "";
    String caseNumber = "";
    String letterNumber = "";
    String correspondenceDescription = "";
    String remaining = "";
    String deliveryMethod = "";
    String plannedReplyDate = "";
    String deliveryConfirmation = "";
    String hearing = "";
    String receivedFrom = "";
    String comments = "";

    @XmlJavaTypeAdapter(DateAdapter.class)
    Date dateReceived = Calendar.getInstance().getTime();
    Date replyDeadline = Calendar.getInstance().getTime();

    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    List<String> links = new ArrayList<String>();


    public BasicCase () { }


    @Override
    public String toString () {
        return (this.letterNumber);
    }


    public void setIsActive (Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsActive () {
        return isActive;
    }


    public void setIsPriority (Boolean isPriority) {
        this.isPriority = isPriority;
    }

    public Boolean getIsPriority () {
        return isPriority;
    }


    public void setIsPending (Boolean isPending) {
        this.isPending = isPending;
    }

    public Boolean getIsPending () {
        return isPending;
    }


    public void setIsArchived (Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public Boolean getIsArchived () {
        return isArchived;
    }


    public void setCompany (String company) {
        this.company = company;
    }

    public String getCompany () {
        return company;
    }


    public void setFrom (String from) {
        this.from = from;
    }

    public String getFrom () {
        return from;
    }


    public void setConcerning (String concerning) {
        this.concerning = concerning;
    }

    public String getConcerning () {
        return concerning;
    }


    public void setInspectionPeriod (String inspectionPeriod) {
        this.inspectionPeriod = inspectionPeriod;
    }

    public String getInspectionPeriod () {
        return inspectionPeriod;
    }


    public void setPlannedEndDate (String plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public String getPlannedEndDate () {
        return plannedEndDate;
    }


    public void setCaseNumber (String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getCaseNumber () {
        return caseNumber;
    }


    public void setLetterNumber (String letterNumber) {
        this.letterNumber = letterNumber;
    }

    public String getLetterNumber () {
        return letterNumber;
    }


    public void setCorrespondenceDescription (String correspondenceDescription) {
        this.correspondenceDescription = correspondenceDescription;
    }

    public String getCorrespondenceDescription () {
        return correspondenceDescription;
    }


    public void setRemaining (String remaining) {
        this.remaining = remaining;
    }

    public String getRemaining () {
        return remaining;
    }


    public void setDeliveryMethod (String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getDeliveryMethod () {
        return deliveryMethod;
    }


    public void setPlannedReplyDate (String plannedReplyDate) {
        this.plannedReplyDate = plannedReplyDate;
    }

    public String getPlannedReplyDate () {
        return plannedReplyDate;
    }


    public void setDeliveryConfirmation (String deliveryConfirmation) {
        this.deliveryConfirmation = deliveryConfirmation;
    }

    public String getDeliveryConfirmation () {
        return deliveryConfirmation;
    }


    public void setHearing (String hearing) {
        this.hearing = hearing;
    }

    public String getHearing () {
        return hearing;
    }


    public void setReceivedFrom (String receivedFrom) {
        this.receivedFrom = receivedFrom;
    }

    public String getReceivedFrom () {
        return receivedFrom;
    }


    public void setComments (String comments) {
        this.comments = comments;
    }

    public String getComments () {
        return comments;
    }


    public void setDateReceived (Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public Date getDateReceived () {
        return dateReceived;
    }


    public void setReplyDeadline (Date replyDeadline) {
        this.replyDeadline = replyDeadline;
    }

    public Date getReplyDeadline () {
        return replyDeadline;
    }


    public void setLinks (List<String> links) {
        this.links = links;
    }

    public List<String> getLinks () {
        return links;
    }

}
