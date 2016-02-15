package config;

import adaptador.AdaptadorException;
import adaptador.AdaptadorSymphony;
import adaptador.CuentaDatabase;
import adaptador.DatabaseMysql;
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

	public AdaptadorSymphony leerAdaptador() throws ConfiguracionException {
		AdaptadorSymphony adaptador;
		try {
			adaptador = new AdaptadorSymphony();
			adaptador.setSeccion(config.getString("adaptador.seccion"));
		} catch (AdaptadorException e) {
			throw new ConfiguracionException(e.getMessage(), e);
		}
		return adaptador;
	}

	public ArrayList<CuentaCorreo> leerCorreos() throws ConfiguracionException {
		ArrayList<CuentaCorreo> correos = new ArrayList<>();
		List<HierarchicalConfiguration<ImmutableNode>> lista = config.childConfigurationsAt("correos");
		for (HierarchicalConfiguration<ImmutableNode> prop : lista) {
			String direccion = prop.getString("direccion");
			String password = prop.getString("password");
			String servidor = prop.getString("servidor");
			correos.add(new CuentaCorreo(direccion, password, servidor));
		}
		return correos;
	}

	public DatabaseMysql leerDatabase() {
		DatabaseMysql database = new DatabaseMysql();
		CuentaDatabase cuenta = new CuentaDatabase();
		cuenta.setDatabase(config.getString("database.dbname"));
		cuenta.setUsuario(config.getString("database.usuariobd"));
		cuenta.setPassword(config.getString("database.passwordbd"));
		cuenta.setServer(config.getString("database.server"));
		cuenta.setPort(config.getString("database.port"));
		database.setCuenta(cuenta);
		return database;
	}
}
