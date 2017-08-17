package com.ghostcompany.hackfest.ghostcompany.models;

/**
 * Created by helicoptero on 11/06/2017.
 */
import java.io.Serializable;
import java.util.Date;


public class Empresa implements Serializable{

    private Long idEmpresa;

    private String title;

    private String endereco;

    private String lat;

    private String lng;

    private Date dataEmpresa;

  //  private User user;

    private String empresaCode;

    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
    /*
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
*/
    public Date getDataEmpresa() {
        return dataEmpresa;
    }

    public void setDataEmpresa(Date dataEmpresa) {
        this.dataEmpresa = dataEmpresa;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setEmpresaCode(String occurenceCode) {
        this.empresaCode = occurenceCode;
    }

    public String getEmpresaCode() {
        return empresaCode;
    }


}
