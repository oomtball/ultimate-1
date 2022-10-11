/* Copyright (c) 2017-2022, TU Dortmund University
 * This file is part of ADD-Lib, https://add-lib.scce.info/.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * Neither the name of the TU Dortmund University nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package info.scce.addlib.profiling.jmh.bdd.states;

import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;

public class NQueens {

    private final BDDManager bddManager;
    private final int boardSize;

    public NQueens(BDDManager bddManager, int boardSize) {
        this.bddManager = bddManager;
        this.boardSize = boardSize;
    }

    public BDD createNQueensBDD() {
        /* Create a BDD based on the N-queens problem */

        /* BDD variables based on whether queen is on tile (i,j) or not */
        BDD[][][] vars = new BDD[boardSize][boardSize][2];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                vars[i][j][0] = bddManager.namedVar("var_" + i + "_" + j);
                vars[i][j][1] = vars[i][j][0].not();
            }
        }

        /* Queen is on tile (i,j) and not beaten by another queen */
        BDD[][] queenOnTileBDD = new BDD[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                queenOnTileBDD[i][j] = placeQueenOnTile(i, j, vars);
            }
        }

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                vars[i][j][0].recursiveDeref();
                vars[i][j][1].recursiveDeref();
            }
        }

        /* One queen per row */
        BDD nQueensBDD = bddManager.readOne();
        for (int i = 0; i < boardSize; i++) {
            BDD onePerRowBDD = bddManager.readLogicZero();
            for (int j = 0; j < boardSize; j++) {
                BDD onePerRowBDDTemp = onePerRowBDD.or(queenOnTileBDD[i][j]);
                onePerRowBDD.recursiveDeref();
                onePerRowBDD = onePerRowBDDTemp;
            }

            BDD nQueensBDDTemp = nQueensBDD.and(onePerRowBDD);
            nQueensBDD.recursiveDeref();
            onePerRowBDD.recursiveDeref();
            nQueensBDD = nQueensBDDTemp;
        }

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                queenOnTileBDD[i][j].recursiveDeref();
            }
        }

        return nQueensBDD;
    }

    private BDD placeQueenOnTile(int row, int col, BDD[][][] vars) {
        BDD queenOnTileBDD = bddManager.readOne();
        BDD queenOnTileBDDTemp = queenOnTileBDD.and(vars[row][col][0]);
        queenOnTileBDD.recursiveDeref();
        queenOnTileBDD = queenOnTileBDDTemp;

        /* No other queen in row */
        for (int j = 0; j < boardSize; j++) {
            if (j != col) {
                queenOnTileBDDTemp = queenOnTileBDD.and(vars[row][j][1]);
                queenOnTileBDD.recursiveDeref();
                queenOnTileBDD = queenOnTileBDDTemp;
            }
        }

        /* No other queen in column */
        for (int i = 0; i < boardSize; i++) {
            if (i != row) {
                queenOnTileBDDTemp = queenOnTileBDD.and(vars[i][col][1]);
                queenOnTileBDD.recursiveDeref();
                queenOnTileBDD = queenOnTileBDDTemp;
            }
        }

        /* No other queen in diagonal */
        int lowerVal = Math.min(row, col);
        int diagonalSize = boardSize - Math.abs(row - col);

        for (int i = 0; i < diagonalSize; i++) {
            int rowIdx = row - lowerVal + i;
            int colIdx = col - lowerVal + i;
            if (rowIdx != row) {
                queenOnTileBDDTemp = queenOnTileBDD.and(vars[rowIdx][colIdx][1]);
                queenOnTileBDD.recursiveDeref();
                queenOnTileBDD = queenOnTileBDDTemp;
            }
        }

        /* No other queen in anti-diagonal */
        int antiLowerVal = Math.min(row, boardSize - 1 - col);
        int antiDiagonalSize = boardSize - Math.abs(row - (boardSize - 1 - col));

        for (int i = 0; i < antiDiagonalSize; i++) {
            int rowIdx = row - antiLowerVal + i;
            int colIdx = col + antiLowerVal - i;
            if (rowIdx != row) {
                queenOnTileBDDTemp = queenOnTileBDD.and(vars[rowIdx][colIdx][1]);
                queenOnTileBDD.recursiveDeref();
                queenOnTileBDD = queenOnTileBDDTemp;
            }
        }
        return queenOnTileBDD;
    }
}
