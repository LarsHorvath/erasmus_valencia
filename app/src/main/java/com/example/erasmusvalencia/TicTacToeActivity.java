package com.example.erasmusvalencia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class TicTacToeActivity extends BaseThemeChangerActivity {


    static final String TAG ="TicTacToe";
    ArrayList<ImageButton> fields;
    Button playAgainButton;
    TextView winnerText, turnText;
    Event event;
    int event_id;
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
        getData();
        setData();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favouriteIcon:
                if (event.isFavourite()) {
                    event.setFavourite(false);
                    item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                    Toast.makeText(this,"removed from favourites", Toast.LENGTH_SHORT).show();
                } else {
                    event.setFavourite(true);
                    item.setIcon(R.drawable.ic_favorite_black_24dp);
                    Toast.makeText(this,"Event added to favourites", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.shareIcon:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String toSend;
                if (event.getUrl() != null) toSend = String.format(Locale.ENGLISH,"Hi, I would like to share the following event with you:\n\n*%s*\n%s _%s_\n%s %s\n%s\n\n Are you in?",event.getTitle(), getString(R.string.emoji_clock), Event.dayToString(event.getStartDate(), Event.DAY_AND_TIME), getString(R.string.emoji_location), event.getLocation(),event.getUrl());
                else toSend = String.format(Locale.ENGLISH,"Hi, I would like to share the following event with you:\n\n*%s*\n%s _%s_\n%s %s\n\n Are you in?",event.getTitle(), getString(R.string.emoji_clock), Event.dayToString(event.getStartDate(), Event.DAY_AND_TIME), getString(R.string.emoji_location), event.getLocation());
                sendIntent.putExtra(Intent.EXTRA_TEXT, toSend);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, null));
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem settingsItem = menu.findItem(R.id.favouriteIcon);
        // set your desired icon here based on a flag if you like
        if (event != null) {
            if (event.isFavourite()) settingsItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_black_24dp));
            else settingsItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black_24dp));
        }
        else {
            Log.i(TAG, "onPrepareOptionsMenu: event isn't defined yet wjasdfjkasdf jkö asdfjlöksdaf");
        }


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_details_menu, menu);
        MenuItem fav_item = findViewById(R.id.favouriteIcon);
        if (fav_item == null) return true;
        if (event.isFavourite()) {
            fav_item.setIcon(R.drawable.ic_favorite_black_24dp);
        } else {
            fav_item.setIcon(R.drawable.ic_favorite_border_black_24dp);
        }
        return true;
    }

    private void getData() {
        if(getIntent().hasExtra("event_id")) {
            event_id = getIntent().getIntExtra("event_id", 0);
        }
        else {
            Toast.makeText(this,"no data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData() {
        event = Event.allEvents.get(event_id);
    }


}
