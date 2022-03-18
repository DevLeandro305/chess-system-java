package boardGame;

public class Piece {
	
	protected Position position;
	private Board board;
	
	public Piece(Board board) {
		this.board = board;
		position = null;
	}

	protected Board getBoard() {			//está protected por que o tabuleiro em si não seja acessado pelo programa, somente pela camada boardGame;
		return board;
	}

}
