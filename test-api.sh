#!/bin/bash

# Script para testar as APIs do sistema Tuning OPS
echo "=== Teste de API do Hệ thống Tuning OPS ==="
echo ""

# Configurações
API_URL="http://localhost:8080/api"

# Cores para saída
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função de teste
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo -e "${BLUE}Testando: ${description}${NC}"
    echo "Método: $method, Endpoint: $endpoint"
    
    if [ -n "$data" ]; then
        echo "Dados: $data"
        response=$(curl -s -X $method -H "Content-Type: application/json" -d "$data" "$API_URL$endpoint")
    else
        response=$(curl -s -X $method "$API_URL$endpoint")
    fi
    
    if [ -n "$response" ]; then
        echo -e "${GREEN}Sucesso! Resposta recebida.${NC}"
        echo "$response" | python -m json.tool 2>/dev/null || echo "$response"
        echo ""
        return 0
    else
        echo -e "${RED}Falha! Nenhuma resposta recebida.${NC}"
        echo ""
        return 1
    fi
}

# Testar endpoint de estatísticas
echo "===== Estatísticas ====="
test_endpoint "GET" "/incidents/stats" "" "Obter estatísticas de incidentes"

# Testar listar todos os incidentes
echo "===== Listar Incidentes ====="
test_endpoint "GET" "/incidents" "" "Listar todos os incidentes"

# Criar um incidente de teste
echo "===== Criar Incidente ====="
incident_data='{
    "title": "Teste API - Incidente Criado via Script",
    "description": "Este é um incidente de teste criado pelo script de teste de API",
    "status": "INVESTIGATING",
    "severityLevel": "SEV2",
    "affectedService": "API Test Service",
    "reportedBy": "Script de Teste"
}'
test_endpoint "POST" "/incidents" "$incident_data" "Criar novo incidente"

# Extrair ID do último incidente criado para usar nos próximos testes
LAST_ID=$(curl -s "$API_URL/incidents" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

if [ -n "$LAST_ID" ]; then
    echo "ID do incidente para testes: $LAST_ID"
    
    # Obter detalhes de um incidente específico
    echo "===== Detalhes do Incidente ====="
    test_endpoint "GET" "/incidents/$LAST_ID" "" "Obter detalhes do incidente #$LAST_ID"
    
    # Atualizar o incidente
    echo "===== Atualizar Incidente ====="
    update_data='{
        "title": "Teste API - Incidente Atualizado",
        "description": "Este incidente foi atualizado pelo script de teste",
        "status": "IDENTIFIED",
        "severityLevel": "SEV2",
        "affectedService": "API Test Service Updated",
        "assignee": "Técnico de Suporte"
    }'
    test_endpoint "PUT" "/incidents/$LAST_ID" "$update_data" "Atualizar incidente #$LAST_ID"
    
    # Adicionar uma atualização ao timeline do incidente
    echo "===== Adicionar Timeline ====="
    timeline_data='{
        "message": "Atualização adicionada pelo script de teste",
        "updateType": "STATUS_CHANGE",
        "createdBy": "Script de Teste"
    }'
    test_endpoint "POST" "/incidents/$LAST_ID/updates" "$timeline_data" "Adicionar atualização ao incidente #$LAST_ID"
    
    # Verificar os detalhes do incidente após as atualizações
    echo "===== Verificar Atualizações ====="
    test_endpoint "GET" "/incidents/$LAST_ID" "" "Verificar detalhes atualizados do incidente #$LAST_ID"
else
    echo -e "${RED}Não foi possível obter o ID do último incidente. Pulando testes de atualização.${NC}"
fi

echo "=== Testes de API concluídos ==="
