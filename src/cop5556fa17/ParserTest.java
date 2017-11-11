package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.AST.*;

import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class ParserTest {

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
	 * Simple test case with an empty program. This test expects an exception
	 * because all legal programs must have at least an identifier
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */

@Test
public void testcaseFailed1() throws SyntaxException, LexicalException {
	String input = "p[polar_r[x,y], p2[+x , -y]]"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}


@Test
public void testcaseFailed2() throws SyntaxException, LexicalException {
	String input = "p image1 = Z;"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}

@Test
public void testcaseFailed3() throws SyntaxException, LexicalException {
	String input = "b+c+d+-e-+f+!g"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}

@Test
public void testcaseFailed4() throws SyntaxException, LexicalException {
	String input = "cart_y[a,r]"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}

@Test
public void testcaseFailed5() throws SyntaxException, LexicalException {
	String input = "polar_a[x,y]"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}

@Test
public void testcaseFailed6() throws SyntaxException, LexicalException {
	String input = "polar_a[x,y]"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}

@Test
public void testcaseFailed7() throws SyntaxException, LexicalException {
	String input = "cos(3)"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}

@Test
public void testcaseFailed8() throws SyntaxException, LexicalException {
	String input = "atan(3)"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}

@Test
public void testcaseFailed9() throws SyntaxException, LexicalException {
	String input = "abs(-3)"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}

@Test
public void testcaseFailed10() throws SyntaxException, LexicalException {
	String input = "!x"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}

@Test
public void testcaseFailed11() throws SyntaxException, LexicalException {
	String input = "(X)"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}

@Test
public void testcaseFailed12() throws SyntaxException, LexicalException {
	String input = "cart_x[a,r]"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}

@Test
public void testcaseFailed13() throws SyntaxException, LexicalException {
	String input = "polar_r[x,y]"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}

@Test
public void testcaseFailed14() throws SyntaxException, LexicalException {
	String input = "polar_r((5)|true)"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}


@Test
public void testcaseFailed15() throws SyntaxException, LexicalException {
	String input = "++--x?x?R:a:abs[A,R]"; 
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);
	//Program ast=parser.program();
	Expression ast = parser.expression();
	show(ast);
}
@Test 
public void program_variableDeclaration_valid2() throws SyntaxException, LexicalException {
String input = "identifier boolean bool = sin[+1,1230]&x&!!!!!false | 123*0/true%(!y);";
show(input);
Scanner scanner = new Scanner(input).scan();
show(scanner);
Parser parser = new Parser(scanner);
Program ast = parser.parse();
show(ast);
assertEquals("identifier", ast.name);
Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements.get(0);
assertEquals(KW_boolean, dec.type.kind);
assertEquals("bool", dec.name);
Expression_Binary exp_binary = (Expression_Binary) dec.e;
assertEquals(OP_OR, exp_binary.op);
assertEquals(KW_sin, exp_binary.firstToken.kind);
Expression_Binary exp_binary1 = (Expression_Binary) exp_binary.e0;
assertEquals(OP_AND, exp_binary1.op);
assertEquals(KW_sin, exp_binary1.firstToken.kind);
Expression_Binary exp_binary10 = (Expression_Binary) exp_binary1.e0;
assertEquals(OP_AND, exp_binary10.op);
Expression_FunctionAppWithIndexArg exp_func_index = (Expression_FunctionAppWithIndexArg) exp_binary10.e0;
Expression_PredefinedName exp_pre_name = (Expression_PredefinedName) exp_binary10.e1;
assertEquals(KW_x, exp_pre_name.firstToken.kind);
Index arg = exp_func_index.arg;
Expression_Unary index_exp_unary = (Expression_Unary) arg.e0;
Expression_IntLit index_exp_intlit = (Expression_IntLit) arg.e1;
assertEquals(1230, index_exp_intlit.value);
assertEquals(OP_PLUS, index_exp_unary.op);
Expression_IntLit index_exp_unary_exp_intlit = (Expression_IntLit) index_exp_unary.e;
assertEquals(1, index_exp_unary_exp_intlit.value);
Expression_Unary exp_unary = (Expression_Unary) exp_binary1.e1;
Expression_Binary exp_binary2 = (Expression_Binary) exp_binary.e1;
Expression_Unary x = (Expression_Unary) exp_binary2.e1;
assertEquals(KW_y, x.e.firstToken.kind);	
}

@Test
public void testExpRanAbc() throws LexicalException, SyntaxException {
	String input = "x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y & x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y & x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y & x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	Expression expAst = parser.expression();
	show(expAst);
	assertEquals(expAst.toString(),
			"Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]], op=OP_NEQ, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]]], op=OP_AND, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]], op=OP_NEQ, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]]]], op=OP_AND, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]], op=OP_NEQ, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]]]], op=OP_AND, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]], op=OP_NEQ, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]]]]");
}
@Test
public void testNameOnlyExp() throws LexicalException, SyntaxException {
	String input = "prog k [[x,y]]";  //Legal program with only a name
	show(input);            //display input
	Scanner scanner = new Scanner(input).scan();   //Create scanner and create token list
	show(scanner);    //display the tokens
	Parser parser = new Parser(scanner);   //create parser
	Program ast = parser.parse();          //parse program and get AST
	show(ast);                             //Display the AST
//	assertEquals(ast.name, "prog");        //Check the name field in the Program object
//	assertTrue(ast.decsAndStatements.isEmpty());   //Check the decsAndStatements list in the Program object.  It should be empty.
}


}
