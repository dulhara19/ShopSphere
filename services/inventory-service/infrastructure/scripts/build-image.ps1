param(
    [string]$Tag = "latest"
)

$ImageName = "inventory-service:$Tag"
docker build -t $ImageName -f infrastructure/docker/Dockerfile .
Write-Host "Built image $ImageName"
