package cop5556fa17;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.Parser.SyntaxException;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeCheckVisitor.SemanticException;

import static cop5556fa17.Scanner.Kind.*;

public class TypeCheckTest {

	// set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}
	
	
	/**
	 * Scans, parses, and type checks given input String.
	 * 
	 * Catches, prints, and then rethrows any exceptions that occur.
	 * 
	 * @param input
	 * @throws Exception
	 */
	void typeCheck(String input) throws Exception {
		show(input);
		try {
			Scanner scanner = new Scanner(input).scan();
			ASTNode ast = new Parser(scanner).parse();
			show(ast);
			ASTVisitor v = new TypeCheckVisitor();
			//thrown.expect(SemanticException.class);
			ast.visit(v, null);
		} catch (Exception e) {
			show(e);
			throw e;
		}
	}

	/**
	 * Simple test case with an almost empty program.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSmallest() throws Exception {
		String input = "n"; //Smallest legal program, only has a name
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
														// initialize it
		show(scanner); // Display the Scanner
		Parser parser = new Parser(scanner); // Create a parser
		ASTNode ast = parser.parse(); // Parse the program
		TypeCheckVisitor v = new TypeCheckVisitor();
		String name = (String) ast.visit(v, null);
		show("AST for program " + name);
		show(ast);
	}



	
	/**
	 * This test should pass with a fully implemented assignment
	 * @throws Exception
	 */
	 @Test
	 public void testDec1() throws Exception {
	 String input = "prog file abc=def;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testDec2() throws Exception {
	 String input = "prog int k	= 2;";
	 typeCheck(input);
	 }
	 
	 /**
	  * This program does not declare k. The TypeCheckVisitor should
	  * throw a SemanticException in a fully implemented assignment.
	  * @throws Exception
	  */
	 @Test
	 public void testUndec() throws Exception {
	 String input = "prog k = 42;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testUndec1() throws Exception {
	 String input = "Prog url ident = \"asdfadf\";";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testUndec2() throws Exception {
	 String input = "prog boolean k=true; int k = 5;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testUndec3() throws Exception {
	 String input = "prog int k=1; int b=2; int c = k+b;";
	 //thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testUndec4() throws Exception {
	 String input = "prog int k=1; int b=2; boolean c = k+b;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec5() throws Exception {
	 String input = "prog image image1; image image2; image1 <- image2;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec6() throws Exception {
	 String input = "prog  image image1; image image2; image1 <- @1;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec7() throws Exception {
	 String input = "prog file imagefile  = \"imageFile2017.\"; image image1;  image1 <- imagefile;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec8() throws Exception {
	 String input = "prog file aa=\"http://example.com/\";";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec9() throws Exception {
	 String input = "raktima url ab=\"world.docx\";";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec10() throws Exception {
	 String input = "abc int def;";
	 //thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec11() throws Exception {
	 String input = "abc";
	 //thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec12() throws Exception {
	 String input = "prog int k=2; k=++++k;";
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec13() throws Exception {
	 String input = "raktima int k=+r;";
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec14() throws Exception {
	 String input = "raktima int k=+r;";
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec15() throws Exception {
	 String input = "raktima int k=sin(2);";
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec16() throws Exception {
	 String input = "raktima int k=2;k[[x,y]]=2;";
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec17() throws Exception {
	 String input = "raktima int k=2;k[[r,A]]=2;";
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec18() throws Exception {
	 String input = "raktima b[[x,y]]=x;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec19() throws Exception {
	 String input = "raktima int b=2;b[[x,y]]=x;";
	 //thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec20() throws Exception {
	 String input = "raktima url b=\"http://www.google.com\";";
	 //thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec21() throws Exception {
	 String input = "raktima url b=\"http://www.google.com\";";
	 //thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec22() throws Exception {
	 String input = "raktima int b=2; int c=4; c=b==c;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec23() throws Exception {
	 String input = "prog int k=k+1;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec24() throws Exception {
	 String input = "prog url b=c;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec25() throws Exception {
	 String input = "prog url b=\"https://www.google.com\"; "
	 		         + "url c=b;";
	 //thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec26() throws Exception {
	 String input = "prog int filepng=2;int png=3; image[filepng,png] imageName <- imagepng;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec27() throws Exception {
	 String input = "prog image b <- @2;";
	 //thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void testUndec28() throws Exception {
	 String input = "prog int b=2; b[[x,y]]=+-!3;";
	 //thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void test12() throws Exception {
	 String input = "prog int x_y=12; image [10,11] abcd <- x_y;"; 
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void test13() throws Exception {
	 String input = "prog int x_y=12; x_y -> b;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 @Test
	 public void test1() throws Exception {
	 String input = "prog int k = 42; int k=12;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test2() throws Exception {
	 String input = "prog int k = 42;\n boolean k=true;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test3() throws Exception {
	 String input = "prog file k = 42;\n boolean k=true;";
	 thrown.expect(SyntaxException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test4() throws Exception {
	 String input = "prog file k = 42;\n boolean k=true;";
	 thrown.expect(SyntaxException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test5() throws Exception {
	 String input = "prog image[filepng,png] imageName <- imagepng; \n boolean ab=true;"; 
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test6() throws Exception {
	 String input = "prog image[filepng,png] imageName; \n boolean ab=true;"; 
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test7() throws Exception {
	 String input = "prog int abcd=(true&true);"; 
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test8() throws Exception {
	 String input = "prog boolean abcd=(true&true|false&1);"; 
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test9() throws Exception {
	 String input = "prog image x;"; 
	 thrown.expect(SyntaxException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test10() throws Exception {
	 String input = "prog image [10,11] abcd <- \"\";"; 
	 //thrown.expect(SyntaxException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test11() throws Exception {
	 String input = "prog image [10,11] abcd <- @(1234+234);"; 
	 typeCheck(input);
	 }
	 
	 // SourceSinkDeclaration
	 @Test
	 public void test30() throws Exception {
	 String input = "prog url imageurl=\"https://www.google.com\";"; 
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test14() throws Exception {
	 String input = "prog url imageurl1=\"https://www.google.com\"; url imageurl=imageurl1;"; 
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test15() throws Exception {
	 String input = "prog url imageurl=@(1234+3454);";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 //Statements for Image Out and Image in
	 @Test
	 public void test16() throws Exception {
	 String input = "prog image imageName;image imageName1 <- \"https://www.google.com\";"+ 
			        "imageName -> SCREEN;";
	 //thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test17() throws Exception {
	 String input = "prog file fileName=\"filepng\"; image image1<- fileName;";
	 typeCheck(input);
	 }
	 
	 //LHS Assignment Statement
	 @Test
	 public void test18() throws Exception {
	 String input = "prog image imageName; int array; array[[x,y]]=imageName[5,6];";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test19() throws Exception {
	 String input = "prog image imageName;array[[x,y]]=imageName[5,6];";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test20() throws Exception {
	 String input = "prog image imageName;array[[x,y]]=imageName[5,6];";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test21() throws Exception {
	 String input = "prog image imageName;int array;array[[r,A]]=imageName[5,6];";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test22() throws Exception {
	 String input = "prog int value1=10; int value2 =20; int sinValue=abs(sin(value1)); int cosValue=abs(cos(value2));";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test23() throws Exception {
	 String input = "prog int value1=10; int value2 =20; int sinValue; int cosValue; sinValue=abs(sin(atan(cos(value1))));"
	 		        + " cosValue=cart_x[value1*10,value2*20];";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test24() throws Exception {
	 String input = "prog int x_value=10; int y_value =20; int sinValue; int cosValue; "
	 			    + " sinValue=polar_a[x_value*1/2+12+13,y_value*1/3*1/2*3/4%2];"
	 		        + " cosValue=cart_y[x_value/2*2+2,(x_value>y_value)?sinValue/234:sinValue*10-23];"
	 		        + " int cotValue=polar_r[sinValue/cosValue,sinValue*100/400+cosValue];";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test25() throws Exception {
	 String input = "";
	 thrown.expect(SyntaxException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test26() throws Exception {
		 String input = "prog boolean k = false;\n k=k;";
		 typeCheck(input);
     }
	 
	 @Test
	 public void test27() throws Exception {
	 String input = "prog int k=(5*5+12+3-5+4+false);";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void test28() throws Exception {
	 String input = "prog int k=((5+6/0+1/2+2%3));";
	 typeCheck(input);
	 }
	 @Test
	 public void test29() throws Exception {
	 String input = "prog int k=((5+6/0+1/2+2%3));";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void gitangtestUndec() throws Exception {
	 String input = "prog k = 42;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void gitangdec() throws Exception {
	 String input = "prog int k;";
	 typeCheck(input);
	 }
	 	
	 
	 @Test
	 public void gitangtestStatementAssign() throws Exception {
	 String input = "prog int gitang = 2+2;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void gitangtestStatementAssignwitherror() throws Exception {
	 String input = "prog gitang = 2+2;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 
	 @Test
	 public void gitangtestStatementAssign1() throws Exception {
	 String input = "prog boolean gitang = 2>3;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void gitangtestStatementAssign3() throws Exception {
	 String input = "prog boolean gitang = true|false;";
	 typeCheck(input);
	 }
	 	 
	 @Test
	 public void gitangtestStatementAssign4() throws Exception {
	 String input = "prog boolean gitang = 10+2!=12;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void gitangtestStatementAssign5() throws Exception {
	 String input = "prog boolean gitang = true;"
	 		+ "\n"
	 		+ "gitang = 10>12;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void gitangtestStatementAssign6() throws Exception {
	 String input = "prog boolean gitang = true;"
	 		+ "\n"
	 		+ "gitang[[x,y]] = 10>12;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void gitangtestStatementAssign7() throws Exception {
	 String input = "prog boolean gitang = true;"
	 		+ "\n"
	 		+ "gitang[[x,y]] = 10>12;";
	 typeCheck(input);
	 }
	 
	 
	 @Test
	 public void gitangtestStatementIn() throws Exception {
	 String input = "prog file gitang = \"true\";"
	 		+ "\n"
	 		+ "gitang <- \"PLPAssignment\";";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void gitangtestStatementOut1() throws Exception {
	 String input = "prog file gitang = \"true\";"
	 		+ "\n"
	 		+ "gitang -> SCREEN;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void gitangtestStatementOut2() throws Exception {
	 String input = "prog file gitang = \"true\";"
	 		+ "\n"
			+ "int k;"
	 		+ "gitang -> k ;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	
	 @Test
	 public void gitangtestStatementOut3() throws Exception {
	 String input = "prog file gitang = @ 4+5;"
	 		+ "\n"
			+ " file k = \"pokemon\";"
	 		+ "gitang -> k;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 
	 @Test
	 public void gitangtestStatementIn4() throws Exception {
	 String input = "prog file gitang = \"true\";"
	 		+ "\n"
			+ "file k =@ 2+2 ;"
	 		+ "gitang -> k;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 //***** gagan Test Cases ****
	 
	 @Test
	 public void testDecGSC11() throws Exception {
			String input="prog int c=3; image d; image b<- @ d [3+x,4+c];";
		//	thrown.expect(SemanticException.class);
			typeCheck(input);
		}
	 
	 @Test
	 public void testDecGSC1() throws Exception {
		String input="prog int b=5; int c=10; int d=!b+c;";
		typeCheck(input);
	}
	@Test
	 public void testDecGSC2() throws Exception {
		String input="prog int b=5; int c=10; int d=-c+b;";
		typeCheck(input);
	}
	@Test
	 public void testDecGSC3() throws Exception {
		String input="prog int b=5; int c=10; boolean d=-b+c;";
		thrown.expect(SemanticException.class);
		typeCheck(input);
	}

	
	@Test
	 public void testDecGSC4() throws Exception {
		String input="prog boolean b=true; boolean c=false; boolean d=!b==c;";
		typeCheck(input);
	}
	@Test
	 public void testDecGSC5() throws Exception {
		String input="prog boolean b=true; boolean c=false; boolean d=+b==c;";
		thrown.expect(SemanticException.class);
		typeCheck(input);
	}

	//	Expression_PredefinedName ::=  predefNameKind
	//			Expression_PredefinedName.TYPE <= INTEGER
	@Test
	 public void testDecGSC6() throws Exception {
		String input="prog int b=KW_a;";
		thrown.expect(SemanticException.class);
		typeCheck(input);
	}
	@Test
	 public void testDecGSC8() throws Exception {
		String input="prog int b=3+KW_a;";
		thrown.expect(SemanticException.class);
		typeCheck(input);
	}
	 
	
	@Test
	 public void testDecGSC9() throws Exception {
		String input="prog int c=3; int b=c [3+x,4+c];";
		thrown.expect(SemanticException.class);
		typeCheck(input);
	}
	@Test
	 public void testDecGSC10() throws Exception {
		String input="prog int c=3; image d; int b=d [3+x,4+c];";
		//thrown.expect(SemanticException.class);
		typeCheck(input);
	}
	
	//********Adhiraj Test Cases ********
	
	@Test
	 public void testDec03() throws Exception {
		 String input = "adhiraj int b = 5; int d = 8; int v = 11; int val = 3*b/d-v;";
		 //thrown.expect(SemanticException.class);
		 typeCheck(input); 
	 }
	
	@Test
	 public void testAd03() throws Exception {
	 String input = "adhiraj file ui = \"rest\"; image [!Z != 4 ? true : 4,2] k <- ui;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testAd04() throws Exception {
	 String input = "adhiraj file ui = \"rest\"; image [!Z != 4 ? true : false,2] k <- ui;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testAd05() throws Exception {
	 String input = "adhiraj file ui = \"rest\"; image [!Z != 4 ? 2 : 4,false] k <- ui;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testAd06() throws Exception {
	 String input = "adhiraj file ui = \"rest\"; image [4+2 ? 2 : 4,false] k <- ui;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	
	
	@Test
	 public void testAd01() throws Exception {
	 String input = "adhiraj file ui = \"rest\"; image [4 != 4 ? 3 : 4,2] k <- u;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testAd02() throws Exception {
	 String input = "adhiraj file ui = \"rest\"; image [!Z != 4 ? 3 : 4,2] k <- ui;";
	 typeCheck(input);
	 }
	
	@Test
	 public void testDec02() throws Exception {
		 String input = "adhiraj int val = 9+1-true;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input); 
	 }
	
	@Test
	 public void testDec01() throws Exception {
		 String input = "adhiraj int val = 9+1+0-9-0+2;";
		 typeCheck(input); 
	 }
	
}
