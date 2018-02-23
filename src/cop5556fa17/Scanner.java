/* *
 * Scanner for the class project in COP5556 Programming Language Principles 
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Scanner {

    static HashMap<String, Kind> KeywordMap = new HashMap<String, Kind>();
    static HashMap<String, Kind> OperatorMap = new HashMap<String, Kind>();
    static HashMap<String, Kind> SeparatorMap = new HashMap<String, Kind>();
    static HashMap<String, Kind> BooleanMap = new HashMap<String, Kind>();

    public static enum State {
        KEYWORD, START, IDENTIFIER_START, IDENTIFIER_PART, INTEGER_LITERAL, INTEGER_START, DIGIT, WHITESPACE_START, WHITESPACE,
        OPERATOR, OPERATOR_SINGLE, BOOLEAN_LITERAL, SEPARATOR, COMMENT, STRING_LITERAL, EOF;
    }

    public static enum Kind {
        IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, KW_x/* x */, KW_X/* X */, KW_y/* y */,
        KW_Y/* Y */, KW_r/* r */, KW_R/* R */, KW_a/* a */, KW_A/* A */, KW_Z/* Z */, KW_DEF_X/* DEF_X */,
        KW_DEF_Y/* DEF_Y */, KW_SCREEN/* SCREEN */, KW_cart_x/* cart_x */, KW_cart_y/* cart_y */, KW_polar_a/* polar_a */,
        KW_polar_r/* polar_r */, KW_abs/* abs */, KW_sin/* sin */, KW_cos/* cos */, KW_atan/* atan */, KW_log/* log */,
        KW_image/* image */, KW_int/* int */, KW_boolean/* boolean */, KW_url/* url */, KW_file/* file */,
        OP_ASSIGN/* = */, OP_GT/* > */, OP_LT/* < */, OP_EXCL/* ! */, OP_Q/* ? */, OP_COLON/* : */, OP_EQ/* == */,
        OP_NEQ/* != */, OP_GE/* >= */, OP_LE/* <= */, OP_AND/* & */, OP_OR/* | */,
        OP_PLUS/* + */, OP_MINUS/* - */, OP_TIMES/* * */, OP_DIV/* / */, OP_MOD/* % */, OP_POWER/* ** */, OP_AT/* @ */, OP_RARROW/* -> */, OP_LARROW/* <- */, LPAREN/* ( */,
        RPAREN/* ) */, LSQUARE/* [ */, RSQUARE/* ] */, SEMI/* ; */, COMMA/* , */, EOF;
    }

    static {

        BooleanMap.put("true", Kind.BOOLEAN_LITERAL);
        BooleanMap.put("false", Kind.BOOLEAN_LITERAL);

        KeywordMap.put("x", Kind.KW_x);
        KeywordMap.put("X", Kind.KW_X);
        KeywordMap.put("y", Kind.KW_y);
        KeywordMap.put("Y", Kind.KW_Y);
        KeywordMap.put("r", Kind.KW_r);
        KeywordMap.put("R", Kind.KW_R);
        KeywordMap.put("a", Kind.KW_a);
        KeywordMap.put("A", Kind.KW_A);
        KeywordMap.put("Z", Kind.KW_Z);
        KeywordMap.put("DEF_X", Kind.KW_DEF_X);
        KeywordMap.put("DEF_Y", Kind.KW_DEF_Y);
        KeywordMap.put("SCREEN", Kind.KW_SCREEN);
        KeywordMap.put("cart_x", Kind.KW_cart_x);
        KeywordMap.put("cart_y", Kind.KW_cart_y);
        KeywordMap.put("polar_a", Kind.KW_polar_a);
        KeywordMap.put("polar_r", Kind.KW_polar_r);
        KeywordMap.put("abs", Kind.KW_abs);
        KeywordMap.put("sin", Kind.KW_sin);
        KeywordMap.put("cos", Kind.KW_cos);
        KeywordMap.put("atan", Kind.KW_atan);
        KeywordMap.put("log", Kind.KW_log);
        KeywordMap.put("image", Kind.KW_image);
        KeywordMap.put("int", Kind.KW_int);
        KeywordMap.put("boolean", Kind.KW_boolean);
        KeywordMap.put("url", Kind.KW_url);
        KeywordMap.put("file", Kind.KW_file);

        OperatorMap.put("=", Kind.OP_ASSIGN);
        OperatorMap.put(">", Kind.OP_GT);
        OperatorMap.put("<", Kind.OP_LT);
        OperatorMap.put("!", Kind.OP_EXCL);
        OperatorMap.put("?", Kind.OP_Q);
        OperatorMap.put(":", Kind.OP_COLON);
        OperatorMap.put("==", Kind.OP_EQ);
        OperatorMap.put("!=", Kind.OP_NEQ);
        OperatorMap.put(">=", Kind.OP_GE);
        OperatorMap.put("<=", Kind.OP_LE);
        OperatorMap.put("&", Kind.OP_AND);
        OperatorMap.put("|", Kind.OP_OR);
        OperatorMap.put("+", Kind.OP_PLUS);
        OperatorMap.put("-", Kind.OP_MINUS);
        OperatorMap.put("*", Kind.OP_TIMES);
        OperatorMap.put("/", Kind.OP_DIV);
        OperatorMap.put("%", Kind.OP_MOD);
        OperatorMap.put("**", Kind.OP_POWER);
        OperatorMap.put("@", Kind.OP_AT);
        OperatorMap.put("->", Kind.OP_RARROW);
        OperatorMap.put("<-", Kind.OP_LARROW);
        OperatorMap.put("<=", Kind.OP_LE);

        SeparatorMap.put("(", Kind.LPAREN);
        SeparatorMap.put(")", Kind.RPAREN);
        SeparatorMap.put("[", Kind.LSQUARE);
        SeparatorMap.put("]", Kind.RSQUARE);
        SeparatorMap.put(";", Kind.SEMI);
        SeparatorMap.put(",", Kind.COMMA);


    }

    /**
     * Extra character added to the end of the input characters to simplify the
     * Scanner.
     */
    static final char EOFchar = 0;

    /**
     * The list of tokens created by the scan method.
     */
    final ArrayList<Token> tokens;

    /**
     * An array of characters representing the input. These are the characters
     * from the input string plus and additional EOFchar at the end.
     */
    final char[] chars;

    /**
     * position of the next token to be returned by a call to nextToken
     */
    private int nextTokenPos = 0;

    Scanner(String inputString) {
        int numChars = inputString.length();
        this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1);
        // input string terminated with null char
        chars[numChars] = EOFchar;
        tokens = new ArrayList<Token>();
    }

    /**
     * Method to scan the input and create a list of Tokens.
     * <p>
     * If an error is encountered during scanning, throw a LexicalException.
     *
     * @return
     * @throws LexicalException
     */
    public Scanner scan() throws LexicalException {
        /* TODO Replace this with a correct and complete implementation!!! */
        int initialPos = 0;
        int pos = 0;
        int line = 1;
        int posInLine = 1;
        int len = chars.length;
        char ch;
        StringBuilder sb = new StringBuilder();
        State state = State.START;

        while (pos < len) {
            switch (state) {
                case START: {
                    ch = chars[pos];

                    //Checking if it as Integer Literal Char
                    if ((int) ch > 47 && (int) ch < 58) {

                        //If '0' then simply add the token
                        if ((int) ch == 48) {
                            pos++;
                            tokens.add(new Token(Kind.INTEGER_LITERAL, initialPos, (pos - initialPos), line, posInLine));
                            posInLine = posInLine + (pos - initialPos);
                            initialPos = pos;
                            break;
                        }
                        //If a non-zero Digit is encountered, change state to INTEGER_LITERAL and check for more digits
                        else {
                            sb.append(ch);
                            state = State.INTEGER_START;
                            pos++;
                            break;
                        }
                    }

                    //Checking if it is a Identifier Start Char
                    else if (((int) ch > 64 && (int) ch < 91) || ((int) ch > 96 && (int) ch < 123) || (int) ch == 95 || (int) ch == 36) {
                        state = State.IDENTIFIER_START;
                        break;
                    }

                    //Checking if it is a WhiteSpace Char
                    else if ((int) ch == 32 || (int) ch == 9 || (int) ch == 12 || (int) ch == 10 || (int) ch == 13) {
                        state = State.WHITESPACE_START;
                        break;
                    }

                    //Checking if it is a start of a Comment
                    else if ((int) ch == 47) {
                        state = State.COMMENT;
                        break;
                    }

                    //Checking if it is an Operator
                    else if (OperatorMap.containsKey(String.valueOf(ch))) {
                        state = State.OPERATOR;
                        break;
                    }

                    //Checking if it is a Separator
                    else if (SeparatorMap.containsKey(String.valueOf(ch))) {
                        state = State.SEPARATOR;
                        break;
                    }

                    //Checking if it is a start of a String
                    else if ((int) ch == 34) {
                        state = State.STRING_LITERAL;
                        break;
                    }

                    //Checking if it is an EOF Char
                    else if (ch == EOFchar) {
                        state = State.EOF;
                        break;
                    } else {
                        throw new LexicalException("Invalid Character found", pos);
                    }


                }

                case STRING_LITERAL: {

                    ch = chars[pos];
                    sb.append(ch);
                    pos++;
                    while (state == State.STRING_LITERAL) {
                        ch = chars[pos];
                        if ((int) ch == 34) {
                            //add string token as we got "
                            sb.append(ch);
                            pos++;
                            tokens.add(new Token(Kind.STRING_LITERAL, initialPos, (pos - initialPos), line, posInLine));
                            posInLine = posInLine + (pos - initialPos);
                            initialPos = pos;
                            sb = new StringBuilder();
                            state = State.START;
                            break;
                        } else if ((int) ch == 10 || (int) ch == 13) {
                            throw new LexicalException("New Line Found within String", pos);
                        } else if (ch == EOFchar) {
                            throw new LexicalException("End of File Found within String", pos);
                        } else if (ch == '\\') {
                            /*StringBuilder constructBackslash = new StringBuilder();
                            constructBackslash.append(ch).append(chars[pos + 1]);*/
                            if (chars[pos + 1] == 'b' || chars[pos + 1] == 't' || chars[pos + 1] == 'n' || chars[pos + 1] == 'f' || chars[pos + 1] == 'r' || chars[pos + 1] == '\"' || chars[pos + 1] == '\\' || chars[pos + 1] == '\'') {
                                //add both to sb
                                sb.append(ch);
                                sb.append(chars[pos + 1]);
                                pos = pos + 2;
                            } else {
                                //we have got some char after \ which is not part of escape sequences, hence throw error
                                throw new LexicalException("Invalid Escape Character found", pos + 1);
                            }
                        } else {
                            sb.append(ch);
                            pos++;
                        }

                    }
                    break;
                }

                case IDENTIFIER_START: {
                    state = State.IDENTIFIER_PART;
                    ch = chars[pos];
                    sb.append(ch);
                    pos++;
                    while (state == State.IDENTIFIER_PART) {
                        ch = chars[pos];
                        if (((int) ch > 64 && (int) ch < 91) || ((int) ch > 96 && (int) ch < 123) || (int) ch == 95 || (int) ch == 36 || ((int) ch > 47 && (int) ch < 58)) {
                            sb.append(ch);
                            pos++;
                        } else {
                            //state = State.START;
                            if (KeywordMap.containsKey(sb.toString())) {
                                state = State.KEYWORD;
                                break;
                            } else if (BooleanMap.containsKey(sb.toString())) {
                                state = State.BOOLEAN_LITERAL;
                            } else {
                                tokens.add(new Token(Kind.IDENTIFIER, initialPos, (pos - initialPos), line, posInLine));
                                posInLine = posInLine + (pos - initialPos);
                                initialPos = pos;
                                sb = new StringBuilder();
                                state = State.START;
                            }
                        }
                    }
                    break;
                }
                case KEYWORD: {
                    tokens.add(new Token(KeywordMap.get(sb.toString()), initialPos, (pos - initialPos), line, posInLine));
                    posInLine = posInLine + (pos - initialPos);
                    initialPos = pos;
                    sb = new StringBuilder();
                    state = State.START;
                    break;
                }

                case BOOLEAN_LITERAL: {
                    tokens.add(new Token(BooleanMap.get(sb.toString()), initialPos, (pos - initialPos), line, posInLine));
                    posInLine = posInLine + (pos - initialPos);
                    initialPos = pos;
                    sb = new StringBuilder();
                    state = State.START;
                    break;
                }

                case INTEGER_START: {
                    state = State.DIGIT;
                    while (state == State.DIGIT) {
                        ch = chars[pos];
                        if ((int) ch > 47 && (int) ch < 58) {
                            sb.append(ch);
                            pos++;
                        } else {
                            state = State.INTEGER_LITERAL;
                        }
                    }
                    break;
                }

                case INTEGER_LITERAL: {
                    try {
                        Integer.parseInt(sb.toString());
                        tokens.add(new Token(Kind.INTEGER_LITERAL, initialPos, (pos - initialPos), line, (posInLine)));
                        posInLine = posInLine + (pos - initialPos);
                        initialPos = pos;
                        sb = new StringBuilder();
                        state = State.START;
                        break;
                    } catch (Exception e) {
                        throw new LexicalException("Integer larger than Max Integer Limit", initialPos);
                    }

                }

                case WHITESPACE_START: {
                    state = State.WHITESPACE;
                    while (state == State.WHITESPACE) {
                        ch = chars[pos];
                        if ((int) ch == 32 || (int) ch == 9 || (int) ch == 12) {
                            pos++;
                            posInLine++;
                            initialPos = pos;
                        } else if ((int) ch == 13) {
                            if ((int) chars[pos + 1] == 10) {
                                pos = pos + 2;
                                posInLine = 1;
                                line++;
                                initialPos = pos;
                            } else {
                                pos++;
                                line++;
                                posInLine = 1;
                                initialPos = pos;
                            }
                        } else if ((int) ch == 10) {
                            pos++;
                            line++;
                            posInLine = 1;
                            initialPos = pos;
                        } else {
                            state = State.START;
                        }
                    }
                    break;
                }

                case OPERATOR: {
                    ch = chars[pos];
                    if (ch == '=') {
                        if (chars[pos + 1] == '=') {
                            //add == in tokens
                            pos = pos + 2;
                            tokens.add(new Token(OperatorMap.get("=="), initialPos, (pos - initialPos), line, (posInLine)));
                            posInLine = posInLine + (pos - initialPos);
                            initialPos = pos;
                            sb = new StringBuilder();
                            state = State.START;
                            break;
                        } else {
                            state = State.OPERATOR_SINGLE;
                            break;
                        }
                    } else if (ch == '!' || ch == '>') {
                        if (chars[pos + 1] == '=') {
                            StringBuilder constructOper = new StringBuilder();
                            constructOper.append(ch).append(chars[pos + 1]);
                            //add == in tokens
                            pos = pos + 2;
                            tokens.add(new Token(OperatorMap.get(constructOper.toString()), initialPos, (pos - initialPos), line, (posInLine)));
                            posInLine = posInLine + (pos - initialPos);
                            initialPos = pos;
                            sb = new StringBuilder();
                            state = State.START;
                            break;
                        } else {
                            state = State.OPERATOR_SINGLE;
                            break;
                        }
                    } else if (ch == '*') {
                        if (chars[pos + 1] == '*') {
                            //add == in tokens
                            pos = pos + 2;
                            tokens.add(new Token(OperatorMap.get("**"), initialPos, (pos - initialPos), line, (posInLine)));
                            posInLine = posInLine + (pos - initialPos);
                            initialPos = pos;
                            sb = new StringBuilder();
                            state = State.START;
                            break;
                        } else {
                            state = State.OPERATOR_SINGLE;
                            break;
                        }
                    } else if (ch == '-') {
                        if (chars[pos + 1] == '>') {
                            //add == in tokens
                            pos = pos + 2;
                            tokens.add(new Token(OperatorMap.get("->"), initialPos, (pos - initialPos), line, (posInLine)));
                            posInLine = posInLine + (pos - initialPos);
                            initialPos = pos;
                            sb = new StringBuilder();
                            state = State.START;
                            break;
                        } else {
                            state = State.OPERATOR_SINGLE;
                            break;
                        }
                    } else if (ch == '<') {
                        if (chars[pos + 1] == '-') {
                            //add <- in tokens
                            pos = pos + 2;
                            tokens.add(new Token(OperatorMap.get("<-"), initialPos, (pos - initialPos), line, (posInLine)));
                            posInLine = posInLine + (pos - initialPos);
                            initialPos = pos;
                            sb = new StringBuilder();
                            state = State.START;
                            break;
                        } else if (chars[pos + 1] == '=') {
                            pos = pos + 2;
                            tokens.add(new Token(OperatorMap.get("<="), initialPos, (pos - initialPos), line, (posInLine)));
                            posInLine = posInLine + (pos - initialPos);
                            initialPos = pos;
                            sb = new StringBuilder();
                            state = State.START;
                            break;
                        } else {
                            state = State.OPERATOR_SINGLE;
                            break;
                        }
                    } else {
                        state = State.OPERATOR_SINGLE;
                        break;
                    }
                }

                case OPERATOR_SINGLE: {
                    ch = chars[pos];
                    pos++;
                    tokens.add(new Token(OperatorMap.get(String.valueOf(ch)), initialPos, (pos - initialPos), line, (posInLine)));
                    posInLine = posInLine + (pos - initialPos);
                    initialPos = pos;
                    sb = new StringBuilder();
                    state = State.START;
                    break;
                }

                case SEPARATOR: {
                    ch = chars[pos];
                    pos++;
                    tokens.add(new Token(SeparatorMap.get(String.valueOf(ch)), initialPos, (pos - initialPos), line, (posInLine)));
                    posInLine = posInLine + (pos - initialPos);
                    initialPos = pos;
                    sb = new StringBuilder();
                    state = State.START;
                    break;
                }

                case COMMENT: {
                    ch = chars[pos];
                    if (chars[pos + 1] == '/') {
                        while ((int) ch != 13 && (int) ch != 10 && ch != EOFchar) {
                            pos++;
                            posInLine++;
                            initialPos = pos;
                            ch = chars[pos];
                        }
                        state = State.START;
                    } else {
                        state = State.OPERATOR;
                    }
                    break;
                }

                case EOF: {
                    tokens.add(new Token(Kind.EOF, initialPos, 0, line, posInLine));
                    pos++;
                    break;
                }
            }
        }

        return this;

        /*tokens.add(new Token(Kind.EOF, pos, 0, line, posInLine));
        return this;*/


       /* while (pos < len - 1) {
            ch = chars[pos];
            sb.append(ch);
            switch (ch) {
                case 'D': {
                    pos++;
                    posInLine++;
                    while (pos < len - 1) {
                        ch = chars[pos];
                        sb.append(ch);
                        pos++;
                        posInLine++;
                    }

                    posInLine--;
                    Kind tokenType = KeywordMap.get(sb.toString());
                    tokens.add(new Token(tokenType, initialPos, ((pos) - initialPos), line, posInLine));
                    initialPos = pos;
                    posInLine++;
                    break;


                }
                case EOFchar: {
                    flag = 1;
                    tokens.add(new Token(Kind.EOF, initialPos, 1, line, posInLine));
                    pos++;
                    break;
                }
            }
        }
        if (flag != 1) {
            ch = chars[pos];
            if (ch == EOFchar) {
                tokens.add(new Token(Kind.EOF, pos, (pos - initialPos), line, posInLine));
            }
        }
*/

    }

    /**
     * Returns true if the internal iterator has more Tokens
     *
     * @return
     */
    public boolean hasTokens() {
        return nextTokenPos < tokens.size();
    }

    /**
     * Returns the next Token and updates the internal iterator so that the next
     * call to nextToken will return the next token in the list.
     * <p>
     * It is the callers responsibility to ensure that there is another Token.
     * <p>
     * Precondition: hasTokens()
     *
     * @return
     */
    public Token nextToken() {
        return tokens.get(nextTokenPos++);
    }

    /**
     * Returns the next Token, but does not update the internal iterator. This
     * means that the next call to nextToken or peek will return the same Token
     * as returned by this methods.
     * <p>
     * It is the callers responsibility to ensure that there is another Token.
     * <p>
     * Precondition: hasTokens()
     *
     * @return next Token.
     */
    public Token peek() {
        return tokens.get(nextTokenPos);
    }

    /**
     * Resets the internal iterator so that the next call to peek or nextToken
     * will return the first Token.
     */
    public void reset() {
        nextTokenPos = 0;
    }

    /**
     * Returns a String representation of the list of Tokens
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Tokens:\n");
        for (int i = 0; i < tokens.size(); i++) {
            sb.append(tokens.get(i)).append('\n');
        }
        return sb.toString();
    }

    @SuppressWarnings("serial")
    public static class LexicalException extends Exception {

        int pos;

        public LexicalException(String message, int pos) {
            super(message);
            this.pos = pos;
        }

        public int getPos() {
            return pos;
        }

    }


    /**
     * Class to represent Tokens.
     * <p>
     * This is defined as a (non-static) inner class which means that each Token
     * instance is associated with a specific Scanner instance. We use this when
     * some token methods access the chars array in the associated Scanner.
     *
     * @author Beverly Sanders
     */
    public class Token {
        public final Kind kind;
        public final int pos;
        public final int length;
        public final int line;
        public final int pos_in_line;

        public Token(Kind kind, int pos, int length, int line, int pos_in_line) {
            super();
            this.kind = kind;
            this.pos = pos;
            this.length = length;
            this.line = line;
            this.pos_in_line = pos_in_line;
        }

        public String getText() {
            if (kind == Kind.STRING_LITERAL) {
                return chars2String(chars, pos, length);
            } else
                return String.copyValueOf(chars, pos, length);
        }

        /**
         * To get the text of a StringLiteral, we need to remove the enclosing "
         * characters and convert escaped characters to the represented
         * character. For example the two characters \ t in the char array
         * should be converted to a single tab character in the returned String
         *
         * @param chars
         * @param pos
         * @param length
         * @return
         */
        private String chars2String(char[] chars, int pos, int length) {
            StringBuilder sb = new StringBuilder();
            for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial
                // and final "
                char ch = chars[i];
                if (ch == '\\') { // handle escape
                    i++;
                    ch = chars[i];
                    switch (ch) {
                        case 'b':
                            sb.append('\b');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'r':
                            sb.append('\r'); // for completeness, line termination
                            // chars not allowed in String
                            // literals
                            break;
                        case 'n':
                            sb.append('\n'); // for completeness, line termination
                            // chars not allowed in String
                            // literals
                            break;
                        case '\"':
                            sb.append('\"');
                            break;
                        case '\'':
                            sb.append('\'');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        default:
                            assert false;
                            break;
                    }
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }

        /**
         * precondition: This Token is an INTEGER_LITERAL
         *
         * @returns the integer value represented by the token
         */
        public int intVal() {
            assert kind == Kind.INTEGER_LITERAL;
            return Integer.valueOf(String.copyValueOf(chars, pos, length));
        }

        public String toString() {
            return "[" + kind + "," + String.copyValueOf(chars, pos, length) + "," + pos + "," + length + "," + line
                    + "," + pos_in_line + "]";
        }

        /**
         * Since we overrode equals, we need to override hashCode.
         * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
         * <p>
         * Both the equals and hashCode method were generated by eclipse
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((kind == null) ? 0 : kind.hashCode());
            result = prime * result + length;
            result = prime * result + line;
            result = prime * result + pos;
            result = prime * result + pos_in_line;
            return result;
        }

        /**
         * Override equals method to return true if other object is the same
         * class and all fields are equal.
         * <p>
         * Overriding this creates an obligation to override hashCode.
         * <p>
         * Both hashCode and equals were generated by eclipse.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Token other = (Token) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (kind != other.kind)
                return false;
            if (length != other.length)
                return false;
            if (line != other.line)
                return false;
            if (pos != other.pos)
                return false;
            if (pos_in_line != other.pos_in_line)
                return false;
            return true;
        }

        /**
         * used in equals to get the Scanner object this Token is associated
         * with.
         *
         * @return
         */
        private Scanner getOuterType() {
            return Scanner.this;
        }

    }

}
