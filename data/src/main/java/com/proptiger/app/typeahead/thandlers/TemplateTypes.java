package com.proptiger.app.typeahead.thandlers;

public enum TemplateTypes {

    ProjectsIn("Projects In", THandlerProjectIn.class), 
    UpcomingProjectsIn("Upcoming Projects In", THandlerProjectIn.class), 
    NewProjectsIn("New Projects In", THandlerProjectIn.class),
    PreLaunchProjectsIn("Pre Launch Projects In", THandlerProjectIn.class), 
    UnderConstProjectsIn("Under Construction Projects In", THandlerProjectIn.class), 
    ReadyToMoveProjectsIn("Ready to move Projects In", THandlerProjectIn.class), 
    AffordableProjectsIn("Affordable Projects In", THandlerProjectIn.class), 
    LuxuryProjectsIn("Luxury Projects In", THandlerProjectIn.class), 
    TopProjectsIn("Top Projects In", THandlerProjectIn.class), 
    TopPropertiesIn("Top Properties In", THandlerProjectIn.class), 
    
    ProjectBy("Projects by", THandlerProjectsBy.class),
    PropertyBy("Property by", THandlerProjectsBy.class),
    PropertyForSaleIn("Property for sale in", THandlerPropertyFor.class),
    PropertyForResaleIn("Property for resale in", THandlerPropertyFor.class),
    
    ProjectsUnder("Projects Under", THandlerProjectBudgetArea.class),
    ProjectsBelow("Projects Below", THandlerProjectBudgetArea.class),
    ProjectsAbove("Projects Above", THandlerProjectBudgetArea.class),
    ProjectsBetween("Projects Between", THandlerProjectBudgetArea.class),

    PropertyUnder("Properties Under", THandlerProjectBudgetArea.class),
    PropertyBelow("Properties Below", THandlerProjectBudgetArea.class),
    PropertyAbove("Properties Above", THandlerProjectBudgetArea.class),
    PropertyBetween("Properties Between", THandlerProjectBudgetArea.class),
    
    BHKUnder("BHK Under", THandlerBhkBudgetArea.class),
    BHKBelow("BHK Below", THandlerBhkBudgetArea.class),
    BHKAbove("BHK Above", THandlerBhkBudgetArea.class),
    BHKBetween("BHK Between", THandlerBhkBudgetArea.class);

    private String                         text;
    Class<? extends RootTHandler> clazz;

    private TemplateTypes(String text, Class<? extends RootTHandler> clazz) {
        this.text = text;
        this.clazz = clazz;
    }

    public String getText() {
        return text;
    }

    public Class<? extends RootTHandler> getClazz() {
        return clazz;
    }

}
