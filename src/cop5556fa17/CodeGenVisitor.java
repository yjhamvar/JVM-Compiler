package cop5556fa17;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
import cop5556fa17.Scanner.Kind;
import cop5556fa17.AST.Statement_Assign;
//import cop5556fa17.image.ImageFrame;
//import cop5556fa17.image.ImageSupport;

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

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
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
		// CodeGenUtils.genLog(GRADE, mv, "entering main");

		cw.visitField(ACC_STATIC, "x", "I", null, null);
		cw.visitField(ACC_STATIC, "y", "I", null, null);
		cw.visitField(ACC_STATIC, "X", "I", null, null);
		cw.visitField(ACC_STATIC, "Y", "I", null, null);
		cw.visitField(ACC_STATIC, "r", "I", null, null);
		cw.visitField(ACC_STATIC, "a", "I", null, null);
		cw.visitField(ACC_STATIC, "R", "I", null, null);
		cw.visitField(ACC_STATIC, "A", "I", null, null);
		cw.visitField(ACC_STATIC, "Z", "I", null, 16777215);
		cw.visitField(ACC_STATIC, "DEF_X", "I", null, 256);
		cw.visitField(ACC_STATIC, "DEF_Y", "I", null, 256);

		// visit decs and statements to add field to class
		// and instructions to main method, respectivley
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		// generates code to add string to log
		// CodeGenUtils.genLog(GRADE, mv, "leaving main");

		// adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);

		// adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);

		// handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);

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
		String decType = new String();
		if (declaration_Variable.Type.equals(Type.INTEGER)) {
			decType = "I";
		} else if (declaration_Variable.Type.equals(Type.BOOLEAN)) {
			decType = "Z";
		}
		int initValue = 0;
		FieldVisitor fv = cw.visitField(ACC_STATIC, declaration_Variable.name, decType, null, initValue);
		fv.visitEnd();

		if (declaration_Variable.e != null) {
			declaration_Variable.e.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Variable.name, decType);
		}
		return null;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		expression_Binary.e0.visit(this, arg);
		expression_Binary.e1.visit(this, arg);

		Label gotTrue = new Label();
		Label gotFalse = new Label();

		if (expression_Binary.op.equals(Kind.OP_OR)) {
			mv.visitInsn(IOR);
		} else if (expression_Binary.op.equals(Kind.OP_AND)) {
			mv.visitInsn(IAND);
		} else if (expression_Binary.op.equals(Kind.OP_EQ)) {
			mv.visitJumpInsn(IF_ICMPEQ, gotTrue);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, gotFalse);

			mv.visitLabel(gotTrue);
			mv.visitInsn(ICONST_1);

			mv.visitLabel(gotFalse);
		} else if (expression_Binary.op.equals(Kind.OP_NEQ)) {
			mv.visitJumpInsn(IF_ICMPNE, gotTrue);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, gotFalse);

			mv.visitLabel(gotTrue);
			mv.visitInsn(ICONST_1);

			mv.visitLabel(gotFalse);

		} else if (expression_Binary.op.equals(Kind.OP_LT)) {
			mv.visitJumpInsn(IF_ICMPLT, gotTrue);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, gotFalse);

			mv.visitLabel(gotTrue);
			mv.visitInsn(ICONST_1);

			mv.visitLabel(gotFalse);

		} else if (expression_Binary.op.equals(Kind.OP_GT)) {
			mv.visitJumpInsn(IF_ICMPGT, gotTrue);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, gotFalse);

			mv.visitLabel(gotTrue);
			mv.visitInsn(ICONST_1);

			mv.visitLabel(gotFalse);

		} else if (expression_Binary.op.equals(Kind.OP_LE)) {
			mv.visitJumpInsn(IF_ICMPLE, gotTrue);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, gotFalse);

			mv.visitLabel(gotTrue);
			mv.visitInsn(ICONST_1);

			mv.visitLabel(gotFalse);

		} else if (expression_Binary.op.equals(Kind.OP_GE)) {
			mv.visitJumpInsn(IF_ICMPGE, gotTrue);
			mv.visitInsn(ICONST_0);
			mv.visitJumpInsn(GOTO, gotFalse);

			mv.visitLabel(gotTrue);
			mv.visitInsn(ICONST_1);

			mv.visitLabel(gotFalse);
		} else if (expression_Binary.op.equals(Kind.OP_PLUS)) {
			mv.visitInsn(IADD);
		} else if (expression_Binary.op.equals(Kind.OP_MINUS)) {
			mv.visitInsn(ISUB);
		} else if (expression_Binary.op.equals(Kind.OP_TIMES)) {
			mv.visitInsn(IMUL);
		} else if (expression_Binary.op.equals(Kind.OP_DIV)) {
			mv.visitInsn(IDIV);
		} else if (expression_Binary.op.equals(Kind.OP_MOD)) {
			mv.visitInsn(IREM);
		}

		// CodeGenUtils.genLogTOS(GRADE, mv, expression_Binary.Type);
		return null;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		if (expression_Unary.e != null) {
			expression_Unary.e.visit(this, arg);
		}
		if (expression_Unary.op.equals(Kind.OP_MINUS)) {
			mv.visitInsn(INEG);
		} else if (expression_Unary.op.equals(Kind.OP_EXCL)) {
			if (expression_Unary.e.Type.equals(Type.INTEGER)) {
				mv.visitLdcInsn(INTEGER.MAX_VALUE);
				mv.visitInsn(IXOR);
			} else if (expression_Unary.e.Type.equals(Type.BOOLEAN)) {
				Label gotZero = new Label();
				Label gotOne = new Label();

				mv.visitJumpInsn(IFEQ, gotZero);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, gotOne);

				mv.visitLabel(gotZero);
				mv.visitInsn(ICONST_1);

				mv.visitLabel(gotOne);
			}
		}
		// CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.Type);
		return null;
	}

	// generate code to leave the two values on the stack
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		if (index.e0 != null) {
			index.e0.visit(this, arg);
		}
		if (index.e1 != null) {
			index.e1.visit(this, arg);
		}
		if (!index.isCartesian()) {
			mv.visitInsn(DUP2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig, false);
			mv.visitInsn(DUP_X2);
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig, false);
		}
		return null;
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		mv.visitFieldInsn(GETSTATIC, className, expression_PixelSelector.name, "Ljava/awt/image/BufferedImage;");
		if (expression_PixelSelector.index != null) {
			expression_PixelSelector.index.visit(this, arg);
		}
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getPixel", ImageSupport.getPixelSig, false);
		return null;
	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		if (expression_Conditional.condition != null) {
			expression_Conditional.condition.visit(this, arg);

			Label gotTrue = new Label();
			Label gotFalse = new Label();

			mv.visitJumpInsn(IFEQ, gotFalse);
			expression_Conditional.trueExpression.visit(this, arg);
			mv.visitJumpInsn(GOTO, gotTrue);

			mv.visitLabel(gotFalse);
			expression_Conditional.falseExpression.visit(this, arg);

			mv.visitLabel(gotTrue);
		}
		return null;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		cw.visitField(ACC_STATIC, declaration_Image.name, ImageSupport.ImageDesc, null, null);
		if (declaration_Image.source != null) {
			declaration_Image.source.visit(this, arg);
			if (declaration_Image.xSize != null) {
				declaration_Image.xSize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				declaration_Image.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			} else {
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Image.name, ImageSupport.ImageDesc);
		} else {
			if (declaration_Image.xSize != null) {
				declaration_Image.xSize.visit(this, arg);
				declaration_Image.ySize.visit(this, arg);
			} else {
				mv.visitFieldInsn(GETSTATIC, className, "DEF_X", "I");
				mv.visitFieldInsn(GETSTATIC, className, "DEF_Y", "I");
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeImage", ImageSupport.makeImageSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Image.name, "Ljava/awt/image/BufferedImage;");
		}
		return null;
	}

	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		mv.visitLdcInsn(source_StringLiteral.fileOrUrl);
		return null;
	}

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {

		mv.visitVarInsn(ALOAD, 0);
		if (source_CommandLineParam.paramNum != null) {
			source_CommandLineParam.paramNum.visit(this, arg);
		}
		mv.visitInsn(AALOAD);
		return null;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		mv.visitFieldInsn(GETSTATIC, className, source_Ident.name, "Ljava/lang/String;");
		return null;
	}

	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {

		cw.visitField(ACC_STATIC, declaration_SourceSink.name, "Ljava/lang/String;", null, null);
		if (declaration_SourceSink.source != null) {
			declaration_SourceSink.source.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_SourceSink.name, "Ljava/lang/String;");
		}
		return null;
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		mv.visitLdcInsn(expression_IntLit.value);
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		if (expression_FunctionAppWithExprArg.arg != null) {
			expression_FunctionAppWithExprArg.arg.visit(this, arg);
		}
		if (expression_FunctionAppWithExprArg.function.equals(Kind.KW_abs)) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "abs", RuntimeFunctions.absSig, false);
		} else if (expression_FunctionAppWithExprArg.function.equals(Kind.KW_log)) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "log", RuntimeFunctions.logSig, false);
		}
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		if (expression_FunctionAppWithIndexArg.arg.e0 != null) {
			expression_FunctionAppWithIndexArg.arg.e0.visit(this, arg);
		}
		if (expression_FunctionAppWithIndexArg.arg.e1 != null) {
			expression_FunctionAppWithIndexArg.arg.e1.visit(this, arg);
		}

		if (expression_FunctionAppWithIndexArg.function.equals(Kind.KW_cart_x)) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig, false);
		} else if (expression_FunctionAppWithIndexArg.function.equals(Kind.KW_cart_y)) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig, false);
		} else if (expression_FunctionAppWithIndexArg.function.equals(Kind.KW_polar_a)) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
		} else if (expression_FunctionAppWithIndexArg.function.equals(Kind.KW_polar_r)) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
		}
		return null;
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		String predefName = new String();
		if (expression_PredefinedName.kind.equals(Kind.KW_x)) {
			predefName = "x";
		} else if (expression_PredefinedName.kind.equals(Kind.KW_y)) {
			predefName = "y";
		} else if (expression_PredefinedName.kind.equals(Kind.KW_r)) {
			predefName = "r";
		} else if (expression_PredefinedName.kind.equals(Kind.KW_a)) {
			predefName = "a";
		} else if (expression_PredefinedName.kind.equals(Kind.KW_X)) {
			predefName = "X";
		} else if (expression_PredefinedName.kind.equals(Kind.KW_Y)) {
			predefName = "Y";
		} else if (expression_PredefinedName.kind.equals(Kind.KW_R)) {
			predefName = "R";
		} else if (expression_PredefinedName.kind.equals(Kind.KW_A)) {
			predefName = "A";
		} else if (expression_PredefinedName.kind.equals(Kind.KW_Z)) {
			predefName = "Z";
		} else if (expression_PredefinedName.kind.equals(Kind.KW_DEF_X)) {
			predefName = "DEF_X";
		} else if (expression_PredefinedName.kind.equals(Kind.KW_DEF_Y)) {
			predefName = "DEF_Y";
		}
		mv.visitFieldInsn(GETSTATIC, className, predefName, "I");
		return null;
	}

	/**
	 * For Integers and booleans, the only "sink"is the screen, so generate code
	 * to print to console. For Images, load the Image onto the stack and visit
	 * the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		// TODO in HW5: only INTEGER and BOOLEAN
		// TODO HW6 remaining cases
		Type decType = statement_Out.getDec().Type;
		if (decType.equals(Type.INTEGER)) {

			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, "I");
			CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);

		} else if (decType.equals(Type.BOOLEAN)) {

			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, "Z");
			CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);

		} else if (decType.equals(Type.IMAGE)) {
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, "Ljava/awt/image/BufferedImage;");
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
		// TODO (see comment )
		statement_In.source.visit(this, arg);
		Type decType = statement_In.getDec().Type;

		if (decType.equals(Type.INTEGER)) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "I");
		} else if (decType.equals(Type.BOOLEAN)) {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "Z");
		} else if (decType.equals(Type.IMAGE)) {
			Declaration_Image declImage = (Declaration_Image) statement_In.getDec();
			if (declImage.xSize == null) {
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			} else {
				declImage.xSize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				declImage.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, declImage.name, ImageSupport.ImageDesc);
		}
		return null;
	}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	/*
	 * @Override public Object visitStatement_Transform(Statement_Assign
	 * statement_Assign, Object arg) throws Exception { //TODO (see comment)
	 * throw new UnsupportedOperationException(); }
	 */

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		if (lhs.Type.equals(Type.INTEGER)) {
			mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "I");
		} else if (lhs.Type.equals(Type.BOOLEAN)) {
			mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "Z");
		} else if (lhs.Type.equals(Type.IMAGE)) {
			mv.visitFieldInsn(GETSTATIC, className, lhs.name, "Ljava/awt/image/BufferedImage;");
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "setPixel", ImageSupport.setPixelSig, false);
		}
		return null;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		mv.visitMethodInsn(INVOKESTATIC, ImageFrame.className, "makeFrame", ImageSupport.makeFrameSig, false);
		mv.visitInsn(POP);
		return null;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		mv.visitFieldInsn(GETSTATIC, className, sink_Ident.name, "Ljava/lang/String;");
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "write", ImageSupport.writeSig, false);
		return null;
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {

		mv.visitLdcInsn(expression_BooleanLit.value);
		// CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return null;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident, Object arg) throws Exception {
		if (expression_Ident.Type.equals(Type.BOOLEAN)) {
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, "Z");
		} else {
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, "I");
		}
		return null;
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		if (statement_Assign.lhs.Type.equals(Type.BOOLEAN) || statement_Assign.lhs.Type.equals(Type.INTEGER)) {
			if (statement_Assign.e != null)
				statement_Assign.e.visit(this, arg);
			if (statement_Assign.lhs != null)
				statement_Assign.lhs.visit(this, arg);
		} else {
			Label OuterCondition = new Label();
			Label OuterLoopInc = new Label();
			Label InnerLoop = new Label();
			Label InnerLoopCondition = new Label();
			Label InnerLoopInc = new Label();
			Label formula = new Label();

			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "X", "I");
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "Y", "I");

			mv.visitInsn(ICONST_0);
			mv.visitFieldInsn(PUTSTATIC, className, "x", "I");
			mv.visitJumpInsn(GOTO, OuterCondition);
			mv.visitLabel(InnerLoop);
			mv.visitInsn(ICONST_0);
			mv.visitFieldInsn(PUTSTATIC, className, "y", "I");
			mv.visitJumpInsn(GOTO, InnerLoopCondition);
			mv.visitLabel(formula);

			if (!statement_Assign.isCartesian()) {
				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig,
						false);
				mv.visitFieldInsn(PUTSTATIC, className, "r", "I");

				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig,
						false);
				mv.visitFieldInsn(PUTSTATIC, className, "a", "I");

			}
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);

			mv.visitLabel(InnerLoopInc);
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitFieldInsn(PUTSTATIC, className, "y", "I");

			mv.visitLabel(InnerLoopCondition);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
			mv.visitJumpInsn(IF_ICMPLT, formula);

			mv.visitLabel(OuterLoopInc);
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitFieldInsn(PUTSTATIC, className, "x", "I");

			mv.visitLabel(OuterCondition);
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
			mv.visitJumpInsn(IF_ICMPLT, InnerLoop);
		}
		return null;
	}
}
