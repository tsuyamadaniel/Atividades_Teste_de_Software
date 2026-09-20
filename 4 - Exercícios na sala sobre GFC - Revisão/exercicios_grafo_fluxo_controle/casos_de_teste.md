# Casos de teste derivados dos CFGs

## Exercício 1

| ID | valor | clienteVip | pagamentoAprovado | Saída esperada |
|---|---:|---|---|---|
| T1 | 100 | false | true | PEDIDO APROVADO: 100.0 |
| T2 | 500 | false | true | PEDIDO APROVADO: 450.0 |
| T3 | 100 | true | true | PEDIDO APROVADO: 95.0 |
| T4 | 100 | false | false | PAGAMENTO RECUSADO |

Teste complementar:
- `valor=500, clienteVip=true, pagamentoAprovado=true` → `PEDIDO APROVADO: 425.0`
- `valor=500, clienteVip=true, pagamentoAprovado=false` → `PAGAMENTO RECUSADO`

## Exercício 2

| ID | temperaturas | Saída esperada |
|---|---|---:|
| T1 | `{}` | 0 |
| T2 | `{-5}` | 2 |
| T3 | `{40}` | 1 |
| T4 | `{20}` | 0 |
| T5 | `{0}` | 0 |
| T6 | `{35}` | 0 |
| T7 | `{-5, 40, 20}` | 3 |
