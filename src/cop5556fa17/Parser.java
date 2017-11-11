package cop5556fa17;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cop5556fa17.AST.*;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}


	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program p=null;
		p=program();
		//System.out.println("Inside parse after processing program");
		matchEOF();
		return p;
	}
	

	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {
		//TODO  implement this
		//System.out.println("Inside program");
		Program program=null;
		Token firstToken=t;
		Token name=t;
		ArrayList<ASTNode> decsAndStatements=new ArrayList<>();
		identifier();
		if(t.kind.equals(KW_int)|| t.kind.equals(KW_boolean)|| t.kind.equals(KW_image)|| t.kind.equals(KW_url)
				|| t.kind.equals(KW_file) ||t.kind.equals(IDENTIFIER) ) {
		while(t.kind.equals(KW_int)|| t.kind.equals(KW_boolean)|| t.kind.equals(KW_image)|| t.kind.equals(KW_url)
				|| t.kind.equals(KW_file) ||t.kind.equals(IDENTIFIER))
		{
			if(t.kind.equals(KW_int)|| t.kind.equals(KW_boolean)|| t.kind.equals(KW_image)|| t.kind.equals(KW_url)
				|| t.kind.equals(KW_file))
			{
				decsAndStatements.add(declaration());
				if(t.kind.equals(SEMI))
				{
					match(SEMI);
				
				}
				else
					throw new SyntaxException(t,"program(): no ; found");
			}
			else if(t.kind.equals(IDENTIFIER))
			{
				decsAndStatements.add(statement());
				if(t.kind.equals(SEMI))
				{
					//System.out.println("In program");
					match(SEMI);
				}
				else
					throw new SyntaxException(t,"program():no ; found");
			}
		}
		}
//		else if(t.kind.equals(EOF))
//			return;
//		else
//			throw new SyntaxException(t,"program():unexpected token found");
		program =new Program(firstToken,name,decsAndStatements);
		return program;
		
	}

	public Statement statement() throws SyntaxException {
		Statement statement=null;
		// TODO Auto-generated method stub
		//match(IDENTIFIER);
		//System.out.println(t.kind);
		Token newToken=scanner.peek();
		if(newToken.kind.equals(OP_ASSIGN)|| newToken.kind.equals(OP_RARROW)|| newToken.kind.equals(OP_LARROW)||  newToken.kind.equals(LSQUARE))
		{
			
			if(newToken.kind.equals(OP_ASSIGN)|| newToken.kind.equals(LSQUARE))
			{
				statement=assignmentstatement();
			}
			else if(newToken.kind.equals(OP_RARROW))
			{
				statement=imageOutStatement();
			}
			else if(newToken.kind.equals(OP_LARROW))
			{
				statement=imageInStatement();
			}
//			else
//			{
//				throw new SyntaxException(t,"statement(): failure to find the first values of statement");
//			}
		}
		else {
			throw new SyntaxException(t,"statement(): no = or -> or <- found");
		}
		//System.out.println("In statement "+statement);
		return statement;
		
	}

	private Statement_In imageInStatement() throws SyntaxException {
		// TODO Auto-generated method stub
		Statement_In statement_in=null;
        Token firstToken=t;
        Token name=t;
        Source source=null;
        if(t.kind.equals(IDENTIFIER))
            consume();
		if(t.kind.equals(OP_LARROW))
			match(OP_LARROW);
		source=source();
		statement_in=new Statement_In(firstToken,name,source);
        return statement_in;
	}

	private Statement_Out imageOutStatement() throws SyntaxException {
		// TODO Auto-generated method stub
		 Statement_Out statement_out=null;
	        Token firstToken=t;
	        Token name=t;
	        Sink sink=null;
	        if(t.kind.equals(IDENTIFIER))
	            consume();
		if(t.kind.equals(OP_RARROW))
			match(OP_RARROW);
		sink=sink();
		statement_out=new Statement_Out(firstToken,name,sink);
        return statement_out;
	}

	private Statement_Assign assignmentstatement() throws SyntaxException {
		// TODO Auto-generated method stub
		Statement_Assign statement_assign=null;
        Token firstToken=t;
        LHS lhs=null;
        Expression expression=null;
		lhs=lhs();
		if(t.kind.equals(OP_ASSIGN))
			match(OP_ASSIGN);
		else
			throw new SyntaxException(t,"assignmentStatement(): == not found");
		expression=expression();
		
		statement_assign=new Statement_Assign(firstToken,lhs,expression);
		//System.out.println("hi"+firstToken);
        return statement_assign;
	}

	public LHS lhs() throws SyntaxException {
		// TODO Auto-generated method stub
		LHS lhs=null;
        Token firstToken=t;
        Token name=t;
        match(IDENTIFIER);
        Index in=null;
		if(t.kind.equals(LSQUARE))
		{	
			match(LSQUARE);
			in=lhsSelector();
			if(t.kind.equals(RSQUARE))
				match(RSQUARE);
			else
			{
				throw new SyntaxException(t,"lhs(): RSQUARE not found");
			}
		}
		else if(t.kind.equals(OP_ASSIGN))
			{
			lhs=new LHS(firstToken,name,in);
		     return lhs;
			}
		else
		{
			throw new SyntaxException(t,"lhs(): token not == or [ not found");
		}
		 lhs=new LHS(firstToken,name,in);
	     return lhs;
			
		
	}

	public Index lhsSelector() throws SyntaxException {
		// TODO Auto-generated method stub
		Index i=null;
		if(t.kind.equals(LSQUARE))
		{
			match(LSQUARE);
			if(t.kind.equals(KW_x))
				i=xySelector();
			else if(t.kind.equals(KW_r))
			    i=raSelector();
			else {
				throw new SyntaxException(t,"lhsSelector():Did not find x or r");
			}
			if(t.kind.equals(RSQUARE))
				match(RSQUARE);
			else
				throw new SyntaxException(t,"lhsSelector():Did not find RSQUARE");
		}
		else
		{
			throw new SyntaxException(t,"lhsSelector():Did not find [");
		}
		return i;
		
	}

	public Index raSelector() throws SyntaxException {
		// TODO Auto-generated method stub
		Index i=null;
        Token firstToken=t;
        Expression_PredefinedName e0=null;
        Expression_PredefinedName e1=null;
		if(t.kind.equals(KW_r))
		{
			Token ftr=t;
			Kind k=t.kind;
			match(KW_r);
			 e0=new Expression_PredefinedName(ftr,k);
			if(t.kind.equals(COMMA))
			{	match(COMMA);
				if(t.kind.equals(KW_A))
				{	Token ftA=t;
	                k=t.kind;
					match(KW_A);
					e1=new Expression_PredefinedName(ftA,k);}
				else
					throw new SyntaxException(t,"raSelector():Did not find A");
			}
			else
				throw new SyntaxException(t,"raSelector():Did not find ,");
			
		}
		else
		{
			throw new SyntaxException(t,"raSelector():Did not find r");
		}
		i=new Index(firstToken,e0,e1);
        return i;
	}

	public Index xySelector() throws SyntaxException {
		// TODO Auto-generated method stub
		Index i=null;
        Token firstToken=t;
        Expression_PredefinedName e0=null;
        Expression_PredefinedName e1=null;
        Kind k;
		if(t.kind.equals(KW_x))
		{
			Token ftx=t;
			k=t.kind;
			match(KW_x);
			e0=new Expression_PredefinedName(ftx,k);
			if(t.kind.equals(COMMA))
			{	
				match(COMMA);
				if(t.kind.equals(KW_y))
				{	Token fty=t;
				    k=t.kind;
				    match(KW_y);
				    e1=new Expression_PredefinedName(fty,k);
				}
				else
					throw new SyntaxException(t,"xySelector():Did not find y");
			}
			else
				throw new SyntaxException(t,"raSelector():Did not find ,");		
		}
		else
		{
			throw new SyntaxException(t,"raSelector():Did not find x");
		}
		 i=new Index(firstToken,e0,e1);
	     return i;
	}

	private Declaration_SourceSink sourceSinkDeclaration() throws SyntaxException {
		// TODO Auto-generated method stub
		Declaration_SourceSink declaration_SourceSink=null;
        Token firstToken=t;
        Source s=null;
        Token type=null;
        Token name=null;
       // System.out.println("In sourceSink declaration"+t);
		if(t.kind.equals(KW_url)|| t.kind.equals(KW_file))
		{
			type=t;
			sourceSinkType();
			name=t;
			identifier();
			if(t.kind.equals(OP_ASSIGN))
			{
				match(OP_ASSIGN);
				if(t.kind.equals(STRING_LITERAL)|| t.kind.equals(OP_AT)|| t.kind.equals(IDENTIFIER))
					s=source();
			}
			else
			{
				throw new SyntaxException(t,"Failed in sourceSinkDeclaration()");
			}
		}
		declaration_SourceSink=new Declaration_SourceSink(firstToken,type,name,s);
        return declaration_SourceSink;
		
	}

	private Source source()  throws SyntaxException{
		// TODO Auto-generated method stub
		 Source s=null;
		 Token firstToken=t;
		 Token name=t; 
		if(t.kind.equals(STRING_LITERAL))
		{String fileorurl=t.getText();
		match(STRING_LITERAL);
		s=new Source_StringLiteral(firstToken,fileorurl);
		}
		else if(t.kind.equals(OP_AT))
		{	Expression paramnum=null; match(OP_AT); paramnum=expression();
		 s=new Source_CommandLineParam(firstToken,paramnum);}
		else if(t.kind.equals(IDENTIFIER))
		{	match(IDENTIFIER);
		s=new Source_Ident(firstToken,name);
		}
		else
			throw new SyntaxException(t, " Failed at source()");
		return s;
	}

	public void sourceSinkType() throws SyntaxException {
		// TODO Auto-generated method stub
		if(t.kind.equals(KW_url))
		{
			match(KW_url);
		}
		else if(t.kind.equals(KW_file))
			match(KW_file);
		else
			throw new SyntaxException(t, " Failed at sourceSinkType()");
	}

	private Declaration_Image imageDeclaration() throws SyntaxException {
		// TODO Auto-generated method stub
		Token firstToken=t;
		Declaration_Image declaration_image=null;
		Expression xsize=null;
		Expression ysize=null;
		Source s=null;
		Token name=null;
		//System.out.println("in declaration "+t);
		if(t.kind.equals(KW_image))
		{
			match(KW_image);
			if(t.kind.equals(LSQUARE))
			{
				match(LSQUARE);
				xsize=expression();
				if(t.kind.equals(COMMA))
				{
					match(COMMA);
					ysize=expression();
					if(t.kind.equals(RSQUARE))
						match(RSQUARE);
					else
					{
						String message =  "Expected ] at line " + t.line + " and position :" + t.pos_in_line;
				   		throw new SyntaxException(t, message);
					}
				}
				else {
					String message =  "Expected token at line " + t.line + " and position :" + t.pos_in_line;
			   		throw new SyntaxException(t, message);
				}
			}
			if(t.kind.equals(IDENTIFIER))
			{	
				name=t;
				match(IDENTIFIER);
				if(t.kind.equals(OP_LARROW))
				{	
					match(OP_LARROW);
					s=source();
				}
				else if(t.kind.equals(SEMI)) 
				{	declaration_image=new Declaration_Image(firstToken,xsize,ysize,name,s);
		        return declaration_image;}
			}
			else
			{
				String message =  "imageDeclaration():Expected IDENTIFIER at line " + t.line + " and position :" + t.pos_in_line;
			   	throw new SyntaxException(t, message);
			}
		}
		declaration_image=new Declaration_Image(firstToken,xsize,ysize,name,s);
        return declaration_image;

		}

	private Sink sink() throws SyntaxException {
		// TODO Auto-generated method stub
		Sink sink=null;
        Token firstToken=t;
        Token name=t;
		if(t.kind.equals(IDENTIFIER))
		{ sink=new Sink_Ident(firstToken,name);	identifier();}
		else if(t.kind.equals(KW_SCREEN))
		{	 sink=new Sink_SCREEN(firstToken);  match(KW_SCREEN);}
		else
		{
			String message =  "sink():Unexpected token at line " + t.line + " and position :" + t.pos_in_line;
	   		throw new SyntaxException(t, message);
		}
		return sink;
		
	}

	private Declaration declaration() throws SyntaxException {
		// TODO Auto-generated method stub
		Declaration declaration=null;
		//System.out.println("here");
		if(t.kind.equals(KW_int)|| t.kind.equals(KW_boolean))
			declaration=variableDeclaration();
		if(t.kind.equals(KW_image))
			declaration=imageDeclaration();
		if(t.kind.equals(KW_url)|| t.kind.equals(KW_file))
			declaration=sourceSinkDeclaration();
		return declaration;
	}

	private Declaration_Variable variableDeclaration() throws SyntaxException {
		// TODO Auto-generated method stub
		Declaration_Variable declarationVariable=null;
		Token firstToken=t;
		Token type=t;
		Expression expression=null;
		Token name=null;
		if(t.kind.equals(KW_int)||t.kind.equals(KW_boolean))
		{
			varType();
			name=t;
			identifier();
			if(t.kind.equals(OP_ASSIGN))
			{
				match(OP_ASSIGN);
				expression=expression();
			}
			else if(t.kind.equals(SEMI))
			{	declarationVariable=new Declaration_Variable(firstToken,type,name,expression);
			return declarationVariable;}
			else
			  {
		    	 String message =  "Unexpected token at line " + t.line + " and position :" + t.pos_in_line;
		   		throw new SyntaxException(t, message);
		       }	
		}
		declarationVariable=new Declaration_Variable(firstToken,type,name,expression);
		return declarationVariable;
	}

	public void varType() throws SyntaxException {
		// TODO Auto-generated method stub
		
		if(t.kind.equals(KW_int))
			match(KW_int);
		else if	(t.kind.equals(KW_boolean))
			match(KW_boolean);
		else
		{
			throw new SyntaxException(t,"varType():token is not int or boolean");
		}
		
	}

	private void identifier() throws SyntaxException {
		// TODO Auto-generated method stub
		//System.out.println("In identifier"+t);
		if(t.kind.equals(IDENTIFIER))
			match(IDENTIFIER);
		else 
		{
			throw new SyntaxException(t,"identifier():token is not identifier");
		}
		
	}

	private void consume() {
		// TODO Auto-generated method stub
		t = scanner.nextToken();
	}

	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * 
	 * @throws SyntaxException
	 */
	Expression expression() throws SyntaxException {
		//TODO implement this.
		//Expression expression_conditional=null;
		Token firstToken=t;
		Expression condition=null;
		Expression trueExpression=null;
		Expression falseExpression=null;
		condition=orExpression();
		   // System.out.println(t.kind);
		    if(t.kind.equals(OP_Q))
			{
		    	match(OP_Q);
		    	trueExpression=expression();
		    	match(OP_COLON);
		    	falseExpression=expression();
		    	condition=new Expression_Conditional(firstToken,condition,trueExpression,falseExpression);
			}	
		    //System.out.println("in expression "+condition);
		    return condition;
		}
		//throw new UnsupportedOperationException();
	public Expression orExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		//Expression_Binary expression_binary=null;
		Token firstToken=t;
		Expression e0=null;
		Token op=null;
		Expression e1=null;
		e0=andExpression();
		while(t.kind.equals(OP_OR))
		{
			op=t;
			match(OP_OR);
			e1=andExpression();
			e0=new Expression_Binary(firstToken,e0,op,e1);
		}
		//e0=new Expression_Binary(firstToken,e0,op,e1);
		return e0;
	}

	public Expression andExpression() throws SyntaxException {
			// TODO Auto-generated method stub
		//Expression_Binary expression_binary=null;
		Token firstToken=t;
		Expression e0=null;
		Token op=null;
		Expression e1=null;
			e0=eqExpression();
			while(t.kind.equals(OP_AND))
			{
				op=t;
				match(OP_AND);
				e1=eqExpression();
				e0=new Expression_Binary(firstToken,e0,op,e1);
			}
			
			return e0;
		}

	public Expression eqExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		//Expression_Binary expression_binary=null;
		Token firstToken=t;
		Expression e0=null;
		Token op=null;
		Expression e1=null;
		e0=relExpression();
		while(t.kind.equals(OP_EQ)|| t.kind.equals(OP_NEQ))
		{
			if(t.kind.equals(OP_EQ))
			{	
				op=t;
				match(OP_EQ);
			}
			else if(t.kind.equals(OP_NEQ))
			{	
				op=t;
				match(OP_NEQ);
			}
			e1=relExpression();
			e0=new Expression_Binary(firstToken,e0,op,e1);
		}
		return e0;
	}

	public Expression relExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		//Expression_Binary expression_binary=null;
		Token firstToken=t;
		Expression e0=null;
		Token op=null;
		Expression e1=null;
		e0=addExpression();
		while(t.kind.equals(OP_LT)  || t.kind.equals(OP_GT) ||  t.kind.equals(OP_LE)  || 
				t.kind.equals(OP_GE))
		{
			if(t.kind.equals(OP_LT))
			{
				op=t;match(OP_LT);
			}
			else if(t.kind.equals(OP_GT))
			{	op=t;match(OP_GT);}
			else if(t.kind.equals(OP_LE))
			{	op=t;match(OP_LE);}
			else if(t.kind.equals(OP_GE))
			{	op=t;match(OP_GE);}
			e1=addExpression();
			e0=new Expression_Binary(firstToken,e0,op,e1);
		}
		return e0;
	}

	public Expression addExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		//Expression_Binary expression_binary=null;
		Token firstToken=t;
		Expression e0=null;
		Token op=null;
		Expression e1=null;
		e0=multExpression();
		while(t.kind.equals(OP_PLUS)  || t.kind.equals(OP_MINUS))
		{
			if(t.kind.equals(OP_PLUS))
				{op=t;match(OP_PLUS);}
			else if(t.kind.equals(OP_MINUS))
				{op=t;match(OP_MINUS);}
			e1=multExpression();
			e0=new Expression_Binary(firstToken,e0,op,e1);
		}
	
		return e0;
	}

	public Expression multExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Expression_Binary expression_binary=null;
		Token firstToken=t;
		Expression e0=null;
		Token op=null;
		Expression e1=null;
		e0=unaryExpression();
		while(t.kind.equals(OP_TIMES)  || t.kind.equals(OP_DIV)||t.kind.equals(OP_MOD))
		{
			if(t.kind.equals(OP_TIMES))
				{op=t;match(OP_TIMES);}
			else if(t.kind.equals(OP_DIV))
				{op=t;match(OP_DIV);}
			else if(t.kind.equals(OP_MOD))
				{op=t;match(OP_MOD);}
			e1=unaryExpression();
			e0=new Expression_Binary(firstToken,e0,op,e1);
			
		}
		//System.out.println("In multexpression"+e0);
		return e0;
	}

	public Expression unaryExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Expression_Unary expression_unary=null;
		Token firstToken=t;
		//System.out.println("hi"+firstToken);
		Token op=null;
		Expression e=null;
		if(t.kind.equals(OP_PLUS))
		{
			op=t;
			match(OP_PLUS);
			e=unaryExpression();
		}
		else if(t.kind.equals(OP_MINUS))
		{
			op=t;
			match(OP_MINUS);
			e=unaryExpression();
		}
		else 
		{
			e=unaryExpressionNotPlusMinus();
			return e;
			
		}
		//System.out.println(op);
		expression_unary=new Expression_Unary(firstToken,op,e);
		// System.out.println("in unary expresiion "+expression_unary);
		return expression_unary;
	}

	public Expression unaryExpressionNotPlusMinus() throws SyntaxException {
		// TODO Auto-generated method stub
		Expression expression_unary=null;
		Token firstToken=t;
		Token op=null;
		Expression expression=null;
		 if(t.kind.equals(OP_EXCL)) 
		 {
			 op=t;
			 match(OP_EXCL);
			 expression=unaryExpression();
			 expression_unary=new Expression_Unary(firstToken,op,expression);
		 }
		 else if(t.kind.equals(INTEGER_LITERAL) || t.kind.equals(LPAREN)||t.kind.equals(KW_sin)
				 || t.kind.equals(KW_cos)||t.kind.equals(KW_atan)||t.kind.equals(KW_abs)
				|| t.kind.equals(KW_cart_x)||t.kind.equals(KW_cart_y)||t.kind.equals(KW_polar_a)
				||t.kind.equals(KW_polar_r) || t.kind.equals(BOOLEAN_LITERAL))
		 {
			//System.out.println("Going here");
			expression_unary=primary();
		 }
		 else if(t.kind.equals(IDENTIFIER))
			 expression_unary=identOrPixelSelectorExpression();
		  else if(t.kind.equals(KW_x)||t.kind.equals(KW_y)||
	                t.kind.equals(KW_r)||t.kind.equals(KW_a)||t.kind.equals(KW_X)||t.kind.equals(KW_Y)||t.kind.equals(KW_Z)||t.kind.equals(KW_A)||
	                t.kind.equals(KW_R)||t.kind.equals(KW_DEF_X)||t.kind.equals(KW_DEF_Y))
		 {
			  Kind kind=t.kind;
			 if(t.kind==Kind.KW_x)
				match(Kind.KW_x);
			else if(t.kind==Kind.KW_y)
				match(Kind.KW_y);
			else if(t.kind==Kind.KW_r)
				match(Kind.KW_r);
			else if(t.kind==Kind.KW_a)
				match(Kind.KW_a);
			else if(t.kind==Kind.KW_X)
				match(Kind.KW_X);
			else if(t.kind==Kind.KW_Y)
				match(Kind.KW_Y);
			else if(t.kind==Kind.KW_Z)
				match(Kind.KW_Z);
			else if(t.kind==Kind.KW_A)
				match(Kind.KW_A);
			else if(t.kind==Kind.KW_R)
				match(Kind.KW_R);
			else if(t.kind==Kind.KW_DEF_X)
				match(Kind.KW_DEF_X);
			else if(t.kind==Kind.KW_DEF_Y)
				match(Kind.KW_DEF_Y);
			else
			{
				throw new SyntaxException(t,"unaryExpressionNotPlusMinus()Unexpected token");
			}
			 expression_unary=new Expression_PredefinedName(firstToken,kind);
		 }
		 //System.out.println("in unary expresiion not plus or minus"+expression_unary);
		 return expression_unary;

	}
	public Expression functionApplication() throws SyntaxException {
		// TODO Auto-generated method stub
		  Expression expression=null;
	        Token firstToken=t;
		if(t.kind.equals(KW_sin)
				 || t.kind.equals(KW_cos)||t.kind.equals(KW_atan)||t.kind.equals(KW_abs)
				|| t.kind.equals(KW_cart_x)||t.kind.equals(KW_cart_y)||
				t.kind.equals(KW_polar_a)||t.kind.equals(KW_polar_r))
		{
			Kind func=t.kind;
			functionName();
			if(t.kind.equals(LPAREN))
			{
				Expression arg=null;
				match(LPAREN);
				arg=expression();
				match(RPAREN);
				expression=new Expression_FunctionAppWithExprArg(firstToken,func,arg);
			}
			else if(t.kind.equals(LSQUARE))
			{
				Index arg=null;
				match(LSQUARE);
				arg=selector();
				match(RSQUARE);
				 expression=new Expression_FunctionAppWithIndexArg(firstToken,func,arg);
			}
			else
			{
				throw new SyntaxException(t, "functionApplication(): Token not found");
			}
			
		}
		else
		{
			throw new SyntaxException(t, "functionApplication(): Token not found");
		}
		//System.out.println("in finctionApplication "+expression);
		return expression;
	}

	public Expression identOrPixelSelectorExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		  Expression expression=null;
	        Token firstToken=t;
	        Token ident=t;
            Token name=t;
		if(t.kind.equals(IDENTIFIER))
		{ 
			match(IDENTIFIER);
			expression=new Expression_Ident(firstToken,ident);
			//System.out.println("In ident or pixel selector expression "+ expression);
			if(t.kind.equals(LSQUARE))
			{
				Index i=null;
				match(LSQUARE);
				i=selector();
				match(RSQUARE);
			    expression=new Expression_PixelSelector(firstToken,name,i);
			}
		}
		else
		{
			throw new SyntaxException(t,"primary():Unexpected token found in primary");
		}
		//System.out.println(expression);
		return expression;
		
	}

	public Index selector() throws SyntaxException {
		// TODO Auto-generated method stub
		Index i=null;
        Token firstToken=t;
        //System.out.println("first token is:"+t);
        Expression e0=null;
        Expression e1=null;
		e0=expression();
		//System.out.println("here in selector"+e0);
		if(t.kind.equals(COMMA))
		{	
			match(COMMA);
			e1=expression();
		}
		else
		{
			throw new SyntaxException(t,"selector():, not found");
		}
		i=new Index(firstToken,e0,e1);
		//System.out.println("now here in selector"+e0);
        return i;
	}

	public Expression primary() throws SyntaxException {
		// TODO Auto-generated method stub
		 Expression expression=null;
	     Token firstToken=t;
		if(t.kind.equals(INTEGER_LITERAL))
		{	int val=Integer.parseInt(t.getText());match(INTEGER_LITERAL); 
		expression=new Expression_IntLit(firstToken,val);}
		else if(t.kind.equals(LPAREN))
		{	
			match(LPAREN);
			expression=expression();
			if(t.kind.equals(RPAREN))
				match(RPAREN);
		}
		else if(t.kind.equals(KW_sin)
				 || t.kind.equals(KW_cos)||t.kind.equals(KW_atan)||t.kind.equals(KW_abs)
				|| t.kind.equals(KW_cart_x)||t.kind.equals(KW_cart_y)||
				t.kind.equals(KW_polar_a)||t.kind.equals(KW_polar_r))
		{
			//System.out.println("Here in primry!!!!!!!!!!!!!!!");
			expression=functionApplication();
		}
		else if(t.kind.equals(BOOLEAN_LITERAL))
		{
			boolean val=Boolean.parseBoolean(t.getText());
			match(BOOLEAN_LITERAL);
			expression=new Expression_BooleanLit(firstToken,val);
		}
		else
		{
			throw new SyntaxException(t,"primary():Unexpected token found in primary");
		}
		
		return expression;
	}



	public void functionName() throws SyntaxException {
		// TODO Auto-generated method stub
		if(t.kind.equals(KW_sin))
			match(KW_sin);
		else if( t.kind.equals(KW_cos))
			match(KW_cos);
		else if(t.kind.equals(KW_atan))
			match(KW_atan);
		else if(t.kind.equals(KW_abs))
			match(KW_abs);
		else if(t.kind.equals(KW_cart_x))
			match(KW_cart_x);
		else if(t.kind.equals(KW_cart_y))
			match(KW_cart_y);
		else if(t.kind.equals(KW_polar_a))
			match(KW_polar_a);
		else if(t.kind.equals(KW_polar_r))
			match(KW_polar_r);
		else
			throw new SyntaxException(t, "functionName(): Token not found");
		
	}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		//System.out.println(t.kind);
		if (t.kind.equals(EOF)) {
			return t;
		}
		String message =  "Expected EOL at line " + t.line + "and position :" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
	
	 void match(Kind kind) throws SyntaxException{
		// System.out.println("match(): "+t.getText()+" "+kind);
	       if( t.kind.equals(kind)){
	    	   
			   consume();
	       } else  
	       {
	    	   String message =  "match(): Expected token "+ kind+" but got token "+t.kind+" at " + t.line + ":" + t.pos_in_line;
	   		throw new SyntaxException(t, message);
	       }
	   }
	 


	 


}
