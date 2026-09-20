package br.edu.ifpr.boletim;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParticipacaoTest {

    @Test
    void deveDarDoisPontosQuandoEntregouAtividade() {
        Participacao participacao = new Participacao();
        assertEquals(2, participacao.calcularPontos(true, false));
    }

    @Test
    void deveDarUmPontoQuandoParticipouDaAula() {
        Participacao participacao = new Participacao();
        assertEquals(1, participacao.calcularPontos(false, true));
    }

    @Test
    void deveSomarTresPontosQuandoFezAsDuasAcoes() {
        Participacao participacao = new Participacao();
        assertEquals(3, participacao.calcularPontos(true, true));
    }

    @Test
    void deveDarZeroQuandoNaoFezNenhumaAcao() {
        Participacao participacao = new Participacao();
        assertEquals(0, participacao.calcularPontos(false, false));
    }
}
