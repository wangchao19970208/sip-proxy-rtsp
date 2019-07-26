package com.shenzhen.teamway.model;

import java.io.Serializable;

public class Camera implements Serializable {
    private String deviceCode;

    private String rtspUrl;

    private String name;

    private String manufacturer;

    private String model;

    private String owner;

    private String civilCode;

    private String address;

    private Integer parental;

    private Integer registerWay;

    private String certNum;

    private Integer certifiable;

    private Integer errCode;

    private Integer secrecy;

    private String ipAddress;

    private Integer dPort;

    private String status;

    private static final long serialVersionUID = 1L;

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode == null ? null : deviceCode.trim();
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl == null ? null : rtspUrl.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer == null ? null : manufacturer.trim();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model == null ? null : model.trim();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner == null ? null : owner.trim();
    }

    public String getCivilCode() {
        return civilCode;
    }

    public void setCivilCode(String civilCode) {
        this.civilCode = civilCode == null ? null : civilCode.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Integer getParental() {
        return parental;
    }

    public void setParental(Integer parental) {
        this.parental = parental;
    }

    public Integer getRegisterWay() {
        return registerWay;
    }

    public void setRegisterWay(Integer registerWay) {
        this.registerWay = registerWay;
    }

    public String getCertNum() {
        return certNum;
    }

    public void setCertNum(String certNum) {
        this.certNum = certNum == null ? null : certNum.trim();
    }

    public Integer getCertifiable() {
        return certifiable;
    }

    public void setCertifiable(Integer certifiable) {
        this.certifiable = certifiable;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public Integer getSecrecy() {
        return secrecy;
    }

    public void setSecrecy(Integer secrecy) {
        this.secrecy = secrecy;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress == null ? null : ipAddress.trim();
    }

    public Integer getdPort() {
        return dPort;
    }

    public void setdPort(Integer dPort) {
        this.dPort = dPort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Camera other = (Camera) that;
        return (this.getDeviceCode() == null ? other.getDeviceCode() == null : this.getDeviceCode().equals(other.getDeviceCode()))
            && (this.getRtspUrl() == null ? other.getRtspUrl() == null : this.getRtspUrl().equals(other.getRtspUrl()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getManufacturer() == null ? other.getManufacturer() == null : this.getManufacturer().equals(other.getManufacturer()))
            && (this.getModel() == null ? other.getModel() == null : this.getModel().equals(other.getModel()))
            && (this.getOwner() == null ? other.getOwner() == null : this.getOwner().equals(other.getOwner()))
            && (this.getCivilCode() == null ? other.getCivilCode() == null : this.getCivilCode().equals(other.getCivilCode()))
            && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
            && (this.getParental() == null ? other.getParental() == null : this.getParental().equals(other.getParental()))
            && (this.getRegisterWay() == null ? other.getRegisterWay() == null : this.getRegisterWay().equals(other.getRegisterWay()))
            && (this.getCertNum() == null ? other.getCertNum() == null : this.getCertNum().equals(other.getCertNum()))
            && (this.getCertifiable() == null ? other.getCertifiable() == null : this.getCertifiable().equals(other.getCertifiable()))
            && (this.getErrCode() == null ? other.getErrCode() == null : this.getErrCode().equals(other.getErrCode()))
            && (this.getSecrecy() == null ? other.getSecrecy() == null : this.getSecrecy().equals(other.getSecrecy()))
            && (this.getIpAddress() == null ? other.getIpAddress() == null : this.getIpAddress().equals(other.getIpAddress()))
            && (this.getdPort() == null ? other.getdPort() == null : this.getdPort().equals(other.getdPort()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDeviceCode() == null) ? 0 : getDeviceCode().hashCode());
        result = prime * result + ((getRtspUrl() == null) ? 0 : getRtspUrl().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getManufacturer() == null) ? 0 : getManufacturer().hashCode());
        result = prime * result + ((getModel() == null) ? 0 : getModel().hashCode());
        result = prime * result + ((getOwner() == null) ? 0 : getOwner().hashCode());
        result = prime * result + ((getCivilCode() == null) ? 0 : getCivilCode().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getParental() == null) ? 0 : getParental().hashCode());
        result = prime * result + ((getRegisterWay() == null) ? 0 : getRegisterWay().hashCode());
        result = prime * result + ((getCertNum() == null) ? 0 : getCertNum().hashCode());
        result = prime * result + ((getCertifiable() == null) ? 0 : getCertifiable().hashCode());
        result = prime * result + ((getErrCode() == null) ? 0 : getErrCode().hashCode());
        result = prime * result + ((getSecrecy() == null) ? 0 : getSecrecy().hashCode());
        result = prime * result + ((getIpAddress() == null) ? 0 : getIpAddress().hashCode());
        result = prime * result + ((getdPort() == null) ? 0 : getdPort().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", deviceCode=").append(deviceCode);
        sb.append(", rtspUrl=").append(rtspUrl);
        sb.append(", name=").append(name);
        sb.append(", manufacturer=").append(manufacturer);
        sb.append(", model=").append(model);
        sb.append(", owner=").append(owner);
        sb.append(", civilCode=").append(civilCode);
        sb.append(", address=").append(address);
        sb.append(", parental=").append(parental);
        sb.append(", registerWay=").append(registerWay);
        sb.append(", certNum=").append(certNum);
        sb.append(", certifiable=").append(certifiable);
        sb.append(", errCode=").append(errCode);
        sb.append(", secrecy=").append(secrecy);
        sb.append(", ipAddress=").append(ipAddress);
        sb.append(", dPort=").append(dPort);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}