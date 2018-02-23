package cop5556fa17;

import java.net.URL;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
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
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;

public class TypeCheckVisitor implements ASTVisitor {

	SymbolTable SymbolTable = new SymbolTable();

	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;

		public SemanticException(Token t, String message) {
			super("line " + t.line + " pos " + t.pos_in_line + ": " + message);
			this.t = t;
		}

	}

	/**
	 * The program name is only used for naming the class. It does not rule out
	 * variables with the same name. It is returned for convenience.
	 *
	 * @throws Exception
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node : program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		String message = "Error in Decl_Var";
		if (SymbolTable.lookupType(declaration_Variable.name) == null) {
			declaration_Variable.Type = TypeUtils.getType(declaration_Variable.type);
			if (declaration_Variable.e != null) {
				Object expressionType = declaration_Variable.e.visit(this, arg);
				if (declaration_Variable.Type.equals(expressionType)) {
					SymbolTable.insert(declaration_Variable.name, declaration_Variable);
					return declaration_Variable.Type;
				} else {
					throw new SemanticException(declaration_Variable.firstToken, message);
				}
			} else {
				SymbolTable.insert(declaration_Variable.name, declaration_Variable);
				return declaration_Variable.Type;
			}
		} else {
			throw new SemanticException(declaration_Variable.firstToken, message);
		}
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		String message = "Error in Exp_Binary";
		if (expression_Binary.e0 != null && expression_Binary.e1 != null) {
			Object e0Type = expression_Binary.e0.visit(this, arg);
			Object e1Type = expression_Binary.e1.visit(this, arg);
			if (e0Type.equals(e1Type)) {
				if (expression_Binary.op.equals(Kind.OP_EQ) || expression_Binary.op.equals(Kind.OP_NEQ)) {
					expression_Binary.Type = Type.BOOLEAN;
				} else if ((expression_Binary.op.equals(Kind.OP_GE) || expression_Binary.op.equals(Kind.OP_GT)
						|| expression_Binary.op.equals(Kind.OP_LT) || expression_Binary.op.equals(Kind.OP_LE))
						&& (e0Type.equals(Type.INTEGER))) {
					expression_Binary.Type = Type.BOOLEAN;
				} else if ((expression_Binary.op.equals(Kind.OP_AND) || expression_Binary.op.equals(Kind.OP_OR))
						&& (e0Type.equals(Type.INTEGER) || e0Type.equals(Type.BOOLEAN))) {
					expression_Binary.Type = (Type) e0Type;
				} else if ((expression_Binary.op.equals(Kind.OP_DIV) || expression_Binary.op.equals(Kind.OP_MINUS)
						|| expression_Binary.op.equals(Kind.OP_MOD) || expression_Binary.op.equals(Kind.OP_PLUS)
						|| expression_Binary.op.equals(Kind.OP_POWER) || expression_Binary.op.equals(Kind.OP_TIMES))
						&& (e0Type.equals(Type.INTEGER))) {
					expression_Binary.Type = Type.INTEGER;
				} else {
					throw new SemanticException(expression_Binary.firstToken, message);
				}
				return expression_Binary.Type;
			} else {
				throw new SemanticException(expression_Binary.firstToken, message);
			}
		} else {
			throw new SemanticException(expression_Binary.firstToken, message);
		}
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		String message = "Error in Exp_Unary";
		if (expression_Unary.e != null) {
			Object expType = expression_Unary.e.visit(this, arg);
			if (expression_Unary.op.equals(Kind.OP_EXCL)
					&& (expType.equals(Type.BOOLEAN) || expType.equals(Type.INTEGER))) {
				expression_Unary.Type = (Type) expType;
			} else if ((expression_Unary.op.equals(Kind.OP_PLUS) || expression_Unary.op.equals(Kind.OP_MINUS))
					&& expType.equals(Type.INTEGER)) {
				expression_Unary.Type = Type.INTEGER;
			} else {
				throw new SemanticException(expression_Unary.firstToken, message);
			}
			return expression_Unary.Type;
		} else {
			throw new SemanticException(expression_Unary.firstToken, message);
		}

	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		String message = "Error in Index";
		if (index.e0 != null && index.e1 != null) {
			Object e0Type = index.e0.visit(this, arg);
			Object e1Type = index.e1.visit(this, arg);
			if (e0Type.equals(Type.INTEGER) && e1Type.equals(Type.INTEGER)) {
				boolean boolVal;
				boolVal = !(index.e0.firstToken.kind.equals(Kind.KW_r) && index.e1.firstToken.kind.equals(Kind.KW_a));
				index.setCartesian(boolVal);
				return index.isCartesian();
			} else {
				throw new SemanticException(index.firstToken, message);
			}
		} else {
			throw new SemanticException(index.firstToken, message);
		}

	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		String message = "Error in Exp_PixelSelector";
		if (SymbolTable.lookupDec(expression_PixelSelector.name) != null) {
			SymbolTable.lookupDec(expression_PixelSelector.name).Type = SymbolTable
					.lookupType(expression_PixelSelector.name);
			if (expression_PixelSelector.index != null) {
				expression_PixelSelector.index.visit(this, arg);
			}
			if (SymbolTable.lookupDec(expression_PixelSelector.name).Type.equals(Type.IMAGE)) {
				expression_PixelSelector.Type = Type.INTEGER;
			} else if (expression_PixelSelector.index == null) {
				expression_PixelSelector.Type = SymbolTable.lookupDec(expression_PixelSelector.name).Type;
			} else {
				throw new SemanticException(expression_PixelSelector.firstToken, message);
			}
			return expression_PixelSelector.Type;
		} else {
			throw new SemanticException(expression_PixelSelector.firstToken, message);
		}

	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		String message = "Error in Exp_Conditional";
		if (expression_Conditional.condition != null && expression_Conditional.trueExpression != null
				&& expression_Conditional.falseExpression != null) {
			Object conditionType = expression_Conditional.condition.visit(this, arg);
			Object trueType = expression_Conditional.trueExpression.visit(this, arg);
			Object falseType = expression_Conditional.falseExpression.visit(this, arg);

			if (conditionType.equals(Type.BOOLEAN) && trueType.equals(falseType)) {
				expression_Conditional.Type = (Type) trueType;
				return expression_Conditional.Type;
			} else {
				throw new SemanticException(expression_Conditional.firstToken, message);
			}
		} else {
			throw new SemanticException(expression_Conditional.firstToken, message);
		}
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		String message = "Error in Decl_Image";
		if (SymbolTable.lookupType(declaration_Image.name) == null) {
			declaration_Image.Type = Type.IMAGE;
			if (declaration_Image.source != null) {
				declaration_Image.source.visit(this, arg);
			}
			if (declaration_Image.xSize != null) {
				Object xType = declaration_Image.xSize.visit(this, arg);
				if (declaration_Image.ySize != null && xType.equals(Type.INTEGER)
						&& (declaration_Image.ySize.visit(this, arg)).equals(Type.INTEGER)) {
					SymbolTable.insert(declaration_Image.name, declaration_Image);
					return declaration_Image.Type;
				} else {
					throw new SemanticException(declaration_Image.firstToken, message);
				}
			} else {
				SymbolTable.insert(declaration_Image.name, declaration_Image);
				return declaration_Image.Type;
			}
		} else {
			throw new SemanticException(declaration_Image.firstToken, message);
		}
	}

	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		if (isValidURL(source_StringLiteral.fileOrUrl)) {
			source_StringLiteral.Type = Type.URL;
		} else {
			source_StringLiteral.Type = Type.FILE;
		}
		return source_StringLiteral.Type;
	}

	/*
	 * @Override public Object
	 * visitSource_CommandLineParam(Source_CommandLineParam
	 * source_CommandLineParam, Object arg) throws Exception { String message =
	 * "Error in Source_CommandLineParam"; if (source_CommandLineParam.paramNum
	 * != null) { Object expType = source_CommandLineParam.paramNum.visit(this,
	 * arg); source_CommandLineParam.Type = (Type) expType; if
	 * (source_CommandLineParam.Type.equals(Type.INTEGER)) { return
	 * source_CommandLineParam.Type; } else { throw new
	 * SemanticException(source_CommandLineParam.firstToken, message); } } else
	 * { throw new SemanticException(source_CommandLineParam.firstToken,
	 * message); } }
	 */

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		String message = "Error in Source_CommandLineParam";
		if (source_CommandLineParam.paramNum != null) {
			Object expType = source_CommandLineParam.paramNum.visit(this, arg);
			source_CommandLineParam.Type = null;
			if (expType.equals(Type.INTEGER)) {
				return source_CommandLineParam.Type;
			} else {
				throw new SemanticException(source_CommandLineParam.firstToken, message);
			}
		} else {
			throw new SemanticException(source_CommandLineParam.firstToken, message);
		}
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		String message = "Error in Source_Ident";
		if (SymbolTable.lookupType(source_Ident.name) != null) {
			source_Ident.Type = SymbolTable.lookupType(source_Ident.name);
			if (source_Ident.Type.equals(Type.FILE) || source_Ident.Type.equals(Type.URL)) {
				return source_Ident.Type;
			} else {
				throw new SemanticException(source_Ident.firstToken, message);
			}
		} else {
			throw new SemanticException(source_Ident.firstToken, message);
		}
	}

	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		String message = "Error in Decl_SourceSink";
		if (SymbolTable.lookupType(declaration_SourceSink.name) == null) {
			if (declaration_SourceSink.type.equals(Kind.KW_url)) {
				declaration_SourceSink.Type = Type.URL;
			} else {
				declaration_SourceSink.Type = Type.FILE;
			}
			if (declaration_SourceSink.source != null) {
				Object sourceType = declaration_SourceSink.source.visit(this, arg);
				if (sourceType == null || declaration_SourceSink.Type.equals(sourceType)) {
					SymbolTable.insert(declaration_SourceSink.name, declaration_SourceSink);
					return declaration_SourceSink.Type;
				} else {
					throw new SemanticException(declaration_SourceSink.firstToken, message);
				}
			} else {
				throw new SemanticException(declaration_SourceSink.firstToken, message);
			}
		} else {
			throw new SemanticException(declaration_SourceSink.firstToken, message);
		}
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		expression_IntLit.Type = Type.INTEGER;
		return expression_IntLit.Type;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		String message = "Error in Exp_FunctionAppWithExprArg";
		if (expression_FunctionAppWithExprArg.arg != null) {
			Object argType = expression_FunctionAppWithExprArg.arg.visit(this, arg);
			if (argType.equals(Type.INTEGER)) {
				expression_FunctionAppWithExprArg.Type = Type.INTEGER;
				return expression_FunctionAppWithExprArg.Type;
			} else {
				throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, message);
			}
		} else {
			throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, message);
		}
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		String message = "Error in Exp_FunctionAppWithIndexArg";
		expression_FunctionAppWithIndexArg.Type = Type.INTEGER;
		if (expression_FunctionAppWithIndexArg.arg != null) {
			expression_FunctionAppWithIndexArg.arg.visit(this, arg);
		} else {
			throw new SemanticException(expression_FunctionAppWithIndexArg.firstToken, message);
		}
		return expression_FunctionAppWithIndexArg.Type;
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		expression_PredefinedName.Type = Type.INTEGER;
		return expression_PredefinedName.Type;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		String message = "Error in Statement_Out";
		Object SinkType;
		if (SymbolTable.lookupDec(statement_Out.name) != null) {
			statement_Out.setDec(SymbolTable.lookupDec(statement_Out.name));
			if (statement_Out.sink != null) {
				SinkType = statement_Out.sink.visit(this, arg);
			} else {
				throw new SemanticException(statement_Out.firstToken, message);
			}
			Object nameType = SymbolTable.lookupType(statement_Out.name);

			if (((nameType.equals(Type.INTEGER) || nameType.equals(Type.BOOLEAN)) && SinkType.equals(Type.SCREEN))
					|| (nameType.equals(Type.IMAGE) && (SinkType.equals(Type.FILE) || SinkType.equals(Type.SCREEN)))) {
				return statement_Out.getDec();
			} else {
				throw new SemanticException(statement_Out.firstToken, message);
			}
		} else {
			throw new SemanticException(statement_Out.firstToken, message);
		}
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		String message = "Error in Statement_In";
		Object sourceType;
		statement_In.setDec(SymbolTable.lookupDec(statement_In.name));
		if (statement_In.source != null) {
			sourceType = statement_In.source.visit(this, arg);
		} else {
			throw new SemanticException(statement_In.firstToken, message);
		}
		return statement_In.getDec();
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		String message = "Error in Statement_Assign";
		Object lhsType = null;
		Object eType = null;
		if (statement_Assign.lhs != null && statement_Assign.e != null) {
			lhsType = statement_Assign.lhs.visit(this, arg);
			eType = statement_Assign.e.visit(this, arg);
		} else {
			throw new SemanticException(statement_Assign.firstToken, message);
		}
		if (lhsType.equals(eType) || (lhsType.equals(Type.IMAGE) && eType.equals(Type.INTEGER))) {
			statement_Assign.setCartesian(statement_Assign.lhs.isCartesian());
			return statement_Assign.isCartesian();
		} else {
			throw new SemanticException(statement_Assign.firstToken, message);
		}
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		String message = "Error in visitLHS: LHS is null";
		if (lhs != null && lhs.name != null) {
			if (SymbolTable.lookupDec(lhs.name) != null) {
				lhs.setDec(SymbolTable.lookupDec(lhs.name));
				lhs.Type = lhs.getDec().Type;
				if (lhs.index != null) {
					boolean boolVal = (boolean) lhs.index.visit(this, arg);
					lhs.setCartesian(boolVal);
				} else {
					lhs.setCartesian(false);
				}
				return lhs.Type;
			} else {
				throw new SemanticException(lhs.firstToken, message);
			}
		} else {
			throw new SemanticException(lhs.firstToken, message);
		}

	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		sink_SCREEN.Type = Type.SCREEN;
		return sink_SCREEN.Type;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		String message = "Error in Sink_Ident";
		if (SymbolTable.lookupType(sink_Ident.name) != null) {
			sink_Ident.Type = SymbolTable.lookupType(sink_Ident.name);
			if (sink_Ident.Type.equals(Type.FILE)) {
				return sink_Ident.Type;
			} else {
				throw new SemanticException(sink_Ident.firstToken, message);
			}
		} else {
			throw new SemanticException(sink_Ident.firstToken, message);
		}

	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		expression_BooleanLit.Type = Type.BOOLEAN;
		return expression_BooleanLit.Type;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident, Object arg) throws Exception {
		String message = "Error in Expression_Ident";
		if (SymbolTable.lookupType(expression_Ident.name) != null) {
			expression_Ident.Type = SymbolTable.lookupType(expression_Ident.name);
			return expression_Ident.Type;
		} else {
			throw new SemanticException(expression_Ident.firstToken, message);
		}
	}

	public boolean isValidURL(String name) {
		try {
			new URL(name);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

}
