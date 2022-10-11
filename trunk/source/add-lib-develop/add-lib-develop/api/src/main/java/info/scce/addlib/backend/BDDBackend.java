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
package info.scce.addlib.backend;

public interface BDDBackend extends Backend {

    /* BDDManager methods */
    long readOne(long ddManager);

    long readLogicZero(long ddManager);

    long ithVar(long ddManager, int var);

    long newVar(long ddManager);

    long newVarAtLevel(long ddManager, int level);

    /* BDD methods */
    @Override
    long t(long dd);

    @Override
    long e(long dd);

    @Override
    long eval(long ddManager, long dd, int... input);

    long not(long dd);

    long ite(long ddManager, long dd, long t, long e);

    long iteLimit(long ddManager, long dd, long t, long e, int limit);

    long iteConstant(long ddManager, long dd, long t, long e);

    long intersect(long ddManager, long dd, long g);

    long and(long ddManager, long dd, long g);

    long andLimit(long ddManager, long dd, long g, int limit);

    long or(long ddManager, long dd, long g);

    long orLimit(long ddManager, long dd, long g, int limit);

    long nand(long ddManager, long dd, long g);

    long nor(long ddManager, long dd, long g);

    long xor(long ddManager, long dd, long g);

    long xnor(long ddManager, long dd, long g);

    long xnorLimit(long ddManager, long dd, long g, int limit);

    int leq(long ddManager, long dd, long g);

    long compose(long ddManager, long dd, long g, int var);

    long vectorCompose(long ddManager, long dd, long... vector);

    int isComplement(long dd);

}
