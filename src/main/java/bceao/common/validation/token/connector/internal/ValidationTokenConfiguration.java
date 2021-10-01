package bceao.common.validation.token.connector.internal;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@Operations(ValidationTokenOperations.class)
public class ValidationTokenConfiguration {

	@Parameter
	private String clientId;

	@Parameter
	private String clientSecret;

	@Parameter
	private String urlValidation;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getUrlValidation() {
		return urlValidation;
	}

	public void setUrlValidation(String urlValidation) {
		this.urlValidation = urlValidation;
	}

}
