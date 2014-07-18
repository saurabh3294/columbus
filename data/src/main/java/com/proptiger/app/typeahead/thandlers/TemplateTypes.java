package com.proptiger.app.typeahead.thandlers;

public enum TemplateTypes {

    ProjectsIn("projects in", THandlerProjectIn.class), 
    UpcomingProjectsIn("upcoming projects in", THandlerProjectIn.class), 
    NewProjectsIn("new projects in", THandlerProjectIn.class),
    PreLaunchProjectsIn("pre launch projects in", THandlerProjectIn.class), 
    UnderConstProjectsIn("under construction projects in", THandlerProjectIn.class), 
    ReadyToMoveProjectsIn("ready to move projects in", THandlerProjectIn.class), 
    AffordableProjectsIn("affordable projects in", THandlerProjectIn.class), 
    LuxuryProjectsIn("luxury projects in", THandlerProjectIn.class), 
    TopProjectsIn("top rojects in", THandlerProjectIn.class), 
    TopPropertiesIn("top projects in", THandlerProjectIn.class), 
    
    ProjectBy("projects by", THandlerProjectsBy.class),
    PropertyBy("property by", THandlerProjectsBy.class),
    PropertyForSaleIn("property for sale in", THandlerPropertyFor.class),
    PropertyForResaleIn("property for resale in", THandlerPropertyFor.class);
    
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
