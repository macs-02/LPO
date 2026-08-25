# Progetto Finale LPO a.a. 2025-2026 - Consegna

## Descrizione

Interprete Java per il linguaggio esteso con operazioni su vettori, implementato secondo le specifiche del progetto finale LPO.

### Funzionalità implementate

- **Parser ricorsivo discendente** con 1 token di lookahead
- **Semantica statica** (type checker) con visitor pattern
- **Semantica dinamica** (esecutore) con visitor pattern
- **Operazioni su vettori**: concatenazione (`@`), zip (`++`), flatten (`!!`), vettore singleton (`[e]`)
- **Costrutto for-each**: `for(var x in expr) { ... }`
- **Operatori logici**: AND (`&&`), NOT (`!`)
- **Assert statement**: `assert expr`

### Struttura del progetto

```
lp25-26-delivery/
├── README.md
├── run-tests.ps1          # Script PowerShell per eseguire tutti i test
├── projectLabo/           # Codice sorgente Java
│   ├── Main.java          # Entry point con CLI
│   ├── parser/            # Tokenizer e Parser
│   │   ├── ast/           # Nodi AST (29 classi)
│   │   ├── Parser.java
│   │   ├── Tokenizer.java
│   │   └── TokenType.java
│   └── visitors/          # Semantica statica e dinamica
│       ├── StaticSemanticsVisitor.java
│       ├── DynamicSemanticsVisitor.java
│       ├── type/          # Gerarchia dei tipi statici
│       ├── value/         # Gerarchia dei valori runtime
│       └── environment/   # Gestione scope
└── tests/                 # 32 programmi di test
    ├── success/           # 8 test (output atteso)
    └── failure/           # 24 test (errori attesi)
        ├── static-semantics/
        ├── static-semantics-ntc/
        ├── static-semantics-only/
        └── static-semantics-only-ntc/
```

## Compilazione

Requisiti: Java 17 o superiore

```powershell
cd projectLabo
javac -d . -sourcepath . parser/*.java parser/ast/*.java visitors/*.java visitors/type/*.java visitors/value/*.java visitors/environment/*.java Main.java
```

## Esecuzione

### Sintassi CLI

```
java -cp . projectLabo.Main [-i <file>] [-o <file>] [-ntc]
```

### Opzioni

- `-i <file>`: Legge il programma da file (default: stdin)
- `-o <file>`: Scrive l'output su file (default: stdout)
- `-ntc`: Salta il type checking (no semantic check statica)

### Esempi

```powershell
# Esecuzione da file
java -cp . projectLabo.Main -i tests/success/prog01.txt

# Salvataggio output
java -cp . projectLabo.Main -i tests/success/prog01.txt -o output.txt

# Senza type checking
java -cp . projectLabo.Main -ntc -i tests/failure/static-semantics-ntc/prog01.txt
```

## Test automatici

Per eseguire tutti i 32 test e verificare che passino:

```powershell
.\run-tests.ps1
```

Lo script:
1. Compila il progetto
2. Esegue tutti i test nelle 5 categorie
3. Confronta output/errore con l'atteso
4. Riporta risultati per categoria e totale

### Categorie di test

1. **success/** (8 test): Programmi corretti, output atteso nei commenti
2. **failure/static-semantics/** (9 test): Errori di tipo rilevati dal type checker
3. **failure/static-semantics-ntc/** (9 test): Errori runtime quando si salta il type checking
4. **failure/static-semantics-only/** (3 test): Errori di tipo che non causano errori runtime
5. **failure/static-semantics-only-ntc/** (3 test): Programmi corretti solo senza type checking

## Formato output

### Successo
- Interi: `42`
- Booleani: `true` / `false`
- Coppie: `(v1,v2)`
- Vettori: `VectorValue[n]` (dove n è la dimensione)

### Errori
- Errore di sintassi: `Syntax error: on line <n>: <messaggio>`
- Errore statico: `Static error: Found <tipo>, expected <tipo>`
- Errore dinamico: `Dynamic error: <messaggio>`

## Note di implementazione

- **Visitor pattern**: Tutti i nodi AST implementano `accept(Visitor<T>)` per il double dispatch
- **Scope a 3 livelli** nel for-each: scope esterno, scope variabile iterazione, scope blocco
- **Prodotto esterno**: Memorizzato per colonne (column-major order)
- **Errori**: Messaggi identici alla specifica F# di riferimento

## Stato

✅ **Tutti i 32 test passano**

- 8/8 success
- 9/9 static-semantics
- 9/9 static-semantics-ntc
- 3/3 static-semantics-only
- 3/3 static-semantics-only-ntc
