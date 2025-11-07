package com.chessmaster.StockFish;

import org.springframework.web.client.RestClient;
import org.springframework.stereotype.Service;

import com.chessmaster.Models.Move;
import com.chessmaster.Models.Piece;
import org.springframework.beans.factory.annotation.Value;

@Service
public class FenUtil {

    private final RestClient restClient;

    @Value("${stockfish.url}")
    private String stockfishBaseUrl;

    public FenUtil(RestClient restClient) {
        this.restClient = restClient;
    }

   public static String boardToFEN(Piece[][] board, String turn) {

    StringBuilder fen = new StringBuilder();

    // FEN ranks: 8 → 1
    for (int row = 7; row >= 0; row--) {
        int empty = 0;

        for (int col = 0; col < 8; col++) {
            Piece piece = board[row][col];

            if (piece == null) {
                empty++;
            } else {
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append(pieceToFenChar(piece));   // must return K/Q/R/B/N/P or lowercase
            }
        }
        if (empty > 0) {
            fen.append(empty);
        }
        if (row > 0) fen.append('/');
    }

    // Convert turn
    String fenTurn = turn.equalsIgnoreCase("white") ? "w" : "b";

    // No castling, no en-passant, halfmove=0, fullmove=1
    fen.append(" ").append(fenTurn)
       .append(" - - 0 1");

    return fen.toString();
}

    private static String pieceToFenChar(Piece piece) {
        String name = piece.getName().toLowerCase();
        String color = piece.getColor().toLowerCase();

        char c;
        switch (name) {
            case "king":   c = 'k'; break;
            case "queen":  c = 'q'; break;
            case "rook":   c = 'r'; break;
            case "bishop": c = 'b'; break;
            case "knight": c = 'n'; break;
            case "pawn":   c = 'p'; break;
            default:       c = '?';
        }

        return color.equals("white")
                ? String.valueOf(Character.toUpperCase(c))
                : String.valueOf(c);
    }

   public static Move parseBestMove(String bestmove) {

        String from = bestmove.substring(0, 2);
        String to   = bestmove.substring(2, 4);

        int oc = from.charAt(0) - 'a';
        int or = Character.getNumericValue(from.charAt(1)) - 1;

        int nc = to.charAt(0) - 'a';
        int nr = Character.getNumericValue(to.charAt(1)) - 1;

        return new Move(or, oc, nr, nc);
    }

    public Move getnxtMove(String fen, int depth) {

        StockfishRequest request = new StockfishRequest(fen, depth);

        StockfishResponse res = restClient.post()
                .uri(stockfishBaseUrl + "/bestmove")
                .body(request)
                .retrieve()
                .body(StockfishResponse.class);

        return parseBestMove(res.getBestmove());
    }

}
