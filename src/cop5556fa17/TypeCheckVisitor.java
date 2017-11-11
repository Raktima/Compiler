package cop5556fa17;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.omg.Messaging.SyncScopeHelper;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;

public class TypeCheckVisitor implements ASTVisitor {
	
	Map<String, Declaration> symbolTable=new HashMap<String, Declaration>();
	

		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			//System.out.println("In program: "+node);
			node.visit(this, arg);
			
		}
		
		return program.name;
	}
	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println(symbolTable.containsKey(declaration_Image.name));
		if(symbolTable.get(declaration_Image.name)==null)
		{
			symbolTable.put(declaration_Image.name,declaration_Image);
			declaration_Image.setType(Type.IMAGE);
		}
		else
		{
			//System.out.println("here");
			throw new SemanticException(declaration_Image.firstToken, "Type Exception Occured at visitDeclaration_Variable");
		}
		Source s=declaration_Image.source;
		if(s!=null)
		s=(Source)s.visit(this, null);
		Expression xSize=declaration_Image.xSize;
		Expression ySize=declaration_Image.ySize;
		if(xSize!=null )
		{
			xSize=(Expression)xSize.visit(this, null);
			if(ySize!=null )
			{
				ySize=(Expression)ySize.visit(this, null);
				if(xSize.Type.equals(Type.INTEGER) && ySize.Type.equals(Type.INTEGER))
				{
					//do nothing
				}
				else
					throw new SemanticException(declaration_Image.firstToken, "Type Exception Occured at visitDeclaration_Image");
			}
			else
				throw new SemanticException(declaration_Image.firstToken, "Type Exception Occured at visitDeclaration_Image");
		}
		else if(xSize==null && ySize==null)
		{
			//do nothing
		}
	
		//else
		//	throw new SemanticException(declaration_Image.firstToken, "Type Exception Occured at visitDeclaration_Image");
		return declaration_Image;
	}
	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("In visitDeclaration_SourceSink: "+declaration_SourceSink);
		Source s=declaration_SourceSink.source;
		if(!symbolTable.containsKey(declaration_SourceSink.name))
		{
			symbolTable.put(declaration_SourceSink.name,declaration_SourceSink);
			//System.out.println(TypeUtils.getType(declaration_SourceSink.type));
			if(declaration_SourceSink.type.equals(Kind.KW_url))
				declaration_SourceSink.setType(Type.URL);
			if(declaration_SourceSink.type.equals(Kind.KW_file))
				declaration_SourceSink.setType(Type.FILE);
			//System.out.println(s);
			if(s!=null)
			{
				s=(Source)declaration_SourceSink.source.visit(this, null);
				if(s.Type.equals(declaration_SourceSink.Type))
				{
					//do nothing
				}
				else
					throw new SemanticException(declaration_SourceSink.firstToken, "Type of Source and Declaration_Variable do not match");
			}
		}
		else
		{
			throw new SemanticException(declaration_SourceSink.firstToken, "Type Exception Occured at visitDeclaration_Variable");
		}
		return declaration_SourceSink;
		//throw new UnsupportedOperationException();
	}
	

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Expression e=declaration_Variable.e;
		if(e!=null)
			e=(Expression) e.visit(this, null);
		if(!symbolTable.containsKey(declaration_Variable.name))
		{
			symbolTable.put(declaration_Variable.name,declaration_Variable);
			//System.out.println(symbolTable.get(declaration_Variable.name));
			declaration_Variable.setType(TypeUtils.getType(declaration_Variable.type));
			if(e!=null)
			{
				
				if(!e.Type.equals(declaration_Variable.Type))
				{
					//System.out.println("Failed here");
					throw new SemanticException(declaration_Variable.firstToken, "Type Exception Occured at visitDeclaration_Variable");
				}
				
			}
		}
		else
		{
			throw new SemanticException(declaration_Variable.firstToken, "Type Exception Occured at visitDeclaration_Variable");
		}
		
		return declaration_Variable;
	}
	
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("In visitStatement_Assign "+ arg) ;
		Expression e=(Expression)statement_Assign.e;
		if(e!=null)
		{	e=(Expression)e.visit(this, null);
		//System.out.println("In visitStatement_Assign "+ arg) ;
		//else
		//	throw new SemanticException(statement_Assign.firstToken, "Type Exception Occured at visitStatement_Assign");
		LHS l=(LHS)statement_Assign.lhs;
		if(l!=null)
		{	l=(LHS)l.visit(this, null);
		//System.out.println(e.Type);
		//else
		//	throw new SemanticException(statement_Assign.firstToken, "Type Exception Occured at visitStatement_Assign");
		if(l.Type.equals(e.Type))
			statement_Assign.setCartesian(l.isCartesian);
		else
			throw new SemanticException(statement_Assign.firstToken, "Type Exception Occured at visitStatement_Assign");
		}
		}
		
		return statement_Assign;
		//throw new UnsupportedOperationException();
	}
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Source s=statement_In.source;
		if(s!=null)
		{	s=(Source)s.visit(this, null);
		//else
		//throw new SemanticException(statement_In.firstToken, "Type Exception Occured at visitStatement_In");
		if(symbolTable.get(statement_In.name).Type!=null)
		{
			statement_In.dec=symbolTable.get(statement_In.name);
			Object nameType=statement_In.dec.Type;
			if(nameType.equals(statement_In.source.Type))
				return statement_In;
			else
				throw new SemanticException(statement_In.firstToken, "Type Exception Occured at visitStatement_In");
		}
		else
			throw new SemanticException(statement_In.firstToken, "Type Exception Occured at visitStatement_In");
		}
		//throw new UnsupportedOperationException();
		return statement_In;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Sink s=(Sink)statement_Out.sink;
		if(s!=null)
		{	s=(Sink)s.visit(this, null);
		//else
		//	throw new SemanticException(statement_Out.firstToken, "Type Exception Occured at visitStatement_Out");
		if(symbolTable.get(statement_Out.name)!=null)
		{
			Declaration nameDec=symbolTable.get(statement_Out.name);
		Type nameType;
		statement_Out.setDec(nameDec);
		if(nameDec==null)
		{
			throw new SemanticException(statement_Out.firstToken, "Type Exception Occured at visitStatement_Out");	
		}
		else
			nameType=symbolTable.get(statement_Out.name).Type;
		if(((nameType.equals(Type.INTEGER)|| nameType.equals(Type.BOOLEAN)) && s.Type.equals(Type.SCREEN))
				|| (nameType.equals(Type.IMAGE) && (s.Type.equals(Type.FILE)||s.Type.equals(Type.SCREEN))))
		{
					//do nothing
		}
		else
			throw new SemanticException(statement_Out.firstToken, "Type Exception Occured at visitStatement_Out");
		}
		else
			throw new SemanticException(statement_Out.firstToken, "visitStatement_Out():Declaration not found in symbol table");
		}
		return statement_Out;
			
		//throw new UnsupportedOperationException();
	}
	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e0=expression_Binary.e0;
		if(e0!=null)
			e0=(Expression)e0.visit(this, null);
		//else 
		//	throw new SemanticException(expression_Binary.firstToken, "Type Exception Occured at visitExpression_Binary");
		Expression e1=expression_Binary.e1;
		if(e1!=null)
		{	e1=(Expression)e1.visit(this, null);
		//else
		//	throw new SemanticException(expression_Binary.firstToken, "Type Exception Occured at visitExpression_Binary");
			if(expression_Binary.op.equals(Kind.OP_EQ) || expression_Binary.op.equals(Kind.OP_NEQ))
				expression_Binary.Type=Type.BOOLEAN;
			else if(expression_Binary.op.equals(Kind.OP_GE)||expression_Binary.op.equals(Kind.OP_GT)
					||expression_Binary.op.equals(Kind.OP_LT) || expression_Binary.op.equals(Kind.OP_LE)
					&& e0.Type.equals(Type.INTEGER))
				expression_Binary.Type=Type.BOOLEAN;
			else if(expression_Binary.op.equals(Kind.OP_AND)|| expression_Binary.op.equals(Kind.OP_OR) 
					&& (e0.Type.equals(Type.INTEGER)|| e0.Type.equals(Type.BOOLEAN)))
				expression_Binary.Type=e0.Type;
			else if((expression_Binary.op.equals(Kind.OP_DIV)|| expression_Binary.op.equals(Kind.OP_MINUS)||
					expression_Binary.op.equals(Kind.OP_MOD)|| expression_Binary.op.equals(Kind.OP_PLUS)||
					expression_Binary.op.equals(Kind.OP_POWER)||expression_Binary.op.equals(Kind.OP_TIMES))
					&& (e0.Type.equals(Type.INTEGER)))
					expression_Binary.Type=Type.INTEGER;
			else 
				expression_Binary.Type=null;
			if(e0.Type.equals(e1.Type) && expression_Binary.Type!=null)
			{
				//do nothing
			}
			else
			{
				throw new SemanticException(expression_Binary.firstToken, "Type Exception Occured at visitExpression_Binary");
			}
		}
		return expression_Binary;
		//throw new UnsupportedOperationException();
	}
	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Expression conditionExpression=expression_Conditional.condition;
		Expression trueExpression=(Expression)expression_Conditional.trueExpression;
		Expression falseExpression=(Expression)expression_Conditional.falseExpression;
		if(conditionExpression!=null)
		{
			conditionExpression=(Expression)conditionExpression.visit(this, null);
		//else
		//	throw new SemanticException(expression_Conditional.firstToken, "Type Exception Occured at visitExpression_Binary");
		if(trueExpression!=null)
		{	trueExpression=(Expression)trueExpression.visit(this, null);
		//else
		//	throw new SemanticException(expression_Conditional.firstToken, "Type Exception Occured at visitExpression_Binary");
		if(falseExpression!=null)
		{	falseExpression=(Expression)falseExpression.visit(this, null);
		//System.out.println(falseExpression.Type);
		//else
		//	throw new SemanticException(expression_Conditional.firstToken, "Type Exception Occured at visitExpression_Binary");
		if(conditionExpression.Type.equals(Type.BOOLEAN) && 
				trueExpression.Type.equals(falseExpression.Type))
		{
			expression_Conditional.Type=trueExpression.Type;
			//conditionExpression.visit(this, null);
			//trueExpression.visit(this, null);
			//falseExpression.visit(this, null);
		}
		else
		{
			throw new SemanticException(expression_Conditional.firstToken, "Type Exception Occured at visitExpression_Conditional");
		}
		}}}
		return expression_Conditional;
			
		//throw new UnsupportedOperationException();
	}
	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e=(Expression)expression_FunctionAppWithExprArg.arg;
		if(e!=null)
		{	e=(Expression)e.visit(this, null);
		//else
		//	throw new SemanticException(expression_FunctionAppWithExprArg.firstToken,"Type Exception Occured at visitExpression_FunctionAppWithExprArg");
		if(e.Type.equals(Type.INTEGER))
		{	expression_FunctionAppWithExprArg.Type=Type.INTEGER;
			//e.visit(this, null);
		}
		else
			throw new SemanticException(expression_FunctionAppWithExprArg.firstToken,"Type Exception Occured at visitExpression_FunctionAppWithExprArg");
		//throw new UnsupportedOperationException();
		}
		return expression_FunctionAppWithExprArg;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("In visitExpression_FunctionAppWithIndexArg "+ expression_FunctionAppWithIndexArg.toString());
		Index i=(Index)expression_FunctionAppWithIndexArg.arg;
		if(i!=null)
			i=(Index)i.visit(this, null);
		expression_FunctionAppWithIndexArg.Type=Type.INTEGER;
		//throw new UnsupportedOperationException();
		return expression_FunctionAppWithIndexArg;
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		expression_IntLit.Type=Type.INTEGER;
		return expression_IntLit;
	}
	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//expression_PixelSelector.name
		if(symbolTable.get(expression_PixelSelector.name)!=null) {
		Type nameType=symbolTable.get(expression_PixelSelector.name).Type;
		if(nameType.equals(Type.IMAGE))
			expression_PixelSelector.Type=Type.INTEGER;
		else if(expression_PixelSelector.index==null)
			expression_PixelSelector.Type=nameType;
		else 
			expression_PixelSelector.Type=null;
		if(expression_PixelSelector.Type==null)
			throw new SemanticException(expression_PixelSelector.firstToken, "Type Exception Occured at visitExpression_PixelSelector");
		Index i=expression_PixelSelector.index;
		if(i!=null)
			i=(Index)i.visit(this, null);
		return expression_PixelSelector;
	}
		else
			throw new SemanticException(expression_PixelSelector.firstToken, "Type Exception Occured at visitExpression_PixelSelector");
		//throw new UnsupportedOperationException();
	}
	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		expression_PredefinedName.Type=Type.INTEGER;
		return expression_PredefinedName;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e=expression_Unary.e;
		if(e!=null) 
		{	e=(Expression)e.visit(this, null);
		//else
		//	throw new SemanticException(expression_Unary.firstToken, "Type Exception Occured at visitExpression_Unary");	
		Type t=e.Type;
		if(expression_Unary.op.equals(Kind.OP_EXCL) && (t.equals(Type.BOOLEAN)||t.equals(Type.INTEGER)))
			expression_Unary.Type=t;
		else if((expression_Unary.op.equals(Kind.OP_PLUS)||expression_Unary.op.equals(Kind.OP_MINUS))
				&& t.equals(Type.INTEGER))
			expression_Unary.Type=Type.INTEGER;
		else
			expression_Unary.Type=null;
		if(expression_Unary.Type==null)
			throw new SemanticException(expression_Unary.firstToken, "Type Exception Occured at visitExpression_Unary");
		//throw new UnsupportedOperationException();
		}
		return expression_Unary;
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		Expression e0=index.e0;
		if(e0!=null) 
		{	e0=(Expression)e0.visit(this, null);
		
		//else
		//	throw new SemanticException(index.firstToken, "Type Exception Occured at visitIndex");
		Expression e1=index.e1;
		if(e1!=null) 
		{	e1=(Expression)e1.visit(this, null);
		//System.out.println("Here "+e1.toString());
		//else
		//	throw new SemanticException(index.firstToken, "Type Exception Occured at visitIndex");
		//System.out.println(e1.Type);
		if(e0.Type.equals(Type.INTEGER) && e1.Type.equals(Type.INTEGER))
		{	index.setCartesian(!(e0.firstToken.kind==Kind.KW_r && e1.firstToken.kind==Kind.KW_a));
			//System.out.println(index.isCartesian());
		}
		else
			throw new SemanticException(index.firstToken, "Type Exception Occured at visitIndex");
		//throw new UnsupportedOperationException();
		//e0.visit(this, null);
		//e1.visit(this, null);
		}
		}
		return index;
	}
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		Index i=(Index)lhs.index;
		if(i!=null)
			i=(Index)i.visit(this, null);
		//System.out.println(symbolTable.get(lhs.name));
		if(symbolTable.get(lhs.name)!=null)
		{
			lhs.Decalaration=symbolTable.get(lhs.name);
			lhs.Type=lhs.Decalaration.Type;
			if(i!=null)
				lhs.setCartesian(i.isCartesian());
			else
				lhs.setCartesian(false);
		}
		else
			throw new SemanticException(lhs.firstToken, "visitLHS(): lhs name doesnt exist in symboltable");
		return lhs;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		if(symbolTable.get(sink_Ident.name)!=null) {
		sink_Ident.Type=symbolTable.get(sink_Ident.name).Type;
		if(!sink_Ident.Type.equals(Type.FILE))
			throw new SemanticException(sink_Ident.firstToken, "Type Exception Occured at visitSink_Ident");
		//throw new UnsupportedOperationException();
		return sink_Ident;
		}
		else
			throw new SemanticException(sink_Ident.firstToken, "visitSink_Ident(): name not found in symbol table");
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		sink_SCREEN.Type=Type.SCREEN;
		return sink_SCREEN;
		//throw new UnsupportedOperationException();
	}



	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		expression_BooleanLit.Type=Type.BOOLEAN;
		return expression_BooleanLit;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("here");
		if(symbolTable.get(expression_Ident.name)!=null)
			expression_Ident.Type=symbolTable.get(expression_Ident.name).Type;
		else
			throw new SemanticException(expression_Ident.firstToken,"Declaration not found in symbol table");
		return expression_Ident;
		//throw new UnsupportedOperationException();
	}
	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		Expression e=(Expression)source_CommandLineParam.paramNum;
		if(e!=null) 
		{	e=(Expression)e.visit(this, null);
		//else
		//	throw new SemanticException(source_CommandLineParam.firstToken,"visitSource_CommandLineParam():Expression e is null");
		source_CommandLineParam.Type=e.Type;
		//System.out.println(source_CommandLineParam.Type);
		if(!source_CommandLineParam.Type.equals(Type.INTEGER))
		{
			throw new SemanticException(source_CommandLineParam.firstToken,"Type Exception Occured at visitSource_Ident");
		}
		}
		return source_CommandLineParam;	
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("In visitSource_Ident: "+source_Ident);
		if(symbolTable.get(source_Ident.name)!=null)
		{
			source_Ident.Type=symbolTable.get(source_Ident.name).Type;
			if(source_Ident.Type.equals(Type.FILE)|| source_Ident.Type.equals(Type.URL))
				return source_Ident;
			else
				throw new SemanticException(source_Ident.firstToken,"Not a file or url");
		
		}
		else
		{
			//System.out.println("Here");
			throw new SemanticException(source_Ident.firstToken,"Type Exception Occured at visitSource_Ident");
		}
			//throw new UnsupportedOperationException();
	}
	

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
			  if(isValidURL(source_StringLiteral.fileOrUrl))
			  {
				  source_StringLiteral.setType(Type.URL);
			  }
			  else
			  { 
				  source_StringLiteral.setType(Type.FILE);
			  }
			  //System.out.println(source_StringLiteral.Type);
			  return source_StringLiteral;
	}
	
	public boolean isValidURL(String urlStr) {
	    try {
	      URI uri = new URI(urlStr);
	      return uri.getScheme().equals("http") || uri.getScheme().equals("https");
	    }
	    catch (Exception e) {
	        return false;
	    }
	}

}
