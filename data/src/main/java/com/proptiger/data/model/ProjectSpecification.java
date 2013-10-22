/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;


/**
 *
 * @author mukand
 */
@Entity
@ResourceMetaInfo
@Table(name="RESI_PROJ_SPECIFICATION")
public class ProjectSpecification implements BaseModel{
    @FieldMetaInfo(displayName="PROJECT ID", description="PROJECT ID")
    @Column(name="PROJECT_ID")
    @Id
    private int id;
    
    @FieldMetaInfo(displayName="FLOORING MASTER BEDROOM", description="FLOORING MASTER BEDROOM")
    @Column(name="FLOORING_MASTER_BEDROOM")
    private String flooringMasterBedroom;
    
    @FieldMetaInfo(displayName="FLOORING OTHER BEDROOM", description="FLOORING OTHER BEDROOM")
    @Column(name="FLOORING_OTHER_BEDROOM")
    private String flooringOtherBedroom;
    
    @FieldMetaInfo(displayName="FLOORING LIVING DINING", description="FLOORING LIVING DINING")
    @Column(name="FLOORING_LIVING_DINING")
    private String flooringLivingDining;

    @FieldMetaInfo(displayName="FLOORING KITCHEN", description="FLOORING KITCHEN")
    @Column(name="FLOORING_KITCHEN")
    private String flooringKitchen;

    @FieldMetaInfo(displayName="FLOORING TOILETS", description="FLOORING TOILETS")
    @Column(name="FLOORING_TOILETS")
    private String flooringToilets;

    @FieldMetaInfo(displayName="FLOORING BALCONY", description="FLOORING BALCONY")
    @Column(name="FLOORING_BALCONY")
    private String flooringBalcony;

    @FieldMetaInfo(displayName="WALLS INTERIOR", description="WALLS INTERIOR")
    @Column(name="WALLS_INTERIOR")
    private String wallsInterior;

    @FieldMetaInfo(displayName="WALLS EXTERIOR", description="WALLS EXTERIOR")
    @Column(name="WALLS_EXTERIOR")
    private String wallsExterior;

    @FieldMetaInfo(displayName="WALLS KITCHEN", description="WALLS KITCHEN")
    @Column(name="WALLS_KITCHEN", nullable = true)
    private String wallsKitchen;

    @FieldMetaInfo(displayName="DOORS MAIN", description="DOORS MAIN")
    @Column(name="DOORS_MAIN")
    private String doorsMain;

    @FieldMetaInfo(displayName="DOORS INTERNAL", description="DOORS INTERNAL")
    @Column(name="DOORS_INTERNAL")
    private String doorsInternal;

    @FieldMetaInfo(displayName="WINDOWS", description="WINDOWS")
    @Column(name="WINDOWS")
    private String windows;

    @FieldMetaInfo(displayName="ELECTRICAL FITTINGS", description="ELECTRICAL FITTINGS")
    @Column(name="ELECTRICAL_FITTINGS")
    private String electricalFittings;

    @FieldMetaInfo(displayName="FITTINGS AND FIXTURES TOILETS", description="FITTINGS AND FIXTURES TOILETS")
    @Column(name="FITTINGS_AND_FIXTURES_TOILETS")
    private String fittingsAndFixturesToilets;

    @FieldMetaInfo(displayName="FITTINGS AND FIXTURES KITCHEN", description="FITTINGS AND FIXTURES KITCHEN")
    @Column(name="FITTINGS_AND_FIXTURES_KITCHEN")
    private String fittingsAndFixturesKitchen;

    @FieldMetaInfo(displayName="OTHERS", description="OTHERS")
    @Column(name="OTHERS", nullable = true)
    private String others;

    @FieldMetaInfo(displayName="WALLS TOILETS", description="WALLS TOILETS")
    @Column(name="WALLS_TOILETS", nullable = true)
    private String wallsToilets;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFlooringMasterBedroom() {
        return flooringMasterBedroom;
    }

    public void setFlooringMasterBedroom(String flooringMasterBedroom) {
        this.flooringMasterBedroom = flooringMasterBedroom;
    }

    public String getFlooringOtherBedroom() {
        return flooringOtherBedroom;
    }

    public void setFlooringOtherBedroom(String flooringOtherBedroom) {
        this.flooringOtherBedroom = flooringOtherBedroom;
    }

    public String getFlooringLivingDining() {
        return flooringLivingDining;
    }

    public void setFlooringLivingDining(String flooringLivingDining) {
        this.flooringLivingDining = flooringLivingDining;
    }

    public String getFlooringKitchen() {
        return flooringKitchen;
    }

    public void setFlooringKitchen(String flooringKitchen) {
        this.flooringKitchen = flooringKitchen;
    }

    public String getFlooringToilets() {
        return flooringToilets;
    }

    public void setFlooringToilets(String flooringToilets) {
        this.flooringToilets = flooringToilets;
    }

    public String getFlooringBalcony() {
        return flooringBalcony;
    }

    public void setFlooringBalcony(String flooringBalcony) {
        this.flooringBalcony = flooringBalcony;
    }

    public String getWallsInterior() {
        return wallsInterior;
    }

    public void setWallsInterior(String wallsInterior) {
        this.wallsInterior = wallsInterior;
    }

    public String getWallsExterior() {
        return wallsExterior;
    }

    public void setWallsExterior(String wallsExterior) {
        this.wallsExterior = wallsExterior;
    }

    public String getWallsKitchen() {
        return wallsKitchen;
    }

    public void setWallsKitchen(String wallsKitchen) {
        this.wallsKitchen = wallsKitchen;
    }

    public String getDoorsMain() {
        return doorsMain;
    }

    public void setDoorsMain(String doorsMain) {
        this.doorsMain = doorsMain;
    }

    public String getDoorsInternal() {
        return doorsInternal;
    }

    public void setDoorsInternal(String doorsInternal) {
        this.doorsInternal = doorsInternal;
    }

    public String getWindows() {
        return windows;
    }

    public void setWindows(String WINDOWS) {
        this.windows = WINDOWS;
    }

    public String getElectricalFittings() {
        return electricalFittings;
    }

    public void setElectricalFittings(String electricalFittings) {
        this.electricalFittings = electricalFittings;
    }

    public String getFittingsAndFixturesToilets() {
        return fittingsAndFixturesToilets;
    }

    public void setFittingsAndFixturesToilets(String fittingsAndFixturesToilets) {
        this.fittingsAndFixturesToilets = fittingsAndFixturesToilets;
    }

    public String getFittingsAndFixturesKitchen() {
        return fittingsAndFixturesKitchen;
    }

    public void setFittingsAndFixturesKitchen(String fittingsAndFixturesKitchen) {
        this.fittingsAndFixturesKitchen = fittingsAndFixturesKitchen;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String OTHERS) {
        this.others = OTHERS;
    }

    public String getWallsToilets() {
        return wallsToilets;
    }

    public void setWallsToilets(String wallsToilets) {
        this.wallsToilets = wallsToilets;
    }

}
