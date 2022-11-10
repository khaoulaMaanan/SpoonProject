package spoon;

public abstract class GenericProcessor<T> {
	/* ATTRIBUTES */
	protected T parser;
	
	/* CONSTRUCTOR */
	public GenericProcessor(String projectPath) {
		setParser(projectPath);
	}

	
	public abstract void setParser(String projectPath);
}
