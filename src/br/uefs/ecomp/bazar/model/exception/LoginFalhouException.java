package br.uefs.ecomp.bazar.model.exception;

public class LoginFalhouException extends Exception{

    public LoginFalhouException(String mensagem){
        super(mensagem);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
