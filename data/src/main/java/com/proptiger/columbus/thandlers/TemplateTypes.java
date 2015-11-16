package com.proptiger.columbus.thandlers;

public enum TemplateTypes {

    /*
     * Caution : the enum types should not be renamed as they are used in CTR
     * analysis
     */

    ProjectsIn("Property In", THandlerProjectIn.class), UpcomingProjectsIn("Upcoming Property In",
            THandlerProjectIn.class), NewProjectsIn("New Property In", THandlerProjectIn.class), UnderConstProjectsIn(
            "Under Construction Property In", THandlerProjectIn.class), ReadyToMoveProjectsIn(
            "Ready to move Property In", THandlerProjectIn.class), AffordableProjectsIn("Affordable Property In",
            THandlerProjectIn.class), LuxuryProjectsIn("Luxury Property In", THandlerProjectIn.class), PropertyForSaleIn(
            "Property for sale in", THandlerProjectIn.class), PropertyForResaleIn("Property for resale in",
            THandlerProjectIn.class),

    PropertyBy("Property by", THandlerProjectsBy.class),

    PropertyUnder("Property Under", THandlerProjectBudgetArea.class), PropertyBelow("Property Below",
            THandlerProjectBudgetArea.class), PropertyAbove("Property Above", THandlerProjectBudgetArea.class), PropertyBetween(
            "Property Between", THandlerProjectBudgetArea.class);

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
