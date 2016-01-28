package config;

import lector.CuentaCorreo;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.XMLBuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.ImmutableNode;

import java.util.ArrayList;
import java.util.List;

public class ConfiguracionProcesadorCorreos {

	XMLConfiguration config;

	public ConfiguracionProcesadorCorreos() throws ConfiguracionException {
		try {
			Parameters params = new Parameters();
			FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<>(XMLConfiguration.class);
			XMLBuilderParameters xbp = params.xml();
			xbp.setFileName("configuracion.xml");
			//xbp.setValidating(true);
			builder.configure(xbp);
			config = builder.getConfiguration();
		} catch (ConfigurationException cex) {
			throw new ConfiguracionException(cex.getMessage(), cex);
		}
	}

	public ArrayList<CuentaCorreo> leerCorreos() throws ConfiguracionException {
		ArrayList<CuentaCorreo> correos = new ArrayList<>();
		//List<Object> fields = config.getList("correos.correo.direccion");
		List<HierarchicalConfiguration<ImmutableNode>> lista = config.childConfigurationsAt("correos");
		for (HierarchicalConfiguration<ImmutableNode> prop : lista) {
			String direccion = prop.getString("direccion");
			String password = prop.getString("password");
			String servidor = prop.getString("servidor");
			correos.add(new CuentaCorreo(direccion, password, servidor));
		}
		return correos;
	}

}
