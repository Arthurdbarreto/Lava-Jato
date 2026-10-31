import assert from 'node:assert/strict';

const project = 'demo-lava-sprint4';
const base = `http://127.0.0.1:8088/v1/projects/${project}/databases/(default)/documents`;
const prefix = `projects/${project}/databases/(default)/documents/`;
const token = uid => [
  {alg: 'none', typ: 'JWT'},
  {aud: project, iss: `https://securetoken.google.com/${project}`, sub: uid, user_id: uid,
    iat: Math.floor(Date.now()/1000), exp: Math.floor(Date.now()/1000)+3600,
    firebase: {sign_in_provider: 'custom', identities: {}}}
].map(v => Buffer.from(JSON.stringify(v)).toString('base64url')).join('.') + '.';
const encode = value => typeof value === 'boolean' ? {booleanValue: value}
  : typeof value === 'number' ? {integerValue: String(value)}
  : value instanceof Date ? {timestampValue: value.toISOString()} : {stringValue: value};
const put = (path, data, mask) => ({update: {name: prefix+path,
  fields: Object.fromEntries(Object.entries(data).map(([key,value]) => [key, encode(value)]))},
  ...(mask ? {updateMask:{fieldPaths:mask}} : {})});
const del = path => ({delete:prefix+path});
async function commit(uid, writes) {
  const res = await fetch(base+':commit', {method:'POST',
    headers:{'Content-Type':'application/json', Authorization:'Bearer '+(uid === 'admin' ? 'owner' : token(uid))},
    body:JSON.stringify({writes})});
  return {status:res.status, body:await res.text()};
}
async function read(path, uid='admin') {
  const res = await fetch(base+'/'+path, {headers:{Authorization:'Bearer '+(uid==='admin' ? 'owner' : token(uid))}});
  return {status:res.status, data:await res.json()};
}
const date = '2030-01-20';
const slotId = time => `lava_jato_prime_${date}_${time.replace(':','-')}`;
function booking(uid, id, time='14:30') {
  const a = {usuarioId:uid, estabelecimentoId:'lava_jato_prime', horarioId:slotId(time),
    controleVersao:2, inicio:new Date(`${date}T${time}:00-03:00`), veiculoId:'car',
    serviceName:uid==='a'?'Lavagem':'Polimento', date, time, vehicleDescription:'Teste',
    vehiclePlate:'TESTE', address:'Teste', status:'CONFIRMADO'};
  const s = {estabelecimentoId:'lava_jato_prime', date, time, agendamentoId:id, bloqueioLegado:false};
  return {a,s,writes:[put(`agendamentos/${id}`,a),put(`horarios_reservados/${slotId(time)}`,s)]};
}
let passed=0;
function ok(result) { assert.equal(result.status,200,result.body); }
function denied(result) { assert.equal(result.status,403,result.body); }
async function test(name, run) { await run(); passed++; console.log(`PASS ${name}`); }

ok(await commit('admin',[put('usuarios/a/veiculos/car',{}),put('usuarios/b/veiculos/car',{})]));
await test('controle ausente bloqueia novas reservas',async()=>denied(await commit('a',booking('a','blocked').writes)));
ok(await commit('admin',[put('controle_horarios/lava_jato_prime',{reconciliado:true})]));
let winner,loser,winnerId;
await test('dois usuarios concorrentes: exatamente um confirmado e uma reserva', async()=>{
  const results=await Promise.all([commit('a',booking('a','race-a').writes),commit('b',booking('b','race-b').writes)]);
  assert.deepEqual(results.map(x=>x.status).sort(),[200,403],JSON.stringify(results));
  winner=results[0].status===200?'a':'b'; loser=winner==='a'?'b':'a'; winnerId=`race-${winner}`;
  assert.equal((await read(`agendamentos/race-${loser}`)).status,404);
  assert.equal((await read(`horarios_reservados/${slotId('14:30')}`)).data.fields.agendamentoId.stringValue,winnerId);
});
await test('outro usuario nao le agendamento do dono', async()=>assert.equal((await read(`agendamentos/${winnerId}`,loser)).status,403));
const cancel = id => put(`agendamentos/${id}`,{status:'CANCELADO'},['status']);
const release = () => del(`horarios_reservados/${slotId('14:30')}`);
await test('outro usuario nao cancela nem libera',async()=>denied(await commit(loser,[cancel(winnerId),release()])));
await test('cliente nao finaliza',async()=>denied(await commit(winner,[put(`agendamentos/${winnerId}`,{status:'FINALIZADO'},['status'])])));
await test('nao libera reserva isoladamente',async()=>denied(await commit(winner,[release()])));
await test('cancelamento normal exige liberacao atomica',async()=>denied(await commit(winner,[cancel(winnerId)])));
await test('agendamento isolado e negado',async()=>denied(await commit('a',[booking('a','isolated','15:00').writes[0]])));
await test('reserva isolada e negada',async()=>denied(await commit('a',[booking('a','isolated','15:00').writes[1]])));
await test('veiculo de outro usuario nao e aceito',async()=>{
  const item=booking('c','vehicle','16:00'); denied(await commit('c',item.writes));
});
await test('inicio forjado nao autoriza data passada',async()=>{
  const item=booking('a','past','16:00'); item.a.date='2020-01-20'; item.s.date=item.a.date;
  item.a.horarioId='lava_jato_prime_2020-01-20_16-00';
  denied(await commit('a',[put('agendamentos/past',item.a),put(`horarios_reservados/${item.a.horarioId}`,item.s)]));
});
await test('cancelamento do dono libera e permite nova reserva',async()=>{
  ok(await commit(winner,[cancel(winnerId),release()]));
  assert.equal((await read(`horarios_reservados/${slotId('14:30')}`)).status,404);
  ok(await commit(loser,booking(loser,'replacement').writes));
});
await test('cancelamento antigo nao pode apagar reserva nova',async()=>denied(await commit(winner,[cancel(winnerId),release()])));
await test('legados duplicados permanecem bloqueados apos cancelamentos',async()=>{
  const legacy={usuarioId:'a',date,time:'17:00',status:'CONFIRMADO'};
  ok(await commit('admin',[put('agendamentos/legacy-1',legacy),put('agendamentos/legacy-2',legacy),
    put(`horarios_reservados/${slotId('17:00')}`,{estabelecimentoId:'lava_jato_prime',date,time:'17:00',agendamentoId:'legacy-1',bloqueioLegado:true})]));
  ok(await commit('a',[cancel('legacy-1')]));
  assert.equal((await read('agendamentos/legacy-2')).data.fields.status.stringValue,'CONFIRMADO');
  denied(await commit('b',booking('b','legacy-conflict','17:00').writes));
  ok(await commit('a',[cancel('legacy-2')]));
  assert.equal((await read(`horarios_reservados/${slotId('17:00')}`)).status,200);
});
await test('cliente nao ativa controle administrativo',async()=>denied(await commit('a',[put('controle_horarios/lava_jato_prime',{reconciliado:true})])));
console.log(`RESULT ${passed} passed; 0 failed`);
