package cop5556fa17;

import java.util.HashMap;

import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.TypeUtils.Type;

public class SymbolTable {
	HashMap<String, Declaration> symbolTable = new HashMap<String, Declaration>();

	public Type lookupType(String name) {
		if (symbolTable.get(name) == null) {
			return null;
		} else {
			return symbolTable.get(name).Type;
		}
	}

	public Declaration lookupDec(String name) {
		return symbolTable.get(name);
	}

	public void insert(String name, Declaration declaration) {
		symbolTable.put(name, declaration);
	}
}