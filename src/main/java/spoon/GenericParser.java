package spoon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericParser<T> {
	/* ATTRIBUTES */
	protected String projectPath;
	protected String projectSrcPath;
	protected String projectBinPath;
	protected T parser;
	
	/* CONSTRUCTOR */
	public GenericParser(String projectPath) {
		setProjectPaths(projectPath);
	}
	
	/* METHODS */
	public String getProjectPath() {
		return projectPath;
	}
	
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	private void setProjectSrcPath(String projectSrcPath) {
		this.projectSrcPath = projectSrcPath;
	}

	private void setProjectBinPath(String projectBinPath) {
		this.projectBinPath = projectBinPath;
	}
	
	private void setProjectPaths(String projectPath) {
		setProjectPath(projectPath);
		setProjectSrcPath(projectPath+File.separator+"src"+File.separator);
		setProjectBinPath(projectPath+File.separator+"bin"+File.separator);
	}
	
	public List<File> listJavaFiles(String filePath){
		File folder = new File(filePath);
		List<File> javaFiles = new ArrayList<>();
		String fileName = "";
		
		for (File file: folder.listFiles()) {
			fileName = file.getName();
			
			if (file.isDirectory())
				javaFiles.addAll(listJavaFiles(file.getAbsolutePath()));
			else if (fileName.endsWith(".java"))
				javaFiles.add(file);
		}
		
		return javaFiles;
	}

	public abstract void configure();
}
