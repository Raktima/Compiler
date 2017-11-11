/**
 * /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
 */

package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;

import static cop5556fa17.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(scanner.new Token(kind, pos, length, line, pos_in_line), t);
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token check(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}

	/**
	 * Simple test case with a (legal) empty program
	 *   
	 * @throws LexicalException
	 */
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, as we will want to do 
	 * later, the end of line character would be inserted by the text editor.
	 * Showing the input will let you check your input is what you think it is.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void testSemi() throws LexicalException {

		String input = ";;\n;;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testSemi1() throws LexicalException {

		
		String input = "abc";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		//checkNext(scanner, KW_a, 0, 1, 1, 1);
		//checkNext(scanner, OP_ASSIGN, 1, 1, 1, 2);
		checkNext(scanner, IDENTIFIER, 0, 3, 1, 1);
		checkNextIsEOF(scanner);
	}

	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it a String literal
	 * that is missing the closing ".  
	 * 
	 * Note that the outer pair of quotation marks delineate the String literal
	 * in this test program that provides the input to our Scanner.  The quotation
	 * mark that is actually included in the input must be escaped, \".
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */


	@Test
	public void testSemi3() throws LexicalException {

		
		String input = "a==b";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_a, 0, 1, 1, 1);
		checkNext(scanner, OP_EQ, 1, 2, 1, 2);
		checkNext(scanner, IDENTIFIER, 3, 1, 1, 4);
		checkNextIsEOF(scanner);
	}

	@Test     
    public void testSemi5() throws LexicalException {         
        String input = "z@file";         
        Scanner scanner = new Scanner(input).scan();         
        show(input);         
        show(scanner);         
        checkNext(scanner,IDENTIFIER, 0,1, 1, 1);                
        checkNext(scanner,OP_AT, 1,1, 1, 2);
        checkNext(scanner,KW_file, 2,4, 1, 3);    
        checkNextIsEOF(scanner);     
        }

	@Test
    public void testSemi8() throws LexicalException {
        String input = "ABC\r\nabc";
        Scanner scanner = new Scanner(input).scan();
        show(input);
        show(scanner);
        checkNext(scanner,IDENTIFIER, 0,3, 1, 1);
        checkNext(scanner,IDENTIFIER, 5,3, 2, 1);
        checkNextIsEOF(scanner);
    }

	@Test        
    public void testSemi10() throws LexicalException {            
        String input = "  ==abc<=\n=><--> A !==! ==";            
        Scanner scanner = new Scanner(input).scan();            
        show(input);            
        show(scanner);
        checkNext(scanner,OP_EQ,2,2,1,3);
        checkNext(scanner,IDENTIFIER,4,3,1,5);            
        checkNext(scanner,OP_LE,7,2,1,8);            
        checkNext(scanner,OP_ASSIGN,10,1,2,1);     
        checkNext(scanner,OP_GT,11,1,2,2);
        checkNext(scanner,OP_LARROW,12,2,2,3);     
        checkNext(scanner,OP_RARROW,14,2,2,5);
        checkNext(scanner,KW_A,17,1,2,8);     
        checkNext(scanner,OP_NEQ,19,2,2,10);
        checkNext(scanner,OP_ASSIGN,21,1,2,12);
        checkNext(scanner,OP_EXCL,22,1,2,13);
        checkNext(scanner,OP_EQ,24,2,2,15);
        checkNextIsEOF(scanner);        
        }

    
    @Test
    public void testSemi12() throws LexicalException{
        String input="//abc";
        show(input);
        Scanner scanner= new Scanner(input).scan();
        show(scanner);
        checkNextIsEOF(scanner);
    }

    @Test
    public void testSemi14() throws LexicalException{
        String input= "hot\r\ndog";
        show(input);
        Scanner scanner= new Scanner(input).scan();
        show(scanner);
        checkNext(scanner,IDENTIFIER,0,3,1,1);
        checkNext(scanner,IDENTIFIER,5,3,2,1);
        checkNextIsEOF(scanner);
    }

    @Test
    public void testSemi16() throws LexicalException{
        String input= "&true@";
        show(input);
        Scanner scanner= new Scanner(input).scan();
        show(scanner);
        checkNext(scanner,OP_AND,0,1,1,1);
        checkNext(scanner,BOOLEAN_LITERAL,1,4,1,2);
        checkNext(scanner,OP_AT,5,1,1,6);
        checkNextIsEOF(scanner);
    }

    @Test
    public void testSemi18() throws LexicalException{
        String input= "****";
        show(input);
        Scanner scanner= new Scanner(input).scan();
        show(scanner);
        checkNext(scanner,OP_POWER,0,2,1,1);
        checkNext(scanner,OP_POWER,2,2,1,3);
        checkNextIsEOF(scanner);
    }

    @Test
    public void testSemi20() throws LexicalException{
        String input= "catx";
        show(input);
        Scanner scanner= new Scanner(input).scan();
        show(scanner);
        checkNext(scanner,IDENTIFIER,0,4,1,1);
        checkNextIsEOF(scanner);
    }

    @Test
    public void testSemi22() throws LexicalException{
        String input= "01";
        show(input);
        Scanner scanner= new Scanner(input).scan();
        show(scanner);
        checkNext(scanner,INTEGER_LITERAL,0,1,1,1);
        checkNext(scanner,INTEGER_LITERAL,1,1,1,2);
        checkNextIsEOF(scanner);
    }

    @Test
    public void testSemi24() throws LexicalException {
        String input = "//abc\n abc";
        Scanner scanner = new Scanner(input).scan();
        show(input);
        show(scanner);
        checkNext(scanner, IDENTIFIER, 7, 3, 2, 2);
        checkNextIsEOF(scanner);
    }

    @Test
    public void testSemi26() throws LexicalException {
        String input = "abc123";
        Scanner scanner = new Scanner(input).scan();
        show(input);
        show(scanner);
        checkNext(scanner, IDENTIFIER, 0, 6, 1, 1);
        checkNextIsEOF(scanner);
    }

    @Test
    public void testSemi28() throws LexicalException{
        String input= "01\n _true";
        show(input);
        Scanner scanner= new Scanner(input).scan();
        show(scanner);
        checkNext(scanner,INTEGER_LITERAL,0,1,1,1);
        checkNext(scanner,INTEGER_LITERAL,1,1,1,2);
        checkNext(scanner, IDENTIFIER, 4, 5, 2, 2);
        checkNextIsEOF(scanner);
    }

    @Test
    public void testSemi30() throws LexicalException{
        String input= "ABC\"\\n\"";
        show(input);
        Scanner scanner= new Scanner(input).scan();
        show(scanner);
        checkNext(scanner, IDENTIFIER, 0, 3, 1, 1);
        checkNext(scanner,STRING_LITERAL,3,4,1,4);
        checkNextIsEOF(scanner);
       
    }

    @Test
    public void testSemi32() throws LexicalException{
        String input= "\"a\"";
        show(input);
        Scanner scanner= new Scanner(input).scan();
        show(scanner);
        checkNext(scanner,STRING_LITERAL,0,3,1,1);
        checkNextIsEOF(scanner);
       
    }

    
    @Test
    public void testKeywordWhiteSpace() throws LexicalException {
        String input = "DEF_X\rimage ";  //The input is  DEF_X .  This is legal
        show(input);        //Display the input 
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        checkNext(scanner,KW_DEF_X,0,5,1,1);
        checkNext(scanner,KW_image,6,5,2,1);
        checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
    }

    
    @Test
    public void testComment() throws LexicalException {
        String input = "//abc\rabc";  //The input is  " " .  This is legal
        show(input);        //Display the input 
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        checkNext(scanner,IDENTIFIER,6,3,2,1);
        checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
    }

    
    @Test
    public void testIDKeyword() throws LexicalException {
        String input = "aafile";  //The input is  DEF_X .  This is legal
        show(input);        //Display the input 
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        checkNext(scanner,IDENTIFIER,0,6,1,1);
        checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
    }

    
    @Test
    public void testIDKeywords() throws LexicalException {
        String input = "\"aa\"";  //The input is  DEF_X .  This is legal
        show(input);        //Display the input 
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        checkNext(scanner,STRING_LITERAL,0,4,1,1);
        checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
    }
    
    @Test
    public void testID() throws LexicalException {
        String input = "abc\r\ndef";
        show(input);
        Scanner sc = new Scanner(input).scan();
        show(sc);
        checkNext(sc, IDENTIFIER,0,3,1,1);
        checkNext(sc, IDENTIFIER,5,3,2,1);
        checkNextIsEOF(sc);  //Check that the only token is the EOF token.
    }

   
    @Test
    public void StringLiteraln() throws LexicalException {
        String input = "\"abc\"\"";
        //System.out.println("hi "+input.charAt(0));
        show(input);
        thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
        try {
            new Scanner(input).scan();
        } catch (LexicalException e) {  //
            show(e);
            assertEquals(6,e.getPos());
            throw e;
        }
    }
        @Test
        public void testSemi34() throws LexicalException{
            String input= "\n";
            show(input);
            Scanner scanner= new Scanner(input).scan();
            show(scanner);
            checkNextIsEOF(scanner);
        }

        @Test
        public void testSemi36() throws LexicalException{
            String input= "\"\n\"";
            show(input);
            thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
            try {
                new Scanner(input).scan();
            } catch (LexicalException e) {  //
                show(e);
                assertEquals(1,e.getPos());
                throw e;
            }
        }

        @Test
        public void testSemi37() throws LexicalException{
            String input= "abcd 1234";
            show(input);
            Scanner scanner= new Scanner(input).scan();
            show(scanner);
            checkNext(scanner,IDENTIFIER,0,4,1,1);
            checkNext(scanner,INTEGER_LITERAL,5,4,1,6);
            checkNextIsEOF(scanner);
            }

        @Test
        public void testSemi39() throws LexicalException{
            String input= "\";;;\t\"";
            show(input);
            Scanner scanner= new Scanner(input).scan();
            show(scanner);
            checkNext(scanner,STRING_LITERAL,0,6,1,1);
            checkNextIsEOF(scanner);
            }
 
        @Test
        public void testSemi41() throws LexicalException{
            String input= "\"\\t\"";
            show(input);
            Scanner scanner= new Scanner(input).scan();
            show(scanner);
            checkNext(scanner,STRING_LITERAL,0,4,1,1);
            checkNextIsEOF(scanner);
            }

        @Test
        public void testSemi43() throws LexicalException {
            String input="hello\bworld";
                show(input);
                thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
                try {
                    Scanner scanner = new Scanner(input).scan();
                } catch (LexicalException e) { 
                    show(e);
                    assertEquals(5,e.getPos());
                    throw e;
                }
        }


        @Test
        public void testSemi47() throws LexicalException {
            String input="\"ab\\\"def" ;
                show(input);
                thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
                try {
                    Scanner scanner = new Scanner(input).scan();
                } catch (LexicalException e) { 
                    show(e);
                    assertEquals(8,e.getPos());
                    throw e;
                }
        }

        
@Test
public void testSemi49() throws LexicalException{
	String input = "b\"a\\n\""; 
    show(input);
    Scanner scanner= new Scanner(input).scan();
    show(scanner);
    checkNext(scanner, IDENTIFIER, 0, 1, 1, 1);
    checkNext(scanner, STRING_LITERAL, 1, 5, 1, 2);  
    checkNextIsEOF(scanner);
    }

@Test
public void testSemi51() throws LexicalException{
	String input = "\"b\bab\""; 
    show(input);
    Scanner scanner= new Scanner(input).scan();
    show(scanner);
    checkNext(scanner, STRING_LITERAL, 0, 6, 1, 1);  
    checkNextIsEOF(scanner);
}

@Test
public void testSemi53() throws LexicalException{
	String input = "a\nab"; 
    show(input);
    Scanner scanner= new Scanner(input).scan();
    show(scanner);
    checkNext(scanner, KW_a, 0, 1, 1, 1);
    checkNext(scanner, IDENTIFIER, 2, 2, 2, 1);
    checkNextIsEOF(scanner);
}


/**
 * Test illustrating how to put a new line in the input program and how to check
 * content of tokens.
 * 
 * Because we are using a Java String literal for input, we use \n for the end
 * of line character. (We should also be able to handle \n, \r, and \r\n
 * properly.)
 * 
 * Note that if we were reading the input from a file, as we will want to do
 * later, the end of line character would be inserted by the text editor.
 * Showing the input will let you check your input is what you think it is.
 * 
 * @throws LexicalException
 */

@Test
public void testOperators() throws LexicalException {
	String input = ">0 =";
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	// (Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int
	// pos_in_line)
	checkNext(scanner, OP_GT, 0, 1, 1, 1);
	checkNext(scanner, INTEGER_LITERAL, 1, 1, 1, 2);
	checkNext(scanner, OP_ASSIGN, 3, 1, 1, 4);
	checkNextIsEOF(scanner);
}



@Test
public void testWhiteSpaces1() throws LexicalException {
	String input = ";\r\n01230000";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	checkNext(scanner, SEMI, 0, 1, 1, 1);
	checkNext(scanner, INTEGER_LITERAL, 3, 1, 2, 1);
	checkNext(scanner, INTEGER_LITERAL, 4, 7, 2, 2);
	checkNextIsEOF(scanner);
}



@Test
public void testStrings2() throws LexicalException {
	String input = "DEF_X $$";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	checkNext(scanner, KW_DEF_X, 0, 5, 1, 1);
	checkNext(scanner, IDENTIFIER, 6, 2, 1, 7);
	checkNextIsEOF(scanner);
}


@Test
public void testStringLiterals1() throws LexicalException {
	String input = "\"abc def \n\"";
	show(input);
	Scanner scanner = null;
	try {
		scanner = new Scanner(input).scan();
		show(scanner);
	} catch (LexicalException ex) {
		// checkNext(scanner, STRING_LITERAL, 0,9,1,1);
		// checkNextIsEOF(scanner);
	}
}



@Test
public void testLineTerminator2() throws LexicalException {

	String input = "\r\nabc";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	checkNext(scanner, IDENTIFIER, 2, 3, 2, 1);
	checkNextIsEOF(scanner);

}



@Test
public void testValidIntLiterals() throws LexicalException {

	String input = "010";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
	checkNext(scanner, INTEGER_LITERAL, 1, 2, 1, 2);
	checkNextIsEOF(scanner);

}


@Test
public void testInValidInteger() throws LexicalException {

	String input = "99999999999999999999999999999999999999";
	show(input);
	thrown.expect(LexicalException.class);
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) { //
		show(e);
		throw e;
	}
}



@Test
public void testInValidStringLiteral1() throws LexicalException {

	String input = "(\"\n\")";
	thrown.expect(LexicalException.class);
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) {
		show(e);
		assertEquals(2, e.getPos());
		throw e;
	}
}



@Test
public void testValidStringLiteral2() throws LexicalException {

	String input = "[;\"\\\\\"]"; // [ ; " \ \ " ]
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	checkNext(scanner, LSQUARE, 0, 1, 1, 1);
	checkNext(scanner, SEMI, 1, 1, 1, 2);
	checkNext(scanner, STRING_LITERAL, 2, 4, 1, 3);
	checkNext(scanner, RSQUARE, 6, 1, 1, 7);
	checkNextIsEOF(scanner);

}



@Test
public void testValidStringLiteral3() throws LexicalException {

	String input = "[;\"\\']\";"; // [ ; " \ ' ] " ;
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	checkNext(scanner, LSQUARE, 0, 1, 1, 1);
	checkNext(scanner, SEMI, 1, 1, 1, 2);
	checkNext(scanner, STRING_LITERAL, 2, 5, 1, 3);
	checkNext(scanner, SEMI, 7, 1, 1, 8);
	checkNext(scanner, EOF, 8, 0, 1, 9);
}



// identifier

@Test
public void testIdentifier1() throws LexicalException {

	String input = "xx";
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	checkNext(scanner, IDENTIFIER, 0, 2, 1, 1);
	checkNextIsEOF(scanner);
}


@Test
public void testEmpty3() throws LexicalException {
	String input = "//fgfh"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}



@Test
public void testZeroandDigit() throws LexicalException {
	String input = "012304560"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
	checkNext(scanner, INTEGER_LITERAL, 1, 8, 1, 2);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}


@Test
public void testcommentSpace2() throws LexicalException {
	String input = "//abc\r\n1234567 1234567 //X\n 01234";
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, INTEGER_LITERAL, 7, 7, 2, 1);
	checkNext(scanner, INTEGER_LITERAL, 15, 7, 2, 9);
	checkNext(scanner, INTEGER_LITERAL, 28, 1, 3, 2);
	checkNext(scanner, INTEGER_LITERAL, 29, 4, 3, 3);
	checkNextIsEOF(scanner);
}



@Test
public void testSemiKW() throws LexicalException {
	String input = ";;SCREEN;;";
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, SEMI, 0, 1, 1, 1);
	checkNext(scanner, SEMI, 1, 1, 1, 2);
	checkNext(scanner, KW_SCREEN, 2, 6, 1, 3);
	checkNext(scanner, SEMI, 8, 1, 1, 9);
	checkNext(scanner, SEMI, 9, 1, 1, 10);
	checkNextIsEOF(scanner);
}



@Test
public void testcommentStart() throws LexicalException {
	String input = "//abc\r\nSCREEN";
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, KW_SCREEN, 7, 6, 2, 1);

	checkNextIsEOF(scanner);
}



@Test
public void testcommentMidOPr() throws LexicalException {
	String input = "SCREEN//abc\r\n/43//\"'\"\r_4";
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, KW_SCREEN, 0, 6, 1, 1);
	checkNext(scanner, OP_DIV, 13, 1, 2, 1);
	checkNext(scanner, INTEGER_LITERAL, 14, 2, 2, 2);
	checkNext(scanner, IDENTIFIER, 22, 2, 3, 1);
	checkNextIsEOF(scanner);
}


@Test
public void testSemiKWInLit() throws LexicalException {
	String input = "143;0;SCREEN(true;_true,_$a";
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, INTEGER_LITERAL, 0, 3, 1, 1);
	checkNext(scanner, SEMI, 3, 1, 1, 4);
	checkNext(scanner, INTEGER_LITERAL, 4, 1, 1, 5);
	checkNext(scanner, SEMI, 5, 1, 1, 6);
	checkNext(scanner, KW_SCREEN, 6, 6, 1, 7);
	checkNext(scanner, LPAREN, 12, 1, 1, 13);
	checkNext(scanner, BOOLEAN_LITERAL, 13, 4, 1, 14);
	checkNext(scanner, SEMI, 17, 1, 1, 18);
	checkNext(scanner, IDENTIFIER, 18, 5, 1, 19);
	checkNext(scanner, COMMA, 23, 1, 1, 24);
	checkNext(scanner, IDENTIFIER, 24, 3, 1, 25);
	checkNextIsEOF(scanner);
}



@Test
public void testKW() throws LexicalException {
	String input = "a"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, KW_a, 0, 1, 1, 1);
	// checkNext(scanner,INTEGER_LITERAL,1,8,1,2);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}



@Test
public void testKW2() throws LexicalException {
	String input = "SCREEn"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, IDENTIFIER, 0, 6, 1, 1);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}


@Test
public void testIntLtrIdr2() throws LexicalException {
	String input = "1230truea"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, INTEGER_LITERAL, 0, 4, 1, 1);
	// checkNext(scanner,INTEGER_LITERAL,1,3,1,2);
	checkNext(scanner, IDENTIFIER, 4, 5, 1, 5);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}



@Test
public void tesSpr() throws LexicalException {
	String input = ";"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, SEMI, 0, 1, 1, 1);
	// checkNext(scanner,INTEGER_LITERAL,1,3,1,2);
	// checkNext(scanner,IDENTIFIER,4,5,1,5);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}


@Test
public void tesOpr2() throws LexicalException {
	String input = "<-"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, OP_LARROW, 0, 2, 1, 1);
	// checkNext(scanner,INTEGER_LITERAL,1,3,1,2);
	// checkNext(scanner,IDENTIFIER,4,5,1,5);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}



@Test
public void failEscapeCharacter() throws LexicalException {
	String input = "b^a";
	show(input);
	thrown.expect(LexicalException.class); // Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) { //
		show(e);
		assertEquals(1, e.getPos());
		throw e;
	}
}



public void tesStrLit() throws LexicalException {
	String input = "\"bn\n\""; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, STRING_LITERAL, 0, 5, 1, 1);
	// checkNext(scanner,INTEGER_LITERAL,1,3,1,2);
	// checkNext(scanner,IDENTIFIER,4,5,1,5);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}



@Test
public void testComm() throws LexicalException {
	String input = "//ddcdcd\\nf\nf"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, IDENTIFIER, 12, 1, 2, 1);
	checkNextIsEOF(scanner);
}



@Test
public void testValidStringLiteral7() throws LexicalException {
	String input = "x=\"\n\";";
	show(input);
	thrown.expect(LexicalException.class); // Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) { //
		show(e);
		assertEquals(3, e.getPos());
		throw e;
	}
}



@Test
public void testZeroandDigit1() throws LexicalException {
	String input = "012304560"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
	checkNext(scanner, INTEGER_LITERAL, 1, 8, 1, 2);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}


@Test
public void testcommentSpace21() throws LexicalException {
	String input = "//abc\r\n1234567 1234567 //X\n 01234";
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, INTEGER_LITERAL, 7, 7, 2, 1);
	checkNext(scanner, INTEGER_LITERAL, 15, 7, 2, 9);
	checkNext(scanner, INTEGER_LITERAL, 28, 1, 3, 2);
	checkNext(scanner, INTEGER_LITERAL, 29, 4, 3, 3);
	checkNextIsEOF(scanner);
}





@Test
public void testSemiKW1() throws LexicalException {
	String input = ";;SCREEN;;";
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, SEMI, 0, 1, 1, 1);
	checkNext(scanner, SEMI, 1, 1, 1, 2);
	checkNext(scanner, KW_SCREEN, 2, 6, 1, 3);
	checkNext(scanner, SEMI, 8, 1, 1, 9);
	checkNext(scanner, SEMI, 9, 1, 1, 10);
	checkNextIsEOF(scanner);
}



@Test
public void testcommentStart1() throws LexicalException {
	String input = "//abc\r\nSCREEN";
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, KW_SCREEN, 7, 6, 2, 1);
	checkNextIsEOF(scanner);
}


@Test
public void testcommentMidOPr1() throws LexicalException {
	String input = "SCREEN//abc\r\n/43//\"'\"\r_4";
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, KW_SCREEN, 0, 6, 1, 1);
	checkNext(scanner, OP_DIV, 13, 1, 2, 1);
	checkNext(scanner, INTEGER_LITERAL, 14, 2, 2, 2);
	checkNext(scanner, IDENTIFIER, 22, 2, 3, 1);
	checkNextIsEOF(scanner);
}



@Test
public void testBL1() throws LexicalException {
	String input = "true"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, BOOLEAN_LITERAL, 0, 4, 1, 1);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}


@Test
public void testIdrwithKWBL1() throws LexicalException {
	String input = "trueacd"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, IDENTIFIER, 0, 7, 1, 1);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}



@Test
public void testIntLtrIdr1() throws LexicalException {
	String input = "0123truea"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
	checkNext(scanner, INTEGER_LITERAL, 1, 3, 1, 2);
	checkNext(scanner, IDENTIFIER, 4, 5, 1, 5);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}



@Test
public void testIntLtrIdr31() throws LexicalException {
	String input = "true023"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, IDENTIFIER, 0, 7, 1, 1);
	// checkNext(scanner,INTEGER_LITERAL,1,3,1,2);
	// checkNext(scanner,IDENTIFIER,4,5,1,5);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}



@Test
public void tesOpr1() throws LexicalException {
	String input = "@"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, OP_AT, 0, 1, 1, 1);
	// checkNext(scanner,INTEGER_LITERAL,1,3,1,2);
	// checkNext(scanner,IDENTIFIER,4,5,1,5);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}


@Test
public void tesOpr31() throws LexicalException {
	String input = "<o"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, OP_LT, 0, 1, 1, 1);
	// checkNext(scanner,INTEGER_LITERAL,1,3,1,2);
	checkNext(scanner, IDENTIFIER, 1, 1, 1, 2);
	checkNextIsEOF(scanner); // Check that the only token is the EOF token.
}



@Test
public void failEscapeCharacter1() throws LexicalException {
	String input = "b^a";
	show(input);
	thrown.expect(LexicalException.class); // Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) { //
		show(e);
		assertEquals(1, e.getPos());
		throw e;
	}
}



@Test
public void stringLiteral3() throws LexicalException {
	String input = "abc \\r\\n"; // The input is the empty string. This is legal
	show(input); // Display the input
	thrown.expect(LexicalException.class); // Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) { //
		show(e);
		assertEquals(4, e.getPos());
		throw e;
	}
	
}

@Test
public void stringLiteral5() throws LexicalException {
	String input ="\"abc\t\""; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, STRING_LITERAL, 0, 6, 1, 1);
	checkNextIsEOF(scanner);
	
}

@Test
public void stringLiteral7() throws LexicalException {
	String input ="\"abcd\\\""; // The input is the empty string. This is legal
	show(input); // Display the input
	thrown.expect(LexicalException.class); // Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) { //
		show(e);
		assertEquals(7, e.getPos());
		throw e;
	}
	
}


@Test
public void stringLiteral10() throws LexicalException {
	String input ="//\n this awesome" ; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, IDENTIFIER, 4, 4, 2, 2);
	checkNext(scanner, IDENTIFIER, 9, 7, 2, 7);
	checkNextIsEOF(scanner);
	
}

@Test
public void stringLiteral11() throws LexicalException {
	String input ="\"\\\\ \""; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, STRING_LITERAL, 0, 5, 1, 1);
	checkNextIsEOF(scanner);
	
}

@Test
public void stringLiteral13() throws LexicalException {
	String input ="true"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, BOOLEAN_LITERAL, 0, 4, 1, 1);
	checkNextIsEOF(scanner);
	
}

@Test
public void stringLiteral15() throws LexicalException {
	String input ="ABC\0"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, IDENTIFIER, 0, 3, 1, 1);
	checkNextIsEOF(scanner);
	
}

@Test
public void testZeroandDigit2() throws LexicalException {
	String input = "\"\nabc\""; // The input is the empty string. This is legal
	show(input); // Display the input
	thrown.expect(LexicalException.class); // Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) { //
		show(e);
		assertEquals(1, e.getPos());
		throw e;
	}
}

@Test
public void testZeroandDigit4() throws LexicalException {
	String input = "\"abc\"def"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, STRING_LITERAL, 0, 5, 1, 1);
	checkNext(scanner, IDENTIFIER, 5, 3, 1, 6);
	checkNextIsEOF(scanner);
}

@Test
public void testZeroandDigit6() throws LexicalException {
	String input = "abc\0def";
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, IDENTIFIER, 0, 3, 1, 1);
	checkNextIsEOF(scanner);
}

@Test
public void testZeroandDigit8() throws LexicalException {
	String input = "\" greet\\ings\""; // The input is the empty string. This is legal
	show(input); // Display the input
	thrown.expect(LexicalException.class); // Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) { //
		show(e);
		assertEquals(7, e.getPos());
		throw e;
	}
}


@Test
public void testZeroandDigit10() throws LexicalException {
	String input = "\"abc\\\"a"; // The input is the empty string. This is legal
	show(input); // Display the input
	thrown.expect(LexicalException.class); // Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) { //
		show(e);
		assertEquals(7, e.getPos());
		throw e;
	}
}

@Test
public void testZeroandDigit12() throws LexicalException {
	String input = "x=-2"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, KW_x, 0, 1, 1, 1);
	checkNext(scanner, OP_ASSIGN, 1, 1, 1, 2);
	checkNext(scanner, OP_MINUS, 2, 1, 1, 3);
	checkNext(scanner, INTEGER_LITERAL, 3, 1, 1, 4);
	checkNextIsEOF(scanner);
}

@Test
public void testZeroandDigit14() throws LexicalException {
	String input = "A\tB"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, KW_A, 0, 1, 1, 1);
	checkNext(scanner, IDENTIFIER, 2, 1, 1, 3);
	checkNextIsEOF(scanner);
}

@Test
public void testZeroandDigit16() throws LexicalException {
	String input = "Test\rabc"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, IDENTIFIER, 0, 4, 1, 1);
	checkNext(scanner, IDENTIFIER, 5, 3, 2, 1);
	checkNextIsEOF(scanner);
}

@Test
public void testZeroandDigit18() throws LexicalException {
	String input = "x=\"\b\";"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, KW_x,0,1,1,1);
	checkNext(scanner, OP_ASSIGN,1,1,1,2);
	checkNext(scanner, STRING_LITERAL,2,3,1,3);
	checkNext(scanner, SEMI,5,1,1,6);
	checkNextIsEOF(scanner);
}


@Test
public void testZeroandDigit20() throws LexicalException {
	String input = "\"\n\""; // The input is the empty string. This is legal
	show(input); // Display the input
	thrown.expect(LexicalException.class); // Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) { //
		show(e);
		assertEquals(1, e.getPos());
		throw e;
	}
}

@Test
public void testZeroandDigit22() throws LexicalException {
	String input = "SCREEN(\"a\nb\");"; // The input is the empty string. This is legal
	show(input); // Display the input
	thrown.expect(LexicalException.class); // Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) { //
		show(e);
		assertEquals(9, e.getPos());
		throw e;
	}
}

@Test
public void testZeroandDigit24() throws LexicalException {
	String input = "man\ngirl"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, IDENTIFIER,0,3,1,1);
	checkNext(scanner, IDENTIFIER,4,4,2,1);
	checkNextIsEOF(scanner);
}

@Test
public void testZeroandDigit26() throws LexicalException {
	String input = "\r\nabc"; // The input is the empty string. This is legal
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
	show(scanner); // Display the Scanner
	checkNext(scanner, IDENTIFIER,2,3,2,1);
	checkNextIsEOF(scanner);
}
public void testSemi2() throws LexicalException {
	String input = ";;\t;;";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, SEMI, 0, 1, 1, 1);
	checkNext(scanner, SEMI, 1, 1, 1, 2);
	checkNext(scanner, SEMI, 3, 1, 1, 4);
	checkNext(scanner, SEMI, 4, 1, 1, 5);
	checkNextIsEOF(scanner);
}

@Test
public void testComma() throws LexicalException {
	String input = "\r;\n,\r\n;";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, SEMI, 1, 1, 2, 1);
	checkNext(scanner, COMMA, 3, 1, 3, 1);
	checkNext(scanner, SEMI, 6, 1, 4, 1);
	checkNextIsEOF(scanner);
}


@Test
public void testPAREN() throws LexicalException {
	String input = " &@ ";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, OP_AND, 1, 1, 1, 2);
	checkNext(scanner, OP_AT, 2, 1, 1, 3);
	checkNextIsEOF(scanner);
}

@Test
public void singleOP() throws LexicalException {
	String input = "@";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, OP_AT, 0, 1, 1, 1);
//	checkNext(scanner, COMMA, 3, 1, 3, 1);
//	checkNext(scanner, SEMI, 6, 1, 4, 1);
//	checkNext(scanner, SEMI, 4, 1, 2, 2);
	checkNextIsEOF(scanner);
}

@Test
public void doubleOP() throws LexicalException {
	String input = "/*";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, OP_DIV, 0, 1, 1, 1);
	checkNext(scanner, OP_TIMES, 1, 1, 1, 2);
	checkNextIsEOF(scanner);
}

@Test
public void comment() throws LexicalException {
	String input = "//\n/";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, OP_DIV, 3, 1, 2, 1);
	checkNextIsEOF(scanner);
}
@Test
public void comment1() throws LexicalException {
	String input = "//newline\nsinx==sin p";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, IDENTIFIER, 10, 4, 2, 1);
	checkNext(scanner, OP_EQ, 14, 2, 2,5 );
	checkNext(scanner, KW_sin, 16, 3, 2, 7);
	checkNext(scanner, IDENTIFIER, 20, 1, 2, 11);
	checkNextIsEOF(scanner);
}


@Test
public void digits() throws LexicalException {
	String input = "300008";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, INTEGER_LITERAL, 0, 6, 1, 1);
	checkNextIsEOF(scanner);
}

@Test
public void digits1() throws LexicalException {
	String input = "90\n0";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, INTEGER_LITERAL, 0, 2, 1, 1);
	checkNext(scanner, INTEGER_LITERAL, 3, 1, 2, 1);
	checkNextIsEOF(scanner);
}
@Test
public void digits2() throws LexicalException {
	String input = "0\rprado1";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
	checkNext(scanner, IDENTIFIER, 2, 6, 2, 1);
	checkNextIsEOF(scanner);
}
@Test
public void digits3() throws LexicalException {
	String input = "911//hurricane";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, INTEGER_LITERAL, 0, 3, 1, 1);
	checkNextIsEOF(scanner);
}

@Test
public void testKeywords() throws LexicalException {
	String input =  "true x== false y" ;
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, BOOLEAN_LITERAL, 0, 4, 1, 1);
	checkNext(scanner, KW_x, 5, 1, 1, 6);
	checkNext(scanner, OP_EQ, 6, 2, 1, 7);
	checkNext(scanner, BOOLEAN_LITERAL, 9, 5, 1, 10);
	checkNext(scanner, KW_y, 15, 1, 1, 16);
	Scanner.Token token = scanner.nextToken();
	assertEquals(Scanner.Kind.EOF,token.kind);
}
@Test
public void testEOF() throws LexicalException {
	String input = ";";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, SEMI, 0, 1, 1, 1);
	Scanner.Token token = scanner.nextToken();
	assertEquals(Scanner.Kind.EOF,token.kind);
}
/**
 * This example shows how to test that your scanner is behaving when the
 * input is illegal.  In this case, we are giving it a String literal
 * that is missing the closing ".  
 * 
 * Note that the outer pair of quotation marks delineate the String literal
 * in this test program that provides the input to our Scanner.  The quotation
 * mark that is actually included in the input must be escaped, \".
 * 
 * The example shows catching the exception that is thrown by the scanner,
 * looking at it, and checking its contents before rethrowing it.  If caught
 * but not rethrown, then JUnit won't get the exception and the test will fail.  
 * 
 * The test will work without putting the try-catch block around 
 * new Scanner(input).scan(); but then you won't be able to check 
 * or display the thrown exception.
 * 
 * @throws LexicalException
 */

@Test
public void integerOutofBoundException() throws LexicalException {
	String input = "3000000000";
	show(input);
	thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) {  //
		show(e);
		assertEquals(0,e.getPos());
		throw e;
	}
}

@Test
public void identifierTest() throws LexicalException {
	String input = "=\nbab";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);

	checkNext(scanner, OP_ASSIGN, 0, 1, 1, 1);
	checkNext(scanner, IDENTIFIER, 2, 3, 2, 1);

	checkNextIsEOF(scanner);
}


@Test
public void identifierTest2() throws LexicalException {
	String input = "*/\n";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);

	checkNext(scanner, OP_TIMES, 0, 1, 1, 1);
	checkNext(scanner, OP_DIV, 1, 1, 1, 2);
//	checkNext(scanner, SEMI, 8, 1, 3, 1);
//	checkNext(scanner, KW_log,9, 3, 3 , 2);

	checkNextIsEOF(scanner);
}


@Test
public void identifierTest3() throws LexicalException {
	String input = "@**\t";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, OP_AT, 0, 1, 1, 1);
	checkNext(scanner, OP_POWER, 1, 2, 1, 2);
	checkNextIsEOF(scanner);
}

@Test
public void identifierTest4() throws LexicalException {
	String input = "abc<=sinx";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);

	checkNext(scanner, IDENTIFIER, 0, 3, 1, 1);
	checkNext(scanner, OP_LE, 3, 2, 1, 4);
	checkNext(scanner, IDENTIFIER, 5, 4, 1, 6);
	checkNextIsEOF(scanner);
}


@Test
public void identifierTest8() throws LexicalException {
	String input = "a||b=c&&d";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);

	checkNext(scanner, KW_a, 0, 1, 1, 1);
	checkNext(scanner, OP_OR, 1, 1, 1, 2);
	checkNext(scanner, OP_OR, 2, 1, 1, 3);
	checkNext(scanner, IDENTIFIER, 3, 1, 1, 4);
	checkNext(scanner, OP_ASSIGN, 4, 1, 1, 5);
	checkNext(scanner, IDENTIFIER, 5, 1, 1, 6);
	checkNext(scanner, OP_AND, 6, 1, 1, 7);
	checkNext(scanner, OP_AND, 7, 1, 1, 8);
	checkNext(scanner, IDENTIFIER, 8, 1, 1, 9);
	checkNextIsEOF(scanner);
}
@Test
public void identifierTest5() throws LexicalException {
	String input = "//abc\0def";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNextIsEOF(scanner);
}

@Test
public void identifierTest6() throws LexicalException {
	String input = "booleanx==boolean TRUE";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, IDENTIFIER, 0, 8, 1, 1);
	checkNext(scanner, OP_EQ, 8, 2, 1, 9);
	checkNext(scanner, KW_boolean, 10, 7, 1, 11);
	checkNext(scanner, IDENTIFIER, 18, 4, 1, 19);
	checkNextIsEOF(scanner);
}

@Test
public void identifierTest7() throws LexicalException {
	String input = "polar_r\t=>abs(x)";
	
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);
	checkNext(scanner, KW_polar_r, 0, 7, 1, 1);
	checkNext(scanner, OP_ASSIGN, 8, 1, 1, 9);
	checkNext(scanner, OP_GT, 9, 1, 1, 10);
	checkNext(scanner, KW_abs, 10, 3, 1, 11);
	checkNext(scanner, LPAREN, 13, 1, 1, 14);
	checkNext(scanner, KW_x, 14, 1, 1, 15);
	checkNext(scanner, RPAREN, 15, 1, 1, 16);
	checkNextIsEOF(scanner);
}






@Test
public void stringLittest() throws LexicalException {
	String input = ",  x ,";
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);

	checkNext(scanner, COMMA, 0, 1, 1, 1);
	checkNext(scanner, KW_x, 3, 1, 1, 4);
	checkNext(scanner, COMMA, 5, 1, 1, 6);
	checkNextIsEOF(scanner);
}


@Test
public void stringLittest1() throws LexicalException {
	String input = "\"prad\\nppatnaik\"";
	Scanner scanner = new Scanner(input).scan();
	show(input);
	show(scanner);

	checkNext(scanner, STRING_LITERAL, 0, 16, 1, 1);

	checkNextIsEOF(scanner);
}
@Test
public void failUnclosedStringLiteral() throws LexicalException {
	String input = "\"true";
	show(input);
	thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) {  //
		show(e);
		assertEquals(5,e.getPos());
		throw e;
	}
}

@Test
public void failUnclosedStringLiteral2() throws LexicalException {
	String input = "\"\npradosa\"";
	show(input);
	thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) {  //
		show(e);
		assertEquals(1,e.getPos());
		throw e;
	}
}

@Test
public void illiegalCharacterfound() throws LexicalException {
	String input = "a\b";
	show(input);
	thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) {  //
		show(e);
		assertEquals(1,e.getPos());
		throw e;
	}
}
@Test
public void testAssign() throws LexicalException {
	String input = "=";  
	show(input);        //Display the input 
	Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
	show(scanner);   //Display the Scanner
	checkNext(scanner, OP_ASSIGN, 0, 1, 1, 1);
}
@Test
public void testGE() throws LexicalException {
	String input = ">=";  
	show(input);        //Display the input 
	Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
	show(scanner);   //Display the Scanner
	checkNext(scanner, OP_GE, 0, 2, 1, 1);
}

@Test
public void testGEwithAssign() throws LexicalException {
	String input = ">==";  
	show(input);        //Display the input 
	Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
	show(scanner);   //Display the Scanner
	checkNext(scanner, OP_GE, 0, 2, 1, 1);
	checkNext(scanner, OP_ASSIGN, 2, 1, 1, 3);
	checkNextIsEOF(scanner);


}


@Test
public void Comment() throws LexicalException {
	String input = "//abcd\n>=";  
	show(input);        //Display the input 
	Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
	show(scanner);   //Display the Scanner
	//checkNext(scanner, IDENTIFIER, 1,3, 1, 2);
	checkNext(scanner, OP_GE, 7, 2, 2, 1);
	checkNextIsEOF(scanner);


}
@Test
public void identifierwithOP() throws LexicalException {
	String input = " _AB\n >==";  
	show(input);        //Display the input 
	Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
	show(scanner);   //Display the Scanner
	checkNext(scanner, IDENTIFIER, 1,3, 1, 2);
	checkNext(scanner, OP_GE, 6, 2, 2, 2);
	checkNext(scanner, OP_ASSIGN, 8, 1, 2, 4);
	checkNextIsEOF(scanner);


}	

@Test
public void Keyword() throws LexicalException {
	String input = "\"ab\\tc\"";  
	show(input);        //Display the input 
	Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
	show(scanner);   //Display the Scanner
//	checkNext(scanner, IDENTIFIER, 0,3, 1, 1);
	//checkNext(scanner, OP_EXCL, 3, 1, 1, 4);
	checkNext(scanner, STRING_LITERAL, 0,7, 1, 1);
	checkNextIsEOF(scanner);


}


      

@Test
public void failquoteInStringLiteral() throws LexicalException {
	String input = "\"ab\"c\"";  
	show(input);
	
}

@Test
public void Integerliteral() throws LexicalException {
	String input = "[14]\"a\tb\"";  
	show(input);        //Display the input 
	Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
	show(scanner);   //Display the Scanner
//	checkNext(scanner,LSQUARE,0,1,1,1);
	//checkNext(scanner,INTEGER_LITERAL,1,2,1,2);
	//checkNext(scanner,RSQUARE,3,1,1,4);
//	checkNext(scanner,STRING_LITERAL,4,1,1,4);

  //  checkNextIsEOF(scanner);
}
@Test
public void identifierwithOP1() throws LexicalException {
	String input = "\b";  
	show(input);        //Display the input 
	thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) {  //
		show(e);
		assertEquals(0,e.getPos());
		throw e;
	}
}



@Test
public void Keyword1() throws LexicalException {
	String input = "100 000";  
	show(input);        //Display the input 
	Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
	show(scanner);   //Display the Scanner
//	checkNext(scanner, IDENTIFIER, 0,3, 1, 1);
	//checkNext(scanner, OP_EXCL, 3, 1, 1, 4);
	checkNext(scanner, INTEGER_LITERAL, 0,3, 1, 1);
	checkNext(scanner, INTEGER_LITERAL, 4,1, 1, 5);
	checkNext(scanner, INTEGER_LITERAL, 5,1, 1, 6);
	checkNext(scanner, INTEGER_LITERAL, 6,1, 1, 7);
	checkNextIsEOF(scanner);
}
@Test
public void identifierwithOP2() throws LexicalException {
	String input = "\" greetings \\ \"";  
	show(input);        //Display the input 
	thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
	try {
		new Scanner(input).scan();
	} catch (LexicalException e) {  //
		show(e);
		assertEquals(12,e.getPos());
		throw e;
	}
}

@Test
public void identifierwithOP4() throws LexicalException {
	String input = "\"a&b\"";  
	show(input);        //Display the input 
	Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
	show(scanner);   //Display the Scanner
//	checkNext(scanner, IDENTIFIER, 0,3, 1, 1);
	//checkNext(scanner, OP_EXCL, 3, 1, 1, 4);
	checkNext(scanner, STRING_LITERAL, 0,5, 1, 1);
}
 
}



     
    

