/*
 * Classe que cria a pe�a de xadrez em si, nesse caso a Torre e o seu m�todo toString para imprimir o nome atribuido da pe�a no tabuleiro
 */

package chess.pieces;

import boardGame.Board;
import chess.ChessPiece;
import chess.Color;

public class Rook extends ChessPiece{

	public Rook(Board board, Color color) {
		super(board, color);
	}
	
	@Override
	public String toString() {
		return "R";
	}
}
