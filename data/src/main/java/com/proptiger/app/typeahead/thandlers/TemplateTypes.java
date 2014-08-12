package com.proptiger.app.typeahead.thandlers;

public enum TemplateTypes {

    /* Caution : the enum types should not be renamed as they are used in CTR analysis */
    
    ProjectsIn("Property In", THandlerProjectIn.class), 
    UpcomingProjectsIn("Upcoming Property In", THandlerProjectIn.class), 
    NewProjectsIn("New Property In", THandlerProjectIn.class),
    PreLaunchProjectsIn("Pre Launch Property In", THandlerProjectIn.class), 
    UnderConstProjectsIn("Under Construction Property In", THandlerProjectIn.class), 
    ReadyToMoveProjectsIn("Ready to move Property In", THandlerProjectIn.class), 
    AffordableProjectsIn("Affordable Property In", THandlerProjectIn.class), 
    LuxuryProjectsIn("Luxury Property In", THandlerProjectIn.class), 
    TopProjectsIn("Top Property In", THandlerProjectIn.class), 
    
    PropertyBy("Property by", THandlerProjectsBy.class),
    PropertyForSaleIn("Property for sale in", THandlerPropertyFor.class),
    PropertyForResaleIn("Property for resale in", THandlerPropertyFor.class),
    
    PropertyUnder("Property Under", THandlerProjectBudgetArea.class),
    PropertyBelow("Property Below", THandlerProjectBudgetArea.class),
    PropertyAbove("Property Above", THandlerProjectBudgetArea.class),
    PropertyBetween("Property Between", THandlerProjectBudgetArea.class);
    
//    BHKUnder("BHK Under", THandlerBhkBudgetArea.class),
//    BHKBelow("BHK Below", THandlerBhkBudgetArea.class),
//    BHKAbove("BHK Above", THandlerBhkBudgetArea.class),
//    BHKBetween("BHK Between", THandlerBhkBudgetArea.class);
    
    private String                text;
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
