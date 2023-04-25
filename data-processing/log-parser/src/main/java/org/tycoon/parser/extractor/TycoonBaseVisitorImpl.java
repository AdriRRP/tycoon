package org.tycoon.parser.extractor;

import org.tycoon.parser.antlr.TycoonBaseVisitor;
import org.tycoon.parser.antlr.TycoonParser;
import org.tycoon.parser.catalog.Action;
import org.tycoon.parser.catalog.Hand;
import org.tycoon.parser.catalog.Phase;
import org.tycoon.parser.catalog.Seat;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.spark.sql.execution.columnar.NULL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by adri on 17/07/17.
 */
public class TycoonBaseVisitorImpl extends TycoonBaseVisitor<Void> {

    // Attributes where data is stored
    private ArrayList<Hand> result = null;
    private Hand hand = null;
    private Seat seat = null;
    private Phase phase = null;
    private Action action = null;

    // Returns collected hands info
    public ArrayList<Hand> get() {
        return result;
    }

    @Override
    public Void visitHands(TycoonParser.HandsContext ctx) {
        result = new ArrayList<Hand>(); // Creates new hand array
        visitChildren(ctx); // Visit childrens
        return null;
    }

    @Override
    public Void visitHand(TycoonParser.HandContext ctx) {
        hand = new Hand(); // Creates a new hand
        visitChildren(ctx); // Visit hand's children nodes
        if (hand != null) result.add(hand); // Add not null hands to results
        return null;
    }

    @Override
    public Void visitHandheader(TycoonParser.HandheaderContext ctx) {

        try {
            // Setting hand id: first appear of numbers non-terminal symbol
            hand.setId(ctx.numbers(0).getText());

            // Setting game type: GAMETYPE terminal symbol
            hand.setGameType(ctx.GAMETYPE().getText());

            // If we are on a tournament
            if (ctx.curr != null) {
                // Setting currency: contained in curr TAG
                hand.setCurrency(ctx.curr.getText());
                // Setting tournament id: seacond appear of numbers non-terminal symbol
                hand.setTournamentId(ctx.numbers(1).getText());
                // Setting entry price:
                Float entryPrice = 0f;
                for (TycoonParser.NumbersContext num : ctx.tournamentprice().numbers())
                    entryPrice += Float.parseFloat(num.getText());
                hand.setEntryPrice(entryPrice);
                // Setting tourney level:
                hand.setLevel(romanToDecimal(ctx.romnum().getText()));
            } else {
                // Setting currency: first appear of CURRABBR
                // If CURRABBR is empty, currency=null
                if (ctx.CURRENCYABBR().size() > 0)
                    hand.setCurrency(ctx.CURRENCYABBR().get(0).getText());
                else if (ctx.CURRENCYSYM().size() > 0) {
                    if (ctx.CURRENCYSYM().get(0).getText().equals("$"))
                        hand.setCurrency("USD");
                    else if (ctx.CURRENCYSYM().get(0).getText().equals("€"))
                        hand.setCurrency("EUR");
                }
            }

            // Setting big blind and small blind size: depends on numbers non-terminal symbol appear
            switch (ctx.numbers().size()) {
                case 3:
                    hand.setSmallBlind(Float.parseFloat(ctx.numbers(1).getText()));
                    hand.setBigBlind(Float.parseFloat(ctx.numbers(2).getText()));
                    break;
                case 4:
                    hand.setSmallBlind(Float.parseFloat(ctx.numbers(2).getText()));
                    hand.setBigBlind(Float.parseFloat(ctx.numbers(3).getText()));
                    break;
                case 5:
                    hand.setSmallBlind(Float.parseFloat(ctx.numbers(3).getText()));
                    hand.setBigBlind(Float.parseFloat(ctx.numbers(4).getText()));
                    break;
                default:
                    break;
            }

            // Setting ante:
            hand.setAnte(0f);

            // Setting date:
            try {
                hand.setDate(new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(ctx.DATE().get(0).getText() + " " + ctx.TIME().get(0).getText()));
            } catch (java.text.ParseException e) {
                hand.setDate(null);
            }

            // Setting time zone:
            hand.setTimeZone(ctx.TIMEZONE().get(0).getText());

            visitChildren(ctx);
        } catch (Exception e) {
            //System.out.println("Error parsing hand : " + e.getMessage());
        }

        return null;
    }

    @Override
    public Void visitTableheader(TycoonParser.TableheaderContext ctx) {
        try {
            // Setting table id:
            StringBuffer tableId = new StringBuffer();

            for (TerminalNode elem : (ctx.identifier() != null ? ctx.identifier().ID() : ctx.numbers(0).NUM()))
                tableId.append(tableId.length() > 0 ? " " : "").append(elem.getText());

            if (ctx.ROMNUM() != null)
                tableId.append(" ").append(ctx.ROMNUM().getText());

            hand.setTableId(tableId.toString());

            // Setting table max players:
            hand.setTableMaxPlayers(Integer.parseInt(ctx.numbers(
                    ctx.numbers().size() == 2 ? 0 : 1 // If we have 2 numbers, take 0 index, else take 1 index
            ).getText()));

            // Setting table max players:
            hand.setDealerSeatNum(Integer.parseInt(ctx.numbers(
                    ctx.numbers().size() == 2 ? 1 : 2 // If we have 2 numbers, take 1 index, else take 2 index
            ).getText()));
        } catch (Exception e) {
            //System.out.println("Error parsing hand : " + e.getMessage());
        }

        visitChildren(ctx);

        return null;
    }

    @Override
    public Void visitSeat(TycoonParser.SeatContext ctx) {
        try {
            seat = new Seat(
                    Integer.parseInt(ctx.numbers(0).getText()),
                    ctx.identifier().getText(),
                    Float.parseFloat(ctx.numbers(1).getText()),
                    Float.parseFloat(ctx.numbers(1).getText()),
                    ""
            );
            hand.addSeat(seat);
        } catch (Exception e) {
            //System.out.println("Error parsing hand : " + e.getMessage());
        }

        visitChildren(ctx);

        return null;
    }

    @Override
    public Void visitMandatorypost(TycoonParser.MandatorypostContext ctx) {
        try {
            // Subtracts mandatory posts from each player's final stack
            Integer seatIndex = getSeatIndex(ctx.identifier().getText());

            hand.getSeatAt(seatIndex).setFinalStack(
                    hand.getSeatAt(seatIndex).getFinalStack() - Float.parseFloat(ctx.numbers().getText())
            );

            if (ctx.BETORPOS().getText().equals("button"))
                hand.setDealerIndex(getSeatIndex(ctx.identifier().getText()));
            if (ctx.BETORPOS().getText().equals("the ante"))
                hand.setAnte(Float.parseFloat(ctx.numbers().getText()));

        } catch (Exception e) {
            //System.out.println("Error parsing hand : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitPhaseheader(TycoonParser.PhaseheaderContext ctx) {
        try {

            Integer phaseId = null;

            switch (ctx.PHASENAME().getText()) {
                case "HOLE CARDS":
                    phaseId = Phase.HOLECARDS;
                    break;
                case "FLOP":
                    phaseId = Phase.FLOP;
                    break;
                case "TURN":
                    phaseId = Phase.TURN;
                    break;
                case "RIVER":
                    phaseId = Phase.RIVER;
                    break;
                case "SHOW DOWN":
                    phaseId = Phase.SHOWDOWN;
                    break;
            }

            String phaseCards = null;

            if (ctx.cards() != null)
                for (int i = 0; i < ctx.cards().size(); i++)
                    phaseCards = phaseCards != null ? phaseCards.concat(ctx.cards(i).getText()) : ctx.cards(i).getText();

            // If PhaseId is the showndown, put the same cards that river
            if (phaseId == Phase.SHOWDOWN)
                for (Phase phase : hand.getPhases())
                    if (phase.getId() == Phase.RIVER)
                        phaseCards = phase.getCards();

            Float pot = 0f;
            // If exists previous phases
            if (hand.getPhases().size() > 0)
                // If last phase has actions
                if (hand.getPhases().get(hand.getPhases().size() - 1).getActions().size() > 0) {
                    // pot is equals to last action pot
                    pot = hand.getPhases()
                            .get(hand.getPhases().size() - 1)
                            .getActions()
                            .get(
                                    hand.getPhases()
                                            .get(hand.getPhases().size() - 1)
                                            .getActions()
                                            .size() - 1
                            ).getPot();
                    // If last phase has no actions
                } else {
                    // pot is equals to last phase pot
                    pot = hand.getPhases()
                            .get(hand.getPhases().size() - 1)
                            .getPot();
                    // If not exists previous phases
                }
            else {
                // pot is equal to addition of difference between initial and final stack of each player
                for (int i = 0; i < hand.getSeats().size(); i++) {
                    pot += hand.getSeatAt(i).getInitialStack() - hand.getSeatAt(i).getFinalStack();
                }
            }

            phase = new Phase(
                    phaseId,
                    phaseCards,
                    pot,
                    new ArrayList<Action>()
            );
            hand.addPhase(phase);
        } catch (Exception e) {
            //System.out.println("Error parsing hand : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitDealt(TycoonParser.DealtContext ctx) {
        try {
            for (int i = 0; i < hand.getSeats().size(); i++)
                if (hand.getSeats().get(i).getIdPlayer().equals(ctx.identifier().getText()))
                    hand.getSeats().get(i).setCards(ctx.cards().getText());
        } catch (Exception e) {
            //System.out.println("Error parsing hand : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitHanddetail(TycoonParser.HanddetailContext ctx) {
        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitSummaryheader(TycoonParser.SummaryheaderContext ctx) {
        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitSummary(TycoonParser.SummaryContext ctx) {
        try {
            if( ctx.summaryaction().MUCKED() != null && ctx.summaryaction().cards() != null)
                for( Seat s : hand.getSeats() )
                    if( s.getIdPlayer().equals(ctx.identifier().getText()) )
                        s.setCards(ctx.summaryaction().cards().getText());
        } catch (Exception e ) {

        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitSummaryaction(TycoonParser.SummaryactionContext ctx) {



        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitShow(TycoonParser.ShowContext ctx) {
        try {
            for (int i = 0; i < hand.getSeats().size(); i++)
                if (hand.getSeats().get(i).getIdPlayer().equals(ctx.identifier().getText()))
                    hand.getSeats().get(i).setCards(ctx.cards().getText());
        } catch (Exception e) {
            //System.out.println("Error parsing hand : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitFold(TycoonParser.FoldContext ctx) {
        try {
            int seatIndex = getSeatIndex(ctx.identifier().getText());
            action = new Action(
                    phase.getActions().size(),
                    seatIndex,
                    Action.FOLD,
                    0f,
                    0f,
                    false,
                    calculatePot()
            );
            phase.addAction(action);
        } catch (Exception e) {
            //System.out.println("Error parsing hand : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitCheck(TycoonParser.CheckContext ctx) {
        try {
            int seatIndex = getSeatIndex(ctx.identifier().getText());
            action = new Action(
                    phase.getActions().size(),
                    seatIndex,
                    Action.CHECK,
                    0f,
                    0f,
                    false,
                    calculatePot()
            );

            action.setAllin(hand.getSeatAt(seatIndex).getFinalStack() == 0);

            phase.addAction(action);
        } catch (Exception e) {
            //System.out.println("Error parsing hand : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitCall(TycoonParser.CallContext ctx) {
        try {

            int seatIndex = getSeatIndex(ctx.identifier().getText());
            action = new Action(
                    phase.getActions().size(),
                    seatIndex,
                    Action.CALL,
                    Float.parseFloat(ctx.numbers().getText()),
                    Float.parseFloat(ctx.numbers().getText()),
                    false,
                    calculatePot() + Float.parseFloat(ctx.numbers().getText())
            );

            hand.getSeatAt(seatIndex).setFinalStack(
                    hand.getSeatAt(seatIndex).getFinalStack() - Float.parseFloat(ctx.numbers().getText())
            );

            action.setAllin(hand.getSeatAt(seatIndex).getFinalStack() == 0);

            phase.addAction(action);

        } catch (Exception e) {
            //System.out.println("Error parsing hand : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitBet(TycoonParser.BetContext ctx) {
        try {
            int seatIndex = getSeatIndex(ctx.identifier().getText());
            action = new Action(
                    phase.getActions().size(),
                    seatIndex,
                    Action.BET,
                    Float.parseFloat(ctx.numbers().getText()),
                    Float.parseFloat(ctx.numbers().getText()),
                    false,
                    calculatePot() + Float.parseFloat(ctx.numbers().getText())
            );

            hand.getSeatAt(seatIndex).setFinalStack(
                    hand.getSeatAt(seatIndex).getFinalStack() - Float.parseFloat(ctx.numbers().getText())
            );

            action.setAllin(hand.getSeatAt(seatIndex).getFinalStack() == 0);

            phase.addAction(action);

        } catch (Exception e) {
            //System.out.println("Error parsing hand : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitRaise(TycoonParser.RaiseContext ctx) {
        try {
            int seatIndex = getSeatIndex(ctx.identifier().getText());
            action = new Action(
                    phase.getActions().size(),
                    seatIndex,
                    Action.RAISE,
                    Float.parseFloat(ctx.numbers(0).getText()),
                    Float.parseFloat(ctx.numbers(1).getText()),
                    false,
                    calculatePot() + Float.parseFloat(ctx.numbers(1).getText())
            );

            hand.getSeatAt(seatIndex).setFinalStack(
                    hand.getSeatAt(seatIndex).getFinalStack() - Float.parseFloat(ctx.numbers(1).getText())
            );

            action.setAllin(hand.getSeatAt(seatIndex).getFinalStack() == 0);

            phase.addAction(action);

        } catch (Exception e) {
            //System.out.println("Error parsing hand : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitReturned(TycoonParser.ReturnedContext ctx) {
        try {
            hand.getSeatAt(getSeatIndex(ctx.identifier().getText())).setFinalStack(
                    hand.getSeatAt(getSeatIndex(ctx.identifier().getText())).getFinalStack() +
                            Float.parseFloat(ctx.numbers().getText())
            );
        } catch (Exception e) {
            //System.out.println("Exception accessing element : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitCollected(TycoonParser.CollectedContext ctx) {
        try {
            hand.getSeatAt(getSeatIndex(ctx.identifier().getText())).setFinalStack(
                    hand.getSeatAt(getSeatIndex(ctx.identifier().getText())).getFinalStack() +
                            Float.parseFloat(ctx.numbers(0).getText())
            );
        } catch (Exception e) {
            //System.out.println("Exception accessing element : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitRebuys(TycoonParser.RebuysContext ctx) {
        try {
            hand.getSeatAt(getSeatIndex(ctx.identifier().getText())).setFinalStack(
                    hand.getSeatAt(getSeatIndex(ctx.identifier().getText())).getFinalStack() +
                            Float.parseFloat(ctx.numbers(0).getText())
            );
        } catch (Exception e) {
            //System.out.println("Exception accessing element : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitAddon(TycoonParser.AddonContext ctx) {
        try {
            hand.getSeatAt(getSeatIndex(ctx.identifier().getText())).setFinalStack(
                    hand.getSeatAt(getSeatIndex(ctx.identifier().getText())).getFinalStack() +
                            Float.parseFloat(ctx.numbers(0).getText())
            );
        } catch (Exception e) {
            //System.out.println("Exception accessing element : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitFinished(TycoonParser.FinishedContext ctx) {
        try {
            hand.getSeatAt(getSeatIndex(ctx.identifier().getText())).setItm(
                    ctx.RCV() != null
            );
        } catch (Exception e) {
            //System.out.println("Exception accessing element : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitWintournament(TycoonParser.WintournamentContext ctx) {
        try {
            hand.getSeatAt(getSeatIndex(ctx.identifier().getText())).setWin(true);
        } catch (Exception e) {
            //System.out.println("Exception accessing element : " + e.getMessage());
        }

        visitChildren(ctx);
        return null;
    }

    private Integer romanToDecimal(String roman) {
        Integer decimal = 0;
        Integer lastNumber = 0;

        String romanNumeral = roman.toUpperCase();
        for (int x = romanNumeral.length() - 1; x >= 0; x--) {
            char convertToDecimal = romanNumeral.charAt(x);

            switch (convertToDecimal) {
                case 'M':
                    decimal = processDecimal(1000, lastNumber, decimal);
                    lastNumber = 1000;
                    break;
                case 'D':
                    decimal = processDecimal(500, lastNumber, decimal);
                    lastNumber = 500;
                    break;
                case 'C':
                    decimal = processDecimal(100, lastNumber, decimal);
                    lastNumber = 100;
                    break;
                case 'L':
                    decimal = processDecimal(50, lastNumber, decimal);
                    lastNumber = 50;
                    break;
                case 'X':
                    decimal = processDecimal(10, lastNumber, decimal);
                    lastNumber = 10;
                    break;
                case 'V':
                    decimal = processDecimal(5, lastNumber, decimal);
                    lastNumber = 5;
                    break;
                case 'I':
                    decimal = processDecimal(1, lastNumber, decimal);
                    lastNumber = 1;
                    break;
            }
        }

        return decimal;
    }

    private Integer processDecimal(Integer decimal, Integer lastNumber, Integer lastDecimal) {
        return lastNumber > decimal ? lastDecimal - decimal : lastDecimal + decimal;
    }

    private Integer getSeatIndex(String playerId) {
        for (int i = 0; i < hand.getSeats().size(); i++)
            if (hand.getSeatAt(i).getIdPlayer().equals(playerId))
                return i;
        return -1;
    }

    private Float calculatePot() {
        Float pot = 0f;
        // If exists previous phases
        if (hand.getPhases().size() > 0)
            // If last phase has actions
            if (hand.getPhases().get(hand.getPhases().size() - 1).getActions().size() > 0) {
                // pot is equals to last action pot
                pot = hand.getPhases()
                        .get(hand.getPhases().size() - 1)
                        .getActions()
                        .get(
                                hand.getPhases()
                                        .get(hand.getPhases().size() - 1)
                                        .getActions()
                                        .size() - 1
                        ).getPot();
                // If last phase has no actions
            } else {
                // pot is equals to last phase pot
                pot = hand.getPhases()
                        .get(hand.getPhases().size() - 1)
                        .getPot();
                // If not exists previous phases
            }
        else {
            // pot is equal to addition of difference between initial and final stack of each player
            for (int i = 0; i < hand.getSeats().size(); i++) {
                pot += hand.getSeatAt(i).getInitialStack() - hand.getSeatAt(i).getFinalStack();
            }
        }
        return pot;
    }


}
