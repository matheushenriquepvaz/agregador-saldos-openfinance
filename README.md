# Agregador de Saldos Open Finance

Projeto Spring Boot para agregação de extratos da Finbras e de instituições externas, com H2 para testes, Swagger e cache Redis.

## Tecnologias
- Java 21
- Spring Boot
- Spring Data JPA
- H2 Database
- Redis Cache
- Springdoc OpenAPI / Swagger

## Como executar

1. Suba o Redis localmente na porta `6379`.
   Exemplo com Docker:

```powershell
docker run --name redis-finbras -p 6379:6379 redis:7-alpine
```

2. Rode a aplicação:

```powershell
mvn spring-boot:run
```

## Swagger

Acesse:

```text
http://localhost:8080/swagger-ui.html
```

## Endpoints principais

### Extratos da Finbras
- `GET /api/extratos/finbras`
- `GET /api/extratos/finbras/cache`

### Extratos externos
- `GET /api/extratos/externos`
- `GET /api/extratos/externos/cache`
- `GET /api/extratos/externos/{institutionId}`
- `GET /api/extratos/externos/{institutionId}/cache`

### Saldo unificado
- `GET /api/saldos/unificado`
- `GET /api/saldos/unificado/{institutionId}`

Regras da resposta unificada:
- quando o lançamento for da Finbras, `sourceBankName` e `sourceBankCnpj` vêm vazios;
- `movementValue` é sempre positivo;
- `movementType` indica `DEBITO` ou `CREDITO`;
- a Finbras é consultada sempre direto;
- as outras instituições podem ser servidas por cache.

### Mensageria mockada
- `POST /api/mensageria/pedir-extrato`

Corpo opcional:

```json
{
  "institutionId": "BANCO_ALPHA"
}
```

Se você não enviar corpo, o sistema simula a solicitação para as 3 instituições externas.

### Dados fictícios
- `POST /api/dev/popular-dados`

Esse endpoint recria os lançamentos fictícios da Finbras e de 3 instituições externas.

## H2 Console

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:agregador
```

