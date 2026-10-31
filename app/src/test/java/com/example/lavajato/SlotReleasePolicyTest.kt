package com.example.lavajato

import com.example.lavajato.data.model.SlotReleasePolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SlotReleasePolicyTest {
    private val appointment = mapOf<String, Any>(
        "controleVersao" to 2L, "estabelecimentoId" to "lava_jato_prime",
        "date" to "2026-12-20", "time" to "14:30",
        "horarioId" to "lava_jato_prime_2026-12-20_14-30"
    )
    private val slot = mapOf<String, Any>(
        "estabelecimentoId" to "lava_jato_prime", "date" to "2026-12-20",
        "time" to "14:30", "agendamentoId" to "a", "bloqueioLegado" to false
    )

    @Test fun liberaSomenteReservaCorrespondenteReconciliada() {
        assertTrue(SlotReleasePolicy.canRelease("a", appointment, slot, true))
    }
    @Test fun conservaReservaSemReconciliacao() {
        assertFalse(SlotReleasePolicy.canRelease("a", appointment, slot, false))
    }
    @Test fun conservaReservaComOutrosConfirmadosLegados() {
        assertFalse(SlotReleasePolicy.canRelease("a", appointment, slot + ("bloqueioLegado" to true), true))
    }
    @Test fun conservaReservaDeOutroAgendamento() {
        assertFalse(SlotReleasePolicy.canRelease("b", appointment, slot, true))
    }
    @Test fun conservaReservaDeRegistroAntigo() {
        assertFalse(SlotReleasePolicy.canRelease("a", appointment - "controleVersao", slot, true))
        assertFalse(SlotReleasePolicy.canRelease("a", appointment - "estabelecimentoId", slot, true))
    }
    @Test fun conservaReservaAusenteOuIncompleta() {
        assertFalse(SlotReleasePolicy.canRelease("a", appointment, emptyMap(), true))
        assertFalse(SlotReleasePolicy.canRelease("a", appointment, slot - "bloqueioLegado", true))
    }
    @Test fun conservaReservaComDataHorarioOuEstabelecimentoDivergente() {
        for ((field, value) in listOf("date" to "2026-12-21", "time" to "15:00", "estabelecimentoId" to "outro")) {
            assertFalse(SlotReleasePolicy.canRelease("a", appointment, slot + (field to value), true))
        }
        assertFalse(SlotReleasePolicy.canRelease("a", appointment + ("horarioId" to "outro"), slot, true))
    }
}
