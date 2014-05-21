package com.proptiger.data.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.enums.portfolio.PaymentPlan;
import com.proptiger.data.model.portfolio.PortfolioListingPaymentPlan;

/**
 * Model class for project payment schedule
 * 
 * @author Rajeev Pandey
 * 
 */
@Entity
@Table(name = "PROJ_PAYMENT_SCHEDULE")
public class ProjectPaymentSchedule extends BaseModel {

    private static final long serialVersionUID = -5749828536177848914L;

    @Id
    @Column(name = "PAYMENT_SCHEDULE_ID")
    @JsonIgnore
    private Integer           paymentScheduleId;

    @Column(name = "PROJECT_ID")
    private Integer           projectId;

    @Column(name = "INSTALLMENT_NO")
    private int               installmentNumber;

    @Column(name = "INSTALLMENT_NAME")
    private String            installmentName;

    @Column(name = "PAYMENT_PLAN")
    @Enumerated(EnumType.STRING)
    private PaymentPlan       paymentPlan;

    @Column(name = "COMPONENT_NAME")
    private String            componentName;

    @Column(name = "COMPONENT_TYPE")
    private int               componentType;

    @Column(name = "COMPONENT_VALUE")
    private double            componentValue;

    public Integer getPaymentScheduleId() {
        return paymentScheduleId;
    }

    public void setPaymentScheduleId(Integer paymentScheduleId) {
        this.paymentScheduleId = paymentScheduleId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public int getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(int installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public String getInstallmentName() {
        return installmentName;
    }

    public void setInstallmentName(String installmentName) {
        this.installmentName = installmentName;
    }

    public PaymentPlan getPaymentPlan() {
        return paymentPlan;
    }

    public void setPaymentPlan(PaymentPlan paymentPlan) {
        this.paymentPlan = paymentPlan;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public int getComponentType() {
        return componentType;
    }

    public void setComponentType(int componentType) {
        this.componentType = componentType;
    }

    public double getComponentValue() {
        return componentValue;
    }

    public void setComponentValue(double componentValue) {
        this.componentValue = componentValue;
    }

    public static Set<PortfolioListingPaymentPlan> convertToPortfolioListingPaymentPlan(
            List<ProjectPaymentSchedule> paymentScheduleList) {
        Set<PortfolioListingPaymentPlan> list = new LinkedHashSet<PortfolioListingPaymentPlan>();
        for (ProjectPaymentSchedule paymentSchedule : paymentScheduleList) {
            PortfolioListingPaymentPlan listingPaymentPlan = new PortfolioListingPaymentPlan();
            listingPaymentPlan.setAmount(0.0D);
            listingPaymentPlan.setComponentName(paymentSchedule.getComponentName());
            listingPaymentPlan.setComponentValue(paymentSchedule.getComponentValue());
            listingPaymentPlan.setDueDate(null);
            listingPaymentPlan.setInstallmentName(paymentSchedule.getInstallmentName());
            listingPaymentPlan.setInstallmentNumber(paymentSchedule.getInstallmentNumber());
            listingPaymentPlan.setPaymentDate(null);
            listingPaymentPlan.setPaymentPlan(paymentSchedule.getPaymentPlan());
            listingPaymentPlan.setPaymentSource(null);
            listingPaymentPlan.setStatus(null);
            list.add(listingPaymentPlan);
        }
        return list;
    }

}
