package excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Excel {

	private ExcelConfig cfg;
	private String nombreCondicion;
	private SXSSFWorkbook wb;

	public Excel(ExcelConfig cfg) throws ExcelException {
		this.cfg = cfg;
		wb = new SXSSFWorkbook(100);
	}

	public Excel(ExcelConfig unaConfiguracion, String unNombreCondicion) throws ExcelException {
		this.cfg = unaConfiguracion;
		this.nombreCondicion = unNombreCondicion;
		wb = new SXSSFWorkbook(100);
	}

	@SuppressWarnings("unused")
	public void abrir() throws ExcelException {
		try {
			File file = new File(getFilename());
			FileInputStream fis = new FileInputStream(file);
			XSSFWorkbook tempwb = new XSSFWorkbook(fis);
			wb = new SXSSFWorkbook(tempwb);
		} catch (IOException e) {
			throw new ExcelException("grabar - " + e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	public Sheet getCurrentSheet(String sName) {
		Sheet currentSheet = wb.getSheet(sName);
		if (currentSheet == null) {
			currentSheet = wb.createSheet(sName);
		}
		return currentSheet;
	}

	private String getFilename() {
		return cfg.getRuta() + cfg.getNombreArchivo().replace(".", String.format("-%s.", nombreCondicion));
	}

	public void grabar() throws ExcelException {
		try {
			FileOutputStream out = new FileOutputStream(getFilename());
			wb.write(out);
			out.close();
			wb.close();
		} catch (IOException e) {
			throw new ExcelException("grabar - " + e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}
}
