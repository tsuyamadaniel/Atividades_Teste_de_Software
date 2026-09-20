# Exercícios — Grafo de Fluxo de Controle

## Exercício 1 — Classificação de pedido

### 1. Blocos básicos

| Nó | Bloco básico |
|---|---|
| 1 | Início; `desconto = 0` |
| 2 | Decisão: `valor >= 500` |
| 3 | `desconto = 10` |
| 4 | Decisão: `clienteVip` |
| 5 | `desconto += 5` |
| 6 | Decisão: `!pagamentoAprovado` |
| 7 | `return "PAGAMENTO RECUSADO"` |
| 8 | `valorFinal = valor - (valor * desconto / 100)` |
| 9 | `return "PEDIDO APROVADO: " + valorFinal` |
| 10 | Fim |

### 2. Decisões

1. `valor >= 500`
2. `clienteVip`
3. `!pagamentoAprovado`

Cada decisão possui saída verdadeira (T) e falsa (F).

### 3–4. CFG

```text
                 ┌───────────────┐
                 │ 1 Início      │
                 │ desconto = 0  │
                 └───────┬───────┘
                         │
                         v
                 ┌────────────────┐
              ┌─>│ 2 valor >= 500│
              │  └──────┬─────┬───┘
              │       T │     │ F
              │         v     └──────────────┐
              │ ┌───────────────┐            │
              │ │ 3 desconto=10 │            │
              │ └───────┬───────┘            │
              │         │                    │
              │         └─────────┬──────────┘
              │                   v
              │          ┌────────────────┐
              │          │ 4 clienteVip?  │
              │          └──────┬─────┬───┘
              │               T │     │ F
              │                 v     └─────────────┐
              │        ┌────────────────┐            │
              │        │ 5 desconto +=5 │            │
              │        └───────┬────────┘            │
              │                │                     │
              │                └──────────┬──────────┘
              │                           v
              │                 ┌────────────────────┐
              │                 │ 6 !pagamentoAprov. │
              │                 └───────┬────────┬───┘
              │                       T │        │ F
              │                         v        v
              │                 ┌────────────┐ ┌──────────────────────┐
              │                 │ 7 return   │ │ 8 calcula valorFinal │
              │                 │ recusado   │ └───────────┬──────────┘
              │                 └─────┬──────┘             │
              │                       │                    v
              │                       │            ┌─────────────────┐
              │                       │            │ 9 return aprovado│
              │                       │            └────────┬────────┘
              │                       v                    v
              │                    ┌──────────────────────────┐
              └────────────────────│ 10 Fim                  │
                                   └──────────────────────────┘
```

### 5–7. N, E e complexidade

Considerando os nós 1 a 10:

- `N = 10`
- `E = 11`

Arestas:

`1→2, 2→3, 2→4, 3→4, 4→5, 4→6, 5→6, 6→7, 6→8, 7→10, 8→9, 9→10`

Portanto, há na verdade **12 arestas** na enumeração acima.

Assim:

`V(G) = E - N + 2`

`V(G) = 12 - 10 + 2 = 4`

Pela segunda fórmula:

`V(G) = número de decisões + 1`

`V(G) = 3 + 1 = 4`

Logo, a complexidade ciclomática é **4**.

### 8–10. Base de caminhos independentes e testes

Uma base possível:

| Caminho | Sequência | Entradas | Resultado |
|---|---|---|---|
| P1 | 1-2F-4F-6F-8-9-10 | `valor=100`, `clienteVip=false`, `pagamentoAprovado=true` | `PEDIDO APROVADO: 100.0` |
| P2 | 1-2T-3-4F-6F-8-9-10 | `valor=500`, `clienteVip=false`, `pagamentoAprovado=true` | `PEDIDO APROVADO: 450.0` |
| P3 | 1-2F-4T-5-6F-8-9-10 | `valor=100`, `clienteVip=true`, `pagamentoAprovado=true` | `PEDIDO APROVADO: 95.0` |
| P4 | 1-2F-4F-6T-7-10 | `valor=100`, `clienteVip=false`, `pagamentoAprovado=false` | `PAGAMENTO RECUSADO` |

Observação: a base contém quatro caminhos independentes, conforme `V(G)=4`. Existem outras combinações possíveis, mas elas não são necessárias para formar a base mínima.

### Questões para discussão

**Quantas combinações entre as três condições são possíveis?**

Cada uma das três condições pode assumir dois estados. Portanto:

`2 × 2 × 2 = 8 combinações`

**O número de combinações possíveis é igual à complexidade ciclomática?**

Não. As 8 combinações representam combinações de valores lógicos das condições. A complexidade ciclomática mede a quantidade de caminhos linearmente independentes do grafo. Para este método, são 4 caminhos independentes, embora possam existir 8 combinações de entradas.

**Como o `return` dentro da terceira condição altera o grafo?**

Ele cria um caminho de saída antecipada. Quando `!pagamentoAprovado` é verdadeiro, o fluxo vai diretamente ao fim, sem executar o cálculo de `valorFinal`.

**É possível executar o cálculo de `valorFinal` quando o pagamento não foi aprovado?**

Não. Se `!pagamentoAprovado` for verdadeiro, o `return` encerra o método antes do cálculo.

---

# Exercício 2 — Análise de leituras de temperatura

### 1. Blocos básicos

| Nó | Bloco básico |
|---|---|
| 1 | Início; `alertas = 0`; `i = 0` |
| 2 | Decisão: `i < temperaturas.length` |
| 3 | Decisão: `temperaturas[i] < 0` |
| 4 | `alertas += 2` |
| 5 | Decisão: `temperaturas[i] > 35` |
| 6 | `alertas++` |
| 7 | `i++` |
| 8 | `return alertas` |
| 9 | Fim |

### 2. Decisões

1. `i < temperaturas.length` — condição do `while`
2. `temperaturas[i] < 0` — primeiro `if`
3. `temperaturas[i] > 35` — `else if`

Logo, há 3 decisões.

### 3–4. CFG

```text
                         ┌─────────────────────┐
                         │ 1 Início            │
                         │ alertas=0; i=0      │
                         └──────────┬──────────┘
                                    │
                                    v
                         ┌─────────────────────┐
                    ┌───>│ 2 i < length ?     │
                    │    └──────┬────────┬─────┘
                    │         T │        │ F
                    │           v        v
                    │   ┌─────────────────┐  ┌───────────────┐
                    │   │ 3 temp[i] < 0 ? │  │ 8 return      │
                    │   └──────┬──────┬───┘  │ alertas       │
                    │        T │      │ F     └──────┬────────┘
                    │          v      v              │
                    │ ┌────────────┐ ┌─────────────────────┐
                    │ │4 alertas+=2│ │5 temp[i] > 35 ?    │
                    │ └─────┬──────┘ └──────┬────────┬─────┘
                    │       │             T │        │ F
                    │       │               v        │
                    │       │        ┌────────────┐  │
                    │       │        │ 6 alertas++│  │
                    │       │        └─────┬──────┘  │
                    │       │              │         │
                    │       └──────────────┴────┬────┘
                    │                            v
                    │                    ┌────────────┐
                    │                    │ 7 i++      │
                    │                    └─────┬──────┘
                    │                          │
                    └──────────────────────────┘

                         8 return ─────────────> 9 Fim
```

As três classificações possíveis dentro de cada iteração são:

- temperatura `< 0` → nó 4;
- temperatura `> 35` → nó 6;
- temperatura `>= 0 && <= 35` → nenhum incremento de `alertas`, seguindo para o nó 7.

### 5–6. N, E e complexidade

Nós:

`N = 9`

Arestas:

`1→2`
`2→3`
`2→8`
`3→4`
`3→5`
`4→7`
`5→6`
`5→7`
`6→7`
`7→2`
`8→9`

Logo:

`E = 11`

Complexidade:

`V(G) = E - N + 2`

`V(G) = 11 - 9 + 2 = 4`

Pela segunda fórmula:

`V(G) = decisões + 1`

`V(G) = 3 + 1 = 4`

Logo, a complexidade ciclomática é **4**.

### 7–9. Base de caminhos independentes e testes

| Caminho | Sequência | Entrada | Retorno |
|---|---|---|---|
| P1 | 1-2F-8-9 | `{}` | `0` |
| P2 | 1-2T-3T-4-7-2F-8-9 | `{-5}` | `2` |
| P3 | 1-2T-3F-5T-6-7-2F-8-9 | `{40}` | `1` |
| P4 | 1-2T-3F-5F-7-2F-8-9 | `{20}` | `0` |

Esses quatro caminhos formam uma base possível de caminhos independentes.

### Caminhos adicionais úteis

Embora não sejam necessários para a base mínima, entradas com várias temperaturas demonstram a repetição do laço:

- `{-5, 40, 20}` → `2 + 1 + 0 = 3`
- `{40, -2, 36}` → `1 + 2 + 1 = 4`
- `{0, 35}` → `0`

### 10. Por que o retorno do laço precisa aparecer no CFG?

Porque o `while` pode executar várias vezes. Depois de processar uma temperatura e executar `i++`, o fluxo retorna à condição `i < temperaturas.length`.

Essa aresta de retorno representa a repetição do laço. Sem ela, o grafo não representaria corretamente a estrutura de controle do programa.

### Questões para discussão

**Um vetor com várias temperaturas percorre um único caminho ou pode repetir partes do grafo?**

Ele pode repetir partes do grafo. A cada nova posição do vetor, o fluxo volta ao nó 2 e percorre novamente os nós correspondentes à classificação da temperatura.

**Qual entrada permite sair do método sem acessar uma posição do vetor?**

Um vetor vazio, por exemplo `{}`. Nesse caso, `i < temperaturas.length` já é falso na primeira avaliação.

**Os testes dos valores 0 e 35 ajudam a avaliar quais fronteiras?**

Sim. Eles verificam os limites das condições. `0` não é menor que zero, e `35` não é maior que 35. Portanto, ambos seguem o caminho de temperatura normal.

**Por que o `else if` deve ser representado como uma nova decisão?**

Porque ele possui uma condição própria (`temperaturas[i] > 35`) que pode ser verdadeira ou falsa. Assim, acrescenta um ponto de decisão ao grafo e à complexidade ciclomática.
