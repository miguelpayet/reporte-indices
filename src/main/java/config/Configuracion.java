package config;

import adaptador.Condicion;
import adaptador.Consulta;
import adaptador.CuentaDatabase;
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

	private XMLConfiguration config;

	public Configuracion() throws ConfiguracionException {
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

	private Consulta leerConsulta() {
		List<Condicion> condiciones = new ArrayList<>();
		List<HierarchicalConfiguration<ImmutableNode>> lista = config.childConfigurationsAt("queries.condiciones");
		for (HierarchicalConfiguration<ImmutableNode> prop : lista) {
			Condicion cond = new Condicion(prop.getString("hoja"), prop.getString("cadena"));
			condiciones.add(cond);
		}
		return new Consulta(config.getString("queries.principal"), condiciones);
	}

	public ArrayList<CuentaDatabase> leerCuentas() throws ConfiguracionException {
		ArrayList<CuentaDatabase> cuentas = new ArrayList<>();
		List<HierarchicalConfiguration<ImmutableNode>> lista = config.childConfigurationsAt("cuentas");
		Consulta cons = leerConsulta();
		for (HierarchicalConfiguration<ImmutableNode> prop : lista) {
			CuentaDatabase cuenta = new CuentaDatabase();
			cuenta.setService(prop.getString("service"));
			cuenta.setUsuario(prop.getString("usuario"));
			cuenta.setPassword(prop.getString("password"));
			cuenta.setHost(prop.getString("host"));
			cuenta.setConsulta(cons);
			cuentas.add(cuenta);
		}
		return cuentas;
	}

	public ExcelConfig leerExcel() throws ConfiguracionException {
		ExcelConfig excelCfg = new ExcelConfig();
		excelCfg.setRuta(config.getString("excel.ruta"));
		excelCfg.setNombreArchivo(config.getString("excel.nombre"));
		return excelCfg;
	}

}
