lexer grammar TycoonLex;


// Phrases
ISBUTTON : 'is the button' ;
ISALLIN : 'is all-in' ;
INCHIPS : 'in chips' ;
TRANSCRIPT : 'Transcript for' ;
YOURLAST : 'your last' ;
REQUEST : 'requested by' ;
GAMETYPE : ( 'Hold\'em No Limit' | 'Omaha Pot Limit' ) ;
TIMEDOUT : 'has timed out while being disconnected' ;
SITTINGOUT : 'is sitting out' ;
CONNECTED : 'is connected' ;
DCONNECTED : 'is disconnected' ;
TOUT : 'has timed out' ;
TOUTDICONN : 'has timed out while disconnected' ;
SOUT : 'sits out' ;
UCBET : 'Uncalled bet' ;
RETURNED : 'returned to' ;
FINISHED : 'finished the tournament';
LEAVES : 'leaves the table' ;
HRETURNED : 'has returned' ;
BOUNTY : 'bounty for eliminating' ;
BOUNTYINCREASE : 'and their own bounty increases by' ;
ELIMINATING : 'for eliminating' ;
TENTRY : 'an entry to tournament' ;
REBUYS : 're-buys and receives' ;
ADDON : 'takes the add-on and receives' ;
BREAKSKIP : 'All players have agreed to skip the break. Game resuming.' ;
OUTOFHAND : 'out of hand' ;
MVINTOSB : 'moved from another table into small blind' ;
MVINTOBB : 'moved from another table into big blind' ;
JOINS : 'joins the table at seat' ;
WAITTOPLAY : 'will be allowed to play after the button' ;
DEALTTO : 'Dealt to' ;
DSHOWS : 'doesn\'t show hand' ;
MUCKS : 'mucks hand' ;
LCARDS : 'lower cards' ;
FULL : 'full of' ;
FOLDED : 'folded before Flop' | 'folded on the Flop' | 'folded on the Turn' | 'folded on the River' ;
DBET : 'didn\'t bet' ;
TPOT : 'Total pot' ;
MPOT : 'Main pot' ;
SPOT : 'Side pot' ;
BETORPOS : 'button' | 'small blind' | 'big blind' | 'small & big blinds' | 'the ante' ;
TWICE : 'Hand was run twice' ;
CANCELLED : 'Hand cancelled' ;



// Words
PS : 'PokerStars' ;
ZOOM : 'Zoom' ;
HANDTYPE : 'Hand' | 'Game' ;
TOURNAMENT : 'Tournament' | 'tournament' ;
FREEROLL : 'Freeroll' ;
SC : 'SC' | 'StarsCoin' ;
NONE : 'None' ;
LVL : 'Level' ;
SEAT : 'Seat' ;
TABLE : 'Table' ;
MAX : 'max' ;
HANDS : 'hands' ;
ORDINALS : ( '0'..'9' )+ ( 'st' | 'nd' | 'rd' | 'th' ) ;
LONGORDINAL : 'FIRST' | 'SECOND' ;
PLACE : 'place' ;
AND : 'and' ;
//AMPERSAND : '&' ;
RCV : 'received' ;
RCS : 'receives' ;
FROM : 'from' ;
COLLECTED : 'collected' ;
POT : 'pot' ;
MAIN : 'main' ;
SIDE : 'side' ;
WINS : 'wins' ;
THE : 'the' ;
TO : 'to' ;
IN : 'in' ;
OF : 'of' ;
CHIPS : 'chips' ;
FOR : 'for' ;
POSTS : 'posts' ;
CONGRATS : 'congratulations!' ;
FOLDS : 'folds' ;
CALLS : 'calls' ;
BETS : 'bets' ;
RAISES : 'raises' ;
SHOWS : 'shows' ;
HIGH : 'high' ;
HIGHER : 'higher' ;
KICKER : 'kicker' ;
LOWER : 'lower' ;
SHOWED : 'showed' ;
SUMMARY : 'SUMMARY' ;
RAKE : 'Rake' ;
BOARD : 'Board' ;
LOST : 'lost' ;
CHECKS : 'checks' ;
WON : 'won' ;
WITH : 'with' ;
MUCKED : 'mucked' ;

// Symbols
OPAREN : '(' ;
CPAREN : ')' ;
OBRKT : '[' ;
CBRKT : ']' ;
//NLINE : '\r'? '\n' ;
HASH : '#' ;
COLON : ':' ;
PLUS : '+' ;
DASH : '-' ;
FSLASH : '/' ;
SQUOTE : '\'' ;
DQUOTE : '"' ;
DOT : '.' ;
COMMA : ',' ;
PIPE : '|' ;
THREESTERISKS : '***' ;


// Values



//POSTTYPE : 'the ante' | 'small blind' | 'big blind' ;
PHASENAME : 'HOLE CARDS' | 'FLOP' | 'TURN' | 'RIVER' | 'SHOW DOWN' ;
CARD : ( '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | 'T' | 'J' | 'Q' | 'K' | 'A' )( 's' | 'd' | 'h' | 'c' );
HANDRANK :  'high card' | 'a pair' | 'two pair' | 'three of a kind' | 'a straight' | 'a flush' | 'a full house' | 'four of a kind' | 'a straight flush'  | 'a Royal Flush' ;
CARDVALUE : ('Ace' | 'Deuce' | 'Three' | 'Four' | 'Five' | 'Six' | 'Seven' | 'Eight' | 'Nine' | 'Ten' | 'Jack' | 'Queen' | 'King') ('s' | 'es')? ;

fragment YEAR : ('19' ('0'..'9')('0'..'9')) | ('20' ('0'..'9')('0'..'9'));
fragment MONTH : ('0' ('0'..'9')) | ('1'('0'..'2'));
fragment DAY : ('0' ('0'..'9')) | (('1'..'3')('0'..'9'));
fragment MIN_SEC : ('0' ('0'..'9')) | (('1'..'5')('0'..'9'));
fragment HOUR : ('0'..'9') | ('1' ('0'..'9')) | ('2' ('0'..'3'));
fragment IDCHAR	: 'A'..'Z' | 'a'..'z'
					| ( '0'..'9' )
					| '_'
					| '\u00C0'..'\u00D6'
					| '\u00D8'..'\u00F6'
					| '\u00F8'..'\u02FF'
					| '\u0370'..'\u037D'
					| '\u037F'..'\u1FFF'
					| '\u200C'..'\u200D'
					;

DATE : YEAR '/' MONTH '/' DAY;
DATE_TIME : DATE TIME TIMEZONE;
TIME : HOUR ':' MIN_SEC ':' MIN_SEC;
TIMEZONE : 'ET' | 'CET';
/************************************************************/
CURRENCYABBR : 'USD' | 'EUR';
CURRENCYSYM : '$' | '€';
NUM : ( '0'..'9' )+ ( '.' ( '0'..'9' )+ )?;
ROMNUM : ( 'I' | 'V' | 'X' | 'L' | 'D' | 'C' | 'M' )+;

/************************************************************/
//TABLEID : ( ('A'..'Z') | ('a'..'z') | ('0'..'9') )+;
EMAIL : ( ('A'..'Z') | ('a'..'z') | ('0'..'9') | '_' )+ '@' ( ('A'..'Z') | ('a'..'z') | ('0'..'9') )+ '.' ( ('A'..'Z') | ('a'..'z') | ('0'..'9') )+ ;

WS :  ( ' ' | '\t' | '\r' | '\n') -> skip;
SAID : 'said, "' .*? '"' ;
ID :	IDCHAR+ ;