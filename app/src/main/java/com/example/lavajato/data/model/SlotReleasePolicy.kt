package com.example.lavajato.data.model

// Dados desconhecidos e reservas reconciliadas com pendências permanecem bloqueados.
internal object SlotReleasePolicy {
    fun canRelease(
        appointmentId: String,
        appointment: Map<String, Any>,
        slot: Map<String, Any>,
        reconciled: Boolean
    ): Boolean {
        val establishment = "lava_jato_prime"
        val date = appointment["date"] as? String ?: return false
        val time = appointment["time"] as? String ?: return false
        val expectedId = "${establishment}_${date}_${time.replace(':', '-')}"
        return reconciled && appointment["controleVersao"] == 2L &&
            appointment["estabelecimentoId"] == establishment &&
            appointment["horarioId"] == expectedId &&
            slot["agendamentoId"] == appointmentId && slot["bloqueioLegado"] == false &&
            slot["estabelecimentoId"] == establishment && slot["date"] == date && slot["time"] == time
    }
}
