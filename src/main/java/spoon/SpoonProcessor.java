package spoon;


public class SpoonProcessor extends GenericProcessor<SpoonParser>  {

    public SpoonProcessor( String projectPath) {
        super( projectPath);
    }
    public void setParser(String projectPath) {
        parser = new SpoonParser(projectPath);
    }

    public void setParser(SpoonParser parser) {
        this.parser = parser;
    }

}
