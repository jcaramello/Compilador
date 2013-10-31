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
		
		CodeGenerator.Gen(Instructions.LOAD, "2");
		CodeGenerator.Gen(Instructions.LOAD, "3");		
		CodeGenerator.Gen(Instructions.ADD);
		CodeGenerator.Gen(Instructions.VTLabel, "Sarasa");
		
		
		CodeGenerator.close();
	}

}
