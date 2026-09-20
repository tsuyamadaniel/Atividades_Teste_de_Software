package br.edu.ifpr.boletim;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoletimTest {

    @Test
    void deveAprovarAlunoComMediaOito() {
        Boletim boletim = new Boletim();
        assertEquals("APROVADO", boletim.verificarSituacao(8));
    }

    @Test
    void deveAprovarAlunoNoLimiteSete() {
        Boletim boletim = new Boletim();
        assertEquals("APROVADO", boletim.verificarSituacao(7));
    }

    @Test
    void deveRecuperarNotaAlunoComMediaQuatro() {
        Boletim boletim = new Boletim();
        assertEquals("RECUPERACAO", boletim.verificarSituacao(4));
    }

    @Test
    void deveRecuperarNotaAlunoEntreQuatroESete() {
        Boletim boletim = new Boletim();
        assertEquals("RECUPERACAO", boletim.verificarSituacao(6.5));
    }

    @Test
    void deveReprovarAlunoComMediaDois() {
        Boletim boletim = new Boletim();
        assertEquals("REPROVADO", boletim.verificarSituacao(2));
    }

    @Test
    void deveReprovarAlunoAbaixoDeQuatro() {
        Boletim boletim = new Boletim();
        assertEquals("REPROVADO", boletim.verificarSituacao(3.99));
    }

    @Test
    void deveCalcularMediaInteira() {
        Boletim boletim = new Boletim();
        assertEquals(5.0, boletim.calcularMedia(5, 5), 0.0001);
    }

    @Test
    void deveCalcularMediaComParteDecimal() {
        Boletim boletim = new Boletim();
        assertEquals(7.5, boletim.calcularMedia(8, 7), 0.0001);
    }

    @Test
    void deveContarZeroAprovadosEmArrayVazio() {
        Boletim boletim = new Boletim();
        assertEquals(0, boletim.contarAprovados(new double[] {}));
    }

    @Test
    void deveContarUmAprovado() {
        Boletim boletim = new Boletim();
        assertEquals(1, boletim.contarAprovados(new double[] {8}));
    }

    @Test
    void deveContarApenasMediasMaioresOuIguaisASete() {
        Boletim boletim = new Boletim();
        assertEquals(2, boletim.contarAprovados(new double[] {8, 5, 7, 3}));
    }
}
