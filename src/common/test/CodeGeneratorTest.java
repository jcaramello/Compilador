package common.test;

import static org.junit.Assert.*;

import org.junit.Test;

import common.Application;
import common.CodeGenerator;
import common.Instructions;

public class CodeGeneratorTest {

	@Test
	public void test() {
		Application.Name = "CodeGeneratorTest";
		
		CodeGenerator.gen(Instructions.LOAD, "2");
		CodeGenerator.gen(Instructions.LOAD, "3");		
		CodeGenerator.gen(Instructions.ADD);
		CodeGenerator.gen(Instructions.VTLabel, "Sarasa");
		
		
		CodeGenerator.close();
	}

}
