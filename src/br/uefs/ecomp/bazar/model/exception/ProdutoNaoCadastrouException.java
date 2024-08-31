package br.uefs.ecomp.bazar.model.exception;

public class ProdutoNaoCadastrouException extends Exception{

    public ProdutoNaoCadastrouException (String mensagem){
        super(mensagem);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
