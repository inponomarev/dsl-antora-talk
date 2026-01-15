gcd(A,B,G) :- A1 is abs(A), B1 is abs(B), gcd0(A1,B1,G).
gcd0(A,0,A). % <.>
gcd0(A,B,G) :- B \= 0, R is A mod B, gcd0(B,R,G). % <.>
