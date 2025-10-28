@echo off
REM make_key.bat
REM Usage:
REM   make_key.bat           -> prompts for serial and prints key
REM   make_key.bat "SERIAL"  -> prints key for given SERIAL

if "%~1"=="" (
  powershell -NoProfile -Command ^
    "$s = Read-Host 'Enter baseboard serial'; if(!$s){ Write-Error 'No input'; exit 2 }; " ^
    "$h = [System.Security.Cryptography.SHA256]::Create().ComputeHash([System.Text.Encoding]::UTF8.GetBytes($s)); " ^
    "$b = [System.Convert]::ToBase64String($h); " ^
    "$k = $b.Replace('+','X').Replace('/','Y').Replace('=',''); " ^
    "$k = ($k -replace '[^A-Za-z0-9]',''); if($k.Length -ge 15){ $k = $k.Substring(0,15) }; Write-Output $k.ToUpper()"
) else (
  powershell -NoProfile -Command ^
    "$s = '%~1'; " ^
    "$h = [System.Security.Cryptography.SHA256]::Create().ComputeHash([System.Text.Encoding]::UTF8.GetBytes($s)); " ^
    "$b = [System.Convert]::ToBase64String($h); " ^
    "$k = $b.Replace('+','X').Replace('/','Y').Replace('=',''); " ^
    "$k = ($k -replace '[^A-Za-z0-9]',''); if($k.Length -ge 15){ $k = $k.Substring(0,15) }; Write-Output $k.ToUpper()"
)
