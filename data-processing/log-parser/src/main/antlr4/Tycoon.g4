grammar Tycoon;
import TycoonLex;

hands : hand+ ;

hand :  ( TRANSCRIPT ( YOURLAST numbers HANDS )? REQUEST identifier OPAREN EMAIL CPAREN )?
        ( HANDTYPE HASH numbers )? handheader tableheader ( seat | mandatorypost | event )+ ( phaseheader ( dealt | action | event )* )* summaryheader summary+ ;

handheader : 	PS ZOOM? HANDTYPE HASH numbers COLON // Hand Header
		( ZOOM? TOURNAMENT HASH numbers COMMA ( curr=FREEROLL | numbers curr=SC | tournamentprice curr=CURRENCYABBR ) )?  // Tornament info
		GAMETYPE // Game Type
		( DASH LVL romnum )? // Tournament Blinds Level
		( OPAREN CURRENCYSYM? numbers FSLASH CURRENCYSYM? numbers CURRENCYABBR? CPAREN )? //  Blinds Amount
		DASH DATE TIME TIMEZONE ( OBRKT DATE TIME TIMEZONE CBRKT )*; // Date and Time

tableheader : 	TABLE SQUOTE ( numbers | identifier ) ROMNUM? SQUOTE numbers DASH MAX SEAT HASH numbers ISBUTTON ; // identifier as TABLE NAME ID (for comodity)

seat : SEAT numbers COLON identifier OPAREN CURRENCYSYM? numbers INCHIPS CPAREN ( TIMEDOUT | SITTINGOUT | ( OUTOFHAND OPAREN ( MVINTOSB | MVINTOBB ) CPAREN ) )?;

event 	: identifier TIMEDOUT #Timedout
		| identifier ( OUTOFHAND OPAREN ( MVINTOSB | MVINTOBB ) CPAREN ) #Moved
		| identifier COLON? SITTINGOUT #Sittingout
		| identifier CONNECTED #Connected
		| identifier DCONNECTED #Disconnected
		| identifier TOUT #Tout
		| identifier TOUTDICONN #Toutdisconnected
		| identifier SOUT #Sitsout
		| UCBET OPAREN CURRENCYSYM? numbers CPAREN RETURNED identifier #Returned
		| identifier FINISHED ( IN ORDINALS PLACE )? ( AND RCV  CURRENCYSYM? numbers DOT )? #Finished
		| identifier COLLECTED CURRENCYSYM? numbers FROM ( POT | MAIN POT | ( SIDE POT ( DASH numbers )? ) ) #Collected
		| identifier LEAVES #Leaves
		| identifier HRETURNED #Hreturned
		| identifier WINS THE TOURNAMENT AND RCS CURRENCYSYM? numbers DASH CONGRATS #Wintournament
		| identifier WINS THE CURRENCYSYM? numbers BOUNTY identifier #Winbounty
		| identifier WINS CURRENCYSYM? numbers ELIMINATING identifier ( BOUNTYINCREASE CURRENCYSYM? numbers TO CURRENCYSYM? numbers )? #Wineliminating
		| identifier WINS TENTRY HASH numbers #Wintentry
		| identifier REBUYS CURRENCYSYM? numbers CHIPS FOR CURRENCYSYM? numbers SC? #Rebuys
		| identifier ADDON CURRENCYSYM? numbers CHIPS FOR CURRENCYSYM? numbers SC? #Addon
		| BREAKSKIP #Breakskip
		| identifier SAID #Chat
		| identifier JOINS HASH numbers #Joins
		| identifier WAITTOPLAY #Waiting
		| CANCELLED #Cancelled
		;

mandatorypost : identifier COLON ( POSTS BETORPOS CURRENCYSYM? numbers ( AND ISALLIN )? | SOUT ) ;

phaseheader : THREESTERISKS LONGORDINAL? PHASENAME THREESTERISKS ( OBRKT cards CBRKT )* ;

dealt : DEALTTO identifier OBRKT cards CBRKT ;

action 	: identifier COLON FOLDS ( OBRKT cards CBRKT )? # Fold
		| identifier COLON CHECKS # Check
		| identifier COLON CALLS CURRENCYSYM? numbers ( AND ISALLIN )? # Call
		| identifier COLON BETS CURRENCYSYM? numbers ( AND ISALLIN )? # Bet
		| identifier COLON RAISES CURRENCYSYM? numbers TO CURRENCYSYM? numbers ( AND ISALLIN )? # Raise
		| identifier COLON SHOWS OBRKT cards CBRKT ( OPAREN HANDRANK handdetail? CPAREN )? # Show
		| identifier COLON DSHOWS # DoesntShow
		| identifier COLON MUCKS # Mucks
		;

handdetail	: CARDVALUE kickers?
			| OF CARDVALUE kickers?
			| COMMA CARDVALUE AND CARDVALUE kickers?
			| COMMA CARDVALUE kickers?
			| COMMA CARDVALUE TO CARDVALUE kickers?
			| COMMA CARDVALUE HIGH ( DASH CARDVALUE HIGHER)? (DASH LCARDS )? kickers?
			| COMMA CARDVALUE FULL CARDVALUE kickers?
			;

kickers	: DASH CARDVALUE ( PLUS CARDVALUE )* KICKER
		| DASH LOWER KICKER
		;

summaryheader : THREESTERISKS SUMMARY THREESTERISKS ( summarytotalpot summarymainpot? summarysidepot* PIPE RAKE CURRENCYSYM? NUM+ TWICE? )? ( LONGORDINAL? BOARD OBRKT cards CBRKT )*
			  ;

summarytotalpot : TPOT CURRENCYSYM? numbers DOT?
                ;

summarymainpot : MPOT CURRENCYSYM? numbers DOT?
               ;

summarysidepot : SPOT ( DASH NUM+ )? CURRENCYSYM? numbers DOT?
               ;

summary 	    : SEAT numbers COLON identifier ( OPAREN BETORPOS CPAREN )* summaryaction ;

summaryaction 	: FOLDED ( OPAREN DBET CPAREN )?
				| SHOWED OBRKT cards CBRKT ( AND ( LOST | WON  OPAREN CURRENCYSYM? numbers CPAREN ) WITH HANDRANK handdetail? COMMA? )+
				| COLLECTED OPAREN CURRENCYSYM? numbers CPAREN
				| MUCKED ( OBRKT cards CBRKT )?
				;

tournamentprice : ( CURRENCYSYM? numbers PLUS? )+;

identifier : ID+;

numbers : NUM+;

romnum : ROMNUM+;

cards: CARD+;