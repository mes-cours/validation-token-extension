package bceao.common.validation.token.connector.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.List;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ValidationTokenOperations {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidationTokenOperations.class);

	private static CloseableHttpClient _client = null;

	@MediaType(value = ANY, strict = false)
	@Alias("ValidateToken")
	@Summary(value = "Valide le Token reçu depuis Keycloak")
	public List<String> validToken(@Config ValidationTokenConfiguration configuration, @ParameterGroup(name = "Authorization") ValidationAuthorizationParameters parameters) {

		List<String> resultValidation = new ArrayList<String>();
		resultValidation = validationToken(parameters.getAuthorization(), configuration.getClientId(),
				configuration.getClientSecret(), configuration.getUrlValidation());
		return resultValidation;
	}
	
	@MediaType(value = ANY, strict = false)
	@Alias("ValidateRoles")
	@Summary(value = "Valide le role de l'utilisateur")
	public Boolean validRoles(@ParameterGroup(name = "Roles") ValidationRolesParameters parameters) {

		System.out.println("ROLES : "+parameters.getListes());
		boolean resultAccess = false;
		resultAccess = haveAccess(parameters.getListes(), parameters.getOperation());
		return resultAccess;
	}

	private List<String> validationToken(String authorization, String clientid, String clientsecret, String url) {

		List<String> roles = new ArrayList<String>();

		String result = "";
		HttpPost post = new HttpPost(url);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("client_id", clientid));
		urlParameters.add(new BasicNameValuePair("client_secret", clientsecret));

		String tokenExtraction = "";

		if (authorization != null) {
			tokenExtraction = authorization.replace("Bearer ", "");
		}
		urlParameters.add(new BasicNameValuePair("token", tokenExtraction));
		LOGGER.info("Requête de validation du token");
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			CloseableHttpResponse response = _client.execute(post);
			result = EntityUtils.toString(response.getEntity());

			JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();

			LOGGER.info("Requête de validation du token OK");
			if (jsonObject.has("active") && jsonObject.get("active").getAsBoolean()) {
				if (jsonObject.has("resource_access")) {
					JsonObject ressource = jsonObject.get("resource_access").getAsJsonObject();
					if (ressource.has(clientid)) {
						JsonObject client_roles = ressource.get(clientid).getAsJsonObject();
						JsonArray list = client_roles.get("roles").getAsJsonArray();
						LOGGER.info("Nombre de roles trouvés " + list.size());
						for (int i = 0; i < list.size(); i++) {
							roles.add(list.get(i).getAsString());
						}
					}
				} else {
					//LOGGER.log(Level.WARNING, "token incomplet");
					throw new RuntimeException("token incomplet");
				}
			} else {
				// LOGGER.log(Level.WARNING, "Invalide token");
				throw new RuntimeException("Invalide token");
			}

		} catch (ParseException e) {
			LOGGER.error("Problème lors de l'envoie de requête de validation du token", e);
		}

		catch (IOException e) {
			LOGGER.error("Problème lors de l'envoie de requête de validation du token", e);
		}

		return roles;
	}

	private Boolean haveAccess(List<String> roles, String operation) {
		boolean haveAccess = false;

		if (roles != null) {
			for (int i = 0; i < roles.size(); i++) {
				String role = roles.get(i);
				if (role.equals(operation)) {
					haveAccess = true;
					break;
				}
			}
		}

		return haveAccess;
	}

	static {

		SSLContext context = null;
		try {
			context = new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build();

			_client = HttpClients.custom().setSSLContext(context).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
					.build();

		} catch (KeyManagementException e) {
			LOGGER.error("Problème lors de la construction du contexte SSL ",e);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Problème lors de la construction du contexte SSL ",e);
		} catch (KeyStoreException e) {
			LOGGER.error("Problème lors de la construction du contexte SSL ",e);
		}

	}
}
