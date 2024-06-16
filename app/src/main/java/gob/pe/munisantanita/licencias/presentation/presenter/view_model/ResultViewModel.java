package gob.pe.munisantanita.licencias.presentation.presenter.view_model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ResultViewModel  implements Serializable {

    private String licNum;
    private String licAnio;
    private String ruc;
    private String tipo;
    private String nomSolicitante;
    private String dirPredio;
    private String nomContri;
    private String areaM2;
    private String giro;
    private String licEstado;
    private String fechaVencimiento;
    private String fechaEmision;
    private boolean estadoConsulta;
    private String msgEstadoConsulta;


    public String getLicNum() {
        return licNum;
    }
    public void setLicNum(String licNum) {
        this.licNum = licNum;
    }

    public String getLicAnio() {
        return licAnio;
    }
    public void setLicAnio(String licAnio) {
        this.licAnio = licAnio;
    }

    public String getRuc() {
        return ruc;
    }
    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNomSolicitante() {
        return nomSolicitante;
    }
    public void setNomSolicitante(String nomSolicitante) {
        this.nomSolicitante = nomSolicitante;
    }

    public String getDirPredio() {
        return dirPredio;
    }
    public void setDirPredio(String dirPredio) {
        this.dirPredio = dirPredio;
    }

    public String getNomContri() {
        return nomContri;
    }
    public void setNomContri(String nomContri) {
        this.nomContri = nomContri;
    }

    public String getAreaM2() {
        return areaM2;
    }
    public void setAreaM2(String areaM2) {
        this.areaM2 = areaM2;
    }

    public String getGiro() {
        return giro;
    }
    public void setGiro(String giro) {
        this.giro = giro;
    }

    public String getLicEstado() {
        return licEstado;
    }
    public void setLicEstado(String licEstado) {
        this.licEstado = licEstado;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public String getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(String fechaEmision) { this.fechaEmision = fechaEmision; }

    public boolean isEstadoConsulta() { return estadoConsulta; }
    public void setEstadoConsulta(boolean estadoConsulta) { this.estadoConsulta = estadoConsulta; }

    public String getMsgEstadoConsulta() { return msgEstadoConsulta; }
    public void setMsgEstadoConsulta(String msgEstadoConsulta) { this.msgEstadoConsulta = msgEstadoConsulta; }
}
