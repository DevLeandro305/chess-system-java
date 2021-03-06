package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardGame.Board;
import boardGame.Piece;
import boardGame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
	
	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
	
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
	}
	
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0 ; i < board.getRows() ; i++) {
			for (int j = 0 ; j < board.getColumns() ; j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		
		return mat;
	}
	
	//Metodo para mostrar os possiveis movimentos da pe?a, colorindo as possiveis posicoes no tabuleiro
	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	//Metodo para mover as pe?as no tabuleiro
	public ChessPiece performChessMove (ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
		
		//testa se o movimento do jogador atual se colocou em xeque
		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}
		
		ChessPiece movedPiece = (ChessPiece)board.piece(target);
		
		//Movimento especial promo??o
		promoted = null;
		if (movedPiece instanceof Pawn) {
			if ((movedPiece.getColor() == Color.WHITE && target.getRow() == 0) || (movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
				promoted = (ChessPiece)board.piece(target);
				promoted = replacePromotedPiece("Q");
			}
		}
		
		//testa se o oponente ficou em xeque
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		//testa o xeque-mate
		if (testCheckmate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}
		
		//movimento especial en passant
		if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		} else {
			enPassantVulnerable = null;
		}
		
		return (ChessPiece)capturedPiece;
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if (!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
			return promoted;
		}
		
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type, Color color) {
		if (type.equals("B")) return new Bishop(board, color);
		if (type.equals("N")) return new Knight(board, color);
		if (type.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);
	}
	
	//Metodo para fazer o movimento em si, recebendo a posicao inicial e a posicao final
	private Piece makeMove(Position source, Position target) {
		 ChessPiece p = (ChessPiece)board.removePiece(source);
		 p.increaseMoveCount();
		 Piece capturedPiece = board.removePiece(target);
		 board.placePiece(p, target);
		 
		 if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		 
		 //Movimento especial roque pequeno
		 if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			 Position sourceRook = new Position(source.getRow(), source.getColumn() + 3);
			 Position targetRook = new Position(source.getRow(), source.getColumn() + 1);
			 ChessPiece rook = (ChessPiece)board.removePiece(sourceRook);
			 board.placePiece(rook, targetRook);
			 rook.increaseMoveCount();
		 }
		 
		 //Movimento especial roque grande
		 if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			 Position sourceRook = new Position(source.getRow(), source.getColumn() - 4);
			 Position targetRook = new Position(source.getRow(), source.getColumn() - 1);
			 ChessPiece rook = (ChessPiece)board.removePiece(sourceRook);
			 board.placePiece(rook, targetRook);
			 rook.increaseMoveCount();
		 }
		 
		 //Movimento especial en passant
		 if (p instanceof Pawn) {
			 if (source.getColumn() != target.getColumn() && capturedPiece == null) {
				 Position pawnPosition;
				 if (p.getColor() == Color.WHITE) {
					 pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				 } else {
					 pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				 }
				 capturedPiece = board.removePiece(pawnPosition);
				 capturedPieces.add(capturedPiece);
				 piecesOnTheBoard.remove(capturedPiece);
			 }
		 }
		 
		 return capturedPiece;
	}
	
	//Metodo para desfazer o movimento caso o jogador se coloque em posi??o de xeque
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		
		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}

		//Movimento especial roque pequeno
		 if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			 Position sourceRook = new Position(source.getRow(), source.getColumn() + 3);
			 Position targetRook = new Position(source.getRow(), source.getColumn() + 1);
			 ChessPiece rook = (ChessPiece)board.removePiece(targetRook);
			 board.placePiece(rook, sourceRook);
			 rook.decreaseMoveCount();
		 }
		 
		 //Movimento especial roque grande
		 if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			 Position sourceRook = new Position(source.getRow(), source.getColumn() - 4);
			 Position targetRook = new Position(source.getRow(), source.getColumn() - 1);
			 ChessPiece rook = (ChessPiece)board.removePiece(targetRook);
			 board.placePiece(rook, sourceRook);
			 rook.decreaseMoveCount();
		 }
		 
		 //Movimento especial en passant
		 if (p instanceof Pawn) {
			 if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
				 ChessPiece pawn = (ChessPiece)board.removePiece(target);
				 Position pawnPosition;
				 if (p.getColor() == Color.WHITE) {
					 pawnPosition = new Position(3, target.getColumn());
				 } else {
					 pawnPosition = new Position(4, target.getColumn());
				 }
				 board.placePiece(pawn, pawnPosition);
			 }
		 }
	
	}
	
	//Metodo para validar se existe alguma pe?a na posicao inicial do movimento
	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece");
		}
	}
	
	//Metodo para validar a posicao final do movimento
	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to target position");
		}
	}
	
	//Metodo para troca de turno baseado na cor do jogador atual
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	//Metodo para devolver as cores contrarias do oponente, se for branco, devolve a cor preta
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	//Metodo para achar a pe?a rei com base na sua cor
	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("There is no " + color + " King on the board");
	}
	
	//Metodo para testar se alguma pe?a pode colocar o rei em xeque
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece p: opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}
	
	//Metodo para testar o xeque-mate
	private boolean testCheckmate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());		//retorna todas as pe?as da cor passada como parametro
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece  capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	//M?todo para colocar uma pe?a no tabuleiro seguindo a regra do xadrez e n?o da matriz
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	//M?todo responsavel por iniciar a partida de xadrez, colocando as pe?as nos deus devidos lugares
	private void initialSetup() {
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
		placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));
		
		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('b', 8, new Knight(board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		placeNewPiece('e', 8, new King(board, Color.BLACK, this));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));
		placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));

	}
	
}
