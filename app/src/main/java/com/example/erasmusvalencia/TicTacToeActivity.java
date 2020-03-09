package com.example.erasmusvalencia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class TicTacToeActivity extends AppCompatActivity {


    static final String TAG ="TicTacToe";
    ArrayList<ImageButton> fields;
    Button playAgainButton;
    TextView winnerText, turnText;
    final static int BLANK = 0;
    final static int XX = 1;
    final static int OO = -1;
    int imResIds[] = {R.drawable.blank_60dp, R.drawable.ic_close_black_60dp, R.drawable.ic_radio_button_unchecked_black_60dp};
    int occupation[];
    int turn;
    int fieldsLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);
        winnerText = findViewById(R.id.winnerText);
        turnText = findViewById(R.id.turnText);
        playAgainButton = findViewById(R.id.newGameButton);
        int ids[][] = { {R.id.field00, R.id.field01, R.id.field02},
                        {R.id.field10, R.id.field11, R.id.field12},
                        {R.id.field20, R.id.field21, R.id.field22},};
        fields = new ArrayList<>();
        for (int pos = 0; pos < 9; pos++) {
            fields.add((ImageButton) findViewById(ids[row(pos)][col(pos)]));
            fields.get(pos).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleClick(fields.indexOf(view));
                }
            });
        }
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initGame();
            }
        });
        this.initGame();
    }

    private static int row(int position) {
        return position/3;
    }
    private static int col(int position) {
        return position % 3;
    }
    private static int position(int row, int column) {
        return 3*row + column;
    }
    private void handleClick(int pos) {
        if (occupation[pos] != BLANK || turn == BLANK) {
            return;
        }
        fields.get(pos).setImageResource(imResIds[(turn + 3) % 3]);
        occupation[pos] = turn;
        --fieldsLeft;
        endTurn();
    }

    private void endTurn() {
        int winner = checkWinner();
        if (winner == BLANK && fieldsLeft > 0) {
            turn *= -1;
            String turnNotice;
            switch (turn) {
                case XX: turnNotice = "It's X's turn"; break;
                case OO: turnNotice = "It's O's turn"; break;
                default: turnNotice = ""; break;
            }
            turnText.setText(turnNotice);
        }
        else {
            endGame(winner);
        }
    }

    private void endGame(int winner) {
        String winnerNotice;
        switch (winner) {
            case BLANK: winnerNotice = "This game ended in a draw."; break;
            case XX: winnerNotice = "The X's won this game"; break;
            case OO: winnerNotice = "This game was won by the O's"; break;
            default: winnerNotice = "You really are an idiot"; break;
        }
        turnText.setVisibility(View.INVISIBLE);
        winnerText.setText(winnerNotice);
        winnerText.setVisibility(View.VISIBLE);
        playAgainButton.setVisibility(View.VISIBLE);
        turn = BLANK;
    }

    private int checkWinner() {
        int dw = checkDiagonals();
        int rw = checkRows();
        int cw = checkColumns();

        return dw != BLANK ? dw : rw != BLANK ? rw : cw;
    }

    private int checkDiagonals() {
        int sum1 = 0;
        int sum2 = 0;
        for (int i = 0; i < 3; i++) {
            sum1 += occupation[position(i,i)];
            sum2 += occupation[position(2-i,i)];
        }
        Log.i(TAG, "checkDiagonals: sum1 " + sum1 + " sum2 " + sum2);
        if (sum1 == 3*XX || sum2 == 3*XX) return XX;
        else if (sum1 == 3*OO || sum2 == 3*OO) return OO;
        else return BLANK;
    }

    private int checkRows() {
        for (int i = 0; i < 3; i++) {
            int sum = 0;
            for (int j = 0; j < 3; j++) {
                sum += occupation[position(i, j)];
            }
            Log.i(TAG, "checkRows: row " + i + " sum " + sum);
            if (sum == 3*XX) return XX;
            else if (sum == 3*OO) return OO;
        }
        return BLANK;
    }

    private int checkColumns() {
        for (int i = 0; i < 3; i++) {
            int sum = 0;
            for (int j = 0; j < 3; j++) {
                sum += occupation[position(j, i)];
            }
            Log.i(TAG, "checkColumns: col " + i + " sum " + sum);
            if (sum == 3*XX) return XX;
            else if (sum == 3*OO) return OO;
        }
        return BLANK;
    }

    private void initGame() {
        fieldsLeft = 9;
        turn = XX;
        occupation = new int[9];
        turnText.setText("It's X's turn");
        turnText.setVisibility(View.VISIBLE);
        winnerText.setVisibility(View.INVISIBLE);
        playAgainButton.setVisibility(View.INVISIBLE);
        for (ImageButton field : fields) {
            field.setImageResource(imResIds[BLANK]);
        }
    }

}
