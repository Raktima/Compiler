package cop5556fa17;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556fa17.Scanner.Kind;
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
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
//import cop5556fa17.image.ImageFrame;
//mport cop5556fa17.image.ImageSupport;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}
	int DEF_X=256;
    int DEF_Y=256;
    int Z = 16777215;
    Map<Kind, Integer> map=new HashMap<>();
    
	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	Object initValue;
	String fieldName;
	FieldVisitor fv;
	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		   map.put(Kind.KW_x, 1);
		   map.put(Kind.KW_y, 2);
		   map.put(Kind.KW_X, 3);
		   map.put(Kind.KW_Y, 4);
		   map.put(Kind.KW_r, 5);
		   map.put(Kind.KW_R, 6);
		   map.put(Kind.KW_a, 7);
		   map.put(Kind.KW_A, 8);
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.name;
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();
		// add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// if GRADE, generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "entering main");

		// visit decs and statements to add field to class
		// and instructions to main method, respectivley
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		// generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "leaving main");

		// adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);

		// adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);


		// Sets max stack size and number of local vars.
		// Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the
		// constructor,
		// asm will calculate this itself and the parameters are ignored.
		// If you have trouble with failures in this routine, it may be useful
		// to temporarily set the parameter in the ClassWriter constructor to 0.
		// The generated classfile will not be correct, but you will at least be
		// able to see what is in it.
		mv.visitMaxs(0, 0);

		// terminate construction of main method
		mv.visitEnd();

		// terminate class construction
		cw.visitEnd();

		// generate classfile as byte array and return
		return cw.toByteArray();
	}
	
	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		
		String fieldName = declaration_Variable.name;
		String fieldType = "";
		if (declaration_Variable.Type.equals(Type.INTEGER)) {
			fieldType = "I";
			initValue=new Integer(0);

		} else if (declaration_Variable.Type.equals(Type.BOOLEAN)) {
			initValue=new Boolean(false);
			fieldType = "Z";
		}
		FieldVisitor fv = cw.visitField(ACC_STATIC, fieldName, fieldType, null, initValue);
		fv.visitEnd();
		if (declaration_Variable.e != null) {
			declaration_Variable.e.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Variable.name, fieldType);
		}
		return null;
	}
	
	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO HW6
		String fieldName=declaration_SourceSink.name;
		fv = cw.visitField(ACC_STATIC, fieldName, ImageSupport.StringDesc, null, null);	
		fv.visitEnd();
		
		if(declaration_SourceSink.source!=null)
		{
			declaration_SourceSink.source.visit(this, arg);
			
			//mv.visitFieldInsn(PUTSTATIC,className , declaration_SourceSink.name,fieldType );
			mv.visitFieldInsn(PUTSTATIC, className,declaration_SourceSink.name, ImageSupport.StringDesc);//<--change 3
			//System.out.println("Here");
		}
		
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {	
		Label ifTrueLabel = new Label();
		Label ifFalseLabel = new Label();
		if (expression_Binary.e0 != null)
			expression_Binary.e0.visit(this, arg);
		if (expression_Binary.e1 != null)
			expression_Binary.e1.visit(this, arg);
		
		if (expression_Binary.op.equals(Kind.OP_AND))
		{
			mv.visitInsn(IAND);
		}
		else if (expression_Binary.op.equals(Kind.OP_OR)) 
		{
			mv.visitInsn(IOR);
		}  
		else if (expression_Binary.op.equals(Kind.OP_EQ)) 
		{
			mv.visitJumpInsn(IF_ICMPEQ, ifTrueLabel);
			mv.visitLdcInsn(0);
			mv.visitJumpInsn(GOTO, ifFalseLabel);
			mv.visitLabel(ifTrueLabel);
			mv.visitLdcInsn(1);
			mv.visitLabel(ifFalseLabel);	
		} 
		else if (expression_Binary.op.equals(Kind.OP_NEQ)) 
		{
			mv.visitJumpInsn(IF_ICMPNE, ifTrueLabel);
			mv.visitLdcInsn(0);
			mv.visitJumpInsn(GOTO, ifFalseLabel);
			mv.visitLabel(ifTrueLabel);
			mv.visitLdcInsn(1);
			mv.visitLabel(ifFalseLabel);
		}  
		else if (expression_Binary.op.equals(Kind.OP_GT)) 
		{
			mv.visitJumpInsn(IF_ICMPGT, ifTrueLabel);
			mv.visitLdcInsn(0);
			mv.visitJumpInsn(GOTO, ifFalseLabel);
			mv.visitLabel(ifTrueLabel);
			mv.visitLdcInsn(1);
			mv.visitLabel(ifFalseLabel);
		} else if (expression_Binary.op.equals(Kind.OP_GE))
		{
			mv.visitJumpInsn(IF_ICMPGE, ifTrueLabel);
			mv.visitLdcInsn(0);
			mv.visitJumpInsn(GOTO, ifFalseLabel);
			mv.visitLabel(ifTrueLabel);
			mv.visitLdcInsn(1);
			mv.visitLabel(ifFalseLabel);
		} 
		else if (expression_Binary.op.equals(Kind.OP_LT)) 
		{
			mv.visitJumpInsn(IF_ICMPLT, ifTrueLabel);
			mv.visitLdcInsn(0);
			mv.visitJumpInsn(GOTO, ifFalseLabel);
			mv.visitLabel(ifTrueLabel);
			mv.visitLdcInsn(1);
			mv.visitLabel(ifFalseLabel);
		} else if (expression_Binary.op.equals(Kind.OP_LE)) 
		{
			mv.visitJumpInsn(IF_ICMPLE, ifTrueLabel);
			mv.visitLdcInsn(0);
			mv.visitJumpInsn(GOTO, ifFalseLabel);
			mv.visitLabel(ifTrueLabel);
			mv.visitLdcInsn(1);
			mv.visitLabel(ifFalseLabel);
		}
		else if (expression_Binary.op.equals(Kind.OP_MOD))
		{
			mv.visitInsn(IREM);
		}
		else if (expression_Binary.op.equals(Kind.OP_PLUS)) 
		{
			mv.visitInsn(IADD);
		} 
		else if (expression_Binary.op.equals(Kind.OP_MINUS))
		{
			mv.visitInsn(ISUB);
		}
		else if (expression_Binary.op.equals(Kind.OP_TIMES)) 
		{
			mv.visitInsn(IMUL);
		} 
		else if (expression_Binary.op.equals(Kind.OP_DIV)) 
		{
			mv.visitInsn(IDIV);
		} 
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Binary.Type);
		return null;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		if (expression_Unary.e != null)
			expression_Unary.e.visit(this, arg);
		
		if (expression_Unary.op.equals(Kind.OP_MINUS))
		{
			mv.visitInsn(INEG);
		} 
		else if (expression_Unary.op.equals(Kind.OP_EXCL))
		{
			if (expression_Unary.e.Type.equals(Type.BOOLEAN))
			{
				Label trueLabel = new Label();
				Label falseLabel = new Label();
				mv.visitJumpInsn(IFEQ, falseLabel);
				mv.visitLdcInsn(0);
				mv.visitJumpInsn(GOTO, trueLabel);
				mv.visitLabel(falseLabel);
				mv.visitLdcInsn(1);
				mv.visitLabel(trueLabel);
			}
			else if (expression_Unary.e.Type.equals(Type.INTEGER)) 
			{
				mv.visitLdcInsn(INTEGER.MAX_VALUE);
				mv.visitInsn(IXOR);
			} 
		}
		else if (expression_Unary.op.equals(Kind.OP_PLUS)) 
		{
		     //do nothing	
		} 
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.Type);
		return null;
	}


	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO HW6
		if(index.e0 != null) 
			index.e0.visit(this, arg);
		if(index.e1 != null) 
			index.e1.visit(this, arg);
			
		if(index.isCartesian())
		{
			// do nothing
		}
		else
		{
			mv.visitInsn(DUP2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig,false);
			mv.visitInsn(DUP_X2);
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig,false);
		}
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO HW6
		mv.visitFieldInsn(GETSTATIC, className,expression_PixelSelector.name , ImageSupport.ImageDesc);
		if(expression_PixelSelector.index!=null)
			expression_PixelSelector.index.visit(this, arg);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getPixel", ImageSupport.getPixelSig,false);
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {		
		Label trueCondition = new Label();
		Label falseCondition = new Label();
		
		if(expression_Conditional.condition!=null)
		expression_Conditional.condition.visit(this, arg);
		
		mv.visitJumpInsn(IFEQ, falseCondition);
		if(expression_Conditional.trueExpression!=null)
			expression_Conditional.trueExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO, trueCondition);
		
		mv.visitLabel(falseCondition);
		if(expression_Conditional.falseExpression!=null)
			expression_Conditional.falseExpression.visit(this, arg);
		
		mv.visitLabel(trueCondition);		
		// TODO
		 //CodeGenUtils.genLogTOS(GRADE, mv,expression_Conditional.trueExpression.Type);
		 return null;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		// TODO HW6
		FieldVisitor fv = cw.visitField(ACC_STATIC, declaration_Image.name, ImageSupport.ImageDesc, null, null);
		fv.visitEnd();
		if(declaration_Image.source!=null)
		{
			declaration_Image.source.visit(this, arg);
			if(declaration_Image.xSize==null && declaration_Image.ySize==null)
			{
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			else
			{
				declaration_Image.xSize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
				declaration_Image.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig,false);
		}
		else
		{
			if(declaration_Image.xSize==null && declaration_Image.ySize==null)
			{
				mv.visitLdcInsn(DEF_X);
				mv.visitLdcInsn(DEF_Y);
			}
			else
			{
				declaration_Image.xSize.visit(this, arg);
				//mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
				declaration_Image.ySize.visit(this, arg);
				//mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
			}
			 mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeImage", ImageSupport.makeImageSig,false);
		}
		mv.visitFieldInsn(PUTSTATIC, className, declaration_Image.name,ImageSupport.ImageDesc); 
		//System.out.println("In dec_image");
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		// TODO HW6
		//mv.visitFieldInsn(GETSTATIC, className, source_StringLiteral.fileOrUrl, "Ljava/lang/String");
		 mv.visitLdcInsn(source_StringLiteral.fileOrUrl); //<---change 1
		return null;
	}

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		mv.visitVarInsn(ALOAD, 0);
		source_CommandLineParam.paramNum.visit(this, arg);
		mv.visitInsn(AALOAD);
		return null;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		// TODO HW6
		//mv.visitFieldInsn(GETSTATIC, className, source_Ident.name, "Ljava/lang/String");
		mv.visitFieldInsn(GETSTATIC, className, source_Ident.name, ImageSupport.StringDesc); // <-- change 2
		return null;
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		mv.visitLdcInsn(expression_IntLit.value);
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
		return null;

	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		// TODO HW6
		if(expression_FunctionAppWithExprArg.arg!=null)
			expression_FunctionAppWithExprArg.arg.visit(this, arg);
		
		if(expression_FunctionAppWithExprArg.function.equals(Kind.KW_abs))
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"abs",RuntimeFunctions.absSig,false);
		else if(expression_FunctionAppWithExprArg.function.equals(Kind.KW_log))
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"log",RuntimeFunctions.absSig,false);
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		// TODO HW6
		if(expression_FunctionAppWithIndexArg.arg.e0!=null)
			expression_FunctionAppWithIndexArg.arg.e0.visit(this, arg);
		if(expression_FunctionAppWithIndexArg.arg.e1!=null)
			expression_FunctionAppWithIndexArg.arg.e1.visit(this, arg);
		if(expression_FunctionAppWithIndexArg.function.equals(Kind.KW_cart_x))
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig,false);
		
		else if(expression_FunctionAppWithIndexArg.function.equals(Kind.KW_cart_y))
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig,false);
		
		else if(expression_FunctionAppWithIndexArg.function.equals(Kind.KW_polar_a))
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig,false);
		
		else if(expression_FunctionAppWithIndexArg.function.equals(Kind.KW_polar_r))
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig,false);
		return null;
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO HW6	
		//System.out.println(expression_PredefinedName.firstToken);
		Kind kind=expression_PredefinedName.kind;
		if(kind.equals(Kind.KW_DEF_X))
			mv.visitLdcInsn(DEF_X);
		else if(kind.equals(Kind.KW_DEF_Y))
			mv.visitLdcInsn(DEF_Y);
		else if(kind.equals(Kind.KW_r))
		{
			mv.visitVarInsn(ILOAD,1 );
			mv.visitVarInsn(ILOAD,2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r",RuntimeFunctions.polar_rSig, false);
			mv.visitVarInsn(ISTORE,5);
			mv.visitVarInsn(ILOAD,5);
		}
		else if(kind.equals(Kind.KW_a))
		{
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a",RuntimeFunctions.polar_aSig, false);
			mv.visitVarInsn(ISTORE,7);
			mv.visitVarInsn(ILOAD,7);
		}
		else if (kind.equals(Kind.KW_Z))
			mv.visitLdcInsn(Z);
		else
			mv.visitVarInsn(ILOAD, map.get(expression_PredefinedName.kind));
		return null;
		//throw new UnsupportedOperationException();
	}

	/**
	 * For Integers and booleans, the only "sink"is the screen, so generate code
	 * to print to console. For Images, load the Image onto the stack and visit
	 * the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		if (statement_Out.dec.Type.equals(Type.INTEGER)) {
			 mv.visitFieldInsn(GETSTATIC, className,statement_Out.name, "I");
			 CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
			 mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println","(I)V", false); 
		} else if (statement_Out.dec.Type.equals(Type.BOOLEAN)) {
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, "Z");
			CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
			 mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println","(Z)V",false);
		}
		else if(statement_Out.dec.Type.equals(Type.IMAGE))
		{
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, ImageSupport.ImageDesc);
			CodeGenUtils.genLogTOS(GRADE, mv, Type.IMAGE);
			statement_Out.sink.visit(this, arg);
		}
		return null;
	}
	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 * 
	 * In HW5, you only need to handle INTEGER and BOOLEAN Use
	 * java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean to convert
	 * String to actual type.
	 * 
	 * TODO HW6 remaining types
	 */
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {		
		statement_In.source.visit(this, arg);
		
		if (statement_In.getDec().Type.equals(TypeUtils.Type.INTEGER)) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "I");
		}
		else if (statement_In.getDec().Type.equals(TypeUtils.Type.BOOLEAN)) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "Z");
		}
		else if(statement_In.dec.Type.equals(Type.IMAGE))
		{
			if(statement_In.source!=null)
			{
				Declaration_Image d=(Declaration_Image)statement_In.dec;
				if(d.xSize==null && d.ySize==null)
				{
					mv.visitInsn(ACONST_NULL);
					mv.visitInsn(ACONST_NULL);
				}
				else
				{
					d.xSize.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
					d.ySize.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
				}
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig,false);
				mv.visitFieldInsn(PUTSTATIC, className, statement_In.name,ImageSupport.ImageDesc);
			}
			
		}
		//System.out.println("In statement in");
		return null;
	}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	/* @Override
	 public Object visitStatement_Transform(Statement_Assign statement_Assign,
	 Object arg) throws Exception {
	 //TODO (see comment)
	 throw new UnsupportedOperationException();
	} */

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		if(lhs.Type.equals(TypeUtils.Type.INTEGER))
		 	mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "I");
		else if(lhs.Type.equals(TypeUtils.Type.BOOLEAN))
			 	mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "Z");
		else if(lhs.Type.equals(TypeUtils.Type.IMAGE)) 
		{				
			mv.visitFieldInsn(GETSTATIC, className, lhs.name, ImageSupport.ImageDesc);
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className,"setPixel", ImageSupport.setPixelSig, false);	    
		}
		return null;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		// TODO HW6
		mv.visitMethodInsn(INVOKESTATIC, ImageFrame.className,"makeFrame",ImageSupport.makeFrameSig,false);
		mv.visitInsn(POP);
		return null;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		// TODO HW6
		//mv.visitFieldInsn(GETSTATIC, className, sink_Ident.name, "Ljava/io/File;");
		mv.visitFieldInsn(GETSTATIC, className, sink_Ident.name, ImageSupport.StringDesc); //<-- another change
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className,"write",ImageSupport.writeSig,false);
		return null;
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		mv.visitLdcInsn(expression_BooleanLit.value);
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return null;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident, Object arg) throws Exception {
		if (expression_Ident.Type.equals(Type.INTEGER))	
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, "I");
		else if(expression_Ident.Type.equals(Type.BOOLEAN))
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, "Z");
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Ident.Type);
		return null;
	}
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		if(statement_Assign.lhs.Type.equals(Type.INTEGER) || statement_Assign.lhs.Type.equals(Type.BOOLEAN)) 
		{
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);
		}
		else if(statement_Assign.lhs.Type.equals(Type.IMAGE))
		{				
				boolean bool=statement_Assign.lhs.isCartesian();
				mv.visitFieldInsn(GETSTATIC,className, statement_Assign.lhs.name,ImageSupport.ImageDesc);
                mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
				mv.visitVarInsn(ISTORE, 3);
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
				mv.visitVarInsn(ISTORE, 4);
			
			    Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitInsn(ICONST_0);
				mv.visitVarInsn(ISTORE, 1);
				Label l1 = new Label();
				mv.visitLabel(l1);
				Label l2 = new Label();
				mv.visitJumpInsn(GOTO, l2);
				Label l3 = new Label();
				mv.visitLabel(l3);
				mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
				mv.visitInsn(ICONST_0);
				mv.visitVarInsn(ISTORE, 2);
				Label l4 = new Label();
				mv.visitLabel(l4);
				Label l5 = new Label();
				mv.visitJumpInsn(GOTO, l5);
				Label l6 = new Label();
				mv.visitLabel(l6);
				mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
				statement_Assign.e.visit(this, arg);
				statement_Assign.lhs.visit(this, arg);
				Label l7 = new Label();
				mv.visitLabel(l7);
				mv.visitIincInsn(2, 1);
				mv.visitLabel(l5);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitVarInsn(ILOAD, 2);
				mv.visitVarInsn(ILOAD, 4);
				mv.visitJumpInsn(IF_ICMPLT, l6);
				Label l8 = new Label();
				mv.visitLabel(l8);
				mv.visitIincInsn(1, 1);
				mv.visitLabel(l2);
				mv.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
				mv.visitVarInsn(ILOAD, 1);
				mv.visitVarInsn(ILOAD, 3);
				mv.visitJumpInsn(IF_ICMPLT, l3);	
			
		}
		return null;
	}
	

}
