package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public abstract class Statement extends ASTNode {
	public Type Type;

	public Type getType() {
		return Type;
	}

	public void setType(Type type) {
		Type = type;
	}

	public Statement(Token firstToken) {
		super(firstToken);
	}

}
