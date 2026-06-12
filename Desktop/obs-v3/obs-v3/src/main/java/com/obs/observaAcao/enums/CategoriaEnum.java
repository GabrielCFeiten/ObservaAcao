package com.obs.observaAcao.enums;

public enum CategoriaEnum {
    DENUNCIAR_ESGOTO_CEU_ABERTO(PrioridadeEnum.ALTA, true),
    DENUNCIAR_LIXO_ACUMULADO(PrioridadeEnum.MEDIA, true),
    DENUNCIAR_AGUA_PARADA(PrioridadeEnum.ALTA, true),
    DENUNCIAR_ESTABELECIMENTO_IRREGULAR(PrioridadeEnum.ALTA, true),
    SOLICITAR_EMISSAO_ALVARA_SANITARIO(PrioridadeEnum.BAIXA, false),
    SOLICITAR_RENOVACAO_ALVARA(PrioridadeEnum.MEDIA, false),
    SOLICITAR_VISTORIA_SANITARIA(PrioridadeEnum.MEDIA, false),
    DENUNCIAR_ALIMENTOS_ESTRAGADOS(PrioridadeEnum.ALTA, true),
    DENUNCIAR_FALTA_HIGIENE_ESTABELECIMENTO(PrioridadeEnum.ALTA, true),
    DENUNCIAR_PRAGAS_URBANAS(PrioridadeEnum.MEDIA, true),
    SOLICITAR_DESINFECCAO_LOCAL(PrioridadeEnum.MEDIA, false),
    SOLICITAR_COLETA_RESIDUOS_ESPECIAIS(PrioridadeEnum.BAIXA, false),
    DENUNCIAR_POLUICAO_AGUA(PrioridadeEnum.ALTA, true),
    DENUNCIAR_MA_ARMAZENAGEM_MEDICAMENTOS(PrioridadeEnum.ALTA, true);
    private final PrioridadeEnum prioridade;
    private final boolean permiteAnonimo;
    CategoriaEnum(PrioridadeEnum prioridade, boolean permiteAnonimo) {
        this.prioridade = prioridade;
        this.permiteAnonimo = permiteAnonimo;
    }
    public PrioridadeEnum getPrioridade() { return prioridade; }
    public boolean isPermiteAnonimo() { return permiteAnonimo; }
}
