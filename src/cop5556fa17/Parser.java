package cop5556fa17;

import java.util.ArrayList;
import java.util.HashSet;

import cop5556fa17.Scanner.Token;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionApp;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Statement;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Sink;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;

import static cop5556fa17.Scanner.Kind.*;
import static cop5556fa17.Scanner.Kind;
import cop5556fa17.Parser.SyntaxException;

public class Parser {

	Scanner scanner;
	Token t;

	static HashSet<Kind> RaSelector = new HashSet<Kind>();
	static HashSet<Kind> XySelector = new HashSet<Kind>();
	static HashSet<Kind> LhsSelector = new HashSet<Kind>();
	static HashSet<Kind> FunctionName = new HashSet<Kind>();
	static HashSet<Kind> FunctionApplication = new HashSet<Kind>();
	static HashSet<Kind> Primary = new HashSet<Kind>();
	static HashSet<Kind> Lhs = new HashSet<Kind>();
	static HashSet<Kind> IdentOrPixelSelectorExpression = new HashSet<Kind>();
	static HashSet<Kind> UnaryExpressionNotPlusMinus = new HashSet<Kind>();
	static HashSet<Kind> UnaryExpression = new HashSet<Kind>();

	static HashSet<Kind> MultExpression = new HashSet<Kind>();
	static HashSet<Kind> AddExpression = new HashSet<Kind>();
	static HashSet<Kind> RelExpression = new HashSet<Kind>();
	static HashSet<Kind> EqExpression = new HashSet<Kind>();
	static HashSet<Kind> AndExpression = new HashSet<Kind>();
	static HashSet<Kind> OrExpression = new HashSet<Kind>();

	static HashSet<Kind> Expression = new HashSet<Kind>();

	static HashSet<Kind> AssignmentStatement = new HashSet<Kind>();

	static HashSet<Kind> ImageInStatement = new HashSet<Kind>();
	static HashSet<Kind> Sink = new HashSet<Kind>();
	static HashSet<Kind> ImageOutStatement = new HashSet<Kind>();
	static HashSet<Kind> Statement = new HashSet<Kind>();
	static HashSet<Kind> ImageDeclaration = new HashSet<Kind>();
	static HashSet<Kind> SourceSinkType = new HashSet<Kind>();
	static HashSet<Kind> Source = new HashSet<Kind>();
	static HashSet<Kind> SourceSinkDeclaration = new HashSet<Kind>();
	static HashSet<Kind> VarType = new HashSet<Kind>();
	static HashSet<Kind> VariableDeclaration = new HashSet<Kind>();

	static HashSet<Kind> Declaration = new HashSet<Kind>();
	static HashSet<Kind> Selector = new HashSet<Kind>();

	static {

		RaSelector.add(KW_r);
		XySelector.add(KW_x);
		LhsSelector.add(LSQUARE);

		FunctionName.add(KW_sin);
		FunctionName.add(KW_cos);
		FunctionName.add(KW_atan);
		FunctionName.add(KW_abs);
		FunctionName.add(KW_cart_x);
		FunctionName.add(KW_cart_y);
		FunctionName.add(KW_polar_a);
		FunctionName.add(KW_polar_r);

		FunctionApplication.addAll(FunctionName);

		Primary.add(INTEGER_LITERAL);
		Primary.add(LPAREN);
		Primary.addAll(FunctionApplication);
		Primary.add(BOOLEAN_LITERAL);

		Lhs.add(IDENTIFIER);

		IdentOrPixelSelectorExpression.add(IDENTIFIER);

		UnaryExpressionNotPlusMinus.add(OP_EXCL);
		UnaryExpressionNotPlusMinus.addAll(Primary);
		UnaryExpressionNotPlusMinus.addAll(IdentOrPixelSelectorExpression);
		UnaryExpressionNotPlusMinus.add(KW_x);
		UnaryExpressionNotPlusMinus.add(KW_y);
		UnaryExpressionNotPlusMinus.add(KW_r);
		UnaryExpressionNotPlusMinus.add(KW_a);
		UnaryExpressionNotPlusMinus.add(KW_X);
		UnaryExpressionNotPlusMinus.add(KW_Y);
		UnaryExpressionNotPlusMinus.add(KW_Z);
		UnaryExpressionNotPlusMinus.add(KW_A);
		UnaryExpressionNotPlusMinus.add(KW_R);
		UnaryExpressionNotPlusMinus.add(KW_DEF_X);
		UnaryExpressionNotPlusMinus.add(KW_DEF_Y);

		UnaryExpression.add(OP_PLUS);
		UnaryExpression.add(OP_MINUS);
		UnaryExpression.addAll(UnaryExpressionNotPlusMinus);

		MultExpression.addAll(UnaryExpression);
		AddExpression.addAll(MultExpression);
		RelExpression.addAll(AddExpression);
		EqExpression.addAll(RelExpression);
		AndExpression.addAll(EqExpression);
		OrExpression.addAll(AndExpression);

		Expression.addAll(OrExpression);

		AssignmentStatement.addAll(Lhs);

		ImageInStatement.add(IDENTIFIER);

		Sink.add(IDENTIFIER);
		Sink.add(KW_SCREEN);
		ImageOutStatement.add(IDENTIFIER);

		Statement.addAll(AssignmentStatement);
		Statement.addAll(ImageOutStatement);
		Statement.addAll(ImageInStatement);

		ImageDeclaration.add(KW_image);

		SourceSinkType.add(KW_url);
		SourceSinkType.add(KW_file);

		Source.add(STRING_LITERAL);
		Source.add(OP_AT);
		Source.add(IDENTIFIER);

		SourceSinkDeclaration.addAll(SourceSinkType);

		VarType.add(KW_int);
		VarType.add(KW_boolean);

		VariableDeclaration.addAll(VarType);

		Declaration.addAll(VariableDeclaration);
		Declaration.addAll(ImageDeclaration);
		Declaration.addAll(SourceSinkDeclaration);

		Selector.addAll(Expression);
	}

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input. Checks for EOF
	 *
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	/**
	 * Program ::= IDENTIFIER ( Declaration SEMI | Statement SEMI )*
	 * <p>
	 * Program is start symbol of our grammar.
	 *
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {
		Program progReturn = null;
		ArrayList<ASTNode> decsAndStatements = new ArrayList<ASTNode>();
		Token firstToken = t;
		if (t.kind.equals(IDENTIFIER)) {
			match();
			while ((Declaration.contains(t.kind) || Statement.contains(t.kind))) {
				if (Declaration.contains(t.kind)) {
					Declaration d = declaration();
					if (t.kind.equals(SEMI)) {
						match();
						decsAndStatements.add(d);
					} else {
						throwSyntaxException();
					}
				} else if (Statement.contains(t.kind)) {
					Statement s = statement();
					if (t.kind.equals(SEMI)) {
						match();
						decsAndStatements.add(s);
					} else {
						throwSyntaxException();
					}
				} else {
					throwSyntaxException();
				}
			}
		} else {
			throwSyntaxException();
		}
		progReturn = new Program(firstToken, firstToken, decsAndStatements);
		return progReturn;
	}

	public void throwSyntaxException() throws SyntaxException {
		throw new SyntaxException(t, "Invalid Token of the Kind: " + t.kind + " found at Line: " + t.line
				+ " at Position in Line: " + t.pos_in_line);
	}

	public Declaration declaration() throws SyntaxException {
		Declaration decReturn = null;
		if (VariableDeclaration.contains(t.kind)) {
			decReturn = variableDeclaration();
		} else if (ImageDeclaration.contains(t.kind)) {
			decReturn = imageDeclaration();
		} else if (SourceSinkDeclaration.contains(t.kind)) {
			decReturn = sourceSinkDeclaration();
		} else {
			throwSyntaxException();
		}
		return decReturn;
	}

	public Declaration sourceSinkDeclaration() throws SyntaxException {
		Declaration decReturn = null;
		Token firstToken = t, identName = null;
		Source s = null;
		if (SourceSinkType.contains(t.kind)) {
			sourceSinkType();
			if (t.kind.equals(IDENTIFIER)) {
				identName = t;
				match();
				if (t.kind.equals(OP_ASSIGN)) {
					match();
					if (Source.contains(t.kind)) {
						s = source();
						decReturn = new Declaration_SourceSink(firstToken, firstToken, identName, s);
					} else {
						throwSyntaxException();
					}
				} else {
					throwSyntaxException();
				}
			} else {
				throwSyntaxException();
			}
		} else {
			throwSyntaxException();
		}
		return decReturn;
	}

	public void sourceSinkType() throws SyntaxException {
		if (SourceSinkType.contains(t.kind)) {
			match();
		} else {
			throwSyntaxException();
		}
	}

	// ImageDeclaration ::= KW_image (LSQUARE Expression COMMA Expression
	// RSQUARE | E) IDENTIFIER ( OP_LARROW Source | E )

	public Declaration imageDeclaration() throws SyntaxException {
		Declaration decReturn = null;
		Token firstToken = t, identName = null;
		Expression e0 = null, e1 = null;
		Source s = null;
		if (ImageDeclaration.contains(t.kind)) {
			match();
			if (t.kind.equals(LSQUARE)) {
				match();
				if (Expression.contains(t.kind)) {
					e0 = expression();
					if (t.kind.equals(COMMA)) {
						match();
						if (Expression.contains(t.kind)) {
							e1 = expression();
							if (t.kind.equals(RSQUARE)) {
								match();
							} else {
								throwSyntaxException();
							}
						} else {
							throwSyntaxException();
						}
					} else {
						throwSyntaxException();
					}
				} else {
					throwSyntaxException();
				}
			}
			if (t.kind.equals(IDENTIFIER)) {
				identName = t;
				match();
				if (t.kind.equals(OP_LARROW)) {
					match();
					if (Source.contains(t.kind)) {
						s = source();
						decReturn = new Declaration_Image(firstToken, e0, e1, identName, s);
					} else {
						throwSyntaxException();
					}
				} else {
					decReturn = new Declaration_Image(firstToken, e0, e1, identName, s);
					return decReturn;
				}
			} else {
				throwSyntaxException();
			}
		} else {
			throwSyntaxException();
		}
		return decReturn;
	}

	public Source source() throws SyntaxException {
		Source sourceReturn = null;
		Token firstToken = t;
		if (t.kind.equals(STRING_LITERAL)) {
			sourceReturn = new Source_StringLiteral(firstToken, firstToken.getText());
			match();
		} else if (t.kind.equals(IDENTIFIER)) {
			sourceReturn = new Source_Ident(firstToken, firstToken);
			match();
		} else if (t.kind.equals(OP_AT)) {
			match();
			if (Expression.contains(t.kind)) {
				Expression e = expression();
				sourceReturn = new Source_CommandLineParam(firstToken, e);
			} else {
				throwSyntaxException();
			}
		} else {
			throwSyntaxException();
		}
		return sourceReturn;
	}

	public Declaration variableDeclaration() throws SyntaxException {
		Declaration decReturn = null;
		Token firstToken = t, identName = null;
		Expression e = null;
		// TODO Auto-generated method stub
		if (VarType.contains(t.kind)) {
			varType();
			if (t.kind.equals(IDENTIFIER)) {
				identName = t;
				match();
				if (t.kind.equals(OP_ASSIGN)) {
					match();
					if (Expression.contains(t.kind)) {
						e = expression();
						decReturn = new Declaration_Variable(firstToken, firstToken, identName, e);
					} else {
						throwSyntaxException();
					}
				} else {
					decReturn = new Declaration_Variable(firstToken, firstToken, identName, e);
					return decReturn;
				}
			} else {
				throwSyntaxException();
			}
		} else {
			throwSyntaxException();
		}
		return decReturn;
	}

	public void varType() throws SyntaxException {
		if (VarType.contains(t.kind)) {
			match();
		} else {
			throwSyntaxException();
		}
	}

	public void match() {
		// TODO Auto-generated method stub
		// System.out.println(t.kind);
		t = scanner.nextToken();

	}

	/**
	 * Expression ::= OrExpression OP_Q Expression OP_COLON Expression |
	 * OrExpression
	 * <p>
	 * Our test cases may invoke this routine directly to support incremental
	 * development.
	 *
	 * @throws SyntaxException
	 */
	Expression expression() throws SyntaxException {
		Expression e0 = null, e1 = null, e2 = null, exp = null;
		Token firstToken = t;
		if (OrExpression.contains(t.kind)) {
			e0 = orExpression();
			if (t.kind.equals(OP_Q)) {
				match();
				if (Expression.contains(t.kind)) {
					e1 = expression();
					if (t.kind.equals(OP_COLON)) {
						match();
						if (Expression.contains(t.kind)) {
							e2 = expression();
							exp = new Expression_Conditional(firstToken, e0, e1, e2);
						} else {
							throwSyntaxException();
						}
					} else {
						throwSyntaxException();
					}
				} else {
					throwSyntaxException();
				}
			} else {
				return e0;
			}
		} else {
			throwSyntaxException();
		}
		return exp;
	}

	public Expression orExpression() throws SyntaxException {
		Expression e0 = null, e1 = null;
		Token firstToken = t, op = null;
		if (AndExpression.contains(t.kind)) {
			e0 = andExpression();
			while (t.kind.equals(OP_OR)) {
				op = t;
				match();
				if (AndExpression.contains(t.kind)) {
					e1 = andExpression();
					e0 = new Expression_Binary(firstToken, e0, op, e1);
				} else {
					throwSyntaxException();
				}
			}
		} else {
			throwSyntaxException();
		}
		return e0;
	}

	public Expression andExpression() throws SyntaxException {
		Expression e0 = null, e1 = null;
		Token firstToken = t, op = null;
		if (EqExpression.contains(t.kind)) {
			e0 = eqExpression();
			while (t.kind.equals(OP_AND)) {
				op = t;
				match();
				if (EqExpression.contains(t.kind)) {
					e1 = eqExpression();
					e0 = new Expression_Binary(firstToken, e0, op, e1);
				} else {
					throwSyntaxException();
				}
			}
		} else {
			throwSyntaxException();
		}
		return e0;
	}

	public Expression eqExpression() throws SyntaxException {
		Expression e0 = null, e1 = null;
		Token firstToken = t, op = null;
		if (RelExpression.contains(t.kind)) {
			e0 = relExpression();
			while (t.kind.equals(OP_EQ) || t.kind.equals(OP_NEQ)) {
				op = t;
				match();
				if (RelExpression.contains(t.kind)) {
					e1 = relExpression();
					e0 = new Expression_Binary(firstToken, e0, op, e1);
				} else {
					throwSyntaxException();
				}
			}
		} else {
			throwSyntaxException();
		}
		return e0;
	}

	public Expression relExpression() throws SyntaxException {
		Expression e0 = null, e1 = null;
		Token firstToken = t, op = null;
		if (AddExpression.contains(t.kind)) {
			e0 = addExpression();
			while (t.kind.equals(OP_LT) || t.kind.equals(OP_GT) || t.kind.equals(OP_LE) || t.kind.equals(OP_GE)) {
				op = t;
				match();
				if (AddExpression.contains(t.kind)) {
					e1 = addExpression();
					e0 = new Expression_Binary(firstToken, e0, op, e1);
				} else {
					throwSyntaxException();
				}
			}
		} else {
			throwSyntaxException();
		}
		return e0;
	}

	public Expression addExpression() throws SyntaxException {
		Expression e0 = null, e1 = null;
		Token firstToken = t, op = null;
		if (MultExpression.contains(t.kind)) {
			e0 = multExpression();
			while (t.kind.equals(OP_PLUS) || t.kind.equals(OP_MINUS)) {
				op = t;
				match();
				if (MultExpression.contains(t.kind)) {
					e1 = multExpression();
					e0 = new Expression_Binary(firstToken, e0, op, e1);
				} else {
					throwSyntaxException();
				}
			}
		} else {
			throwSyntaxException();
		}
		return e0;
	}

	public Expression multExpression() throws SyntaxException {
		Expression e0 = null, e1 = null;
		Token firstToken = t, op = null;
		if (UnaryExpression.contains(t.kind)) {
			e0 = unaryExpression();
			while (t.kind.equals(OP_TIMES) || t.kind.equals(OP_DIV) || t.kind.equals(OP_MOD)) {
				op = t;
				match();
				if (UnaryExpression.contains(t.kind)) {
					e1 = unaryExpression();
					e0 = new Expression_Binary(firstToken, e0, op, e1);
				} else {
					throwSyntaxException();
				}
			}
		} else {
			throwSyntaxException();
		}
		return e0;
	}

	public Expression unaryExpression() throws SyntaxException {
		Expression e = null;
		Token firstToken = t;
		if (t.kind.equals(OP_PLUS) || t.kind.equals(OP_MINUS)) {
			match();
			if (UnaryExpression.contains(t.kind)) {
				Expression e1 = unaryExpression();
				e = new Expression_Unary(firstToken, firstToken, e1);
			} else {
				throwSyntaxException();
			}
		} else if (UnaryExpressionNotPlusMinus.contains(t.kind)) {
			e = unaryExpressionNotPlusMinus();
		} else {
			throwSyntaxException();
		}
		return e;
	}

	public Expression unaryExpressionNotPlusMinus() throws SyntaxException {
		Expression e = null;
		Token firstToken = t;

		if (UnaryExpressionNotPlusMinus.contains(t.kind)) {
			if (Primary.contains(t.kind)) {
				Expression e1 = primary();
				e = e1;
			} else if (IdentOrPixelSelectorExpression.contains(t.kind)) {
				Expression e1 = identOrPixelSelectorExpression();
				e = e1;
			} else if (t.kind.equals(OP_EXCL)) {
				match();
				if (UnaryExpression.contains(t.kind)) {
					Expression e1 = unaryExpression();
					e = new Expression_Unary(firstToken, firstToken, e1);
				} else {
					throwSyntaxException();
				}
			} else {
				e = new Expression_PredefinedName(firstToken, firstToken.kind);
				match();
			}
		} else {
			throwSyntaxException();
		}
		return e;
	}

	public Expression identOrPixelSelectorExpression() throws SyntaxException {
		Expression e = null;
		Token firstToken = t;
		Index i = null;

		if (t.kind.equals(IDENTIFIER)) {
			match();
			if (t.kind.equals(LSQUARE)) {
				match();
				if (Selector.contains(t.kind)) {
					i = selector();
					if (t.kind.equals(RSQUARE)) {
						match();
						e = new Expression_PixelSelector(firstToken, firstToken, i);
					} else {
						throwSyntaxException();
					}
				} else {
					throwSyntaxException();
				}
			} else {
				e = new Expression_Ident(firstToken, firstToken);
				return e;
			}
		} else {
			throwSyntaxException();
		}
		return e;
	}

	public Expression primary() throws SyntaxException {
		Expression e = null;
		Token firstToken = t;

		if (t.kind.equals(INTEGER_LITERAL)) {
			e = new Expression_IntLit(firstToken, firstToken.intVal());
			match();
		} else if (t.kind.equals(LPAREN)) {
			match();
			if (Expression.contains(t.kind)) {
				Expression e1 = expression();
				if (t.kind.equals(RPAREN)) {
					match();
					e = e1;
				} else {
					throwSyntaxException();
				}
			} else {
				throwSyntaxException();
			}
		} else if (FunctionApplication.contains(t.kind)) {
			Expression e1 = functionApplication();
			e = e1;
		} else if (t.kind.equals(BOOLEAN_LITERAL)) {
			boolean b;
			if (t.getText().equals("true")) {
				b = true;
			} else {
				b = false;
			}
			e = new Expression_BooleanLit(firstToken, b);
			match();
		} else {
			throwSyntaxException();
		}
		return e;
	}

	public Expression_FunctionApp functionApplication() throws SyntaxException {
		Expression_FunctionApp e = null;
		Token firstToken = t;

		if (FunctionName.contains(t.kind)) {
			functionName();
			if (t.kind.equals(LPAREN)) {
				match();
				if (Expression.contains(t.kind)) {
					Expression e1 = expression();
					if (t.kind.equals(RPAREN)) {
						match();
						e = new Expression_FunctionAppWithExprArg(firstToken, firstToken.kind, e1);
					} else {
						throwSyntaxException();
					}
				} else {
					throwSyntaxException();
				}
			} else if (t.kind.equals(LSQUARE)) {
				match();
				if (Selector.contains(t.kind)) {
					Index i = selector();
					if (t.kind.equals(RSQUARE)) {
						match();
						e = new Expression_FunctionAppWithIndexArg(firstToken, firstToken.kind, i);
					} else {
						throwSyntaxException();
					}
				} else {
					throwSyntaxException();
				}
			} else {
				throwSyntaxException();
			}
		} else {
			throwSyntaxException();
		}
		return e;
	}

	public Index selector() throws SyntaxException {
		Index i = null;
		Expression e0 = null, e1 = null;
		Token firstToken = t;
		if (Expression.contains(t.kind)) {
			e0 = expression();
			if (t.kind.equals(COMMA)) {
				match();
				if (Expression.contains(t.kind)) {
					e1 = expression();
					i = new Index(firstToken, e0, e1);
				} else {
					throwSyntaxException();
				}
			} else {
				throwSyntaxException();
			}
		} else {
			throwSyntaxException();
		}
		return i;
	}

	public void functionName() throws SyntaxException {
		if (FunctionName.contains(t.kind)) {
			match();
		} else {
			throwSyntaxException();
		}
	}

	public Statement statement() throws SyntaxException {
		Statement stmtReturn = null;
		if (ImageOutStatement.contains(t.kind) && scanner.peek().kind.equals(OP_RARROW)) {
			stmtReturn = imageOutStatement();
		} else if (ImageInStatement.contains(t.kind) && scanner.peek().kind.equals(OP_LARROW)) {
			stmtReturn = imageInStatement();
		} else if (AssignmentStatement.contains(t.kind)) {
			stmtReturn = assignmentStatement();
		}
		return stmtReturn;
	}

	public Statement imageInStatement() throws SyntaxException {
		Statement stmtInReturn = null;
		Token firstToken = t;
		if (t.kind.equals(IDENTIFIER)) {
			match();
			if (t.kind.equals(OP_LARROW)) {
				match();
				if (Source.contains(t.kind)) {
					Source s = source();
					stmtInReturn = new Statement_In(firstToken, firstToken, s);
				} else {
					throwSyntaxException();
				}
			} else {
				throwSyntaxException();
			}
		} else {
			throwSyntaxException();
		}
		return stmtInReturn;
	}

	public Statement assignmentStatement() throws SyntaxException {
		Statement stmtAssignReturn = null;
		Token firstToken = t;
		if (Lhs.contains(t.kind)) {
			LHS l = lhs();
			if (t.kind.equals(OP_ASSIGN)) {
				match();
				if (Expression.contains(t.kind)) {
					Expression e = expression();
					stmtAssignReturn = new Statement_Assign(firstToken, l, e);
				} else {
					throwSyntaxException();
				}
			} else {
				throwSyntaxException();
			}
		} else {
			throwSyntaxException();
		}
		return stmtAssignReturn;
	}

	public LHS lhs() throws SyntaxException {
		LHS l = null;
		Token firstToken = t;
		Index i = null;
		if (t.kind.equals(IDENTIFIER)) {
			match();
			if (t.kind.equals(LSQUARE)) {
				match();
				if (LhsSelector.contains(t.kind)) {
					i = lhsSelector();
					if (t.kind.equals(RSQUARE)) {
						match();
						l = new LHS(firstToken, firstToken, i);
					} else {
						throwSyntaxException();
					}
				} else {
					throwSyntaxException();
				}
			} else {
				l = new LHS(firstToken, firstToken, i);
				return l;
			}
		} else {
			throwSyntaxException();
		}
		return l;
	}

	public Index lhsSelector() throws SyntaxException {
		Index i = null;
		if (t.kind.equals(LSQUARE)) {
			match();
			if (XySelector.contains(t.kind)) {
				i = xySelector();
			} else if (RaSelector.contains(t.kind)) {
				i = raSelector();
			} else {
				throwSyntaxException();
			}
			if (t.kind.equals(RSQUARE)) {
				match();
			} else {
				throwSyntaxException();
			}
		} else {
			throwSyntaxException();
		}
		return i;
	}

	public Index xySelector() throws SyntaxException {
		Token firstToken = t;
		Index i = null;
		Expression e0 = null, e1 = null;
		if (t.kind.equals(KW_x)) {
			e0 = new Expression_PredefinedName(t, t.kind);
			match();
			if (t.kind.equals(COMMA)) {
				match();
				if (t.kind.equals(KW_y)) {
					e1 = new Expression_PredefinedName(t, t.kind);
					match();
					i = new Index(firstToken, e0, e1);
				} else {
					throwSyntaxException();
				}
			} else {
				throwSyntaxException();
			}
		} else {
			throwSyntaxException();
		}
		return i;
	}

	public Index raSelector() throws SyntaxException {
		Token firstToken = t;
		Index i = null;
		Expression e0 = null, e1 = null;
		if (t.kind.equals(KW_r)) {
			e0 = new Expression_PredefinedName(t, t.kind);
			match();
			if (t.kind.equals(COMMA)) {
				match();
				if (t.kind.equals(KW_a)) {
					e1 = new Expression_PredefinedName(t, t.kind);
					match();
					i = new Index(firstToken, e0, e1);
				} else {
					throwSyntaxException();
				}
			} else {
				throwSyntaxException();
			}
		} else {
			throwSyntaxException();
		}
		return i;
	}

	public Statement imageOutStatement() throws SyntaxException {
		Statement stmtOutReturn = null;
		Token firstToken = t;
		if (t.kind.equals(IDENTIFIER)) {
			match();
			if (t.kind.equals(OP_RARROW)) {
				match();
				if (Sink.contains(t.kind)) {
					Sink s = sink();
					stmtOutReturn = new Statement_Out(firstToken, firstToken, s);
				} else {
					throwSyntaxException();
				}
			} else {
				throwSyntaxException();
			}
		} else {
			throwSyntaxException();
		}
		return stmtOutReturn;
	}

	public Sink sink() throws SyntaxException {
		Sink sinkReturn = null;
		Token firstToken = t;
		if (Sink.contains(t.kind)) {
			if (t.kind.equals(IDENTIFIER)) {
				sinkReturn = new Sink_Ident(firstToken, firstToken);
			} else if (t.kind.equals(KW_SCREEN)) {
				sinkReturn = new Sink_SCREEN(firstToken);
			}
			match();
		} else {
			throwSyntaxException();
		}
		return sinkReturn;
	}

	/**
	 * Only for check sat end of program. Does not "consume" EOF so no attempt
	 * to get nonexistent next Token.
	 *
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message = "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}
}