package br.uefs.ecomp.bazar.model;

import java.io.Serializable;
import java.util.*;

public interface LancesStategy {
    void adicionarLance(Usuario usuario, Lance lance);
    List<Lance> getLancesList();
    Map<Usuario,Lance> getLancesMap();
    default void setLances(List<Lance> lances) {
        throw new UnsupportedOperationException("Método setLances(List<Lance>) não implementado");
    }
    default void setLances(Map<Usuario, Lance> lances) {
        throw new UnsupportedOperationException("Método setLances(Map<Usuario, Lance>) não implementado");
    }
    //Set<Map.Entry<Usuario, Lance>> entrySet();
    void setTipoArmazenamento(String tipoArmazenamento);
    boolean constainsUsuario(Usuario key);
}
