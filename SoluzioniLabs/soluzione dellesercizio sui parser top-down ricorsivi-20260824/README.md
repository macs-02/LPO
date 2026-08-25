Completare la classe `Parser` implementando i metodi necessari per il parsing della seguente grammatica:

> `Prog ::= Exp EOF`
> `Exp ::= Mul (PLUS Mul)*`
> `Mul ::= Atom (TIMES Atom)*`
> `Atom ::= NUM | OPEN_PAR Exp CLOSE_PAR`

#### Richieste:

- **parseProg()** : Gestisce l'espressione principale seguita dalla fine del file (EOF), restituendo il rispettivo AST.
- **parseExp()** e **parseMul()** : Implementano il parsing per addizioni e moltiplicazioni, restituendo i rispettivi AST.
- **parseAtom()** : Gestisce le espressioni atomiche, restituendo il rispettivo AST.
- **parseNum()** e **parseRoundPar()** : Implementano il parsing di letterali numerici e di espressioni tra parentesi, restituendo i rispettivi AST.

L'implementazione deve utilizzare correttamente i metodi `consume()`, `match()` e `tokenizer.next()`.
La classe `Main` permette di leggere un programma da stdin o file di input e, in assenza di errori, di stampare l'AST prodotto su stdout o file di output.

_Nota: Assicurarsi che l'ordine di precedenza tra gli operatori e l'associatività siano coerenti con la grammatica fornita._
