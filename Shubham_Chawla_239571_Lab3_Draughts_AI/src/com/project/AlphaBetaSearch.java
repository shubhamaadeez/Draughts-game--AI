package com.project;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlphaBetaSearch extends Player implements ABS_interface {

    private int depth;
    private static double nodesCount;
    private static double maxPrune;
    private static double minPrune;
    private Point skip;
    public AlphaBetaSearch(Side s, int depth)  //Computer side
    {
        super("AlphaBetaSearch", s);
        this.depth = depth;
    }
    //making a move by checking best next move
    public Board.Decision makeMove(Board board)
    {
        nodesCount++;
        Move m = minimaxBegin(board, depth, getSide(), true); // MIN MAX call
        Board.Decision decision = board.makeMove(m, getSide());
        if(decision == Board.Decision.CAPTURE_MOVE)
            skip = m.getEnd();

        System.out.println("\nMax depth: " + depth);
        System.out.println("Number of nodes generated: " + nodesCount);
        System.out.println("Number of times pruning done in Max function: " + maxPrune);
        System.out.println("Number of times pruning done in Min function: " + minPrune);
        return decision;
    }
 // Implementation of Min-Max Function   
    private Move minimaxBegin(Board board, int depth, Side side, boolean maxPlayer) 
    {
        int alpha = -1000;
        int beta = 1000;
        List<Move> captureMoves = board.getAllValidCaptureMoves(side); // Board.java
        List<Move> possibleMovesList;
        List<Integer> heuristics = new ArrayList<>();
        
        if(skip != null) {
            possibleMovesList = board.getValidCaptureMoves(skip.x, skip.y, side);
            skip = null;
        }
        else if(!captureMoves.isEmpty()) {
            possibleMovesList = board.getAllValidCaptureMoves(side);
        }
        else {
            possibleMovesList = board.getAllValidMoves(side);
        }

        if(possibleMovesList.isEmpty())
            return null;
        
        Board tempBoard = null;
        for(int i = 0; i < possibleMovesList.size(); i++)
        {
            tempBoard = board.clone();
            tempBoard.makeMove(possibleMovesList.get(i), side);
            heuristics.add(minimax(tempBoard, depth - 1, switchSide(side), !maxPlayer, alpha, beta));
        }

        int maxHeuristics = -1000;

        Random rand = new Random();         //using random to go through all possible moves
        for(int i = heuristics.size() - 1; i >= 0; i--) {
            if (heuristics.get(i) >= maxHeuristics) {
                maxHeuristics = heuristics.get(i);
            }
        }
        for(int i = 0; i < heuristics.size(); i++)
        {
            if(heuristics.get(i) < maxHeuristics)
            {
                heuristics.remove(i);
                possibleMovesList.remove(i);
                i--;
            }
        }
        return possibleMovesList.get(rand.nextInt(possibleMovesList.size()));
    }
// Evaluation Function for using this as a heustric  
    private int heuristic(Board b)
    {
        //using the difference between black or white pieces as heuristic
        if(getSide() == Side.BLACK)
            return b.getNumBlackPieces() - b.getNumWhitePieces();
        else
            return b.getNumWhitePieces() - b.getNumBlackPieces();

    }
// Alpha Beta Pruning finding the best move
    private int minimax(Board board, int depth, Side side, boolean maxPlayer, int alpha, int beta)
    {
        if(depth == 0) {
            return heuristic(board);
        }
        List<Move> possibleMovesList = board.getAllValidMoves(side);

        int bestValue = 0;
        Board tempBoard = null;

        if(maxPlayer)
        {
            //MAX-VALUE
            bestValue = -1000;          //min utility value
            for(int i = 0; i < possibleMovesList.size(); i++)
            {
                tempBoard = board.clone();
                tempBoard.makeMove(possibleMovesList.get(i), side);

                int result_value = minimax(tempBoard, depth - 1, switchSide(side), !maxPlayer, alpha, beta);

                bestValue = Math.max(result_value, bestValue);
                alpha = Math.max(alpha, bestValue);

                if(alpha >= beta) {
                    maxPrune++;
                    break;
                }
            }
        }
        else
        {
            //MIN-VALUE
            bestValue = 1000;           //max utility value
            for(int i = 0; i < possibleMovesList.size(); i++)
            {
                tempBoard = board.clone();
                tempBoard.makeMove(possibleMovesList.get(i), side);

                int result_value = minimax(tempBoard, depth - 1, switchSide(side), !maxPlayer, alpha, beta);

                bestValue = Math.min(result_value, bestValue);
                beta = Math.min(beta, bestValue);

                if(alpha >= beta) {
                    minPrune++;
                    break;
                }
            }
        }

        return bestValue;       //return the result from min or max function
    }


    private Side switchSide(Side side)
    {
        if(side == Side.BLACK)
            return Side.WHITE;
        return Side.BLACK;
    }
}
