package main;

import adaptador.CuentaDatabase;
import config.Configuracion;
import config.ConfiguracionException;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class ConsultadorAplicacion {

	private static Configuracion configuracion = null;
	private static final Logger logger = LogManager.getLogger(ConsultadorAplicacion.class);
	private static ArrayList<CuentaDatabase> cuentas;
	private static Long intervaloLog;
	private static Boolean usarHilos;

	private static Options crearOpciones() {
		Options options = new Options();
		Option optHilos = Option.builder("m").longOpt("multihilo").required(false).hasArg(true).type(Long.class).desc("usar un hilo por consulta (1 = si, 0 = no)").build();
		options.addOption(optHilos);
		Option optIntervalo = Option.builder("i").longOpt("intervalo-log").required(false).hasArg(true).type(Boolean.class).desc("minutos de intervalo de impresión de avance").build();
		options.addOption(optIntervalo);
		return options;
	}

	public static Configuracion getConfiguracion() throws ConfiguracionException {
		if (configuracion == null) {
			configuracion = new Configuracion();
		}
		return configuracion;
	}

	static Long getIntervaloLog() {
		return intervaloLog;
	}

	public static Logger getLogger() {
		return logger;
	}

	private static void init(String[] args) throws ConfiguracionException {
		cuentas = ConsultadorAplicacion.getConfiguracion().leerCuentas();
		initOpciones(args);
	}

	private static void initOpciones(String[] args) throws ConfiguracionException {
		Options options = crearOpciones();
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			intervaloLog = Long.parseLong(cmd.getOptionValue("i", "1"));
			String opcHilos = cmd.getOptionValue("m", "0");
			usarHilos = !opcHilos.equals("0");
		} catch (ParseException e) {
			throw new ConfiguracionException(String.format("error en parámetros %s", e.getMessage()), e);
		}
	}

	public static Boolean isUsarHilos() {
		return usarHilos;
	}

	private static void lanzarCuentas() {
		cuentas.forEach(CuentaDatabase::ejecutarConsultas);
	}

	public static void main(String[] args) {
		logger.info("inicio");
		try {
			init(args);
		} catch (ConfiguracionException e) {
			ConsultadorAplicacion.logger.error("error en main: " + e.getMessage());
			e.printStackTrace();
		}
		lanzarCuentas();
		logger.info("final");
	}

}
