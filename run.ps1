Write-Host "Starting dproxy..."
Set-Location -Path "c:\Projects\Kotlin\Chorister\"
Push-Location "dev-proxy"
Start-Process -FilePath "node" -ArgumentList ".\node_modules\dprox\dprox.js" -PassThru 
Pop-Location
Write-Host "Starting Zitadel..."
Start-Process -FilePath ".\zitadel\zitadel.exe" -ArgumentList "start-from-init --masterkey "evFuvyi4MaFrkpyo8EznFoa8ECfuKgXi" --config .\zitadel-config.yaml" -PassThru
pause
Write-Host "Starting Keycloak..."
Start-Process -FilePath ".\keycloak\bin\standalone.bat" -ArgumentList "-Djboss.socket.binding.port-offset=920" -PassThru
pause
Push-Location -Path frontend
Write-Host "Starting front-end..."
yarn serve
Pop-Location