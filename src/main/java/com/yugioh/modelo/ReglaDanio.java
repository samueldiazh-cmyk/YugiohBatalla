package com.yugioh.modelo;


public interface ReglaDanio {

  
    ResultadoAtaque calcular(Monstruo atacante, Monstruo defensor);

    
    ResultadoAtaque calcular(Monstruo atacante);
}