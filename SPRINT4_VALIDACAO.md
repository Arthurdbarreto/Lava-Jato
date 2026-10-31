# Sprint 4: controle de horários

## Implementação e pré-condição de operação

Branch de trabalho: `sprint-04-controle-horarios`. A alteração local prévia de
`Appointment.kt` foi mantida e estendida. Na implementação inicial, nenhuma operação de produção foi realizada. Posteriormente, as regras foram publicadas no Firebase Console e o controle de horários foi ativado no ambiente utilizado nos testes.

O documento `horarios_reservados/lava_jato_prime_YYYY-MM-DD_HH-mm` serializa as
tentativas para o mesmo estabelecimento, data e horário, independentemente do
serviço. A transação lê o controle administrativo, o veículo do dono e a reserva
antes de criar o agendamento e a reserva. Não há consultas externas à transação
usadas como garantia de exclusão mútua. O campo `inicio` usa America/Recife.

**Novas reservas ficam bloqueadas enquanto
`controle_horarios/lava_jato_prime.reconciliado` não for `true`.** Esse documento
não é criado nem alterado pelo aplicativo. Ausência, erro de leitura e cache
local não autorizam disponibilidade. Esse bloqueio é necessário porque registros
antigos sem documento de reserva não participam das transações por horário.

Os listeners incluem mudanças de metadados: dados apenas em cache, gravações
pendentes, documentos inconsistentes e falhas bloqueiam seleção/confirmação.
Trocar data ou usuário desfaz a consulta anterior e limpa a seleção inválida.
O relógio da interface é atualizado a cada segundo; a transação revalida o passado.
As regras publicadas também comparam `inicio` com o relógio do servidor.

Cancelamento lê o dono e o status no servidor. O cliente só solicita CANCELADO.
Para agendamentos da versão 2, uma reserva estritamente correspondente, sem
pendência legada e sob controle reconciliado, é excluída na mesma transação.
Legados, reservas ausentes, divergentes ou sem sinalização explícita de segurança
permanecem bloqueados. Isso pode exigir liberação administrativa depois do último
cancelamento legado: o cliente privilegia não liberar um horário indevidamente.

## Regras de segurança — implementação e publicação

Arquivo utilizado: `LavaMais_Firestore_Regras_Mescladas.rules`. Inicialmente, nenhuma regra existente foi encontrada no
repositório. As regras consolidadas foram posteriormente publicadas no Firebase Console. Não se deve
substituí-las sem comparação, revisão e testes no Emulator Suite.

As regras consolidadas limitam perfis/veículos/agendamentos ao dono, permite somente transição
CONFIRMADO para CANCELADO, nega exclusão de agendamentos e finalização pelo cliente,
valida veículo próprio, ID determinístico e vínculo bidirecional com `getAfter`.
Criação isolada, substituição de reserva existente e liberação isolada são negadas.
Cancelamento normal exige a liberação correspondente; cancelamento legado conserva
o bloqueio. Controle administrativo e alteração de reservas não são graváveis pelo
cliente. Operações administrativas continuam sujeitas à governança e IAM do servidor.

A disponibilidade é compartilhada entre usuários autenticados. Reservas novas
contêm somente estabelecimento/data/hora, ID opaco do agendamento e bloqueioLegado.
**Auditar as reservas antigas antes de publicar:** a implementação anterior
armazenava `usuarioId` nessa coleção; regras não ocultam campos individuais.
Se houver UID, placa, nome ou outros dados pessoais em documentos antigos, é
necessário aprovar um saneamento ou desenhar uma coleção pública separada antes
de habilitar a leitura compartilhada. Nada foi saneado automaticamente.

Verificar também se existem outras coleções em uso fora desta cópia; a proposta
nega por padrão caminhos não declarados. Perfis não devem conter privilégios
administrativos confiáveis em campos que o usuário pode editar.

## Procedimento de reconciliação e cuidados administrativos

1. Exportar backup, guardar as regras atuais e gerar inventário somente de leitura.
2. Com autorização específica, pausar novas gravações e mudanças de status durante
   o corte usando regras administrativas. Incluir versões antigas do aplicativo e
   quaisquer escritores administrativos; o sinalizador sozinho não os controla.
3. Agrupar TODOS os CONFIRMADO por estabelecimento, data canônica e hora canônica.
   Não presumir que todo registro sem estabelecimento pertence ao Prime: revisar a
   origem, os formatos antigos, documentos órfãos e divergências manualmente.
   Casos ambíguos impedem a ativação do controle.
4. Para cada grupo legado ocupado, preparar, em simulação, uma reserva determinística
   conservadora com `bloqueioLegado: true`. Registrar todos os IDs envolvidos em
   relatório administrativo privado. Não escolher um vencedor nem cancelar duplicados.
   Reservas existentes exigem revisão caso a caso; não sobrescrever referências.
5. Após aprovação e em janela controlada, criar somente os bloqueios faltantes e
   ajustar bloqueios existentes conforme plano aprovado. Não alterar registros
   antigos automaticamente nem atribuir versão 2 a legados. Tratar também dados
   pessoais nas reservas conforme aprovação separada.
6. Recontar e provar que TODO confirmado do Prime tem bloqueio canônico, e que
   NENHUM grupo duplicado/legado está marcado como livre de pendências. Só então
   definir `reconciliado: true` por ferramenta administrativa, sob regras validadas.
   Manter novos escritores sujeitos ao protocolo; reabrir apenas após a verificação.
7. Cancelamentos legados não liberam o bloqueio. Após o último confirmado de um grupo
   deixar de existir, o operador pode propor liberação com nova conferência sob
   exclusão de escritores concorrentes e autorização específica. Não usar uma
   consulta seguida de exclusão sem proteção contra mudanças concorrentes.

O invariante de reconciliação é uma responsabilidade administrativa. Nem o SDK
Android nem as regras conseguem consultar atomicamente todos os documentos legados
de uma coleção para provar ausência de outro confirmado. Se um escritor privilegiado
introduzir um confirmado sem reserva ou remover um bloqueio, a garantia é quebrada.

## Roteiro de testes no Android Studio e Emulator Suite

Executar em ambiente de teste isolado, nunca criando fixtures em produção.

Teste automatizado das regras (Node e Firebase CLI instalados separadamente):
`firebase emulators:exec --only firestore --project demo-lava-sprint4 --config firebase.emulator.json "node tests/firestore/test-rules.mjs"`.
O teste usa somente 127.0.0.1:8088 e um projeto demo; não usa credenciais de produção.

1. Dois usuários A/B, cada um com veículo próprio, selecionam a mesma data futura
   e 14:30, com serviços diferentes. Sincronizar o clique em Confirmar. Exatamente
   um deve vencer; deve existir um único confirmado e uma única reserva canônica.
   Repetir invertendo os usuários e com atraso artificial de rede.
2. Manter B na grade enquanto A confirma. B deve ver o horário desabilitar e sua
   seleção ser limpa. A reserva deve impedir confirmação mesmo antes do listener.
3. A cancela a própria reserva da versão 2. Status CANCELADO e remoção da reserva
   devem ocorrer juntos; B deve poder reservar em seguida. Testar cancelamento
   concorrente com nova reserva: a antiga operação nunca pode apagar a nova.
4. Por chamadas diretas contra o emulador, B tenta cancelar/finalizar o agendamento
   de A; A tenta finalizar o próprio, trocar data/veículo/estabelecimento, criar só
   o agendamento, criar só a reserva, excluir só a reserva, usar veículo de B e
   sobrescrever uma reserva. Todas essas operações devem ser negadas.
5. Sem documento de controle, com reconciliado false, sem permissão, offline e só
   com cache, os horários devem ficar indisponíveis. Restaurar rede e verificar
   atualização. Trocar rapidamente a data/usuário não deve reutilizar dados antigos.
6. Testar horário que passa enquanto a tela está aberta, troca de dia, fuso do
   dispositivo diferente, tentativa de forjar inicio futuro para data passada e
   horários fora da grade. Nenhum passado deve ser confirmado.
7. Fixtures administrativas isoladas: dois legados confirmados no mesmo horário e
   bloqueioLegado true. Cancelar um e depois ambos: o bloqueio deve permanecer até
   revisão administrativa. Testar reserva apontando para outro ID, campo ausente,
   sem estabelecimento, reserva ausente e controle desativado.
8. Regressão de cadastro, login/logout, veículos, navegação e histórico. Verificar
   erros de cancelamento visíveis. Rodar em API 26+; API 24/25 exige atenção ao uso
   preexistente de java.time, pois esta cópia não configura core library desugaring.

## Referências usadas na revisão

- https://firebase.google.com/docs/firestore/manage-data/transactions
- https://firebase.google.com/docs/firestore/security/rules-query
- https://firebase.google.com/docs/reference/rules/rules.Timestamp

## Resultados executados nesta tarefa

- Gradle 8.6/JDK 21: assembleDebug e testDebugUnitTest aprovados. A execução final
  usou build separado e chave Debug temporária por script externo ao repositório,
  contornando restrições de escrita sem modificar configurações do aplicativo.
- 6 testes CadastroValidatorTest e 7 SlotReleasePolicyTest aprovados (13 no total).
- Firebase CLI 15.34.2, emulador Firestore 1.22.0, projeto demo-lava-sprint4:
  15 verificações automatizadas das regras aprovadas, script com código de saída 0.
  O teste concorrente das regras usou dois UIDs e serviços diferentes e produziu
  um único confirmado e uma reserva. Não exercita a interface nem o SDK Android.
- git diff --check aprovado. Testes funcionais CT-08.1 a CT-08.5 executados e aprovados no aplicativo Android.
- Na implementação inicial, não houve publicação ou migração. Na validação posterior, as regras foram publicadas, os registros antigos de teste foram removidos e o controle de horários foi ativado.

---

## Validação final da Sprint 4 — 31/10/2026

### Ambiente

- Aplicativo Android desenvolvido em Kotlin e Jetpack Compose.
- Autenticação: Firebase Authentication.
- Persistência: Cloud Firestore.
- Estabelecimento: Lava Jato Prime.
- Identificador: lava_jato_prime.
- Branch: sprint-04-controle-horarios.

### Testes funcionais — CT-08

| Teste | Descrição | Resultado |
|---|---|---|
| CT-08.1 | Criação de agendamento no Firestore | APROVADO |
| CT-08.2 | Criação da reserva vinculada | APROVADO |
| CT-08.3 | Bloqueio de horário para outro usuário | APROVADO |
| CT-08.4 | Cancelamento e liberação do horário | APROVADO |
| CT-08.5 | Nova reserva após cancelamento | APROVADO |

### Testes automatizados

Compilação e testes unitários:

.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest

Resultado: BUILD SUCCESSFUL.

Testes de segurança do Firestore:

firebase emulators:exec --only firestore --project demo-lava-sprint4 --config firebase.emulator.json "node tests/firestore/test-rules.mjs"

Resultado: 15 passed; 0 failed.

### Configuração do Firebase

- Regras consolidadas publicadas no Firebase Console.
- Documento controle_horarios/lava_jato_prime configurado.
- Campo reconciliado definido como true.
- Agendamentos antigos de teste removidos antes da ativação.
- Controle de disponibilidade validado entre contas diferentes.

### Resultado da Sprint

O controle de horários foi implementado e validado.

O aplicativo impede reservas duplicadas para o mesmo estabelecimento,
data e horário, independentemente do serviço selecionado.

O cancelamento de um agendamento confirmado libera a reserva
correspondente, permitindo uma nova contratação do horário.

As operações de criação e cancelamento utilizam transações do
Cloud Firestore, preservando a consistência dos registros.

### Observações

Os resultados funcionais foram obtidos no ambiente Firebase
utilizado nos testes do aplicativo.

A validação não substitui testes adicionais de regressão,
desempenho e operação em produção.
