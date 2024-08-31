package br.uefs.ecomp.bazar.model.exception;

public class LeilaoNaoEncerradoException extends Exception{

    public LeilaoNaoEncerradoException(String mensagem){
        super(mensagem);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
