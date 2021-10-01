package bceao.common.validation.token.connector.internal;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

@Xml(prefix = "validation-token")
@Extension(name = "Validation Token And Roles")
@Configurations(ValidationTokenConfiguration.class)
public class ValidationTokenExtension {

}
