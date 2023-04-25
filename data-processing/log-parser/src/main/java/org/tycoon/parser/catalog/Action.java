package org.tycoon.parser.catalog;

import java.util.Objects;

public class Action {
    // Class Constants

    public static final int BIG_BLIND = -2;
    public static final int SMALL_BLIND = -1;
    public static final int ANTE = 0;
    public static final int FOLD = 1;
    public static final int CHECK = 2;
    public static final int CALL = 3;
    public static final int BET = 4;
    public static final int RAISE = 5;

    private Integer _seq_num; // Action's squence number in the phase
    private Integer _seat_idx; // Index of Seat who do the action
    private Integer _type; // Which action is done
    private Float _amount; // If the action requires put chips, the total amount of those chips.
    private Float _bet_size; // If the action is bet or raise, the amount this action adds to pot.
    private Boolean _allin; // Is player allin?
    private Float _pot; // The pot size before the action

    public Action() {
        _seq_num = 0;
        _seat_idx = 0;
        _type = null;
        _amount = 0f;
        _bet_size = 0f;
        _allin = false;
        _pot = 0f;
    }

    public Action(Integer _seq_num, Integer _seat_idx, Integer _type, Float _amount, Float _bet_size, Boolean _allin, Float _pot) {
        this._seq_num = _seq_num;
        this._seat_idx = _seat_idx;
        this._type = _type;
        this._amount = _amount;
        this._bet_size = _bet_size;
        this._allin = _allin;
        this._pot = _pot;
    }

    // Getters
    public Integer getSeqNum() {
        return _seq_num;
    }

    public Integer getSeatIdx() {
        return _seat_idx;
    }

    public Integer getType() {
        return _type;
    }

    public Float getAmount() {
        return _amount;
    }

    public Float getBetSize() {
        return _bet_size;
    }

    public Boolean isAllin() {
        return _allin;
    }

    public Float getPot() {
        return _pot;
    }

    // Setters
    public void setSeqNum(Integer seq_num) {
        _seq_num = seq_num;
    }

    public void setSeatIdx(Integer seat_idx) {
        _seat_idx = seat_idx;
    }

    public void setType(Integer type) {
        _type = type;
    }

    public void setAmount(Float amount) {
        _amount = amount;
    }

    public void setBetSize(Float bet_size) {
        _bet_size = bet_size;
    }

    public void setAllin(Boolean allin) {
        _allin = allin;
    }

    public void setPot(Float pot) {
        _pot = pot;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Action) {
            Action that = (Action) o;
            return Objects.equals(this._seq_num, that._seq_num) &&
                    Objects.equals(this._seat_idx, that._seat_idx) &&
                    Objects.equals(this._type, that._type) &&
                    Objects.equals(this._amount, that._amount) &&
                    Objects.equals(this._bet_size, that._bet_size) &&
                    this._allin == that._allin &&
                    Objects.equals(this._pot, that._pot);
        }
        return false;
    }

    // toString
    @Override
    public String toString() {
        return "Action {\n" +
                "SEQ_NUM:\t\t" + getSeqNum() + "\n" +
                "SEAT_IDX:\t\t" + getSeatIdx() + "\n" +
                "TYPE:\t\t\t" + getType() + "\n" +
                "AMOUNT:\t\t\t" + getAmount() + "\n" +
                "BET_SIZE:\t\t" + getBetSize() + "\n" +
                "ALLIN:\t\t\t" + isAllin() + "\n" +
                "POT:\t\t\t" + getPot() + "\n" +
                "}\n";
    }
}