# Relatório do grupo

Integrantes:

## Grafos e complexidade

Modelo adotado: cada condição composta (`&&`/`||`) é tratada como **uma decisão** no CFG compacto. O `switch` é modelado com uma saída para cada alternativa (`PR`, `SP/RJ` e `default`). Exceções têm uma saída de erro no desenho quando necessário, mas não são contabilizadas como branches pelo JaCoCo. A complexidade é calculada por `V(G) = E - N + 2`; no modelo compacto, equivale a `1 + número de decisões` e, no `switch`, a `1 + (número de saídas - 1)`.

| Método | V(G) | Caminhos independentes | Restrições de viabilidade |
| --- | ---: | --- | --- |
| `PoliticaDesconto.calcular` | 9 | 9 caminhos básicos | Cupom e limiares de subtotal/histórico restringem algumas combinações. |
| `CalculadoraFrete.calcular` | 9 | 9 caminhos básicos | O laço só executa quando o peso passa de 2 kg; gratuidade exige líquido >= R$ 300 e entrega normal. |
| `AnaliseRisco.avaliar` | 7 | 7 caminhos básicos | Revisão depende de combinações específicas de histórico, total, VIP e expresso. |
| `PagamentoService.pagar` | 4 | 4 caminhos básicos | Nova tentativa só ocorre após `IllegalStateException`; `false` retorna imediatamente. |
| `Pedido.subtotalCentavos` | 3 | 3 caminhos básicos | O `continue` depende de quantidade zero. |
| `Pedido.pesoGramas` | 2 | 2 caminhos básicos | Zero iterações ou uma/mais iterações. |
| `Pedido.temFragil` | 3 | 3 caminhos básicos | Retorno antecipado ao encontrar item frágil ativo. |
| `Pedido.estoqueSuficiente` | 3 | 3 caminhos básicos | `break` ocorre quando uma linha não está disponível. |
| `PedidoService.fechar` | 6 | 6 caminhos básicos | Bloqueio, subtotal zero, estoque, risco e pagamento criam saídas distintas. |

### CFGs em forma textual

Os diagramas abaixo representam os blocos e decisões principais. `S` é a saída normal e `X` é uma saída por exceção.

**`PoliticaDesconto.calcular`**

```text
Entrada -> subtotal < 0?
             |sim -> X
             |não -> VIP?
                      |sim -> 10%
                      |não -> subtotal >= 500?
                               |sim -> 5%
                               |não -> 0%
                      -> cupom nulo/branco?
                           |sim -> teto -> S
                           |não -> switch
                                   |BEMVINDO -> elegível?
                                   |EXTRA10  -> elegível?
                                   |default  -> X
                                   -> teto 20% -> S
```

**`CalculadoraFrete.calcular`**

```text
Entrada -> líquido < 0?
             |sim -> X
             |não -> switch UF
                      |PR -> 1200
                      |SP/RJ -> 2000
                      |default -> 3000
                    -> peso > 2kg?
                         |sim -> soma 300 e repete
                         |não -> continua
                    -> líquido >= 300 && normal?
                         |sim -> frete 0
                         |não -> mantém
                    -> VIP? -> metade / mantém
                    -> expresso? -> +1500 / mantém
                    -> frágil? -> +500 / mantém -> S
```

**`AnaliseRisco.avaliar`**

```text
Entrada -> total < 0?
             |sim -> X
             |não -> bloqueado?
                      |sim -> RECUSADO
                      |não -> compras == 0?
                               |sim -> total > 1000 || expresso?
                                        |sim -> REVISAO
                                        |não -> APROVADO
                               |não -> total > 5000 && !VIP?
                                        |sim -> REVISAO
                                        |não -> APROVADO
```

**`PagamentoService.pagar`**

```text
Entrada -> valida total -> valida limite -> do
            tentativa++ -> autorizar
              |retorna true/false -> S
              |IllegalStateException -> tentativa < limite?
                                          |sim -> nova tentativa
                                          |não -> false -> S
```

**`Pedido`**

```text
subtotal: Entrada -> for -> quantidade == 0? -> continue / soma -> for -> S
peso:     Entrada -> for -> soma peso -> for -> S
fragil:   Entrada -> for -> quantidade > 0 && fragil? -> true / próximo -> S
estoque:  Entrada -> for -> disponível? -> próximo / false + break -> S
```

**`PedidoService.fechar`**

```text
Entrada -> referências válidas -> bloqueado?
       |sim -> BLOQUEADO
       |não -> subtotal -> zero?
                    |sim -> X
                    |não -> estoque suficiente?
                             |não -> SEM_ESTOQUE
                             |sim -> desconto -> frete -> risco
                                          |não APROVADO -> REVISAO/RECUSADO
                                          |APROVADO -> pagamento
                                                        -> PAGO/RECUSADO
```

## Matriz de testes

| ID / método JUnit | Unidade | Entrada e estado do stub | Resultado esperado | Caminho / aresta | Critério atendido |
| --- | --- | --- | --- | --- | --- |
| D01 `PoliticaDescontoTest.vipRecebeDezPorCento` | Desconto | VIP, subtotal 10000, sem cupom | 1000 | VIP=true | ramo VIP |
| D02 `comumRecebeCincoPorCentoAcimaDoLimite` | Desconto | comum, subtotal 50000 | 2500 | VIP=false, limite=true | limite 500 |
| D03 `comumAbaixoDoLimiteNaoRecebeDesconto` | Desconto | comum, subtotal 49999 | 0 | limite=false | ramo sem desconto |
| D04 `bemVindoConcede...` | Desconto | novo, 10000, ` bemvindo ` | 2000 | BEMVINDO elegível | normalização + condição |
| D05 `extra10ConcedeDezPorCento` | Desconto | subtotal 30000, EXTRA10 | 3000 | EXTRA10 elegível | cupom |
| D06 `descontoCombinadoFicaLimitado...` | Desconto | VIP, 20000, EXTRA10 | 4000 | teto 20% | operador ternário |
| D07 `cupomDesconhecidoLancaExcecao` | Desconto | cupom inválido | `IllegalArgumentException` | default do switch | exceção |
| F01 `deveUsarTarifaDoParana` | Frete | PR, normal, <2kg | 1200 | switch PR | case |
| F02 `deveUsarTarifaDeSaoPaulo` | Frete | SP | 2000 | switch SP | case |
| F03 `deveUsarTarifaDoRioDeJaneiro` | Frete | RJ | 2000 | switch RJ | case compartilhado |
| F04 `deveUsarTarifaPadraoParaOutraUf` | Frete | MG | 3000 | default | default |
| F05 `deveCobrarUmaParcela...` | Frete | 2001g | 1500 | primeira iteração | laço |
| F06 `deveCobrarDuasParcelas...` | Frete | 4001g | 2100 | três iterações | laço |
| F07 `freteNormalEhGratis...` | Frete | líquido 30000, normal | 0 | gratuidade | condição composta |
| F08 `vipPagaMetadeDoFrete` | Frete | VIP, PR | 600 | VIP=true | ramo VIP |
| F09 `expressoAcrescenta...` | Frete | expresso | 2700 | expresso=true | adicional |
| F10 `fragilAcrescenta...` | Frete | frágil | 1700 | frágil=true | adicional |
| R01 `clienteBloqueadoEhRecusado` | Risco | bloqueado | RECUSADO | retorno antecipado | bloqueio |
| R02 `novoClienteComTotalAcima...` | Risco | novo, total 100001 | REVISAO | novo + total alto | OR |
| R03 `novoClienteComEntregaExpressa...` | Risco | novo, expresso | REVISAO | novo + expresso | OR |
| R04 `clienteComHistoricoAcima...` | Risco | comum, histórico, 500001 | REVISAO | histórico + alto + não VIP | AND |
| R05 `clienteVipComHistorico...` | Risco | VIP, histórico, 500001 | APROVADO | !VIP=false | curto-circuito/ramo |
| P01 `deveAprovarNaPrimeiraTentativa` | Pagamento | stub retorna true | true, 1 chamada | retorno imediato | sucesso |
| P02 `deveRecusarSemRepetir...` | Pagamento | stub retorna false | false, 1 chamada | retorno imediato | recusa |
| P03 `deveTentarNovamente...` | Pagamento | 1 indisponibilidade, depois true | true, 2 chamadas | catch + loop | repetição |
| P04 `deveEsgotarTentativas...` | Pagamento | sempre indisponível | false, 3 chamadas | loop até limite | esgotamento |
| P05 `devePropagarOutrasExcecoes` | Pagamento | `IllegalArgumentException` | exceção propagada | catch não captura | exceção |
| PS01 `clienteBloqueadoRetorna...` | Serviço | bloqueado | BLOQUEADO, zero, 0 cobranças | retorno inicial | colaboração |
| PS02 `pedidoSemItensAtivos...` | Serviço | subtotal zero | `IllegalArgumentException` | subtotal == 0 | retorno/exceção |
| PS03 `faltaDeEstoque...` | Serviço | quantidade > estoque | SEM_ESTOQUE, 0 cobranças | retorno antecipado | `break` |
| PS04 `riscoPendente...` | Serviço | novo cliente + total alto | REVISAO, 0 cobranças | risco != aprovado | colaboração |
| PS05 `pagamentoRecusado...` | Serviço | stub false | PAGAMENTO_RECUSADO | pagamento | integração |
| PS06 `pagamentoPodeSerTentado...` | Serviço | 2 indisponibilidades + true | PAGO, 3 chamadas | retry | estado do stub |
| PS07 `devePropagarExcecao...` | Serviço | processador lança exceção | exceção | propagação | exceção |

**Correção importante na matriz:** para 2001 g, o peso excedente é 1 g, portanto o laço acrescenta R$ 3,00. Assim, em `F05` o valor esperado correto é **1500 centavos**. Para 4001 g, o excedente inicial é 2001 g, então o laço executa três vezes e o valor esperado correto é **2100 centavos**.

## Evolução da cobertura

Como o ambiente desta entrega não possui Maven instalado, os testes foram escritos e revisados com base no código-fonte, mas o relatório numérico do JaCoCo precisa ser obtido executando `mvn clean test` no ambiente com JDK 17+ e Maven 3.9+. Não foi inventado percentual de cobertura.

| Etapa | Testes executados | Linhas | Branches | Métodos | Classes | Lacunas e justificativas |
| --- | --- | --- | --- | --- | --- | --- |
| Inicial | 1 teste em `PedidoServiceTest` | Gerado pelo JaCoCo | Gerado pelo JaCoCo | Gerado pelo JaCoCo | Gerado pelo JaCoCo | Diversos ramos ainda não exercitados |
| Completa | Toda a suíte criada | Executar JaCoCo | Executar JaCoCo | Executar JaCoCo | Executar JaCoCo | Conferir no relatório após `mvn clean test` |

## Análise crítica

- Cobertura de ramos não demonstra cobertura de caminhos completos: por exemplo, duas decisões independentes em `Participacao` podem ter todos os ramos cobertos sem executar todas as combinações de entradas. O mesmo princípio aparece no frete, risco e desconto.
- Condições compostas podem sofrer curto-circuito. Em `AnaliseRisco`, por exemplo, `total > 500_000 && !cliente.vip()` não avalia a segunda condição quando a primeira é falsa. Os testes com total no limite e cliente VIP ajudam a exercitar resultados diferentes dessa decisão.
- Alguns caminhos podem ser viáveis na unidade, mas não pelo fluxo completo do serviço. `AnaliseRisco` pode ser testada diretamente com diferentes combinações de cliente/total; no `PedidoService`, retornos anteriores, como bloqueio ou falta de estoque, impedem que determinadas etapas sejam alcançadas.
- Exceções foram testadas com `assertThrows`, incluindo entradas inválidas, cupom desconhecido, processador com exceção e indisponibilidade temporária. O `try/catch` de `PagamentoService` foi exercitado tanto com `IllegalStateException` quanto com outra exceção.
- Os laços foram exercitados com zero, uma e várias iterações quando aplicável: arrays/listas vazias ou pequenas no `Pedido`, e pesos abaixo, imediatamente acima e bem acima de 2 kg no frete.
- Para mutação proposital, uma alteração simples como trocar `>= 7` por `> 7` em uma regra de aprovação deve ser detectada pelos testes que verificam exatamente os limites 7 e 4. A alteração deve ser desfeita antes da entrega.
