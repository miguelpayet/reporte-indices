package main;

import adaptador.CuentaDatabase;
import adaptador.Database;
import adaptador.DatabaseException;
import excel.Excel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.sql.*;

class Consultador {

	private final static String QUERY_BLOQUES = "SELECT Blocks*128 megabytes FROM DBA_Segments WHERE OWNER = :1 AND Segment_Name = :2 AND Segment_Type LIKE '%INDEX%'";
	private final static String QUERY_COLUMNAS = "SELECT column_name, descend FROM dba_ind_columns WHERE index_name = :1 AND table_owner = :2 AND table_name = :3 ORDER BY column_position";
	private final static String QUERY_CURSOR = "SELECT owner, index_name, index_type, table_owner, table_name, distinct_keys, num_rows, tablespace_name FROM SYS.dba_indexes WHERE OWNER NOT IN ('SYS', 'SYSTEM','CTXSYS','XDB','OLAPSYS','ORDSYS','MDSYS','SYSMAN','WMSYS','SQLTXPLAIN','EXPIMP') ORDER BY 4,5,2"; //AND OWNER='USUWEB00'
	private final static String TIPO_ASC = "ASC";
	private Connection conn;
	private Database db;
	private Excel excel;
	private Sheet excelSheet;
	private int filaExcel;
	private PreparedStatement pstmtBloques;
	private PreparedStatement pstmtColumnas;
	private PreparedStatement pstmtCursor;

	Consultador(CuentaDatabase cuenta) throws ConsultadorException {
		try {
			filaExcel = 0;
			db = new Database();
			db.setCuenta(cuenta);
			db.connect();
			prepararSentencias();
		} catch (Exception e) {
			throw new ConsultadorException(e.getMessage(), e);
		}
	}

	void cerrarConexion() throws ConsultadorException {
		try {
			conn.close();
		} catch (SQLException e) {
			throw new ConsultadorException("SQLException - cerrarConexion: " + e.getMessage(), e);
		}
	}

	void consultar(Excel excel) throws ConsultadorException {
		try {
			try {
				ResultSet rsetCursor = pstmtCursor.executeQuery();
				ResultSetMetaData rsmdInd = rsetCursor.getMetaData();
				grabarHeader(rsmdInd);
				try {
					int filaExcel = 1;
					int iColumna;
					while (rsetCursor.next()) {
						Row row = excelSheet.createRow(filaExcel++);
						ConsultadorGeneral.getLogger().info(rsetCursor.getString(4) + "." + rsetCursor.getString(5) + ":" + rsetCursor.getString(1) + "." + rsetCursor.getString(2));
						iColumna = grabarColumnas(row, rsetCursor, rsmdInd, 0);
						double bloques = consultarBloques(rsetCursor);
						grabarColumna(row, bloques, iColumna);
						String sColumnas = consultarConcatenar(rsetCursor);
						grabarColumna(row, sColumnas, iColumna + 1);
					}
				} finally {
					rsetCursor.close();
				}
			} finally {
				pstmtBloques.close();
				pstmtColumnas.close();
				pstmtCursor.close();
			}
		} catch (SQLException e) {
			throw new ConsultadorException("SQLException - consultar: " + e.getMessage(), e);
		}
	}

	private Double consultarBloques(ResultSet rset) throws ConsultadorException {
		Double bloques;
		try {
			pstmtBloques.setString(1, rset.getString(1));
			pstmtBloques.setString(2, rset.getString(2));
			ResultSet rsetBloque = pstmtBloques.executeQuery();
			if (rsetBloque.next()) {
				bloques = rsetBloque.getDouble(1);
			} else {
				bloques = -1d;
			}
		} catch (SQLException e) {
			throw new ConsultadorException("SQLException- consultarBloques: " + e.getMessage(), e);
		}
		return bloques;
	}

	private String consultarConcatenar(ResultSet rsetInd) throws ConsultadorException {
		StringBuilder sbColumnas = new StringBuilder();
		try {
			pstmtColumnas.setString(1, rsetInd.getString(2));
			pstmtColumnas.setString(2, rsetInd.getString(1));
			pstmtColumnas.setString(3, rsetInd.getString(5));
			ResultSet rsetCol = pstmtColumnas.executeQuery();
			try {
				while (rsetCol.next()) {
					if (sbColumnas.length() > 0) {
						sbColumnas.append(",");
					}
					sbColumnas.append(rsetCol.getString(1));
					if (!rsetCol.getString(2).equals(Consultador.TIPO_ASC)) {
						sbColumnas.append(" ");
						sbColumnas.append(rsetCol.getString(2));
					}
				}
			} finally {
				rsetCol.close();
			}
		} catch (SQLException e) {
			throw new ConsultadorException("SQLException - consultarColumnas: " + e.getMessage(), e);
		}
		return sbColumnas.toString();
	}

	private void grabarColumna(Row row, String valor, int columna) {
		Cell celda = row.createCell(columna);
		celda.setCellValue(valor);
	}

	private void grabarColumna(Row row, Double valor, int columna) {
		Cell celda = row.createCell(columna);
		celda.setCellValue(valor);
	}

	private int grabarColumnas(Row row, ResultSet rset, ResultSetMetaData rsmd, int columna) throws ConsultadorException {
		try {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				Cell celda = row.createCell(columna++);
				switch (rsmd.getColumnType(i)) {
					case Types.VARCHAR:
						celda.setCellValue(rset.getString(i));
						break;
					case Types.NUMERIC:
						celda.setCellValue(rset.getFloat(i));
						break;
				}
			}
		} catch (SQLException e) {
			throw new ConsultadorException("SQLException - grabarColumnas: " + e.getMessage(), e);
		}
		return columna;
	}

	private void grabarHeader(ResultSetMetaData rsmdInd) throws ConsultadorException {
		Row row = excelSheet.createRow(filaExcel++);
		int colInd = grabarTitulos(row, rsmdInd, 0);
		grabarTitulo(row, "MEGABYTES", colInd);
		grabarTitulo(row, "COLUMNAS", colInd + 1);
	}

	private void grabarTitulo(Row row, String strTitulo, int columna) {
		Cell celda = row.createCell(columna);
		celda.setCellValue(strTitulo);
	}

	private int grabarTitulos(Row row, ResultSetMetaData rsmd, int columna) throws ConsultadorException {
		try {
			for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
				Cell celda = row.createCell(columna++);
				celda.setCellValue(rsmd.getColumnName(i));
			}
		} catch (SQLException e) {
			throw new ConsultadorException("SQLException - grabarTitulos: " + e.getMessage(), e);
		}
		return columna;
	}

	private void prepararSentencias() throws ConsultadorException {
		try {
			conn = db.getConnection();
			pstmtCursor = conn.prepareStatement(Consultador.QUERY_CURSOR);
			pstmtBloques = conn.prepareStatement(Consultador.QUERY_BLOQUES);
			pstmtColumnas = conn.prepareStatement(Consultador.QUERY_COLUMNAS);
		} catch (DatabaseException e) {
			throw new ConsultadorException("DatabaseException - prepararSentencias: " + e.getMessage(), e);
		} catch (SQLException e) {
			throw new ConsultadorException("SQLException - prepararSentencias: " + e.getMessage(), e);
		}
	}

	public void setExcel(Excel excel) {
		this.excel = excel;
		this.excelSheet = excel.getCurrentSheet(db.getCuenta().getService());
	}

}
