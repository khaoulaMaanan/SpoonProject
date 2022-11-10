package spoon;

import spoon.reflect.CtModel;


public class SpoonParser extends GenericParser<Launcher> {
    private String projectPath;
    public SpoonParser(String projectPath) {
        super(projectPath);
        this.projectPath = projectPath;
    }

    @Override
    public void configure() {}


    @Override
    public String getProjectPath() {
        return this.projectPath;
    }

    public CtModel getModel() {
        parser = new Launcher(); // create launcher
        parser.addInputResource(getProjectPath());
        parser.buildModel();

        return  parser.getModel();// set comments enabled
    }

}

