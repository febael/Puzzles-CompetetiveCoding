Here are some code that I've written mostly in response to requests from companies during interview processes. Some are written to tackle a challenging problem in my personal time, though.

TETRIS
------
Written for BaseCase in May, 2015. Requirement, as I've received it, is :

Implement a simple ‘text-mode’ version of the Tetris game, following the specification below. You are free to use a language of your choice, but please don’t go beyond the specification or add any features not explicitly requested.
If you feel some part of this spec is unclear or contradictory in some detail, please resolve it using your best judgement, and make a note explaining your decision.
There are 5 different pieces in this version of Tetris:

    0000      0      0      0      00 
              0      0     00      00
              00    00     0
        
and they fall down a 20x20 tetris board:

    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |                    |
    |____________________|
    
The game starts with a random piece appearing at the top of the board. The user is then prompted to make a move:
* a (return): move piece left
* d (return): move piece right
* w (return): rotate piece counter clockwise
* s (return): rotate piece clockwise

If the move the user selects is valid, then it is executed and the screen redrawn (you can use printf()/cout/System.out.println(), etc to redraw the entire board). If the action is not valid, then the user is again prompted to enter a valid move.

Note that the game only updates after the user has entered a valid action. A valid move is defined thus: The piece is altered as per the user’s input, and then displaced by 1 row downwards.

If the piece, drawn at its new location, is not outside the bounds of the board, and does not overlap any pieces that previously fell, then the move is valid.

If the piece’s new position is such that it allows no valid move, then a new piece appears along the top of the board, randomly positioned along the x-axis. If this new piece happens offer no valid move, then the game is over and the program exits.
