# Laboratorio di LP, 17 marzo 2026: gestione dell'I/O e delle eccezioni

## Esercizio proposto

### Obiettivo
Implementare la classe `projectLabo.Main` che tramite un oggetto `Tokenizer` permette l'analisi lessicale dell'input e la visualizzazione del corrispondente risultato sull'output.

---

### 1. Funzionamento del Tokenizer
Il sorgente di tutte le classi accessorie è disponibile nel folder `projectLabo/parser`.
Un oggetto `Tokenizer` viene inizializzato con un  `BufferedReader` che rappresenta l'input da analizzare. 
Il tokenizer è in grado di identificare i componenti lessicali dell'input (token), le cui categorie sintattiche sono definite da espressioni regolari. 
La classe `Tokenizer` implementa l'interfaccia `TokenizerInterface` con i seguenti metodi:

* `TokenType next() throws TokenizerException`: Riconosce il prossimo token e ne restituisce il tipo (`TokenType`). Ignora automaticamente spazi e commenti e restituisce un'eccezione `TokenizerException` se il token non viene correttamente riconosciuto o se si verifica un errore di I/O.

  **Fine Input:** Quando l'input termina, il metodo restituisce `EOF`.
Successive chiamate a `next()` non hanno alcun effetto.

* **Accesso ai dati:** Dopo una chiamata a `next()`, i dettagli del token corrente sono accessibili tramite:
    * `String tokenString()`: La stringa del token riconosciuto.
    * `int intValue()`: Il valore intero del token riconosciuto (solo se il suo tipo è `NUM`).
    * `boolean boolValue()`: Il valore booleano del token riconosciuto (solo se il suo tipo è `BOOL`).
    * `TokenType tokenType()`: Il tipo del token riconosciuto.

  **Nota bene:** Questi metodi sollevano l'eccezione `IllegalStateException` se vengono chiamati quando nessun token è stato riconosciuto o se il tipo del token non è quello atteso, nel caso di `intValue()` e `boolValue()`.     

---

### 2. Specifica della classe Main
Oltre all'analisi dell'input mediante l'uso di un tokenizer, la classe `Main` gestisce le opzioni passate da linea di comando, l'I/O e le possibili eccezioni. 

Il metodo `main` deve essere interamente completato, mentre il codice ausiliario alla gestione delle opzioni e dell'I/O è già presente, 

#### Gestione degli argomenti da linea di comando

* **Input (`-i <path>`):** Se specificato, il programma legge dal file indicato dal percorso `<path>`. In assenza del flag, il programma legge dallo **standard input**.
* **Output (`-o <path>`):** Se specificato, il programma scrive i risultati nel file indicato dal percorso `<path>`. In assenza del flag, il programma scrive sullo **standard output**.

**Regole di validazione:**
1. Se viene passata un'opzione non riconosciuta, il programma deve stampare un messaggio di errore e terminare.
2. Se un'opzione richiede un argomento (come `-i` o `-o`) ma questo non viene fornito, il programma deve segnalare l'errore e terminare.

#### Logica di stampa
Per ogni token identificato (incluso `EOF`), il programma deve stampare una riga formattata esattamente come segue:
`string: <lessema>    type: <tipo>    number: <valore intero>    boolean: <valore booleano>`

* Se `<lessema>`, `<valore intero>` o `<valore booleano>` non sono definiti, allora deve essere stampato `null`.
* L'iterazione termina non appena viene riconosciuto il token `EOF`.

#### Gestione delle eccezioni (stderr)
Eventuali eccezioni devono essere intercettate e i relativi messaggi stampati sullo **standard error** (`stderr`):
* **IOException:** Stampare `"I/O error: "` seguito dal messaggio dell'eccezione.
* **TokenizerException:** Stampare `"Lexical error "` seguito dal messaggio dell'eccezione.
* **Altre eccezioni:** Stampare `"Unexpected error."` seguito dallo stack trace.

---

### Esempio di esecuzione
Eseguendo il programma con l'input `var x = true;`, l'output deve essere:
```text
string: var 	 type: VAR 	 number: null 	 boolean: null
string: x 	 type: IDENT 	 number: null 	 boolean: null
string: = 	 type: ASSIGN 	 number: null 	 boolean: null
string: true 	 type: BOOL 	 number: null 	 boolean: true
string: ; 	 type: STMT_SEP 	 number: null 	 boolean: null
string: null 	 type: EOF 	 number: null 	 boolean: null

Un esempio più complesso `lab07.txt` è presente nel repository, insieme al corrispondente output atteso `lab07.out`.