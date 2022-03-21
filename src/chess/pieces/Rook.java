/*
 * Classe que cria a peça de xadrez em si, nesse caso a Torre e o seu método toString para imprimir o nome atribuido da peça no tabuleiro
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
