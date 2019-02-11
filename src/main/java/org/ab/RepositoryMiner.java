package org.ab;

import org.ab.ast.ClassObject;
import org.ab.ast.FileObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;

public class RepositoryMiner {
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new IllegalArgumentException("Illegal Arguments.");
		}
		
		mine(args[0], args[1]);
	}
	
	private static void mine(String repoFolder, String sha) {
		SystemObject system = new SystemObject(repoFolder, new String[]{"v4"});
		printClassesAndMethods(system);
	}
	
	private static void printClassesAndMethods(SystemObject system) {
		for (FileObject f: system.getFiles()) {
			for (ClassObject c: f.getClasses()) {
				System.out.println(f.getPackageName() + '.' + c.getName());
				for (MethodObject m: c.getMethods()) {
					System.out.println(m.getName());
				}
				System.out.println();
			}
		}
	}
}
