package cop5556fa17;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

import cop5556fa17.CodeGenUtils.DynamicClassLoader;
import cop5556fa17.AST.Program;

public class CodeGenVisitorTest implements ImageResources {

	@Rule
	public Timeout globalTimeout = Timeout.millis(12000);

	static boolean doPrint = true;
	static boolean doCreateFile = false;

	static void show(Object s) {
		if (doPrint) {
			System.out.println(s);
		}
	}

	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	private boolean devel = false;
	private boolean grade = true;

	public static final int Z = 0xFFFFFF;

	/**
	 * Generates bytecode for given input. Throws exceptions for Lexical,
	 * Syntax, and Type checking errors
	 * 
	 * @param input
	 *            String containing source code
	 * @return Generated bytecode
	 * @throws Exception
	 */
	byte[] genCode(String input) throws Exception {

		// scan, parse, and type check
		Scanner scanner = new Scanner(input);
		show(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		show(program);

		// generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		// output the generated bytecode
		show(CodeGenUtils.bytecodeToString(bytecode));

		// write byte code to file
		if (doCreateFile) {
			String name = ((Program) program).name;
			String classFileName = "bin/" + name + ".class";
			OutputStream output = new FileOutputStream(classFileName);
			output.write(bytecode);
			output.close();
			System.out.println("wrote classfile to " + classFileName);
		}

		// return generated classfile as byte array
		return bytecode;
	}

	/**
	 * Run main method in given class
	 * 
	 * @param className
	 * @param bytecode
	 * @param commandLineArgs
	 *            String array containing command line arguments, empty array if
	 *            none
	 * @throws Exception
	 */
	void runCode(String className, byte[] bytecode, String[] commandLineArgs) throws Exception {
		RuntimeLog.initLog(); // initialize log used for grading.
		DynamicClassLoader loader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
		Class<?> testClass = loader.define(className, bytecode);
		Class[] argTypes = { commandLineArgs.getClass() };
		Method m = testClass.getMethod("main", argTypes);
		show("Output from " + m + ":"); // print name of method to be executed
		Object passedArgs[] = { commandLineArgs }; // create array containing
													// params, in this case a
													// single array.
		m.invoke(null, passedArgs);
	}

	/**
	 * Delays for 5 seconds. May be useful during development to delay closing
	 * frames displaying images
	 */
	void sleepFor5() throws Exception {
		Thread.sleep(5000);
	}

	/**
	 * Blocks program until a key is pressed to the console. May be useful
	 * during development to delay closing frames displaying images
	 */
	void waitForKey() throws IOException {
		System.out.println("enter any char to exit");
		int b = System.in.read();
	}

	/**
	 * Used in most test cases. Change once here to change behavior in all
	 * tests.
	 * 
	 * @throws Exception
	 */
	void keepFrame() throws Exception {
		sleepFor5();
	}

	
	

	@Test
	public void failedSemantic2() throws Exception {
		String prog = "image7";
		String input = prog
				+ "//args: <inputImageURL> <outputImageURL>\nimage[1024,1024] g; \n\nimage[1024,1024] h; \ng <- @ 0;\n file f = @ 1; \ng -> SCREEN;\nh[[r,a]] =  g[r,a];h -> SCREEN; \nh -> f;";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1, imageFile2 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println("**************");
		System.out.println(RuntimeLog.getGlobalString());
		System.out.println("Z=" + 0xFFFFFF);
	}

	@Test
	public void failedSemantic3() throws Exception {
		String prog = "imageIO1";
		String input = prog
				+ "//args: <inputImageFullPath> <outputImageFullPath>\n image g; \n file f = @ 1; \ng <- @ 0;\ng -> SCREEN;\ng -> f;\nimage h;\nh <- f; \nh -> SCREEN;";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1, imageFile2 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failed4() throws Exception {
		String prog = "image10";
		String input = prog
				+ "//args: <imageURL>\nimage[1024,1024] g; \n\nimage[1024,1024] h; \ng <- @ 0;\ng -> SCREEN;\nh[[x,y]] =  g[x,Y-y];h -> SCREEN; \n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1, imageFile2 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println("******************");
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedAdhiraj5() throws Exception {
		String prog = "imageIO2";
		String input = prog
				+ "//args: <imageURL>\n image g; \n file f = \"newImage.jpg\"; \ng <- @ 0;\ng -> SCREEN;\ng -> f;\nimage h;\nh <- f;\nh -> SCREEN;";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti1() throws Exception {
		String prog = "imageTestXY";
		String input = prog
				+ "\nimage[512,500] g;\ng[[x,y]] = Z;\nint foo = X;\nint bar = Y;\nfoo -> SCREEN;bar -> SCREEN;";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti2() throws Exception {
		String prog = "imageGenMagenta";
		String input = prog + "\nimage[512,256] g; \ng[[x,y]] = 255 + 16711680;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti3() throws Exception {
		String prog = "image1";
		String input = prog + "//args: <imageURL>\nimage g; \ng <- @ 0;\ng -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti4() throws Exception {
		String prog = "image2";
		String input = prog + "//args: <imageURL>\nimage[128,128] g; \ng <- @ 0;\ng -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti5() throws Exception {
		String prog = "image3";
		String input = prog + "//args: <imageURL>\nimage[128,128] g; \ng <- @ 0;\ng -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti6() throws Exception {
		String prog = "image4";
		String input = prog
				+ "//args: <imageURL>\nimage[1024,1024] g; \n\nimage[1024,1024] h; \ng <- @ 0;\ng -> SCREEN;\nh[[x,y]] = g[x,y];h -> SCREEN; \n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti7() throws Exception {
		String prog = "image5";
		String input = prog
				+ "//args: <imageURL>\nimage[1024,1024] g; \n\nimage[1024,1024] h; \ng <- @ 0;\ng -> SCREEN;\nh[[x,y]] = ! g[x,y];h -> SCREEN; \n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti8() throws Exception {
		String prog = "image6";
		String input = prog
				+ "//args: <imageURL>\nimage[1024,1024] g; \n\nimage[1024,1024] h; \ng <- @ 0;\ng -> SCREEN;\nh[[x,y]] = ! g[x,y];h -> SCREEN; \n\nimage[1024,1024] average; \naverage[[x,y]] = h[x,y]*3;average -> SCREEN; \n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}
	
	@Test
	public void failedSrishti9() throws Exception {
		String prog = "image7";
		String input = prog
				+ "//args: <inputImageURL> <outputImageURL>\nimage[1024,1024] g; \n\nimage[1024,1024] h; \ng <- @ 0;\n file f = @ 1; \ng -> SCREEN;\nh[[r,a]] = g[r,a];h -> SCREEN; \nh -> f;";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1,imageFile2 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}
	
	@Test
	public void failedSrishti10() throws Exception {
		String prog = "image8";
		String input = prog
				+ "//args: <imageURL>\nimage[1024,1024] g; \n\nimage[1024,1024] h; \ng <- @ 0;\ng -> SCREEN;\nh[[x,y]] = Z-g[x,y];h -> SCREEN; \n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti11() throws Exception {
		String prog = "image9";
		String input = prog
				+ "//args: <imageURL>\nimage[1024,1024] g; \n\nimage[1024,1024] h; \ng <- @ 0;\ng -> SCREEN;\nh[[x,y]] = (g[x,y] > Z/2) ? Z-g[x,y] : g[x,y];h -> SCREEN; \n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti12() throws Exception {
		String prog = "imageGenBlack";
		String input = prog + "\nimage[500,274] g; \ng[[x,y]] = 256 + 65280 + 16711680;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti13() throws Exception {
		String prog = "imageGenGreen";
		String input = prog + "\nimage g; \ng[[x,y]] = 65280;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti14() throws Exception {
		String prog = "imageGenWhite";
		String input = prog
				+ " int w = 256; \nint h = 512;\n\nimage[w,h] g; \ng[[x,y]] = 255 + 65280 + 16711680;\ng -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti15() throws Exception {
		String prog = "imageCopy";
		String input = prog
				+ "//args: <imageURL>\nimage[1024,1024] g; \n\nimage[1024,1024] h; \ng <- @ 0;\ng -> SCREEN;\nh[[x,y]] = g[x,y];\nh -> SCREEN; \n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti16() throws Exception {
		String prog = "imageGen1";
		String input = prog + "\nimage[512,512] g; \ng[[x,y]] = 150;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti17() throws Exception {
		String prog = "imageGen2";
		String input = prog + "\nimage[512,512] g; \ng[[x,y]] = x;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti18() throws Exception {
		String prog = "imageGen3";
		String input = prog + "\nimage[1024,512] g; \ng[[x,y]] = x*y;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti19() throws Exception {
		String prog = "imageGen4";
		String input = prog + "\nimage[1024,1024] g; \ng[[r,a]] = r;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti20() throws Exception {
		String prog = "imageGen5";
		String input = prog + "\nimage[1024,1024] g; \ng[[r,a]] = r * 100;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti21() throws Exception {
		String prog = "imageGen6";
		String input = prog + "\nimage[512,512] g; \ng[[x,y]] = (x%7>1)?(y%7>1)? 0 : Z : Z;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti22() throws Exception {
		String prog = "imageGen7";
		String input = prog + "\nimage[512,512] g; \ng[[x,y]] = (x%20>1)?(y%20>1)? 0 : Z : Z;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti23() throws Exception {
		String prog = "imageGen8";
		String input = prog
				+ "\nimage[512,512] g; \ng[[x,y]] = (x%20>1)?(y%20>1)? 16711680 : x/2+ 65280 : x/2+ 65280;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti24() throws Exception {
		String prog = "imageGenZ";
		String input = prog + " int w = 256; \nint h = 512;\n\nimage[w,h] g; \ng[[x,y]] = Z;\ng -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti25() throws Exception {
		String prog = "imageIO1";
		String input = prog
				+ "//args: <inputImageFullPath> <outputImageFullPath>\n image g; \n file f = @ 1; \ng <- @ 0;\ng -> SCREEN;\ng -> f;\nimage h;\nh <- f; \nh -> SCREEN;";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1, imageFile2 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti26() throws Exception {
		String prog = "imageIO2";
		String input = prog
				+ "//args: <imageURL>\n image g; \n file f = \"newImage.jpg\"; \ng <- @ 0;\ng -> SCREEN;\ng -> f;\nimage h;\nh <- f;\nh -> SCREEN;";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti27() throws Exception {
		String prog = "imageMod";
		String input = prog
				+ "//args: <imageURL>\n int width = 256; \nint height = 256;\n\nimage[width,height] g; \nimage[width,height] h;\nint k = 255+65280; k -> SCREEN ;g[[x,y]] = k;g -> SCREEN;\nh[[x,y]] = g[x,y] & 255; \n h -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti28() throws Exception {
		String prog = "imageGenYellow";
		String input = prog + "\nimage[512,256] g; \ng[[x,y]] = 65280 + 16711680;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti29() throws Exception {
		String prog = "imageGenBlue";
		String input = prog + "\nimage[512,256] g; \ng[[x,y]] = 255;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti30() throws Exception {
		String prog = "imageGenCyan";
		String input = prog + "\nimage[512,256] g; \ng[[x,y]] = 255 + 65280;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti31() throws Exception {
		String prog = "imageGenRed";
		String input = prog + "\nimage[512,512] g; \ng[[x,y]] = 16711680;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti32() throws Exception {
		String prog = "image10";
		String input = prog
				+ "//args: <imageURL>\nimage[1024,1024] g; \n\nimage[1024,1024] h; \ng <- @ 0;\ng -> SCREEN;\nh[[x,y]] = g[x,Y-y];h -> SCREEN; \n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti33() throws Exception {
		String prog = "image11";
		String input = prog
				+ "//args:<iamgeURL>\n int width = 4; \nint height = 4;\n\nimage[width,height] g; \nimage[width,height] h;\nint k = 255+65280; k -> SCREEN ; int kk = k % 255; \n kk -> SCREEN ;g[[x,y]] = kk;g -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}

	@Test
	public void failedSrishti34() throws Exception {
		String prog = "imagePreDef";
		String input = prog + "//args: <imageURL>\nimage g; \ng[[r,a]] = cart_x[r,a]; \ng -> SCREEN;\n";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = { imageFile1 };
		runCode(prog, bytecode, commandLineArgs);
		System.out.println(RuntimeLog.getGlobalString());
	}


}