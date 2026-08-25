# Script di test automatico per il progetto finale LPO
# Esegue tutti i 32 test e verifica che passino

$ErrorActionPreference = "Continue"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Test Automatici - Progetto Finale LPO" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Compilazione in directory temporanea
Write-Host "Compilazione del progetto..." -ForegroundColor Yellow
$binDir = "bin_temp"
if (Test-Path $binDir) { Remove-Item $binDir -Recurse -Force }
New-Item -ItemType Directory -Path $binDir | Out-Null

Push-Location "projectLabo"
javac -d "../$binDir" -sourcepath . parser/*.java parser/ast/*.java visitors/*.java visitors/type/*.java visitors/value/*.java visitors/environment/*.java Main.java 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRORE: Compilazione fallita!" -ForegroundColor Red
    Pop-Location
    Remove-Item "../$binDir" -Recurse -Force
    exit 1
}
Write-Host "Compilazione completata con successo." -ForegroundColor Green
Write-Host ""
Pop-Location

# Funzione per estrarre l'output atteso dai commenti
function Get-ExpectedOutput {
    param([string]$filePath)
    
    $lines = Get-Content $filePath
    $expected = @()
    
    foreach ($line in $lines) {
        if ($line -match '//\s*(.+)$') {
            $comment = $matches[1].Trim()
            # Ignora commenti vuoti o solo descrittivi
            if ($comment -ne "" -and $comment -notmatch "^(tutte le asserzioni|il test non viene superato)") {
                # Cerca stringhe tra virgolette (output atteso)
                if ($comment -match '"([^"]+)"') {
                    $expected += $matches[1]
                } else {
                    $expected += $comment
                }
            }
        }
    }
    
    return ($expected -join "`n")
}

# Funzione per eseguire un test
function Run-Test {
    param(
        [string]$testFile,
        [string]$category,
        [bool]$useNtc
    )
    
    $testName = [System.IO.Path]::GetFileName($testFile)
    $args = @("-cp", $binDir, "projectLabo.Main")
    
    if ($useNtc) {
        $args += "-ntc"
    }
    
    $args += @("-i", $testFile)
    
    try {
        $output = & java @args 2>&1 | Out-String
        $output = $output.Trim()
        
        # Determina il risultato atteso in base alla categoria
        $expected = Get-ExpectedOutput $testFile
        $passed = $false
        
        switch ($category) {
            "success" {
                # Per i test di successo, verifica che non ci siano errori
                if ($output -notmatch "(Syntax|Static|Dynamic|Lexical) error") {
                    # Verifica che l'output corrisponda a quanto atteso
                    if ($expected -eq "" -or $output -eq $expected) {
                        $passed = $true
                    }
                }
            }
            "static-semantics" {
                # Deve esserci un errore statico
                if ($output -match "Static error:") {
                    $passed = $true
                }
            }
            "static-semantics-ntc" {
                # Con -ntc deve esserci un errore dinamico
                if ($output -match "Dynamic error:") {
                    $passed = $true
                }
            }
            "static-semantics-only" {
                # Senza -ntc deve esserci un errore statico
                if ($output -match "Static error:") {
                    $passed = $true
                }
            }
            "static-semantics-only-ntc" {
                # Con -ntc NON deve esserci errore
                if ($output -notmatch "(Syntax|Static|Dynamic|Lexical) error") {
                    $passed = $true
                }
            }
        }
        
        if ($passed) {
            Write-Host "  [PASS] $testName" -ForegroundColor Green
            return $true
        } else {
            Write-Host "  [FAIL] $testName" -ForegroundColor Red
            Write-Host "         Output: $output" -ForegroundColor Gray
            if ($expected -ne "") {
                Write-Host "         Atteso: $expected" -ForegroundColor Gray
            }
            return $false
        }
    }
    catch {
        Write-Host "  [FAIL] $testName - Eccezione: $_" -ForegroundColor Red
        return $false
    }
}

# Statistiche totali
$totalTests = 0
$passedTests = 0

# Funzione per eseguire una categoria di test
function Run-TestCategory {
    param(
        [string]$categoryPath,
        [string]$categoryName,
        [bool]$useNtc
    )
    
    if (-not (Test-Path $categoryPath)) {
        return
    }
    
    $testFiles = Get-ChildItem $categoryPath -Filter "*.txt" | Sort-Object Name
    $categoryPassed = 0
    $categoryTotal = $testFiles.Count
    
    Write-Host "Categoria: $categoryName ($categoryTotal test)" -ForegroundColor Cyan
    
    foreach ($testFile in $testFiles) {
        $script:totalTests++
        if (Run-Test $testFile.FullName $categoryName $useNtc) {
            $script:passedTests++
            $categoryPassed++
        }
    }
    
    Write-Host "  Risultato: $categoryPassed/$categoryTotal" -ForegroundColor $(if ($categoryPassed -eq $categoryTotal) { "Green" } else { "Yellow" })
    Write-Host ""
}

# Esecuzione di tutte le categorie
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Esecuzione Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Test di successo (senza -ntc)
Run-TestCategory "tests/success" "success" $false

# 2. Test con errori statici (senza -ntc)
Run-TestCategory "tests/failure/static-semantics" "static-semantics" $false

# 3. Test con errori dinamici (con -ntc)
Run-TestCategory "tests/failure/static-semantics-ntc" "static-semantics-ntc" $true

# 4. Test solo errori statici (senza -ntc)
Run-TestCategory "tests/failure/static-semantics-only" "static-semantics-only" $false

# 5. Test solo errori statici (con -ntc, nessun errore)
Run-TestCategory "tests/failure/static-semantics-only-ntc" "static-semantics-only-ntc" $true

# Risultato finale
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Risultato Finale" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Test totali: $totalTests" -ForegroundColor White
Write-Host "Test passati: $passedTests" -ForegroundColor $(if ($passedTests -eq $totalTests) { "Green" } else { "Yellow" })
Write-Host "Test falliti: $($totalTests - $passedTests)" -ForegroundColor $(if ($passedTests -eq $totalTests) { "Green" } else { "Red" })
Write-Host ""

if ($passedTests -eq $totalTests) {
    Write-Host "[OK] TUTTI I TEST SONO STATI SUPERATI!" -ForegroundColor Green
    Remove-Item $binDir -Recurse -Force
    exit 0
} else {
    Write-Host "[ERRORE] ALCUNI TEST SONO FALLITI" -ForegroundColor Red
    Remove-Item $binDir -Recurse -Force
    exit 1
}
