package org.ab.ast;

public class TopLevelClassObject extends ClassObject {
	private FileObject file;

	public TopLevelClassObject(String identifier) {
		super(identifier);
	}

	@Override
	public FileObject getFile() {
		return file;
	}
	
	public void setFile(FileObject file) {
		this.file = file;
	}
}
