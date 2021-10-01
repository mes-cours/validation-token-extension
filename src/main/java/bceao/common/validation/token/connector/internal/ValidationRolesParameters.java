package bceao.common.validation.token.connector.internal;

import java.util.ArrayList;
import java.util.List;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class ValidationRolesParameters {
	
	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@Optional(defaultValue = "Role Utilisateur")
	private String operation;
	
	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@Optional(defaultValue = "Listes Roles")
	private List<String> listes = new ArrayList<>();

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public List<String> getListes() {
		return listes;
	}

	public void setListes(List<String> listes) {
		this.listes = listes;
	}

}
