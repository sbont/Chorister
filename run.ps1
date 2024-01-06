Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
Set-Location -Path "c:\Projects\private\Chorister\"

# Proxy server
Write-Host "Starting proxy..."
Push-Location "reverse-proxy"
Start-Process -FilePath "caddy" -ArgumentList "run --config .\Caddyfile-dev" -PassThru 
Pop-Location

# DB
Write-Host "Starting Cockroach..."
Start-Process -FilePath "$($env:APPDATA)\cockroach\cockroach.exe" -ArgumentList "start-single-node --certs-dir cockroachdb\certs --store=C:\db\ --listen-addr=localhost:26258 --http-addr=localhost:8085" -PassThru
pause

# Auth server
Write-Host "Starting Zitadel..."
Start-Process -FilePath ".\zitadel\zitadel.exe" -ArgumentList "start --masterkey evFuvyi4MaFrkpyo8EznFoa8ECfuKgXi --config .\zitadel\zitadel-config-local-dell.yaml --tlsMode disabled" -PassThru
pause

# Write-Host "Starting Keycloak..."
# Start-Process -FilePath ".\keycloak\bin\standalone.bat" -ArgumentList "-Djboss.socket.binding.port-offset=920" -PassThru
# pause

# Frontend
Push-Location -Path frontend
Write-Host "Starting front-end..."
yarn run dev
Pop-Location

Read-Host -Prompt "Press Enter to exit"