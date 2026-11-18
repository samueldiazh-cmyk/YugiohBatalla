package com.yugioh.modelo;

import java.util.Objects;


public class CalculadoraDanio implements ReglaDanio {

    @Override
    public ResultadoAtaque calcular(Monstruo atacante, Monstruo defensor) {
        validarAtacante(atacante);
        Objects.requireNonNull(defensor, "El monstruo defensor es obligatorio.");
        if (atacante == defensor) {
            throw new IllegalArgumentException("Un monstruo no puede atacarse a sí mismo.");
        }
        return defensor.estaEnAtaque()
                ? ataqueContraAtaque(atacante, defensor)
                : ataqueContraDefensa(atacante, defensor);
    }

    @Override
    public ResultadoAtaque calcular(Monstruo atacante) {
        validarAtacante(atacante);
        int danio = atacante.getAtaque();
        return new ResultadoAtaque(atacante.getNombre(), null, danio, ResultadoAtaque.Afectado.DEFENSOR,
                false, false, atacante.getNombre() + " ataca directamente e inflige " + danio + " de daño.");
    }

    private ResultadoAtaque ataqueContraAtaque(Monstruo atacante, Monstruo defensor) {
        int atq = atacante.getAtaque();
        int atqDefensor = defensor.getAtaque();
        int diferencia = Math.abs(atq - atqDefensor);
        String a = atacante.getNombre();
        String d = defensor.getNombre();

        if (atq > atqDefensor) {
            return new ResultadoAtaque(a, d, diferencia, ResultadoAtaque.Afectado.DEFENSOR,
                    false, true, a + " destruye a " + d + ".");
        }
        if (atq < atqDefensor) {
            return new ResultadoAtaque(a, d, diferencia, ResultadoAtaque.Afectado.ATACANTE,
                    true, false, d + " resiste y destruye a " + a + ".");
        }
        if (atq == 0) {
            return new ResultadoAtaque(a, d, 0, ResultadoAtaque.Afectado.NINGUNO,
                    false, false, "Ambos tienen 0 ATQ: no ocurre nada.");
        }
        return new ResultadoAtaque(a, d, 0, ResultadoAtaque.Afectado.NINGUNO,
                true, true, "Empate de ATQ: " + a + " y " + d + " son destruidos.");
    }

    private ResultadoAtaque ataqueContraDefensa(Monstruo atacante, Monstruo defensor) {
        int atq = atacante.getAtaque();
        int def = defensor.getDefensa();
        String a = atacante.getNombre();
        String d = defensor.getNombre();

        if (atq > def) {
            return new ResultadoAtaque(a, d, 0, ResultadoAtaque.Afectado.NINGUNO,
                    false, true, a + " destruye a " + d + " en defensa (sin daño a LP).");
        }
        if (atq < def) {
            return new ResultadoAtaque(a, d, def - atq, ResultadoAtaque.Afectado.ATACANTE,
                    false, false, d + " bloquea el ataque; el atacante recibe daño de rebote.");
        }
        return new ResultadoAtaque(a, d, 0, ResultadoAtaque.Afectado.NINGUNO,
                false, false, "ATQ igual a DEF: " + d + " resiste sin daño.");
    }

    private static void validarAtacante(Monstruo atacante) {
        Objects.requireNonNull(atacante, "El monstruo atacante es obligatorio.");
        if (!atacante.estaEnAtaque()) {
            throw new AccionInvalidaException(
                    atacante.getNombre() + " está en posición de defensa y no puede atacar.");
        }
    }
}