package br.uefs.ecomp.bazar.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Venda implements Serializable {

    @Serial
    private static final long serialVersionUID = 9L;
    private final double valor;

    private final Usuario comprador;

    private final Usuario vendedor;

    private final Produto produto;

    private final Leilao leilao;

    private Date hora_da_venda;

    public Venda(double valor,Usuario comprador, Usuario vendedor, Produto produto, Leilao leilao){ //, Date hora_da_venda
        this.valor = valor;
        this.comprador = comprador;
        this.vendedor = vendedor;
        this.produto = produto;
        this.leilao = leilao;
        this.hora_da_venda = null;//hora_da_venda;
    }

    public double getValor(){
        return valor;
    }

    public Produto getProduto() {
        return produto;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public Leilao getLeilao() {
        return leilao;
    }
    public void setHora_da_venda(Date hora){
        this.hora_da_venda = hora;

    }

    public Date getHora_da_venda() {
        return hora_da_venda;
    }
}
