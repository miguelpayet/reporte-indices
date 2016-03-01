package config;

import adaptador.CuentaDatabase;
import excel.Excel;
import excel.ExcelConfig;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.XMLBuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.ImmutableNode;

import java.util.ArrayList;
import java.util.List;

public class Configuracion {

	XMLConfiguration config;

	public Configuracion() throws ConfiguracionException {
		try {
			Parameters params = new Parameters();
			FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class);
			XMLBuilderParameters xbp = params.xml();
			xbp.setFileName("configuracion.xml");
			//xbp.setValidating(true);
			builder.configure(xbp);
			config = builder.getConfiguration();
		} catch (ConfigurationException cex) {
			throw new ConfiguracionException(cex.getMessage(), cex);
		}
	}

	public ExcelConfig leerExcel() throws ConfiguracionException {
		ExcelConfig excelCfg = new ExcelConfig();
		excelCfg.setRuta(config.getString("excel.ruta"));
		excelCfg.setNombreArchivo(config.getString("excel.nombre"));
		return excelCfg;
	}

	public ArrayList<CuentaDatabase> leerCuentas() throws ConfiguracionException {
		ArrayList<CuentaDatabase> cuentas = new ArrayList<CuentaDatabase>();
		List<HierarchicalConfiguration<ImmutableNode>> lista = config.childConfigurationsAt("cuentas");
		for (HierarchicalConfiguration<ImmutableNode> prop : lista) {
			CuentaDatabase cuenta = new CuentaDatabase();
			cuenta.setService(prop.getString("service"));
			cuenta.setUsuario(prop.getString("usuario"));
			cuenta.setPassword(prop.getString("password"));
			cuenta.setHost(prop.getString("host"));
			cuentas.add(cuenta);
		}
		return cuentas;
	}

}
