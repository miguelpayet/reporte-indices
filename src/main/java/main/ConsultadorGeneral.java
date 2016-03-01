package main;

import adaptador.CuentaDatabase;
import config.Configuracion;
import config.ConfiguracionException;
import excel.Excel;
import excel.ExcelConfig;
import excel.ExcelException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class ConsultadorGeneral {

	private static Configuracion configuracion = null;
	private ArrayList<CuentaDatabase> cuentas;
	private Excel excel;
	private ExcelConfig excelCfg;
	private static final Logger logger = LogManager.getLogger(ConsultadorGeneral.class);

	private static Configuracion getConfiguracion() throws ConfiguracionException {
		if (configuracion == null) {
			configuracion = new Configuracion();
		}
		return configuracion;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void main(String[] args) {
		logger.info("inicio");
		ConsultadorGeneral cg = new ConsultadorGeneral();
		try {
			cg.init();
		} catch (ConfiguracionException e) {
			ConsultadorGeneral.logger.error("error en main: " + e.getMessage());
			e.printStackTrace();
		}
		cg.lanzarCuentas();
		logger.info("final");
	}

	private void init() throws ConfiguracionException {
		cuentas = ConsultadorGeneral.getConfiguracion().leerCuentas();
		excelCfg = ConsultadorGeneral.getConfiguracion().leerExcel();
		excel = new Excel(excelCfg);

	}

	private void lanzarCuentas() {
		for (CuentaDatabase cuenta : cuentas) {
			try {
				Consultador lector = new Consultador(cuenta);
				lector.setExcel(excel);
				try {
					lector.consultar(excel);
				} finally {
					lector.cerrarConexion();
					excel.grabar();
				}
			} catch (ConsultadorException e) {
				ConsultadorGeneral.logger.error("error en lanzarCuentas: " + e.getMessage());
				e.printStackTrace();
			} catch (ExcelException e) {
				ConsultadorGeneral.logger.error("error al grabar excel en lanzarCuentas: " + e.getMessage());
			}
		}
	}

}
