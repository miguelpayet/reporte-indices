package main;

import org.apache.poi.ss.usermodel.*;

import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class Consultador implements Runnable {

	private class TimerConsulta extends TimerTask {
		@Override
		public void run() {
			mostrarAvance();
		}
	}

	private Connection conn;
	private Sheet excelSheet;
	private int filaExcel;
	private CellStyle formatoFecha;
	private PreparedStatement pstmtCursor;
	private Timer timer;

	public Consultador(Connection unaConexion, String consulta, String unaCondicion, int unaFilaExcel) throws ExcepcionConsultador {
		try {
			conn = unaConexion;
			filaExcel = unaFilaExcel;
			prepararSentencias(String.format("%s %s", consulta, unaCondicion));
		} catch (Exception e) {
			throw new ExcepcionConsultador(e.getMessage(), e);
		}
	}

	private void cerrarConexion() throws ExcepcionConsultador {
		try {
			conn.close();
		} catch (SQLException e) {
			throw new ExcepcionConsultador("SQLException - cerrarConexion: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("ThrowFromFinallyBlock")
	private void consultar() throws ExcepcionConsultador {
		try {
			try {
				ResultSet rsetCursor = pstmtCursor.executeQuery();
				ResultSetMetaData rsmdInd = rsetCursor.getMetaData();
				if (filaExcel == 0) {
					grabarHeader(rsmdInd);
				}
				try {
					while (rsetCursor.next()) {
						Row row = excelSheet.createRow(filaExcel++);
						grabarColumnas(row, rsetCursor, rsmdInd, 0);
					}
				} finally {
					rsetCursor.close();
				}
			} finally {
				if (timer != null) {
					timer.cancel();
				}
				pstmtCursor.close();
			}
		} catch (SQLException e) {
			throw new ExcepcionConsultador("SQLException - consultar: " + e.getMessage(), e);
		}
	}

	private void crearFormatos() {
		Workbook wb = excelSheet.getWorkbook();
		formatoFecha = wb.createCellStyle();
		CreationHelper createHelper = wb.getCreationHelper();
		formatoFecha.setDataFormat(createHelper.createDataFormat().getFormat("d-mmm-yy"));
	}

	public int getFilaExcel() {
		return filaExcel;
	}

	private int grabarColumnas(Row row, ResultSet rset, ResultSetMetaData rsmd, int columna) throws ExcepcionConsultador {
		try {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				Cell celda = row.createCell(columna++);
				switch (rsmd.getColumnType(i)) {
					case Types.CHAR:
						celda.setCellValue(rset.getString(i));
						break;
					case Types.VARCHAR:
						celda.setCellValue(rset.getString(i));
						break;
					case Types.NUMERIC:
						celda.setCellValue(rset.getFloat(i));
						break;
					case Types.DATE:
						celda.setCellValue(rset.getDate(i));
						celda.setCellStyle(formatoFecha);
						break;
					case Types.TIMESTAMP:
						celda.setCellValue(rset.getDate(i));
						celda.setCellStyle(formatoFecha);
						break;
				}
			}
		} catch (SQLException e) {
			throw new ExcepcionConsultador("SQLException - grabarColumnas: " + e.getMessage(), e);
		}
		return columna;
	}

	private void grabarHeader(ResultSetMetaData rsmdInd) throws ExcepcionConsultador {
		Row row = excelSheet.createRow(filaExcel++);
		grabarTitulos(row, rsmdInd, 0);
	}

	private int grabarTitulos(Row row, ResultSetMetaData rsmd, int columna) throws ExcepcionConsultador {
		try {
			for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
				Cell celda = row.createCell(columna++);
				celda.setCellValue(rsmd.getColumnName(i));
			}
		} catch (SQLException e) {
			throw new ExcepcionConsultador("SQLException - grabarTitulos: " + e.getMessage(), e);
		}
		return columna;
	}

	private void mostrarAvance() {
		ConsultadorAplicacion.getLogger().info(String.format("%s - %s filas", excelSheet.getSheetName(), filaExcel));
	}

	private void prepararSentencias(String unQuery) throws ExcepcionConsultador {
		try {
			pstmtCursor = conn.prepareStatement(unQuery);
		} catch (SQLException e) {
			throw new ExcepcionConsultador("SQLException - prepararSentencias: " + e.getMessage(), e);
		}
	}

	public void run() {
		try {
			startTimer();
			consultar();
		} catch (ExcepcionConsultador e) {
			ConsultadorAplicacion.getLogger().error(String.format("Error al ejecutar consulta: %s ", e.getMessage()));
		} finally {
			try {
				cerrarConexion();
			} catch (ExcepcionConsultador e) {
				ConsultadorAplicacion.getLogger().error(String.format("Error al cerrar conexión: %s ", e.getMessage()));
			}
		}
	}

	public void setExcel(Sheet unExcel) {
		this.excelSheet = unExcel;
		crearFormatos();
	}

	private void startTimer() {
		long intervalo = ConsultadorAplicacion.getIntervaloLog() * 60 * 1000;
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerConsulta(), 0, intervalo);
	}

}
