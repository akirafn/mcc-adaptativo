package br.ufscar.mcc.model;

public class MethodProfile {

	private int methodId;
	private String methodName;
	private String className;
	private int methodCount;
	
	public MethodProfile() {
		methodId = 0;
		methodCount = 0;
	}


	public int getMethodId() {
		return methodId;
	}


	public void setMethodId(int methodId) {
		this.methodId = methodId;
	}


	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}


	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public int getMethodCount() {
		return methodCount;
	}


	public void setMethodCount(int methodCount) {
		this.methodCount = methodCount;
	}

	@Override
	public int hashCode() {
		final int prime = 59;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodProfile other = (MethodProfile) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		return true;
	}
	
	
}
