package br.uefs.ecomp.bazar.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class LancesMap implements LancesStategy, Serializable {

    @Serial
    private static final long serialVersionUID = 11L;

    private Map<Usuario, Lance> lances = new HashMap<>();


    @Override
    public void adicionarLance(Usuario usuario, Lance lance) {
        lances.put(usuario, lance);
    }
    public boolean constainsUsuario(Usuario usuario){
        return this.lances.containsKey(usuario);
    }

    @Override
    public void setTipoArmazenamento(String tipoArmazenamento) {
        Map<Usuario, Lance> novoMapa;
        if ("LinkedHashMap".equalsIgnoreCase(tipoArmazenamento)) {
            novoMapa = new LinkedHashMap<>(lances);
        } else if ("HashMap".equalsIgnoreCase(tipoArmazenamento)) {
            novoMapa = new HashMap<>(lances);
        } else {
            throw new IllegalArgumentException("Tipo de armazenamento desconhecido: " + tipoArmazenamento);
        }
        lances = novoMapa;
    }
    @Override
    public void setLances(Map<Usuario, Lance> novoLances) {
        this.lances = novoLances;
    }
    public Map<Usuario, Lance> getLancesMap() {
        return lances;
    }

    @Override
    public List<Lance> getLancesList() {
        return new ArrayList<>(lances.values());
    }
}
