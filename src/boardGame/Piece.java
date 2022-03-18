package boardGame;

public class Piece {
	
	protected Position position;
	private Board board;
	
	public Piece(Board board) {
		this.board = board;
		position = null;
	}

	protected Board getBoard() {			//est� protected por que o tabuleiro em si n�o seja acessado pelo programa, somente pela camada boardGame;
		return board;
	}

}
