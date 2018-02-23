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
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = ""; // The input is the empty string. Parsing should fail
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
														// initialize it
		show(scanner); // Display the tokens
		Parser parser = new Parser(scanner); // Create a parser
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast = parser.parse();
			; // Parse the program, which should throw an exception
		} catch (SyntaxException e) {
			show(e); // catch the exception and show it
			throw e; // rethrow for Junit
		}
	}

	@Test
	public void testNameOnly() throws LexicalException, SyntaxException {
		String input = "prog"; // Legal program with only a name
		show(input); // display input
		Scanner scanner = new Scanner(input).scan(); // Create scanner and
														// create token list
		show(scanner); // display the tokens
		Parser parser = new Parser(scanner); // create parser
		Program ast = parser.parse(); // parse program and get AST
		show(ast); // Display the AST
		assertEquals(ast.name, "prog"); // Check the name field in the Program
										// object
		assertTrue(ast.decsAndStatements.isEmpty()); // Check the
														// decsAndStatements
														// list in the Program
														// object. It should be
														// empty.
	}

	@Test
	public void testDec1() throws LexicalException, SyntaxException {
		String input = "prog int k;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		// This should have one Declaration_Variable object, which is at
		// position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements.get(0);
		assertEquals(KW_int, dec.type.kind);
		assertEquals("k", dec.name);
		assertNull(dec.e);
	}

	// Srishti

	@Test
	public void exp1() throws SyntaxException, LexicalException {
		String input = "Z-old";
		Expression e = null;
		try {
			e = (new Parser(new Scanner(input).scan())).expression();
		} catch (cop5556fa17.Parser.SyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		show(e);
		assertEquals(Expression_Binary.class, e.getClass());
		Expression_Binary ebin = (Expression_Binary) e;
		assertEquals(Expression_PredefinedName.class, ebin.e0.getClass());
		assertEquals(KW_Z, ((Expression_PredefinedName) (ebin.e0)).kind);
		assertEquals(Expression_Ident.class, ebin.e1.getClass());
		assertEquals("old", ((Expression_Ident) (ebin.e1)).name);
		assertEquals(OP_MINUS, ebin.op);
	}

	@Test
	public void expression11() throws SyntaxException, LexicalException {
		String input = "2";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = null;
		try {
			exp = parser.expression();
		} catch (cop5556fa17.Parser.SyntaxException e1) {
			System.out.println("Syntax Exception for input= " + input);
		}
		show("Expression Returned= " + exp);
		assertEquals(Expression_IntLit.class, exp.getClass());
		Expression_IntLit exp_intLit = (Expression_IntLit) exp;
		assertEquals(2, exp_intLit.value);
	}

	/**
	 * Selector ::= Expression COMMA Expression function first
	 */
	@Test
	public void selector1() throws SyntaxException, LexicalException {
		String input = "++++x , ++++x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);

		Index index = parser.selector();

		assertEquals(Expression_Unary.class, index.e0.getClass());
		assertEquals(Expression_Unary.class, index.e1.getClass());
		assertEquals(OP_PLUS, ((Expression_Unary) (index.e0)).op);
		assertEquals(Expression_Unary.class, ((Expression_Unary) (index.e0)).e.getClass());
		assertEquals(KW_x,
				((Expression_PredefinedName) ((Expression_Unary) ((Expression_Unary) ((Expression_Unary) ((Expression_Unary) (index.e0)).e).e).e).e).kind);
	}

	/**
	 * RaSelector ::= KW_r COMMA KW_A
	 */
	@Test
	public void testRaSelector1() throws SyntaxException, LexicalException {
		String input = "r , a";
		// String input = "r , AB";
		// String input = "r ; A";
		// String input = "R , A";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);

		Index index = parser.raSelector();

		show(index);
		assertEquals(Expression_PredefinedName.class, index.e0.getClass());
		assertEquals(Expression_PredefinedName.class, index.e1.getClass());
		assertEquals(KW_r, ((Expression_PredefinedName) index.e0).kind);
		assertEquals(KW_a, ((Expression_PredefinedName) index.e1).kind);
	}

	/**
	 * XySelector ::= KW_x COMMA KW_y
	 */
	@Test
	public void xySelector1() throws SyntaxException, LexicalException {
		String input = "x,y,x,y";
		// String input = "x , y";
		// String input = "xv,y";
		// String input = "x,yb";
		// String input = "x y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);

		Index index = parser.xySelector();

		show(index);
		assertEquals(Expression_PredefinedName.class, index.e0.getClass());
		assertEquals(Expression_PredefinedName.class, index.e1.getClass());
		assertEquals(KW_x, ((Expression_PredefinedName) index.e0).kind);
		assertEquals(KW_y, ((Expression_PredefinedName) index.e1).kind);
	}

	/**
	 * LhsSelector ::= LSQUARE ( XySelector | RaSelector ) RSQUARE
	 */
	@Test
	public void lhsSelector1() throws SyntaxException, LexicalException {
		String input = "[x,y]";
		// String input = "[r,A]";
		// String input = "[r,y]";
		// String input = "[][]";
		// String input = "[x,y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);

		Index index = parser.lhsSelector();

		show(index);
		assertEquals(Expression_PredefinedName.class, index.e0.getClass());
		assertEquals(Expression_PredefinedName.class, index.e1.getClass());
		assertEquals(KW_x, ((Expression_PredefinedName) index.e0).kind);
		assertEquals(KW_y, ((Expression_PredefinedName) index.e1).kind);
	}

	/**
	 * FunctionName ::= KW_sin | KW_cos | KW_atan | KW_abs | KW_cart_x |
	 * KW_cart_y | KW_polar_a | KW_polar_r
	 */
	@Test
	public void functionName1() throws SyntaxException, LexicalException {
		// String input = "sin";
		// String input = "cos";
		// String input = "atan";
		// String input = "abs";
		// String input = "cart_x";
		// String input = "cart_y";
		// String input = "polar_a";
		// String input = "polar_r";
		String input = "sin x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.functionName();
	}

	/**
	 * FunctionApplication ::= FunctionName LPAREN Expression RPAREN |
	 * FunctionName LSQUARE Selector RSQUARE
	 */
	@Test
	public void functionApplication_WithExprArg() throws SyntaxException, LexicalException {
		String input = "sin(r)";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = null;
		try {
			exp = parser.functionApplication();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}

		show(exp);
		assertEquals(Expression_FunctionAppWithExprArg.class, exp.getClass());
		assertEquals(KW_sin, ((Expression_FunctionAppWithExprArg) exp).function);
		assertEquals(KW_r, ((Expression_PredefinedName) ((Expression_FunctionAppWithExprArg) exp).arg).kind);
	}

	/**
	 * FunctionApplication ::= FunctionName LPAREN Expression RPAREN |
	 * FunctionName LSQUARE Selector RSQUARE
	 */
	@Test
	public void functionApplication_WithIndexArg() throws SyntaxException, LexicalException {
		String input = "sin[x,x]";
		// String input = "sin[y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = null;
		try {
			exp = parser.functionApplication();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}

		show(exp);
		assertEquals(Expression_FunctionAppWithIndexArg.class, exp.getClass());
		assertEquals(KW_sin, ((Expression_FunctionAppWithIndexArg) exp).function);
		assertEquals(Expression_PredefinedName.class,
				((Index) ((Expression_FunctionAppWithIndexArg) exp).arg).e0.getClass());
		assertEquals(KW_x,
				((Expression_PredefinedName) ((Index) ((Expression_FunctionAppWithIndexArg) exp).arg).e0).kind);
	}

	@Test
	public void testLHS() throws SyntaxException, LexicalException {
		String input = "togepi[[x,y]]";
		// String input = "meowth";
		// String input = "onyx []";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		LHS lhs = parser.lhs();

		show(lhs);
		assertEquals("togepi", lhs.name);
		assertEquals(Expression_PredefinedName.class, lhs.index.e0.getClass());
		assertEquals(Expression_PredefinedName.class, lhs.index.e1.getClass());
		assertEquals(KW_x, ((Expression_PredefinedName) lhs.index.e0).kind);
	}

	/**
	 * IdentOrPixelSelectorExpression::= IDENTIFIER LSQUARE Selector RSQUARE |
	 * IDENTIFIER
	 */
	@Test
	public void IdentOrPixelSelectorExpression1() throws SyntaxException, LexicalException {
		String input = "togepi[x,y]";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression e = parser.identOrPixelSelectorExpression();
		show(e);
		assertEquals(Expression_PixelSelector.class, e.getClass());
		assertEquals("togepi", ((Expression_PixelSelector) e).name);
		assertEquals(Index.class, ((Expression_PixelSelector) e).index.getClass());
		assertEquals(((Expression_PixelSelector) e).index.e0.getClass(), Expression_PredefinedName.class);
		assertEquals(((Expression_PixelSelector) e).index.e1.getClass(), Expression_PredefinedName.class);

	}

	/**
	 * Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN |
	 * FunctionApplication | BOOLEAN
	 */
	@Test
	public void primary1() throws SyntaxException, LexicalException {
		// String input = "1234";
		// String input = "(plp)";
		// String input = "( x x )";
		String input = "true";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression e = parser.primary();
		show(e);
		assertEquals(Expression_BooleanLit.class, e.getClass());
		assertEquals(((Expression_BooleanLit) e).value, true);
	}

	/**
	 * Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN |
	 * FunctionApplication | BOOLEAN
	 */
	@Test
	public void primary21() throws SyntaxException, LexicalException {
		// String input = "1234";
		// String input = "(plp)";
		// String input = "( x x )";
		String input = "false";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression e = parser.primary();
		show(e);
		assertEquals(Expression_BooleanLit.class, e.getClass());
		assertEquals(((Expression_BooleanLit) e).value, false);
	}

	@Test
	public void expression1() throws SyntaxException, LexicalException {
		String input = "2";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = null;
		try {
			exp = parser.expression();
		} catch (cop5556fa17.Parser.SyntaxException e1) {
			System.out.println("Syntax Exception for input= " + input);
		}
		show("Expression Returned= " + exp);
		assertEquals(Expression_IntLit.class, exp.getClass());
		Expression_IntLit exp_intLit = (Expression_IntLit) exp;
		assertEquals(2, exp_intLit.value);
	}

	/**
	 * **************************************************************************************************************************************************
	 * ********************************************** SRISHTI's TESTS (In Progress)          ************************************************************
	 * **************************************************************************************************************************************************
	 */

	/**
	 * Selector ::= Expression COMMA Expression function first
	 */
	@Test
	public void selector() throws SyntaxException, LexicalException {
		String input = "++++x , ++++x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);

		Index index = parser.selector();

		assertEquals(Expression_Unary.class, index.e0.getClass());
		assertEquals(Expression_Unary.class, index.e1.getClass());
		assertEquals(OP_PLUS, ((Expression_Unary) (index.e0)).op);
		assertEquals(Expression_Unary.class, ((Expression_Unary) (index.e0)).e.getClass());
		assertEquals(KW_x,
				((Expression_PredefinedName) ((Expression_Unary) ((Expression_Unary) ((Expression_Unary) ((Expression_Unary) (index.e0)).e).e).e).e).kind);
	}

	/**
	 * RaSelector ::= KW_r COMMA KW_A
	 */
	@Test
	public void testRaSelector() throws SyntaxException, LexicalException {
		String input = "r , a";
		// String input = "r , AB";
		// String input = "r ; A";
		// String input = "R , A";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);

		Index index = parser.raSelector();

		show(index);
		assertEquals(Expression_PredefinedName.class, index.e0.getClass());
		assertEquals(Expression_PredefinedName.class, index.e1.getClass());
		assertEquals(KW_r, ((Expression_PredefinedName) index.e0).kind);
		assertEquals(KW_a, ((Expression_PredefinedName) index.e1).kind);
	}

	/**
	 * XySelector ::= KW_x COMMA KW_y
	 */
	@Test
	public void xySelector() throws SyntaxException, LexicalException {
		String input = "x,y,x,y";
		// String input = "x , y";
		// String input = "xv,y";
		// String input = "x,yb";
		// String input = "x y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);

		Index index = parser.xySelector();

		show(index);
		assertEquals(Expression_PredefinedName.class, index.e0.getClass());
		assertEquals(Expression_PredefinedName.class, index.e1.getClass());
		assertEquals(KW_x, ((Expression_PredefinedName) index.e0).kind);
		assertEquals(KW_y, ((Expression_PredefinedName) index.e1).kind);
	}

	/**
	 * LhsSelector ::= LSQUARE ( XySelector | RaSelector ) RSQUARE
	 */
	@Test
	public void lhsSelector() throws SyntaxException, LexicalException {
		String input = "[x,y]";
		// String input = "[r,A]";
		// String input = "[r,y]";
		// String input = "[][]";
		// String input = "[x,y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);

		Index index = parser.lhsSelector();

		show(index);
		assertEquals(Expression_PredefinedName.class, index.e0.getClass());
		assertEquals(Expression_PredefinedName.class, index.e1.getClass());
		assertEquals(KW_x, ((Expression_PredefinedName) index.e0).kind);
		assertEquals(KW_y, ((Expression_PredefinedName) index.e1).kind);
	}

	/**
	 * FunctionName ::= KW_sin | KW_cos | KW_atan | KW_abs | KW_cart_x |
	 * KW_cart_y | KW_polar_a | KW_polar_r
	 */
	@Test
	public void functionName() throws SyntaxException, LexicalException {
		// String input = "sin";
		// String input = "cos";
		// String input = "atan";
		// String input = "abs";
		// String input = "cart_x";
		// String input = "cart_y";
		// String input = "polar_a";
		// String input = "polar_r";
		String input = "sin x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.functionName();
	}

	/**
	 * FunctionApplication ::= FunctionName LPAREN Expression RPAREN |
	 * FunctionName LSQUARE Selector RSQUARE
	 */
	@Test
	public void functionApplication_WithExprArg1() throws SyntaxException, LexicalException {
		String input = "sin(r)";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = null;
		try {
			exp = parser.functionApplication();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}

		show(exp);
		assertEquals(Expression_FunctionAppWithExprArg.class, exp.getClass());
		assertEquals(KW_sin, ((Expression_FunctionAppWithExprArg) exp).function);
		assertEquals(KW_r, ((Expression_PredefinedName) ((Expression_FunctionAppWithExprArg) exp).arg).kind);
	}

	/**
	 * FunctionApplication ::= FunctionName LPAREN Expression RPAREN |
	 * FunctionName LSQUARE Selector RSQUARE
	 */
	@Test
	public void functionApplication_WithIndexArg1() throws SyntaxException, LexicalException {
		String input = "sin[x,x]";
		// String input = "sin[y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = null;
		try {
			exp = parser.functionApplication();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}

		show(exp);
		assertEquals(Expression_FunctionAppWithIndexArg.class, exp.getClass());
		assertEquals(KW_sin, ((Expression_FunctionAppWithIndexArg) exp).function);
		assertEquals(Expression_PredefinedName.class,
				((Index) ((Expression_FunctionAppWithIndexArg) exp).arg).e0.getClass());
		assertEquals(KW_x,
				((Expression_PredefinedName) ((Index) ((Expression_FunctionAppWithIndexArg) exp).arg).e0).kind);
	}

	/**
	 * Lhs::= IDENTIFIER ( LSQUARE LhsSelector RSQUARE | e )
	 */
	@Test
	public void testLHS1() throws SyntaxException, LexicalException {
		String input = "togepi[[x,y]]";
		// String input = "meowth";
		// String input = "onyx []";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		LHS lhs = parser.lhs();

		show(lhs);
		assertEquals("togepi", lhs.name);
		assertEquals(Expression_PredefinedName.class, lhs.index.e0.getClass());
		assertEquals(Expression_PredefinedName.class, lhs.index.e1.getClass());
		assertEquals(KW_x, ((Expression_PredefinedName) lhs.index.e0).kind);
	}

	/**
	 * IdentOrPixelSelectorExpression::= IDENTIFIER LSQUARE Selector RSQUARE |
	 * IDENTIFIER
	 */
	@Test
	public void IdentOrPixelSelectorExpression() throws SyntaxException, LexicalException {
		// String input = "togepi[[xxx,yyyy]]";
		// String input = "meowth";
		// String input = "onyx []";
		String input = "togepi[x,y]";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression e = parser.identOrPixelSelectorExpression();

		show(e);
		assertEquals(Expression_PixelSelector.class, e.getClass());
		assertEquals("togepi", ((Expression_PixelSelector) e).name);
		assertEquals(Index.class, ((Expression_PixelSelector) e).index.getClass());
		assertEquals(((Expression_PixelSelector) e).index.e0.getClass(), Expression_PredefinedName.class);
		assertEquals(((Expression_PixelSelector) e).index.e1.getClass(), Expression_PredefinedName.class);
	}

	/**
	 * Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN |
	 * FunctionApplication | BOOLEAN
	 */
	@Test
	public void primary() throws SyntaxException, LexicalException {
		// String input = "1234";
		// String input = "(plp)";
		// String input = "( x x )";
		String input = "true";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);

		Expression e = parser.primary();

		show(e);
		assertEquals(Expression_BooleanLit.class, e.getClass());
		assertEquals(((Expression_BooleanLit) e).value, true);
	}

	/**
	 * Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN |
	 * FunctionApplication | BOOLEAN
	 */
	@Test
	public void primary2() throws SyntaxException, LexicalException {
		// String input = "1234";
		// String input = "(plp)";
		// String input = "( x x )";
		String input = "false";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);

		Expression e = parser.primary();

		show(e);
		assertEquals(Expression_BooleanLit.class, e.getClass());
		assertEquals(((Expression_BooleanLit) e).value, false);
	}

	/**
	 * Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN |
	 * FunctionApplication | BOOLEAN
	 */
	@Test
	public void primary3() throws SyntaxException, LexicalException {
		String input = "(!R | !DEF_X & 5)";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);

		Expression e = parser.primary();

		show(e);
		// Expected output:
		// Expression_Binary [e0=Expression_Unary [op=OP_EXCL,
		// e=Expression_PredefinedName [name=KW_R]], op=OP_OR,
		// e1=Expression_Binary [e0=Expression_Unary [op=OP_EXCL,
		// e=Expression_PredefinedName [name=KW_DEF_X]], op=OP_AND,
		// e1=Expression_IntLit [value=5]]]

	}

	/**
	 * UnaryExpressionNotPlusMinus ::= OP_EXCL UnaryExpression | Primary |
	 * IdentOrPixelSelectorExpression | KW_x | KW_y | KW_r | KW_a | KW_X | KW_Y
	 * | KW_Z | KW_A | KW_R | KW_DEF_X | KW_DEF_Y
	 */
	@Test
	public void unaryExpressionNotPlusMinus() throws SyntaxException, LexicalException {
		String input = "!-5 ";
		// String input = "1234";
		// String input = "togepi[[xxx,yyyy]]";
		// String input = "Z";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);

		Expression exp = parser.unaryExpressionNotPlusMinus();

		show(exp);
		assertEquals(Expression_Unary.class, exp.getClass());
		assertEquals(OP_EXCL, ((Expression_Unary) exp).op);
		assertEquals(Expression_Unary.class, ((Expression_Unary) exp).e.getClass());
		assertEquals(OP_MINUS, ((Expression_Unary) ((Expression_Unary) exp).e).op);
		assertEquals(Expression_IntLit.class, ((Expression_Unary) ((Expression_Unary) exp).e).e.getClass());
		assertEquals(5, ((Expression_IntLit) ((Expression_Unary) ((Expression_Unary) exp).e).e).value);
	}

	/**
	 * UnaryExpression ::= OP_PLUS UnaryExpression | OP_MINUS UnaryExpression |
	 * UnaryExpressionNotPlusMinus
	 */
	@Test
	public void unaryExpression() throws SyntaxException, LexicalException {
		// String input = "++";
		// String input = "--";
		String input = "+++!-5";
		// String input = "Z";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);

		Expression exp = parser.unaryExpression();

		show(exp);
		// Expected Output:
		// Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_PLUS,
		// e=Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_EXCL,
		// e=Expression_Unary [op=OP_MINUS, e=Expression_IntLit [value=5]]]]]]
	}

	/**
	 * MultExpression := UnaryExpression ( ( OP_TIMES | OP_DIV | OP_MOD )
	 * UnaryExpression )*
	 */
	@Test
	public void multExpression() throws SyntaxException, LexicalException {
		String input = "!DEF_Y * +!DEF_Y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = parser.multExpression();
		show(exp);
		assertEquals(Expression_Binary.class, exp.getClass());
		assertEquals(Expression_Unary.class, ((Expression_Binary) exp).e0.getClass());
		assertEquals(Expression_Unary.class, ((Expression_Binary) exp).e1.getClass());
		assertEquals(Expression_PredefinedName.class, ((Expression_Unary) ((Expression_Binary) exp).e0).e.getClass());
		assertEquals(Expression_Unary.class, ((Expression_Unary) ((Expression_Binary) exp).e1).e.getClass());
		assertEquals(Expression_PredefinedName.class,
				((Expression_Unary) ((Expression_Unary) ((Expression_Binary) exp).e1).e).e.getClass());
		
		String ast = "Expression_Binary [e0=Expression_Unary [op=OP_EXCL, e=Expression_PredefinedName [name=KW_DEF_Y]], op=OP_TIMES, e1=Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_EXCL, e=Expression_PredefinedName [name=KW_DEF_Y]]]]";
		assert(ast.equals(exp.toString()));
	}

	/**
	 * AddExpression ::= MultExpression ( (OP_PLUS | OP_MINUS ) MultExpression
	 * )*
	 */
	@Test
	public void addExpression() throws SyntaxException, LexicalException {
		String input = "x*x + x%x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = parser.addExpression();
		
		show(exp);
		assertEquals(Expression_Binary.class , exp.getClass());
		assertEquals(Expression_Binary.class , ((Expression_Binary)exp).e0.getClass());
		assertEquals(Expression_Binary.class , ((Expression_Binary)exp).e1.getClass());
		assertEquals(Expression_PredefinedName.class , ((Expression_Binary)((Expression_Binary)exp).e0).e0.getClass());
		assertEquals(KW_x , ((Expression_PredefinedName)((Expression_Binary)((Expression_Binary)exp).e0).e0).kind);
		
		String ast = "Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_x]], op=OP_PLUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_MOD, e1=Expression_PredefinedName [name=KW_x]]]";
		assert(ast.equals(exp.toString()));
	}

	/**
	 * RelExpression ::= AddExpression ( ( OP_LT | OP_GT | OP_LE | OP_GE )
	 * AddExpression)*
	 */
	@Test
	public void relExpression() throws SyntaxException, LexicalException {
		String input = "x*x + x%x < x*x + x%x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = parser.relExpression();
		
		show(exp);
		String ast = "Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_x]], op=OP_PLUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_MOD, e1=Expression_PredefinedName [name=KW_x]]], op=OP_LT, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_x]], op=OP_PLUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_MOD, e1=Expression_PredefinedName [name=KW_x]]]]";
		assert(ast.equals(exp.toString()));
	}

	/**
	 * EqExpression ::= RelExpression ( (OP_EQ | OP_NEQ ) RelExpression )*
	 */
	@Test
	public void eqExpression() throws SyntaxException, LexicalException {
		String input = "y*y - y*y > y*y - y*y != y*y - y*y > y*y - y*y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = parser.eqExpression();
		show(exp);
		
		String ast = "Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GT, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]], op=OP_NEQ, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GT, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]]]";
		assert(ast.equals(exp.toString()));
	}

	/**
	 * AndExpression ::= EqExpression ( OP_AND EqExpression )*
	 */
	@Test
	public void andExpression() throws SyntaxException, LexicalException {
		String input = "y*y - y*y > y*y - y*y = y*y - y*y > y*y - y*y & y*y - y*y > y*y - y*y = y*y - y*y > y*y - y*y & y*y - y*y > y*y - y*y = y*y - y*y > y*y - y*y ";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = parser.andExpression();
		show(exp);
		
		String ast = "Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GT, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]]";
		assert(ast.equals(exp.toString()));
	}

	/**
	 * OrExpression ::= AndExpression ( OP_OR AndExpression)*
	 */
	@Test
	public void orExpression() throws SyntaxException, LexicalException {
		String input = "y*y - y*y > y*y - y*y == y*y - y*y > y*y - y*y | 8";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = parser.orExpression();
		show(exp);
		
		String ast = "Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GT, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]], op=OP_EQ, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GT, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_y], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]]], op=OP_OR, e1=Expression_IntLit [value=8]]";
		assert(ast.equals(exp.toString()));
	}

	/**
	 * Expression ::= OrExpression OP_Q Expression OP_COLON Expression |
	 * OrExpression
	 */
	@Test
	public void testExpression() throws SyntaxException, LexicalException {
		// String input = "x|x:@";
		String input = "x?x:Y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = null;
		try {
			exp = parser.expression(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
		
		show(exp);
		String ast = "Expression_Conditional [condition=Expression_PredefinedName [name=KW_x], trueExpression=Expression_PredefinedName [name=KW_x], falseExpression=Expression_PredefinedName [name=KW_Y]]";
		assert(ast.equals(exp.toString()));
	}

	/**
	 * AssignmentStatement ::= Lhs OP_ASSIGN Expression
	 */
	@Test
	public void assignmentStatement() throws SyntaxException, LexicalException {
		String input = "meowth = 5";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Statement stmt_asgn = parser.assignmentStatement();
		
		show(stmt_asgn);
		String ast = "Statement_Assign [lhs=name [name=meowth, index=null], e=Expression_IntLit [value=5]]";
		assert(ast.equals(stmt_asgn.toString()));
	}

	/**
	 * ImageInStatement ::= IDENTIFIER OP_LARROW Source
	 */
	@Test
	public void imageInStatement() throws SyntaxException, LexicalException {
		String input = "String <- @Y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Statement stmt_in = parser.imageInStatement();
		
		show(stmt_in);
		String ast = "Statement_In [name=String, source=Source_CommandLineParam [paramNum=Expression_PredefinedName [name=KW_Y]]]";
		assert(ast.equals(stmt_in.toString()));
	}

	/**
	 * Sink ::= IDENTIFIER | KW_SCREEN //ident must be file
	 */
	@Test
	public void sink() throws SyntaxException, LexicalException {
		String input = "SCREEN SCREEN";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Sink sink = parser.sink();
		
		show(sink);
		String ast = "Sink_SCREEN [kind=KW_SCREEN]";
		assert(ast.equals(sink.toString()));
	}

	/**
	 * ImageOutStatement ::= IDENTIFIER OP_RARROW Sink
	 */
	@Test
	public void imageOutStatement() throws SyntaxException, LexicalException {
		String input = "string -> SCREEN";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Statement stmt_out = parser.imageOutStatement();
		
		show(stmt_out);
		String ast = "Statement_Out [name=string, sink=Sink_SCREEN [kind=KW_SCREEN]]";
		assert(ast.equals(stmt_out.toString()));
	}

	/**
	 * Statement ::= AssignmentStatement | ImageOutStatement | ImageInStatement
	 */
	@Test
	public void statement() throws SyntaxException, LexicalException {
		String input = "String [ [x,y]] = 789";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Statement stmt = parser.statement();
		
		show(stmt);
		String ast = "Statement_Assign [lhs=name [name=String, index=Index [e0=Expression_PredefinedName [name=KW_x], e1=Expression_PredefinedName [name=KW_y]]], e=Expression_IntLit [value=789]]";
		assert(ast.equals(stmt.toString()));
	}

	/**
	 * ImageDeclaration ::= KW_image (LSQUARE Expression COMMA Expression
	 * RSQUARE | e) IDENTIFIER ( OP_LARROW Source | e )
	 */
	@Test
	public void imageDeclaration() throws SyntaxException, LexicalException {
		String input = "image [ ++++x , ++++x ] string";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Declaration decl_image = parser.imageDeclaration();
		
		show(decl_image);
		String ast = "Declaration_Image [xSize=Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_PLUS, e=Expression_PredefinedName [name=KW_x]]]]], ySize=Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_PLUS, e=Expression_PredefinedName [name=KW_x]]]]], name=string, source=null]";
		assert(ast.equals(decl_image.toString()));
	}

	/**
	 * SourceSinkType := KW_url | KW_file
	 */
	@Test
	public void sourceSinkType() throws SyntaxException, LexicalException {
		String input = "file";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.sourceSinkType();
	}

	/**
	 * Source ::= STRING_LITERAL | IDENTIFIER | OP_AT Expression
	 */
	@Test
	public void source() throws SyntaxException, LexicalException {
		String input = "@5692";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Source source = parser.source();
		
		show(source);
		String ast = "Source_CommandLineParam [paramNum=Expression_IntLit [value=5692]]";
		assert(ast.equals(source.toString()));
	}

	/**
	 * SourceSinkDeclaration ::= SourceSinkType IDENTIFIER OP_ASSIGN Source
	 */
	@Test
	public void sourceSinkDeclaration() throws SyntaxException, LexicalException {
		String input = "url is = @ 5";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Declaration decl_sourceSink = parser.sourceSinkDeclaration();
		
		show(decl_sourceSink);
		String ast = "Declaration_SourceSink [type=KW_url, name=is, source=Source_CommandLineParam [paramNum=Expression_IntLit [value=5]]]";
		assert(ast.equals(decl_sourceSink.toString()));
	}

	/**
	 * VarType ::= KW_int | KW_boolean
	 */
	@Test
	public void varType() throws SyntaxException, LexicalException {
		String input = "boolean";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.varType();
	}

	/**
	 * VariableDeclaration ::= VarType IDENTIFIER ( OP_ASSIGN Expression | e )
	 */
	@Test
	public void variableDeclaration() throws SyntaxException, LexicalException {
		String input = "boolean pikachu = 10";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Declaration decl_var = parser.variableDeclaration();
		
		show(decl_var);
		String ast = "Declaration_Variable [type=[KW_boolean,boolean,0,7,1,1], name=pikachu, e=Expression_IntLit [value=10]]";
		assert(ast.equals(decl_var.toString()));
	}

	/**
	 * Declaration :: = VariableDeclaration | ImageDeclaration |
	 * SourceSinkDeclaration
	 */
	@Test
	public void declaration() throws SyntaxException, LexicalException {
		String input = "int blah = mmoreBlah";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Declaration decl = parser.declaration();
		
		show(decl);
		String ast = "Declaration_Variable [type=[KW_int,int,0,3,1,1], name=blah, e=Expression_Ident [name=mmoreBlah]]";
		assert(ast.equals(decl.toString()));
	}

	/**
	 * Program ::= IDENTIFIER ( Declaration SEMI | Statement SEMI )*
	 */
	@Test
	public void testProgram() throws SyntaxException, LexicalException {
		String input = "KW_sin( )";
		// String input = "KW_sin[ ]";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program program = parser.program();
		
		show(program);
		String ast = "Program [name=KW_sin, decsAndStatements=[]]";
		assert(ast.equals(program.toString()));
	}

	@Test
	public void expression3() throws SyntaxException, LexicalException {
		String input = "x * 43 OP_OR 2 + 2";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression exp = parser.expression();
		
		show(exp);
		String ast = "Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_IntLit [value=43]]";
		assert(ast.equals(exp.toString()));
	}

	@Test
	public void testcase55() throws SyntaxException, LexicalException {
		String input = "(6*2/23/4*22*sin(x))||(abs(6*2*12)+cart_x[x,y]+cart_y[(6/23),(7/23)]+polar_a[6/2/2,2/3/4]+polar_r(z))&true";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser Parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Parser.expression();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testExpRecursion() throws SyntaxException, LexicalException {
		String input = "sin ( Def_X )";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		// thrown.expect(SyntaxException.class);
		Expression exp = parser.expression();
		
		show(exp);
		String ast = "Expression_FunctionAppWithExprArg [function=KW_sin, arg=Expression_Ident [name=Def_X]]";
		assert(ast.equals(exp.toString()));
	}

	/**
	 * **************************************************************************************************************************************************
	 * ********************************************** GITANG's TESTS
	 * ************************************************************
	 * **************************************************************************************************************************************************
	 */

	@Test
	public void testDec2() throws LexicalException, SyntaxException {
		String input = "prog file gitang = \"hello\";";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		// This should have one Declaration_Variable object, which is at
		// position 0 in the decsAndStatements list
		Declaration_SourceSink dec = (Declaration_SourceSink) ast.decsAndStatements.get(0);
		assertEquals(KW_file, dec.type);
		assertEquals("gitang", dec.name);
		Source_StringLiteral source_StringLiteral = (Source_StringLiteral) dec.source;
		assertEquals(source_StringLiteral.fileOrUrl, "hello");
	}

	@Test
	public void testDec3() throws LexicalException, SyntaxException {
		String input = "prog file adhiraj = @ 2 + 3;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		// This should have one Declaration_Variable object, which is at
		// position 0 in the decsAndStatements list
		Declaration_SourceSink dec = (Declaration_SourceSink) ast.decsAndStatements.get(0);
		assertEquals(KW_file, dec.type);
		assertEquals("adhiraj", dec.name);
		Source_CommandLineParam src_cmd = (Source_CommandLineParam) dec.source;
		Expression_Binary e = (Expression_Binary) src_cmd.paramNum;
		assertEquals(e.op, OP_PLUS);
		Expression_IntLit inte0 = (Expression_IntLit) e.e0;
		Expression_IntLit inte1 = (Expression_IntLit) e.e1;

		assertEquals(2, inte0.value);
		assertEquals(3, inte1.value);
	}

	@Test
	public void testDec4() throws LexicalException, SyntaxException {
		String input = "prog file adhiraj = god;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		// This should have one Declaration_Variable object, which is at
		// position 0 in the decsAndStatements list
		Declaration_SourceSink dec = (Declaration_SourceSink) ast.decsAndStatements.get(0);
		assertEquals(KW_file, dec.type);
		assertEquals("adhiraj", dec.name);
		Source_Ident source_ident = (Source_Ident) dec.source;
		assertEquals(source_ident.name, "god");
	}

	@Test
	public void testDec5() throws LexicalException, SyntaxException {
		String input = "prog image gitang;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		// This should have one Declaration_Variable object, which is at
		// position 0 in the decsAndStatements list
		Declaration_Image dec = (Declaration_Image) ast.decsAndStatements.get(0);
		assertNull(dec.source);
		assertEquals("gitang", dec.name);
		assertNull(dec.xSize);
		assertNull(dec.ySize);
	}

	@Test
	public void testDec6() throws LexicalException, SyntaxException {
		String input = "prog image [2,2] gitang;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		// This should have one Declaration_Variable object, which is at
		// position 0 in the decsAndStatements list
		Declaration_Image dec = (Declaration_Image) ast.decsAndStatements.get(0);
		assertNull(dec.source);
		assertEquals("gitang", dec.name);

		Expression_IntLit inte0 = (Expression_IntLit) dec.xSize;
		Expression_IntLit inte1 = (Expression_IntLit) dec.ySize;

		assertEquals(2, inte0.value);
		assertEquals(2, inte1.value);
	}

	/**
	 * **************************************************************************************************************************************************
	 * ********************************************** Sahil's TESTS
	 * ************************************************************
	 * **************************************************************************************************************************************************
	 */

	@Test
	public void testDec2_sahil() throws LexicalException, SyntaxException {
		String input = "prog image xxx;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");// This should have one
										// Declaration_Variable object,
		// which is at position 0 in the decsAndStatements list
		Declaration_Image dec = (Declaration_Image) ast.decsAndStatements.get(0);
		assertEquals(KW_image, dec.firstToken.kind);
		assertNull(dec.xSize);
		assertNull(dec.ySize);
		assertEquals("xxx", dec.name);
		assertNull(dec.source);
	}

	@Test
	public void testDec3_sahil() throws LexicalException, SyntaxException {
		String input = "prog int abc=1977893;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");// This should have one
										// Declaration_Variable object,
		// which is at position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements.get(0);
		assertEquals(KW_int, dec.type.kind);
		assertEquals("abc", dec.name);
		Expression_IntLit lit = (Expression_IntLit) dec.e;
		assertEquals(INTEGER_LITERAL, lit.firstToken.kind);
		assertEquals(1977893, lit.value);
	}

	@Test
	public void testDec4_sahil() throws LexicalException, SyntaxException {
		String input = "prog boolean  jbbhh  = true;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");// This should have one
										// Declaration_Variable object,
		// which is at position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements.get(0);
		assertEquals(KW_boolean, dec.type.kind);
		assertEquals("jbbhh", dec.name);
		Expression_BooleanLit lit = (Expression_BooleanLit) dec.e;
		assertEquals(BOOLEAN_LITERAL, lit.firstToken.kind);
		assertEquals(true, lit.value);
		String str = "Program [name=prog, decsAndStatements=[Declaration_Variable [type=[KW_boolean,boolean,5,7,1,6], name=jbbhh, e=Expression_BooleanLit [value=true]]]]";
		assertEquals(ast.toString(), str);
	}

	@Test
	public void testDec5_sahil() throws LexicalException, SyntaxException {
		String input = "prog boolean  jbbhh  = ture;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");// This should have one
										// Declaration_Variable object,
		// which is at position 0 in the decsAndStatements list
		thrown.expect(SyntaxException.class);
		try {
			ASTNode dst = parser.parse();
			; // Parse the program, which should throw an exception
		} catch (SyntaxException e) {
			show(e); // catch the exception and show it
			throw e; // rethrow for Junit
		}
	}

	@Test
	public void testImageOutStmt1() throws LexicalException, SyntaxException {
		String input = "prog img->SCREEN;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");// This should have one
										// Declaration_Variable object,
		// which is at position 0 in the decsAndStatements list
		Statement_Out st_out = (Statement_Out) ast.decsAndStatements.get(0);
		assertEquals(IDENTIFIER, st_out.firstToken.kind);
		assertEquals("img", st_out.name);
		// Expression_IntLit lit= (Expression_IntLit)dec.e;
		Sink sink = (Sink) st_out.sink;
		// assertEquals(INTEGER_LITERAL,lit.firstToken.kind);
		assertEquals(KW_SCREEN, st_out.sink.firstToken.kind);
		assertEquals("SCREEN", st_out.sink.firstToken.getText());
	}

	@Test
	public void testImageOutStmt2() throws LexicalException, SyntaxException {
		String input = "prog img2->pointer;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");// This should have one
										// Declaration_Variable object,
		// which is at position 0 in the decsAndStatements list
		Statement_Out st_out = (Statement_Out) ast.decsAndStatements.get(0);
		assertEquals(IDENTIFIER, st_out.firstToken.kind);
		assertEquals("img2", st_out.name);
		// Expression_IntLit lit= (Expression_IntLit)dec.e;
		Sink sink = (Sink) st_out.sink;
		// assertEquals(INTEGER_LITERAL,lit.firstToken.kind);
		assertEquals(IDENTIFIER, st_out.sink.firstToken.kind);
		assertEquals("pointer", st_out.sink.firstToken.getText());
	}

	@Test
	public void testImageImgDec1() throws LexicalException, SyntaxException {
		String input = "prog image img<-pointer;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");// This should have one
										// Declaration_Variable object,
		// which is at position 0 in the decsAndStatements list
		Declaration_Image dec_img = (Declaration_Image) ast.decsAndStatements.get(0);
		assertEquals(KW_image, dec_img.firstToken.kind);
		assertEquals("img", dec_img.name);
		// assertEquals(KW_image, dec_img.);
		// Expression_IntLit lit= (Expression_IntLit)dec.e;
		Source source = (Source) dec_img.source;
		// assertEquals(INTEGER_LITERAL,lit.firstToken.kind);
		assertNull(dec_img.xSize);
		assertNull(dec_img.ySize);
		assertEquals(IDENTIFIER, dec_img.source.firstToken.kind);
		assertEquals("pointer", dec_img.source.firstToken.getText());
	}

	@Test
	public void testImageImgDec2() throws LexicalException, SyntaxException {
		String input = "prog image img<-@Z;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");// This should have one
										// Declaration_Variable object,
		// which is at position 0 in the decsAndStatements list
		Declaration_Image dec_img = (Declaration_Image) ast.decsAndStatements.get(0);
		assertEquals(KW_image, dec_img.firstToken.kind);
		assertEquals("img", dec_img.name);
		// assertEquals(KW_image, dec_img.);
		// Expression_IntLit lit= (Expression_IntLit)dec.e;
		Source source = (Source) dec_img.source;
		// assertEquals(INTEGER_LITERAL,lit.firstToken.kind);
		assertNull(dec_img.xSize);
		assertNull(dec_img.ySize);
		assertEquals(OP_AT, dec_img.source.firstToken.kind);
		assertEquals("@", dec_img.source.firstToken.getText());
		Source_CommandLineParam param = (Source_CommandLineParam) source;
		Expression_PredefinedName name = (Expression_PredefinedName) param.paramNum;
		assertEquals(KW_Z, name.firstToken.kind);
		assertEquals("Z", name.firstToken.getText());
	}

	@Test
	public void testImageImgDec3() throws LexicalException, SyntaxException {
		String input = "prog image[X,Y] img<-\"http:www.ufl.edu\";";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		Declaration_Image dec_img = (Declaration_Image) ast.decsAndStatements.get(0);
		assertEquals(KW_image, dec_img.firstToken.kind);
		Expression_PredefinedName name = (Expression_PredefinedName) dec_img.xSize;
		assertEquals(KW_X, name.firstToken.kind);
		name = (Expression_PredefinedName) dec_img.ySize;
		assertEquals(KW_Y, name.firstToken.kind);
		assertEquals("img", dec_img.name);
		Source_StringLiteral lit = (Source_StringLiteral) dec_img.source;
		assertEquals(STRING_LITERAL, lit.firstToken.kind);
		assertEquals("http:www.ufl.edu", lit.fileOrUrl);
	}

	@Test
	public void testImageImgDec4() throws LexicalException, SyntaxException {
		String input = "prog image[X,Y] img;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		Declaration_Image dec_img = (Declaration_Image) ast.decsAndStatements.get(0);
		assertEquals(KW_image, dec_img.firstToken.kind);
		Expression_PredefinedName name = (Expression_PredefinedName) dec_img.xSize;
		assertEquals(KW_X, name.firstToken.kind);
		name = (Expression_PredefinedName) dec_img.ySize;
		assertEquals(KW_Y, name.firstToken.kind);
		assertEquals("img", dec_img.name);
		assertNull(dec_img.source);
	}

	/**
	 * **************************************************************************************************************************************************
	 * ********************************************** Raktima's TESTS
	 * ************************************************************
	 * **************************************************************************************************************************************************
	 */

	@Test
	public void testDec2_Raktima() throws LexicalException, SyntaxException {
		String input = "prog boolean tera;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog");
		// This should have one Declaration_Variable object, which is at
		// position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements.get(0);
		assertEquals(KW_boolean, dec.type.kind);
		assertEquals("tera", dec.name);
		assertNull(dec.e);
	}

	@Test
	public void testDec3_Raktima() throws LexicalException, SyntaxException {
		String input = "prog int b=1+2;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		String str = "Program [name=prog, decsAndStatements=[Declaration_Variable [type=[KW_int,int,5,3,1,6], name=b, e=Expression_Binary [e0=Expression_IntLit [value=1], op=OP_PLUS, e1=Expression_IntLit [value=2]]]]]";
		assert (str.equals(ast.toString()));
	}

	@Test
	public void statement4() throws SyntaxException, LexicalException {
		String input = "raktima raktima [[x,y]]=a+b;";
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		String str = "Program [name=raktima, decsAndStatements=[Statement_Assign [lhs=name [name=raktima, index=Index [e0=Expression_PredefinedName [name=KW_x], e1=Expression_PredefinedName [name=KW_y]]], e=Expression_Binary [e0=Expression_PredefinedName [name=KW_a], op=OP_PLUS, e1=Expression_Ident [name=b]]]]]";
		assert (str.equals(ast.toString()));
	}

	@Test
	public void statement5() throws SyntaxException, LexicalException {
		String input = "a+b;";
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression ast = parser.expression();
		show(ast);
		String str = "Expression_Binary [e0=Expression_PredefinedName [name=KW_a], op=OP_PLUS, e1=Expression_Ident [name=b]]";
		assert (str.equals(ast.toString()));
	}

	@Test
	public void testDec4_Raktima() throws LexicalException, SyntaxException {
		String input = "prog int k=2;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		String str = "Program [name=prog, decsAndStatements=[Declaration_Variable [type=[KW_int,int,5,3,1,6], name=k, e=Expression_IntLit [value=2]]]]";
		assert (str.equals(ast.toString()));
	}

	@Test
	public void statement_Raktima() throws LexicalException, SyntaxException {
		String input = "2";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression ast = parser.expression();
		show(ast);
		String str = "Expression_IntLit [value=2]";
		assert (str.equals(ast.toString()));
	}

	@Test
	public void statement1() throws LexicalException, SyntaxException {
		String input = "abc int def;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		// This should have one Declaration_Variable object, which is at
		// position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements.get(0);
		assertEquals(KW_int, dec.type.kind);
		assertEquals("def", dec.name);
		assertNull(dec.e);
	}

	@Test
	public void statement2() throws LexicalException, SyntaxException {
		String input = "abc";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "abc");
		String str="Program [name=abc, decsAndStatements=[]]";
		assert (str.equals(ast.toString()));
	}

	@Test
	public void statement3() throws LexicalException, SyntaxException {
		String input = "++++x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression ast = parser.expression();
		show(ast);
		// assertEquals(ast.name, "abc");
		// This should have one Declaration_Variable object, which is at
		// position 0 in the decsAndStatements list
		// Declaration_Variable dec = (Declaration_Variable)
		// ast.decsAndStatements
		// .get(0);
		// assertEquals(KW_int, dec.type.kind);
		// assertEquals("def", dec.name);
		// assertNull(dec.e);
		String str="Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_PLUS, e=Expression_PredefinedName [name=KW_x]]]]]";
		assert (str.equals(ast.toString()));
	}

	@Test
	public void statement6() throws LexicalException, SyntaxException {
		String input = "+x=x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression ast = parser.expression();
		show(ast);
		// assertEquals(ast.name, "abc");
		// This should have one Declaration_Variable object, which is at
		// position 0 in the decsAndStatements list
		// Declaration_Variable dec = (Declaration_Variable)
		// ast.decsAndStatements
		// .get(0);
		// assertEquals(KW_int, dec.type.kind);
		// assertEquals("def", dec.name);
		// assertNull(dec.e);
		String str="Expression_Unary [op=OP_PLUS, e=Expression_PredefinedName [name=KW_x]]";
		assert (str.equals(ast.toString()));
	}

	@Test
	public void statement7() throws LexicalException, SyntaxException {
		String input = "+x=x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression ast = parser.expression();
		show(ast);
		// assertEquals(ast.name, "abc");
		// This should have one Declaration_Variable object, which is at
		// position 0 in the decsAndStatements list
		// Declaration_Variable dec = (Declaration_Variable)
		// ast.decsAndStatements
		// .get(0);
		// assertEquals(KW_int, dec.type.kind);
		// assertEquals("def", dec.name);
		// assertNull(dec.e);
		String str="Expression_Unary [op=OP_PLUS, e=Expression_PredefinedName [name=KW_x]]";
		assert (str.equals(ast.toString()));
	}

	@Test
	public void statement8() throws LexicalException, SyntaxException {
		String input = "+++x = +(+(+x)) = x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression ast = parser.expression();
		show(ast);
		// assertEquals(ast.name, "abc");
		// This should have one Declaration_Variable object, which is at
		// position 0 in the decsAndStatements list
		// Declaration_Variable dec = (Declaration_Variable)
		// ast.decsAndStatements
		// .get(0);
		// assertEquals(KW_int, dec.type.kind);
		// assertEquals("def", dec.name);
		// assertNull(dec.e);
		String str="Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_PLUS, e=Expression_Unary [op=OP_PLUS, e=Expression_PredefinedName [name=KW_x]]]]";
		assert (str.equals(ast.toString()));
	}

	@Test
	public void statement9() throws LexicalException, SyntaxException {
		String input = "myprog boolean val=false";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression ast = parser.expression();
		show(ast);
		// assertEquals(ast.name, "abc");
		// This should have one Declaration_Variable object, which is at
		// position 0 in the decsAndStatements list
		// Declaration_Variable dec = (Declaration_Variable)
		// ast.decsAndStatements
		// .get(0);
		// assertEquals(KW_int, dec.type.kind);
		// assertEquals("def", dec.name);
		// assertNull(dec.e);
		String str="Expression_Ident [name=myprog]";
		assert (str.equals(ast.toString()));
	}

	@Test
	public void testcase2() throws SyntaxException, LexicalException {
		String input = "a bcd";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.program(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testcase3() throws SyntaxException, LexicalException {
		String input = "cart_x cart_y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.program(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testcase5() throws SyntaxException, LexicalException {
		String input = "prog image[filepng,png] imageName <- imagepng"; // Error
																		// as
																		// there
																		// is
																		// not
																		// semicolon
																		// for
																		// ending
																		// the
																		// statement
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		// Parser.program();
		thrown.expect(SyntaxException.class);
		try {
			parser.program(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testcase6() throws SyntaxException, LexicalException {
		String input = "imageDeclaration image[\"abcd\"] "; // Should fail for
															// image[""]
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.program(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testcase7() throws SyntaxException, LexicalException {
		String input = "prog image[filepng,png] imageName <- imagepng; \n boolean ab=true;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.program();
		show(ast);
		assertEquals("prog", ast.name);
		// First Declaration statement
		Declaration_Image dec = (Declaration_Image) ast.decsAndStatements.get(0);
		assertEquals(KW_image, dec.firstToken.kind);
		assertEquals("imageName", dec.name);
		Expression_Ident ei = (Expression_Ident) dec.xSize;
		assertEquals("filepng", ei.name);
		ei = (Expression_Ident) dec.ySize;
		assertEquals("png", ei.name);
		Source_Ident s = (Source_Ident) dec.source;
		assertEquals("imagepng", s.name);
		// Second Declaration statement
		Declaration_Variable dec2 = (Declaration_Variable) ast.decsAndStatements.get(1);
		assertEquals("ab", dec2.name);
		assertEquals(KW_boolean, dec2.firstToken.kind);
		Expression_BooleanLit ebi = (Expression_BooleanLit) dec2.e;
		assertEquals(true, ebi.value);
	}

	@Test
	public void testcase8() throws SyntaxException, LexicalException {
		String input = "prog image[filepng,jpg] imageName;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.program();
		show(ast);
		assertEquals("prog", ast.name);
		Declaration_Image dec1 = (Declaration_Image) ast.decsAndStatements.get(0);
		assertEquals(dec1.name, "imageName");
		Expression_Ident exi = (Expression_Ident) dec1.xSize;
		Expression_Ident eyi = (Expression_Ident) dec1.ySize;
		assertEquals(exi.name, "filepng");
		assertEquals(eyi.name, "jpg");
		assertNull(dec1.source);
	}

	@Test
	public void testcase10() throws SyntaxException, LexicalException {
		String input = "prog @expr k=12;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.program(); // Parse the program
		show(ast);
		assertEquals(ast.name, "prog");
		assertEquals(ast.decsAndStatements.size(), 0);
	}

	@Test
	public void testcase10parse() throws SyntaxException, LexicalException {
		String input = "prog @expr k=12;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast = parser.parse(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testcase11() throws SyntaxException, LexicalException {
		String input = "prog \"abcded\" boolean a=true;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.program(); // Parse the program
		show(ast);
		assertEquals(ast.name, "prog");
		assertEquals(ast.decsAndStatements.size(), 0);
		String str="Program [name=prog, decsAndStatements=[]]";
		assert (str.equals(ast.toString()));
	}

	@Test
	public void testcase11_parse() throws SyntaxException, LexicalException {
		String input = "prog \"abcded\" boolean a=true;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast = parser.parse(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testcase12() throws SyntaxException, LexicalException {
		String input = "isBoolean boolean ab=true; boolean cd==true; abcd=true ? return true: return false;"; // Should
																												// fail
																												// for
																												// ==
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Program ast = parser.program(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testcase13() throws SyntaxException, LexicalException {
		String input = "isBoolean boolean ab=true; boolean cd==true; abcd=true ? return true: return false;"; // Should
																												// fail
																												// for
																												// =
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Program ast = parser.program(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testcase14() throws SyntaxException, LexicalException {
		String input = "isUrl url filepng=\"abcd\"; \n @expr=12; url awesome=@expr; \n url filepng=abcdefg";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.program(); // Parse the program
		show(ast);
		assertEquals(ast.name, "isUrl");
		assertEquals(ast.decsAndStatements.size(), 1);
		Declaration_SourceSink dss = (Declaration_SourceSink) ast.decsAndStatements.get(0);
		System.out.println("Here" + dss.name);
		assertEquals(dss.name, "filepng");
		assertEquals(dss.type, KW_url);
		Source_StringLiteral s = (Source_StringLiteral) dss.source;
		assertEquals(s.fileOrUrl, "abcd");
	}

	@Test
	public void testcase14_parse() throws SyntaxException, LexicalException {
		String input = "isUrl url filepng=\"abcd\"; \n @expr=12; url awesome=@expr; \n url filepng=abcdefg";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast = parser.parse(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testcase15() throws SyntaxException, LexicalException {
		String input = "isUrl url filepng=\"abcd\" \n @expr=12; url awesome=@expr; \n url filepng=abcdefg"; // Should
																											// fail
																											// for
																											// ;
																											// in
																											// line
																											// one
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Program ast = parser.program(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testcase16() throws SyntaxException, LexicalException {
		String input = "isFile file filepng=\"abcd\"; \n @expr=12; url filepng=@expr; \n url filepng=abcdefg";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.program(); // Parse the program
		show(ast);
		assertEquals(ast.name, "isFile");
		assertEquals(ast.decsAndStatements.size(), 1);
		assertEquals(ast.firstToken.kind, IDENTIFIER);

		// Declaration Statements
		Declaration_SourceSink ds = (Declaration_SourceSink) ast.decsAndStatements.get(0);
		assertEquals(ds.type, KW_file);
		assertEquals(ds.name, "filepng");
		Source_StringLiteral s = (Source_StringLiteral) ds.source;
		assertEquals(s.fileOrUrl, "abcd");
		// assertEquals(ast.)
	}

	@Test
	public void testcase16_parse() throws SyntaxException, LexicalException {
		String input = "isFile file filepng=\"abcd\"; \n @expr=12; url filepng=@expr; \n url filepng=abcdefg";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast = parser.parse(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testcase17() throws SyntaxException, LexicalException {
		String input = "isFile file filepng=\"abcd\" \n @expr=12; url filepng=@expr; \n url filepng=abcdefg"; // Should
																												// fail
																												// for
																												// ;
																												// in
																												// line
																												// one
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Program ast = parser.program(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testcase18() throws SyntaxException, LexicalException {
		String input = "isurl url urlname;"; // Should fail for url as url can
												// only be initalised
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Program ast = parser.program(); // Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testcase19() throws SyntaxException, LexicalException {
		String input = "declaration int xyz;\n boolean zya;\n image imagename;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.program(); // Parse the program
		show(ast);
		assertEquals(ast.name, "declaration");
		assertEquals(ast.firstToken.kind, IDENTIFIER);

		// Declaration statements start
		Declaration_Variable dv1 = (Declaration_Variable) ast.decsAndStatements.get(0);
		assertEquals(dv1.name, "xyz");
		assertEquals(dv1.type.kind, KW_int);
		assertNull(dv1.e);

		Declaration_Variable dv2 = (Declaration_Variable) ast.decsAndStatements.get(1);
		assertEquals(dv2.name, "zya");
		assertEquals(dv2.type.kind, KW_boolean);
		assertNull(dv2.e);

		Declaration_Image dv3 = (Declaration_Image) ast.decsAndStatements.get(2);
		assertEquals(dv3.name, "imagename");
		assertNull(dv3.source);
		assertNull(dv3.xSize);
		assertNull(dv3.ySize);

		// Declaration statement end
	}

	@Test
	public void testcase201() throws SyntaxException, LexicalException {
		String input = "imageProgram image imageName;" + "\n imageName->abcdpng; " + "\n imageName -> SCREEN; "
				+ "\n imageName <- \"awesome\";" + "\n imageName <- @express; \n" + "\n imageName <- abcdpng;"; // Image
																												// related
																												// Test
																												// cases
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Program ast = parser.program(); // Parse the program
		show(ast);
		assertEquals(ast.name, "imageProgram");

		// Declaration statement start
		Declaration_Image dv1 = (Declaration_Image) ast.decsAndStatements.get(0);
		assertEquals(dv1.name, "imageName");
		assertNull(dv1.xSize);
		assertNull(dv1.ySize);
		assertNull(dv1.source);

		Statement_Out dv2 = (Statement_Out) ast.decsAndStatements.get(1);
		assertEquals(dv2.name, "imageName");
		Sink_Ident si2 = (Sink_Ident) dv2.sink;
		assertEquals(si2.name, "abcdpng");
	}
	
	
	
	/**
	 * **************************************************************************************************************************************************
	 * **********************************************Assignment 2 tests
	 * ************************************************************
	 * **************************************************************************************************************************************************
	 */
	
    @Test
    public void testEmpty1() throws LexicalException, SyntaxException {
        String input = ""; // The input is the empty string. This is not legal
        show(input); // Display the input
        Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
        // initialize it
        show(scanner); // Display the Scanner
        Parser parser = new Parser(scanner); // Create a parser
        thrown.expect(SyntaxException.class);
        try {
            parser.parse(); // Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    /**
     * Another example. This is a legal program and should pass when your parser
     * is implemented.
     *
     * @throws LexicalException
     * @throws SyntaxException
     */

    @Test
    public void testDec11() throws LexicalException, SyntaxException {
        String input = "prog int k;";
        show(input);
        Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
        // initialize it
        show(scanner); // Display the Scanner
        Parser parser = new Parser(scanner); //
        parser.parse();
    }

    /**
     * This example invokes the method for expression directly. Effectively, we
     * are viewing Expression as the start symbol of a sub-language.
     * <p>
     * Although a compiler will always call the parse() method, invoking others
     * is useful to support incremental development. We will only invoke
     * expression directly, but following this example with others is
     * recommended.
     *
     * @throws SyntaxException
     * @throws LexicalException
     */
    @Test
    public void expression111() throws SyntaxException, LexicalException {
        String input = "2";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.expression(); // Call expression directly.
    }

    //Yasho
    @Test
    public void testRaSelector11() throws SyntaxException, LexicalException {
        String input = "r , a";
        // String input = "r , AB";
        // String input = "r ; A";
        // String input = "R , A";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.raSelector();
    }

    /**
     * XySelector ::= KW_x COMMA KW_y
     */
    @Test
    public void xySelector111() throws SyntaxException, LexicalException {
        String input = "x,y";
        // String input = "x , y";
        // String input = "xv,y";
        // String input = "x,yb";
        // String input = "x y";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.xySelector();
    }

    /**
     * LhsSelector ::= LSQUARE ( XySelector | RaSelector ) RSQUARE
     */
    @Test
    public void lhsSelector111() throws SyntaxException, LexicalException {
        String input = "[x,y]";
        // String input = "[r,A]";
        // String input = "[r,y]";
        // String input = "[][]";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.lhsSelector();
    }

    /**
     * FunctionName ::= KW_sin | KW_cos | KW_atan | KW_abs | KW_cart_x |
     * KW_cart_y | KW_polar_a | KW_polar_r
     */
    @Test
    public void functionName11() throws SyntaxException, LexicalException {
        String input = "sin";
        /*
         * String input = "sin"; String input = "cos"; String input = "atan";
		 * String input = "abs"; String input = "cart_x"; String input =
		 * "cart_y"; String input = "polar_a"; String input = "polar_r";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.functionName();
    }

    /**
     * FunctionName ::= KW_sin | KW_cos | KW_atan | KW_abs | KW_cart_x |
     * KW_cart_y | KW_polar_a | KW_polar_r
     */
    @Test
    public void functionNameFail() throws SyntaxException, LexicalException {
        String input = "polar_b";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.functionName(); // Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    /**
     * FunctionApplication ::= FunctionName LPAREN Expression RPAREN |
     * FunctionName LSQUARE Selector RSQUARE
     */
    @Test
    public void functionApplication() throws SyntaxException, LexicalException {
        String input = "sin(2)";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.functionApplication();
    }

    /**
     * FunctionApplication ::= FunctionName LPAREN Expression RPAREN |
     * FunctionName LSQUARE Selector RSQUARE
     */
    @Test
    public void functionApplication2() throws SyntaxException, LexicalException {
        String input = "sin(sin(true))";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.functionApplication();
    }

    /**
     * FunctionApplication ::= FunctionName LPAREN Expression RPAREN |
     * FunctionName LSQUARE Selector RSQUARE
     */
    @Test
    public void functionApplicationFail() throws SyntaxException, LexicalException {
        String input = "sin(sin)";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.functionApplication(); // Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    /**
     * VarType ::= KW_int | KW_boolean
     */
    @Test
    public void varType1() throws SyntaxException, LexicalException {
        String input = "int";
        /*
         * String input = "int"; String input = "boolean";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.varType();
    }

    /**
     * VarType ::= KW_int | KW_boolean
     */
    @Test
    public void varTypeFail() throws SyntaxException, LexicalException {
        String input = "bool";
        /*
         * String input = "true"; String input = "false"; String input= "bool";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.varType();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    /**
     * SourceSinkType := KW_url | KW_file
     */
    @Test
    public void sourceSinkType1() throws SyntaxException, LexicalException {
        String input = "url";
        /*
         * String input = "url"; String input = "file";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.sourceSinkType();
    }

    /**
     * SourceSinkType := KW_url | KW_file
     */
    @Test
    public void sourceSinkTypeFail() throws SyntaxException, LexicalException {
        String input = "URL";
		/*
		 * String input = "URL"; String input = "FILE";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.sourceSinkType();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    /**
     * Sink ::= IDENTIFIER | KW_SCREEN
     */
    @Test
    public void Sink() throws SyntaxException, LexicalException {
        String input = "yasho";
		/*
		 * String input = "yasho";
		 * String input = "SCREEN";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.sink();
    }

    /**
     * Sink ::= IDENTIFIER | KW_SCREEN
     */
    @Test
    public void SinkFail() throws SyntaxException, LexicalException {
        String input = "123";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.sink();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    /**
     * ImageOutStatement ::= IDENTIFIER OP_RARROW Sink
     */
    @Test
    public void ImageOutStatement() throws SyntaxException, LexicalException {
        String input = "yasho->SCREEN";
		/*
		 * String input = "yasho";
		 * String input = "SCREEN";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.imageOutStatement();
    }

    /**
     * ImageOutStatement ::= IDENTIFIER OP_RARROW Sink
     */
    @Test
    public void ImageOutStatementFail() throws SyntaxException, LexicalException {
        String input = "yasho->123";
		/*
		 * String input = "123->SCREEN";
		 * String input = "yasho<-SCREEN";
		 * String input = "yasho->123";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.imageOutStatement();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    /**
     * ImageInStatement ::= IDENTIFIER OP_LARROW Source
     */
    @Test
    public void ImageInStatement() throws SyntaxException, LexicalException {
        String input = "yasho<-yasho";
		/*
		 * String input = "yasho<-@2";
		 * String input = "yasho<-\"yasho\"";
		 * String input = "yasho<-yasho";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.imageInStatement();
    }

    /**
     * ImageInStatement ::= IDENTIFIER OP_LARROW Source
     */
    @Test
    public void ImageInStatementFail() throws SyntaxException, LexicalException {
        String input = "yasho<-@";
		/*
		 * String input = "yasho<-@";
		 * String input = "yasho->\"yasho\"";
		 * String input = "yasho<-123";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.imageInStatement();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    /**
     * Lhs::=  IDENTIFIER ( LSQUARE LhsSelector RSQUARE   | Epsilon )
     */
    @Test
    public void lhs() throws SyntaxException, LexicalException {
        String input = "yasho";
		/*
		 * String input = "yasho";
		 * String input = "yasho[[x,y]]";
		 * String input = "yasho[[r,A]]";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.lhs();
    }

    /**
     * ImageInStatement ::= IDENTIFIER OP_LARROW Source
     */
    @Test
    public void lhsFail() throws SyntaxException, LexicalException {
        String input = "yasho[[x,y]";
		/*
		 * String input = "int";
		 * String input = "yasho[[x,y]";
		 * String input = "yasho[[r,a]]";
		 * String input = "int";
		 * String input = "4";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.lhs();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    /**
     * IdentOrPixelSelectorExpression::=  IDENTIFIER LSQUARE Selector RSQUARE   | IDENTIFIER
     */
    @Test
    public void identOrPixelSelectorExpression() throws SyntaxException, LexicalException {
        String input = "yasho[true,y]";
		/*
		 * String input = "yasho[1,y]";
		 * String input = "yasho";
		 * String input = "yasho[true,y]";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.identOrPixelSelectorExpression();
    }

    /**
     * IdentOrPixelSelectorExpression::=  IDENTIFIER LSQUARE Selector RSQUARE   | IDENTIFIER
     */
    @Test
    public void identOrPixelSelectorExpressionFail() throws SyntaxException, LexicalException {
        String input = "yasho [,y]";
		/*
		 * String input = "yasho[sin,y]";
		 * String input = "yasho[false,y";
		 * String input = "yasho [,y]";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.identOrPixelSelectorExpression();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    /**
     * Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN | FunctionApplication | BOOLEAN_LITERAL
     */
    @Test
    public void Primary() throws SyntaxException, LexicalException {
        String input = "(2?true:2==2==6+7&6)";
		/*
		 * String input = "123";
		 * String input = "true";
		 * String input = "yasho[true,y]";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.primary();
    }


    @Test
    public void testyasho1() throws SyntaxException, LexicalException {
        String input = "(a*b),c*d";
		/*
		 * String input = "123";
		 * String input = "true";
		 * String input = "yasho[true,y]";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.selector();
    }

    @Test
    public void testyasho2() throws SyntaxException, LexicalException {
        String input = "++++x";
		/*
		 * String input = "123";
		 * String input = "true";
		 * String input = "yasho[true,y]";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.expression();
    }

    @Test
    public void testyasho3() throws SyntaxException, LexicalException {
        String input = "var[[x,y]]=5;";
		/*
		 * String input = "123";
		 * String input = "true";
		 * String input = "yasho[true,y]";
		 */
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.lhs();
    }


    @Test
    public void expression03() throws SyntaxException, LexicalException {
        String input = "x * 43 | 2 + 2";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        //Call expression directly.

        parser.expression();   //Parse the program

    }

    @Test
    public void expression04() throws SyntaxException, LexicalException {
        String input = "x * 43 OP_OR 2 + 2";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        //Call expression directly.

        parser.expression();   //Parse the program
    }


    //Adhiraj

    private Parser startParser(String input) throws LexicalException {
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        return new Parser(scanner);
    }

    @Test
    public void expression2adj() throws SyntaxException, LexicalException {
        String input = "2 @";
        Parser parser = startParser(input);
        parser.expression();  //Call expression directly.
    }


    @Test
    public void expression3adj() throws SyntaxException, LexicalException {
        String input = "x * 43 | 2 + 2";
        Parser parser = startParser(input);


        parser.expression();   //Parse the program

    }


    @Test
    public void expression4adj() throws SyntaxException, LexicalException {
        String input = "x * 43 OP_OR 2 + 2";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }


    @Test
    public void xySelector11() throws SyntaxException, LexicalException {
        String input = "prog ide [[x,y]] =2;";
        Parser parser = startParser(input);
        parser.parse();
    }

    @Test
    public void raSelector1() throws SyntaxException, LexicalException {
        String input = "prog ide [[r,a]] =2;";
        Parser parser = startParser(input);

        parser.parse();
    }


    @Test
    public void functionApplication1() throws SyntaxException, LexicalException {
        String input = "polar_r (2)   ";
        Parser parser = startParser(input);
        parser.expression();
    }

    @Test
    public void functionApplication3() throws SyntaxException, LexicalException {
        String input = "polar_r [2,2]   ";
        Parser parser = startParser(input);
        parser.expression();
    }

    @Test
    public void functionApplication2adj() throws SyntaxException, LexicalException {
        String input = "sin (2 @)   ";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.expression();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void selector11() throws SyntaxException, LexicalException {
        String input = "prog ide [[r,a]] = sin [ 2,2 ? X :x ];";
        Parser parser = startParser(input);
        parser.parse();
    }

    @Test
    public void selector3() throws SyntaxException, LexicalException {
        String input = "prog ide [[r,a]] = sin [ 2,2 ];";
        Parser parser = startParser(input);
        parser.parse();
    }

    @Test
    public void selector2() throws SyntaxException, LexicalException {
        String input = "prog ide [[r,A]] = sin [ x,@ ];";

        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }

    }


    @Test
    public void lhs1() throws SyntaxException, LexicalException {
        String input = "prg abc = 4 + 4+ 4;";
        Parser parser = startParser(input);
        parser.parse();
    }

    @Test
    public void lhs2() throws SyntaxException, LexicalException {
        String input = "prog abc [ = 5 + 5";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }

    }

    @Test
    public void lhs3() throws SyntaxException, LexicalException {
        String input = "prague abc [ [x,y]  ] = true;";
        Parser parser = startParser(input);
        parser.parse();
    }


    @Test
    public void identOrPixelSelectorExpression1() throws SyntaxException, LexicalException {
        String input = "abc ";
        Parser parser = startParser(input);
        parser.expression();
    }

    @Test
    public void identOrPixelSelectorExpression2() throws SyntaxException, LexicalException {
        String input = "abc [x,x] ";
        Parser parser = startParser(input);
        parser.expression();
    }

    @Test
    public void identOrPixelSelectorExpression3() throws SyntaxException, LexicalException {
        String input = "abc [";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.expression();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void primary11() throws SyntaxException, LexicalException {
        String input = "42 ";
        Parser parser = startParser(input);
        parser.expression();
    }

    @Test
    public void primary211() throws SyntaxException, LexicalException {
        String input = "true";
        Parser parser = startParser(input);
        parser.expression();
    }

    @Test
    public void primary31() throws SyntaxException, LexicalException {
        String input = "(x)";
        Parser parser = startParser(input);
        parser.expression();
    }

    @Test
    public void primary4() throws SyntaxException, LexicalException {
        String input = "polar_r (2)";
        Parser parser = startParser(input);
        parser.expression();
    }

    @Test
    public void primary5() throws SyntaxException, LexicalException {
        String input = "abc [";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.expression();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void unaryExpressionNotPlusMinus1() throws SyntaxException, LexicalException {
        String input = "42 ";
        Parser parser = startParser(input);
        parser.expression();
    }

    @Test
    public void unaryExpressionNotPlusMinus2() throws SyntaxException, LexicalException {
        String input = "R ";
        Parser parser = startParser(input);
        parser.expression();
    }

    @Test
    public void unaryExpressionNotPlusMinus3() throws SyntaxException, LexicalException {
        String input = "!+++++++++!+++++++------------x ";
        Parser parser = startParser(input);
        parser.expression();
    }

    @Test
    public void unaryExpressionNotPlusMinus4() throws SyntaxException, LexicalException {
        String input = "true1 [2,2]";
        Parser parser = startParser(input);
        parser.expression();
    }

    @Test
    public void multExpression1() throws SyntaxException, LexicalException {
        String input = "x* 43";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void multExpression2() throws SyntaxException, LexicalException {
        String input = "++++++x";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void multExpression3() throws SyntaxException, LexicalException {
        String input = "x* 43 * 44* 45 * 46";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void multExpression4() throws SyntaxException, LexicalException {
        String input = "false* 43 * 44/ 45 % 46";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void addExpression1() throws SyntaxException, LexicalException {
        String input = "++--y";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void addExpression2() throws SyntaxException, LexicalException {
        String input = "++++++x + +++++x";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void addExpression3() throws SyntaxException, LexicalException {
        String input = "++++++x - +++++x";

        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void addExpression4() throws SyntaxException, LexicalException {
        String input = "@";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.expression();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void relExpression1() throws SyntaxException, LexicalException {
        String input = "y";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void relExpression2() throws SyntaxException, LexicalException {
        String input = "++++++x > +++++x";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void eqExpression1() throws SyntaxException, LexicalException {
        String input = "y = y";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void eqExpression2() throws SyntaxException, LexicalException {
        String input = "++++++x != +++++x";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void andExpression1() throws SyntaxException, LexicalException {
        String input = "y = y & x = x";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void orExpression1() throws SyntaxException, LexicalException {
        String input = "++++++x != +++++x | ++++++x != +++++x";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void orExpression2() throws SyntaxException, LexicalException {
        String input = "Z";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }

    @Test
    public void assignmentStatement1() throws SyntaxException, LexicalException {
        String input = "prog xx = x;";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }

    @Test
    public void assignmentStatement2() throws SyntaxException, LexicalException {
        String input = "prog 2 + 2;";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();   //Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void imageInStatement1() throws SyntaxException, LexicalException {
        String input = "prog xx <- xx;";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }

    @Test
    public void imageInStatement2() throws SyntaxException, LexicalException {
        String input = "prog x2 + 2;";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();   //Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void sink1() throws SyntaxException, LexicalException {
        String input = "prog xxxsdds -> mf;";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }

    @Test
    public void sink2() throws SyntaxException, LexicalException {
        String input = "prog dsf -> SCREEN;";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }

    @Test
    public void statement11() throws SyntaxException, LexicalException {
        String input = "df xx = y ? z: f;";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }

    @Test
    public void statement21() throws SyntaxException, LexicalException {
        String input = "ds xx = y ? z; f;";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();   //Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void imageDeclaration1() throws SyntaxException, LexicalException {
        String input = "dsd image xx <- \"yabadabadooo\";";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }

    @Test
    public void imageDeclaration2() throws SyntaxException, LexicalException {
        String input = "df image [y2,x2];";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();   //Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void sourceSinkDeclaration1() throws SyntaxException, LexicalException {
        String input = "uuu file xxyz = \"42\";";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }

    @Test
    public void sourceSinkDeclaration2() throws SyntaxException, LexicalException {
        String input = "yu url yzyywdc  = @;";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();   //Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void variableDeclaration1() throws SyntaxException, LexicalException {
        String input = "iu boolean  jbbhh  = xxz;";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }

    @Test
    public void variableDeclaration2() throws SyntaxException, LexicalException {
        String input = "uiui boolean zzxz = @;";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();   //Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void declaration1() throws SyntaxException, LexicalException {
        String input = "op image xxx;";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }

    @Test
    public void declaration2() throws SyntaxException, LexicalException {
        String input = "jk @;";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();   //Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void fullprogram1() throws SyntaxException, LexicalException {
        String input = "axby boolean gitang=a;";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }


    @Test
    public void fullprogram2() throws SyntaxException, LexicalException {
        String input = "axby boolean gitang=a*21-42>true;";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }


    @Test
    public void fullprogram3() throws SyntaxException, LexicalException {
        String input = "axby boolean gitang=a*21-42>true!=trade|!+sin(5+2)&cart_y[2+2,6+true];";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }

    //Raktima Test Cases

    @Test
    public void testDec21() throws LexicalException, SyntaxException {
        String input = "hello";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);  //
        parser.parse();
    }


    @Test
    public void expression2() throws SyntaxException, LexicalException {
        String input = ";;\n;;";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();  //Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void expression31() throws SyntaxException, LexicalException {
        String input = "z@file";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();  //Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void expression4() throws SyntaxException, LexicalException {
        String input = "abc int def;";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.parse();  //Parse the program
    }

    @Test
    public void expression5() throws SyntaxException, LexicalException {
        String input = "abc";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.parse();  //Parse the program
    }

    @Test
    public void expression6() throws SyntaxException, LexicalException {
        String input = "a==b";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();  //Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void expression7() throws SyntaxException, LexicalException {
        String input = "ABC\r\nabc";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();  //Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testDec31() throws LexicalException, SyntaxException {
        String input = "++++x";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);  //
        parser.expression();
    }

    @Test
    public void testDec41() throws LexicalException, SyntaxException {
        String input = "+x=x";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);  //
        parser.expression();
    }

    @Test
    public void testDec51() throws LexicalException, SyntaxException {
        String input = "+++x = +(+(+x)) = x";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);  //
        parser.expression();
    }

    @Test
    public void testDec61() throws LexicalException, SyntaxException {
        String input = "boolean val=false;";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.parse();  //Parse the program
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testDec7() throws LexicalException, SyntaxException {
        String input = "myprog boolean val=false";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);  //
        parser.expression();
    }

    @Test
    public void testDec8() throws LexicalException, SyntaxException {
        String input = "a=a-+b";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);  //
        parser.expression();
    }

    @Test
    public void sourceSinkTypeFai3() throws SyntaxException, LexicalException {
        //String input = "URL";
        String input = "(a*b,c*d";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.sourceSinkType();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void sourceSinkTypeFai4() throws SyntaxException, LexicalException {
        //String input = "URL";
        String input = "(a*b),c*d";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.sourceSinkType();
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void sourceSinkTypeFail1() throws SyntaxException, LexicalException {
        String input = "b = b-+c";
		/*String input = "URL";
		String input = "FILE";*/
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.statement();
    }


    //Gitang

    @Test
    public void fullprogram1git() throws SyntaxException, LexicalException {
        String input = "axby boolean gitang=a;";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }


    @Test
    public void fullprogram2git() throws SyntaxException, LexicalException {
        String input = "axby boolean gitang=a*21-42>true;";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }


    @Test
    public void fullprogram3git() throws SyntaxException, LexicalException {
        String input = "axby boolean gitang=a*21-42>true!=trade|!+sin(5+2)&cart_y[2+2,6+true];";
        Parser parser = startParser(input);

        parser.parse();   //Parse the program
    }

    @Test
    public void testcase21() throws SyntaxException, LexicalException {
        String input = "assign int abc=123456;\n"
                + "abc[[x,y]]=123456;\n"
                + "abc[[r,a]]=123244;\n";//Assignment statement
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser Parser = new Parser(scanner);
        Parser.program();  //Parse the program
    }

    @Test
    public void testcase20() throws SyntaxException, LexicalException {
        String input = "imageProgram image imageName;"
                + "\n imageName->abcdpng; "
                + "\n imageName -> SCREEN; "
                + "\n imageName <- \"awesome\";"
                + "\n imageName <- @express; \n"
                + "\n imageName <- abcdpng;";  // Image related Test cases
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser Parser = new Parser(scanner);
        Parser.program();  //Parse the program
    }


    @Test
    public void testcase551() throws SyntaxException, LexicalException {
        String input = "(6*2/23/4*22*sin(x))||(abs(6*2*12)+cart_x[x,y]+cart_y[(6/23),(7/23)]+polar_a[6/2/2,2/3/4]+polar_r(z))&true";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        
        thrown.expect(SyntaxException.class);
        try {
            parser.expression(); //Call expression directly.
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    //Pawas
    @Test
    public void identOrPixelSelectorExpression1pws() throws SyntaxException, LexicalException {
        String input = "var [ true, true ]";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.identOrPixelSelectorExpression(); //Call expression directly.
    }


    @Test
    public void identOrPixelSelectorExpression2pws() throws SyntaxException, LexicalException {
        String input = "var";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.identOrPixelSelectorExpression(); //Call expression directly.
    }


    @Test
    public void identOrPixelSelectorExpression3pws() throws SyntaxException, LexicalException {
        String input = "cos";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.identOrPixelSelectorExpression(); //Call expression directly.
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void lhs1pws() throws SyntaxException, LexicalException {
        String input = "hello";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.lhs(); //Call expression directly.
    }


    @Test
    public void lhs2pws() throws SyntaxException, LexicalException {
        String input = "world [ [x,y] ]";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.lhs(); //Call expression directly.
    }


    @Test
    public void lhs3pws() throws SyntaxException, LexicalException {
        String input = "hello [[r,a]]";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.lhs(); //Call expression directly.
    }


    @Test
    public void lhs4pws() throws SyntaxException, LexicalException {
        String input = "[[x,y]]";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.lhs(); //Call expression directly.
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void functionApplication1pws() throws SyntaxException, LexicalException {
        String input = "sin ( x | y)";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.functionApplication(); //Call expression directly.
    }


    @Test
    public void functionApplication2pws() throws SyntaxException, LexicalException {
        String input = "atan [ X | Y, Y | Z ]";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.functionApplication(); //Call expression directly.
    }


    @Test
    public void functionApplication3pws() throws SyntaxException, LexicalException {
        String input = "atan []";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.functionApplication(); //Call expression directly.
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void functionName1pws() throws SyntaxException, LexicalException {
        String input = "sin";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.functionName(); //Call expression directly.
    }


    @Test
    public void functionName2pws() throws SyntaxException, LexicalException {
        String input = "polar_r";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.functionName(); //Call expression directly.
    }


    @Test
    public void functionName3pws() throws SyntaxException, LexicalException {
        String input = "x";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.functionName(); //Call expression directly.
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void lhsSelector11() throws SyntaxException, LexicalException {
        String input = "[ x , y ]";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.lhsSelector(); //Call expression directly.
    }


    @Test
    public void lhsSelector2() throws SyntaxException, LexicalException {
        String input = "[r , a]";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.lhsSelector(); //Call expression directly.
    }


    @Test
    public void lhsSelector3() throws SyntaxException, LexicalException {
        String input = "[A,r]";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.lhsSelector(); //Call expression directly.
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void xySelector1pws() throws SyntaxException, LexicalException {
        String input = "x,y";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.xySelector(); //Call expression directly.
    }


    @Test
    public void xySelector2pws() throws SyntaxException, LexicalException {
        String input = "y";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.xySelector(); //Call expression directly.
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void raSelector1pws() throws SyntaxException, LexicalException {
        String input = "r,a";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.raSelector(); //Call expression directly.
    }


    @Test
    public void raSelector2() throws SyntaxException, LexicalException {
        String input = "r";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.raSelector(); //Call expression directly.
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void selector1pws() throws SyntaxException, LexicalException {
        String input = "X | Y, Y | Z";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.selector(); //Call expression directly.
    }


    @Test
    public void selector2pws() throws SyntaxException, LexicalException {
        String input = "true, true";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        parser.selector(); //Call expression directly.
    }


    @Test
    public void selector3pws() throws SyntaxException, LexicalException {
        String input = "true";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            parser.selector(); //Call expression directly.
        } catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    //Akhil

    @Test
    public void expression2akh() throws SyntaxException, LexicalException {
        String input = "2 @";
        Parser parser = startParser(input);
        parser.expression();  //Call expression directly.
    }


    @Test
    public void expression3akh() throws SyntaxException, LexicalException {
        String input = "x * 43 | 2 + 2";
        Parser parser = startParser(input);


        parser.expression();   //Parse the program

    }


    @Test
    public void expression4akh() throws SyntaxException, LexicalException {
        String input = "x * 43 OP_OR 2 + 2";
        Parser parser = startParser(input);

        parser.expression();   //Parse the program
    }



    @Test
    public void xySelector1akh() throws SyntaxException, LexicalException {
        String input = "x , y";
        Parser parser = startParser(input);
        parser.xySelector();
    }
    @Test
    public void raSelector1akh() throws SyntaxException, LexicalException {
        String input = "r,a";
        Parser parser = startParser(input);
        parser.raSelector();
    }

    @Test
    public void functionName1akh() throws SyntaxException, LexicalException {
        String input = "sin";
        Parser parser = startParser(input);
        parser.functionName();
    }
    @Test
    public void functionApplication1akh() throws SyntaxException, LexicalException {
        String input = "polar_r (2)   ";
        Parser parser = startParser(input);
        parser.functionApplication();
    }

    @Test
    public void functionApplication3akh() throws SyntaxException, LexicalException {
        String input = "polar_r [2,2]   ";
        Parser parser = startParser(input);
        parser.functionApplication();
    }
    @Test
    public void functionApplication2akh() throws SyntaxException, LexicalException {
        String input = "sin (2 @)   ";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.functionApplication();
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void selector1akh() throws SyntaxException, LexicalException {
        String input = "2,2 ? X :x";
        Parser parser = startParser(input);
        parser.selector();
    }

    @Test
    public void selector3akh() throws SyntaxException, LexicalException {
        String input = "2,2";
        Parser parser = startParser(input);
        parser.selector();
    }

    @Test
    public void selector2akh() throws SyntaxException, LexicalException {
        String input = "x,@";
        Parser parser = startParser(input);

        thrown.expect(SyntaxException.class);
        try {
            parser.selector();
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }

    }


    @Test
    public void lhs1akh() throws SyntaxException, LexicalException {
        String input = "abc ";
        Parser parser = startParser(input);
        parser.lhs();
    }
    @Test
    public void lhs2akh() throws SyntaxException, LexicalException {
        String input = "abc [";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.lhs();
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }

    }

    @Test
    public void lhs3akh() throws SyntaxException, LexicalException {
        String input = "abc [ [x,y]  ] ";
        Parser parser = startParser(input);
        parser.lhs();
    }


    @Test
    public void identOrPixelSelectorExpression1akh() throws SyntaxException, LexicalException {
        String input = "abc ";
        Parser parser = startParser(input);
        parser.identOrPixelSelectorExpression();
    }
    @Test
    public void identOrPixelSelectorExpression2akh() throws SyntaxException, LexicalException {
        String input = "abc [x,x] ";
        Parser parser = startParser(input);
        parser.identOrPixelSelectorExpression();
    }
    @Test
    public void identOrPixelSelectorExpression3akh() throws SyntaxException, LexicalException {
        String input = "abc [";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.identOrPixelSelectorExpression();
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void primary1akh() throws SyntaxException, LexicalException {
        String input = "42 ";
        Parser parser = startParser(input);
        parser.primary();
    }
    @Test
    public void primary2akh() throws SyntaxException, LexicalException {
        String input = "true";
        Parser parser = startParser(input);
        parser.primary();
    }
    @Test
    public void primary3akh() throws SyntaxException, LexicalException {
        String input = "(x)";
        Parser parser = startParser(input);
        parser.primary();
    }

    @Test
    public void primary4akh() throws SyntaxException, LexicalException {
        String input = "polar_r (2)";
        Parser parser = startParser(input);
        parser.primary();
    }

    @Test
    public void primary5akh() throws SyntaxException, LexicalException {
        String input = "abc [";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.primary();
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void unaryExpressionNotPlusMinus1akh() throws SyntaxException, LexicalException {
        String input = "42 ";
        Parser parser = startParser(input);
        parser.unaryExpressionNotPlusMinus();
    }
    @Test
    public void unaryExpressionNotPlusMinus2akh() throws SyntaxException, LexicalException {
        String input = "R ";
        Parser parser = startParser(input);
        parser.unaryExpressionNotPlusMinus();
    }
    @Test
    public void unaryExpressionNotPlusMinus3akh() throws SyntaxException, LexicalException {
        String input = "!+++++++++!+++++++------------x ";
        Parser parser = startParser(input);
        parser.unaryExpressionNotPlusMinus();
    }
    @Test
    public void unaryExpressionNotPlusMinus4akh() throws SyntaxException, LexicalException {
        String input = "true1";
        Parser parser = startParser(input);
        parser.unaryExpressionNotPlusMinus();
    }

    @Test
    public void multExpression1akh() throws SyntaxException, LexicalException {
        String input = "x* 43";
        Parser parser = startParser(input);

        parser.multExpression();   //Parse the program
    }
    @Test
    public void multExpression2akh() throws SyntaxException, LexicalException {
        String input = "++++++x";
        Parser parser = startParser(input);

        parser.multExpression();   //Parse the program
    }
    @Test
    public void multExpression3akh() throws SyntaxException, LexicalException {
        String input = "x* 43 * 44* 45 * 46";
        Parser parser = startParser(input);

        parser.multExpression();   //Parse the program
    }

    @Test
    public void multExpression4akh() throws SyntaxException, LexicalException {
        String input = "false* 43 * 44/ 45 % 46";
        Parser parser = startParser(input);

        parser.multExpression();   //Parse the program
    }
    @Test
    public void addExpression1akh() throws SyntaxException, LexicalException {
        String input = "++--y";
        Parser parser = startParser(input);

        parser.addExpression();   //Parse the program
    }
    @Test
    public void addExpression2akh() throws SyntaxException, LexicalException {
        String input = "++++++x + +++++x";
        Parser parser = startParser(input);

        parser.addExpression();   //Parse the program
    }
    @Test
    public void addExpression3akh() throws SyntaxException, LexicalException {
        String input = "++++++x - +++++x";

        Parser parser = startParser(input);

        parser.addExpression();   //Parse the program
    }

    @Test
    public void addExpression4akh() throws SyntaxException, LexicalException {
        String input = "@";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.addExpression();
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }
    @Test
    public void relExpression1akh() throws SyntaxException, LexicalException {
        String input = "y";
        Parser parser = startParser(input);

        parser.relExpression();   //Parse the program
    }
    @Test
    public void relExpression2akh() throws SyntaxException, LexicalException {
        String input = "++++++x > +++++x";
        Parser parser = startParser(input);

        parser.addExpression();   //Parse the program
    }

    @Test
    public void eqExpression1akh() throws SyntaxException, LexicalException {
        String input = "y = y";
        Parser parser = startParser(input);

        parser.eqExpression();   //Parse the program
    }
    @Test
    public void eqExpression2akh() throws SyntaxException, LexicalException {
        String input = "++++++x != +++++x";
        Parser parser = startParser(input);

        parser.eqExpression();   //Parse the program
    }
    @Test
    public void andExpression1akh() throws SyntaxException, LexicalException {
        String input = "y = y & x = x";
        Parser parser = startParser(input);

        parser.eqExpression();   //Parse the program
    }
    @Test
    public void orExpression1akh() throws SyntaxException, LexicalException {
        String input = "++++++x != +++++x | ++++++x != +++++x";
        Parser parser = startParser(input);

        parser.orExpression();   //Parse the program
    }
    @Test
    public void orExpression2akh() throws SyntaxException, LexicalException {
        String input = "Z";
        Parser parser = startParser(input);

        parser.orExpression();   //Parse the program
    }

    @Test
    public void assignmentStatement1akh() throws SyntaxException, LexicalException {
        String input = "xx = x";
        Parser parser = startParser(input);

        parser.assignmentStatement();   //Parse the program
    }
    @Test
    public void assignmentStatement2akh() throws SyntaxException, LexicalException {
        String input = "2 + 2";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.assignmentStatement();   //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void imageInStatement1akh() throws SyntaxException, LexicalException {
        String input = "xx <- xx";
        Parser parser = startParser(input);

        parser.imageInStatement();   //Parse the program
    }
    @Test
    public void imageInStatement2akh() throws SyntaxException, LexicalException {
        String input = "x2 + 2";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.imageInStatement();   //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void sink1akh() throws SyntaxException, LexicalException {
        String input = "xxxsdds =";
        Parser parser = startParser(input);

        parser.sink();   //Parse the program
    }

    @Test
    public void sink2akh() throws SyntaxException, LexicalException {
        String input = "SCREEN = OLED";
        Parser parser = startParser(input);

        parser.sink();   //Parse the program
    }


    @Test
    public void imageOutStatement1akh() throws SyntaxException, LexicalException {
        String input = "xx -> SCREEN";
        Parser parser = startParser(input);

        parser.imageOutStatement();   //Parse the program
    }
    @Test
    public void imageOutStatement2akh() throws SyntaxException, LexicalException {
        String input = "xxx <- SCREEN1";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.imageOutStatement();   //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }
    @Test
    public void statement1akh() throws SyntaxException, LexicalException {
        String input = "xx = y ? z: f";
        Parser parser = startParser(input);

        parser.statement();   //Parse the program
    }
    @Test
    public void statement2akh() throws SyntaxException, LexicalException {
        String input = "xx = y ? z; f";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.statement();   //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }
    @Test
    public void imageDeclaration1akh() throws SyntaxException, LexicalException {
        String input = "image xx <- \"yabadabadooo\"";
        Parser parser = startParser(input);

        parser.imageDeclaration();   //Parse the program
    }
    @Test
    public void imageDeclaration2akh() throws SyntaxException, LexicalException {
        String input = "image [y2,x2]";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.imageDeclaration();   //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }
    @Test
    public void source1akh() throws SyntaxException, LexicalException {
        String input = " \"yabadabadooo\"";
        Parser parser = startParser(input);

        parser.source();   //Parse the program
    }
    @Test
    public void source2akh() throws SyntaxException, LexicalException {
        String input = "@ 2";
        Parser parser = startParser(input);

    }
    @Test
    public void sourceSinkDeclaration1akh() throws SyntaxException, LexicalException {
        String input = "file xxyz = \"42\"";
        Parser parser = startParser(input);

        parser.sourceSinkDeclaration();   //Parse the program
    }
    @Test
    public void sourceSinkDeclaration2akh() throws SyntaxException, LexicalException {
        String input = "url yzyywdc  = @";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.sourceSinkDeclaration();   //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void varType1akh() throws SyntaxException, LexicalException {
        String input = " boolean";
        Parser parser = startParser(input);

        parser.varType();   //Parse the program
    }
    @Test
    public void variableDeclaration1akh() throws SyntaxException, LexicalException {
        String input = "boolean  jbbhh  = xxz";
        Parser parser = startParser(input);

        parser.variableDeclaration();   //Parse the program
    }
    @Test
    public void variableDeclaration2akh() throws SyntaxException, LexicalException {
        String input = "boolean zzxz = @";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.variableDeclaration();   //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void declaration1akh() throws SyntaxException, LexicalException {
        String input = "image xxx";
        Parser parser = startParser(input);

        parser.declaration();   //Parse the program
    }
    @Test
    public void declaration2akh() throws SyntaxException, LexicalException {
        String input = "@";
        Parser parser = startParser(input);
        thrown.expect(SyntaxException.class);
        try {
            parser.declaration();   //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }
	
    /**
     * 
     * PAWAS
     */
    
    @Test
	public void identOrPixelSelectorExpression15() throws SyntaxException, LexicalException {
		String input = "var [ true, true ]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.identOrPixelSelectorExpression();  //Call expression directly.  
	}

	@Test
	public void identOrPixelSelectorExpression25() throws SyntaxException, LexicalException {
		String input = "var";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Expression_Ident ast = (Expression_Ident)parser.identOrPixelSelectorExpression();
		show(ast);
		assertEquals(IDENTIFIER, ast.firstToken.kind);
	}

	@Test
	public void identOrPixelSelectorExpression35() throws SyntaxException, LexicalException {
		String input = "cos";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner); 
		thrown.expect(SyntaxException.class);
		try {
			parser.identOrPixelSelectorExpression();  //Call expression directly.  
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void lhs15() throws SyntaxException, LexicalException {
		String input = "hello";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		LHS ast = (LHS)parser.lhs();
		show(ast);
		assertEquals(IDENTIFIER, ast.firstToken.kind);
	}

	@Test
	public void lhs25() throws SyntaxException, LexicalException {
		String input = "world [ [x,y] ]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		LHS ast = (LHS)parser.lhs();
		show(ast);
		assertEquals(IDENTIFIER, ast.firstToken.kind);
		assertEquals(Expression_PredefinedName.class, ast.index.e0.getClass());
		assertEquals(Expression_PredefinedName.class, ast.index.e1.getClass());
	}

	@Test
	public void lhs35() throws SyntaxException, LexicalException {
		String input = "hello [[r,a]]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		LHS ast = (LHS)parser.lhs();
		show(ast);
		assertEquals(IDENTIFIER, ast.firstToken.kind);
		assertEquals(Expression_PredefinedName.class, ast.index.e0.getClass());
		assertEquals(Expression_PredefinedName.class, ast.index.e1.getClass());
	}

	@Test
	public void lhs4() throws SyntaxException, LexicalException {
		String input = "[[x,y]]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner); 
		thrown.expect(SyntaxException.class);
		try {
			parser.lhs();  //Call expression directly.  
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void functionApplication15() throws SyntaxException, LexicalException {
		String input = "sin ( x | y)";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Expression_FunctionAppWithExprArg ast = (Expression_FunctionAppWithExprArg)parser.functionApplication();
		show(ast);
		assertEquals(KW_sin, ast.function);
		assertEquals(Expression_Binary.class, ast.arg.getClass());
	}

	@Test
	public void functionApplication25() throws SyntaxException, LexicalException {
		String input = "atan [ X | Y, Y | Z ]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Expression_FunctionAppWithIndexArg ast = (Expression_FunctionAppWithIndexArg)parser.functionApplication();
		show(ast);
		assertEquals(KW_atan, ast.function);
		assertEquals(Expression_Binary.class, ast.arg.e0.getClass());
		assertEquals(Expression_Binary.class, ast.arg.e1.getClass());
	}

	@Test
	public void functionApplication35() throws SyntaxException, LexicalException {
		String input = "atan []";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner); 
		thrown.expect(SyntaxException.class);
		try {
			parser.functionApplication();  //Call expression directly.  
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void functionName3() throws SyntaxException, LexicalException {
		String input = "x";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner); 
		thrown.expect(SyntaxException.class);
		try {
			parser.functionName();  //Call expression directly.  
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void lhsSelector15() throws SyntaxException, LexicalException {
		String input = "[ x , y ]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Index ast = (Index)parser.lhsSelector();
		show(ast);
		assertEquals(Expression_PredefinedName.class, ast.e0.getClass());
		assertEquals(Expression_PredefinedName.class, ast.e1.getClass());
	}

	@Test
	public void lhsSelector25() throws SyntaxException, LexicalException {
		String input = "[r , a]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Index ast = (Index)parser.lhsSelector();
		show(ast);
		assertEquals(Expression_PredefinedName.class, ast.e0.getClass());
		assertEquals(Expression_PredefinedName.class, ast.e1.getClass());
	}

	@Test
	public void lhsSelector35() throws SyntaxException, LexicalException {
		String input = "[A,r]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner); 
		thrown.expect(SyntaxException.class);
		try {
			parser.lhsSelector();  //Call expression directly.  
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void xySelector15() throws SyntaxException, LexicalException {
		String input = "x,y";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Index ast = (Index)parser.xySelector();
		show(ast);
		assertEquals(Expression_PredefinedName.class, ast.e0.getClass());
		assertEquals(Expression_PredefinedName.class, ast.e1.getClass());
	}

	@Test
	public void xySelector2() throws SyntaxException, LexicalException {
		String input = "y";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner); 
		thrown.expect(SyntaxException.class);
		try {
			parser.xySelector();  //Call expression directly.  
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void raSelector15() throws SyntaxException, LexicalException {
		String input = "r,a";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Index ast = (Index)parser.raSelector();
		show(ast);
		assertEquals(Expression_PredefinedName.class, ast.e0.getClass());
		assertEquals(Expression_PredefinedName.class, ast.e1.getClass());
	}

	@Test
	public void raSelector25() throws SyntaxException, LexicalException {
		String input = "r";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner); 
		thrown.expect(SyntaxException.class);
		try {
			parser.raSelector();  //Call expression directly.  
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void selector15() throws SyntaxException, LexicalException {
		String input = "X | Y, Y | Z";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Index ast = (Index)parser.selector();
		show(ast);
		assertEquals(Expression_Binary.class, ast.e0.getClass());
		assertEquals(Expression_Binary.class, ast.e1.getClass());
	}

	@Test
	public void selector52() throws SyntaxException, LexicalException {
		String input = "true, true";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Index ast = (Index)parser.selector();
		show(ast);
		assertEquals(Expression_BooleanLit.class, ast.e0.getClass());
		assertEquals(Expression_BooleanLit.class, ast.e1.getClass());
	}

	@Test
	public void selector35() throws SyntaxException, LexicalException {
		String input = "true";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner); 
		thrown.expect(SyntaxException.class);
		try {
			parser.selector();; //Parse the program, which should throw an exception
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test 
	public void program_parser_noDecOrStatement_invalid() throws SyntaxException, LexicalException {
	String input = "ident1 ;";
	//String input = "ident1 ident2 [[x,y]] = !123 ;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_assignStatement_invalid() throws SyntaxException, LexicalException {
	String input = "ident1 ident2 [x,y] = !123 ;";
	//String input = "ident1 ident2 [[x,y]] = !123 ;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_assignStatement_noAssign_invalid() throws SyntaxException, LexicalException {
	//String input = "ident1 ident2  true;";
	String input = "ident1 ident2 [[x,y]]  !123 ;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_assignStatement_noExpression_invalid() throws SyntaxException, LexicalException {
	//String input = "ident1 ident2 =;";
	String input = "ident1 ident2 [[x,y]] =;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_inStatement_noRarrow_invalid() throws SyntaxException, LexicalException {
	String input = "prog k @123;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_inStatement_noSink_invalid() throws SyntaxException, LexicalException {
	String input = "prog k <-;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_outStatement_noLarrow_invalid() throws SyntaxException, LexicalException {
	//String input = "ident1 ident2  identifier;";
	String input = "ident1 ident2  SCREEN;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_outStatement_noSource_invalid() throws SyntaxException, LexicalException {
	//String input = "ident1 ident2 -> ;";
	String input = "ident1 ident2 -> ;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_sourceDeclaration_noIdentifier_invalid() throws SyntaxException, LexicalException {
	//String input = "ident1 url = \"me1333\";";
	String input = "ident1 file = @ sin[(123), true];";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_sourceDeclaration_noAssign_invalid() throws SyntaxException, LexicalException {
	//String input = "ident1 url ident2  \"me1333\";";
	String input = "ident1 file ident2  @ sin[(123), true];";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_sourceDeclaration_noSource_invalid() throws SyntaxException, LexicalException {
	//String input = "ident1 url ident2 = ;";
	String input = "ident1 file ident2 = ;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_imageDeclaration_noIdentifier_invalid() throws SyntaxException, LexicalException {
	String input = "ident image [x, y]  <- \"jugraj\";";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_imageDeclaration_noIdentifier1_invalid() throws SyntaxException, LexicalException {
	String input = "ident image <- \"jugraj\";";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_imageDeclaration_noIdentifier2_invalid() throws SyntaxException, LexicalException {
	String input = "ident image ;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_imageDeclaration__invalid() throws SyntaxException, LexicalException {
	//String input = "ident image [x y] ident2 <- \"jugraj\";";
	//String input = "ident image [x, y ident2 <- \"jugraj\";";
	//String input = "ident image [x, y ident2 \"jugraj\";";
	//String input = "ident image x, y] ident2 \"jugraj\";";
	String input = "ident image [x, y] ident2  \"jugraj\";";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_variableDeclaration_noExpression_invalid() throws SyntaxException, LexicalException {
	String input = "prog int = ;//comment\nint k = sin(c+b/2);";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_variableDeclaration_noIdentifier_invalid() throws SyntaxException, LexicalException {
	String input = "prog int = sin(c+b/2);//comment\nint k = sin(c+b/2);";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test
	public void unaryExpression_invalid3() throws SyntaxException, LexicalException {
	String input = "(!DEF_X!)";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	parser.expression();  //Parse the program
	}catch (SyntaxException e) {
	show(e);
	throw e;
	}
	}
	@Test
	public void expression14() throws SyntaxException, LexicalException {
	String input = "2";
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);  
	Expression_IntLit ast = (Expression_IntLit) parser.expression();  //Call expression directly.  
	show(ast);
	assertEquals(2, ast.value);
	}
	@Test
	public void expression_valid() throws SyntaxException, LexicalException {
	String input = "+x?(true):sin[false,false]";
	show(input);
	Scanner scanner = new Scanner(input).scan();  
	show(scanner);   
	Parser parser = new Parser(scanner);  
	Expression_Conditional ast = (Expression_Conditional) parser.expression();  //Call expression directly.  
	show(ast);
	assertEquals(OP_PLUS, ast.firstToken.kind);
	Expression_Unary condition = (Expression_Unary) ast.condition;
	assertEquals(OP_PLUS, condition.op);
	Expression_PredefinedName x = (Expression_PredefinedName) condition.e;
	assertEquals("x", x.firstToken.getText());
	Expression_BooleanLit trueExp = (Expression_BooleanLit) ast.trueExpression;
	assertEquals(true, trueExp.value);
	Expression_FunctionAppWithIndexArg falseExp = (Expression_FunctionAppWithIndexArg) ast.falseExpression;
	assertEquals(KW_sin, falseExp.function);
	Index arg = falseExp.arg;
	Expression_BooleanLit e0 = (Expression_BooleanLit) arg.e0;
	assertEquals(false, e0.value);
	Expression_BooleanLit e1 = (Expression_BooleanLit) arg.e1;
	assertEquals(false, e1.value);
	}
	/*
	* To check simple unary expressions
	*/
	@Test
	public void unaryExpression_invalid() throws SyntaxException, LexicalException {
	String input = "+";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	parser.expression();  //Parse the program
	}catch (SyntaxException e) {
	show(e);
	throw e;
	}
	}
	@Test
	public void unaryExpression_invalid1() throws SyntaxException, LexicalException {
	String input = "-";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	parser.expression();  //Parse the program
	}catch (SyntaxException e) {
	show(e);
	throw e;
	}
	}
	@Test
	public void unaryExpression_invalid2() throws SyntaxException, LexicalException {
	String input = "!";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	parser.expression();  //Parse the program
	}catch (SyntaxException e) {
	show(e);
	throw e;
	}
	}
	@Test
	public void unaryExpression_valid() throws SyntaxException, LexicalException {
	String input = "+1";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);  
	Expression_Unary ast = (Expression_Unary) parser.expression();  //Call expression directly.  
	show(ast);
	assertEquals(OP_PLUS, ast.op);
	Expression_IntLit exp_int = (Expression_IntLit) ast.e;
	assertEquals(1, exp_int.value);
	}
	@Test
	public void expressionBooleanLit_valid1() throws SyntaxException, LexicalException {
	String input = "true";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);  
	Expression_BooleanLit ast = (Expression_BooleanLit) parser.expression();  //Call expression directly.  
	show(ast);
	assertEquals(true, ast.value);
	}
	@Test
	public void unaryExpression_valid2() throws SyntaxException, LexicalException {
	String input = "++x";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);  
	Expression_Unary ast = (Expression_Unary) parser.expression();  //Call expression directly.  
	show(ast);
	assertEquals(OP_PLUS, ast.op);
	Expression_Unary exp_un = (Expression_Unary) ast.e;
	assertEquals(OP_PLUS, exp_un.op);
	Expression_PredefinedName exp_pre = (Expression_PredefinedName) exp_un.e;
	assertEquals("x", exp_pre.firstToken.getText());
	}
	@Test
	public void unaryExpression_valid3() throws SyntaxException, LexicalException {
	String input = "+-+-!!true";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);  
	Expression ast = parser.expression();  //Call expression directly.  
	show(ast);
	}
	@Test
	public void unaryExpression_valid4() throws SyntaxException, LexicalException {
	String input = "!x!"; //Only !x is a valid expression and the ending ! is handled by parse()
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);  
	Expression ast = parser.expression();  //Call expression directly.  
	show(ast);
	}
	@Test
	public void unaryExpression_valid5() throws SyntaxException, LexicalException {
	String input = "identifier [exp, exp]";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);  
	Expression ast = parser.expression();  //Call expression directly.  
	show(ast);
	}
	@Test
	public void multiExpression_valid() throws SyntaxException, LexicalException {
	String input = "+1*+1/+1%+1";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);  
	Expression ast = parser.expression();  //Call expression directly.  
	show(ast);
	}
	@Test
	public void addExpression_valid() throws SyntaxException, LexicalException {
	String input = "+1*+1/+1%+1++1*+1/+1%+1-+1*+1/+1%+1";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);  
	Expression ast = parser.expression();  //Call expression directly.  
	show(ast);
	}
	@Test
	public void relExpression_valid() throws SyntaxException, LexicalException {
	String input = "+1*+1/+1%+1++1*+1/+1%+1-+1*+1/+1%+1<+1*+1/+1%+1++1*+1/+1%+1-+1*+1/+1%+1<=+1*+1/+1%+1++1*+1/+1%+1-+1*+1/+1%+1";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);  
	Expression ast = parser.expression();  //Call expression directly.  
	show(ast);
	}
	@Test
	public void eqExpression_valid() throws SyntaxException, LexicalException {
	String input = "true==false==true";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);  
	Expression ast = parser.expression();  //Call expression directly.  
	show(ast);
	}
	@Test
	public void andExpression_valid() throws SyntaxException, LexicalException {
	String input = "sin[+1,1230]&x&!!!!!false";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);  
	Expression ast = parser.expression();  //Call expression directly.  
	show(ast);
	}
	@Test
	public void andExpression_invalid() throws SyntaxException, LexicalException {
	String input = "sin[+1,0123]";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	parser.expression();  //Parse the program
	}catch (SyntaxException e) {
	show(e);
	throw e;
	}
	}
	@Test
	public void orExpression_valid() throws SyntaxException, LexicalException {
	String input = "sin[+1,1230]&x&!!!!!false | 123*0/true%(!y)";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);  
	Expression ast = parser.expression();  //Call expression directly.  
	show(ast);
	}

	@Test 
	public void program_variableDeclaration_valid() throws SyntaxException, LexicalException {
	String input = "prog int k = sin(c+b/2);//comment\nint k = sin(c+b/2);";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals(ast.name, "prog");
	Declaration_Variable dec1 = (Declaration_Variable) ast.decsAndStatements.get(0);
	assertEquals(KW_int, dec1.firstToken.kind);
	assertEquals(KW_int, dec1.type.kind);
	assertEquals("k", dec1.name);
	Expression_FunctionAppWithExprArg exp = (Expression_FunctionAppWithExprArg) dec1.e;
	assertEquals(KW_sin, exp.firstToken.kind);
	assertEquals(KW_sin, exp.function);
	Expression_Binary exp_binary = (Expression_Binary) exp.arg;
	assertEquals("c", exp_binary.firstToken.getText());
	Expression_Ident exp_identc = (Expression_Ident) exp_binary.e0;
	assertEquals("c", exp_identc.firstToken.getText());
	assertEquals("c", exp_identc.name);
	Expression_Binary exp_binary1 = (Expression_Binary) exp_binary.e1;
	assertEquals("b", exp_binary1.firstToken.getText());
	Expression_Ident exp_identb = (Expression_Ident) exp_binary1.e0;
	assertEquals("b", exp_identb.firstToken.getText());
	assertEquals("b", exp_identb.name);
	Expression_IntLit exp_intlit = (Expression_IntLit) exp_binary1.e1;
	assertEquals(2, exp_intlit.firstToken.intVal());
	assertEquals(2, exp_intlit.value);
	Declaration_Variable dec2= (Declaration_Variable) ast.decsAndStatements.get(1);
	assertEquals(KW_int, dec2.firstToken.kind);
	assertEquals(KW_int, dec2.type.kind);
	assertEquals("k", dec2.name);
	Expression_FunctionAppWithExprArg expx = (Expression_FunctionAppWithExprArg) dec2.e;
	assertEquals(KW_sin, expx.firstToken.kind);
	assertEquals(KW_sin, expx.function);
	Expression_Binary exp_binaryx = (Expression_Binary) expx.arg;
	assertEquals("c", exp_binaryx.firstToken.getText());
	Expression_Ident exp_identcx = (Expression_Ident) exp_binaryx.e0;
	assertEquals("c", exp_identcx.firstToken.getText());
	assertEquals("c", exp_identcx.name);
	Expression_Binary exp_binary1x = (Expression_Binary) exp_binaryx.e1;
	assertEquals("b", exp_binary1x.firstToken.getText());
	Expression_Ident exp_identbx = (Expression_Ident) exp_binary1x.e0;
	assertEquals("b", exp_identbx.firstToken.getText());
	assertEquals("b", exp_identbx.name);
	Expression_IntLit exp_intlitx = (Expression_IntLit) exp_binary1x.e1;
	assertEquals(2, exp_intlitx.firstToken.intVal());
	assertEquals(2, exp_intlitx.value); 
	}
	@Test 
	public void program_variableDeclaration_invalid() throws SyntaxException, LexicalException {
	String input = "prog int k = sin(c+b/2)//comment\nint k = sin(c+b/2);";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	thrown.expect(SyntaxException.class);
	try {
	ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	@Test 
	public void program_variableDeclaration_valid1() throws SyntaxException, LexicalException {
	String input = "identifier boolean bool;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals("identifier", ast.name);
	Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements.get(0);
	assertEquals("boolean", dec.firstToken.getText());
	assertEquals(KW_boolean, dec.type.kind);
	assertEquals("bool", dec.name);
	assertEquals(null, dec.e);
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
	public void program_imageDeclaration_valid() throws SyntaxException, LexicalException {
	String input = "ident image [x, y] ident2 <- \"jugraj\";";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals("ident", ast.name);
	Declaration_Image dec = (Declaration_Image) ast.decsAndStatements.get(0);
	assertEquals("ident2", dec.name);
	Expression_PredefinedName xsize = (Expression_PredefinedName) dec.xSize;
	assertEquals(KW_x, xsize.kind);
	Expression_PredefinedName ysize = (Expression_PredefinedName) dec.ySize;
	assertEquals(KW_y, ysize.kind);
	Source_StringLiteral source = (Source_StringLiteral) dec.source;
	assertEquals("jugraj", source.fileOrUrl);
	}
	@Test
	public void program_imageDeclaration_valid1() throws SyntaxException, LexicalException {
	String input = "ident1 image ident2 <- @ (123);";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals("ident1", ast.name);
	Declaration_Image dec = (Declaration_Image) ast.decsAndStatements.get(0);
	assertEquals("image", dec.firstToken.getText());
	assertEquals("ident2", dec.name);
	Source_CommandLineParam source = (Source_CommandLineParam) dec.source;
	assertEquals(OP_AT, source.firstToken.kind);
	Expression_IntLit exp = (Expression_IntLit) source.paramNum;
	assertEquals(123, exp.value);
	}
	@Test
	public void program_imageDeclaration_valid2() throws SyntaxException, LexicalException {
	String input = "ident1 image ident2 <- source_ident;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals("ident1", ast.name);
	Declaration_Image dec = (Declaration_Image) ast.decsAndStatements.get(0);
	assertEquals("ident2", dec.name);
	Source_Ident source = (Source_Ident) dec.source;
	assertEquals("source_ident", source.name);
	}
	@Test
	public void program_sourceSinkDeclaration_valid() throws SyntaxException, LexicalException {
	String input = "ident1 file ident2 = @ sin[(123), true];";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals("ident1", ast.name);
	Declaration_SourceSink dec = (Declaration_SourceSink) ast.decsAndStatements.get(0);
	assertEquals("file", dec.firstToken.getText());
	assertEquals("ident2", dec.name);
	assertEquals(KW_file, dec.type);
	Source_CommandLineParam source = (Source_CommandLineParam) dec.source;
	assertEquals(OP_AT, source.firstToken.kind);
	Expression_FunctionAppWithIndexArg exp = (Expression_FunctionAppWithIndexArg) source.paramNum;
	assertEquals(KW_sin, exp.function);
	Index arg = exp.arg;
	Expression_IntLit exp1 = (Expression_IntLit) arg.e0;
	assertEquals(123, exp1.value);
	Expression_BooleanLit exp_bool = (Expression_BooleanLit) arg.e1;
	assertEquals(true, exp_bool.value);
	}
	@Test
	public void program_sourceSinkDeclaration_valid1() throws SyntaxException, LexicalException {
	String input = "ident1 url ident2 = \"me1333\";";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals("ident1", ast.name);
	Declaration_SourceSink dec = (Declaration_SourceSink) ast.decsAndStatements.get(0);
	assertEquals(KW_url, dec.type);
	assertEquals("ident2", dec.name);
	Source_StringLiteral source = (Source_StringLiteral) dec.source;
	assertEquals("me1333", source.firstToken.getText());
	assertEquals("me1333", source.fileOrUrl);
	}
	@Test
	public void program_assignmentStatement_withoutLHSSelector_valid() throws SyntaxException, LexicalException {
	String input = "ident1 ident2 = true;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals("ident1", ast.name);
	Statement_Assign statement = (Statement_Assign) ast.decsAndStatements.get(0);
	assertEquals("ident2", statement.firstToken.getText());
	Expression_BooleanLit exp = (Expression_BooleanLit) statement.e;
	assertEquals(true, exp.value);
	LHS lhs = statement.lhs;
	assertEquals(null, lhs.index);
	assertEquals("ident2", lhs.name);
	}
	@Test
	public void program_assignmentStatement_withLHSSelector_valid1() throws SyntaxException, LexicalException {
	String input = "ident1 ident2 [[x,y]] = !123 ;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals("ident1", ast.name);
	Statement_Assign state = (Statement_Assign) ast.decsAndStatements.get(0);
	assertEquals("ident2", state.firstToken.getText());
	Expression_Unary exp = (Expression_Unary) state.e;
	assertEquals(OP_EXCL, exp.op);
	Expression_IntLit exp_int = (Expression_IntLit) exp.e;
	assertEquals(123, exp_int.value);
	LHS lhs = state.lhs;
	assertEquals("ident2", lhs.name);
	Index index = lhs.index;
	Expression_PredefinedName exp_pre1 = (Expression_PredefinedName) index.e0;
	assertEquals(KW_x, exp_pre1.kind);
	assertEquals(KW_x, exp_pre1.firstToken.kind);
	Expression_PredefinedName exp_pre2 = (Expression_PredefinedName) index.e1;
	assertEquals(KW_y, exp_pre2.kind);
	assertEquals(KW_y, exp_pre2.firstToken.kind);
	}
	@Test
	public void program_Statement_Out_valid() throws SyntaxException, LexicalException {
	String input = "ident1 ident2 -> SCREEN;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals("ident1", ast.name);
	Statement_Out state = (Statement_Out) ast.decsAndStatements.get(0);
	assertEquals("ident2", state.name);
	Sink_SCREEN sink = (Sink_SCREEN) state.sink;
	assertEquals(KW_SCREEN, sink.kind);
	}
	@Test
	public void program_Statement_Out_valid1() throws SyntaxException, LexicalException {
	String input = "ident1 ident2 -> identifier;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	show(scanner);
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals("ident1", ast.name);
	Statement_Out state = (Statement_Out) ast.decsAndStatements.get(0);
	assertEquals("ident2", state.name);
	Sink_Ident sink = (Sink_Ident) state.sink;
	assertEquals("identifier", sink.name);
	}
	@Test
	public void program_Statement_In_valid() throws LexicalException, SyntaxException {
	String input = "prog k <- @123;";
	show(input);
	Scanner scanner = new Scanner(input).scan(); 
	show(scanner); 
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals(ast.name, "prog"); 
	Statement_In state = (Statement_In) ast.decsAndStatements.get(0);
	assertEquals("k", state.firstToken.getText());
	assertEquals("k", state.name);
	Source_CommandLineParam source = (Source_CommandLineParam) state.source;
	assertEquals(OP_AT, source.firstToken.kind);
	Expression_IntLit exp = (Expression_IntLit) source.paramNum;
	assertEquals(123, exp.value);
	}
	@Test
	public void imageDeclaration_invalid() throws LexicalException, SyntaxException {
	String input = "name image [x y] identifier ;"; 
	show(input); // Display the input
	Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
	// initialize it
	show(scanner); // Display the tokens
	Parser parser = new Parser(scanner); //Create a parser
	thrown.expect(SyntaxException.class);
	try {
	parser.parse();; //Parse the program, which should throw an exception
	} catch (SyntaxException e) {
	show(e);  //catch the exception and show it
	throw e;  //rethrow for Junit
	}
	}
	//UTSA
	@Test
	public void testprogram1() throws LexicalException, SyntaxException {
	String input = "prog int g=(a+b)/2;";
	show(input);
	Scanner scanner = new Scanner(input).scan(); 
	show(scanner); 
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals(ast.name, "prog"); 
	//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
	Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
	.get(0);  
	assertEquals(KW_int, dec.type.kind);
	assertEquals("g", dec.name);
	Expression_Binary e = (Expression_Binary) dec.e;
	Expression_Binary e0 = (Expression_Binary) e.e0;
	Expression_PredefinedName a = (Expression_PredefinedName) e0.e0;
	assertEquals(KW_a, a.kind);
	assertEquals(OP_PLUS, e0.op);
	Expression_Ident b = (Expression_Ident) e0.e1;
	assertEquals("b", b.name);
	assertEquals(OP_DIV, e.op);
	Expression_IntLit n = (Expression_IntLit) e.e1;
	assertEquals(2, n.value);
	}
	@Test
	public void testprogram2() throws LexicalException, SyntaxException {
	String input = "prog image [(a+b/2),(c*y+67)] k <- @(g+h/2-6);";
	show(input);
	Scanner scanner = new Scanner(input).scan(); 
	show(scanner); 
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals(ast.name, "prog"); 
	Declaration_Image dec = (Declaration_Image) ast.decsAndStatements.get(0);
	Expression_Binary e = (Expression_Binary) dec.xSize;
	Expression_PredefinedName x = (Expression_PredefinedName) e.e0;
	assertEquals(KW_a, x.kind);
	assertEquals(OP_PLUS, e.op);
	Expression_Binary e0 = (Expression_Binary) e.e1;
	Expression_Ident b = (Expression_Ident) e0.e0;
	assertEquals("b", b.name);
	assertEquals(OP_DIV, e0.op);
	Expression_IntLit n = (Expression_IntLit) e0.e1;
	assertEquals(2, n.value);
	Expression_Binary ey = (Expression_Binary) dec.ySize;
	Expression_Binary ey0 = (Expression_Binary) ey.e0;
	Expression_Ident c = (Expression_Ident) ey0.e0;
	assertEquals("c", c.name);
	assertEquals(OP_TIMES, ey0.op);
	Expression_PredefinedName y = (Expression_PredefinedName) ey0.e1;
	assertEquals(KW_y, y.kind);
	assertEquals(OP_PLUS, ey.op);
	Expression_IntLit n1 = (Expression_IntLit) ey.e1;
	assertEquals(67, n1.value);
	assertEquals("k", dec.name);
	Source_CommandLineParam s = (Source_CommandLineParam) dec.source;
	Expression_Binary p = (Expression_Binary) s.paramNum;
	Expression_Binary p0 = (Expression_Binary) p.e0;
	Expression_Ident g = (Expression_Ident) p0.e0;
	assertEquals("g", g.name);
	assertEquals(OP_PLUS, p0.op);
	Expression_Binary p01 = (Expression_Binary) p0.e1;
	Expression_Ident h = (Expression_Ident) p01.e0;
	assertEquals("h", h.name);
	assertEquals(OP_DIV, p01.op);
	Expression_IntLit n2 = (Expression_IntLit) p01.e1;
	assertEquals(2, n2.value);
	assertEquals(OP_MINUS, p.op);
	Expression_IntLit n3 = (Expression_IntLit) p.e1;
	assertEquals(6, n3.value); 
	}
	@Test
	public void testprogram3() throws LexicalException, SyntaxException {
	String input = "prog int k = polar_r(c+b/2);//comment starts here\r\n\rint k=polar_r(c+b/2);";
	show(input);
	Scanner scanner = new Scanner(input).scan(); 
	show(scanner); 
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals(ast.name, "prog"); 
	Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements.get(0);  
	Declaration_Variable dec1 = (Declaration_Variable) ast.decsAndStatements.get(1);
	assertEquals(KW_int, dec.type.kind);
	assertEquals("k", dec.name);
	Expression_FunctionAppWithExprArg e = (Expression_FunctionAppWithExprArg) dec.e;
	assertEquals(KW_polar_r, e.function);
	Expression_Binary e0 = (Expression_Binary) e.arg;
	Expression_Ident c = (Expression_Ident) e0.e0;
	assertEquals("c", c.name);
	assertEquals(OP_PLUS, e0.op);
	Expression_Binary e01 = (Expression_Binary) e0.e1;
	Expression_Ident b = (Expression_Ident) e01.e0;
	assertEquals("b", b.name);
	Expression_IntLit n = (Expression_IntLit) e01.e1;
	assertEquals(2, n.value);
	assertEquals(KW_int, dec1.type.kind);
	assertEquals("k", dec1.name);
	Expression_FunctionAppWithExprArg f = (Expression_FunctionAppWithExprArg) dec1.e;
	assertEquals(KW_polar_r, f.function);
	Expression_Binary f0 = (Expression_Binary) f.arg;
	Expression_Ident c1 = (Expression_Ident) f0.e0;
	assertEquals("c", c1.name);
	assertEquals(OP_PLUS, f0.op);
	Expression_Binary f01 = (Expression_Binary) f0.e1;
	Expression_Ident b1 = (Expression_Ident) f01.e0;
	assertEquals("b", b1.name);
	Expression_IntLit n1 = (Expression_IntLit) f01.e1;
	assertEquals(2, n1.value); 
	}

}
