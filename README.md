# 2048 AI

The purpose of this project is to practice certain adversarial search concepts outside of an
Aritificial Intelligence class.

This program provides two different approaches to solving the hit game 2048 by Gabriele Cirulli.

The first is an Alpha Beta Pruning approach, which assumes the game is trying to place you
in the worst poistion possible after each move.

The second is a Expectimax approach, where we consider the probabilities of tile placements to determine
the average best move. This approach is influenced by [nneoneo's](https://github.com/nneonneo/2048-ai) approach
by including a lookup table to avoid previously seen board states and not considering succesive tile placements 
with low probabilities.

The heuristic im using is a combination of bonus points for having high numbers of merges, many empty spaces
and keeping the larges tile in a corner of the board.
   
