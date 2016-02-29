package org.jim.xj.exception;

public class SkinNotFoundException extends XJException{

	public SkinNotFoundException() {
		super("皮肤不存在!");
	}

	public SkinNotFoundException(String msg) {
		super(msg);
	}

}
