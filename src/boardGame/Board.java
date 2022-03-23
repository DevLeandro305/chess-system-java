package boardGame;

public class Board {

	private int rows;
	private int columns;
	private Piece[][] pieces;

	public Board(int rows, int columns) {
		if (rows < 1 || columns < 1) {
			throw new BoardException("Error creating board: there must be at least 1 row and 1 column");
		}
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece[rows][columns];
	}

	public int getRows() {
		return rows;
	}

	// Uma vez instanciado o tabuleiro, n�o deve ser permitido alterar o n� de
	// linhas e colunas, por isso os m�todos set foram retirados

	// public void setRowns(int rows) {
	// this.rows = rows;
	// }

	public int getColumns() {
		return columns;
	}

	// public void setColumns(int columns) {
	// this.columns = columns;
	// }

	public Piece piece(int row, int column) {
		if (!positionExists(row, column)) {
			throw new BoardException("Position not on the board");
		}
		return pieces[row][column];
	}

	public Piece piece(Position position) {
		if (!positionExists(position)) {
			throw new BoardException("Position not on the board");
		}
		return pieces[position.getRow()][position.getColumn()];
	}

	// M�todo para atribuir uma pe�a a um local no tabuleiro, de acordo com a posi��o da linha e coluna informada.
	public void placePiece(Piece piece, Position position) {
		if (thereIsAPiece(position)) {
			throw new BoardException("There is already a piece on position" + position);
		}
		pieces[position.getRow()][position.getColumn()] = piece;
		piece.position = position;
	}
	
	//Metodo para remover uma pe�a do tabuleiro, desde que ela exista no local designado
	public Piece removePiece(Position position) {
		if (!positionExists(position) ) {
			throw new BoardException("Position not ontthe board");
		}
		if (piece(position) == null) {
			return null;
		}
		Piece aux = piece(position);
		aux.position = null;
		pieces[position.getRow()][position.getColumn()] = null;
		return aux;
	}

	// M�todo auxiliar para ajudar no teste se a posi��o no tabuleiro existe, dada uma linha e coluna
	private boolean positionExists(int row, int column) {
		return row >= 0 && row < rows && column >= 0 && column < columns;
	}

	// M�todo para testar se a posi��o no tabuleiro existe
	public boolean positionExists(Position position) {
		return positionExists(position.getRow(), position.getColumn());
	}

	// M�todo para verificar se tem uma pe�a na posi��o solicitada
	public boolean thereIsAPiece(Position position) {
		if (!positionExists(position)) {
			throw new BoardException("Position not on the board");
		}
		return piece(position) != null;
	}

}
