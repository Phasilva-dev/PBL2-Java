package br.uefs.ecomp.bazar.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class LancesList implements LancesStategy, Serializable {

    @Serial
    private static final long serialVersionUID = 10L;

    private List<Lance> lances = new LinkedList<>();


    @Override
    public void adicionarLance(Usuario usuario, Lance lance) {
        lances.add(lance);
    }
    public boolean constainsUsuario(Usuario usuario){
        for (Lance lance : lances) {
            if (lance.getParticipante().equals(usuario)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void setTipoArmazenamento(String tipoArmazenamento) {
        List<Lance> novaLista;
        if ("LinkedList".equalsIgnoreCase(tipoArmazenamento)) {
            novaLista = new LinkedList<>(lances);
        } else if ("ArrayList".equalsIgnoreCase(tipoArmazenamento)) {
            novaLista = new ArrayList<>(lances);
        } else {
            throw new IllegalArgumentException("Tipo de armazenamento desconhecido: " + tipoArmazenamento);
        }
        lances = novaLista;
    }
    public void setLances(List<Lance> novoLances) {
        this.lances = novoLances;
    }

    //Retorna um Map com os valores de lance e de participantes do lance
    public Map<Usuario, Lance> getLancesMap() {
        Map<Usuario, Lance> lancesMap = new HashMap<>();
        for (Lance lance : lances) {
            lancesMap.put(lance.getParticipante(), lance);
        }
        return lancesMap;
    }

    @Override
    public List<Lance> getLancesList(){
        return this.lances;
    }

}
