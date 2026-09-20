# Plano de Teste: Sistema de Reserva de Salas

## 1. Introdução e objetivo

Verificar se o sistema de reserva de salas atende aos requisitos funcionais (RF-01 a RF-08) e não funcionais (RNF-01 a RNF-03), priorizando os riscos críticos: **dupla ocupação, capacidade insegura, alteração sem autorização e falha de notificação**.

## 2. Escopo do produto

- Salas possuem capacidades e recursos diferentes.
- Reservas têm data, horário, turma e responsável.
- Conflitos, manutenção e horário devem ser respeitados.
- Operações dependem do perfil do usuário.
- Alterações afetam notificações e histórico.

### 2.1 Dentro do escopo

Reserva, alteração e cancelamento; regras de conflito, capacidade, manutenção e horário; controle por perfil; notificações; histórico e auditoria; busca de salas; restrição por unidade.

### 2.2 Fora do escopo

Cadastro de usuários e turmas; infraestrutura de e-mail/push (apenas se verifica que o sistema dispara a notificação); usabilidade visual detalhada.

## 3. Requisitos avaliados

### Requisitos funcionais

| ID | Descrição |
|---|---|
| RF-01 | Reservar sala disponível para turma compatível |
| RF-02 | Impedir sobreposição na mesma sala |
| RF-03 | Impedir turma maior que a capacidade |
| RF-04 | Bloquear sala em manutenção |
| RF-05 | Permitir reservas entre 07h30 e 22h30 |
| RF-06 | Só a coordenação altera reserva de outro professor |
| RF-07 | Cancelamento libera o horário e registra histórico |
| RF-08 | Alteração ou cancelamento gera notificação |

### Requisitos não funcionais

| ID | Descrição |
|---|---|
| RNF-01 | A busca responde em até 2 segundos |
| RNF-02 | As operações possuem trilha de auditoria |
| RNF-03 | Acesso limitado às unidades autorizadas |

## 4. Estratégia e abordagem

| Nível / técnica | Aplicação |
|---|---|
| Unitário (estrutural) | Regras de negócio: sobreposição, capacidade, janela de horário, permissão |
| Funcional (caixa-preta) | Partição de equivalência e análise de valor limite em RF-02, RF-03 e RF-05 |
| Integração | Reserva/cancelamento → histórico → notificação |
| Sistema / E2E (Playwright) | Fluxos completos por perfil de usuário |
| Não funcional | Desempenho da busca (RNF-01), auditoria (RNF-02), segurança de acesso (RNF-03) |

## 5. Critérios

- **Entrada:** build implantado em ambiente de teste, dados de massa carregados e casos revisados.
- **Aprovação:** 100% dos casos de prioridade Alta executados e aprovados; nenhum defeito crítico ou alto em aberto.
- **Suspensão:** ambiente indisponível ou defeito bloqueante em login/reserva.
- **Saída:** todos os casos executados, defeitos críticos corrigidos e reteste concluído.

## 6. Ambiente e dados de teste

| Tipo | Itens |
|---|---|
| Salas | S1 (capacidade 30), S2 (capacidade 50), S3 (em manutenção, capacidade 40), sala de outra unidade |
| Usuários | Professor A, Professor B, Coordenação, usuário de outra unidade |
| Turmas | T20 (20 alunos), T30 (30), T31 (31), T50 (50) |

## 7. Riscos e mitigação

| Risco | Impacto | Mitigação |
|---|---|---|
| Dupla ocupação | Crítico | Testes de limite de horário (adjacência e sobreposição) e de concorrência |
| Capacidade insegura | Crítico | Valor limite: N−1, N, N+1 |
| Alteração sem autorização | Crítico | Matriz perfil × operação |
| Falha de notificação | Alto | Verificar disparo em toda alteração/cancelamento, inclusive com falha do serviço |

## 8. Rastreabilidade

| Requisito | Casos de teste |
|---|---|
| RF-01 | CT-01, CT-02 |
| RF-02 | CT-03 a CT-06 |
| RF-03 | CT-07 a CT-09 |
| RF-04 | CT-10, CT-11 |
| RF-05 | CT-12 a CT-17 |
| RF-06 | CT-18 a CT-21 |
| RF-07 | CT-22, CT-23 |
| RF-08 | CT-24 a CT-26 |
| RNF-01 | CT-27 |
| RNF-02 | CT-28, CT-29 |
| RNF-03 | CT-30, CT-31 |

## 9. Casos de teste

Prioridade: **A** = alta, **M** = média.

| ID | Req. | Descrição / entrada | Resultado esperado | Pri. |
|---|---|---|---|---|
| CT-01 | RF-01 | Professor A reserva S2 (50) para T30, 10h–12h, sala livre | Reserva criada, status "confirmada" | A |
| CT-02 | RF-01 | Reserva sem informar turma ou responsável | Rejeitada; mensagem indica campo obrigatório | M |
| CT-03 | RF-02 | Reservar S1 para 10h–12h quando já existe reserva 10h–12h | Rejeitada por conflito | A |
| CT-04 | RF-02 | Reservar S1 para 11h–13h com existente 10h–12h (sobreposição parcial) | Rejeitada | A |
| CT-05 | RF-02 | Reservar S1 para 12h–14h com existente 10h–12h (horários adjacentes) | Aceita (fronteira não conflita) | A |
| CT-06 | RF-02 | Mesmo horário 10h–12h em S2 (sala diferente) | Aceita | M |
| CT-07 | RF-03 | T30 em S1 (capacidade 30) | Aceita (limite exato) | A |
| CT-08 | RF-03 | T31 em S1 (capacidade 30) | Rejeitada: turma excede a capacidade | A |
| CT-09 | RF-03 | T20 em S1 | Aceita | M |
| CT-10 | RF-04 | Reservar S3 (em manutenção) dentro do período de manutenção | Rejeitada com motivo "manutenção" | A |
| CT-11 | RF-04 | Reservar S3 fora do período de manutenção | Aceita | M |
| CT-12 | RF-05 | Reserva iniciando às 07h30 | Aceita (limite inferior) | A |
| CT-13 | RF-05 | Reserva iniciando às 07h29 | Rejeitada | A |
| CT-14 | RF-05 | Reserva terminando às 22h30 | Aceita (limite superior) | A |
| CT-15 | RF-05 | Reserva terminando às 22h31 | Rejeitada | A |
| CT-16 | RF-05 | Reserva 14h–16h (dentro da janela) | Aceita | M |
| CT-17 | RF-05 | Reserva às 03h00 | Rejeitada | M |
| CT-18 | RF-06 | Professor B tenta alterar reserva do Professor A | Negado; reserva inalterada | A |
| CT-19 | RF-06 | Coordenação altera reserva do Professor A | Alteração permitida | A |
| CT-20 | RF-06 | Professor A altera a própria reserva | Permitida | A |
| CT-21 | RF-06 | Professor B tenta cancelar reserva do Professor A | Negado | A |
| CT-22 | RF-07 | Cancelar reserva 10h–12h da S1 e reservar o mesmo horário em seguida | Horário liberado; nova reserva aceita | A |
| CT-23 | RF-07 | Cancelar reserva | Histórico registra quem, quando e qual reserva | A |
| CT-24 | RF-08 | Alterar reserva | Notificação enviada aos envolvidos (responsável/turma) | A |
| CT-25 | RF-08 | Cancelar reserva | Notificação enviada | A |
| CT-26 | RF-08 | Alterar reserva com serviço de notificação indisponível | Alteração não é perdida; falha registrada e notificação reenfileirada | A |
| CT-27 | RNF-01 | Buscar salas disponíveis com massa realista (ex.: 500 salas, carga simultânea) | Resposta em até 2 s | M |
| CT-28 | RNF-02 | Criar, alterar e cancelar reservas | Cada operação gera registro de auditoria (usuário, ação, data/hora) | A |
| CT-29 | RNF-02 | Tentativa negada (CT-18) | Tentativa também registrada na auditoria | M |
| CT-30 | RNF-03 | Usuário de outra unidade tenta reservar sala desta unidade | Acesso negado | A |
| CT-31 | RNF-03 | Usuário autorizado consulta salas da sua unidade | Vê somente as salas da sua unidade | M |

## 10. Observações

- **Concorrência (risco de dupla ocupação):** além dos casos acima, incluir um teste com duas reservas simultâneas para a mesma sala e horário; apenas uma deve ser confirmada.
- **Premissas:** critérios como "quem recebe a notificação" (CT-24) e "período de manutenção" (CT-10/CT-11) foram assumidos. Ajustar caso a atividade os defina de outra forma.
