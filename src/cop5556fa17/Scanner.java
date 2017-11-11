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
import java.util.List;
import java.util.Map;

public class Scanner {
	
	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {
		
		int pos;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}
		
		public int getPos() { return pos; }

	}

	public static Map<String, Kind> keywordMap=new HashMap<String, Kind>();
	

	/** Class to represent Tokens. 
	 * 
	 * This is defined as a (non-static) inner class
	 * which means that each Token instance is associated with a specific 
	 * Scanner instance.  We use this when some token methods access the
	 * chars array in the associated Scanner.
	 * 
	 * 
	 * @author Beverly Sanders
	 *
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
		
			}
			else return String.copyValueOf(chars, pos, length);
		}

		/**
		 * To get the text of a StringLiteral, we need to remove the
		 * enclosing " characters and convert escaped characters to
		 * the represented character.  For example the two characters \ t
		 * in the char array should be converted to a single tab character in
		 * the returned String
		 * 
		 * @param chars
		 * @param pos
		 * @param length
		 * @return
		 */
		private String chars2String(char[] chars, int pos, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial and final "
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
						sb.append('\r'); //for completeness, line termination chars not allowed in String literals
						break;
					case 'n':
						sb.append('\n'); //for completeness, line termination chars not allowed in String literals
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
		 * precondition:  This Token is an INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		public String toString() {
			return "[" + kind + "," + String.copyValueOf(chars, pos, length)  + "," + pos + "," + length + "," + line + ","
					+ pos_in_line + "]";
		}

		/** 
		 * Since we overrode equals, we need to override hashCode.
		 * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
		 * 
		 * Both the equals and hashCode method were generated by eclipse
		 * 
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
		 * Override equals method to return true if other object
		 * is the same class and all fields are equal.
		 * 
		 * Overriding this creates an obligation to override hashCode.
		 * 
		 * Both hashCode and equals were generated by eclipse.
		 * 
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
		 * used in equals to get the Scanner object this Token is 
		 * associated with.
		 * @return
		 */
		private Scanner getOuterType() {
			return Scanner.this;
		}

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
	 * An array of characters representing the input.  These are the characters
	 * from the input string plus and additional EOFchar at the end.
	 */
	final char[] chars;  



	
	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input string terminated with null char
		chars[numChars] = EOFchar;
		tokens = new ArrayList<Token>();
	}
	public static enum State {
		START,
		IN_IDENT,
		IN_DIGIT,
		AFTER_EQ,
		AFTER_NOT,
		AFTER_LT,
		AFTER_GT,
		AFTER_TIMES,
		AFTER_MINUS,
		AFTER_DIV,
		IN_COMMENT,
		IN_STRINGLIT,
		IntermediateState;
	}

	public static enum Kind {
		IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, 
		KW_x/* x */, KW_X/* X */, KW_y/* y */, KW_Y/* Y */, KW_r/* r */, KW_R/* R */, KW_a/* a */, 
		KW_A/* A */, KW_Z/* Z */, KW_DEF_X/* DEF_X */, KW_DEF_Y/* DEF_Y */, KW_SCREEN/* SCREEN */, 
		KW_cart_x/* cart_x */, KW_cart_y/* cart_y */, KW_polar_a/* polar_a */, KW_polar_r/* polar_r */, 
		KW_abs/* abs */, KW_sin/* sin */, KW_cos/* cos */, KW_atan/* atan */, KW_log/* log */, 
		KW_image/* image */,  KW_int/* int */, 
		KW_boolean/* boolean */, KW_url/* url */, KW_file/* file */, OP_ASSIGN/* = */, OP_GT/* > */, OP_LT/* < */, 
		OP_EXCL/* ! */, OP_Q/* ? */, OP_COLON/* : */, OP_EQ/* == */, OP_NEQ/* != */, OP_GE/* >= */, OP_LE/* <= */, 
		OP_AND/* & */, OP_OR/* | */, OP_PLUS/* + */, OP_MINUS/* - */, OP_TIMES/* * */, OP_DIV/* / */, OP_MOD/* % */, 
		OP_POWER/* ** */, OP_AT/* @ */, OP_RARROW/* -> */, OP_LARROW/* <- */, LPAREN/* ( */, RPAREN/* ) */, 
		LSQUARE/* [ */, RSQUARE/* ] */, SEMI/* ; */, COMMA/* , */, EOF;
	}
	/**
	 * Method to scan the input and create a list of Tokens.
	 * 
	 * If an error is encountered during scanning, throw a LexicalException.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	public Scanner scan() throws LexicalException {
		intializeKeywords();
		char buffer[]=new char[chars.length];
		int bufferCounter=0;
		StringBuilder sb=new StringBuilder();
		/* TODO  Replace this with a correct and complete implementation!!! */
		int pos = 0;
		int line = 1;
		int posInLine = 1;
		String booleanTrue="true";
		String booleanFalse="false";
	    State state = State.START;
	    int startPos = 0,flag=0;
	    while (pos <=chars.length) {
	    	
	        char ch = chars[pos];
	        //System.out.println(ch);
	        switch (state) {
	            case START: {
	            	ch=chars[pos];	            	
	                startPos = pos;
	                switch (ch) {
	                    case '+': {tokens.add(new Token(Kind.OP_PLUS, startPos, 1,line,posInLine++));pos++;} break;
	                    case '@': {tokens.add(new Token(Kind.OP_AT, startPos, 1,line,posInLine++));pos++;} break;
	                    case '-': {state = State.AFTER_MINUS;pos++;}break;
	                    case ';': {tokens.add(new Token(Kind.SEMI, startPos, 1,line,posInLine++));pos++;} break;
	                    case ',': {tokens.add(new Token(Kind.COMMA, startPos, 1,line,posInLine++));pos++;} break;
	                    case '%': {tokens.add(new Token(Kind.OP_MOD, startPos, 1,line,posInLine++));pos++;} break;
	                    case '/':{state = State.AFTER_DIV;pos++;}break;
	                    case '"':{state = State.IN_STRINGLIT;pos++;posInLine++;flag=1;}break;
	                    case '=': {state = State.AFTER_EQ;pos++;}break;
	                    case '0': {tokens.add(new Token(Kind.INTEGER_LITERAL,startPos,1,line,posInLine++ ));pos++;}break;
	                    case '>': {state = State.AFTER_GT;pos++;}break;
	                    case '&': {tokens.add(new Token(Kind.OP_AND, startPos,1,line,posInLine++));pos++;} break;
	                    case '|': {tokens.add(new Token(Kind.OP_OR, startPos,1,line,posInLine++));pos++;} break;
	                    case '<': {state = State.AFTER_LT;pos++;}break;
	                    case '!': {state = State.AFTER_NOT;pos++;}break;
	                    case '*': {state = State.AFTER_TIMES;pos++;}break;
	                    case '?': {tokens.add(new Token(Kind.OP_Q, startPos, 1,line,posInLine++));pos++;} break;
	                    case ':': {tokens.add(new Token(Kind.OP_COLON, startPos, 1,line,posInLine++));pos++;} break;
	                    case EOFchar : {tokens.add(new Token(Kind.EOF, pos, 0, line, posInLine));pos++;return this; } // next iteration should terminate loop
	                    case '(': {tokens.add(new Token(Kind.LPAREN, startPos, 1,line,posInLine++));pos++;} break;
	                    case ')': {tokens.add(new Token(Kind.RPAREN, startPos, 1,line,posInLine++));pos++;} break;
	                    case '[': {tokens.add(new Token(Kind.LSQUARE, startPos, 1,line,posInLine++));pos++;} break;
	                    case ']': {tokens.add(new Token(Kind.RSQUARE, startPos, 1,line,posInLine++));pos++;} break;	
	     
	                    default: {
	                        if (Character.isDigit(ch)) {state = State.IN_DIGIT;pos++;posInLine++;sb.append(ch);} 
	                        else if ( ch>=65 && ch<=90 
	                        		||ch>=97 && ch<=122||ch>=60 && ch<=57|| ch==95 ||ch==36)   {
	                        	 sb.append(ch);
	                             state = State.IN_IDENT;
	                             pos++;
	                             posInLine++;      
	                         } 
	                         else if (Character.isWhitespace(ch))
	                         {
	                        	        if(ch=='\r' && chars[pos+1]=='\n')
	                        	 		{
	                        	        	pos++;
	                        	 		}
	                        	        //on getting space,backspace or tab increment position and posInLine only
	                        	        else if(ch==' '|| ch=='\t'||ch=='\b'||ch=='\f')
	                        	        {
	                        	        	//System.out.println("I am here");
	                        	        	pos++;
	                        	        	posInLine++;
	                        	        }
	                        	        else
	                        	        {
	                        	        	pos++;
	                        	        	posInLine=1;
	                        	        	line++;
	                        	        }
	                         }
	                         else
	                         {
	                        	 pos++;
	                        	 throw new Scanner.LexicalException("Invalid character at point",pos-1);
	                         }
	                       }
	                      }
	            }  
	            break;
	            case IN_IDENT: {
	                if (Character.isJavaIdentifierPart(ch) && ch!=EOFchar && ch!='\b') {
	                	sb.append(ch);
	                      pos++;
	                      posInLine++;      
	                      
	                } 
	                		else {
	                			
	                	Kind keyword=keywordMap.get(sb.toString());
	                		if(keywordMap.containsKey(sb.toString()))
	                		{
	                			//System.out.println("aded token keyword");
	                			tokens.add(new Token(keyword, startPos, pos - startPos,line,posInLine-(pos-startPos)));
	                			
	                		}
	                		else if(sb.toString().equals(booleanTrue) || sb.toString().equals(booleanFalse))
	                		{
	                			tokens.add(new Token(Kind.BOOLEAN_LITERAL, startPos, pos - startPos,line,posInLine-(pos-startPos)));
	                		}
	                		else
	                		{	
	                			//System.out.println(sb.toString());
	                			tokens.add(new Token(Kind.IDENTIFIER, startPos, pos - startPos,line,posInLine-(pos-startPos)));
	                		}
	                		sb=new StringBuilder();
	                		state = State.START;
	                }
	          }break;
	            case IN_DIGIT: {
	                if (Character.isDigit(ch) && ch!=EOFchar) {
	                	  sb.append(ch);
	                	  String num=sb.toString(); 
	                	  try
	                	  {
	                		  //System.out.println(num);
	                		  int number=Integer.parseInt(num);
	                	  }
	                	  catch(NumberFormatException e)
	                	  {
	                		  throw new LexicalException("The number is invalid",(pos+1)-sb.length());
	                	  }
	                	  pos++;
	                      posInLine++;
	                } else {
	                			
	                			tokens.add(new Token(Kind.INTEGER_LITERAL, startPos, pos - startPos,line,posInLine-(pos-startPos)));		
	                			state=state.START;
	                			sb=new StringBuilder();
	                }
	          }break;
	            case IN_STRINGLIT: {
	            	//System.out.println("in string literal state");
	            	//System.out.println(ch);
	            	//System.out.println(pos);
	                if(ch=='\\')
	                { 	//System.out.println("In backward slash"+ch+" "+pos);
	                	if(chars[pos+1]=='b'||chars[pos+1]=='t'||chars[pos+1]=='n'||chars[pos+1]=='f'||chars[pos+1]=='r'||chars[pos+1]=='\''||chars[pos+1]=='\"'||chars[pos+1]=='\\')
	                	{
	                		pos+=2;	
	                		posInLine+=2;
	                		state=state.IN_STRINGLIT;
	                	}
	                	else
	                	{
	                		throw new LexicalException("Invalid character",pos);
	                	}
	                	
	                }
	                else if(Character.isJavaIdentifierPart(ch) && ch!=EOFchar)
	                {
	                	//System.out.println("In alphabet "+pos+" "+ ch);
	                	pos++;
	                	posInLine++;
	                }
	                else if(ch=='\"')
	                {
	                	//System.out.println("In double quotes "+pos);
	                	pos++;posInLine++;
	                	tokens.add(new Token(Kind.STRING_LITERAL, startPos, pos - startPos,line,posInLine-(pos-startPos)));
	                	state=state.START;	
	                }
	                else if(ch==EOFchar && flag==1)
	                {
	                	//System.out.println("In else if of EOF char");
	                	//state=state.START;
	                	throw new Scanner.LexicalException("No closing double quotes",pos); 	 
	                }
	                else if(Character.isWhitespace(ch) && ch!='\r' && ch!='\n')
	                {
	                	//System.out.println("In else if of EOF char");
	                	//state=state.START;
	                	pos++;
	                	posInLine++;
	                }
	                else if(ch=='\n'|| ch=='\r')
	                {
	                	throw new Scanner.LexicalException("Character not allowed in string literal",pos); 
	                }
	                else
	                {
	                	pos++;
	                    posInLine++;
	                }
	                }
	          break;
	          
	            case AFTER_EQ: {
	            	if(ch=='=') 
	            	{
	            		state = State.START;
	            		
	            		tokens.add(new Token(Kind.OP_EQ, startPos, 2,line,posInLine));
	            		posInLine+=2;
	            		pos++;
	            	}
                     //state = State.IN_IDENT;pos++;
                     else 
                     {
                    	state = State.START;
 	            		tokens.add(new Token(Kind.OP_ASSIGN, startPos, 1,line,posInLine++));
 	            		//pos++;
                     }
	            }  break;
	            case AFTER_DIV: {
	            	if(ch=='/') 
	            	{
	            		//System.out.println("Her in after div "+pos);
	            		pos++;
	            		state=State.IN_COMMENT;
	            	}
                     else 
                     {
                    	state = State.START;
 	            		tokens.add(new Token(Kind.OP_DIV, startPos, 1,line,posInLine++));
                     }
	            }  break;
	            case IN_COMMENT: {
	            	//System.out.println("Position is "+pos+" line is "+line);
	            	if(ch==EOFchar) 
	            	{
	            		//System.out.println(pos);
	            		state=State.START;
	            	}
	            	else if(ch=='\n' || ch=='\r')
	            	{
	            		if(ch=='\r' && chars[pos+1]=='\n')
	            		{
	            			pos+=2;
	            			line++;
	            			posInLine=1;
	            			state=state.START;
	            		}
	            		//System.out.println("here "+ pos+ " "+line);
	            		else
	            		{
	            			pos++;
	            			line++;
	            			posInLine=1;
	            			state=State.START;
	            		}
	            		
	            	}
                     else 
                     {
                    	//System.out.println("In comment for else "+pos);
                    	pos++;
                     }
	            }  break;
	            case AFTER_MINUS: {
	            	if(ch=='>') 
	            	{
	            		state = State.START;
	            		
	            		tokens.add(new Token(Kind.OP_RARROW, startPos, 2,line,posInLine));
	            		posInLine+=2;
	            		pos++;
	            	}
                     //state = State.IN_IDENT;pos++;
                     else 
                     {
                    	state = State.START;
 	            		tokens.add(new Token(Kind.OP_MINUS, startPos, 1,line,posInLine++));
 	            		//pos++;
                     }
	            }  break;
	            case AFTER_TIMES: {
	            	if(ch=='*') 
	            	{
	            		state = State.START;
	            		
	            		tokens.add(new Token(Kind.OP_POWER, startPos, 2,line,posInLine));
	            		posInLine+=2;
	            		pos++;
	            	}
                     //state = State.IN_IDENT;pos++;
                     else 
                     {
                    	state = State.START;
 	            		tokens.add(new Token(Kind.OP_TIMES, startPos, 1,line,posInLine++));
 	            		//pos++;
                     }
	            }  break;
	            case AFTER_NOT:{
	            	if(ch=='=') 
	            	{
	            	
	            		tokens.add(new Token(Kind.OP_NEQ, startPos, 2,line,posInLine));
	            		posInLine+=2;
	            		state = State.START;
	            		pos++;
	            	}                  
                     else 
                     {
                    	 tokens.add(new Token(Kind.OP_EXCL, startPos, 1,line,posInLine++));
                    	 state = State.START;
                    	 //pos++;
                     }
	            }
	            break;
	            case AFTER_LT: {
	            	if(ch=='=') 
	            	{
	            		
	            		state = State.START;
	            		tokens.add(new Token(Kind.OP_LE, startPos, 2,line,posInLine));
	            		posInLine+=2;
	            		pos++;
	            	}
	            	else if(ch=='-') 
	            	{
	            		
	            		state = State.START;
	            		tokens.add(new Token(Kind.OP_LARROW, startPos, 2,line,posInLine));
	            		posInLine+=2;
	            		pos++;
	            	}
	            	
                     else {
                    	 tokens.add(new Token(Kind.OP_LT, startPos, 1,line,posInLine++));
                    	 state = State.START;
                     }                    	 
	            }  break;
	            case AFTER_GT: {
	            	if(ch=='=') 
	            	{
	            		
	            		state = State.START;
	            		tokens.add(new Token(Kind.OP_GE, startPos, 2,line,posInLine));
	            		posInLine+=2;
	            		pos++;
	            	}
                     else {
                    	 tokens.add(new Token(Kind.OP_GT, startPos, 1,line,posInLine++));
                    	 state = State.START;
                     }                    	 
	            }  break;
	        }// switch(state)
	        }// while
	        //tokens.add(new Token(Kind.EOF, pos, 0));
	    tokens.add(new Token(Kind.EOF, pos, 0, line, posInLine));
	    return this;	
  }
	private void intializeKeywords() {
		// TODO Auto-generated method stub
		keywordMap.put("x",Kind.KW_x);
		keywordMap.put("X",Kind.KW_X);
		keywordMap.put("y",Kind.KW_y);
		keywordMap.put("Y",Kind.KW_Y);
		keywordMap.put("r",Kind.KW_r);
		keywordMap.put("R",Kind.KW_R);
		keywordMap.put("a",Kind.KW_a);
		keywordMap.put("A",Kind.KW_A);
		keywordMap.put("Z",Kind.KW_Z);
		keywordMap.put("DEF_X",Kind.KW_DEF_X);
		keywordMap.put("DEF_Y",Kind.KW_DEF_Y);
		keywordMap.put("SCREEN",Kind.KW_SCREEN);
		keywordMap.put("cart_x",Kind.KW_cart_x);
		keywordMap.put("cart_y",Kind.KW_cart_y);
		keywordMap.put("polar_a",Kind.KW_polar_a);
		keywordMap.put("polar_r",Kind.KW_polar_r);
		keywordMap.put("abs",Kind.KW_abs);
		keywordMap.put("sin",Kind.KW_sin);
		keywordMap.put("cos",Kind.KW_cos);
		keywordMap.put("atan",Kind.KW_atan);
		keywordMap.put("log",Kind.KW_log);
		keywordMap.put("image",Kind.KW_image);
		keywordMap.put("int",Kind.KW_int);
		keywordMap.put("boolean",Kind.KW_boolean);
		keywordMap.put("url",Kind.KW_url);
		keywordMap.put("file",Kind.KW_file);
	}

	/**
	 * Returns true if the internal interator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that
	 * the next call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}
	
	/**
	 * Returns the next Token, but does not update the internal iterator.
	 * This means that the next call to nextToken or peek will return the
	 * same Token as returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
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

}