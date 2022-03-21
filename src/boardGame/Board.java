package boardGame;

public class Board {
	
	private int rows;
	private int columns;
	private Piece[][] pieces;
	
	public Board(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece[rows][columns];
	}
	
	public int getRows() {
		return rows;
	}
	
	public void setRowns(int rows) {
		this.rows = rows;
	}
	
	public int getColumns() {
		return columns;
	}
	
	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	public Piece piece(int row, int column) {
		return pieces[row][column];
	}
	
	public Piece piece(Position position) {
		return pieces[position.getRow()][position.getColumn()];
	}
	
	/*
	 * Método para atribuir uma peça a um local no tabuleiro, de acordo com a posição da linha e coluna informada.
	 */
	public void placePiece(Piece piece, Position position) {
		pieces[position.getRow()][position.getColumn()] = piece;
		piece.position = position;
	}

}
